Quetzal (*Que*ry Tran*z*l*a*tion *L*ibraries)
=======

SPARQL to SQL translation engine for multiple backends, such as DB2, PostgreSQL and Apache Spark.   

# Philosophy
The goal of Quetzal is to provide researchers with a framework to experiment with various techniques to store and query graph data efficiently.  To this end, we provide 3 modular components that:
* Store data:  In the current implementation, data is stored in using a schema similar to the one described in [SIGMOD 2013 paper](http://dl.acm.org/citation.cfm?id=2463718).  The schema lays out all outgoing (or incoming) labeled edges of a given vertex *based on the analysis of data characteristics* to optimize storage for a given dataset.  The goal in the layout is to store the data for a given vertex on a single row in table to optimize for STAR queries which are very common in SPARQL.  
* Compile SPARQL to SQL:  In the current implementation, given a set of statistics about the dataset's characteristics, the compiler can compile SPARQL 1.1 queries into SQL.  The compiler will optimize the order in which it executes the SPARQL query based on statistics of the dataset.
* Support for SQL on multiple backends:  In the current implementation, we support DB2, PostgreSQL, and Apache Spark.  The first two are useful for workloads that require characteristics normally supported by relational backends (e.g., transactional support), the third targets analytic workloads that might mix graph analytic workloads with declarative query workloads. 

# Overview of Components
* Data Layout:  The current implementation uses a *row based layout of graph data*, such that each vertex's incoming edges or outgoing edges are laid out as much as possible on the same row.  For a detailed set of experiments that examine when this layout is advantageous, see [SIGMOD 2013 paper](http://dl.acm.org/citation.cfm?id=2463718).  Outgoing edges are stored in a table called DPH (direct primary hashtable), and incoming edges are stored in a table called RPH (reverse primary hashtable).  Because RDF can have many thousand properties, dedicating a column per property is not an option (in fact, some datasets will exhaust most database systems limits on the number of columns).  RDF data is sparse though, so each vertex tends to have a small subset of the total number of properties.  The current implementation performs an analysis of which properties co-occur with which others, and uses graph coloring to build a hash function that maps properties to columns.  Properties that co-occur together are typically not assigned to the same row.  If they do get assigned to the same row because a single vertex has several hundred edges to all sorts of properties, then collisions are possible and the schema records this fact, and the SQL is adjusted appropriately.  Note that for multi-valued properties, DPH and RPH record only the existence of the property for a given vertex, actual values require a join with a DS (direct secondary) and RS (reverse secondary) table, respectively.
* SPARQL-SQL compiler:  In the current implementation, this compilation job is done by a class called ``com.ibm.research.rdf.store.sparql11.planner.Planner``, in a method called ``public Plan plan(Query q, Store store, SPARQLOptimizerStatistics stats)``.  The goal of the planner is to compile the SPARQL query into SQL, *re-ordering* the query in order to start with the most selective triples (triples with the least cost), joining it with the second most selective triple based on what becomes available when one evaluates the first triple, and so on.  In doing so, the planner must respect the semantics of SPARQL (e.g., not join two variables that are named the same but are on two separate brances of a UNION).  The Planner employs a greedy algorithm to evaluate what available nodes exist for planning, and which one should be planned first.  AND nodes get collapsed into a single "region" of QueryTriple nodes because any triples within an AND node can be targeted first.  Each triple node within an AND can evaluate its cost based on what variables are available, and each node has a notion of what variables it can produce bindings to based on the access method used (e.g., if the access method is DPH, it typically would produce an object variable binding; conversely if the access method is RPH, it would typically produce a subject variable binding).  The cost of producing these bindings is estimated based on the average number of outgoing (DPH) or incoming (RPH) edges in most cases, unless the triple happens to have a popular node which appears in a *top K* set.  Other complex nodes such as EXISTs, UNION or OPTIONAL nodes evaluate their costs recursively by planning for their subtrees. (See https://github.com/Quetzal-RDF/quetzal/tree/master/doc/QuetzalPlanner.pdf)  The planner then chooses the cheapest node to schedule first.  Once it has chosen a node, the set of available variables has changed, so a new of cost computations are performed to find the next step.  The planner proceeds in this manner till there are no more available nodes to plan.  The output of the planner is ``com.ibm.research.rdf.store.sparql11.planner.Plan``, which is basically a binary plan tree that is composed of AND plan nodes, LEFT JOIN nodes, etc.  This serves as the input for the next step.
* SQL generator:  In the current implementation, the plan serves as input to a number of SQL templates, which get created for every type of node in the plan tree.  The ``com.ibm.research.rdf.store.sparql11.sqltemplate`` package contains the templates, which generate SQL modularly per node in the plan tree using common table expressions (CTEs).  The template code is general purpose and keeps track of things such as the specific CTE to node mappings, what external variables need to be projected, which variables should be joined together etc.  The actual job of generating SQL for different backends is accomplished using specialized String Templates from the [String Template](http://www.stringtemplate.org) library.  Example files are ``com.ibm.research.rdf.store.sparql11.sqltemplate.common.stg`` which has the templates that are common to all backends.

For more information on how to get started, click on the Wiki to this repository

# Install and build issues
If you are building from source, get the following:
``git clone https://github.com/themadcreator/rabinfingerprint`` and build using maven.
* Also install the latest JDBC driver from: https://cloud.google.com/bigquery/partners/simba-drivers/#current_jdbc_driver_releases_1151005 and drop it into lib to compile.
Then clone this repository and build using maven.

# Storage of graph data on cloud SQL backing stores such as Spanner and BigQuery
Since the time we worked on Quetzal, a number of cloud databases have emerged that support the complex SQL queries needed to access graph data. One question that we started to ask recently is whether storage of graph data is better suited for a column oriented, nested type data layout such as BigQuery, or whether a row store such as Spanner is better suited for storage of graph data.  There are tradeoffs to each, and this is by no means an exhaustive comparison of the two different approaches, but we performed some very initial experiments on the following layout on BigQuery versus Spanner for a simple graph query which is not just a 1 hop neighborhood of a node, and we note the rather interesting results here.  
* The data and the query:  The graph data are generated from the Lehigh University Benchmark (LUBM) [LUBM](http://swat.cse.lehigh.edu/projects/lubm/) which has a set of students taking courses at a university, and they have advisors.  The data is sparse, and many entities have 1->many edges.  The query is query 9 from that benchmark, which is to find students taking courses taught by their advisors.  Students in that graph take many courses, and have a single advisor.  Each advisor teaches many courses.  And the query asks to find the 'triangle' between them, which is to specify which students take a class that is taught by their advisor.  The graph has 1 billion triples in it, which translates to ~174M rows in an entity oriented store, assuming that 1->many edges such as taking a course, or teaching a course are represented in a single row using arrays or nested data structures.  The dataset is about 79G when written as a JSON file.
* The layout:  Both Spanner and BigQuery provide support for nested data.  Following the entity oriented view of data in Quetzal, the data model is that of a 'subject' or entity, with various edge types mapped to distinct columns. Because BigQuery is ideal for storing columnar, sparse data, we used a 1-1 mapping of each edge type to columns. Furthermore, we did not actually need a reverse mapping since BigQuery has no indexes (every query is a scan).  Instead, it exploits the fact that only specific columns will ever be invoked in a given query. We maintained the same schema for Spanner just to ensure we had an apples to apples comparison.  The layout is therefore like just the DPH table in the [SIGMOD 2013 paper](http://dl.acm.org/citation.cfm?id=2463718), with the one change that we did not separate out the many valued edges into a separate table.  We used Spanner and BigQuery's support for array types to store multi valued predicates in the same column.  Note that Spanner also supports interleaving rows between the two tables which we could have used to support multi valued predicates but we did not do so in this first experiment.  All the code is checked into the spanner-loader and bigquery-loader directories.

* Here are the mappings of edge types to column names in LUBM:

`<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#undergraduateDegreeFrom>=col_8
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#emailAddress>=col_6
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#mastersDegreeFrom>=col_5
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#memberOf>=col_12
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#worksFor>=col_3
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#teachingAssistantOf>=col_15
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#subOrganizationOf>=col_16
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#researchInterest>=col_9
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#teacherOf>=col_7
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#name>=col_2
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#headOf>=col_11
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#telephone>=col_4
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#publicationAuthor>=col_0
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#takesCourse>=col_14
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#doctoralDegreeFrom>=col_10
<http\://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl\#advisor>=col_13
<http\://www.w3.org/1999/02/22-rdf-syntax-ns\#type>=col_1`
* Here is the schema for all the edges in BigQuery for LUBM:

`{"schema":{"fields":[{"name":"subject","type":"string"},{"name":"col_8","type":"string"},{"name":"col_6","type":"string"},{"name":"col_5","type":"string"},{"name":"col_12","type":"string"},{"name":"col_3","type":"string"},{"name":"col_15","type":"string"},{"name":"col_9","type":"string"},{"name":"col_16","type":"string"},{"name":"col_7","type":"string","mode":"repeated"},{"name":"col_2","type":"string"},{"name":"col_11","type":"string"},{"name":"col_4","type":"string"},{"name":"col_0","type":"string","mode":"repeated"},{"name":"col_14","type":"string","mode":"repeated"},{"name":"col_10","type":"string"},{"name":"col_13","type":"string"},{"name":"col_1","type":"string","mode":"repeated"}]}}`

* Here is the corresponding schema for Spanner, written as Java code:

` static void createDatabase(DatabaseAdminClient dbAdminClient, DatabaseId id) {
    Operation<Database, CreateDatabaseMetadata> op = dbAdminClient.createDatabase(
        id.getInstanceId().getInstance(), id.getDatabase(),
        Arrays.asList("CREATE TABLE DPH (\n" + "  subject STRING(MAX) NOT NULL,\n"
            + "  col_0  ARRAY<STRING(MAX)>, \n" + "  col_1  ARRAY<STRING(MAX)>, \n"
            + "  col_2  STRING(MAX), \n" + "  col_3  STRING(MAX), \n" + "  col_4  STRING(MAX), \n"
            + "  col_5  STRING(MAX), \n" + "  col_6  STRING(MAX), \n"
            + "  col_7  ARRAY<STRING(MAX)>, \n" + "  col_8  STRING(MAX), \n"
            + "  col_9  STRING(MAX), \n" + "  col_10  STRING(MAX), \n" + "  col_11  STRING(MAX), \n"
            + "  col_12  STRING(MAX), \n" + "  col_13  STRING(MAX), \n"
            + "  col_14  ARRAY<STRING(MAX)>, \n" + "  col_15  STRING(MAX), \n"
            + "  col_16  STRING(MAX)) \n" + " PRIMARY KEY (subject)"));
    Database db = op.waitFor().getResult();
    System.out.println("Created database [" + db.getId() + "]");
  }`
 * And now for the queries.  BigQuery supports common table expressions which were crucial in providing a nice abstraction to construct complex graph queries.  Here is the query for BigQuery:

`with 
     t1 as (select subject as student, col_13 as advisor from lubm.DPH where col_13 is not null),
     t2 as (select subject as student, col_14 as course from lubm.DPH where col_14 is not null),
     t3 as (select subject as teacher, col_7 as course from lubm.DPH where col_7 is not null),
     t4 as (select teacher, course from t3 t, t.course course),
     t5 as (select student, course from t2 t, t.course course)
     select t5.student, t4.teacher, t4.course from t4, t5, t1 where t4.course = t5.course and t4.teacher = t1.advisor and t5.student = t1.student`
* Here is the corresponding query for Spanner because it has no support for Common Table Expressions (CTEs):

`select dph1.subject as student, dph1.col_13 as advisor, course, dph2.subject as teacher, c from DPH as dph1, DPH as dph2 cross join unnest(dph1.col_14) as course cross join unnest(dph2.col_7) as c where dph1.col_13 is not null and dph1.col_14 is not null and dph2.col_7 is not null and course = c and dph1.col_13 = dph2.subject`
* And the results.  BigQuery performed this query in 67.6s, and processed about 26.1 GB.  Spanner timed out after 15 minutes.  It is possible that Spanner does not handle un-nesting of arrays as well as BigQuery, but this is an interesting datapoint, and suggests that Spanner may need a different style schema for storing 1->many edges.  The performance of BigQuery is rather impressive, for comparison with some of the databases Quetzal supports on a 100M edge dataset [see here](https://github.com/Quetzal-RDF/quetzal/wiki/Benchmarks). 
* Of course, this is a hand crafted experiment for now - but it seems to suggest that BigQuery has at least one key advantage over Spanner for querying graph data, which is its support for CTEs.

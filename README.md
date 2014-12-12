Quetzal (*Que*ry Tran*z*l*a*tion *L*ibraries)
=======

SPARQL to SQL translation engine for multiple backends, such as DB2, PostgreSQL and Apache Spark.   

# Philosophy
The goal of Quetzal is to provide researchers with a framework to experiment with various techniques to store and query graph data efficiently.  To this end, we provide 3 modular components that:
* Store data:  In the current implementation, data is stored in using a schema similar to the one described in [SIGMOD 2013 paper](http://dl.acm.org/citation.cfm?id=2463718).  The schema lays out all outgoing (or incoming) labeled edges of a given vertex *based on the analysis of data characteristics* to optimize storage for a given dataset.  The goal in the layout is to store the data for a given vertex on a single row in table to optimize for STAR queries which are very common in SPARQL.  
* Compile SPARQL to SQL:  In the current implementation, given a set of statistics about the dataset's characteristics, the compiler can compile SPARQL 1.1 queries into SQL.  The compiler will optimize the order in which it executes the SPARQL query based on statistics of the dataset.
* Execute SQL on multiple backends:  In the current implementation, we support DB2, PostgreSQL, and Apache Spark.  The first two are useful for workloads that require characteristics normally supported by relational backends (e.g., transactional support), the third targets analytic workloads that might mix graph analytic workloads with declarative query workloads. 

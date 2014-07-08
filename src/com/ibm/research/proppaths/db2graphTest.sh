
# $1 resultFile
# $2 number of rounds
# $3 number of evaluations per query per round
# $4 globalWarmup (0|1)
# $5  localWarmup (0|1) 
# strategyIndex:  0 = Original; 1 = Delta with Runstats; 2 = Original with Runstats; 3 = Delta
#


for ((  i = 1 ;  i <= $2 ;  i++  ))
do
	# Usage: resultFile strategy? externalSeed? #Round? #RecordedRunPerRound? globalWarmup (0|1)? localWarmup (0|1)?"
	java -ea -jar db2graphTest.jar  $1 1 $i 1 $3 $4 $5
done

for ((  i = 1 ;  i <= $2 ;  i++  ))
do
	java -ea -jar db2graphTest.jar  $1 0 $i 1 $3 $4 $5
done
for ((  i = 1 ;  i <= $2 ;  i++  ))
do
	java -ea -jar db2graphTest.jar  $1 2 $i 1 $3 $4 $5
done

for ((  i = 1 ;  i <= $2 ;  i++  ))
do
	java -ea -jar db2graphTest.jar  $1 3 $i 1 $3 $4 $5
done

# for ((  i = 1 ;  i <= $2 ;  i++  ))
# do
#	java -ea -jar db2graphTest.jar  $1 4 $i 1 $3 $4 $5 
#	java -ea -jar db2graphTest.jar  $1 5 $i 1 $3 $4 $5
#	java -ea -jar db2graphTest.jar  $1 6 $i 1 $3 $4 $5
#	java -ea -jar db2graphTest.jar  $1 7 $i 1 $3 $4 $5
#	java -ea -jar db2graphTest.jar  $1 8 $i 1 $3 $4 $5 
#	java -ea -jar db2graphTest.jar  $1 9 $i 1 $3 $4 $5
#	java -ea -jar db2graphTest.jar  $1 10 $i 1 $3 $4 $5
#	java -ea -jar db2graphTest.jar  $1 11 $i 1 $3 $4 $5
#done 
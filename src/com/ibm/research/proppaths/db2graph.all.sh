# $1 resultFilePrefix


# No global warm up, 5 rounds, 4 evaluations per query per round, local warm up
./db2graphTest.sh $1.NoGlobalWarmup.5Rounds.4EvalPerRound.localWarmup.csv  5 4 0 1

# One global warm up, 20 rounds, 1 evaluation per query per round, no local warm up
#./db2graphTest.sh $1.globalWarmup.20Rounds.1EvalPerRound.nolocalWarmup.csv  20 1 1 0


# No global warm up, 2 rounds, 5 evaluation per query per round, local warm up
#./db2graphTest.sh $1.NoGlobalWarmup.2Rounds.5EvalPerRound.localWarmup.csv  2 5 0 1

# One global warm up, 10 rounds, 1 evaluation per query per round, no local warm up
#./db2graphTest.sh $1.globalWarmup.10Rounds.1EvalPerRound.nolocalWarmup.csv  10 1 1 0


# One global warm up, 55 rounds, 1 evaluation per query per round, no local warm up
#./db2graphTest.sh $1.globalWarmup.55Rounds.1EvalPerRound.nolocalWarmup.csv  55 1 1 0

# No global warm up, 11 rounds, 5 evaluations per query per round, local warm up
#./db2graphTest.sh $1.NoGlobalWarmup.11Rounds.5EvalPerRound.localWarmup.csv  11 5 0 1
import os
import sys

def runProg(topology, includeSelf, influence, swarmSize, iterations, function, dimensions, log) :
	# create directory
	cmd = "java PSOTester " + topology + " " + includeSelf + " " + influence + " " + swarmSize + " " + iterations + " " + function + " " + dimensions
	os.system(cmd + " >> " + log);
	# put log file into directory

top = ["global" , "ring", "random", "von"]
inc = ["yes" , "no"]
inf = ["pg" , "fips"]

for topType in top :
	for incType in inc :
		for infType in inf :
			runProg(topType, incType, infType, "9", "2000", "sphere", "30", "z-sphere-9-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "36", "2000", "sphere", "30", "z-sphere-36-"+topType+"-"+incType+"-"+infType.log")
			# runProg(topType, incType, infType, "49", "2000", "sphere", "30", "z-sphere-49-"+topType+"-"+incType+"-"+infType.log")
			# runProg(topType, incType, infType, "100", "2000", "sphere", "30", "z-sphere-100-"+topType+"-"+incType+"-"+infType.log")
			# runProg(topType, incType, infType, "9", "2000", "rosenbrock", "30", "z-rosenbrock-9-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "36", "2000", "rosenbrock", "30", "z-rosenbrock-36-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "49", "2000", "rosenbrock", "30", "z-rosenbrock-49-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "100", "2000", "rosenbrock", "30", "z-rosenbrock-100-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "9", "2000", "griewank", "10", "z-griewank-9-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "36", "2000", "griewank", "10", "z-griewank-36-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "49", "2000", "griewank", "10", "z-griewank-49-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "100", "2000", "griewank", "10", "z-griewank-100-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "9", "2000", "ackley", "30", "z-ackley-9-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "36", "2000", "ackley", "30", "z-ackley-36-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "49", "2000", "ackley", "30", "z-ackley-49-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "100", "2000", "ackley", "30", "z-ackley-100-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "9", "2000", "rastrigin", "30", "z-rastrigin-9"-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "36", "2000", "rastrigin", "30", "z-rastrigin-36-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType," 49", "2000", "rastrigin", "30", "z-rastrigin-49-"+topType+"-"+incType+"-"+infType+".log")
			# runProg(topType, incType, infType, "100", "2000", "rastrigin", "30", "z-rastrigin-100-"+topType+"-"+incType+"-"+infType+".log")
import Influence.*;
import PSO.Swarm;
import Topology.*;
import java.lang.*;

public class PSOTester
{
	public static void main(String[] args)
	{
		NeighborhoodTopology topology;
		boolean includeSelf;
		NeighborhoodInfluence influence;
		int swarmSize;
		int iterations;
		Swarm.Function function;
		int dimensions;
		int output;

		if (args.length != 7)
		{
			System.out.println("Invalid number of command line arguments! Need 7:");
			System.out
					.println("topology include-self influence swarm-size iterations function dimensions");

			System.out.printf("%12s options:\t%s\n", "Topology", "global ring random von");
			System.out.printf("%12s options:\t%s\n", "Include Self", "yes no");
			System.out.printf("%12s options:\t%s\n", "Influence", "pg fips");
			System.out.printf("%12s options:\t%s\n", "Swarm size",
					"[positive perfect square integer]");
			System.out.printf("%12s options:\t%s\n", "Iterations", "[positive integer value]");
			System.out.printf("%12s options:\t%s\n", "Function",
					"sphere rosenbrock griewank ackley rastrigin");
			System.out.printf("%12s options:\t%s\n", "Dimensions", "[positive integer value]");
			System.out.println();

			System.exit(0);
		}

		// let's assume that the user inputs everything correctly
		includeSelf = args[1].equals("yes") ? true : false;
		swarmSize = Integer.parseInt(args[3]);
		iterations = Integer.parseInt(args[4]);
		dimensions = Integer.parseInt(args[6]);

		String topType = args[0];
		String infType = args[2];
		String funcType = args[5];

		topology = 	topType.equals("global") 	? new Global() : 
					topType.equals("ring") 		? new Ring() : 
					topType.equals("random") 	? new Random() : 
					topType.equals("von") 		? new VonNeumann()
											: null;
		if (topology == null)
		{			
			System.out.println("Invalid topology type!");
			System.exit(1);
		}

		influence = 	infType.equals("pg") 	? new PersonalGlobal() : 
					infType.equals("fips") 	? new FullyInformed() 
										: null;

		if (influence == null)
		{			
			System.out.println("Invalid neighborhood influence type!");
			System.exit(2);
		}

		function = 	funcType.equals("sphere") 		? Swarm.Function.SPHERE : 
					funcType.equals("rosenbrock") 	? Swarm.Function.ROSENBROCK :
					funcType.equals("griewank") 		? Swarm.Function.GRIEWANK :
					funcType.equals("ackley") 		? Swarm.Function.ACKLEY :
					funcType.equals("rastrigin") 		? Swarm.Function.RASTRIGIN
												: null;
		if (function == null)
		{			
			System.out.println("Invalid function type!");
			System.exit(3);
		}
		
		// hardcode number of runs
		int runs = 50;
		Swarm[] pso = new Swarm[runs];
			
		for (int i = 0; i < runs; i++)
		{
			pso[i] = new Swarm(topology, includeSelf, influence, swarmSize, iterations, function, dimensions);
			pso[i].run();
		}

		/* CODE FOR TESTING OUTPUT */
		
		System.out.println(topType + "-" + infType + "-"+ args[1]);

		// output average globalBest at each iteration over all runs
		double[] averageGlobalBestsPerIteration = new double[iterations];
		for (int i = 0; i < iterations; i++)
		{
			for (int j = 0; j < runs; j++) 
			{
				averageGlobalBestsPerIteration[i] += pso[j].getStoredGlobalBests()[i];
			}

		averageGlobalBestsPerIteration[i] = averageGlobalBestsPerIteration[i] / runs;
		if (i % 100 == 0)
			System.out.println(averageGlobalBestsPerIteration[i]);
		} 
		
		/*
		System.out.println(funcType+"-" +swarmSize+"-"+topType + "-" + infType + "-"+ args[1]);
		// output average and standard deviation of the final globalBests over all runs
		double average = 0;
		double variance = 0;
		double stddev = 0;
		for (int i = 0; i < runs; i++)
		{
			average += pso[i].getGlobalBest();
		}
		average = average / runs;
		System.out.println("average:\t" + average);

		for (int i = 0; i < runs; i++)
		{
			variance += Math.pow(pso[i].getGlobalBest() - average, 2);
		}
		variance = variance / runs;
		stddev = Math.pow(variance, .5);
		System.out.println("stddev:\t" + stddev);
		System.out.println();
		*/
		
	}
}
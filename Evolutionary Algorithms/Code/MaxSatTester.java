import java.text.DecimalFormat;

/*
 * Richardo Hopkins, Amy Sham, and Luis Rosias
 * 
 * The interface to run the algorithms. Use this with the correct terminal parameters
 * in order to run our code.
 * 
 * Examples :
 * =====================
 * - Genetic Algorithm -
 * =====================
 * 
 * $ java MaxSatTester swag.cnf 100 bs 1c 0.7 0.01 1000 g
 * 
 * =========
 * - PBIL -
 * =========
 * 
 * $ java MaxSatTester yolo.cnf 150 0.1 0.075 0.02 0.05 1000 p
 * 
 */

public class MaxSatTester
{

	private static double startTime, endTime;
	private static GeneticAlg g;
	private static PBIL p;
	private static char algorithmType;
	private static String fileName;

	public static void main(String[] args)
	{
		if (args.length != 8) {
			System.out.println("Error - Invalid number of arguments.");
			System.out.print  ("Usage: java GeneticAlg [fileName] [# of individuals] [selection type] [crossover type] ");
			System.out.println("[crossover probability] [mutation probability] [# of iterations] [algorithm]");
			System.exit(0);
		}
		
		fileName = args[0];
		int popSize	= Integer.parseInt(args[1]);
		int numGen	= Integer.parseInt(args[6]);
		
		double mutProb;
		
		int[][] SAT = SATFileReader.getFormula(fileName);
		
		algorithmType = args[7].charAt(0);
		
		switch(algorithmType)
		{
			// set up a genetic algorithm and its parameters
			case 'g' :
				
				String sType = args[2];
				String cType = args[3];
				
				GeneticAlg.SelectionMethod sel = sType.equals("ts") ?
						GeneticAlg.SelectionMethod.TOURNAMENT 	: sType.equals("rs") ?
						GeneticAlg.SelectionMethod.RANK			: sType.equals("bs") ?
						GeneticAlg.SelectionMethod.BOLTZMANN	: GeneticAlg.SelectionMethod.OTHER;
				
				GeneticAlg.CrossoverMethod cros = cType.equals("1c") ?
						GeneticAlg.CrossoverMethod.POINT : cType.equals("uc") ?
						GeneticAlg.CrossoverMethod.UNIFORM : GeneticAlg.CrossoverMethod.OTHER;
				
				if(sel.equals(GeneticAlg.SelectionMethod.OTHER))
				{
					System.out.println("Invalid selection type - use \"ts\", \"rs\", or \"bs\"");
					System.exit(0);
				}
				
				if(cros.equals(GeneticAlg.CrossoverMethod.OTHER))
				{
					System.out.println("Invalid crossover type - use \"1c\" or \"uc\"");
					System.exit(0);
				}
				
				double crossProb = Double.parseDouble(args[4]);
				mutProb   = Double.parseDouble(args[5]);
				
				g = new GeneticAlg(SAT, popSize, sel, cros, crossProb, mutProb, numGen);
				
				startTime	= System.currentTimeMillis();
				g.run();
				endTime		= System.currentTimeMillis();
				
				break;
				
				
			case 'p' :
				
				double alphaPlus	= Double.parseDouble(args[2]);
				double alphaNeg		= Double.parseDouble(args[3]);
				mutProb				= Double.parseDouble(args[4]);
				double mutShift		= Double.parseDouble(args[5]);
				
				p = new PBIL(SAT, popSize, alphaPlus, alphaNeg, mutProb, mutShift, numGen);
				
				startTime	= System.currentTimeMillis();
				p.run();
				endTime		= System.currentTimeMillis();
				
				break;
				
			default  :
				System.out.println("Invalid algorithm type. Use 'g' or 'p'.");
				System.exit(0);
		}
		
		printStuff(SAT.length, endTime - startTime);
		//terminalPrint(SAT.length, endTime - startTime);
	}
	
	// prints information about the algorithm
	private static void printStuff(int numClauses, double runTime)
	{
		int bestFitness;
		double ratio;
		int bestIteration;
		
		System.out.println();
		
		boolean[] bestAssignment = (algorithmType == 'p') ? p.getBestSolution() : g.getBestSolution();

		// assignment that achieves those results
		System.out.println("=================Best assignment==================");
		System.out.println("--------------------------------------------------");
		for(int i = 1; i <= SATFileReader.nVariables; ++i)
		{
			System.out.print(String.format("%1$5s", (bestAssignment[i - 1] ? i : "-" + i)  + " "));
			System.out.print((i%10 == 0 || i == SATFileReader.nVariables) ? "\n" : "");
		}
		System.out.println("--------------------------------------------------\n");
		
		//print name of the file containing the problem
		System.out.println(((algorithmType == 'p') ? "PBIL " : "G. ") + "algorithm runtime\t:  " + (endTime - startTime) / 1000 + " s");
				
		bestFitness = (algorithmType == 'p') ? p.getBestFitness() : g.getBestFitness();
		
		ratio = 100 * ((double)bestFitness / numClauses);
		
		// number of clauses that the best assignment found satisfies
		System.out.println("Clauses satisfied\t:  " + bestFitness + " out of " + numClauses + " (" + new DecimalFormat("#.#").format(ratio) + "%)" );
		
		// iteration during which the best assignment was found
		bestIteration = (algorithmType == 'p') ? p.getGeneration() : g.getGeneration();
		
		System.out.println("Found in generation\t:  " + bestIteration);
		System.out.println();
	}
	
	// minimal printing method - prints only the results as (% safisfied) and (time)
	private static void terminalPrint(int numClauses, double runTime)
	{
		int bestFitness = (algorithmType == 'p') ? p.getBestFitness() : g.getBestFitness();
		double ratio = 100 * ((double)bestFitness / numClauses) ;
		int bestIteration = (algorithmType == 'p') ? p.getBestFitness() : g.getBestFitness();
		
		System.out.println(new DecimalFormat("#.#").format(ratio) + "%\t\t" + " " + String.format("%1$5s",(endTime - startTime) / 1000));
	}
}

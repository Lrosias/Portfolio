import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class PBIL
{
	// individuals per generation, number of generations
	private int popSize, numGen;
	
	// learning rates ( + / - )
	private double alphaPlus, alphaNeg;
	
	// mutation rate and amount
	private double mutProb, mutShift;
	
	// probability vector
	private double[] pVect;
	
	// population vector
	private List<Individual> population;

	private int numClauses, numVars;
	
	// keep track of best solution
	private Individual bestEver;
	private int bestIteration;
	
	public PBIL(int[][] SAT, int popSize, double alphaPlus, double alphaNeg, double mutProb, double mutShift, int numGen)
	{
		// grab the arguments
		this.popSize 	= popSize;
		this.alphaPlus 	= alphaPlus;
		this.alphaNeg 	= alphaNeg;
		this.mutProb 	= mutProb;
		this.mutShift 	= mutShift;
		this.numGen 	= numGen;
		
		numClauses	= SAT.length;
		numVars		= SATFileReader.nVariables;
		
		bestEver 	= new Individual(numVars);
		
		// initialize probability vector
		pVect = new double[numClauses];
		
		for(int i = 0; i < numClauses; ++i)
			pVect[i] = 0.5;

		population = new Vector<Individual>(popSize);
	}
	
	public void run()
	{
		generatePopulation();
		
		for(int currentGen = 0; currentGen < numGen; ++currentGen)
		{	
			// return if we find a perfect solution
			if(bestEver.getFitness() == numVars)
				return;
			
			// generate and evaluate individuals into the population
			for(int i = 0; i < popSize; ++i)
			{
				population.get(i).setBits(pVect);
				population.get(i).evaluate();
			}
			
			sortList(population);
			
			Individual best, worst;
			
			worst = population.get(0);
			best  = population.get(popSize - 1);
			
			if(bestEver.getFitness() < best.getFitness()) 
			{
				bestEver = best;
				bestIteration = currentGen+1;
			}
			
			
			for(int i = 0; i < numVars; ++i)
			{
				boolean[] worstBits, bestBits;
				
				worstBits = worst.getBits();
				bestBits = best.getBits();
				
				// update towards best
				pVect[i] = pVect[i] * (1.0 - alphaPlus) + (bestBits[i] ? 1 : 0) * alphaPlus;
				
				// updates away from worst
				if(bestBits[i] != worstBits[i])
					pVect[i] = pVect[i] * (1.0 - alphaNeg) + (worstBits[i] ? 1 : 0) * alphaNeg;
				
				// mutate
				if(Math.random() < mutProb)
				{
					int mutDir = (Math.random() < 0.5) ? 1 : 0;
					
					pVect[i] = pVect[i] * (1.0 - mutShift) + mutDir * mutShift;
				}
			}
			// debug : System.out.println("Best in Generation[" + (currentGen+1) + "] = " + best.getFitness());
		}
		// debug : System.out.println("The best ever is " + bestEver.getFitness());
	}
	
	private void generatePopulation()
	{
		// initialize population
		for(int i = 0; i < popSize; ++i)
		{
			population.add(new Individual(numVars, pVect));
			population.get(i).evaluate();
		}
	}
	
	public int getBestFitness()
	{
		return bestEver.getFitness();
	}
	
	public int getGeneration()
	{
		return bestIteration;
	}
	
	private static void sortList(List<Individual> list)
	{
		Collections.sort(list, new Comparator<Individual>()
		{
			public int compare(Individual a, Individual b)
			{
				return new Integer(a.getFitness()).compareTo(b.getFitness());
			}
		});
	}
	
	public boolean[] getBestSolution()
	{
		return bestEver.getBits();
	}
	
	private static class Individual
	{
		private boolean[] bitstring;
		private int fitness;
		
		public Individual(int length, double[] p)
		{
			fitness = 0;
			bitstring = new boolean[length];
			
			setBits(p);
		}
		
		private void setBits(double[] p)
		{	
			for(int i = 0; i < bitstring.length; ++i)
				bitstring[i] = (Math.random() < p[i]) ? true : false;	
		}
		
		public Individual(int length)
		{
			fitness = 0;
		}
		
		public void evaluate()
		{
			fitness = SATFileReader.evalSAT(bitstring);
		}
		
		public int getFitness()
		{
			return fitness;
		}
		
		public boolean[] getBits()
		{
			return bitstring;
		}
	}	
}

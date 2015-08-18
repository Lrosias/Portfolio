import java.util.Random;
import java.util.Vector;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

/* Richardo Hopkins, Amy Sham, and Luis Rosias
 * 
 * GeneticAlg class
 * 
 * This class represents an instance of a genetic algorithm
 * on a given set of parameters. 
 * 
 */

public class GeneticAlg
{
	// number of times to iterate
	private int maxIter;
	
	// number of individuals per generation
	private int popSize;
	
	// mutation and crossover probabilities
	private double mutProb, crossProb;

	// possible selection and crossover types
	public static enum SelectionMethod { TOURNAMENT , RANK , BOLTZMANN, OTHER };
	public static enum CrossoverMethod { UNIFORM , POINT, OTHER };
	
	private SelectionMethod selectionType;
	private CrossoverMethod crossoverType;
	
	// the SAT data
	private int[][] data;
	
	// the population and breedingPool (set of individuals that are selected)
	private List<Individual> population, breedingPool;
	
	// number of variables in the SAT problem
	private int numVars;
	
	// keep track of best individual and which iteration it was found
	private Individual bestEver;
	private int bestIteration;
	
	
	public GeneticAlg(int[][] data, int popSize, SelectionMethod selectionType, CrossoverMethod crossoverType, 
			double crossProb, double mutProb, int maxIter)
	{
		this.data 			= data;
		this.popSize		= popSize;
		this.selectionType 	= selectionType;
		this.crossoverType 	= crossoverType;
		this.crossProb 		= crossProb;
		this.mutProb 		= mutProb;
		this.maxIter 		= maxIter;
		
		numVars = SATFileReader.nVariables;
		
		population = new Vector<Individual>(popSize);
		breedingPool = new Vector<Individual>(popSize);

		// create the initial population
		generatePop();
	}
	
	// the actual simulation of the Genetic Algorithm
	public void run()
	{
		bestEver = new Individual(null);
		
		for(int i = 0; i < maxIter; ++i)
		{
			// return if we find a perfect solution
			if(bestEver.getNumClauses() == data.length)
				return;
				
			Individual.setMin(data.length);

			// calculate number of clauses satisfied
			for(Individual ind : population)
				ind.setNumClauses(SATFileReader.evalSAT(ind.getBits()));
				
			// calculate fitness
			/*
			 * our fitness function is the number of clauses of each solution
			 * above the minimum of that generation (to get greedier)
			 */
			for(Individual ind : population)
				ind.setFitness(ind.getNumClauses() - Individual.minClauses + 1);
			
			// check if the best of this generation is the best ever
			if(bestEver.getFitness() < population.get(popSize - 1).getFitness()) 
			{
				// if so, record
				bestEver = population.get(popSize - 1);
				bestIteration = i+1;
			}
			
			sortPopulation(population);
			
			// select individuals and place in a breeding pool
			select();
			
			// crossover from breeding pool and generate offspring for next gen
			List<Individual> offspring = crossover();

			// mutate the offspring
			for(Individual child : offspring)
				child.mutate(mutProb);

			// set the offspring as the new population
			population = offspring;
		}
	}
	
	// populates the breeding pool by selecting 
	private void select()
	{
		boolean rank = false;
		
		switch(selectionType)
		{
			case TOURNAMENT :
				//int k = 1;
				int M = 2;
				
				Random r = new Random();
				
				while(breedingPool.size() < popSize)
				{
					// make a tournament pool of M individuals
					Individual[] pool = new Individual[M];

					for(int i = 0; i < M; ++i)
						pool[i] = population.get(r.nextInt(popSize)); // 5 needs to be changed to the number of variables??
				
					if(pool[0].getFitness() > pool[1].getFitness())
						breedingPool.add(pool[0]);
					else
						breedingPool.add(pool[1]);
				}
				break;
				
		
			case RANK :
				rank = true;
				// rank selection is essentially the same as Boltzmann minus the exponentials
				// so carry through
				
			case BOLTZMANN :
				
				double sum = 0;
				
				if(rank)
					// calculate sum of fitnesses for Rank selection
					for(Individual indiv : population)
						sum += indiv.getFitness();
				else
					// calculate sum for Boltzmann using exponentials
					for(Individual indiv : population)
						sum += Math.exp(indiv.getFitness());
				
				
				// calculate each individuals probability of being selected
				for(Individual indiv : population)
				{
					double p = rank ?
					/* Rank Case */	indiv.getFitness() / sum :
					/* Boltzmann */	Math.exp(indiv.getFitness())/sum;
					
					// save this probability
					indiv.setProb(p);
				}
				
				// populate the breeding pool
				while(breedingPool.size() < popSize)
				{
					double p = Math.random();
					
					for(Individual ind : population)
					{
						if(ind.getProb() > p)
						{
							breedingPool.add(ind);
							break;
						}
						else
							p -= ind.getProb();
					}
				}
				break;
		}
		
	}
	
	// populates the offspring pool with offspring of parents (if they have any)
	private List<Individual> crossover()
	{
		List<Individual> offspring = new Vector<Individual>(popSize);
		
		// sort the breeding pool to pair parents based on fitness
		sortPopulation(breedingPool);
		
		for(int i = 0; i < popSize ; i++)
		{
			if(crossProb > Math.random())
			{
				// crossover occurs - choose first pair
				Individual a = breedingPool.get(  i);
				Individual b = breedingPool.get(++i%popSize);
				
				// perform selected type of crossover
				switch(crossoverType)
				{
					case UNIFORM:
						// we crossover twice, to maintain pool size
						offspring.add(Individual.uniCrossover(a, b));
						offspring.add(Individual.uniCrossover(b, a));
						
						break;
						
					case POINT:
						// 1-point crossover produces two children - add both
						Individual[] children = Individual.pointCrossover(a, b);
						
						offspring.add(children[0]);
						offspring.add(children[1]);
						
						break;
				}
			}
			else
			{
				// no crossover, so adults move on to next gen
				offspring.add(breedingPool.get(  i));
				offspring.add(breedingPool.get(++i%popSize));
			}
		}
		return offspring;
	}
	
	// generate a random population of solutions/individuals
	private void generatePop()
	{
		for(int i = 0; i < popSize; ++i)
			population.add(generateIndiv());
	}
	
	// helper method to generate a new individual
	private Individual generateIndiv()
	{
		boolean[] bitstring = new boolean[numVars];
		Random r = new Random();
		
		for(int i = 0; i < numVars; ++i)
			bitstring[i] = r.nextInt(2) == 0 ? false : true;
		
		return new Individual(bitstring);
	}
	
	// use java.util.Collections.sort() to sort lists
	private void sortPopulation(List<Individual> list)
	{
		Collections.sort(list, new Comparator<Individual>()
		{
			public int compare(Individual a, Individual b)
			{
				return new Double(a.getFitness()).compareTo(b.getFitness());
			}
		});
	}
	
	@SuppressWarnings("unused")
	private void printMostFit()
	{
		double clauseRatio = (double) population.get(popSize - 1).getNumClauses() / (double) data.length;
		
		System.out.println("Most fit individual satisfied " + (int)population.get(popSize - 1).getNumClauses() 
				+ " out of " + data.length + " clauses(" + clauseRatio + ").");
	}
	
	// returns fitness of the best individual
	public int getBestFitness()
	{
		return bestEver.getNumClauses();
	}
	
	// returns the bitstring representation of the best solution
	public boolean[] getBestSolution()
	{
		return bestEver.getBits();
	}
	
	// returns the generatino in which the best solution was found
	public int getGeneration()
	{
		return bestIteration;
	}
	

	/*
	 * ==================================
	 *   Individual static nested class
	 * ==================================
	 */
	
	private static class Individual
	{
		// bitstring representation of the solutoin
		private boolean[] bitstring;
		
		// number of clauses satisfied 
		private int clausesSatisfied;
		
		// fitness level as function of clauses satisfied
		private double fitness;
		
		// probability to be selected
		private double selectionProb;
		
		// class variable - current lowest number of clauses satisfied by any individual
		public static double minClauses;
		
		public Individual(boolean[] b)
		{
			bitstring = b;
			fitness = -1;
			selectionProb = 0.0;
		}
		
		public static void setMin(double min)
		{
			minClauses = min;
		}
		
		public static Individual uniCrossover(Individual a, Individual b)
		{
			boolean[] bitstring = new boolean[a.getBits().length];
			
			Random r = new Random();
			
			for(int i = 0; i < bitstring.length; ++i)
				bitstring[i] = (r.nextInt(2) == 0) ? a.getBits()[i] : b.getBits()[i];
			
			return new Individual(bitstring);
		}
		
		public static Individual[] pointCrossover(Individual a, Individual b)
		{
			int pivot = new Random().nextInt(a.getBits().length);
			
			boolean[] bitsA = new boolean[a.getBits().length];
			boolean[] bitsB = new boolean[b.getBits().length];
			
			for(int i = 0; i < pivot; i++)
			{
				bitsA[i] = a.getBits()[i];
				bitsB[i] = b.getBits()[i];
			}
			
			for(int i = pivot; i < bitsA.length; i++)
			{
				bitsB[i] = a.getBits()[i];
				bitsA[i] = b.getBits()[i];
			}

			Individual offspringA = new Individual(bitsA);
			Individual offspringB = new Individual(bitsB);
			
			return new Individual[] { offspringA, offspringB };
		}
		
		public boolean[] getBits()
		{
			return bitstring;
		}
		
		public double getFitness()
		{
			return fitness;
		}
		
		public void setFitness(double f)
		{
			fitness = f;
		}
		
		public int getNumClauses()
		{
			return clausesSatisfied;
		}
		
		public void setNumClauses(int clauses)
		{
			clausesSatisfied = clauses;
			if(clausesSatisfied < minClauses)
				minClauses = clausesSatisfied;
		}
		
		public void mutate(double p)
		{
			for(int i = 0; i < bitstring.length; ++i)
				if(Math.random() < p)
					bitstring[i] = ! bitstring[i];
		}
		
		public double getProb()
		{
			return selectionProb;
		}
		
		public void setProb(double p)
		{
			selectionProb = p;
		}
		
		/*
		public void print()
		{
			for(boolean b : bitstring)
				System.out.print(b ? "#" : "_");
			System.out.println();	 	
		}
		*/
	}
}

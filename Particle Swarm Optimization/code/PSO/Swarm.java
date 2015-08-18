package PSO;

import java.util.ArrayList;

import Influence.NeighborhoodInfluence;
import Topology.NeighborhoodTopology;

public class Swarm
{
	// particles - i.e., the swarm itself
	private ArrayList<Particle>		particles;

	// option of whether or not a neighborhood of a particle
	// is to include that particle
	private boolean					includeSelf;

	// the neighborhood topology that will be tested on -
	// (refer to the enum declared at end of class)
	private NeighborhoodTopology	topology;

	// the neighborhood influence that will be tested with -
	// (refer to the enum declared at end of class)
	private NeighborhoodInfluence	influence;

	// the function we are attempting to optimize
	// (refer to the enum declared at end of class)
	private Function				func;

	// number of iterations to run the algorithm
	private final int				iterations;

	// dimensionality of the solution space
	private final int				dimensions;

	// number of particles on the swarm
	private final int				swarmSize;

	// best value we've seen
	private double					globalBest;

	// array of globalBest values found at each iteration - 
	// for testing purposes
	public double[]				storedGlobalBests;

	// *** PSO parameters ***
	// PHI - total acceleration factor (found in NeighborhoodInfluence.java)
	// Because phi1 = phi2, only phi is recorded to simplify expressions
	// CHI - the constriction coefficient
	private static final double		CHI	= 0.7298;

	public Swarm(NeighborhoodTopology topology, boolean includeSelf,
			NeighborhoodInfluence influence, int swarmSize, int iterations, Function function,
			int dimensions)
	{
		this.topology = topology;
		this.influence = influence;

		this.func = function;

		this.iterations = iterations;
		this.dimensions = dimensions;
		this.swarmSize = swarmSize;

		this.includeSelf = includeSelf;

		globalBest = Double.MAX_VALUE;
        	storedGlobalBests = new double[iterations];

		initialize();
	}

	// sets up all the data necessary to run the PSO
	private final void initialize()
	{
		// initialize particles
		particles = new ArrayList<Particle>(swarmSize);
		for (int i = 0; i < swarmSize; ++i)
		{
			double[] initialPosition = new double[dimensions];

			// make initial position in the correct bounds
			for (int d = 0; d < dimensions; ++d)
				initialPosition[d] = func.initPosition();

			// create a new particle with initial position
			// then add it to the array
			particles.add(new Particle(initialPosition));

			// compute initial fitness, and set to personal best
			Particle particle = particles.get(i);
			double result = func.compute(particle.getPosition());
			particle.updatePersonalBest(result);
		}
	}

	// performs the PSO algorithm
	public void run()
	{
		// this function will assign a neighborhood to every particle
		topology.assignNeighborhoods(particles);

		for (int i = 0; i < iterations; ++i)
		{
			for (Particle particle : particles)
			{
				for (int d = 0; d < dimensions; ++d)
				{
					// calculate acceleration based on influence structure
					double acceleration = influence.calculateAcceleration(particle, d, includeSelf);
					double velocity = particle.getVelocityAt(d);
					
					// accelerate and constrict the velocity
					velocity += acceleration;
					velocity *= CHI;

					// this updates a particle's velocity and its position
					particle.update(d, velocity);
				}

				double fitness = func.compute(particle.getPosition());

				if (fitness < particle.getPersonalBestValue())
					particle.updatePersonalBest(fitness);

				if (fitness < globalBest)
					globalBest = fitness;

				// if the topology is random, this will recalculate the
				// particle's neighborhood. else, it does nothing
				topology.updateNeighbors(particle, particles);
			}

			storedGlobalBests[i] = globalBest;
		}

		//System.out.println("Final globalBest value found " +  " is:\t" + globalBest);
	}

	public double[] getStoredGlobalBests()
	{
		// returns array of globalBests per iteration (for testing purposes)
		return storedGlobalBests;
	}

	public double getGlobalBest()
	{
		// for testing purposes
		return globalBest;
	}

	// ===========================
	// ******** Functions ********
	// ===========================

	public enum Function
	{
		SPHERE	// easy
		{
			@Override
			public double compute(double[] dimensions)
			{
				double result = 0;

				for (double dimension : dimensions)
					result += dimension * dimension;

				return result;
			}

			@Override
			public double initPosition()
			{
				return Math.random() * 50 + 50;
			}

		},

		ROSENBROCK	// hard
		{
			@Override
			public double compute(double[] dimensions)
			{
				double result = 0;

				for (int i = 0; i < dimensions.length - 1; ++i)
					result += 100 * Math.pow(dimensions[i + 1] - dimensions[i] * dimensions[i], 2) 
							+ Math.pow(dimensions[i] - 1, 2);

				return result;
			}

			@Override
			public double initPosition()
			{
				return Math.random() * 15 + 15;
			}
		},

		GRIEWANK	//medium
		{
			@Override
			public double compute(double[] x)
			{
				double sum = 0.0;
				double product = 1.0;

				for (int i = 0; i < x.length; i++)
				{
					sum += ((x[i] * x[i]) / 4000.0);
					product *= Math.cos(x[i] / Math.sqrt(i + 1));
				}

				return (sum - product + 1.0);
			}

			@Override
			public double initPosition()
			{
				return Math.random() * 300 + 300;
			}
		},

		ACKLEY	// medium
		{
			@Override
			public double compute(double[] x)
			{
				double sum1 = 0.0;
				double sum2 = 0.0;

				for (int i = 0; i < x.length; i++)
				{
					sum1 += (x[i] * x[i]);
					sum2 += (Math.cos(2 * Math.PI * x[i]));
				}

				return (-20.0 * Math.exp(-0.2 * Math.sqrt(sum1 / ((double) x.length)))
						- Math.exp(sum2 / ((double) x.length)) + 20.0 + Math.E);
			}

			@Override
			public double initPosition()
			{
				return Math.random() * 16 + 16;
			}
		},

		RASTRIGIN	// hard
		{
			@Override
			public double compute(double[] inputs)
			{
				double res = 10 * inputs.length;

				for (int i = 0; i < inputs.length; i++)
					res += inputs[i] * inputs[i] - 10 * Math.cos(2 * Math.PI * inputs[i]);

				return res;
			}

			@Override
			public double initPosition()
			{
				return Math.random() * 2.56 + 2.56;
			}
		};

		public abstract double compute(double[] dimensions);

		public abstract double initPosition();
	}
}

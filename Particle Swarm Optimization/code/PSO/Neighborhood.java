package PSO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

public class Neighborhood implements Observer
{
	// particles in the neighborhood
	private ArrayList<Particle> neighborhood;
	
	// best particle
	private Particle localBest;
	
	// runner up particle - this is when we want to ignore the self
	private Particle runnerUp;

	public Neighborhood(ArrayList<Particle> neighbors)
	{
		neighborhood = neighbors;
		
		for(Particle p : neighbors)
			p.addObserver(this);
		
		localBest = Collections.min(neighbors);
		
		// arbitrarily assign runner up - it will be updated soon
		runnerUp = neighborhood.get(0);
	}
	
	// this method is invoked whenever any particle in the neighborhood
	// updates its personal best, to check if the local best of the 
	// neighborhood should be updated as well
	@Override
	public void update(Observable obv, Object data)
	{	
		Particle challenger = (Particle) data;
		
		// if challenger is better than best,
		// localBest <- challenger
		// runnerUp <- old localBest
		if(challenger.compareTo(localBest) < 0)
		{
			Particle oldLocal = localBest;
			localBest = challenger;
			runnerUp = oldLocal;
		}
		else if(challenger.compareTo(runnerUp) < 0)
			runnerUp = challenger;
	}
	
	public Particle getLocalBest()
	{
		return localBest;
	}
	
	public Particle getRunnerUp()
	{
		return runnerUp;
	}
	
	public ArrayList<Particle> getNeighbors()
	{
		return neighborhood;
	}
	
	public int size()
	{
		return neighborhood.size();
	}
}

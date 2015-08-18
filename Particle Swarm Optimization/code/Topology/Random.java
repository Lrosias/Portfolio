package Topology;

import java.util.ArrayList;

import PSO.Neighborhood;
import PSO.Particle;

// random chooses k elements randomly without repetition
// as the neighborhood
public class Random implements NeighborhoodTopology
{
	@Override
	public Neighborhood getNeighbors(Particle self, ArrayList<Particle> particles)
	{
		ArrayList<Particle> neighbors = new ArrayList<Particle>();

		while (neighbors.size() == 0)
			for (Particle p : particles)
				if ((!p.equals(self)) && Math.random() <= .5)
					neighbors.add(p);

		return new Neighborhood(neighbors);
	}

	@Override
	public void assignNeighborhoods(ArrayList<Particle> particles)
	{
		for (Particle p : particles)
		{
			Neighborhood nh = getNeighbors(p, particles);
			p.setNeighborhood(nh);
		}
	}

	@Override
	public void updateNeighbors(Particle self, ArrayList<Particle> particles)
	{
		// want to remove all references to neighborhoods that we want to delete
		self.deleteObservers();
		self.setNeighborhood(getNeighbors(self, particles));
	}
}
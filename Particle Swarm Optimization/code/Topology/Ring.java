package Topology;

import java.util.ArrayList;

import PSO.Neighborhood;
import PSO.Particle;

// local best uses the left and right elements of the array of a
// given particle as the neighbors
public class Ring implements NeighborhoodTopology
{
	@Override
	public Neighborhood getNeighbors(Particle self, ArrayList<Particle> particles)
	{
		ArrayList<Particle> neighbors = new ArrayList<Particle>();

		int swarmSize = particles.size();

		// the extra math prevents negative numbers produced from negative mods
		// (prevent out of bounds)
		int leftIndex = ((particles.indexOf(self) - 1) % swarmSize + swarmSize) % swarmSize;
		int rightIndex = ((particles.indexOf(self) + 1) % swarmSize + swarmSize) % swarmSize;

		neighbors.add(particles.get(leftIndex));
		neighbors.add(particles.get(rightIndex));

		// always add self - responsibility of includeSelf is the influence
		neighbors.add(self);

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
	}
}
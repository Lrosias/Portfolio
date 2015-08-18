package Topology;

import java.util.ArrayList;

import PSO.Neighborhood;
import PSO.Particle;

// global uses the whole swarm as the neighborhood
public class Global implements NeighborhoodTopology
{
	@Override
	public Neighborhood getNeighbors(Particle self, ArrayList<Particle> particles)
	{
		ArrayList<Particle> neighbors = new ArrayList<Particle>(particles);
		return new Neighborhood(neighbors);
	}

	// there need be only a single neighborhood that all particles share
	@Override
	public void assignNeighborhoods(ArrayList<Particle> particles)
	{
		// choose arbitrary particle
		Neighborhood nh = getNeighbors(particles.get(0), particles);

		for (Particle p : particles)
			p.setNeighborhood(nh);
	}

	@Override
	public void updateNeighbors(Particle self, ArrayList<Particle> particles)
	{
	}
}
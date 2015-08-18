package Topology;

import java.util.ArrayList;

import PSO.Neighborhood;
import PSO.Particle;

// vonNeumann treats the array as a wrapped 2d array, and defines the
// north, east, west, and south elements as the neighbors
// * vonNeumann assumes the size of the swarm is a perfect square
public class VonNeumann implements NeighborhoodTopology
{
	@Override
	public Neighborhood getNeighbors(Particle self, ArrayList<Particle> particles)
	{
		ArrayList<Particle> neighbors = new ArrayList<Particle>();

		int swarmSize = particles.size();

		int particleIndex = particles.indexOf(self);
		int sideLength = (int) Math.sqrt(swarmSize);

		// now we can think of the swarm as represented by a 2-d array
		// in which i = row, j = col, and index = col*side + row
		// therefore, i = index%sideLength
		// and j = index/sideLength

		int row = particleIndex % sideLength;
		int col = particleIndex / sideLength;

		// north south east west
		int nIndex = ((col - 1) % sideLength + sideLength) % sideLength * sideLength + row;
		int sIndex = (col + 1) % sideLength * sideLength + row;
		int eIndex = col * sideLength + ((row - 1) % sideLength + sideLength) % sideLength;
		int wIndex = col * sideLength + (row + 1) % sideLength;

		neighbors.add(particles.get(nIndex));
		neighbors.add(particles.get(sIndex));
		neighbors.add(particles.get(eIndex));
		neighbors.add(particles.get(wIndex));

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
package Influence;

import PSO.Neighborhood;
import PSO.Particle;

public class FullyInformed implements NeighborhoodInfluence
{
	@Override
	public double calculateAcceleration(Particle self, int d, boolean includeSelf)
	{
		double acceleration = 0;

		Neighborhood neighborhood = self.getNeighborhood();
		double phi_fraction = PHI / neighborhood.size() - (includeSelf ? 0 : 1);

		for (Particle neighbor : neighborhood.getNeighbors())
		{
			if (!includeSelf && neighbor.equals(self))
				continue;

			acceleration += Math.random() * phi_fraction
					* (neighbor.getPersonalBestLocation()[d] - self.getPosition()[d]);
		}

		return acceleration;
	}
}
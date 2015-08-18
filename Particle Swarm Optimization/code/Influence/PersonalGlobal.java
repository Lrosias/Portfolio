package Influence;

import PSO.Neighborhood;
import PSO.Particle;

public class PersonalGlobal implements NeighborhoodInfluence
{
	@Override
	public double calculateAcceleration(Particle self, int d, boolean includeSelf)
	{
		Neighborhood neighborhood = self.getNeighborhood();
		Particle localBestParticle = neighborhood.getLocalBest();

		double aPersonal = Math.random() * PHI / 2
				* (self.getPersonalBestLocation()[d] - self.getPosition()[d]);
		double aGlobal = Math.random() * PHI / 2
				* (localBestParticle.getPosition()[d] - self.getPosition()[d]);

		return aPersonal + aGlobal;
	}
}
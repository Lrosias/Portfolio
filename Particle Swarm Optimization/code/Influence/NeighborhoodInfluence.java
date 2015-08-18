package Influence;
import PSO.Particle;


// ============================
// ******* Influences ********
// ============================
/*
 * A neighborhood influence is basically a policy for computing the
 * acceleration of a given particle.
 * 
 * The 2 policies are:
 * 
 * #Personal/Global - computes acceleration as a function of 2 particles:
 * the personal best and the neighborhood's local best.
 * 
 * #FullyInformed - computes acceleration as a function of the k particles
 * residing in the neighborhood.
 */

// All concrete subclasses of this class are defined and implemented
// in Swarm.java. 
public interface NeighborhoodInfluence
{
	public static final double PHI = 2.05;
	
	public abstract double calculateAcceleration(Particle self, int d, boolean includeSelf);
}

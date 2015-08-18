package Topology;
import java.util.ArrayList;

import PSO.Neighborhood;
import PSO.Particle;


// ============================
// ******* Topologies *********
// ============================
/*
 * All topologies implement the NeighborhoodTopology interface defined in
 * NeighborhoodTopology.java
 * 
 * Each defines the function getNeighbors, which when given a particle,
 * returns the neighborhood centered on that particle.
 * 
 * A neighborhood is defined as an ArrayList of particles, and also keeps
 * track of its local best value.
 */

// All concrete subclasses of this class are defined and implemented
// in Swarm.java. 
public interface NeighborhoodTopology
{
	// given a particle, this function shall calculate its neighbors
	public abstract Neighborhood getNeighbors(Particle self, ArrayList<Particle> particles);

	// this function shall, for a given swarm, assign a particle
	// the neighborhood that it is centered around (i.e., the
	// neighborhood around which the particle will base its
	// computations.
	public abstract void assignNeighborhoods(ArrayList<Particle> particles);
	
	// this function is to allow for certain topologies (e.g,
	// random) to change its neighborhoods each iteration
	public abstract void updateNeighbors(Particle self, ArrayList<Particle> particles);
}

package PSO;
import java.util.Observable;

public class Particle extends Observable implements Comparable<Particle>
{
	// needs to have one position/velocity per dimension (x, y, etc..)
	private double[]		position;
	private double[]		velocity;
	
	// keeps track of a particle's best solution
	private double[]		personalBest;
	private double			personalBestValue;
	
	// the neighborhood the particle will base its computations around
	private Neighborhood	neighborhood;

	public Particle(double[] initialPosition)
	{
		position = initialPosition;
		personalBest = position.clone();
		
		initVelocity();
	}
	
	public void setNeighborhood(Neighborhood neighborhood)
	{
		this.neighborhood = neighborhood;
	}
	
	public Neighborhood getNeighborhood()
	{
		return neighborhood;
	}
	
	// initializes a velocity in the range [-3.0, 3.0]
	private final void initVelocity()
	{
		velocity = new double[position.length];
		
		for(int d = 0; d < velocity.length; ++d)
			velocity[d] = Math.random() * 6.0 - 3.0;
	}

	public double[] getPosition()
	{
		return position;
	}

	public void setPosition(double[] newPosition)
	{
		position = newPosition;
	}
	
	public double getVelocityAt(int d)
	{
		return velocity[d];
	}

	// updates a particles position and velocity
	public void update(int d, double v)
	{
		velocity[d] = v;
		position[d] += v;
	}
	
	public double getPersonalBestValue()
	{
		return personalBestValue;
	}
	
	public double[] getPersonalBestLocation()
	{
		return personalBest;
	}
	
	// this method is called as soon as a particle's fitness is computed
	public void updatePersonalBest(double newBest)
	{
		// thus, personalBest's position is its current position
		personalBest = position.clone();
		personalBestValue = newBest;
		
		// these methods are inherited from the observer class
		// this notifies all observers (neighborhoods) to check their local best
		setChanged();
		notifyObservers(this);
	}
	
	@Override
	public int compareTo(Particle other)
	{
		return ((Double)personalBestValue).compareTo(other.getPersonalBestValue());
	}
}
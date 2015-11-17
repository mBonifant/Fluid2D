package boundary.conditions;

import lattice.Q;

/**
 * Boundary Condition for a cell marked as a wall, where fluid bounces back,
 * based loosely on Jean-Luc Falcone's BounceBack class in his implementation of
 * the LBM. //TODO link to other implementation
 * 
 * @author bonifantmc
 *
 */
public class Wall implements BoundaryCondition {
	/** walls is dense yo */
	private final double RHO = 1.0f;
	/** velocities */
	private final double[] uTemp;
	/** number of microscopic velocities in use */
	private Q q;
	/** new output */
	double[] fPrime;

	/** bounce back for Q9 */
	private static final Wall w9 = new Wall(Q.nine);
	/** bounce back for Q15 */
	private static final Wall w15 = new Wall(Q.fifteen);
	/** bounce back for Q19 */
	private static final Wall w19 = new Wall(Q.nineteen);
	/** bounce back for Q27 */
	private static final Wall w27 = new Wall(Q.twentySeven);

	/**
	 * 
	 * @param size
	 *            the number of microscopic velocities the lattice is using
	 */
	private Wall(Q size) {
		this.q = size;
		this.uTemp = new double[3];
		this.fPrime = new double[this.q.size];
	}

	/**
	 * Get the Wall/BouceBack BondaryCondition for Q 9/15/19/27
	 * 
	 * @param i
	 *            the number of microscopic velocities the lattice is using
	 * @return the BoundaryCondition for the proper Q
	 */
	public static Wall getBoundaryCondition(Q i) {
		switch (i) {
		case nine:
			return w9;
		case fifteen:
			return w15;
		case nineteen:
			return w19;
		case twentySeven:
			return w27;
		}
		System.err.println("Unreachable code");
		System.exit(-1);
		return null;
	}

	/**
	 * @param f
	 *            data about point being inspected
	 * @return rho, the density
	 */
	public double rho(double[] f) {
		return this.RHO;
	}

	/**
	 * @param f
	 *            data about point being inspected
	 * @return U, velocity distribution
	 */
	public double[] u(double[] f, double[] u) {
		return this.uTemp;
	}

	/**
	 * When fluid collides with a Wall it bounces back like magic and flips the
	 * velocities
	 * 
	 * @param f
	 *            the data being updated
	 */
	public void collide(double[] f, double rho, double[] u, double uNorm2, double omega) {
		for (int i = 0; i < this.q.size; i++) {
			this.fPrime[i] = f[this.q.opposites[i]];
			System.arraycopy(this.fPrime, 0, f, 0, this.q.size);
		}
	}

	@Override
	public double computeEquilibrium(int i, double rho, double[] u, double uNorm2) {
		throw new UnsupportedOperationException();
	}

}
package boundary.conditions;

import Fluids.Liquid;
import lattice.Cell;
import lattice.Q;

/**
 * Boundary Condition for a cell marked as a wall, where fluid bounces back,
 * based loosely on Jean-Luc Falcone's BounceBack class in his implementation of
 * the LBM.
 * 
 * @author bonifantmc
 *
 */
public class Wall implements BoundaryCondition {
	/** number of microscopic velocities in use */
	private Q q;
	/** temp array to hold fi's while they're being swapped */
	private double[] fP;
	/** the liquid rebounding against the wall */
	private Liquid l;

	/**
	 * 
	 * @param size
	 *            the number of microscopic velocities the lattice is using
	 * @param l
	 *            the Liquid rebounding against the wall
	 */
	public Wall(Q size, Liquid l) {
		this.q = size;
		this.fP = new double[size.size];
		this.setL(l);
	}

	public void computeRho(Cell c) {
		c.setRho(this.getL().getDensity());
	}

	public void computeU(Cell c) {
		c.setU(0, 0);
	}

	public void computeCollision(Cell c) {
		computeRho(c);
		computeU(c);
		for (int i = 0; i < this.q.size; i++)
			this.fP[i] = c.getF()[this.q.opposites[i]];
		System.arraycopy(this.fP, 0, c.getF(), 0, this.q.size);
	}

	@Override
	public double computeIthEquilibrium(Cell c, int i) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return "Wall";
	}

	public int size() {
		return this.q.size;
	}

	public Liquid getL() {
		return l;
	}

	public void setL(Liquid l) {
		this.l = l;
	}

}
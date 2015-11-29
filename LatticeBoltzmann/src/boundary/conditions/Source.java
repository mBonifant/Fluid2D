package boundary.conditions;

import Fluids.Liquid;
import lattice.Cell;
import lattice.Q;

/***
 * Boundary Condition for a cell marked as a source, where fluid enters the
 * system
 * 
 * @author bonifantmc
 *
 */
public class Source implements BoundaryCondition {
	/** number of micro velocity packets in use */
	private final Q q;
	/** macroscopic velocity at at Source */
	public double[] u = { 1, 0 };
	/** macroscopic speed at Source */
	private double uNorm;
	/** The Liquid emanating from the source */
	private Liquid l;

	/**
	 * @param i
	 *            microvelocity packet count
	 * @param _u
	 *            macro velocity the Liquid comes out
	 * @param l
	 *            the Luiqid coming out of this source
	 */
	public Source(Q i, double[] _u, Liquid l) {
		this.q = i;
		this.u = new double[2];
		this.u[0] = _u[0];
		this.u[1] = _u[1];
		this.uNorm = this.u[0] * this.u[0] + this.u[1] * this.u[1];
		this.setL(l);
	}

	@Override
	public void computeCollision(Cell c) {
		computeRho(c);
		computeU(c);
		for (int i = 0; i < this.q.size; i++) {
			c.setF((1 - this.getL().getOmega()) * c.getF()[i] + this.getL().getOmega()
					* computeIthEquilibrium(c, i), i);
		}
	}

	// its a Source new fluid enters
	@Override
	public void computeRho(Cell c) {
		double f = 0;
		for (double ff : c.getF())
			f += ff;
		c.setRho(f);

	}

	/** its a Source new fluid flows in at the specified rate */
	@Override
	public void computeU(Cell c) {
		c.setU(this.u[0], this.u[1]);
	}

	// TODO currently this is flowing left to right, need to specify if flowing
	// in other directions,
	@Override
	public double computeIthEquilibrium(Cell c, int i) {
		double f = 0;
		double uDotE = c.getU()[0] * this.q.velocities[i][0] + c.getU()[1]
				* this.q.velocities[i][1];

		f = (double) (1.0 + (3.0 + 4.5 * uDotE) * uDotE - 1.5 * c.getSpeedSq());
		return c.getRho() * this.q.weights[i] * f;

	}

	public String toString() {
		return "Source";
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

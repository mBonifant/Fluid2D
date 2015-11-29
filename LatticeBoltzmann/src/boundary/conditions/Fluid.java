package boundary.conditions;

import Fluids.Liquid;
import lattice.Cell;
import lattice.Q;
import lattice.Velocity;

/**
 * Boundary Condition for a cell marked as fluid, where fluid flows into more
 * fluid, uses the LBGK aproximation for collisions
 * 
 * @author bonifantmc
 *
 */
@SuppressWarnings("unused")
public class Fluid implements BoundaryCondition {
	/** the number of microscopic velocities in use */
	protected final Q q;
	/**
	 * a time constant related to decay towards equilibrium based on the
	 * viscosity of the fluid
	 */
	private Liquid l;

	/**
	 * 
	 * @param i
	 *            number of microscopic velocities to expect
	 * @param l
	 *            time constant related to viscosity and dealing with decay
	 *            towards equilibrium
	 */
	public Fluid(Q i, Liquid l) {
		this.setL(l);
		this.q = i;
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

	@Override
	public void computeRho(Cell c) {
		double rho = 0;
		for (double ff : c.getF()) {
			rho += ff;
		}
		c.setRho(rho);
	}

	@Override
	public void computeU(Cell c) {
		double d1 = 0;
		double d2 = 0;
		for (int i = 0; i < this.q.size; i++) {
			d1 += this.q.velocities[i][0] * c.getF()[i];
			d2 += this.q.velocities[i][1] * c.getF()[i];
		}

		c.setU(d1 / c.getRho(), d2 / c.getRho());
	}

	@Override
	public double computeIthEquilibrium(Cell c, int i) {
		double f = 0;
		double uDotE = (c.getU()[0] * this.q.velocities[i][0])
				+ (c.getU()[1] * this.q.velocities[i][1]);

		f = (double) (1 + 3 * uDotE + 4.5 * uDotE * uDotE - 1.5 * c
				.getSpeedSq());
		return c.getRho() * this.q.weights[i] * f;
	}

	public String toString() {
		return "Fluid";
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
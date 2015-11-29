package boundary.conditions;

import lattice.Cell;
import lattice.Q;
import Fluids.Liquid;

/**
 * Boundary Condition for a cell marked as a sink, where fluid drains from the
 * system //TODO NOT SPECIFIED AS A REQUIREMENT FUN BONUS IF YOU DO IT THOUGH
 * 
 * @author bonifantmc
 *
 */
public class Sink implements BoundaryCondition {
	/** the number of micovelocities in use */
	private Q q;
	/** the type of liquid flowing into the sink */
	private Liquid l;

	/**
	 * @param i
	 *            the number of microscopic velocities in use
	 * @param l
	 *            the liquid being sucked into the sink
	 */
	public Sink(Q i, Liquid l) {
		this.q = i;
		this.setL(l);
	}

	@Override
	public void computeCollision(Cell c) {
		computeRho(c);
		computeU(c);
		for (int i = 0; i < this.q.size; i++) {
			double f0, f1;
			f0 = c.getF()[i];
			f1 = computeIthEquilibrium(c, i);

			double ret = (1 - this.getL().getOmega()) * f0 + this.getL().getOmega() * f1;
			c.setF(ret, i);

		}
	}

	@Override
	public void computeRho(Cell c) {
		c.setRho(this.getL().getDensity());
	}

	@Override
	public void computeU(Cell c) {

		double d1 = 0;
		double d2 = 0;
		for (int i = 0; i < this.q.size; i++) {

			d1 += this.q.velocities[i][0] * c.getF()[i];
			d2 += this.q.velocities[i][1] * c.getF()[i];
		}

		c.setU(d1, d2);

	}

	// TODO only does out flow to the left currently
	@Override
	public double computeIthEquilibrium(Cell c, int i) {
		double f = 0;
		double uDotE = c.getU()[0] * this.q.velocities[i][0] + c.getU()[1]
				* this.q.velocities[i][1];
		f = (double) (1.0 + (3.0 + 4.5 * uDotE) * uDotE - 1.5 * c.getSpeedSq());
		return c.getRho() * this.q.weights[i] * f;
	}

	public String toString() {
		return "Sink";
	}

	@Override
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
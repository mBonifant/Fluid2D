package boundary.conditions;

import lattice.Q;

/***
 * Boundary Condition for a cell marked as a source, where fluid enters the
 * system
 * 
 * @author bonifantmc
 *
 */
public class Source extends Fluid {
	private static final Source s9 = new Source(Q.nine);
	private static final Source s15 = new Source(Q.fifteen);
	private static final Source s19 = new Source(Q.nineteen);
	private static final Source s27 = new Source(Q.twentySeven);

	public static Source getBoundaryCondition(Q i) {
		switch (i) {
		case nine:
			return s9;
		case fifteen:
			return s15;
		case nineteen:
			return s19;
		case twentySeven:
			return s27;
		}
		System.err.println("Unreachable code");
		System.exit(-1);
		return null;
	}

	private Source(Q i) {
		super(i);
	}

	@Override
	public void collide(double[] f, double rho, double[] u, double uNorm2, double omega) {
		// TODO Auto-generated method stub

	}

	@Override
	public double rho(double[] f) {
		return super.rho(f);
	}

	@Override
	public double[] u(double[] f, double[] u) {
		return super.u(f, u);
	}

	@Override
	public double computeEquilibrium(int i, double rho, double[] u, double uNorm2) {
		return i == 1 ? 1f : 0f;
	}
}

package boundary.conditions;

import lattice.Q;

/**
 * Boundary Condition for a cell marked as a sink, where fluid drains from the
 * system //TODO NOT SPECIFIED AS A REQUIREMENT FUN BONUS IF YOU DO IT THOUGH
 * 
 * @author bonifantmc
 *
 */
public class Sink extends Fluid {
	private static final Sink s9 = new Sink(Q.nine);
	private static final Sink s15 = new Sink(Q.fifteen);
	private static final Sink s19 = new Sink(Q.nineteen);
	private static final Sink s27 = new Sink(Q.twentySeven);

	public static Sink getBoundaryCondition(Q i) {
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

	private Sink(Q i) {
		super(i);
	}

	@Override
	public void collide(double[] f, double rho, double[] u, double uNorm2, double omega) {
		// TODO Auto-generated method stub

	}

	@Override
	public double rho(double[] f) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] u(double[] f, double[] u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double computeEquilibrium(int i, double rho, double[] u, double uNorm2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
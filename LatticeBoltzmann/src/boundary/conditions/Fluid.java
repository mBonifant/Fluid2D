package boundary.conditions;

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
	private final Q q;
	private final double[] fPrime;
	private double[] uTemp = new double[3];
	private static final Fluid f9 = new Fluid(Q.nine);
	private static final Fluid f15 = new Fluid(Q.fifteen);
	private static final Fluid f19 = new Fluid(Q.nineteen);
	private static final Fluid f27 = new Fluid(Q.twentySeven);

	public static Fluid getBoundaryCondition(Q i) {
		switch (i) {
		case nine:
			return f9;
		case fifteen:
			return f15;
		case nineteen:
			return f19;
		case twentySeven:
			return f27;
		}
		System.err.println("Unreachable code");
		System.exit(-1);
		return null;
	}

	protected Fluid(Q i) {
		q = i;
		fPrime = new double[q.size];
	}

	double omega = 1.0f;

	@Override
	public void collide(double[] f, double rho, double[] u, double speedSq, double omega) {
		for (int i = 0; i < q.size; i++)
			fPrime[i] = (1 - omega) * f[i] + omega * computeEquilibrium(i, rho, u, speedSq);
		System.arraycopy(fPrime, 0, f, 0, q.size);
	}

	@Override
	public double rho(double[] f) {
		double rho = 0;
		for (double ff : f) {
			// System.out.println("force: " + ff);
			rho += ff;
		}
		// System.out.println("Rho: " + rho);
		// System.exit(-1);
		return rho;
	}

	@Override
	public double[] u(double[] f, double[]u) {
		return uFast(f, rho(f), u);
	}

	public double[] uFast(double[] f, double rho, double[] u) {
		u[0] = 0;
		u[1] = 0;
		u[2] = 0;
		for (int i = 0; i < q.size; i++) {
			Velocity.multE(uTemp, q.velocities[i], f[i]);
			Velocity.add(u, uTemp);
		}

		Velocity.multE(u, u, 1.0f / rho);
		return u;
	}

	// speed, dx/dt, usually assumed as dx=dt, c=1;
	private final double c = 1.0f;

	@Override
	public double computeEquilibrium(int i, double rho, double[] u, double uDotu) {
		double f = 0.0f;
		double uDotE = Velocity.dot(u,q.velocities[i]);
		// BKG assumption of f^eq_i
		if (c == 1.0f) {
			f = 1.0f;
			f += (3f * uDotE);
			f += (4.5f * (uDotE * uDotE));
			f -= (1.5f * uDotu);
		} else {
			f = 1.0f;
			f += (3f * uDotE / c);
			f += (4.5f * ((uDotE * uDotE) / (c * c)));
			f -= (1.5f * (uDotu / (c * c)));
		}
		return rho * q.weights[i] * f;
	}
}
package lattice;

import java.awt.Color;

import boundary.conditions.BoundaryCondition;

/**
 * All Lattices are made of cells
 * 
 * @author bonifantmc
 *
 */
public class Cell {
	/** Marks how fluids should behave when striking this cell */
	public final BoundaryCondition bc;
	/** x,y,z velocity components */
	double[] u = new double[3];
	public double rho = 0.0f;
	/**
	 * squared speed, used in plotting speed, since sqrting it is more
	 * computation than we need
	 */
	public double speedSq = 0.0f;
	/** inverse tau, viscosity dependent */
	private double omega = 0.0f;
	/** force vector */
	private double[] f;

	/**
	 * Make a cell with the given boundary condition
	 * 
	 * @param condition
	 *            the boundary condition for the given cell
	 * 
	 * @param omega
	 *            the time decay tau inverse, related to fluid viscosity (tau
	 *            relates to how soon the force will reach equilibrium)
	 * 
	 * @param i
	 *            the number of microscopic velocities the cell will track.
	 */
	public Cell(BoundaryCondition condition, double omega, int i) {
		this.omega = omega;
		this.bc = condition;
		f = new double[i];
	}

	/**
	 * 
	 * @return the microscopic velocities stored in this cell
	 */
	public double[] getF() {
		return this.f;
	}

	/**
	 * @return the mass density
	 */
	public double getRho() {
		return rho;
	}

	/**
	 * Use the BoundaryCondition collision operator
	 */
	public void collide() {
		rho = bc.rho(f);
		u = bc.u(f, u);
		speedSq = Velocity.speedSq(u);
		bc.collide(f, rho, u, speedSq, omega);
	}

	public void setF(double g, int i) {
		this.f[i] = g;
	}

	public Color getColor() {
		return Color.getHSBColor((float) f[1]* 1024, (float) f[1], 0.9f);
	}
}
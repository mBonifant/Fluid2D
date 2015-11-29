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
	public BoundaryCondition bc;
	/** x,y velocity components */
	private double[] u = new double[2];
	/***/
	private double rho = 0.0f;
	/**
	 * squared speed, used in plotting speed, since sqrting it is more
	 * computation than we need
	 */
	private double speedSq = 0.0f;
	/** force vector */
	private double[] f;

	/** the column index of the cell in the lattice */
	public final int col;
	/** the row index of the cell in the lattice */
	public final int row;

	/** Curl of flow at given cell */
	public double curl;

	/**
	 * Make a cell with the given boundary condition
	 * 
	 * @param condition
	 *            the boundary condition for the given cell
	 * 
	 * @param i
	 *            the ith index the cell is at
	 * 
	 * @param j
	 *            the jth index the cell is at
	 * 
	 */
	public Cell(BoundaryCondition condition, int i, int j) {
		this.bc = condition;
		this.f = new double[condition.size()];
		this.col = i;
		this.row = j;
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
		return this.rho;
	}

	/**
	 * Use the BoundaryCondition collision operator
	 */
	public void collide() {
		this.bc.computeCollision(this);
	}

	/**
	 * Reset the ith microdensity packet to a new value
	 * 
	 * @param packet
	 *            the amount of mass to set f[i] to
	 * @param i
	 *            the index of the microdensity packet to reset
	 */
	public void setF(double packet, int i) {
		this.f[i] = packet;
	}

	/** List of possible stats to read from the cell and display */
	public static enum ColorStats {
		/** the speed in speedSq at this cell */
		speed, /** macroscopic density at this cell */
		rho, /** speed in the x direction */
		xSpeed, /** speed in the y direction */
		ySpeed, /** the curl of the flow around this a cell */
		curl, /** micro density 0 */
		f0, /** micro density 1 */
		f1, /** micro density 2 */
		f2, /** micro density 3 */
		f3, /** micro density 4 */
		f4, /** micro density 5 */
		f5, /** micro density 6 */
		f6, /** micro density 7 */
		f7, /** micro density 8 */
		f8;
	}

	/**
	 * 
	 * @param cs
	 *            which Stat to base the retrieved Color on
	 * @return the given value normalized by the tanh function
	 */
	public Color getColor(ColorStats cs, float factor) {
		double f = 0;
		switch (cs) {
		case rho:
			f = this.rho;
			break;
		case speed:
			f = this.speedSq;
			break;
		case xSpeed:
			f = this.u[0];
			break;
		case ySpeed:
			f = this.getU()[1];
			break;
		case f0:
			f = this.getF()[0];
			break;
		case f1:
			f = this.getF()[1];
			break;
		case f2:
			f = this.getF()[2];
			break;
		case f3:
			f = this.getF()[3];
			break;
		case f4:
			f = this.getF()[4];
			break;
		case f5:
			f = this.getF()[5];
			break;
		case f6:
			f = this.getF()[6];
			break;
		case f7:
			f = this.getF()[7];
			break;
		case f8:
			f = this.getF()[8];
			break;
		case curl:
			f = this.curl;
			break;
		default:
			break;
		}

		return Color.getHSBColor((float) f * factor, 0.7f, 0.9f);
	}

	/**
	 * Set the macroscopic density
	 * 
	 * @param r
	 *            the new macroscopic density
	 */
	public void setRho(double r) {
		if (r < 0)
			r = 0;
		else if (Double.isNaN(0)) {
			r = 0;
		}
		this.rho = r;
	}

	public String toString() {
		return this.bc + " rho=" + this.rho + "u={" + getU()[0] + ", "
				+ getU()[1] + "}";

	}

	/**
	 * @return the velocity vector
	 */
	public double[] getU() {
		return this.u;
	}

	/** @return the speed squared/velocity norm */
	public double getSpeedSq() {
		return this.speedSq;
	}

	/**
	 * Set the velocity
	 * 
	 * @param d0
	 *            the x velocity/speed
	 * @param d1
	 *            the y velocity/speed
	 */
	public void setU(double d0, double d1) {
		if (Math.abs(d0) < 0.0001 || Double.isNaN(d0) || Double.isInfinite(d0))
			d0 = 0.0;
		if (Math.abs(d1) < 0.0001 || Double.isNaN(d1) || Double.isInfinite(d1))
			d1 = 0.0;
		this.u[0] = d0;
		this.u[1] = d1;
		this.speedSq = this.u[0] * this.u[0] + this.u[1] * this.u[1];
	}

	/** @return the y velocity/speed */
	public double getY() {
		return this.u[1];
	}

	/** @return the x velocity/speed */
	public double getX() {
		return this.u[0];
	}
}
package boundary.conditions;

/*  Lattice Boltzmann sample, written in Java
 *
 *  Main author: Jean-Luc Falcone
 *  Co-author: Jonas Latt
 *  Copyright (C) 2006 University of Geneva
 *  Address: Jean-Luc Falcone, Rue General Dufour 24,
 *           1211 Geneva 4, Switzerland 
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public 
 *  License along with this program; if not, write to the Free 
 *  Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *  Boston, MA  02110-1301, USA.
 */
/**
 * 
 * Each Cell in the Lattice has a different Boundary Condition that results in a
 * fancy pants collision.
 * 
 * @author bonifantmc
 *
 */
public interface BoundaryCondition {
	/**
	 * @param f
	 *            the data from a given Cell @return macroscopic value, rho,
	 *            mass distribution
	 */
	public double rho(double[] f);

	/**
	 * @param f
	 *            the data from a given Cell macroscopic value, u, velocity
	 *            distribution
	 */
	public double[] u(double[] f, double[] u);

	/** implement collision */
	public void collide(double[] f, double rho, double[] u, double uNorm2, double omega);

	/** compute the ith force equilibrium */
	double computeEquilibrium(int i, double rho, double[] u, double uNorm2);

}

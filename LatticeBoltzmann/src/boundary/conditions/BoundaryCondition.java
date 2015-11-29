package boundary.conditions;

import lattice.Cell;

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
	 * @param c
	 *            the cell to compute rho the macroscopic density for
	 * 
	 */
	public void computeRho(Cell c);

	/**
	 * @param c
	 *            the cell to compute u, the macroscopic velocity for
	 */
	public void computeU(Cell c);

	/**
	 * implement collision
	 * 
	 * @param c
	 *            the cell to compute collision for
	 */
	public void computeCollision(Cell c);

	/**
	 * compute the ith force equilibrium
	 * 
	 * @param c
	 *            the cell to compute Equilibrium for
	 * @param i
	 *            the ith microscopic velocity to compute Equilibrium for
	 * @return the equilibrium microvelocity in the ith direction
	 * 
	 */
	public double computeIthEquilibrium(Cell c, int i);

	/** @return the Boundary Condition's Q size */
	public int size();
}

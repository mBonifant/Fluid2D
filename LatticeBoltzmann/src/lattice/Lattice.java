package lattice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Timer;

import boundary.conditions.BoundaryCondition;
import boundary.conditions.Fluid;
import boundary.conditions.Sink;
import boundary.conditions.Source;
import boundary.conditions.Wall;
import gui.RefreshListener;

/***
 * A Lattice is a 3 Dimensional Array defining a collection of Cells through
 * which fluid propagates as an approximation of Fluid Dynamics. Imagine each
 * cell has a little packet of fluid in it, the lattice tracks how much fluid
 * moves into each cell and at what rate.
 */
public class Lattice {

	/**
	 * Lattice containing cells of fluid.
	 */
	public Cell[][][] lattice;
	/** The number of microscopic velocities each cell will track */
	public final Q cellDim;
	/** The length of the lattice */
	public final int length;
	/** The width of the lattice */
	public final int width;
	/** The depth of the lattice */
	public final int depth;
	public final ActionListener ac = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			doStep();
		}
	};
	public Timer tm = new Timer(250, ac);

	/** the 'thickness' of the fluid running on the lattice */
	final double viscosity;

	/**
	 * constant related to time and decay towards equilibrium stipulated by the
	 * fluid's viscosity
	 */
	final double omega;

	private Vector<RefreshListener> listeners = new Vector<>();
	private Cell[][][] tmp;

	/**
	 * Create a three dimensional Lattice of c-dimensional Cells
	 * 
	 * @param w
	 *            the width of the lattice
	 * @param l
	 *            the length of the lattice
	 * @param d
	 *            the depth of the lattice (set to 1 for 2D Lattice)
	 * @param c
	 *            the number of microscopic velocities each cell tracks
	 * @param v
	 *            viscosity of the fluid on the lattice
	 * 
	 * @param density
	 *            the density of the fluid
	 */
	public Lattice(int w, int l, int d, Q c, double v, double density) {
		this.length = l + 2;
		this.width = w + 2;
		this.depth = d;
		this.cellDim = c;
		this.viscosity = v;
		this.lattice = new Cell[this.width][this.length][this.depth];
		this.tmp = new Cell[this.width][this.length][this.depth];
		this.omega = 1f / (3f * v + 0.5f);
		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.length; j++)
				for (int k = 0; k < this.depth; k++)
					// all edges of the lattice are walls (unless the lattice is
					// only 2D, then ignore the floor/ceiling
					if (i == 0 || i == this.width - 1 || j == 0 || j == this.length - 1
							|| (k == this.depth - 1 && depth != 1)) {
						System.out.println("depth");
						this.lattice[i][j][k] = new Cell(Wall.getBoundaryCondition(cellDim), omega, cellDim.size);
						this.tmp[i][j][k] = new Cell(Wall.getBoundaryCondition(cellDim), omega, cellDim.size);

					} else { // inside the lattice fluid flows
						this.lattice[i][j][k] = new Cell(Fluid.getBoundaryCondition(cellDim), omega, cellDim.size);
						this.tmp[i][j][k] = new Cell(Fluid.getBoundaryCondition(cellDim), omega, cellDim.size);
					}

		this.initVelocity(1.0f);
	}

	/**
	 * Add the given boundary condition to all points inside the given shape.
	 * 
	 * @param b
	 *            the condition to set
	 * @param rs
	 *            the shape to apply the condition to.
	 */
	private void addBoundaryCondition(BoundaryCondition b, RectangularShape rs) {
		// for all points in the shape
		for (int i = (int) rs.getMinX(); i < rs.getMaxX(); i++)
			for (int j = (int) rs.getMinY(); j < rs.getMaxY(); j++) {
				// if the point is also in the lattice, set the boundary
				// condition to the given condition
				if (i > 0 && i < width && j > 0 && j < length && rs.contains(new Point2D.Double(i, j)))
					for (int z = 0; z < depth; z++)
						lattice[i][j][z] = new Cell(b, omega, cellDim.size);

			}
	}

	/**
	 * Mark all cells inside the given shape as Walls, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularWall(RectangularShape rs) {
		addBoundaryCondition(Wall.getBoundaryCondition(cellDim), rs);
	}

	/**
	 * Mark all cells inside the given shape as sinks, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularSink(RectangularShape rs) {
		addBoundaryCondition(Sink.getBoundaryCondition(cellDim), rs);

	}

	/**
	 * Mark all cells inside the given shape as sources, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularSource(RectangularShape rs) {
		addBoundaryCondition(Source.getBoundaryCondition(cellDim), rs);
	}

	/**
	 * Mark all cells inside the given shape as fluid, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularFluid(RectangularShape rs) {
		addBoundaryCondition(Fluid.getBoundaryCondition(cellDim), rs);

	}

	public void addRefreshListener(RefreshListener rl) {
		this.listeners.addElement(rl);
	}

	public void removeRefreshListener(RefreshListener rl) {
		while (this.listeners.contains(rl))
			this.listeners.remove(rl);
	}

	private void collide() {
		for (Cell[][] c : lattice)
			for (Cell[] cc : c)
				for (Cell ccc : cc)
					ccc.collide();
	}

	private void stream() {
		int prevX, prevY, pervZ;
		for (int x = 1; x < width - 1; x++) {
			for (int y = 1; y < length - 1; y++) {
				for (int z = 0; z < depth; z++)
					for (int i = 0; i < cellDim.size; i++) {
						prevX = (int) (x - cellDim.velocities[i][0]);
						prevY = (int) (y - cellDim.velocities[i][1]);
						pervZ = (int) (z - cellDim.velocities[i][2]);
						tmp[x][y][z].setF(lattice[prevX][prevY][pervZ].getF()[i], i);
					}
			}
		}
		Cell[][][] swapLattice = lattice;
		lattice = tmp;
		tmp = swapLattice;
	}

	private void bounce() {
		// TODO boundaries other than Q9
		// North & South Boundary
		for (int z = 0; z < depth; z++) {
			for (int x = 1; x < width; x++) {
				lattice[x][0][z].setF(lattice[x][length - 2][z].getF()[6], 6);
				lattice[x][0][z].setF(lattice[x][length - 2][z].getF()[2], 2);
				lattice[x][0][z].setF(lattice[x][length - 2][z].getF()[5], 5);
				lattice[x][length - 1][z].setF(lattice[x][1][z].getF()[7], 7);
				lattice[x][length - 1][z].setF(lattice[x][1][z].getF()[4], 4);
				lattice[x][length - 1][z].setF(lattice[x][1][z].getF()[8], 8);
			}
			// East & West Boundary
			for (int y = 1; y < length; y++) {
				lattice[0][y][z].setF(lattice[width - 2][y][z].getF()[5], 5);
				lattice[0][y][z].setF(lattice[width - 2][y][z].getF()[8], 8);
				lattice[0][y][z].setF(lattice[width - 2][y][z].getF()[1], 1);
				lattice[width - 1][y][z].setF(lattice[1][y][z].getF()[7], 7);
				lattice[width - 1][y][z].setF(lattice[1][y][z].getF()[3], 3);
				lattice[width - 1][y][z].setF(lattice[1][y][z].getF()[6], 6);
			}
			// Corners
			lattice[width - 1][0][z].setF(lattice[1][length - 2][z].getF()[6], 6);
			lattice[0][length - 1][z].setF(lattice[width - 2][1][z].getF()[8], 8);
			lattice[width - 1][length - 1][z].setF(lattice[1][1][z].getF()[7], 7);
			lattice[0][0][z].setF(lattice[width - 2][length - 2][z].getF()[5], 5);
		}
	}

	public void doStep() {
		System.out.println("stepping");
		// bounce();
		stream();
		collide();
		Iterator<RefreshListener> itr = this.listeners.iterator();
		while (itr.hasNext())
			itr.next().onRefresh();
	}

	double U_MAX = 0.01f;

	public void initVelocity(double density) {
		double divX = (width - 2) * (width - 2);
		double divY = (length - 2) * (length - 2);

		for (int x = 1; x < width - 1; x++)
			for (int y = 1; y < length - 1; y++) {

				double[] u = { 4 * U_MAX / divX * (x - 2) * (width - x), 0f/*4 * U_MAX / divY * (y - 2) * (length - y)*/, 0f };
				double speedSQ = u[0] * u[0] + u[1] * u[1];
				for (int i = 0; i < cellDim.size; i++) {
					double fEq = lattice[x][y][0].bc.computeEquilibrium(i, density, u, speedSQ);
					for (int z = 0; z < depth; z++) {
						lattice[x][y][z].setF(fEq, i);
						System.arraycopy(lattice[x][y][z].u, 0, u, 0, 3);
						lattice[x][y][z].rho = density;
						lattice[x][y][z].speedSq = speedSQ;
					}
				}
			}
	}
}

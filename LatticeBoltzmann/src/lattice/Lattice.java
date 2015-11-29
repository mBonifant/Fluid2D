package lattice;

import gui.Boundary;
import gui.RefreshListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RectangularShape;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Timer;

import Fluids.Liquid;
import Fluids.Water;
import boundary.conditions.BoundaryCondition;
import boundary.conditions.Fluid;
import boundary.conditions.Sink;
import boundary.conditions.Source;
import boundary.conditions.Wall;

/***
 * A Lattice is a 3 Dimensional Array defining a collection of Cells through
 * which fluid propagates as an approximation of Fluid Dynamics. Imagine each
 * cell has a little packet of fluid in it, the lattice tracks how much fluid
 * moves into each cell and at what rate.
 */
public class Lattice {

	// main lattice structure

	/** Lattice containing cells of fluid. */
	public Cell[][] lattice;

	/** a temporary copy of the lattice */
	private Cell[][] tmp;

	/** The number of microscopic velocities each cell will track */
	public final Q cellDim;

	/** The length of the lattice */
	public final int length;
	/** The width of the lattice */
	public final int width;

	// boundary conditions

	/** Boundary Condition for a fluid Source */
	public final Source src;
	/** Boundary Condition for a fluid Sink */
	public final Sink sink;
	/** Boundary Condition for a solid wall */
	public final Wall wall;
	/** Boundary Condition for a fluid */
	public final Fluid fluid;

	/** default velocity of in flowing fluid */
	private double[] defU;
	/** speedSq of the default velocity */
	private double defUNorm;

	/**
	 * listeners checking if they should refresh their display of the lattice
	 */
	private Vector<RefreshListener> listeners = new Vector<>();

	/** An action listener that tells the Lattice to update on step */
	private final ActionListener ac = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			doStep();
		}
	};
	/** A time that tells the Lattice how often to update */
	public Timer tm = new Timer(10000, this.ac);

	public Liquid l;

	/**
	 * Create a three dimensional Lattice of c-dimensional Cells
	 * 
	 * @param w
	 *            the width of the lattice
	 * @param l
	 *            the length of the lattice
	 * 
	 * @param c
	 *            the number of microscopic velocities each cell tracks
	 * @param v
	 *            viscosity of the fluid on the lattice
	 * @param u
	 *            incoming velocity
	 * 
	 * @param density
	 *            the density of the fluid
	 * @param list
	 *            a list of boundaries to add to the lattice
	 */
	public Lattice(int w, int l, Q c, Liquid liq, double[] u,
			List<Boundary> list) {
		this.length = l;
		this.width = w;
		this.cellDim = c;
		this.l = liq;
		this.lattice = new Cell[this.width][this.length];
		this.tmp = new Cell[this.width][this.length];
		this.defU = u;
		this.defUNorm = u[0] * u[0] + u[1] * u[1];
		System.out.println("defU:" + u[0] + "," + u[1] + " norm:"
				+ this.defUNorm);
		this.wall = new Wall(this.cellDim, this.l);
		this.sink = new Sink(this.cellDim, this.l);
		this.src = new Source(this.cellDim, this.defU, this.l);
		this.fluid = new Fluid(this.cellDim, this.l);

		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.length; j++) {
				this.lattice[i][j] = new Cell(this.fluid, i, j);
				this.tmp[i][j] = new Cell(this.fluid, i, j);
				continue;
			}
		this.initVelocity(this.lattice);
		this.initVelocity(this.tmp);

		this.addBoundaryCondition(this.wall, new Rectangle2D.Double(0, 0,
				this.width, 1));
		this.addBoundaryCondition(this.wall, new Rectangle2D.Double(0,
				this.length - 1, this.width, 1));
		for (Boundary b : list)
			this.addBoundaryCondition(b);

	}

	private void addBoundaryCondition(Boundary b) {
		switch (b.rectangle) {
		case SINK_ELLI:
		case SINK_RECT:
			this.addBoundaryCondition(this.sink, b.shape);
			return;
		case SOURCE_ELLI:
		case SOURCE_RECT:
			this.addBoundaryCondition(this.src, b.shape);
			return;
		case WALL_ELLI:
		case WALL_RECT:
			this.addBoundaryCondition(this.wall, b.shape);
			return;
		case FLUID_ELLI:
		case FLUID_RECT: {
			this.addBoundaryCondition(this.fluid, b.shape);
			System.out.println("adding fluid bound");
		}
			return;
		}
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
				if (i >= 0 && i < this.width && j >= 0 && j < this.length
						&& rs.contains(new Point2D.Float(i, j))) {
					this.lattice[i][j].bc = b;
					this.tmp[i][j].bc = b;
				}
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
		addBoundaryCondition(this.wall, rs);
	}

	/**
	 * Mark all cells inside the given shape as sinks, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularSink(RectangularShape rs) {
		addBoundaryCondition(this.sink, rs);

	}

	/**
	 * Mark all cells inside the given shape as sources, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularSource(RectangularShape rs) {
		addBoundaryCondition(this.src, rs);
	}

	/**
	 * Mark all cells inside the given shape as fluid, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularFluid(RectangularShape rs) {
		addBoundaryCondition(this.fluid, rs);

	}

	/**
	 * @param rl
	 *            the RefreshListener to add to the lattice's list of listeners
	 */
	public void addRefreshListener(RefreshListener rl) {
		this.listeners.addElement(rl);
	}

	/**
	 * @param rl
	 *            the RefreshListener to remove from the lattice's list of
	 *            listeners
	 */
	public void removeRefreshListener(RefreshListener rl) {
		while (this.listeners.contains(rl))
			this.listeners.remove(rl);
	}

	/** Calculate the collision step of LBM */
	private void collide() {
		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.length; j++)
				this.lattice[i][j].collide();
	}

	/** Calculate the streaming step of LBM */
	private void stream() {
		int prevX, prevY;
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.length; y++) {
				for (int i = 0; i < this.cellDim.size; i++) {
					prevX = (int) (x - this.cellDim.velocities[i][0]);
					prevY = (int) (y - this.cellDim.velocities[i][1]);
					if (prevX < 0 || prevY < 0 || prevX >= this.width
							|| prevY >= this.length)
						continue;
					this.tmp[x][y]
							.setF(this.lattice[prevX][prevY].getF()[i], i);
				}
			}
		}

		// edit bounds of chamber
		double v = Math.sqrt(this.defUNorm);
		double east = 1f / 9f * (1 + 3 * v + 3 * this.defUNorm), northOrSouthEast = 1f / 36f * (1 + 3 * v + 3 * this.defUNorm);
		double west = 1f / 9f * (1 - 3 * v + 3 * this.defUNorm), northOrSouthWest = 1f / 36f * (1 - 3 * v + 3 * this.defUNorm);

		for (int y = 0; y < this.length; y++) {

			if (this.lattice[0][y].bc == this.fluid) {
				this.lattice[0][y].setF(east, 3);
				this.lattice[0][y].setF(northOrSouthEast, 7);// northeast
				this.lattice[0][y].setF(northOrSouthEast, 6);// southeast

			}
		}
		// Try the same thing at the right edge and see if it works:
		for (int y = 0; y < this.length; y++) {

			if (this.lattice[this.width - 1][y].bc == this.fluid) {
				this.lattice[this.width - 1][y].setF(west, 4);
				this.lattice[this.width - 1][y].setF(northOrSouthWest, 5);// northWest
				this.lattice[this.width - 1][y].setF(northOrSouthWest, 8);// southWest

			}
		}

		Cell[][] swapLattice = this.lattice;
		this.lattice = this.tmp;
		this.tmp = swapLattice;
	}

	/** Process one steam/collision and tell listeners to update */
	public void doStep() {

		collide();
		stream();
		computeCurl();

		Iterator<RefreshListener> itr = this.listeners.iterator();
		while (itr.hasNext())
			itr.next().onRefresh();
	}

	/**
	 * Initalizes the velocity of all cells in the lattice
	 * 
	 * @param l
	 *            the Lattice to init (must init lattice and temp lattice
	 */
	public void initVelocity(Cell[][] l) {
		System.out.println("U=" + this.defU[0] + "," + this.defU[1]);
		for (int x = 0; x < this.width; x++)
			for (int y = 0; y < this.length; y++) {
				// this.initVelocityColumnEast(y, lattice);
				for (int i = 0; i < this.cellDim.size; i++) {
					l[x][y].setU(this.defU[0], this.defU[1]);
					l[x][y].setRho(this.l.getDensity());
					double fEq = l[x][y].bc.computeIthEquilibrium(l[x][y], i);
					l[x][y].setF(fEq, i);

				}
			}
	}

	/**
	 * Compute the curl of the entire lattice
	 * 
	 */
	public void computeCurl() {
		for (int x = 1; x < this.width - 1; x++) {
			for (int y = 1; y < this.length - 1; y++) {
				this.tmp[x][y].curl = (this.lattice[x + 1][y].getY() - this.lattice[x - 1][y]
						.getY())
						- (this.lattice[x][y + 1].getX() - this.lattice[x][y - 1]
								.getX());
			}
		}

		for (int y = 1; y < this.length - 1; y++) {
			this.tmp[0][y].curl = 2
					* (this.lattice[1][y].getY() - this.lattice[0][y].getY())
					- (this.lattice[0][y + 1].getX() - this.lattice[0][y - 1]
							.getX());

			this.tmp[this.width - 1][y].curl = 2
					* (this.lattice[this.width - 1][y].getY() - this.lattice[this.width - 2][y]
							.getY())
					- (this.lattice[this.width - 1][y + 1].getX() - this.lattice[this.width - 1][y - 1]
							.getX());
		}

	}

	public void setLiquid(Liquid l) {
		this.l = l;
		this.wall.setL(l);
		this.fluid.setL(l);
		this.src.setL(l);
		this.sink.setL(l);

	}
}

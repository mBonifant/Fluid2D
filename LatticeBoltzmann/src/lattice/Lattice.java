package lattice;

import gui.Boundary;
import gui.RefreshListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Timer;

import Fluids.Liquid;

/***
 * A Lattice is a 3 Dimensional Array defining a collection of Cells through
 * which fluid propagates as an approximation of Fluid Dynamics. Imagine each
 * cell has a little packet of fluid in it, the lattice tracks how much fluid
 * moves into each cell and at what rate.
 */
public class Lattice {

	/** 4/9 */
	float four9ths = 4.0f / 9.0f;
	/** 1/9 */
	float one9th = 1.0f / 9.0f;
	/** 1/36 */
	float one36th = 1.0f / 36.0f;

	// main lattice structure
	/** width of the chamber */
	public int xdim = 200;
	/** length of the chamber */
	public int ydim = 80;
	/** micro density of fluid not moving */
	float[][] n0;
	/** micro density flowing north */
	float[][] nN;
	/** micro density flowing south */
	float[][] nS;
	/** micro density flowing east */
	float[][] nE;
	/** micro density flowing west */
	float[][] nW;
	/** micro density flowing north west */
	float[][] nNW;
	/** micro density flowing north east */
	float[][] nNE;
	/** micro density flowing south west */
	float[][] nSW;
	/** micro density flowing south east */
	float[][] nSE;

	// Other arrays calculated from the above:
	/** macro density */
	public float[][] density;
	/** x velocity */
	public float[][] xvel;
	/** y velocity */
	public float[][] yvel;
	/** speed squared */
	float[][] speed2;;
	/** curl of the fluid */
	float[][] curl;

	// boundaries
	/** walls for bounce back */
	boolean[][] barrier;
	/** in flowing fluid */
	boolean[][] source;
	/** out flowing fluid */
	boolean[][] sink;

	/** default velocity of in flowing fluid */
	private float[] defU;
	/** speedSq of the default velocity */
	private float defUNorm;

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

	/** the liquid propagating along the lattice */
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
	 * @param liq
	 *            the liquid propagating along the lattice (provides its
	 *            desisty, temperature, viscosity, and omega)
	 * @param u
	 *            incoming velocity
	 * 
	 * @param list
	 *            a list of boundaries to add to the lattice
	 * @param empty
	 *            true if the lattice is being filled, otherwise false
	 */
	public Lattice(int w, int l, Liquid liq, float[] u, List<Boundary> list, boolean empty) {
		this.ydim = l;
		this.xdim = w;
		this.l = liq;
		this.n0 = new float[this.xdim][this.ydim];
		this.nN = new float[this.xdim][this.ydim];
		this.nS = new float[this.xdim][this.ydim];
		this.nE = new float[this.xdim][this.ydim];
		this.nW = new float[this.xdim][this.ydim];
		this.nNW = new float[this.xdim][this.ydim];
		this.nNE = new float[this.xdim][this.ydim];
		this.nSW = new float[this.xdim][this.ydim];
		this.nSE = new float[this.xdim][this.ydim];

		// Other arrays calculated from the above:
		this.density = new float[this.xdim][this.ydim]; // total density
		this.xvel = new float[this.xdim][this.ydim]; // macroscopic x velocity
		this.yvel = new float[this.xdim][this.ydim]; // macroscopic y velocity
		this.speed2 = new float[this.xdim][this.ydim];// macroscopic speed
														// squared
		this.curl = new float[this.xdim][this.ydim];

		// Boolean array, true at sites that contain barriers:
		this.barrier = new boolean[this.xdim][this.ydim];
		this.sink = new boolean[this.xdim][this.ydim];
		this.source = new boolean[this.xdim][this.ydim];
		this.defU = u;
		this.defUNorm = u[0] * u[0] + u[1] * u[1];

		this.initVelocity(empty);

		for (Boundary b : list)
			this.addBoundaryCondition(b);

	}

	/**
	 * Add the given boundary to the system
	 * 
	 * @param b
	 *            the given boundary
	 */
	private void addBoundaryCondition(Boundary b) {
		switch (b.rectangle) {
		case SINK_ELLI:
		case SINK_RECT:
			this.addBoundaryCondition(this.sink, b.shape);
			return;
		case SOURCE_ELLI:
		case SOURCE_RECT:
			this.addBoundaryCondition(this.source, b.shape);
			return;
		case WALL_ELLI:
		case WALL_RECT:
			this.addBoundaryCondition(this.barrier, b.shape);
			return;
		case FLUID_ELLI:
		case FLUID_RECT: {
			this.addBoundaryCondition(null, b.shape);
			return;

		}
		}
	}

	/**
	 * Add the given boundary condition to all points inside the given shape.
	 * 
	 * @param shape
	 *            the condition being set (or null if removing conditions )
	 * @param rs
	 *            the shape to apply the condition to.
	 */
	private void addBoundaryCondition(boolean[][] shape, RectangularShape rs) {
		// for all points in the shape
		for (int i = (int) rs.getMinX(); i < rs.getMaxX(); i++)
			for (int j = (int) rs.getMinY(); j < rs.getMaxY(); j++) {
				// if the point is also in the lattice, set the boundary
				// condition to the given condition
				if (i >= 0 && i < this.xdim && j >= 0 && j < this.ydim && rs.contains(new Point2D.Float(i, j))) {
					if (shape == this.barrier) {
						this.barrier[i][j] = true;
						this.sink[i][j] = false;
						this.source[i][j] = false;

					} else if (shape == this.sink) {
						this.barrier[i][j] = false;
						this.sink[i][j] = true;
						this.source[i][j] = false;
					} else if (shape == this.source) {
						this.barrier[i][j] = false;
						this.sink[i][j] = false;
						this.source[i][j] = true;
					} else {
						this.barrier[i][j] = false;
						this.sink[i][j] = false;
						this.source[i][j] = false;
					}
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
		addBoundaryCondition(this.barrier, rs);
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
		addBoundaryCondition(this.source, rs);
	}

	/**
	 * Mark all cells inside the given shape as fluid, all shapes are extruded
	 * along the depth dimension making columns
	 * 
	 * @param rs
	 *            the rectangular shape to add
	 */
	public void addRectangularFluid(RectangularShape rs) {
		addBoundaryCondition(null, rs);

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

	/** bounce fluid back at walls */
	void bounce() {
		for (int x = 0; x < this.xdim; x++) {
			for (int y = 0; y < this.ydim; y++) {
				if (this.barrier[x][y]) {
					if (this.nN[x][y] > 0 && y - 1 >= 0) {
						this.nS[x][y - 1] = this.nN[x][y];
						this.nN[x][y] = 0;
					}
					if (this.nS[x][y] > 0 && y + 1 < this.ydim) {
						this.nN[x][y + 1]= this.nS[x][y];
						this.nS[x][y] = 0;
					}
					if (this.nE[x][y] > 0 && x - 1 >= 0) {
						this.nW[x - 1][y] = this.nE[x][y];
						this.nE[x][y] = 0;
					}
					if (this.nW[x][y] > 0 && x + 1 < this.xdim) {
						this.nE[x + 1][y] = this.nW[x][y];
						this.nW[x][y] = 0;
					}
					if (this.nNW[x][y] > 0 && x + 1 < this.xdim && y - 1 >= 0) {
						this.nSE[x + 1][y - 1] = this.nNW[x][y];
						this.nNW[x][y] = 0;
					}
					if (this.nNE[x][y] > 0 && x - 1 >= 0 && y - 1 >= 0) {
						this.nSW[x - 1][y - 1] = this.nNE[x][y];
						this.nNE[x][y] = 0;
					}
					if (this.nSW[x][y] > 0 && x + 1 < this.xdim && y + 1 < this.ydim) {
						this.nNE[x + 1][y + 1] = this.nSW[x][y];
						this.nSW[x][y] = 0;
					}
					if (this.nSE[x][y] > 0 && x - 1 >= 0 && y + 1 < this.ydim) {
						this.nNW[x - 1][y + 1] = this.nSE[x][y];
						this.nSE[x][y] = 0;
					}
				}
			}
		}
	}

	/** stream fluid from one lattice cell to the next */
	private void stream() {
		for (int x = 0; x < this.xdim - 1; x++) { // first start in NW corner...
			for (int y = this.ydim - 1; y > 0; y--) {
				if (!(barrier[x][y] && barrier[x][y - 1]))
					this.nN[x][y] = this.nN[x][y - 1]; // move the north-moving
														// particles
				if (!(barrier[x][y] && barrier[x + 1][y - 1]))

					this.nNW[x][y] = this.nNW[x + 1][y - 1]; // and the
																// northwest-moving
				// particles
			}
		}
		for (int x = this.xdim - 1; x > 0; x--) { // now start in NE corner...
			for (int y = this.ydim - 1; y > 0; y--) {
				if (!(barrier[x][y] && barrier[x - 1][y]))

					this.nE[x][y] = this.nE[x - 1][y]; // move the east-moving
				if (!(barrier[x][y] && barrier[x - 1][y - 1]))
					// particles
					this.nNE[x][y] = this.nNE[x - 1][y - 1]; // and the
																// northeast-moving
				// particles
			}
		}
		for (int x = this.xdim - 1; x > 0; x--) { // now start in SE corner...
			for (int y = 0; y < this.ydim - 1; y++) {
				if (!(barrier[x][y] && barrier[x][y + 1]))

					this.nS[x][y] = this.nS[x][y + 1]; // move the south-moving
														// particles
				if (!(barrier[x][y] && barrier[x - 1][y + 1]))

					this.nSE[x][y] = this.nSE[x - 1][y + 1]; // and the
																// southeast-moving
				// particles
			}
		}
		for (int x = 0; x < this.xdim - 1; x++) { // now start in the SW
													// corner...
			for (int y = 0; y < this.ydim - 1; y++) {
				if (!(barrier[x][y] && barrier[x + 1][y]))

					this.nW[x][y] = this.nW[x + 1][y]; // move the west-moving
														// particles
				if (!(barrier[x][y] && barrier[x + 1][y + 1]))

					this.nSW[x][y] = this.nSW[x + 1][y + 1]; // and the
																// southwest-moving
				// particles
			}
		}
		float v = (float) Math.sqrt(getUNorm());

		// We missed a few at the left and right edges:
		for (int y = 0; y < this.ydim - 1; y++) {
			this.nS[0][y] = this.nS[0][y + 1];
		}
		for (int y = this.ydim - 1; y > 0; y--) {
			this.nN[this.xdim - 1][y] = this.nN[this.xdim - 1][y - 1];
		}
		// Now handle left boundary as in Pullan's example code:
		// Stream particles in from the non-existent space to the left, with the
		// user-determined speed:

		for (int y = 0; y < this.ydim; y++)
			if (!this.barrier[0][y]) {
				this.nE[0][y] = this.one9th * (1 + 3 * v + 3 * v * v);
				this.nNE[0][y] = this.one36th * (1 + 3 * v + 3 * v * v);
				this.nSE[0][y] = this.one36th * (1 + 3 * v + 3 * v * v);
			}

		// Try the same thing at the right edge and see if it works:

		for (int y = 0; y < this.ydim; y++)
			if (!this.barrier[this.xdim - 1][y]) {
				this.nW[this.xdim - 1][y] = this.one9th * (1 - 3 * v + 3 * v * v);
				this.nNW[this.xdim - 1][y] = this.one36th * (1 - 3 * v + 3 * v * v);
				this.nSW[this.xdim - 1][y] = this.one36th * (1 - 3 * v + 3 * v * v);
			}

		// Now handle top and bottom edges:
		for (int x = 0; x < this.xdim; x++) {
			this.n0[x][0] = this.four9ths * (1 - 1.5f * v * v);
			this.nE[x][0] = this.one9th * (1 + 3 * v + 3 * v * v);
			this.nW[x][0] = this.one9th * (1 - 3 * v + 3 * v * v);
			this.nN[x][0] = this.one9th * (1 - 1.5f * v * v);
			this.nS[x][0] = this.one9th * (1 - 1.5f * v * v);
			this.nNE[x][0] = this.one36th * (1 + 3 * v + 3 * v * v);
			this.nSE[x][0] = this.one36th * (1 + 3 * v + 3 * v * v);
			this.nNW[x][0] = this.one36th * (1 - 3 * v + 3 * v * v);
			this.nSW[x][0] = this.one36th * (1 - 3 * v + 3 * v * v);
			this.n0[x][this.ydim - 1] = this.four9ths * (1 - 1.5f * v * v);
			this.nE[x][this.ydim - 1] = this.one9th * (1 + 3 * v + 3 * v * v);
			this.nW[x][this.ydim - 1] = this.one9th * (1 - 3 * v + 3 * v * v);
			this.nN[x][this.ydim - 1] = this.one9th * (1 - 1.5f * v * v);
			this.nS[x][this.ydim - 1] = this.one9th * (1 - 1.5f * v * v);
			this.nNE[x][this.ydim - 1] = this.one36th * (1 + 3 * v + 3 * v * v);
			this.nSE[x][this.ydim - 1] = this.one36th * (1 + 3 * v + 3 * v * v);
			this.nNW[x][this.ydim - 1] = this.one36th * (1 - 3 * v + 3 * v * v);
			this.nSW[x][this.ydim - 1] = this.one36th * (1 - 3 * v + 3 * v * v);
		}

		for (int x = 0; x < this.xdim; x++)
			for (int y = 0; y < ydim; y++) {

				this.n0[x][y] *= n0[x][y] <= 0 ? 0 : 1;
				this.nE[x][y] *= nE[x][y] <= 0 ? 0 : 1;
				this.nW[x][y] *= nW[x][y] <= 0 ? 0 : 1;
				this.nS[x][y] *= nS[x][y] <= 0 ? 0 : 1;
				this.nN[x][y] *= nN[x][y] <= 0 ? 0 : 1;
				this.nNE[x][y] *= nNE[x][y] <= 0 ? 0 : 1;
				this.nNW[x][y] *= nNW[x][y] <= 0 ? 0 : 1;
				this.nSE[x][y] *= nSE[x][y] <= 0 ? 0 : 1;
				this.nSW[x][y] *= nSW[x][y] <= 0 ? 0 : 1;

			}
	}

	/** compute collision of fluids */
	private void collide() {
		float n, one9thn, one36thn, vx, vy, vx2, vy2, vx3, vy3, vxvy2, v2, v215;
		float omega = (float) this.l.getOmega();
		float sinsor = 0.02f;
		for (int x = 0; x < this.xdim; x++) {
			for (int y = 0; y < this.ydim; y++) {
				if (!this.barrier[x][y]) {

					// RHO
					this.n0[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * four9ths;
					this.nN[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one9th;
					this.nS[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one9th;
					this.nE[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one9th;
					this.nW[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one9th;
					this.nNW[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one36th;
					this.nNE[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one36th;
					this.nSW[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one36th;
					this.nSE[x][y] += ((this.sink[x][y] ? -sinsor : 0) + (this.source[x][y] ? sinsor : 0)) * one36th;

					
					n = this.n0[x][y] + this.nN[x][y] + this.nS[x][y] + this.nE[x][y] + this.nW[x][y] + this.nNW[x][y]
							+ this.nNE[x][y] + this.nSW[x][y] + this.nSE[x][y];
					if (n < 0 || Float.isInfinite(n)||Float.isNaN(n)|| n < 0.0000000000f)
						n = 0;
					this.density[x][y] = n; // macroscopic density may be needed
											// for
					// plotting

					// U
					one9thn = this.one9th * n;
					one36thn = this.one36th * n;
					if (n > 0) {
						vx = (this.nE[x][y] + this.nNE[x][y] + this.nSE[x][y] - this.nW[x][y] - this.nNW[x][y]
								- this.nSW[x][y]) / n;
					} else
						vx = 0;
					this.xvel[x][y] = vx; // may be needed for plotting
					if (n > 0) {
						vy = (this.nN[x][y] + this.nNE[x][y] + this.nNW[x][y] - this.nS[x][y] - this.nSE[x][y]
								- this.nSW[x][y]) / n;
					} else
						vy = 0;
					this.yvel[x][y] = vy; // may be needed for plotting

					// F
					vx3 = 3 * vx;
					vy3 = 3 * vy;
					vx2 = vx * vx;
					vy2 = vy * vy;
					vxvy2 = 2 * vx * vy;
					v2 = vx2 + vy2;
					this.speed2[x][y] = v2; // may be needed for plotting
					v215 = 1.5f * v2;
					this.n0[x][y] += omega * (this.four9ths * n * (1 - v215) - this.n0[x][y]);
					this.nE[x][y] += omega * (one9thn * (1 + vx3 + 4.5 * vx2 - v215) - this.nE[x][y]);
					this.nW[x][y] += omega * (one9thn * (1 - vx3 + 4.5 * vx2 - v215) - this.nW[x][y]);
					this.nN[x][y] += omega * (one9thn * (1 + vy3 + 4.5 * vy2 - v215) - this.nN[x][y]);
					this.nS[x][y] += omega * (one9thn * (1 - vy3 + 4.5 * vy2 - v215) - this.nS[x][y]);
					this.nNE[x][y] += omega * (one36thn * (1 + vx3 + vy3 + 4.5 * (v2 + vxvy2) - v215) - this.nNE[x][y]);
					this.nNW[x][y] += omega * (one36thn * (1 - vx3 + vy3 + 4.5 * (v2 - vxvy2) - v215) - this.nNW[x][y]);
					this.nSE[x][y] += omega * (one36thn * (1 + vx3 - vy3 + 4.5 * (v2 - vxvy2) - v215) - this.nSE[x][y]);
					this.nSW[x][y] += omega * (one36thn * (1 - vx3 - vy3 + 4.5 * (v2 + vxvy2) - v215) - this.nSW[x][y]);
				}
			}
		}
	}

	/** Process one steam/collision and tell listeners to update */
	public void doStep() {

		collide();
		stream();
		bounce();
		computeCurl();

		Iterator<RefreshListener> itr = this.listeners.iterator();
		while (itr.hasNext())
			itr.next().onRefresh();
	}

	/**
	 * Initializes the velocity of all cells in the lattice
	 * 
	 * @param empty
	 *            true if everything should be set to 0 for an empty lattice
	 */
	private synchronized void initVelocity(boolean empty) {
		float v = (float) Math.sqrt(this.defUNorm);
		for (int x = 0; x < this.xdim; x++) {
			for (int y = 0; y < this.ydim; y++) {
				if (this.barrier[x][y] || empty) {
					this.n0[x][y] = 0;
					this.nE[x][y] = 0;
					this.nW[x][y] = 0;
					this.nN[x][y] = 0;
					this.nS[x][y] = 0;
					this.nNE[x][y] = 0;
					this.nNW[x][y] = 0;
					this.nSE[x][y] = 0;
					this.nSW[x][y] = 0;
					this.xvel[x][y] = 0;
					this.yvel[x][y] = 0;
					this.speed2[x][y] = 0;
				} else {
					this.n0[x][y] = four9ths* (1 - 1.5f * v * v);
					this.nE[x][y] = one9th * (1 + 3 * v + 3 * v * v);
					this.nW[x][y] = one9th * (1 - 3 * v + 3 * v * v);
					this.nN[x][y] = one9th * (1 - 1.5f * v * v);
					this.nS[x][y] = one9th * (1 - 1.5f * v * v);
					this.nNE[x][y] = one36th * (1 + 3 * v + 3 * v * v);
					this.nSE[x][y] = one36th * (1 + 3 * v + 3 * v * v);
					this.nNW[x][y] = one36th* (1 - 3 * v + 3 * v * v);
					this.nSW[x][y] = one36th * (1 - 3 * v + 3 * v * v);
					this.density[x][y] = this.l.getDensity();
					this.xvel[x][y] = this.defU[0];
					this.yvel[x][y] = this.defU[1];
					this.speed2[x][y] = this.defUNorm;
				}
			}
		}
	}

	/**
	 * Compute the curl of the entire lattice
	 * 
	 */
	public void computeCurl() {
		for (int x = 1; x < this.xdim - 1; x++) {
			for (int y = 1; y < this.ydim - 1; y++) {
				this.curl[x][y] = (this.yvel[x + 1][y] - this.yvel[x - 1][y])
						- (this.xvel[x][y + 1] - this.xvel[x][y - 1]);
			}
		}
		for (int y = 1; y < this.ydim - 1; y++) {
			this.curl[0][y] = 2 * (this.yvel[1][y] - this.yvel[0][y]) - (this.xvel[0][y + 1] - this.xvel[0][y - 1]);
			this.curl[this.xdim - 1][y] = 2 * (this.yvel[this.xdim - 1][y] - this.yvel[this.xdim - 2][y])
					- (this.xvel[this.xdim - 1][y + 1] - this.xvel[this.xdim - 1][y - 1]);
		}
	}

	/**
	 * @param l
	 *            the new liquid to switch the lattice to
	 */
	public void setLiquid(Liquid l) {
		this.l = l;
	}

	/**
	 * Change the in-flow velocity
	 * 
	 * @param xV
	 *            the x in flow
	 * @param yV
	 *            the y in flow
	 */
	public void setFlow(float xV, float yV) {
		this.defU = new float[] { xV, yV };
		this.defUNorm = this.defU[0] * this.defU[0] + this.defU[1] * this.defU[1];
	}

	/**
	 * @return the default in flow
	 */
	public float[] getU() {
		return this.defU;
	}

	/** the speed squared of the default in flow */
	public float getUNorm() {
		return this.defUNorm;
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
	public Color getColor(ColorStats cs, float factor, int i, int j) {
		if (this.barrier[i][j])
			return Color.BLACK;
		float f = 0;
		switch (cs) {
		case rho:
			f = this.density[i][j];
			break;
		case speed:
			f = this.speed2[i][j];
			break;
		case xSpeed:
			f = this.xvel[i][j];
			break;
		case ySpeed:
			f = this.yvel[i][j];
			break;
		case f0:
			f = this.n0[i][j];
			break;
		case f1:
			f = this.nN[i][j];
			break;
		case f2:
			f = this.nS[i][j];
			break;
		case f3:
			f = this.nE[i][j];
			break;
		case f4:
			f = this.nW[i][j];
			break;
		case f5:
			f = this.nNW[i][j];
			break;
		case f6:
			f = this.nNE[i][j];
			break;
		case f7:
			f = this.nSW[i][j];
			break;
		case f8:
			f = this.nSE[i][j];
			break;
		case curl:
			f = this.curl[i][j];
			break;
		default:
			break;
		}

		return Color.getHSBColor((float) f * factor, 0.7f, 0.9f);
	}
	
	

}

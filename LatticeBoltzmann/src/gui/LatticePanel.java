package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import lattice.Cell;
import lattice.Lattice;
import boundary.conditions.Sink;
import boundary.conditions.Source;
import boundary.conditions.Wall;

/**
 * @author bonifantmc
 *
 */
@SuppressWarnings("serial")
public class LatticePanel extends JPanel implements RefreshListener {
	/**
	 * Lattice to render
	 */
	int density = 1;
	private final Lattice l;
	private BufferedImage bf;
	private Graphics bfg;
	public float factor = 1;
	private Cell.ColorStats color;

	/**
	 * 
	 * @param l
	 *            Lattice thie panel draws
	 * @param dens
	 *            number of pixels squared per cell
	 * @param factor
	 *            factor to apply to the getHSV to intensify differences in
	 *            macro/microscopic values of a cell
	 * @param c
	 *            which macro/microscopic value to view
	 */
	public LatticePanel(Lattice l, int dens, float factor, Cell.ColorStats c) {
		this.color = c;
		this.factor = factor;
		this.l = l;
		this.density = dens;
		this.setPreferredSize(new Dimension(l.width * this.density, l.length
				* this.density));
		this.setMinimumSize(getPreferredSize());
		this.setMaximumSize(getPreferredSize());
		this.setSize(getPreferredSize());
		this.bf = new BufferedImage(l.width * this.density, l.length
				* this.density, BufferedImage.TYPE_INT_RGB);
		this.bfg = this.bf.getGraphics();
		l.addRefreshListener(this);
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				Point p = arg0.getPoint();
				setToolTipText(l.lattice[p.x / LatticePanel.this.density][p.y
						/ LatticePanel.this.density].toString());

			}
		});
	}

	/**
	 * 
	 * @param g
	 *            graphic to paint the lattice one
	 */
	@Override
	public void paintComponent(Graphics g) {
		Cell c;
		super.paintComponent(g);
		g.clearRect(0, 0, this.l.width * this.density, this.l.length
				* this.density);
		this.bfg.clearRect(0, 0, this.l.width * this.density, this.l.length
				* this.density);

		for (int i = 0; i < this.l.width; i++)
			for (int j = 0; j < this.l.length; j++) {
				c = this.l.lattice[i][j];
				this.bfg.setColor(c.getColor(this.color, factor));
				this.bfg.fillRect(i * this.density, j * this.density,
						this.density, this.density);
			}

		// Boundary.paintBoundarys((Graphics2D) this.bfg, this.bounds, 0, 0);
		g.drawImage(this.bf, 0, 0, null);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void onRefresh() {
		repaint();

	}

	/**
	 * Set the Coloring of the Lattice Panel (if the given object is a coloring
	 * state)
	 * 
	 * @param color
	 *            the coloring state to switch to
	 */
	public void setColor(Object color) {
		if (color instanceof Cell.ColorStats) {
			this.color = (Cell.ColorStats) color;
			System.out.println("new color: " + color);
		}
	}
}

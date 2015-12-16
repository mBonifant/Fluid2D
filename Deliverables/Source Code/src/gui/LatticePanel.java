package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import lattice.Lattice;

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
	private Lattice.ColorStats color;
	static private DecimalFormat df = new DecimalFormat("#.###");

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
	public LatticePanel(Lattice l, int dens, float factor, Lattice.ColorStats c) {
		this.color = c;
		this.factor = factor;
		this.l = l;
		this.density = dens;
		this.setPreferredSize(new Dimension(l.xdim * this.density, l.ydim * this.density));
		this.setMinimumSize(getPreferredSize());
		this.setMaximumSize(getPreferredSize());
		this.setSize(getPreferredSize());
		this.bf = new BufferedImage(l.xdim * this.density, l.ydim * this.density, BufferedImage.TYPE_INT_RGB);
		this.bfg = this.bf.getGraphics();
		l.addRefreshListener(this);
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int x = arg0.getX(), y = arg0.getY();
				if (x >= 0 && y >= 0 && x < l.xdim && y < l.ydim) {
					setToolTipText("Rho = "
							+ df.format(l.density[x / LatticePanel.this.density][y / LatticePanel.this.density])
							+ "u = (" + df.format(l.xvel[x / LatticePanel.this.density][y / LatticePanel.this.density])
							+ ", " + df.format(l.yvel[x / LatticePanel.this.density][y / LatticePanel.this.density])
							+ ")");
				}
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				l.addRectangularWall(new Rectangle2D.Float(arg0.getPoint().x, arg0.getPoint().y, 5, 5));
				repaint();
			}
		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				l.addRectangularWall(new Rectangle2D.Float(arg0.getPoint().x, arg0.getPoint().y, 5, 5));
				repaint();
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
		super.paintComponent(g);
		g.clearRect(0, 0, this.getL().xdim * this.density, this.getL().ydim * this.density);
		this.bfg.clearRect(0, 0, this.getL().xdim * this.density, this.getL().ydim * this.density);

		for (int i = 0; i < this.getL().xdim; i++)
			for (int j = 0; j < this.getL().ydim; j++) {
				this.bfg.setColor(this.getL().getColor(this.color, this.factor, i, j));
				this.bfg.fillRect(i * this.density, j * this.density, this.density, this.density);
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
		if (color instanceof Lattice.ColorStats) {
			this.color = (Lattice.ColorStats) color;
			System.out.println("new color: " + color);
		}
	}

	public Lattice getL() {
		return l;
	}
}

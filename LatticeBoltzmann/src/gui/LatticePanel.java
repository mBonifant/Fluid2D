package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import boundary.conditions.Sink;
import boundary.conditions.Source;
import boundary.conditions.Wall;
import lattice.Cell;
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
	static int density = 1;
	private final Lattice l;
	private BufferedImage bf;
	private Graphics bfg;
	private List<Boundary> bounds;

	/**
	 * 
	 * @param l
	 *            Lattice thie panel draws
	 */
	public LatticePanel(Lattice l, List<Boundary> list) {
		this.l = l;
		this.bounds = list;
		this.setPreferredSize(new Dimension(l.width * density, l.length * density));
		this.setMinimumSize(getPreferredSize());
		this.setMaximumSize(getPreferredSize());
		this.setSize(getPreferredSize());
		bf = new BufferedImage(l.width * density, l.length * density, BufferedImage.TYPE_INT_RGB);
		bfg = bf.getGraphics();
		l.addRefreshListener(this);
	}

	Cell c;

	/**
	 * 
	 * @param g
	 *            graphic to paint the lattice one
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("refreshing paint");
		g.clearRect(0, 0, l.width * density, l.length * density);
		bfg.clearRect(0, 0, l.width * density, l.length * density);
		for (int i = 1; i < l.width - 1; i++)
			for (int j = 1; j < l.length - 1; j++) {
				c = l.lattice[i][j][0];
				bfg.setColor(c.bc instanceof Wall ? Feature.WALL_ELLI.getColor()
						: c.bc instanceof Source ? Feature.SOURCE_ELLI.getColor()
								: c.bc instanceof Sink ? Feature.SINK_ELLI.getColor() : c.getColor());
				bfg.fillRect(i * density, j * density, density, density);
			}

		Boundary.paintBoundarys((Graphics2D) bfg, bounds, 0, 0);
		g.drawImage(bf, 0, 0, null);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void onRefresh() {
		repaint();

	}

}

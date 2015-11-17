package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Opens a specified image to its fullest size possible according to screen
 * dimensions in a separate JFrame from the ILB.
 * <p>
 * EditImage windows cannot be resized.
 * <p>
 * EditImage uses the {@link DrawingTool} as a MouseListener and KeyListener for
 * editing image Annotations.
 * 
 * @author bonifantmc
 * 
 */
@SuppressWarnings("serial")
public class EditLattice extends JPanel {
	ArrayList<Boundary> boundaries = new ArrayList<>();

	/** Tool used to draw annotations on image */
	private final DrawingTool dT;

	int xi = 0, yi = 0;

	EditLattice(int i, int j) {
		this.setPreferredSize(new Dimension(i, j));
		this.setSize(this.getPreferredSize());
		this.setMinimumSize(getPreferredSize());
		this.setMaximumSize(getPreferredSize());
		this.dT = new DrawingTool(this);
		this.addMouseListener(dT);
		this.addMouseMotionListener(dT);
		this.addKeyListener(dT);
	}

	/**
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (boundaries.size() != 0) {
			System.out.println(boundaries);
			Boundary.paintBoundarys((Graphics2D) g, boundaries, xi, yi);
		}
	}

	public ArrayList<Boundary> getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(ArrayList<Boundary> list) {
		this.boundaries.clear();
		if (list != null)
			this.boundaries.addAll(list);
	}
}

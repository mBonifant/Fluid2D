package tests;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import gui.Boundary;
import gui.LatticePanel;
import lattice.Lattice;
import lattice.Q;

/**
 * Test the Lattice structures by adding shapes and ensuring they are added
 * properly.
 * 
 * @author bonifantmc
 *
 */
public class LatticeTests {

	/**
	 * Test the lattice
	 */
	public static void main(String[] args) {

		Rectangle2D.Double rs1 = new Rectangle2D.Double(0, 0, 40, 40);
		Rectangle2D.Double rs2 = new Rectangle2D.Double(323, 123, 34, 65);
		Rectangle2D.Double rs3 = new Rectangle2D.Double(432, 497, 45, 78);
		Ellipse2D.Double es1 = new Ellipse2D.Double(25, 25, 25, 25);
		Ellipse2D.Double es2 = new Ellipse2D.Double(423, 350, 25, 25);
		Ellipse2D.Double es3 = new Ellipse2D.Double(200, 300, 30, 25);

		Lattice l = new Lattice(500, 500, 1, Q.nine, 1.0f, 1.0f);

		l.addRectangularWall(rs1);
		l.addRectangularWall(rs2);
		l.addRectangularWall(rs3);

		// System.out.println(l);
		l.addRectangularWall(es1);
		l.addRectangularWall(es2);
		l.addRectangularWall(es3);

		// l.addRectangularSource(new Ellipse2D.Double(150, 150, 100, 100));
		// System.out.println(l);

		JFrame f = new JFrame();
		LatticePanel p = new LatticePanel(l, new ArrayList<Boundary>());
		f.add(p);
		f.pack();
		f.setVisible(true);
		// Timer t = new Timer(1000, new ActionListener() {
		// public void actionPerformed(ActionEvent evt) {
		// p.repaint();
		// }
		// });
		// t.start();
		for (int i = 0; i < 100; i++) {
			l.doStep();
		}
		System.exit(1);
	}
}

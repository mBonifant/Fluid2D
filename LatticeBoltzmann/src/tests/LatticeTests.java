package tests;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import Fluids.Water;
import gui.Boundary;
import gui.Feature;
import gui.LatticePanel;
import lattice.Lattice;

/**
 * Test the Lattice structures by adding shapes and ensuring they are added
 * properly.
 * 
 * @author bonifantmc
 *
 */
public class LatticeTests {

	/**
	 * @param args
	 *            not important not used, ignored
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Rectangle2D.Float rs1 = new Rectangle2D.Float(0, 0, 40, 40);
		Rectangle2D.Float rs2 = new Rectangle2D.Float(323, 123, 34, 65);
		Rectangle2D.Float rs3 = new Rectangle2D.Float(432, 497, 45, 78);
		Ellipse2D.Float es1 = new Ellipse2D.Float(25, 25, 25, 25);
		Ellipse2D.Float es2 = new Ellipse2D.Float(423, 350, 25, 25);
		Ellipse2D.Float es3 = new Ellipse2D.Float(200, 300, 30, 25);
		Boundary b4 = new Boundary(Feature.WALL_RECT, 2, 2, 1, 1);

		Lattice l = new Lattice(5, 5, new Water(0),
				new float[] { 1, 0 }, new ArrayList<>(), false);

		// l.addRectangularWall(rs1);
		// l.addRectangularWall(rs2);
		// l.addRectangularWall(rs3);
		//
		// // System.out.println(l);
		// l.addRectangularWall(es1);
		// l.addRectangularWall(es2);
		// l.addRectangularWall(es3);
		// l.addRectangularWall(b4.shape);
		// l.addRectangularSource(new Ellipse2D.Float(150, 150, 100, 100));
		// System.out.println(l);
		ArrayList<Boundary> arr = new ArrayList<>();

		// arr.add(b4);
		JFrame f = new JFrame();
		LatticePanel p = new LatticePanel(l, 50, 1024, Lattice.ColorStats.speed);
		f.add(p);
		f.pack();
		f.setVisible(true);
		// Timer t = new Timer(1000, new ActionListener() {
		// public void actionPerformed(ActionEvent evt) {
		// p.repaint();
		// }
		// });
		// t.start();
		l.tm.setDelay(250);
		l.tm.setInitialDelay(0);
		l.tm.start();

		// wait for input to stop
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lattice.Lattice;
import lattice.Q;

/**
 * Opens the application and defines the main window.
 * 
 * @author bonifantmc
 * 
 */
@SuppressWarnings("serial")
public class LBMGui extends JFrame {
	/** panel the user sets up the simulation chamber with */
	private EditLattice drawingLattice;
	/** panel the user runs the simulation in */
	private LatticePanel runningLattice;
	/** the Lattice that LBM runs on */
	private Lattice l;
	boolean prepping;
	/**
	 * Name of resource file containing basic information about the application
	 * for start up
	 */
	private final String config = "config.properties";

	/**
	 * Prepare the main display
	 */
	public LBMGui() {
		super();
		JPanel realcontent = new JPanel();
		realcontent.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setContentPane(realcontent);
		JPanel control = new JPanel();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;

		realcontent.add(control, c);
		setTitle("LIQUID: 2D FLUID DYMANICS SIMULATOR");
		// Basic window design
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO SAVE START UP PROPERTIES
			}
		});
		// TODO get defualt display settings
		// Configurations.getDisplayProperties(this, getConfig());

		this.prepping = true;
		this.drawingLattice = new EditLattice(500, 500);
		c.gridx = 0;
		c.gridy = 0;
		realcontent.add(this.drawingLattice, c);

		JButton run = new JButton("run");
		run.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JButton src = (JButton) e.getComponent();
				if (src.getText().equals("run")) {
					src.setText("pause");
					prepping = false;
					realcontent.remove(drawingLattice);
					c.gridx = 0;
					c.gridy = 0;
					List<Boundary> list = drawingLattice.boundaries;

					l = new Lattice(500, 500, 1, Q.nine, 0.5f, 0.5f);
					for (Boundary b : list)
						switch (b.rectangle) {
						case SINK_ELLI:
						case SINK_RECT:
							l.addRectangularSink(b.shape);
							break;
						case SOURCE_ELLI:
						case SOURCE_RECT:
							l.addRectangularSource(b.shape);
							break;
						case WALL_ELLI:
						case WALL_RECT:
							l.addRectangularWall(b.shape);
							break;
						}

					runningLattice = new LatticePanel(l, list);
					l.tm.start();
					realcontent.add(runningLattice, c);
					pack();
				} else {
					l.tm.stop();
					src.setText("run");
					c.gridx = 0;
					c.gridy = 0;
					realcontent.remove(runningLattice);
					c.gridx = 0;
					c.gridy = 0;
					realcontent.add(drawingLattice, c);
					prepping = true;
					pack();
				}
			}
		});

		control.add(run);

		this.pack();
	}
}
package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Fluids.Liquid;
import Fluids.Water;
import lattice.Cell;
import lattice.Cell.ColorStats;
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
	int XX = 250;
	int YY = 250;

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

	private Liquid medium = new Water(0);

	private float factor = 1f;

	private Cell.ColorStats state = Cell.ColorStats.rho;

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
		c.gridx = 0;
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
		this.drawingLattice = new EditLattice(this.XX, this.YY);
		c.gridx = 0;
		c.gridy = 1;
		realcontent.add(this.drawingLattice, c);

		JButton run = new JButton("run");
		run.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JButton src = (JButton) e.getComponent();
				if (src.getText().equals("run")) {
					src.setText("pause");
					LBMGui.this.prepping = false;
					realcontent.remove(LBMGui.this.drawingLattice);
					c.gridx = 0;
					c.gridy = 1;
					List<Boundary> list = LBMGui.this.drawingLattice.boundaries;

					LBMGui.this.l = new Lattice(LBMGui.this.XX, LBMGui.this.YY,
							Q.nine, LBMGui.this.medium,
							new double[] { 0.1f, 0 }, list);

					LBMGui.this.runningLattice = new LatticePanel(
							LBMGui.this.l, 1, factor, state);
					realcontent.add(LBMGui.this.runningLattice, c);
					LBMGui.this.l.tm.setDelay(10);
					LBMGui.this.l.tm.setInitialDelay(0);
					LBMGui.this.l.tm.start();

					pack();
				} else {
					LBMGui.this.l.tm.stop();
					src.setText("run");
					c.gridx = 0;
					c.gridy = 1;
					realcontent.remove(LBMGui.this.runningLattice);
					c.gridx = 0;
					c.gridy = 1;
					realcontent.add(LBMGui.this.drawingLattice, c);
					LBMGui.this.prepping = true;
					pack();
				}
			}
		});

		control.add(run);
		JComboBox<Cell.ColorStats> displays = new JComboBox<>(
				Cell.ColorStats.values());
		displays.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cb = (JComboBox) arg0.getSource();
				LBMGui.this.state = (ColorStats) cb.getSelectedItem();
				if (LBMGui.this.runningLattice != null)
					LBMGui.this.runningLattice.setColor(cb.getSelectedItem());
			}
		});

		control.add(displays);
		ArrayList<Float> floats = new ArrayList<>();
		floats.add(1f);
		floats.add(2f);
		floats.add(4f);
		floats.add(8f);
		floats.add(16f);
		floats.add(32f);
		floats.add(64f);
		floats.add(128f);
		floats.add(256f);
		floats.add(512f);
		floats.add(1024f);
		floats.add(2048f);
		floats.add(4096f);
		Float[] arr = new Float[floats.size()];
		JComboBox<Float> factors = new JComboBox<Float>(floats.toArray(arr));
		factors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cb = (JComboBox) arg0.getSource();
				// System.out.println(cb.getSelectedItem());
				LBMGui.this.factor = (Float) cb.getSelectedItem();

				if (LBMGui.this.runningLattice != null)
					LBMGui.this.runningLattice.factor = (Float) cb
							.getSelectedItem();
			}
		});

		control.add(factors);

		JSlider temperature = new JSlider();
		temperature.setMaximum(100);
		temperature.setMinimum(0);
		temperature.setMajorTickSpacing(10);
		temperature.setMinorTickSpacing(10);
		temperature.setSnapToTicks(true);
		temperature.setPaintLabels(true);
		temperature.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				if (!source.getValueIsAdjusting()) {
					int temp = (int) source.getValue();
					LBMGui.this.medium.setTemperature(temp);
				}
			}
		});

		control.add(temperature);
		this.pack();
	}
}
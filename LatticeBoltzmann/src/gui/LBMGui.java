package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lattice.Lattice;
import Fluids.Liquid;
import Fluids.Water;

/**
 * Opens the application and defines the main window.
 * 
 * @author bonifantmc
 * 
 */
@SuppressWarnings("serial")
public class LBMGui extends JFrame {
	int XX = 500;
	int YY = 500;

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

	private double xV = 0.05f;
	private double yV = 0f;

	private Lattice.ColorStats state = Lattice.ColorStats.rho;

	private boolean empty = false;

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

		JButton run = new JButton("start");
		JButton pausePlay = new JButton("pause");
		JFormattedTextField xVelocitySetter = new JFormattedTextField(
				new DecimalFormat("#.##"));
		JFormattedTextField yVelocitySetter = new JFormattedTextField(
				new DecimalFormat("#.##"));
		xVelocitySetter.setValue(this.xV);
		yVelocitySetter.setValue(this.yV);

		control.add(run);
		control.add(pausePlay);

		run.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JButton src = (JButton) e.getComponent();
				if (src.getText().equals("start")) {
					src.setText("stop");
					pausePlay.setEnabled(true);
					LBMGui.this.prepping = false;
					realcontent.remove(LBMGui.this.drawingLattice);
					c.gridx = 0;
					c.gridy = 1;
					List<Boundary> list = LBMGui.this.drawingLattice.boundaries;

					LBMGui.this.l = new Lattice(LBMGui.this.XX, LBMGui.this.YY,
							 LBMGui.this.medium, new double[] {
									LBMGui.this.xV, LBMGui.this.yV }, list,
							LBMGui.this.empty);

					LBMGui.this.runningLattice = new LatticePanel(
							LBMGui.this.l, 1, LBMGui.this.factor,
							LBMGui.this.state);
					realcontent.add(LBMGui.this.runningLattice, c);
					LBMGui.this.l.tm.setDelay(5);
					LBMGui.this.l.tm.setInitialDelay(0);
					LBMGui.this.l.tm.start();

					pack();
				} else {
					pausePlay.setEnabled(false);
					pausePlay.setText("Pause");

					LBMGui.this.l.tm.stop();
					src.setText("start");
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

		pausePlay.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (pausePlay.getText().equals("Pause")) {
					LBMGui.this.l.tm.stop();
					pausePlay.setText("Play");
				} else {
					LBMGui.this.l.tm.start();
					pausePlay.setText("Pause");
				}

			}
		});

		JComboBox<Lattice.ColorStats> displays = new JComboBox<>(
				Lattice.ColorStats.values());
		displays.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cb = (JComboBox) arg0.getSource();
				LBMGui.this.state = (lattice.Lattice.ColorStats) cb.getSelectedItem();
				if (LBMGui.this.runningLattice != null) {
					LBMGui.this.runningLattice.setColor(cb.getSelectedItem());
					LBMGui.this.repaint();
				}
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
				LBMGui.this.repaint();
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
		control.add(xVelocitySetter);
		control.add(yVelocitySetter);
		xVelocitySetter.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getSource() == xVelocitySetter)
					if (xVelocitySetter.getValue() instanceof Long)
						LBMGui.this.xV = ((Long) xVelocitySetter.getValue())
								.doubleValue();
					else
						LBMGui.this.xV = (Double) xVelocitySetter.getValue();
				if (LBMGui.this.l != null)
					LBMGui.this.l.setFlow(LBMGui.this.xV, LBMGui.this.yV);

			}
		});
		yVelocitySetter.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getSource() == yVelocitySetter)
					if (yVelocitySetter.getValue() instanceof Long)
						LBMGui.this.yV = ((Long) yVelocitySetter.getValue())
								.doubleValue();
					else
						LBMGui.this.yV = (Double) yVelocitySetter.getValue();
				if (LBMGui.this.l != null)
					LBMGui.this.l.setFlow(LBMGui.this.xV, LBMGui.this.yV);
			}
		});
		xVelocitySetter.setColumns(5);
		yVelocitySetter.setColumns(5);

		JButton fillTheLattice = new JButton("Filled");
		fillTheLattice.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (fillTheLattice.getText().equals("Filled")) {
					fillTheLattice.setText("Filling");
					LBMGui.this.empty = true;
				} else {
					fillTheLattice.setText("Filled");
					LBMGui.this.empty = false;
				}
				pack();
			}

		});
		control.add(fillTheLattice);

		this.pack();
	}
}
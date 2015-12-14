package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lattice.Lattice;
import Fluids.Glycerin;
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

	private float xV = 0.05f;
	private float yV = 0f;

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
		JFormattedTextField xVelocitySetter = new JFormattedTextField(new DecimalFormat("#.##"));
		JFormattedTextField yVelocitySetter = new JFormattedTextField(new DecimalFormat("#.##"));
		xVelocitySetter.setValue(this.xV);
		yVelocitySetter.setValue(this.yV);

		run.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JButton src = (JButton) e.getComponent();
				if (src.getText().equals("start")) {
					src.setText("stop");
					pausePlay.setText("Pause");
					pausePlay.setEnabled(true);
					
					LBMGui.this.prepping = false;
					realcontent.remove(LBMGui.this.drawingLattice);
					c.gridx = 0;
					c.gridy = 1;
					c.anchor = GridBagConstraints.PAGE_START;


					LBMGui.this.l = new Lattice(LBMGui.this.XX, LBMGui.this.YY, LBMGui.this.medium,
							new float[] { LBMGui.this.xV, LBMGui.this.yV }, drawingLattice.boundaries, LBMGui.this.empty);

					LBMGui.this.runningLattice = new LatticePanel(LBMGui.this.l, 1, LBMGui.this.factor,
							LBMGui.this.state);
					realcontent.add(LBMGui.this.runningLattice, c);
					LBMGui.this.l.tm.setDelay(50);
					LBMGui.this.l.tm.setInitialDelay(0);
					LBMGui.this.l.tm.start();

					pack();
				} else {
					src.setText("start");
					pausePlay.setText("Pause");
					pausePlay.setEnabled(false);
					
					LBMGui.this.prepping = true;
					realcontent.remove(LBMGui.this.runningLattice);
					c.gridx = 0;
					c.gridy = 1;
					c.anchor = GridBagConstraints.PAGE_START;
					realcontent.add(LBMGui.this.drawingLattice, c);
					LBMGui.this.l.tm.stop();

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
				new Lattice.ColorStats[] { Lattice.ColorStats.rho, Lattice.ColorStats.speed, Lattice.ColorStats.curl,
						Lattice.ColorStats.xSpeed, Lattice.ColorStats.ySpeed });
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
					LBMGui.this.runningLattice.factor = (Float) cb.getSelectedItem();
				LBMGui.this.repaint();
			}
		});

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

		JComboBox<String> fluids = new JComboBox<String>(new String[] { "Water", "Glycerin" });
		fluids.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cb = (JComboBox) arg0.getSource();
				// System.out.println(cb.getSelectedItem());
				if (((String) cb.getSelectedItem()).equals("Water")) {
					if (medium.getTemperature() > 100) {
						temperature.setValue(100);
						medium.setTemperature(100);
					}
					medium = new Water(medium.getTemperature());
					temperature.setMaximum(100);
					temperature.setMinimum(0);
					temperature.setMinorTickSpacing(10);
					temperature.setMajorTickSpacing(10);
					temperature.setLabelTable(temperature.createStandardLabels(10));
				} else {
					if (medium.getTemperature() < 20) {
						temperature.setValue(20);
						medium.setTemperature(20);
					}
					medium = new Glycerin(medium.getTemperature());
					temperature.setMinimum(20);
					temperature.setMaximum(270);
					temperature.setMinorTickSpacing(10);
					temperature.setLabelTable(temperature.createStandardLabels(30));

					temperature.setMajorTickSpacing(25);
				}
				LBMGui.this.repaint();
			}
		});

		xVelocitySetter.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getSource() == xVelocitySetter)
					if (xVelocitySetter.getValue() instanceof Long)
						LBMGui.this.xV = ((Long) xVelocitySetter.getValue()).floatValue();
					else
						LBMGui.this.xV = Float.valueOf(String.valueOf(xVelocitySetter.getValue()));
				if (LBMGui.this.l != null)
					LBMGui.this.l.setFlow(LBMGui.this.xV, LBMGui.this.yV);

			}
		});
		yVelocitySetter.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getSource() == yVelocitySetter)
					if (yVelocitySetter.getValue() instanceof Long)
						LBMGui.this.yV = ((Long) yVelocitySetter.getValue()).floatValue();
					else
						LBMGui.this.yV = Float.valueOf(String.valueOf(yVelocitySetter.getValue()));
				if (LBMGui.this.l != null)
					LBMGui.this.l.setFlow(LBMGui.this.xV, LBMGui.this.yV);
			}
		});
		xVelocitySetter.setColumns(5);
		yVelocitySetter.setColumns(5);
		/*
		 * JButton fillTheLattice = new JButton("Filled");
		 * fillTheLattice.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseClicked(MouseEvent arg0) { if
		 * (fillTheLattice.getText().equals("Filled")) {
		 * fillTheLattice.setText("Filling"); LBMGui.this.empty = true; } else {
		 * fillTheLattice.setText("Filled"); LBMGui.this.empty = false; }
		 * pack(); }
		 * 
		 * }); control.add(fillTheLattice);
		 */

		control.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.ipady = 2;
		c.anchor = GridBagConstraints.CENTER;
		JLabel disp = new JLabel("Display Value");
		JLabel contrast = new JLabel("Contrast");
		JLabel flu = new JLabel("Fluids");
		JLabel temp = new JLabel("Temperature");
		JLabel xvel = new JLabel("X Vel");
		JLabel yvel = new JLabel("Y Vel");

		c.gridy = 0;
		c.gridx = 2;
		control.add(disp, c);
		c.gridx = 3;
		control.add(contrast, c);
		c.gridx = 4;
		control.add(flu, c);
		c.gridx = 5;
		control.add(temp, c);
		c.gridx = 6;
		control.add(xvel, c);
		c.gridx = 7;
		control.add(yvel, c);

		c.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;

		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridy = 1;

		c.gridx = 0;
		control.add(run, c);
		c.gridx = 1;
		control.add(pausePlay, c);
		c.gridx = 2;
		control.add(displays, c);
		c.gridx = 3;
		control.add(factors, c);
		c.gridx = 4;
		control.add(fluids, c);
		c.gridx = 5;
		control.add(temperature, c);
		c.gridx = 6;
		control.add(xVelocitySetter, c);
		c.gridx = 7;
		control.add(yVelocitySetter, c);
		this.pack();
		temperature.setValue(0);
		fluids.setSelectedIndex(0);
		factors.setSelectedIndex(0);
		displays.setSelectedIndex(0);

	}
}
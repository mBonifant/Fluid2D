package gui;

import java.awt.Color;

/**
 * 
 * @author bonifantmc
 * 
 */

class messages {
	final static String m1 = "Rectangular Sink", m2 = "Rectangular Source", m3 = "Rectangular Wall",
			m4 = "Rectangular Fluid", m5 = "Elliptical Sink", m6 = "Elliptical Source", m7 = "Elliptical Wall",
			m8 = "Elliptical Fluid";

}

public enum Feature {

	/** Rectangular Sink */
	SINK_RECT("ro", Color.BLUE, messages.m1), /** Rectangular Source */
	SOURCE_RECT("ri", Color.RED, messages.m2), /** Rectangular Wall */
	WALL_RECT("rw", Color.MAGENTA, messages.m3), /** Rectangular Fluid */
	FLUID_RECT("rf", Color.GREEN, messages.m4), /** Elliptical Sink */
	SINK_ELLI("eo", Color.BLUE, messages.m5), /** Elliptical Source */
	SOURCE_ELLI("ei", Color.RED, messages.m6), /** Elliptical Wall */
	WALL_ELLI("ew", Color.MAGENTA, messages.m7), /** Elliptical Fluid */
	FLUID_ELLI("ef", Color.GREEN, messages.m8);

	/**
	 * This is the character string that is printed when an annotation is
	 * written to a .lst file
	 */
	private String s;
	/** the string to display for buttons */
	public String msg;
	/** the color to draw the feature with */
	private Color c;

	/**
	 * @param sym
	 *            the character symbol that each Feature has.
	 * @param color
	 *            the color to draw the Feature as.
	 * @param msg
	 *            the message to display for buttons
	 */
	Feature(String sym, Color color, String msg) {
		this.s = sym;
		this.c = color;
		this.msg = msg;
	}

	/**
	 * @return the character string used in printing annotations
	 */
	public String getIdString() {
		return this.s;
	}

	/**
	 * @return The color that this feature should use for drawing.
	 */
	public Color getColor() {
		return this.c;
	}

	/**
	 * @return true if this Feature is rectangular
	 */
	public boolean isRectangle() {
		switch (this) {
		case SOURCE_RECT:
		case WALL_RECT:
		case SINK_RECT:
		case FLUID_RECT:
			return true;
		case SINK_ELLI:
		case SOURCE_ELLI:
		case WALL_ELLI:
		case FLUID_ELLI:
			return false;
		}
		return true;
	}

	/**
	 * Parse abbreviation into enum
	 */

	final static String m1 = "Rectangular Sink", m2 = "Rectangular Source", m3 = "Rectangular Wall",
			m4 = "Rectangular Fluid", m5 = "Elliptical Sink", m6 = "Elliptical Source", m7 = "Elliptical Wall",
			m8 = "Elliptical Fluid";

	public static Feature parseFeature(String string) {
		System.out.println(string);
		switch (string) {
		case "ro":
		case messages.m1:
			return SINK_RECT;
		case "ri":
		case messages.m2:
			return SOURCE_RECT;
		case "rw":
		case messages.m3:
			return WALL_RECT;
		case "rf":
		case messages.m4:
			return FLUID_RECT;

		case "eo":
		case messages.m5:
			return SINK_ELLI;
		case "ei":
		case messages.m6:
			return SOURCE_ELLI;
		case "ew":
		case messages.m7:
			return WALL_ELLI;
		case "ef":
		case messages.m8:
			return FLUID_ELLI;

		}
		return WALL_RECT;
	}

	public static String charValues() {
		return "reiowf";
	}

	public static Feature getConversion(Feature f, String i) {
		String r = f.getIdString();

		switch (i) {
		case "i":
			return Feature.parseFeature(r.substring(0, 1) + "i");
		case "o":
			return Feature.parseFeature(r.substring(0, 1) + "o");
		case "w":
			return Feature.parseFeature(r.substring(0, 1) + "w");
		case "f":
			return Feature.parseFeature(r.substring(0, 1) + "f");
		case "r":
			return Feature.parseFeature("r" + r.substring(1, 2));
		case "e":
			return Feature.parseFeature("e" + r.substring(1, 2));

		}
		return null;
	}
}

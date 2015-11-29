package gui;

import java.awt.Color;

/**
 * 
 * @author bonifantmc
 * 
 */
public enum Feature {

	/** Rectangular Sink */
	SINK_RECT("ro", Color.BLUE), /** Rectangular Source */
	SOURCE_RECT("ri", Color.RED), /** Rectangular Wall */
	WALL_RECT("rw", Color.MAGENTA), /** Elliptical Sink */
	SINK_ELLI("eo", Color.BLUE), /** Elliptical Source */
	SOURCE_ELLI("ei", Color.RED), /** Elliptical Wall */
	WALL_ELLI("ew", Color.MAGENTA), /** Rectangular Fluid */
	FLUID_RECT("rf", Color.GREEN), /** Elliptical Fluid */
	FLUID_ELLI("ef", Color.GREEN);

	/**
	 * This is the character string that is printed when an annotation is
	 * written to a .lst file
	 */
	private String s;

	/** the color to draw the feature with */
	private Color c;

	/**
	 * @param sym
	 *            the character symbol that each Feature has.
	 * @param color
	 *            the color to draw the Feature as.
	 * @param rect
	 *            true rectangle false ellipse.
	 */
	Feature(String sym, Color color) {
		this.s = sym;
		this.c = color;
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
	public static Feature parseFeature(String string) {

		switch (string.toLowerCase()) {
		case "ri":
			return SOURCE_RECT;
		case "ro":
			return SINK_RECT;
		case "rw":
			return WALL_RECT;
		case "ei":
			return SOURCE_ELLI;
		case "eo":
			return SINK_ELLI;
		case "ew":
			return WALL_ELLI;
		case "ef":
			return FLUID_ELLI;
		case "rf":
			return FLUID_RECT;
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

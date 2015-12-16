package gui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Boundaries are rectangular shapes, ellipses and actual rectangles drawn on
 * the screen when the user is setting up their chamber configuration. They
 * first draw a rectangle for the chamber and then add ellipses and recntagles
 * inside it as obstructions
 */
public class Boundary {
	// ////////////////////////////////////////////////
	// Regular Expressions for matching Boundarys //
	// ////////////////////////////////////////////////

	/** a regular expression that matches a rectangle [er][iow][x,y;w,h] */
	static private final String reRectangle = "([er][iow])\\[(-?\\d+),(-?\\d+);(-?\\d+),(-?\\d+)\\]";

	/** The Pattern that checks if a string might be an Boundary */
	static private final Pattern BoundaryPattern = Pattern.compile(reRectangle);

	// ////////////////////////////////////////////////
	// Instance Variables that make up an Boundary //
	// ////////////////////////////////////////////////

	public RectangularShape shape;

	/** True if the shape is a rectangle, false if its an ellipse */
	public Feature rectangle;

	/**
	 * True if the Boundary is being resized, in which case its edge is
	 * thickened slightly. (also thinkens if the mouse hovers over the edge).
	 */
	public boolean isResizing = false;

	// ///////////////
	// Constructors //
	// ///////////////

	/**
	 * Create an Boundary marking a specific rectangular spot on an image.
	 * 
	 * @param rect
	 *            the boundary is a rectangle or else an ellipse, and either a
	 *            wall, sink or source
	 * @param x
	 *            initial x coordinate (left-most x coordinate)
	 * @param y
	 *            initial y coordinate (top-most y coordinate)
	 * @param width
	 *            width of this Boundary
	 * @param height
	 *            height of this Boundary
	 */
	public Boundary(Feature rect, float x, float y, float width, float height) {
		this.rectangle = rect;
		if (rect.isRectangle())
			this.shape = new Rectangle2D.Float(x, y, width, height);
		else
			this.shape = new Ellipse2D.Float(x, y, width, height);
	}

	// ///////////////////
	// general methods //
	// ///////////////////

	/**
	 * Generate a String in the form f{[x,y;w,h] i[x',y';w',h']...}, if
	 * sub-Boundarys are included; otherwise, generate f[x,y;w,h].
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(rectangle.getIdString());
		ret.append("[");
		ret.append((int) shape.getMinX());
		ret.append(",");
		ret.append((int) shape.getMinY());
		ret.append(";");
		ret.append((int) shape.getWidth());
		ret.append(",");
		ret.append((int) shape.getHeight());
		ret.append("]");
		return ret.toString();
	}

	/**
	 * Confirms if point p is within this Boundary, check takes into account
	 * whether or not the Boundary is an ellipse or a rectangle
	 * 
	 * @param p
	 *            the point to check
	 * @return true if the point is inside or on the rectangle, else false;
	 */
	public boolean contains(Point2D.Float p) {
		return shape.contains(p);
	}

	/**
	 */
	public static void paintBoundarys(Graphics2D g, List<Boundary> bounds, int xi, int yi) {
		int x = 0;
		int y = 0;
		for (Boundary bound : bounds) {
			x = xi;
			y = yi;
			if (bound.isResizing)
				g.setStroke(new BasicStroke(5));
			else
				g.setStroke(new BasicStroke(1));
			g.setColor(bound.rectangle.getColor());

			x += bound.shape.getMinX();
			y += bound.shape.getMinY();

			if (bound.rectangle.isRectangle())
				g.fillRect(x, y, (int) bound.shape.getWidth(), (int) bound.shape.getHeight());
			else
				g.fillOval(x, y, (int) bound.shape.getWidth(), (int) bound.shape.getHeight());

		}
	}

	/** performs a deep copy of this Boundary */
	@Override
	public Boundary clone() {
		return new Boundary(rectangle, (float) shape.getMinX(), (float) shape.getMinY(), (float) shape.getWidth(),
				(float) shape.getHeight());
	}

	/**
	 * Performs a deep copy on an ArrayList of Boundarys
	 * 
	 * @param list
	 *            the list to copy
	 * @return the deep copy
	 */
	public static ArrayList<Boundary> cloneList(ArrayList<Boundary> list) {
		ArrayList<Boundary> ret = new ArrayList<>();
		for (Boundary b : list)
			ret.add(b.clone());
		return ret;
	}

	/**
	 * @return the area this Boundary covers in pixels^2.
	 */
	public float getArea() {
		return (float) (shape.getWidth() * shape.getHeight());
	}

	/**
	 * @return the lowest aspect ratio this Boundary could have regardless of
	 *         orientation.
	 */
	public float getAspectRatio() {
		float widthOverHeight = (float) (shape.getWidth() / shape.getHeight());
		float heightOverWidth = (float) (shape.getHeight() / shape.getWidth());
		return widthOverHeight < heightOverWidth ? widthOverHeight : heightOverWidth;

	}

	/**
	 * How close the user needs to be to the shape to catch it for resizing.
	 */
	private static final int MARGIN = 6;

	/**
	 * Confirm if a point is within margins for grabbing the boarder and
	 * resizing it
	 * 
	 * @param pt
	 *            the point to check
	 * @param buffer
	 *            distance from the boarder the pt can be and still return true
	 * @return true if the pt is within the buffer's distance from the boarder
	 */
	public boolean withinBufferRange(Point2D.Float pt) {
		// assume its not within the buffer range
		boolean ret = false;
		// the boarder's of the buffer range
		RectangularShape inner, outer;
		Point2D.Float s = getStart();
		int crop = 2 * MARGIN;

		// define the inner and outer boarders of the MARGIN range
		if (this.rectangle.isRectangle()) {
			inner = new Rectangle2D.Float(s.x + MARGIN, s.y + MARGIN, (float) this.shape.getWidth() - crop,
					(float) this.shape.getHeight() - crop);
			outer = new Rectangle2D.Float(s.x - MARGIN, s.y - MARGIN, (float) this.shape.getWidth() + crop,
					(float) this.shape.getHeight() + crop);
		} else {
			inner = new Ellipse2D.Float(s.x + MARGIN, s.y + MARGIN, (float) this.shape.getWidth() - crop,
					(float) this.shape.getHeight() - crop);
			outer = new Ellipse2D.Float(s.x - MARGIN, s.y - MARGIN, (float) this.shape.getWidth() + crop,
					(float) this.shape.getHeight() + crop);
		}
		ret = !inner.contains(pt) && outer.contains(pt);
		return ret;
	}

	/**
	 * @return the start point of this Boundary relative to its image origin
	 */
	public Point2D.Float getStart() {
		return new Point2D.Float((float) this.shape.getX(), (float) this.shape.getY());
	}

	/**
	 * @return the end point of this Boundary relative to its image origin
	 */
	public Point2D.Float getEnd() {
		Point2D.Float ret = getStart();
		ret.x += shape.getWidth();
		ret.y += shape.getHeight();
		return ret;
	}

	/**
	 * 
	 * @param p
	 *            the new Start Point of this Boundary (relative to its image
	 *            origin)
	 */
	void setStart(Point2D.Float p) {
		this.shape.setFrame(p.x, p.y, this.shape.getMaxX() - p.x, this.shape.getMaxY() - p.y);
	}

	/**
	 * @param p
	 *            the new End Point of this Boundary (relative to its image
	 *            origin)
	 */
	void setEnd(Point2D.Float p) {
		this.shape.setFrame(this.shape.getX(), this.shape.getY(), p.x - this.shape.getX(), p.y - this.shape.getY());
	}

	/**
	 * sets the Boundary's boundaries based on the two given points, with the
	 * confine that the Boundary cannot leave its parent if a parent exists
	 * 
	 * @param pt1
	 *            one point to define the rectangle
	 * @param pt2
	 *            another point to define the rectangle
	 */
	public void setRect(Point2D.Float pt1, Point2D.Float pt2) {
		Point2D.Float s = new Point2D.Float(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y));
		Point2D.Float e = new Point2D.Float(Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y));
		System.out.println(pt1 + " " + pt2);
		this.shape.setFrameFromDiagonal(e, s);
	}

	/**
	 * Iterate through the list of boundaries and return the first boundary
	 * found that contains the given point
	 * 
	 * @param pt
	 *            the point to search for boundaries at
	 * @param list
	 *            the list of boundaries to search for the point in.
	 * @return the first Boundary that contains the point (or null if none
	 *         exist)
	 */
	static public Boundary getBoundaryAtPoint(Point2D.Float pt, ArrayList<Boundary> list) {
		for (Boundary b : list) {
			if (b.contains(pt))
				return b;
		}
		return null;
	}

	/**
	 * Translate the Boundary a distance indicated by the given point
	 * 
	 * @param dist
	 *            more a vector than an actual point this tells how far in the x
	 *            and y direction to move this Boundary
	 * @param frame
	 *            boundary that Boundary can't be translated outside of
	 */
	public void translate(Point2D.Float dist) {
		// translate the distance
		this.shape.setFrame(this.shape.getX() + dist.x, this.shape.getY() + dist.y, this.shape.getWidth(),
				this.shape.getHeight());

	}

	/**
	 * @return the lower left corner of the rectangle
	 */
	public Point2D.Float lowerLeft() {
		return new Point2D.Float(getStart().x, getEnd().y);
	}

	/**
	 * @return the upper right corner of the rectangle
	 */
	public Point2D.Float upperRight() {
		return new Point2D.Float(getEnd().x, getStart().y);
	}

	/**
	 * @return the four corners of the rectangle in the order: upper left, upper
	 *         right, lower left, lower right
	 */
	public Point2D.Float[] getCorners() {
		return new Point2D.Float[] { getStart(), upperRight(), getEnd(), lowerLeft() };
	}

	/** remove the first boundary at the given point */
	public static void removeBoundaryAtPoint(Float pt, ArrayList<Boundary> list) {
		Iterator<Boundary> iter = list.iterator();
		while (iter.hasNext())
			if (iter.next().contains(pt)) {
				iter.remove();
				return;
			}
	}

	/**
	 * Return the first Boundary near the given point
	 * 
	 * @param pt
	 *            point to search for boundaries near
	 * @param list
	 *            the list of boundaries to search for Boundaries in
	 * @return the first Boundary near the given point (near is within MARGIN of
	 *         the point).
	 */
	public static Boundary nearBoundary(Float pt, ArrayList<Boundary> list) {
		Iterator<Boundary> iter = list.iterator();
		while (iter.hasNext()) {
			Boundary b = iter.next();
			if (b.withinBufferRange(pt))
				return b;
		}
		return null;
	}

	/**
	 * Set the given feature and shift from Rectangle to Ellipse or vice versa
	 * 
	 * @param f
	 *            the feature to shift the Boundary to
	 */
	public void setFeature(Feature f) {
		this.rectangle = f;
		if (this.rectangle.isRectangle()) {
			this.shape = new Rectangle2D.Double(this.shape.getX(), this.shape.getY(), this.shape.getWidth(),
					this.shape.getHeight());
		} else {
			this.shape = new Ellipse2D.Double(this.shape.getX(), this.shape.getY(), this.shape.getWidth(),
					this.shape.getHeight());
		}
	}
}
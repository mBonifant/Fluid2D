package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

/**
 * 
 * A Mouse and Key Listener, allows users to draw ellipses and rectangles on a
 * canvar, and label them as either sources, sinks, or walls.
 * 
 * @author bonifantmc
 *
 */

public class DrawingTool extends MouseAdapter implements KeyListener {

	/** The EditImage using this adapter */
	private final EditLattice edit;

	/** This tool's EditImage's Boundaries when first displayed */
	private final BoundarySnapshot initial;

	/** Stack of previous Boundaries as this tools EditImage was edited */
	private final Stack<BoundarySnapshot> undoStack;

	/** Current set of Boundaries for this tool's EditImage, to add to stack */
	private BoundarySnapshot toPush;

	/** The Boundary currently being drawn */
	private Boundary tempBoundary;

	private Feature drawingFeature = Feature.WALL_RECT;
	/**
	 * The point where a user clicked when they started a drag operation.
	 */
	private Point2D.Double anchor = new Point2D.Double(0, 0);

	/** true if editing the start/end of an Boundary */
	private boolean editingExistingBoundary = false;
	/** true if a new Boundary is being made */
	private boolean makingNewBoundary = false;
	/** true if an old Boundary is being dragged to relocate it */
	private boolean draggingExistingBoundary = false;

	/**
	 * 
	 * 
	 * @param eL
	 *            this DrawingTools EditLattice
	 */
	public DrawingTool(EditLattice eL) {
		this.edit = eL;
		this.undoStack = new Stack<>();
		this.initial = new BoundarySnapshot();
		this.initial.takeSnapshot();
		this.initial.setSnapshot();
		setPush(this.initial);
	}

	/**
	 * Pressing the mouse can have three results:
	 * <p>
	 * right-clicking: display a pop-up menu (when over an image display
	 * selections for Boundary Attributes, when not display selections for
	 * Feature)
	 * <p>
	 * left-click: Begin drawing an Boundary double-left-click: delete an
	 * Boundary
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		edit.requestFocus();
		// 1 right click brings up menu
		if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
			new DrawingToolPopUp(e.getPoint()).show(edit, e.getX(), e.getY());
			return;
		}
		Point2D.Double pt = to2DPoint(e.getPoint());
		ArrayList<Boundary> list = edit.getBoundaries();

		// 1 left click, handle Boundary drawing
		if (e.getClickCount() == 1) {

			Boundary a;
			// if Boundary is close, prepare to resize it
			if ((a = Boundary.nearBoundary(pt, list)) != null) {
				setTempBoundary(a);
				setEditingExistingBoundary(true);
				Point2D.Double[] arr = a.getCorners();
				double dist = Integer.MAX_VALUE;
				int i = 0;
				int j = 0;
				for (Point2D.Double pA : arr) {
					double t = pA.distance(pt);
					if (t < dist) {
						dist = t;
						j = i;
					}
					i++;
				}
				// set anchor as point opposite where the mouse is.
				// arr ={upperRight, upperLeft, lowerLeft, lowerRight} so
				// ur=0->ll=2, ul=1->lr=3, etc.
				setAnchor(j == 0 ? arr[2] : j == 1 ? arr[3] : j == 2 ? arr[0] : arr[1]);
				setEditingExistingBoundary(true);
			} else
				// translate when not drawing a feature or sub-feature
				if (
				// 1) when clicking in a Boundary of the same type you're set to
				// draw
			(((a = Boundary.getBoundaryAtPoint(pt, list)) != null) && a.rectangle == this.getDrawingFeature())) {
				setTempBoundary(a);
				setDraggingExistingBoundary(true);
				setAnchor(pt);
			}
			// otherwise start drawing a new Boundary
			else {
				System.out.println("Drawing a new shape");
				setAnchor(pt);
				startDrawingBoundary();
			}
		} else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
			Boundary.removeBoundaryAtPoint(pt, list);
		}
	}

	/**
	 * Handles altering the Boundary as it is formed by left-mouse drags (also
	 * updates the tooltip)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
System.out.println("DRAGGING");		// do nothing for right drags
		if (SwingUtilities.isRightMouseButton(e))
			return;

		Point2D.Double end = to2DPoint(e.getPoint());

		// handle making a new Boundary
		if (isMakingNewBoundary() || isEditingExistingBoundary()) {
			getTempBoundary().setRect(getAnchor(), end);
		} else if (isDraggingExistingBoundary()) {
			// calculate translation distance
			Point2D.Double dist = new Point2D.Double(end.x - getAnchor().x, end.y - getAnchor().y);
			// apply translation
			getTempBoundary().translate(dist);
			// reset anchor or you'll add more than intended next drag and
			// accelerate off screen
			setAnchor(end);
		}

		// update tool tip
		String oldText = edit.getToolTipText();
		edit.setToolTipText("<html><body style='width: 50px'>" + getTempBoundary().toString());
		if (oldText != null && !oldText.equals(edit.getToolTipText())) {
			ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(edit, -1, System.currentTimeMillis(), 0, e.getX(),
					e.getY(), e.getXOnScreen(), e.getYOnScreen(), 0, false, 0));
		}

		// refresh the Boundary
		edit.repaint();
	}

	/**
	 * Finalizes the Boundary currently being drawn.
	 * <p>
	 * Ensures the Boundary (not sub-Boundaries though) obey the requirements
	 * defined by Boundary.MIN_AREA & Boundary.MIN_ASPECT_RATIO. If either of
	 * these conditions is not met, the Boundary is removed.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		setDraggingExistingBoundary(false);
		setEditingExistingBoundary(false);
		setMakingNewBoundary(false);
		setAnchor(null);
		setTempBoundary(null);
		edit.repaint();
		snap();
	}

	/**
	 * Take a snapshot and it its different from the last one add it to the
	 * undostack
	 */
	public void snap() {
		BoundarySnapshot take = new BoundarySnapshot();
		take.takeSnapshot();
		if (!take.equals(getPush())) {
			getUndoStack().push(getPush());
			setPush(take);
		}
	}

	/**
	 * Update's this tool's EditImage's tooltip as mouse moves
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D.Double check = to2DPoint(e.getPoint());
		Boundary.nearBoundary(check, edit.getBoundaries());
		edit.repaint();
	}

	/**
	 * 
	 * Handle key commands, left and right arrow cycle through opening new
	 * EditImages, f,s,p,i,e,n,m change this tool's current Feature,
	 * f,s,p,i,e,n,m, along with crtl pressed change the Boundary type of an
	 * existing Boundary (if the mouse is hovering over one at the moment
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		String key = KeyEvent.getKeyText(e.getKeyCode());
		System.out.println(key);
		if (Feature.charValues().contains(key.toLowerCase()))
			letterCommands(e);
		else if (key.equals("C")) {
			this.edit.getBoundaries().clear();
			this.edit.repaint();
			snap();
		} else if (e.isControlDown() && KeyEvent.getExtendedKeyCodeForChar('z') == e.getKeyCode())
			new UndoMenuItem().actionPerformed(null);

	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// do nothing
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	/**
	 * Set the new temporary Boundary to draw and check if it has a parent
	 */
	void startDrawingBoundary() {
		setTempBoundary(new Boundary(getDrawingFeature(), (double) getAnchor().x, (double) getAnchor().y, 0f, 0f));
		edit.getBoundaries().add(getTempBoundary());
		setMakingNewBoundary(true);
	}

	/**
	 * @return the Boundary being currently drawn
	 */
	Boundary getTempBoundary() {
		return this.tempBoundary;
	}

	/**
	 * @param s
	 *            value to set this tools most recent snapshot, should only be
	 *            called when this tool's state changes
	 */
	void setPush(BoundarySnapshot s) {
		this.toPush = s;
	}

	/**
	 * @param Boundary
	 *            the Boundary currently being drawn will be set to this
	 */
	void setTempBoundary(Boundary Boundary) {
		this.tempBoundary = Boundary;
	}

	/**
	 * @return the initial point of an Boundary the user started to draw from
	 */
	Point2D.Double getAnchor() {
		return this.anchor;
	}

	/**
	 * @param p
	 *            the initial point of an Boundary the user started to draw
	 */
	void setAnchor(Point2D.Double p) {
		this.anchor = p;
	}

	/**
	 * @return the type of the current tempgBoundary
	 */
	Feature getDrawingFeature() {
		return this.drawingFeature;
	}

	/***
	 * @param Feature
	 *            the type to set the current tempBoundary
	 */
	void setDrawingFeature(Feature Feature) {
		this.drawingFeature = Feature;
	}

	/**
	 * @return the Stack of previous actions taken while annotating
	 */
	Stack<BoundarySnapshot> getUndoStack() {
		return this.undoStack;
	}

	/**
	 * 
	 * @param pt
	 *            the plain blah point to convert to a 2D Double point
	 * @return the converted 2D Double Point
	 */
	static Point2D.Double to2DPoint(Point pt) {
		if (pt == null)
			return null;
		return new Point2D.Double(pt.x, pt.y);
	}

	/**
	 * Handle key commands for switching Boundary type both of the current
	 * Boundary being drawn and of those hovered over(only when ctrl is
	 * pressed).
	 * 
	 * @param e
	 *            the KeyEvent indicating which Feature to switch to
	 */
	private void letterCommands(KeyEvent e) {
		System.out.println("letters");
		String keyCode = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();

		Point2D.Double p = to2DPoint(edit.getMousePosition());

		// change an existing Boundaries type, if control is down and mouse
		// over an Boundary
		if (e.isControlDown() && p != null) {
			// cycle to find an Boundary at point p
			for (Boundary a : edit.getBoundaries()) {
				if (a.contains(p)) {
					a.setFeature(Feature.getConversion(a.rectangle, keyCode));
					edit.repaint();
					snap();
					break;
				}
			}
		} else {
			setDrawingFeature(Feature.getConversion(getDrawingFeature(), keyCode));
		}
	}

	/**
	 * @return the most recent snapshot, once the state of this tool changes,
	 *         this should be reset
	 */
	private BoundarySnapshot getPush() {
		return this.toPush;
	}

	/**
	 * @return true if currently editing an existing Boundary
	 */
	private boolean isEditingExistingBoundary() {
		return this.editingExistingBoundary;
	}

	/**
	 * @param editingExistingBoundary
	 *            whether an Boundary is being edited or not
	 */
	private void setEditingExistingBoundary(boolean editingExistingBoundary) {
		this.editingExistingBoundary = editingExistingBoundary;
	}

	/**
	 * @return true if moving an existing Boundary
	 */
	private boolean isDraggingExistingBoundary() {
		return this.draggingExistingBoundary;
	}

	/**
	 * @param draggingExistingBoundary
	 *            should be true if dragging current Boundary, else false
	 */
	private void setDraggingExistingBoundary(boolean draggingExistingBoundary) {
		this.draggingExistingBoundary = draggingExistingBoundary;
	}

	/**
	 * @return true if drawing a new Boundary
	 */
	private boolean isMakingNewBoundary() {
		return this.makingNewBoundary;
	}

	/**
	 * @param makingNewBoundary
	 *            true if drawing a new Boundary false otherwise
	 */
	private void setMakingNewBoundary(boolean makingNewBoundary) {
		this.makingNewBoundary = makingNewBoundary;
	}

	/**
	 * A pop-up menu for selecting which Boundary type to draw or attribute to
	 * label an Boundary. Also displays undo item.
	 * 
	 * @author bonifantmc
	 * 
	 */
	@SuppressWarnings("serial")
	private class DrawingToolPopUp extends JPopupMenu {

		/**
		 * Build a menu for selecting the current Feature, if note is null. If
		 * its not null then build a menu to adjust the given Boundary's
		 * Attributes
		 * 
		 * @param point
		 *            where the window will originate switch the current Feature
		 */
		DrawingToolPopUp(Point2D point) {
			super();
			Boundary note = null;
			List<Boundary> l = edit.getBoundaries();
			for (Boundary n : l) {
				if (n.shape.contains(point)) {
					note = n;
					break;
				}
			}

			if (note == null) {
				addEnumValuesToMenu(Feature.values(), null);
			} else {
				addEnumValuesToMenu(Feature.values(), note);
			}

			addSeparator();

			// insert undo menu item
			add(new UndoMenuItem());
		}

		/**
		 * Adds the given enumerated values to this menu, disabling whichever
		 * should be currently selected
		 * 
		 * @param values
		 *            the enumerated values to add to the menu
		 * @param note
		 *            if note is set then its boundary type is being changed,
		 *            else the boundary type being drawn is being changed
		 */
		private void addEnumValuesToMenu(final Feature[] values, final Boundary note) {
			ButtonGroup group = new ButtonGroup();
			final ArrayList<JMenuItem> items = new ArrayList<>();

			// build items
			for (Feature val : values)
				items.add(new JRadioButtonMenuItem(val.toString()));

			// create a listener for the items
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if (items.contains(arg0.getSource())) {
						String text = ((JMenuItem) arg0.getSource()).getText();

						// these don't require repaints
						if (note == null)
							setDrawingFeature(Feature.parseFeature(text));
						else {
							note.rectangle = Feature.parseFeature(text);
							edit.repaint();
							snap();
						}
					}

				}
			};

			// add the items and select the appropriate item
			for (int j = 0; j < items.size(); j++) {
				JMenuItem i = items.get(j);

				if (i.getText().equals(DrawingTool.this.getDrawingFeature()))
					i.setEnabled(false);
				add(i);
				i.addActionListener(listener);
				group.add(i);
			}
		}
	}

	/**
	 * Menu item for handling undoing the most recent step in Boundary.
	 * 
	 * @author Girish
	 * 
	 */
	@SuppressWarnings("serial")
	private class UndoMenuItem extends AbstractAction {

		/** Create MenuItem */
		UndoMenuItem() {
			putValue(NAME, "Undo");
			putValue(SHORT_DESCRIPTION, "Undo the last Boundary");
			if (getUndoStack().size() <= 0)
				setEnabled(false);
		}

		/**
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!getUndoStack().isEmpty()) {
				BoundarySnapshot prevBoundaries = getUndoStack().pop();

				prevBoundaries.setSnapshot();
				if (getUndoStack().size() > 1)
					putValue(SHORT_DESCRIPTION, String.format("Undo the last Boundary %s ?",
							getUndoStack().peek().snapTempBoundary.toString()));

				setEnabled(!getUndoStack().isEmpty());
				setPush(prevBoundaries);

				edit.repaint();
			}
		}

	}

	/**
	 * 
	 * A snapshot of this Tool at one instance in time.
	 * 
	 * @author Girish
	 * 
	 */
	private class BoundarySnapshot {
		/** this snapshot's list of Boundaries at one instance in time */
		private ArrayList<Boundary> Boundaries;
		/** the current Boundary being edited at one instance in time */
		Boundary snapTempBoundary;
		/** the current Feature being edited at one instance in time */
		private Feature snapDrawingFeature;

		/** the start point of tempBoundary at one instance in time */
		private Point2D.Double snapStart;

		/**
		 * Set values of this snapshot based on this tool's variables currently
		 */
		void takeSnapshot() {
			// copy type
			this.snapDrawingFeature = getDrawingFeature();
			// copy temp Boundary
			this.snapTempBoundary = getTempBoundary() == null ? null : getTempBoundary().clone();
			// copy point
			this.snapStart = getAnchor();
			// copy Boundary list
			this.Boundaries = Boundary.cloneList(edit.getBoundaries());
		}

		/** set values of this tool based on this snapshot */
		void setSnapshot() {
			// replace all
			setDrawingFeature(this.snapDrawingFeature);
			setTempBoundary(this.snapTempBoundary == null ? null : this.snapTempBoundary.clone());
			setAnchor(this.snapStart);
			edit.setBoundaries(this.Boundaries == null ? null : Boundary.cloneList(this.Boundaries));
		}

		/**
		 * Generated hashCode method since equals was overwritten.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + DrawingTool.this.hashCode();
			result = prime * result + ((this.Boundaries == null) ? 0 : this.Boundaries.hashCode());
			result = prime * result + ((this.snapDrawingFeature == null) ? 0 : this.snapDrawingFeature.hashCode());
			result = prime * result + ((this.snapStart == null) ? 0 : this.snapStart.hashCode());
			result = prime * result + ((this.snapTempBoundary == null) ? 0 : this.snapTempBoundary.hashCode());
			return result;
		}

		/**
		 * 
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof BoundarySnapshot) {
				BoundarySnapshot to = (BoundarySnapshot) obj;
				boolean ret = true;
				ret &= this.snapDrawingFeature == to.snapDrawingFeature;
				ret &= this.Boundaries.toString().equals(to.Boundaries.toString());
				return ret;
			}
			return false;
		}
	}
}

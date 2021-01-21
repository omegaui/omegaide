/*
 * 09/07/2006
 *
 * ResizableFrameContentPane.java - A content pane with a size grip that
 * can be used to resize a sizable dialog or frame.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.*;
import javax.swing.*;



/**
 * A panel to be used as the content pane for <code>JDialog</code>s
 * and <code>JFrame</code>s that are resizable.  This panel has
 * a size grip that can be dragged and cause a resize of the window,
 * similar to that found on resizable Microsoft Windows dialogs.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ResizableFrameContentPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private SizeGripIcon gripIcon;


	/**
	 * Constructor.
	 */
	public ResizableFrameContentPane() {
		gripIcon = new SizeGripIcon();
	}


	/**
	 * Constructor.
	 *
	 * @param layout The layout manager.
	 */
	public ResizableFrameContentPane(LayoutManager layout) {
		super(layout);
		gripIcon = new SizeGripIcon();
	}


	/**
	 * Paints this panel.
	 *
	 * @param g The graphics context.
	 */
	/*
	 * We override paint() instead of paintComponent() as if we do the latter,
	 * sometimes child panels will be painted over our size grip, rendering it
	 * invisible.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		gripIcon.paintIcon(this, g, this.getX(), this.getY());
	}


}
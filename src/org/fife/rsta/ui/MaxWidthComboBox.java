/*
 * 11/27/2004
 *
 * MaxWidthComboBox.java - A combo box with a maximum width, to avoid pesky
 * layout problems when the combo contains lengthy strings.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.Dimension;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;


/**
 * A combo box whose width cannot go over a specified value.  This class is
 * useful when you have a layout manager that adheres to the combo box's
 * preferred/maximum sizes (such as <code>SpringLayout</code>), and your
 * combo box contains a value longer than you'd like - the combo box is drawn
 * too large and the GUI looks ugly.  With this class you can set a maximum
 * width for the combo box, and its height will never be affected.
 *
 * @param <E> The type of item in the combo box.
 * @author Robert Futrell
 * @version 0.5
 */
public class MaxWidthComboBox<E> extends JComboBox<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * The width of this combo box will never be greater than this value.
	 */
	private int maxWidth;


	/**
	 * Constructor.
	 *
	 * @param maxWidth The maximum width for this combo box.
	 */
	public MaxWidthComboBox(int maxWidth) {
		this.maxWidth = maxWidth;
	}


	/**
	 * Constructor.
	 *
	 * @param model The model for this combo box.
	 * @param maxWidth The maximum width for this combo box.
	 */
	public MaxWidthComboBox(ComboBoxModel<E> model, int maxWidth) {
		super(model);
		this.maxWidth = maxWidth;
	}


	/**
	 * Overridden to ensure that the returned size has width no greater than
	 * the specified maximum.
	 *
	 * @return The maximum size of this combo box.
	 */
	@Override
	public Dimension getMaximumSize() {
		Dimension size = super.getMaximumSize();
		size.width = Math.min(size.width, maxWidth);
		return size;
	}


	/**
	 * Overridden to ensure that the returned size has width no greater than
	 * the specified maximum.
	 *
	 * @return The minimum size of this combo box.
	 */
	@Override
	public Dimension getMinimumSize() {
		Dimension size = super.getMinimumSize();
		size.width = Math.min(size.width, maxWidth);
		return size;
	}


	/**
	 * Overridden to ensure that the returned size has width no greater than
	 * the specified maximum.
	 *
	 * @return The preferred size of this combo box.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.min(size.width, maxWidth);
		return size;
	}


}

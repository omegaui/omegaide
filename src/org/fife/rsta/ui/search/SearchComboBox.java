/*
 * 09/20/2013
 *
 * SearchComboBox - The combo box used for "find" and "replace" dropdowns.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.license.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.UIUtil;


/**
 * The combo box used for entering text to "find" and "replace" in both the
 * Find/Replace dialogs as well as tool bars.
 *
 * @author Robert Futrell
 * @version 1.0
 */
// NOTE: This class is public to facilitate applications creating other
// subclasses, such as a FindInFilesDialog.
public class SearchComboBox extends RegexAwareComboBox<String> {

	private FindToolBar toolBar;


	/**
	 * Constructor.
	 *
	 * @param toolBar The tool bar that owns this combo box, or {@code null}
	 *        if it is not in a tool bar.
	 * @param replace Whether this combo box is for "replace" text (as opposed
	 *        to "find" text).
	 */
	public SearchComboBox(FindToolBar toolBar, boolean replace) {
		super(replace);
		this.toolBar = toolBar;
		UIUtil.fixComboOrientation(this);
		updateTextFieldKeyMap();
	}


	/**
	 * Overridden to always select the newly-added item.  If the item is
	 * already in the list of choices, it is moved to the top before being
	 * selected.
	 *
	 * @param item The item to add.
	 */
	@Override
	public void addItem(String item) {

		// If they just searched for an item that's already in the list
		// other than the first, move it to the first position.
		int curIndex = getIndexOf(item);
		if (curIndex==-1) {
			super.addItem(item);
		}
		else if (curIndex>0) {
			removeItem(item);
			insertItemAt(item, 0);
		}

		// Always leave with the new item selected
		setSelectedIndex(0);
	}


	private int getIndexOf(String item) {
		for (int i=0; i<dataModel.getSize(); i++) {
			if (dataModel.getElementAt(i).equals(item)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Returns the text in the text field of the combo box.
	 *
	 * @return The text entered into this combo box.
	 */
	public String getSelectedString() {
		JTextComponent comp = UIUtil.getTextComponent(this);
		return comp.getText();
		//return (String)getSelectedItem();
	}


	/**
	 * Returns the <code>Strings</code> contained in this combo box.
	 *
	 * @return A <code>java.util.Vector</code> of strings found in this
	 *         combo box.  If that combo box is empty, than a zero-length
	 *         <code>Vector</code> is returned.
	 */
	public Vector<String> getSearchStrings() {

		// First, ensure that the item in the editor component is indeed in the
		// combo box.
		int selectedIndex = getSelectedIndex();
		if (selectedIndex==-1) {
			addItem(getSelectedString());
		}

		// If they just searched for an item that's already in the list other
		// than the first, move it to the first position.
		else if (selectedIndex>0) {
			String item = (String)getSelectedItem();
			removeItem(item);
			insertItemAt(item, 0);
			setSelectedIndex(0);
		}

		int itemCount = getItemCount();
		Vector<String> vector = new Vector<>(itemCount);
		for (int i=0; i<itemCount; i++) {
			vector.add(getItemAt(i));
		}

		return vector;

	}


	/**
	 * Updates the input map of the text field inside of this search combo.
	 */
	private void updateTextFieldKeyMap() {
		JTextComponent comp = UIUtil.getTextComponent(this);
		// Swing maps Ctrl+H to "delete previous", when applications
		// typically map it to "display 'Replace' UI."
		InputMap im = comp.getInputMap();
		im.put(KeyStroke.getKeyStroke("ctrl H"), "none");
	}


	@Override
	public void updateUI() {
		super.updateUI();
		if (toolBar!=null) {
			toolBar.searchComboUpdateUICallback(this);
		}
		updateTextFieldKeyMap();
	}


}

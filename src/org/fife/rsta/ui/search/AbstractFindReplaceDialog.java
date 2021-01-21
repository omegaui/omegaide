/*
 * 04/08/2004
 *
 * AbstractFindReplaceSearchDialog.java - Base class for FindDialog and
 * ReplaceDialog.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchContext;


/**
 * This is the base class for {@link FindDialog} and {@link ReplaceDialog}. It
 * is basically all of the features common to the two dialogs that weren't
 * taken care of in {@link AbstractSearchDialog}.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public abstract class AbstractFindReplaceDialog extends AbstractSearchDialog {

	/**
	 * Property fired when the user toggles the search direction radio buttons.
	 */
	public static final String SEARCH_DOWNWARD_PROPERTY	= "SearchDialog.SearchDownward";

	// The radio buttons for changing the search direction.
	protected JRadioButton upButton;
	protected JRadioButton downButton;
	protected JPanel dirPanel;
	private String dirPanelTitle;
	protected JLabel findFieldLabel;
	protected JButton findNextButton;

	/**
	 * The "mark all" check box.
	 */
	protected JCheckBox markAllCheckBox;

	/**
	 * Folks listening for events in this dialog.
	 */
	private EventListenerList listenerList;


	/**
	 * Constructor.
	 *
	 * @param owner The dialog that owns this search dialog.
	 */
	public AbstractFindReplaceDialog(Dialog owner) {
		super(owner);
		init();
	}

	/**
	 * Constructor.  Does initializing for parts common to
	 * <code>FindDialog</code> and <code>ReplaceDialog</code> that isn't
	 * taken care of in <code>AbstractSearchDialog</code>'s constructor.
	 *
	 * @param owner The window that owns this search dialog.
	 */
	public AbstractFindReplaceDialog(Frame owner) {
		super(owner);
		init();
	}


	/**
	 * Listens for action events in this dialog.
	 *
	 * @param e The event that occurred.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("UpRadioButtonClicked".equals(command)) {
			context.setSearchForward(false);
		}

		else if ("DownRadioButtonClicked".equals(command)) {
			context.setSearchForward(true);
		}

		else if ("MarkAll".equals(command)) {
			boolean checked = markAllCheckBox.isSelected();
			context.setMarkAll(checked);
		}

		else if (SearchEvent.Type.FIND.name().equals(command)) {
			doSearch(context.getSearchForward()); // Keep current direction
		}

		else {
			super.actionPerformed(e);
		}

	}


	/**
	 * Adds a {@link SearchListener} to this dialog.  This listener will
	 * be notified when find or replace operations are triggered.  For
	 * example, for a Replace dialog, a listener will receive notification
	 * when the user clicks "Find", "Replace", or "Replace All".
	 *
	 * @param l The listener to add.
	 * @see #removeSearchListener(SearchListener)
	 */
	public void addSearchListener(SearchListener l) {
		listenerList.add(SearchListener.class, l);
	}


	private void doSearch(boolean forward) {

		// Add the item to the combo box's list, if it isn't already there.
		JTextComponent tc = UIUtil.getTextComponent(findTextCombo);
		findTextCombo.addItem(tc.getText());
		context.setSearchFor(getSearchString());

		// If the ask is to search in the opposite direction from the UI's
		// current direction, use a cloned search context to avoid updating
		// the UI when we shouldn't (e.g. from a keyboard shortcut to search
		// backward).
		SearchContext contextToFire = context;
		if (forward != context.getSearchForward()) {
			contextToFire = context.clone();
			contextToFire.setSearchForward(forward);
		}

		// Let parent application know
		fireSearchEvent(SearchEvent.Type.FIND, contextToFire);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * <code>event</code> parameter.
	 *
	 * @param type The type of search.
	 * @param context The search context to fire.  If this is {@code null}, this
	 *        dialog's current context (e.g. its current state) is used.  This
	 *        parameter allows for scenarios where we want to search differently
	 *        than what our UI displays; for example, binding a keyboard
	 *        shortcut to "search backwards" no matter what.
	 */
	protected void fireSearchEvent(SearchEvent.Type type, SearchContext context) {

		if (context == null) {
			context = this.context;
		}

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		SearchEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SearchListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new SearchEvent(this, type, context);
				}
				((SearchListener)listeners[i+1]).searchEvent(e);
			}
		}
	}


	/**
	 * Returns the text for the "Down" radio button.
	 *
	 * @return The text for the "Down" radio button.
	 * @see #setDownRadioButtonText
	 */
	public final String getDownRadioButtonText() {
		return downButton.getText();
	}


	/**
	 * Returns the text on the "Find" button.
	 *
	 * @return The text on the Find button.
	 * @see #setFindButtonText
	 */
	public final String getFindButtonText() {
		return findNextButton.getText();
	}


	/**
	 * Returns the label on the "Find what" text field.
	 *
	 * @return The text on the "Find what" text field.
	 * @see #setFindWhatLabelText
	 */
	public final String getFindWhatLabelText() {
		return findFieldLabel.getText();
	}


	/**
	 * Returns the text for the search direction's radio buttons' border.
	 *
	 * @return The text for the search radio buttons' border.
	 * @see #setSearchButtonsBorderText
	 */
	public final String getSearchButtonsBorderText() {
		return dirPanelTitle;
	}


	/**
	 * Returns the text for the "Up" radio button.
	 *
	 * @return The text for the "Up" radio button.
	 * @see #setUpRadioButtonText
	 */
	public final String getUpRadioButtonText() {
		return upButton.getText();
	}


	/**
	 * Called whenever a property in the search context is modified.
	 * Subclasses should override if they listen for additional properties.
	 *
	 * @param e The property change event fired.
	 */
	@Override
	protected void handleSearchContextPropertyChanged(PropertyChangeEvent e) {

		String prop = e.getPropertyName();

		if (SearchContext.PROPERTY_SEARCH_FORWARD.equals(prop)) {
			boolean newValue = (Boolean) e.getNewValue();
			JRadioButton button = newValue ? downButton : upButton;
			button.setSelected(true);
		}

		else if (SearchContext.PROPERTY_MARK_ALL.equals(prop)) {
			boolean newValue = (Boolean) e.getNewValue();
			markAllCheckBox.setSelected(newValue);
		}

		else {
			super.handleSearchContextPropertyChanged(e);
		}

	}


	@Override
	protected FindReplaceButtonsEnableResult handleToggleButtons() {

		FindReplaceButtonsEnableResult er = super.handleToggleButtons();
		boolean enable = er.getEnable();

		findNextButton.setEnabled(enable);

		// setBackground doesn't show up with XP Look and Feel!
		//findTextComboBox.setBackground(enable ?
		//		UIManager.getColor("ComboBox.background") : Color.PINK);
		JTextComponent tc = UIUtil.getTextComponent(findTextCombo);
		tc.setForeground(enable ? UIManager.getColor("TextField.foreground") :
									UIUtil.getErrorTextForeground());

		String tooltip = SearchUtil.getToolTip(er);
		tc.setToolTipText(tooltip); // Always set, even if null

		return er;

	}


	private void init() {

		listenerList = new EventListenerList();

		// Make a panel containing the "search up/down" radio buttons.
		dirPanel = new JPanel();
		dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.LINE_AXIS));
		setSearchButtonsBorderText(getString("Direction"));
		ButtonGroup bg = new ButtonGroup();
		upButton = new JRadioButton(getString("Up"), false);
		upButton.setMnemonic((int)getString("UpMnemonic").charAt(0));
		downButton = new JRadioButton(getString("Down"), true);
		downButton.setMnemonic((int)getString("DownMnemonic").charAt(0));
		upButton.setActionCommand("UpRadioButtonClicked");
		upButton.addActionListener(this);
		downButton.setActionCommand("DownRadioButtonClicked");
		downButton.addActionListener(this);
		bg.add(upButton);
		bg.add(downButton);
		dirPanel.add(upButton);
		dirPanel.add(downButton);

		// Initialize the "mark all" button.
		markAllCheckBox = new JCheckBox(getString("MarkAll"));
		markAllCheckBox.setMnemonic((int)getString("MarkAllMnemonic").charAt(0));
		markAllCheckBox.setActionCommand("MarkAll");
		markAllCheckBox.addActionListener(this);

		// Rearrange the search conditions panel.
		searchConditionsPanel.removeAll();
		searchConditionsPanel.setLayout(new BorderLayout());
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.PAGE_AXIS));
		temp.add(caseCheckBox);
		temp.add(wholeWordCheckBox);
		temp.add(wrapCheckBox);
		searchConditionsPanel.add(temp, BorderLayout.LINE_START);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.PAGE_AXIS));
		temp.add(regexCheckBox);
		temp.add(markAllCheckBox);
		searchConditionsPanel.add(temp, BorderLayout.LINE_END);

		// Create the "Find what" label.
		findFieldLabel = UIUtil.newLabel(getBundle(), "FindWhat",findTextCombo);

		// Create a "Find Next" button.
		findNextButton = UIUtil.newButton(getBundle(), "Find");
		findNextButton.setActionCommand(SearchEvent.Type.FIND.name());
		findNextButton.addActionListener(this);
		findNextButton.setDefaultCapable(true);
		findNextButton.setEnabled(false);	// Initially, nothing to look for.

		installKeyboardActions();

	}


	/**
	 * Adds extra keyboard actions for Find and Replace dialogs.
	 */
	private void installKeyboardActions() {

		JRootPane rootPane = getRootPane();
		InputMap im = rootPane.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am = rootPane.getActionMap();

		int modifier = getToolkit().getMenuShortcutKeyMask();
		KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, modifier);
		im.put(ctrlF, "focusSearchForField");
		am.put("focusSearchForField", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractFindReplaceDialog.this.requestFocus();
			}
		});

		// Shift+Enter and Ctrl+Enter both do a backwards/opposite search
		int shift = InputEvent.SHIFT_MASK;
		int ctrl = InputEvent.CTRL_MASK;
		if (System.getProperty("os.name").toLowerCase().contains("os x")) {
			ctrl = InputEvent.META_MASK;
		}
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, shift);
		im.put(ks, "searchBackward");
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ctrl);
		im.put(ks, "searchBackward");
		am.put("searchBackward", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSearch(!context.getSearchForward());
			}
		});
	}


	/**
	 * Overridden to initialize UI elements specific to this subclass.
	 */
	@Override
	protected void refreshUIFromContext() {
		if (this.markAllCheckBox==null) {
			return; // First time through, UI not realized yet
		}
		super.refreshUIFromContext();
		markAllCheckBox.setSelected(context.getMarkAll());
		boolean searchForward = context.getSearchForward();
		upButton.setSelected(!searchForward);
		downButton.setSelected(searchForward);
	}


	/**
	 * Removes a {@link SearchListener} from this dialog.
	 *
	 * @param l The listener to remove
	 * @see #addSearchListener(SearchListener)
	 */
	public void removeSearchListener(SearchListener l) {
		listenerList.remove(SearchListener.class, l);
	}


	/**
	 * Sets the text label for the "Down" radio button.
	 *
	 * @param text The new text label for the "Down" radio button.
	 * @see #getDownRadioButtonText
	 */
	public void setDownRadioButtonText(String text) {
		downButton.setText(text);
	}


	/**
	 * Sets the text on the "Find" button.
	 *
	 * @param text The text for the Find button.
	 * @see #getFindButtonText
	 */
	public final void setFindButtonText(String text) {
		findNextButton.setText(text);
	}


	/**
	 * Sets the label on the "Find what" text field.
	 *
	 * @param text The text for the "Find what" text field's label.
	 * @see #getFindWhatLabelText
	 */
	public void setFindWhatLabelText(String text) {
		findFieldLabel.setText(text);
	}


	/**
	 * Sets the text for the search direction's radio buttons' border.
	 *
	 * @param text The text for the search radio buttons' border.
	 * @see #getSearchButtonsBorderText
	 */
	public final void setSearchButtonsBorderText(String text) {
		dirPanelTitle = text;
		dirPanel.setBorder(createTitledBorder(dirPanelTitle));
	}


	/**
	 * Sets the text label for the "Up" radio button.
	 *
	 * @param text The new text label for the "Up" radio button.
	 * @see #getUpRadioButtonText
	 */
	public void setUpRadioButtonText(String text) {
		upButton.setText(text);
	}


}

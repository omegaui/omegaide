/*
 * 11/14/2003
 *
 * ReplaceDialog.java - Dialog for replacing text in a GUI.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.ResizableFrameContentPane;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;


/**
 * A "Replace" dialog similar to those found in most Windows text editing
 * applications.  Contains many search options, including:<br>
 * <ul>
 *   <li>Match Case
 *   <li>Match Whole Word
 *   <li>Use Regular Expressions
 *   <li>Search Forwards or Backwards
 * </ul>
 * The dialog also remembers your previous several selections in a combo box.
 * <p>An application can use a <code>ReplaceDialog</code> as follows.  It is suggested
 * that you create an <code>Action</code> or something similar to facilitate
 * "bringing up" the Replace dialog.  Have the main application contain an object
 * that implements <code>ActionListener</code>.  This object will receive the
 * following action events from the Replace dialog:
 * <ul>
 *   <li>{@link SearchEvent.Type#FIND} action when the user clicks the
 *       "Find" button.
 *   <li>{@link SearchEvent.Type#REPLACE} action
 *       when the user clicks the "Replace" button.
 *   <li>{@link SearchEvent.Type#REPLACE_ALL}
 *       action when the user clicks the "Replace All" button.
 * </ul>
 * The application can then call i.e.
 * {@link SearchEngine#find(javax.swing.JTextArea, org.fife.ui.rtextarea.SearchContext) SearchEngine.find()}
 * or
 * {@link SearchEngine#replace(org.fife.ui.rtextarea.RTextArea, org.fife.ui.rtextarea.SearchContext) SearchEngine.replace()}
 * to actually execute the search.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ReplaceDialog extends AbstractFindReplaceDialog {

	private static final long serialVersionUID = 1L;

	private JButton replaceButton;
	private JButton replaceAllButton;
	private JLabel replaceFieldLabel;

	private SearchComboBox replaceWithCombo;

	// This helps us work around the "bug" where JComboBox eats the first Enter
	// press.
	private String lastSearchString;
	private String lastReplaceString;

	/**
	 * Our search listener, cached so we can grab its selected text easily.
	 */
	protected SearchListener searchListener;


	/**
	 * Creates a new <code>ReplaceDialog</code>.
	 *
	 * @param owner The main window that owns this dialog.
	 * @param listener The component that listens for {@link SearchEvent}s.
	 */
	public ReplaceDialog(Dialog owner, SearchListener listener) {
		super(owner);
		init(listener);
	}


	/**
	 * Creates a new <code>ReplaceDialog</code>.
	 *
	 * @param owner The main window that owns this dialog.
	 * @param listener The component that listens for {@link SearchEvent}s.
	 */
	public ReplaceDialog(Frame owner, SearchListener listener) {
		super(owner);
		init(listener);
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if (SearchEvent.Type.REPLACE.name().equals(command) ||
				SearchEvent.Type.REPLACE_ALL.name().equals(command)) {

			context.setSearchFor(getSearchString());
			context.setReplaceWith(replaceWithCombo.getSelectedString());

			JTextComponent tc = UIUtil.getTextComponent(findTextCombo);
			findTextCombo.addItem(tc.getText());

			tc = UIUtil.getTextComponent(replaceWithCombo);
			String replaceText = tc.getText();
			if (replaceText.length()!=0) {
                replaceWithCombo.addItem(replaceText);
            }

			// Let parent app know
			fireSearchEvent(SearchEvent.Type.valueOf(command), null);

		}

		else {
			super.actionPerformed(e);
			if (SearchEvent.Type.FIND.name().equals(command)) {
				handleToggleButtons(); // Replace button could toggle state
			}
		}

	}


	@Override
	protected void escapePressed() {
		// Workaround for the strange behavior (Java bug?) that sometimes
		// the Escape keypress "gets through" from the AutoComplete's
		// registered key Actions, and gets to this EscapableDialog, which
		// hides the entire dialog.  Reproduce by doing the following:
		//   1. In an empty find field, press Ctrl+Space
		//   2. Type "\\".
		//   3. Press Escape.
		// The entire dialog will hide, instead of the completion popup.
		// Further, bringing the Find dialog back up, the completion popup
		// will still be visible.
		if (replaceWithCombo.hideAutoCompletePopups()) {
			return;
		}
		super.escapePressed();
	}


	/**
	 * Returns the text on the "Replace" button.
	 *
	 * @return The text on the Replace button.
	 * @see #setReplaceButtonText
	 */
	public final String getReplaceButtonText() {
		return replaceButton.getText();
	}


	/**
	 * Returns the text on the "Replace All" button.
	 *
	 * @return The text on the Replace All button.
	 * @see #setReplaceAllButtonText
	 */
	public final String getReplaceAllButtonText() {
		return replaceAllButton.getText();
	}


	/**
	 * Returns the <code>java.lang.String</code> to replace with.
	 *
	 * @return The <code>String</code> the user wants to replace the text to
	 *         find with.
	 */
	public String getReplaceString() {
		String text = replaceWithCombo.getSelectedString();
		if (text==null) { // possible from JComboBox
			text = "";
		}
		return text;
	}


	/**
	 * Returns the label on the "Replace with" text field.
	 *
	 * @return The text on the "Replace with" text field.
	 * @see #setReplaceWithLabelText
	 */
	public final String getReplaceWithLabelText() {
		return replaceFieldLabel.getText();
	}


	/**
	 * Called when the regex checkbox is clicked.  Subclasses can override
	 * to add custom behavior, but should call the super implementation.
	 */
	@Override
	protected void handleRegExCheckBoxClicked() {
		super.handleRegExCheckBoxClicked();
		// "Content assist" support
		boolean b = regexCheckBox.isSelected();
		// Always true except when debugging.  findTextCombo done in parent
		replaceWithCombo.setAutoCompleteEnabled(b);
	}


	@Override
	protected void handleSearchContextPropertyChanged(PropertyChangeEvent e) {

		String prop = e.getPropertyName();

		if (SearchContext.PROPERTY_REPLACE_WITH.equals(prop)) {
			String newValue = (String)e.getNewValue();
			if (newValue==null) {
				newValue = "";
			}
			String oldValue = getReplaceString();
			// Prevents IllegalStateExceptions
			if (!newValue.equals(oldValue)) {
				setReplaceString(newValue);
			}
		}

		else {
			super.handleSearchContextPropertyChanged(e);
		}

	}


	@Override
	protected FindReplaceButtonsEnableResult handleToggleButtons() {

		FindReplaceButtonsEnableResult er = super.handleToggleButtons();
		boolean shouldReplace = er.getEnable();
		replaceAllButton.setEnabled(shouldReplace);

		// "Replace" is only enabled if text to search for is selected in
		// the UI.
		if (shouldReplace) {
			String text = searchListener.getSelectedText();
			shouldReplace = matchesSearchFor(text);
		}
		replaceButton.setEnabled(shouldReplace);

		return er;

	}


	/**
	 * Does replace dialog-specific initialization stuff.
	 *
	 * @param listener The component that listens for {@link SearchEvent}s.
	 */
	private void init(SearchListener listener) {

		this.searchListener = listener;

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		// Create a panel for the "Find what" and "Replace with" text fields.
		JPanel searchPanel = new JPanel(new SpringLayout());

		// Create listeners for the combo boxes.
		ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
		ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

		// Create the "Find what" text field.
		JTextComponent textField = UIUtil.getTextComponent(findTextCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Create the "Replace with" text field.
		replaceWithCombo = new SearchComboBox(null, true);
		textField = UIUtil.getTextComponent(replaceWithCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Create the "Replace with" label.
		replaceFieldLabel = UIUtil.newLabel(getBundle(), "ReplaceWith",
				replaceWithCombo);

		JPanel temp = new JPanel(new BorderLayout());
		temp.add(findTextCombo);
		AssistanceIconPanel aip = new AssistanceIconPanel(findTextCombo);
		temp.add(aip, BorderLayout.LINE_START);
		JPanel temp2 = new JPanel(new BorderLayout());
		temp2.add(replaceWithCombo);
		AssistanceIconPanel aip2 = new AssistanceIconPanel(replaceWithCombo);
		temp2.add(aip2, BorderLayout.LINE_START);

		// Orient things properly.
		if (orientation.isLeftToRight()) {
			searchPanel.add(findFieldLabel);
			searchPanel.add(temp);
			searchPanel.add(replaceFieldLabel);
			searchPanel.add(temp2);
		}
		else {
			searchPanel.add(temp);
			searchPanel.add(findFieldLabel);
			searchPanel.add(temp2);
			searchPanel.add(replaceFieldLabel);
		}

		UIUtil.makeSpringCompactGrid(searchPanel, 2, 2,	//rows, cols
											0,0,		//initX, initY
											6, 6);	//xPad, yPad

		// Make a panel containing the inherited search direction radio
		// buttons and the inherited search options.
		JPanel bottomPanel = new JPanel(new BorderLayout());
		temp = new JPanel(new BorderLayout());
		bottomPanel.setBorder(UIUtil.getEmpty5Border());
		temp.add(searchConditionsPanel, BorderLayout.LINE_START);
		temp.add(searchConditionsPanel, BorderLayout.LINE_START);
		temp2 = new JPanel(new BorderLayout());
		temp2.add(dirPanel, BorderLayout.NORTH);
		temp.add(temp2);
		bottomPanel.add(temp, BorderLayout.LINE_START);

		// Now, make a panel containing all the above stuff.
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(searchPanel);
		leftPanel.add(bottomPanel);

		// Make a panel containing the action buttons.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4,1, 5,5));
		ResourceBundle msg = getBundle();
		replaceButton = UIUtil.newButton(msg, "Replace");
		replaceButton.setActionCommand(SearchEvent.Type.REPLACE.name());
		replaceButton.addActionListener(this);
		replaceButton.setEnabled(false);
		replaceButton.setIcon(null);
		replaceButton.setToolTipText(null);
		replaceAllButton = UIUtil.newButton(msg, "ReplaceAll");
		replaceAllButton.setActionCommand(SearchEvent.Type.REPLACE_ALL.name());
		replaceAllButton.addActionListener(this);
		replaceAllButton.setEnabled(false);
		replaceAllButton.setIcon(null);
		replaceAllButton.setToolTipText(null);
		buttonPanel.add(findNextButton);
		buttonPanel.add(replaceButton);
		buttonPanel.add(replaceAllButton);
		buttonPanel.add(cancelButton);		// Defined in superclass.
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(buttonPanel, BorderLayout.NORTH);

		// Put it all together!
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
		contentPane.add(leftPanel);
		contentPane.add(rightPanel, BorderLayout.LINE_END);
		temp = new ResizableFrameContentPane(new BorderLayout());
		temp.add(contentPane, BorderLayout.NORTH);
		setContentPane(temp);
		getRootPane().setDefaultButton(findNextButton);
		setTitle(getString("ReplaceDialogTitle"));
		setResizable(true);
		pack();
		setLocationRelativeTo(getParent());

		setSearchContext(new SearchContext());
		addSearchListener(listener);

		applyComponentOrientation(orientation);

	}


    @Override
    public void setContentAssistImage(Image image) {
        super.setContentAssistImage(image);
        replaceWithCombo.setContentAssistImage(image);
    }


	/**
	 * Sets the text on the "Replace" button.
	 *
	 * @param text The text for the Replace button.
	 * @see #getReplaceButtonText
	 */
	public final void setReplaceButtonText(String text) {
		replaceButton.setText(text);
	}


	/**
	 * Sets the text on the "Replace All" button.
	 *
	 * @param text The text for the Replace All button.
	 * @see #getReplaceAllButtonText
	 */
	public final void setReplaceAllButtonText(String text) {
		replaceAllButton.setText(text);
	}


	/**
	 * Sets the label on the "Replace with" text field.
	 *
	 * @param text The text for the "Replace with" text field's label.
	 * @see #getReplaceWithLabelText
	 */
	public final void setReplaceWithLabelText(String text) {
		replaceFieldLabel.setText(text);
	}


	/**
	 * Sets the <code>java.lang.String</code> to replace with.
	 *
	 * @param newReplaceString The <code>String</code> to put into
	 *        the replace field.
	 */
	public void setReplaceString(String newReplaceString) {
		replaceWithCombo.addItem(newReplaceString);
	}


	/**
	 * Overrides <code>JDialog</code>'s <code>setVisible</code> method; decides
	 * whether or not buttons are enabled.
	 *
	 * @param visible Whether or not the dialog should be visible.
	 */
	@Override
	public void setVisible(boolean visible) {

		if (visible) {

			// Select text entered in the UI
			String text = searchListener.getSelectedText();
			if (text!=null) {
				findTextCombo.addItem(text);
			}

			String selectedItem = findTextCombo.getSelectedString();
			if (selectedItem==null || selectedItem.length()==0) {
				findNextButton.setEnabled(false);
				replaceButton.setEnabled(false);
				replaceAllButton.setEnabled(false);
			}
			else {
				handleToggleButtons();
			}

			super.setVisible(true);
			focusFindTextField();

		}

		else {
			super.setVisible(false);
		}

	}


	/**
	 * This method should be called whenever the <code>LookAndFeel</code> of
	 * the application changes.  This calls
	 * <code>SwingUtilities.updateComponentTreeUI(this)</code> and does
	 * other necessary things.<p>
	 * Note that this is <em>not</em> an override, as JDialogs don't have an
	 * <code>updateUI()</code> method.
	 */
	public void updateUI() {

		SwingUtilities.updateComponentTreeUI(this);
		pack();

		// Create listeners for the combo boxes.
		ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
		ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

		// Fix the Find What combo box's listeners.
		JTextComponent textField = UIUtil.getTextComponent(findTextCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Fix the Replace With combo box's listeners.
		textField = UIUtil.getTextComponent(replaceWithCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

	}


	/**
	 * Listens for changes in the text field (find search field).
	 */
	private class ReplaceDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			JTextComponent findWhatTextField =
					UIUtil.getTextComponent(findTextCombo);
			if (e.getDocument().equals(findWhatTextField.getDocument())) {
				handleToggleButtons();
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			JTextComponent findWhatTextField =
					UIUtil.getTextComponent(findTextCombo);
			if (e.getDocument().equals(findWhatTextField.getDocument()) &&
					e.getDocument().getLength()==0) {
				findNextButton.setEnabled(false);
				replaceButton.setEnabled(false);
				replaceAllButton.setEnabled(false);
			}
			else {
				handleToggleButtons();
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

	}


	/**
	 * Listens for the text fields gaining focus.
	 */
	private class ReplaceFocusAdapter extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {

			JTextComponent textField = (JTextComponent)e.getSource();
			textField.selectAll();

			if (textField==UIUtil.getTextComponent(findTextCombo)) {
				// Remember what it originally was, in case they tabbed out.
				lastSearchString = findTextCombo.getSelectedString();
			}
			else { // if (textField==getTextComponent(replaceWithComboBox)).
				// Remember what it originally was, in case they tabbed out.
				lastReplaceString = replaceWithCombo.getSelectedString();
			}

			// Replace button's state might need to be changed.
			handleToggleButtons();

		}

	}
}

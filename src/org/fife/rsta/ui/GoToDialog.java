/*
 * 11/13/2003
 *
 * GoToDialog.java - A dialog allowing you to skip to a specific line number.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


/**
 * A "Go To" dialog allowing you to go to a specific line number in an
 * instance of RSyntaxTextArea.<p>
 *
 * Example usage:
 * <pre>
 * GoToDialog dialog = new GoToDialog(window);
 * dialog.setMaxLineNumberAllowed(textArea.getLineCount());
 * dialog.setVisible(true);
 * int line = dialog.getLineNumber();
 * if (line &gt; 0) {
 *    try {
 *       textArea.setCaretPosition(textArea.getLineStartOffset(line-1));
 *    } catch (BadLocationException ble) {
 *       ble.printStackTrace(); // Never happens
 *    }
 * }
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GoToDialog extends EscapableDialog {

	private JButton okButton;
	private JButton cancelButton;
	private JTextField lineNumberField;
	private int maxLineNumberAllowed;	// Number of lines in the document.
	private int lineNumber;			// The line to go to, or -1 for Cancel.
	private String errorDialogTitle;

	private static final ResourceBundle MSG = ResourceBundle.getBundle("org.fife.rsta.ui.GoToDialog");


	/**
	 * Creates a new <code>GoToDialog</code>.
	 *
	 * @param owner The parent dialog.
	 */
	public GoToDialog(Dialog owner) {
		super(owner);
		init();
	}


	/**
	 * Creates a new <code>GoToDialog</code>.
	 *
	 * @param owner The parent window.
	 */
	public GoToDialog(Frame owner) {
		super(owner);
		init();
	}


	private void init() {

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		lineNumber = -1;
		maxLineNumberAllowed = 1; // Empty document has 1 line.
		Listener l = new Listener();

		// Set the main content pane for the "GoTo" dialog.
		JPanel contentPane = new ResizableFrameContentPane(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);

		// Make a panel containing the "Line Number" edit box.
		Box enterLineNumberPane = new Box(BoxLayout.LINE_AXIS);
		enterLineNumberPane.setBorder(BorderFactory.createEmptyBorder(
												0, 0, 20, 0));
		lineNumberField = new JTextField(16);
		lineNumberField.setText("1");
		AbstractDocument doc = (AbstractDocument)lineNumberField.getDocument();
		doc.addDocumentListener(l);
		doc.setDocumentFilter(new NumberDocumentFilter());
		JLabel label = UIUtil.newLabel(MSG, "LineNumber", lineNumberField);
		enterLineNumberPane.add(label);
		enterLineNumberPane.add(Box.createHorizontalStrut(15));
		enterLineNumberPane.add(lineNumberField);

		// Make a panel containing the OK and Cancel buttons.
		okButton = UIUtil.newButton(MSG, "OK");
		okButton.addActionListener(l);
		cancelButton = UIUtil.newButton(MSG, "Cancel");
		cancelButton.addActionListener(l);
		Container bottomPanel = createButtonPanel(okButton, cancelButton);

		// Put everything into a neat little package.
		contentPane.add(enterLineNumberPane, BorderLayout.NORTH);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		JRootPane rootPane = getRootPane();
		rootPane.setDefaultButton(okButton);
		setTitle(MSG.getString("GotoDialogTitle"));
		setModal(true);
		applyComponentOrientation(orientation);
		pack();
		setLocationRelativeTo(getParent());

	}


	/**
	 * Called when they've clicked OK or pressed Enter; check the line number
	 * they entered for validity and if it's okay, close this dialog.  If it
	 * isn't okay, display an error message.
	 *
	 * @return Whether the line number was valid.  In this case, this dialog
	 *         will be hidden when this method returns.
	 */
	private boolean attemptToGetGoToLine() {

		try {

			lineNumber = Integer.parseInt(lineNumberField.getText());

			if (lineNumber<1 || lineNumber>maxLineNumberAllowed) {
				lineNumber = -1;
				throw new NumberFormatException();
			}

		} catch (NumberFormatException nfe) {
			displayInvalidLineNumberMessage();
			return false;
		}

		// If we have a valid line number, close the dialog!
		setVisible(false);
		return true;

	}


	/**
	 * Returns a panel containing the OK and Cancel buttons.  This panel is
	 * added to the bottom of this dialog.  Applications that don't like these
	 * buttons right-aligned in the dialog can override this method to change
	 * that behavior.
	 *
	 * @param ok The OK button.
	 * @param cancel The Cancel button.
	 * @return A panel containing the two buttons.
	 */
	protected Container createButtonPanel(JButton ok, JButton cancel) {
		JPanel buttonPanel = new JPanel(new GridLayout(1,2, 5,5));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(buttonPanel, BorderLayout.LINE_END);
		return bottomPanel;
	}


	/**
	 * Displays a message to the user that they have entered an invalid line
	 * number.  The default implementation displays the error message in a
	 * modal.  Subclasses that wish to have a slicker error delivery mechanism
	 * can override.
	 */
	protected void displayInvalidLineNumberMessage() {
		JOptionPane.showMessageDialog(this,
				MSG.getString("LineNumberRange") + maxLineNumberAllowed + ".",
				getErrorDialogTitle(),
				JOptionPane.ERROR_MESSAGE);
	}


	/**
	 * Called when the user clicks Cancel or hits the Escape key.  This
	 * hides the dialog.
	 */
	@Override
	protected void escapePressed() {
		lineNumber = -1;
		super.escapePressed();
	}


	/**
	 * Returns the title for the error dialog.
	 *
	 * @return The title for the error dialog.
	 * @see #setErrorDialogTitle(String)
	 */
	public String getErrorDialogTitle() {
		String title = errorDialogTitle;
		if (title==null) {
			title = MSG.getString("ErrorDialog.Title");
		}
		return title;
	}


	/**
	 * Gets the line number the user entered to go to.
	 *
	 * @return The line number the user decided to go to, or <code>-1</code>
	 *         if the dialog was canceled.  If valid, this will be 1-based,
	 *         not 0-based.
	 */
	public int getLineNumber() {
		return lineNumber;
	}


	/**
	 * Returns the maximum line number the user is allowed to enter.
	 *
	 * @return the maximum line number allowed.
	 * @see #setMaxLineNumberAllowed(int)
	 */
	public int getMaxLineNumberAllowed() {
		return maxLineNumberAllowed;
	}


	/**
	 * Sets the title for the error dialog.
	 *
	 * @param title The new title.  If this is <code>null</code>, a default
	 *        value will be used.
	 * @see #getErrorDialogTitle()
	 */
	public void setErrorDialogTitle(String title) {
		this.errorDialogTitle = title;
	}


	/**
	 * Sets the maximum line number for them to enter.
	 *
	 * @param max The new maximum line number value.
	 * @see #getMaxLineNumberAllowed()
	 */
	public void setMaxLineNumberAllowed(int max) {
		this.maxLineNumberAllowed = max;
	}


	/**
	 * Overrides <code>JDialog</code>'s <code>setVisible</code> method; decides
	 * whether or not buttons are enabled if the user is enabling the dialog.
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			lineNumber = -1;
			okButton.setEnabled(lineNumberField.getDocument().getLength()>0);
			SwingUtilities.invokeLater(() -> {
				lineNumberField.requestFocusInWindow();
				lineNumberField.selectAll();
			});
		}
		super.setVisible(visible);
	}


	/**
	 * Listens for events in this dialog.
	 */
	private class Listener implements ActionListener, DocumentListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (okButton==source) {
				attemptToGetGoToLine();
			}
			else if (cancelButton==source) {
				escapePressed();
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			okButton.setEnabled(lineNumberField.getDocument().getLength()>0);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			okButton.setEnabled(lineNumberField.getDocument().getLength()>0);
		}

	}


	/**
	 * A document filter that only lets the user enter digits.
	 */
	private class NumberDocumentFilter extends DocumentFilter {

		private String fix(String str) {
			if (str!=null) {
				int origLength = str.length();
				for (int i=0; i<str.length(); i++) {
					if (!Character.isDigit(str.charAt(i))) {
						str = str.substring(0, i) + str.substring(i+1);
						i--;
					}
				}
				if (origLength!=str.length()) {
					UIManager.getLookAndFeel().provideErrorFeedback(
							GoToDialog.this);
				}
			}
			return str;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string,
				AttributeSet attr) throws BadLocationException {
			fb.insertString(offset, fix(string), attr);
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset,
				int length, String text, AttributeSet attr)
						throws BadLocationException {
			fb.replace(offset, length, fix(text), attr);
		}

	}


}

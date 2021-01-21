/*
 * 12/08/2004
 *
 * TextFilePropertiesDialog.java - Dialog allowing you to view/edit a
 * text file's properties.
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
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;


/**
 * A dialog that displays the properties of an individual text file being
 * edited by a {@link org.fife.ui.rsyntaxtextarea.TextEditorPane}.  Some
 * properties can be modified directly from this dialog.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public class TextFilePropertiesDialog extends EscapableDialog
								implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField filePathField;
	private JComboBox<String> terminatorCombo;
	private JComboBox<String> encodingCombo;
	private JButton okButton;

	private TextEditorPane textArea;

	private static final ResourceBundle MSG = ResourceBundle.getBundle(
							"org.fife.rsta.ui.TextFilePropertiesDialog");

	private static final String[] LINE_TERMINATOR_LABELS = {
		MSG.getString("SysDef"),
		MSG.getString("CR"),
		MSG.getString("LF"),
		MSG.getString("CRLF"),
	};

	private static final String[] LINE_TERMINATORS = {
		System.getProperty("line.separator"), "\r", "\n", "\r\n"
	};

	/**
	 * Constructor.
	 *
	 * @param parent The main application dialog.
	 * @param textArea The text area on which to report.
	 */
	public TextFilePropertiesDialog(Dialog parent, TextEditorPane textArea) {
		super(parent);
		init(textArea);
	}


	/**
	 * Constructor.
	 *
	 * @param parent The main application window.
	 * @param textArea The text area on which to report.
	 */
	public TextFilePropertiesDialog(Frame parent, TextEditorPane textArea) {
		super(parent);
		init(textArea);
	}


	/**
	 * Listens for actions in this dialog.
	 *
	 * @param e The action event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("TerminatorComboBox".equals(command)) {
			okButton.setEnabled(true);
		}

		else if ("encodingCombo".equals(command)) {
			okButton.setEnabled(true);
		}

		else if ("OKButton".equals(command)) {
			String terminator = getSelectedLineTerminator();
			if (terminator!=null) {
				String old = (String)textArea.getLineSeparator();
				if (!terminator.equals(old)) {
					textArea.setLineSeparator(terminator);
				}
			}
			String encoding = (String)encodingCombo.getSelectedItem();
			if (encoding!=null) {
				textArea.setEncoding(encoding);
			}
			setVisible(false);
		}

		else if ("CancelButton".equals(command)) {
			escapePressed();
		}

	}


	private int calculateWordCount(TextEditorPane textArea) {

		int wordCount = 0;
		RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();

		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(new DocumentCharIterator(textArea.getDocument()));
		for (int nextBoundary=bi.first(); nextBoundary!=BreakIterator.DONE;
				nextBoundary=bi.next()) {
			// getWordInstance() returns boundaries for both words and
			// non-words (whitespace, punctuation, etc.)
			try {
				char ch = doc.charAt(nextBoundary);
				if (Character.isLetterOrDigit(ch)) {
					wordCount++;
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}

		return wordCount;

	}


	/**
	 * Creates a "footer" component containing the OK and Cancel buttons.
	 *
	 * @param ok The OK button.
	 * @param cancel The Cancel button.
	 * @return The footer component for the dialog.
	 */
	protected Container createButtonFooter(JButton ok, JButton cancel) {

		JPanel buttonPanel = new JPanel(new GridLayout(1,2, 5,5));
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		JPanel panel = new JPanel(new BorderLayout());
		ComponentOrientation o = getComponentOrientation();
		final int padding = 8;
		int left = o.isLeftToRight() ? 0 : padding;
		int right = o.isLeftToRight() ? padding : 0;
		panel.setBorder(BorderFactory.createEmptyBorder(10, left, 0, right));
		panel.add(buttonPanel, BorderLayout.LINE_END);
		return panel;

	}


	/**
	 * Returns the title to use for this dialog.
	 *
	 * @param fileName The name of the file whose properties are being shown.
	 * @return The title for this dialog.
	 */
	protected String createTitle(String fileName) {
		return MessageFormat.format(
			MSG.getString("Title"), textArea.getFileName());
	}


	/**
	 * Returns a string representation of a file size, such as "842 bytes",
	 * "1.73 KB" or "3.4 MB".
	 *
	 * @param file The file to get the size of.
	 * @return The string.
	 */
	private static String getFileSizeStringFor(File file) {

		int count = 0;
		double tempSize = file.length();
		double prevSize = tempSize;

		// Keep dividing by 1024 until you get the largest unit that goes
		// into this file's size.
		while (count<4 && ((tempSize = prevSize/1024f)>=1)) {
			prevSize = tempSize;
			count++;
		}

		String suffix;
		switch (count) {
			case 0:
			    suffix = "bytes";
			    break;
			case 1:
			    suffix = "KB";
			    break;
			case 2:
			    suffix = "MB";
			    break;
			case 3:
			    suffix = "GB";
			    break;
			case 4:
            default: // SpotBugs, never happens
			    suffix = "TB";
			    break;
		}

		NumberFormat fileSizeFormat = NumberFormat.getNumberInstance();
		fileSizeFormat.setGroupingUsed(true);
		fileSizeFormat.setMinimumFractionDigits(0);
		fileSizeFormat.setMaximumFractionDigits(1);
		return fileSizeFormat.format(prevSize) + " " + suffix;

	}


	private String getSelectedLineTerminator() {
		return LINE_TERMINATORS[terminatorCombo.getSelectedIndex()];
	}


	private void init(TextEditorPane textArea) {

		this.textArea = textArea;
		setTitle(createTitle(textArea.getFileName()));

		ComponentOrientation o = ComponentOrientation.
									getOrientation(getLocale());

		JPanel contentPane = new ResizableFrameContentPane(new BorderLayout());
		contentPane.setBorder(UIUtil.getEmpty5Border());

		// Where we actually add our content.
		JPanel content2 = new JPanel();
		content2.setLayout(new SpringLayout());
		contentPane.add(content2, BorderLayout.NORTH);

		filePathField = new JTextField(40);
		filePathField.setText(textArea.getFileFullPath());
		filePathField.setEditable(false);
		JLabel filePathLabel = UIUtil.newLabel(MSG, "Path", filePathField);

		JLabel linesLabel = new JLabel(MSG.getString("Lines"));
		JLabel linesCountLabel = new JLabel(
								Integer.toString(textArea.getLineCount()));

		JLabel charsLabel = new JLabel(MSG.getString("Characters"));
		JLabel charsCountLabel = new JLabel(
						Integer.toString(textArea.getDocument().getLength()));

		JLabel wordsLabel = new JLabel(MSG.getString("Words"));
		JLabel wordsCountLabel = new JLabel(
				Integer.toString(calculateWordCount(textArea)));

		terminatorCombo = new JComboBox<>(LINE_TERMINATOR_LABELS);
		if (textArea.isReadOnly()) {
			terminatorCombo.setEnabled(false);
		}
		UIUtil.fixComboOrientation(terminatorCombo);
		setSelectedLineTerminator((String)textArea.getLineSeparator());
		terminatorCombo.setActionCommand("TerminatorComboBox");
		terminatorCombo.addActionListener(this);
		JLabel terminatorLabel = UIUtil.newLabel(MSG, "LineTerminator",
				terminatorCombo);

		encodingCombo = new JComboBox<>();
		if (textArea.isReadOnly()) {
			encodingCombo.setEnabled(false);
		}
		UIUtil.fixComboOrientation(encodingCombo);

		// Populate the combo box with all available encodings.
		Map<String, Charset> availableCharsets = Charset.availableCharsets();
		Set<String> charsetNames = availableCharsets.keySet();
		for (String charsetName : charsetNames) {
			encodingCombo.addItem(charsetName);
		}
		setEncoding(textArea.getEncoding());
		encodingCombo.setActionCommand("encodingCombo");
		encodingCombo.addActionListener(this);
		JLabel encodingLabel = UIUtil.newLabel(MSG, "Encoding", encodingCombo);

		JLabel sizeLabel = new JLabel(MSG.getString("FileSize"));
		File file = new File(textArea.getFileFullPath());
		String size = "";
		if (file.exists() && !file.isDirectory()) {
			size = getFileSizeStringFor(file);
		}
		JLabel sizeLabel2 = new JLabel(size);

		long temp = textArea.getLastSaveOrLoadTime();
		String modifiedString;
		if (temp<=0) { // 0 or -1, can be either
			modifiedString = "";
		}
		else {
			Date modifiedDate = new Date(temp);
			SimpleDateFormat sdf = new SimpleDateFormat(
					"hh:mm a  EEE, MMM d, yyyy");
			modifiedString = sdf.format(modifiedDate);
		}
		JLabel modifiedLabel = new JLabel(MSG.getString("LastModified"));
		JLabel modified = new JLabel(modifiedString);

		if (o.isLeftToRight()) {
			content2.add(filePathLabel);     content2.add(filePathField);
			content2.add(linesLabel);        content2.add(linesCountLabel);
			content2.add(charsLabel);        content2.add(charsCountLabel);
			content2.add(wordsLabel);        content2.add(wordsCountLabel);
			content2.add(terminatorLabel);   content2.add(terminatorCombo);
			content2.add(encodingLabel);     content2.add(encodingCombo);
			content2.add(sizeLabel);         content2.add(sizeLabel2);
			content2.add(modifiedLabel);     content2.add(modified);
		}
		else {
			content2.add(filePathField);     content2.add(filePathLabel);
			content2.add(linesCountLabel);   content2.add(linesLabel);
			content2.add(charsCountLabel);   content2.add(charsLabel);
			content2.add(wordsCountLabel);   content2.add(wordsLabel);
			content2.add(terminatorCombo);   content2.add(terminatorLabel);
			content2.add(encodingCombo);     content2.add(encodingLabel);
			content2.add(sizeLabel2);        content2.add(sizeLabel);
			content2.add(modified);          content2.add(modifiedLabel);
		}

		UIUtil.makeSpringCompactGrid(content2, 8,2, 0,0, 5,5);

		// Make a panel for OK and cancel buttons.
		okButton = UIUtil.newButton(MSG, "OK");
		okButton.setActionCommand("OKButton");
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		JButton cancelButton = UIUtil.newButton(MSG, "Cancel");
		cancelButton.setActionCommand("CancelButton");
		cancelButton.addActionListener(this);
		Container buttons = createButtonFooter(okButton, cancelButton);
		contentPane.add(buttons, BorderLayout.SOUTH);

		setContentPane(contentPane);
		setModal(true);
		applyComponentOrientation(o);
		pack();
		setLocationRelativeTo(getParent());

	}


	/**
	 * Sets the encoding selected by this dialog.
	 *
	 * @param encoding The desired encoding.  If this value is invalid or not
	 *        supported by this OS, <code>US-ASCII</code> is used.
	 */
	private void setEncoding(String encoding) {

		Charset cs1 = Charset.forName(encoding);

		int count = encodingCombo.getItemCount();
		for (int i=0; i<count; i++) {
			String item = encodingCombo.getItemAt(i);
			Charset cs2 = Charset.forName(item);
			if (cs1.equals(cs2)) {
				encodingCombo.setSelectedIndex(i);
				return;
			}
		}

		// Encoding not found: select default.
		cs1 = StandardCharsets.US_ASCII;
		for (int i=0; i<count; i++) {
			String item = encodingCombo.getItemAt(i);
			Charset cs2 = Charset.forName(item);
			if (cs1.equals(cs2)) {
				encodingCombo.setSelectedIndex(i);
				return;
			}
		}

	}


	private void setSelectedLineTerminator(String terminator) {
		for (int i=0; i<LINE_TERMINATORS.length; i++) {
			if (LINE_TERMINATORS[i].equals(terminator)) {
				terminatorCombo.setSelectedIndex(i);
				break;
			}
		}
	}


	/**
	 * Overridden to focus the file path text field and select its contents
	 * when this dialog is made visible.
	 *
	 * @param visible Whether this dialog should be made visible.
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			SwingUtilities.invokeLater(() -> {
				filePathField.requestFocusInWindow();
				filePathField.selectAll();
			});
		}
		super.setVisible(visible);
	}


    /**
     * Iterates over each character in a document.
     */
	private static class DocumentCharIterator implements CharacterIterator {

		private Document doc;
		private int index;
		private Segment s;

		DocumentCharIterator(Document doc) {
			this.doc = doc;
			index = 0;
			s = new Segment();
		}

		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException cnse) { // Never happens
				throw new InternalError("Clone not supported???");
			}
		}

		@Override
		public char current() {
			if (index>=getEndIndex()) {
				return DONE;
			}
			try {
				doc.getText(index, 1, s);
				return s.first();
			} catch (BadLocationException ble) {
				return DONE;
			}
		}

		@Override
		public char first() {
			index = getBeginIndex();
			return current();
		}

		@Override
		public int getBeginIndex() {
			return 0;
		}

		@Override
		public int getEndIndex() {
			return doc.getLength();
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public char last() {
			index = Math.max(0, getEndIndex() - 1);
			return current();
		}

		@Override
		public char next() {
			index = Math.min(index+1, getEndIndex());
			return current();
		}

		@Override
		public char previous() {
			index = Math.max(index-1, getBeginIndex());
			return current();
		}

		@Override
		public char setIndex(int pos) {
			if (pos<getBeginIndex() || pos>getEndIndex()) {
				throw new IllegalArgumentException("Illegal index: " + index);
			}
			index = pos;
			return current();
		}

	}


}

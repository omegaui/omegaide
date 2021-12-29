/*
* 09/20/2013
*
* ReplaceToolBar - A tool bar for "replace" operations in text areas.
*
* This library is distributed under a modified BSD license.  See the included
* RSTAUI.license.txt file for details.
*/
package org.fife.rsta.ui.search;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;


/**
* A toolbar for replace operations in a text editor application.  This provides
* a more seamless experience than using a Find or Replace dialog.
*
* @author Robert Futrell
* @version 0.5
* @see FindToolBar
* @see ReplaceDialog
*/
public class ReplaceToolBar extends FindToolBar {
	
	private JButton replaceButton;
	private JButton replaceAllButton;
	
	/**
	* Our search listener, cached so we can grab its selected text easily.
	*/
	protected SearchListener searchListener;
	
	
	/**
	* Creates the tool bar.
	*
	* @param listener An entity listening for search events.
	*/
	public ReplaceToolBar(SearchListener listener) {
		super(listener);
		this.searchListener = listener;
	}
	
	
	@Override
	public void addNotify() {
		super.addNotify();
		handleToggleButtons();
	}
	
	
	@Override
	protected Container createButtonPanel() {
		
		Box panel = new Box(BoxLayout.LINE_AXIS);
		
		JPanel bp = new JPanel(new GridLayout(2, 2, 5,5));
		bp.setBackground(omega.io.UIManager.c2);
		panel.add(bp);
		
		createFindButtons();
		
		Component filler = Box.createRigidArea(new Dimension(5, 5));
		
		bp.add(findButton);       
		bp.add(replaceButton);
		bp.add(replaceAllButton); 
		bp.add(filler);
		panel.add(bp);
		
		JPanel optionPanel = new JPanel(new SpringLayout());
		optionPanel.setBackground(omega.io.UIManager.c2);
		matchCaseCheckBox = createCB("MatchCase");
		regexCheckBox = createCB("RegEx");
		wholeWordCheckBox = createCB("WholeWord");
		markAllCheckBox = createCB("MarkAll");
		wrapCheckBox = createCB("Wrap");
		// We use a "spacing" middle row, instead of spacing in the call to
		// UIUtil.makeSpringCompactGrid(), as the latter adds trailing
		// spacing after the final "row", which screws up our alignment.
		Dimension spacing = new Dimension(1, 5);
		Component space1 = Box.createRigidArea(spacing);
		Component space2 = Box.createRigidArea(spacing);
		Component space3 = Box.createRigidArea(spacing);
		Component space4 = Box.createRigidArea(spacing);
		
		ComponentOrientation orientation = ComponentOrientation.
		getOrientation(getLocale());
		
		if (orientation.isLeftToRight()) {
			optionPanel.add(matchCaseCheckBox); optionPanel.add(wholeWordCheckBox); optionPanel.add(wrapCheckBox);
			optionPanel.add(space1);            optionPanel.add(space2);            optionPanel.add(space3);
			optionPanel.add(regexCheckBox);     optionPanel.add(markAllCheckBox);   optionPanel.add(space4);
		}
		else {
			optionPanel.add(wrapCheckBox); optionPanel.add(wholeWordCheckBox); optionPanel.add(matchCaseCheckBox);
			optionPanel.add(space3);       optionPanel.add(space2);            optionPanel.add(space1);
			optionPanel.add(space4);       optionPanel.add(markAllCheckBox);   optionPanel.add(regexCheckBox);
		}
		UIUtil.makeSpringCompactGrid(optionPanel, 3,3, 0,0, 0,0);
		panel.add(optionPanel);
		
		return panel;
		
	}
	
	
	@Override
	protected Container createFieldPanel() {
		
		findFieldListener = new ReplaceFindFieldListener();
		
		JPanel temp = new JPanel(new SpringLayout());
		temp.setBackground(omega.io.UIManager.c2);
		
		JLabel findLabel = new JLabel(MSG.getString("FindWhat"));
		findLabel.setBackground(omega.io.UIManager.c2);
		JLabel replaceLabel = new JLabel(MSG.getString("ReplaceWith"));
		replaceLabel.setBackground(omega.io.UIManager.c2);
		
		findCombo = new SearchComboBox(this, false);
		findCombo.setBackground(omega.io.UIManager.c2);
		findCombo.setFont(omega.io.UIManager.PX14);
		JTextComponent findField = UIUtil.getTextComponent(findCombo);
		findFieldListener.install(findField);
		findField.setFont(omega.io.UIManager.PX14);
		Container fcp = createContentAssistablePanel(findCombo);
		
		replaceCombo = new SearchComboBox(this, true);
		replaceCombo.setBackground(omega.io.UIManager.c2);
		replaceCombo.setFont(omega.io.UIManager.PX14);
		JTextComponent replaceField = UIUtil.getTextComponent(replaceCombo);
		replaceField.setFont(omega.io.UIManager.PX14);
		findFieldListener.install(replaceField);
		Container rcp = createContentAssistablePanel(replaceCombo);
		
		// We use a "spacing" middle row, instead of spacing in the call to
		// UIUtil.makeSpringCompactGrid(), as the latter adds trailing
		// spacing after the final "row", which screws up our alignment.
		Dimension spacing = new Dimension(1, 5);
		Component space1 = Box.createRigidArea(spacing);
		Component space2 = Box.createRigidArea(spacing);
		
		if (getComponentOrientation().isLeftToRight()) {
			temp.add(findLabel);     temp.add(fcp);
			temp.add(space1);        temp.add(space2);
			temp.add(replaceLabel);  temp.add(rcp);
		}
		else {
			temp.add(fcp);    temp.add(findLabel);
			temp.add(space2); temp.add(space1);
			temp.add(rcp);    temp.add(replaceLabel);
		}
		UIUtil.makeSpringCompactGrid(temp, 3,2, 0,0, 0,0);
		
		return temp;
	}
	
	
	@Override
	protected void createFindButtons() {
		
		super.createFindButtons();
		
		replaceButton = new JButton(SEARCH_MSG.getString("Replace"));
		makeEnterActivateButton(replaceButton);
		replaceButton.setToolTipText(MSG.getString("Replace.ToolTip"));
		replaceButton.setActionCommand("Replace");
		replaceButton.addActionListener(listener);
		replaceButton.setEnabled(false);
		
		
		replaceAllButton = new JButton(SEARCH_MSG.getString("ReplaceAll"));
		makeEnterActivateButton(replaceAllButton);
		replaceAllButton.setActionCommand("ReplaceAll");
		replaceAllButton.addActionListener(listener);
		replaceAllButton.setEnabled(false);
		
	}
	
	
	/**
	* Called when the regex checkbox is clicked (or its value is modified
	* via a change to the search context).  Subclasses can override
	* to add custom behavior, but should call the super implementation.
	*/
	@Override
	protected void handleRegExCheckBoxClicked() {
		super.handleRegExCheckBoxClicked();
		// "Content assist" support
		boolean b = regexCheckBox.isSelected();
		replaceCombo.setAutoCompleteEnabled(b);
	}
	
	
	@Override
	protected void handleSearchAction(ActionEvent e) {
		String command = e.getActionCommand();
		super.handleSearchAction(e);
		if ("FindNext".equals(command) || "FindPrevious".equals(command)) {
			handleToggleButtons(); // Replace button could toggle state
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
	
	
	private boolean matchesSearchFor(String text) {
		if (text==null || text.length()==0) {
			return false;
		}
		String searchFor = findCombo.getSelectedString();
		if (searchFor!=null && searchFor.length()>0) {
			boolean matchCase = matchCaseCheckBox.isSelected();
			if (regexCheckBox.isSelected()) {
				int flags = Pattern.MULTILINE; // '^' and '$' are done per line.
				flags = RSyntaxUtilities.getPatternFlags(matchCase, flags);
				Pattern pattern;
				try {
					pattern = Pattern.compile(searchFor, flags);
					} catch (PatternSyntaxException pse) {
					pse.printStackTrace(); // Never happens
					return false;
				}
				return pattern.matcher(text).matches();
			}
			else {
				if (matchCase) {
					return searchFor.equals(text);
				}
				return searchFor.equalsIgnoreCase(text);
			}
		}
		return false;
	}
	
	
	/**
	* Overridden to possibly toggle the enabled state of the replace button.
	*/
	@Override
	public boolean requestFocusInWindow() {
		boolean result = super.requestFocusInWindow();
		handleToggleButtons(); // Replace button state may change
		return result;
	}
	
	@Override
	public void setContentAssistImage(Image image) {
		super.setContentAssistImage(image);
		replaceCombo.setContentAssistImage(image);
	}
	
	
	/**
	* Listens for the user typing into the search field.
	*/
	protected class ReplaceFindFieldListener extends FindFieldListener {
		
		@Override
		protected void handleDocumentEvent(DocumentEvent e) {
			super.handleDocumentEvent(e);
			JTextComponent findField = UIUtil.getTextComponent(findCombo);
			JTextComponent replaceField = UIUtil.getTextComponent(replaceCombo);
			if (e.getDocument().equals(findField.getDocument())) {
				handleToggleButtons();
			}
			if (e.getDocument()==replaceField.getDocument()) {
				getSearchContext().setReplaceWith(replaceField.getText());
			}
		}
		
	}
	
	
}

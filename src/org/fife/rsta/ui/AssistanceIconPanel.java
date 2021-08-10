/*
 * 06/13/2009
 *
 * AssistanceIconPanel.java - A panel that sits alongside a text component,
 * that can display assistance icons for that component.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.search.AbstractSearchDialog;


/**
 * A panel meant to be displayed alongside a text component, that can display
 * assistance icons for that text component.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class AssistanceIconPanel extends DecorativeIconPanel
						implements PropertyChangeListener {

	/**
	 * The tool tip text for the light bulb icon.  It is assumed that access
	 * to this field is single-threaded (on the EDT).
	 */
	private static String assistanceAvailable;


    /**
     * Constructor.
     *
     * @param comp The component to listen to.  This can be <code>null</code>
     *        to create a "filler" icon panel for alignment purposes.
     */
    public AssistanceIconPanel(JComponent comp) {
        init(comp);
    }


	/**
	 * Constructor.
	 *
	 * @param comp The component to listen to.  This can be <code>null</code>
	 *        to create a "filler" icon panel for alignment purposes.
     * @param iconWidth The with of decorative icons that will be added to
     *        this panel.  This is used to ensure the proper amount of padding
     *        when no icon is being shown.
	 */
	public AssistanceIconPanel(JComponent comp, int iconWidth) {
        super(iconWidth);
        init(comp);
    }

    private void init(JComponent comp) {

		// null can be passed to make a "filler" icon panel for alignment
		// purposes.
		if (comp!=null) {

			ComponentListener listener = new ComponentListener();

			if (comp instanceof JComboBox) {
				JComboBox<?> combo = (JComboBox<?>)comp;
				Component c = combo.getEditor().getEditorComponent();
				if (c instanceof JTextComponent) { // Always true
					JTextComponent tc = (JTextComponent)c;
					tc.addFocusListener(listener);
				}
			}
			else { // Usually a JTextComponent
				comp.addFocusListener(listener);
			}

			comp.addPropertyChangeListener(
				ContentAssistable.ASSISTANCE_IMAGE, this);

		}

	}


	/**
	 * Returns the "Content Assist Available" tool tip text for the light bulb
	 * icon.  It is assumed that this method is only called on the EDT.
	 *
	 * @return The text.
	 */
	static String getAssistanceAvailableText() {
		if (assistanceAvailable ==null) {
			assistanceAvailable = AbstractSearchDialog.getString("ContentAssistAvailable");
		}
		return assistanceAvailable;
	}


	/**
	 * Called when the property
	 * {@link ContentAssistable#ASSISTANCE_IMAGE} is fired by the component
	 * we are listening to.
	 *
	 * @param e The change event.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		Image img = (Image)e.getNewValue();
		setAssistanceEnabled(img);
	}


	/**
	 * A hook for applications to initialize this panel, if the component
	 * we're listening to already has content assist enabled.
	 *
	 * @param img The image to display, or <code>null</code> if content assist
	 *        is not currently available.
	 */
	public void setAssistanceEnabled(Image img) {
		if (img==null) {
			setIcon(null);
			setToolTipText(null);
		}
		else {
			setIcon(new ImageIcon(img));
			setToolTipText(getAssistanceAvailableText());
		}
	}


	/**
	 * Listens for events in the text component we're annotating.
	 */
	private class ComponentListener implements FocusListener {

		/**
		 * Called when the combo box or text component gains focus.
		 *
		 * @param e The focus event.
		 */
		@Override
		public void focusGained(FocusEvent e) {
			setShowIcon(true);
		}

		/**
		 * Called when the combo box or text component loses focus.
		 *
		 * @param e The focus event.
		 */
		@Override
		public void focusLost(FocusEvent e) {
			setShowIcon(false);
		}
	}
}

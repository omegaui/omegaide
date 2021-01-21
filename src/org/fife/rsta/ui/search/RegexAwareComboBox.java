/*
 * 01/12/2009
 *
 * RegexAwareComboBox.java - A combo box that offers autocomplete assistance
 * for regular expressions.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import javax.swing.ComboBoxModel;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.ContentAssistable;
import org.fife.rsta.ui.MaxWidthComboBox;
import org.fife.rsta.ui.RComboBoxModel;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import java.awt.*;


/**
 * A combo box that offers content assistance for regular expressions.
 *
 * @param <E> The type of item in the combo box.
 * @author Robert Futrell
 * @version 1.0
 */
public class RegexAwareComboBox<E> extends MaxWidthComboBox<E>
		implements ContentAssistable {

	private boolean enabled;
	private boolean replace;
	private AutoCompletion ac;
	private RegexAwareProvider provider;
	private Image contentAssistImage;


	/**
	 * Constructor.
	 *
	 * @param replace Whether this is a "replace" combo box (as opposed to a
	 *        "find" combo box).  This dictates what auto-complete choices the
	 *        user is offered.
	 */
	public RegexAwareComboBox(boolean replace) {
		this(new RComboBoxModel<>(), 200, replace);
	}


	/**
	 * Constructor.
	 *
	 * @param model The combo box's model.
	 * @param maxWidth The maximum width for this combo box.
	 * @param replace Whether this is a "replace" combo box (as opposed to a
	 *        "find" combo box).  This dictates what auto-complete choices the
	 *        user is offered.
	 */
	public RegexAwareComboBox(ComboBoxModel<E> model, int maxWidth,
								boolean replace) {
		super(model, maxWidth);
		setEditable(true);
		this.replace = replace;
	}


	/**
	 * Adds the completion choices for regexes in a "find" text field.
	 *
	 * @param p The completion provider to add to.
	 * @see #addReplaceFieldCompletions(RegexAwareProvider)
	 */
	private void addFindFieldCompletions(RegexAwareProvider p) {

		// Characters
		p.addCompletion(new RegexCompletion(p, "\\\\", "\\\\", "\\\\ - Backslash"));
		p.addCompletion(new RegexCompletion(p, "\\t", "\\t", "\\t - Tab"));
		p.addCompletion(new RegexCompletion(p, "\\n", "\\n", "\\n - Newline"));

		// Character classes
		p.addCompletion(new RegexCompletion(p, "[",   "[",   "[abc] - Any of a, b, or c"));
		p.addCompletion(new RegexCompletion(p, "[^",  "[^",  "[^abc] - Any character except a, b, or c"));

		// Predefined character classes
		p.addCompletion(new RegexCompletion(p, ".",   ".",   ". - Any character"));
		p.addCompletion(new RegexCompletion(p, "\\d", "\\d", "\\d - A digit"));
		p.addCompletion(new RegexCompletion(p, "\\D", "\\D", "\\D - Not a digit"));
		p.addCompletion(new RegexCompletion(p, "\\s", "\\s", "\\s - A whitespace"));
		p.addCompletion(new RegexCompletion(p, "\\S", "\\S", "\\S - Not a whitespace"));
		p.addCompletion(new RegexCompletion(p, "\\w", "\\w", "\\w - An alphanumeric (word character)"));
		p.addCompletion(new RegexCompletion(p, "\\W", "\\W", "\\W - Not an alphanumeric"));

		// Boundary matchers
		p.addCompletion(new RegexCompletion(p, "^", "^", "^ - Line Start"));
		p.addCompletion(new RegexCompletion(p, "$", "$", "$ - Line End"));
		p.addCompletion(new RegexCompletion(p, "\\b", "\b", "\\b - Word beginning or end"));
		p.addCompletion(new RegexCompletion(p, "\\B", "\\B", "\\B - Not a word beginning or end"));

		// Greedy, reluctant and possessive quantifiers
		p.addCompletion(new RegexCompletion(p, "?",    "?",  "X? - Greedy match, 0 or 1 times"));
		p.addCompletion(new RegexCompletion(p, "*",    "*",  "X* - Greedy match, 0 or more times"));
		p.addCompletion(new RegexCompletion(p, "+",    "+",  "X+ - Greedy match, 1 or more times"));
		p.addCompletion(new RegexCompletion(p, "{",    "{",  "X{n} - Greedy match, exactly n times"));
		p.addCompletion(new RegexCompletion(p, "{",    "{",  "X{n,} - Greedy match, at least n times"));
		p.addCompletion(new RegexCompletion(p, "{",    "{",  "X{n,m} - Greedy match, at least n but no more than m times"));
		p.addCompletion(new RegexCompletion(p, "??",   "??", "X?? - Lazy match, 0 or 1 times"));
		p.addCompletion(new RegexCompletion(p, "*?",   "*?", "X*? - Lazy match, 0 or more times"));
		p.addCompletion(new RegexCompletion(p, "+?",   "+?", "X+? - Lazy match, 1 or more times"));
		p.addCompletion(new RegexCompletion(p, "?+",   "?+", "X?+ - Possessive match, 0 or 1 times"));
		p.addCompletion(new RegexCompletion(p, "*+",   "*+", "X*+ - Possessive match, 0 or more times"));
		p.addCompletion(new RegexCompletion(p, "++",   "++", "X++ - Possessive match, 0 or more times"));

		// Back references
		p.addCompletion(new RegexCompletion(p, "\\i", "\\i", "\\i - Match of the capturing group i"));

		// Capturing groups
		p.addCompletion(new RegexCompletion(p, "(", "(", "(Expr) - Mark Expr as capturing group"));
		p.addCompletion(new RegexCompletion(p, "(?:", "(?:", "(?:Expr) - Non-capturing group"));

	}


	/**
	 * Adds the completion choices for regexes in a "replace with" text field.
	 *
	 * @param p The completion provider to add to.
	 * @see #addFindFieldCompletions(RegexAwareProvider)
	 */
	private void addReplaceFieldCompletions(RegexAwareProvider p) {
		p.addCompletion(new RegexCompletion(p, "$",     "$",     "$i - Match of the capturing group i"));
		p.addCompletion(new RegexCompletion(p, "\\",    "\\",    "\\ - Quote next character"));
		p.addCompletion(new RegexCompletion(p, "\\t",   "\\t",   "\\t - Tab"));
		p.addCompletion(new RegexCompletion(p, "\\n",   "\\n",   "\\n - Newline"));
	}


	/**
	 * Lazily creates the AutoCompletion instance this combo box uses.
	 *
	 * @return The auto-completion instance.
	 */
	private AutoCompletion getAutoCompletion() {
		if (ac==null) {
			ac = new AutoCompletion(getCompletionProvider());
		}
		return ac;
	}


	/**
	 * Creates the shared completion provider instance.
	 *
	 * @return The completion provider.
	 */
	protected synchronized CompletionProvider getCompletionProvider() {
		if (provider==null) {
			provider = new RegexAwareProvider();
			if (replace) {
				addReplaceFieldCompletions(provider);
			}
			else {
				addFindFieldCompletions(provider);
			}
		}
		return provider;
	}


    /**
     * Returns the image to display by this text field when content
     * assistance is available.
     *
     * @return The image to display.
     * @see #setContentAssistImage(Image)
     */
    public Image getContentAssistImage() {
        if (contentAssistImage != null) {
            return contentAssistImage;
        }
        return AbstractSearchDialog.getContentAssistImage();
    }


	/**
	 * Hides any auto-complete windows that are visible.
	 *
	 * @return Whether any windows were visible.
	 */
	public boolean hideAutoCompletePopups() {
		return ac != null && ac.hideChildWindows();
	}


	/**
	 * Returns whether regex auto-complete is enabled.
	 *
	 * @return Whether regex auto-complete is enabled.
	 * @see #setAutoCompleteEnabled(boolean)
	 */
	public boolean isAutoCompleteEnabled() {
		return enabled;
	}


	/**
	 * Toggles whether regex auto-complete is enabled.  This method will fire
	 * a property change event of type
	 * {@link ContentAssistable#ASSISTANCE_IMAGE}.
	 *
	 * @param enabled Whether regex auto complete should be enabled.
	 * @see #isAutoCompleteEnabled()
	 */
	public void setAutoCompleteEnabled(boolean enabled) {
		if (this.enabled!=enabled) {
			this.enabled = enabled;
			if (enabled) {
				AutoCompletion ac = getAutoCompletion();
				JTextComponent tc = (JTextComponent)getEditor().
											getEditorComponent();
				ac.install(tc);
			}
			else {
				ac.uninstall();
			}
			String prop = ContentAssistable.ASSISTANCE_IMAGE;
			// Must take care how we fire the property event, as Swing
			// property change support won't fire a notice if old and new are
			// both non-null and old.equals(new).
			if (enabled) {
				firePropertyChange(prop, null, getContentAssistImage());
			}
			else {
				firePropertyChange(prop, null, null);
			}
		}
	}


    /**
     * Sets the image to display by this text field when content
     * assistance is available.
     *
     * @param image The image.  If this is {@code null}, a default image
     *        (a light bulb) is used).  This should be kept small, around
     *        8x8 for standard resolution monitors.
     * @see #getContentAssistImage()
     */
    public void setContentAssistImage(Image image) {
        contentAssistImage = image;
    }


	/**
	 * A completion provider for regular expressions.
	 */
	private static class RegexAwareProvider extends DefaultCompletionProvider {

		@Override
		protected boolean isValidChar(char ch) {
			switch (ch) {
				case '\\':
				case '(':
				case '*':
				case '.':
				case '?':
				case '[':
				case '^':
				case ':':
				case '{':
				case '$':
				case '+':
					return true;
				default:
					return false;
			}
		}

	}


    /**
     * A completion representing a regular expression.
     */
	private static class RegexCompletion extends BasicCompletion {

		private String inputText;

		/**
		 * Constructor.
		 *
		 * @param provider The parent completion provider.
		 * @param inputText The text the user must input.
		 * @param replacementText The text to replace.
		 * @param shortDesc A short description of the completion.  This will be
		 *        displayed in the completion list.
		 */
		RegexCompletion(CompletionProvider provider, String inputText,
								String replacementText, String shortDesc) {
			super(provider, replacementText, shortDesc);
			this.inputText = inputText;
		}

		@Override
		public String getInputText() {
			return inputText;
		}

		@Override
		public String toString() {
			return getShortDescription();
		}

	}


}

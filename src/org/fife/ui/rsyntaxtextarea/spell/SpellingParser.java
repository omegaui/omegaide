/*
 * 07/21/2009
 *
 * SpellingParser.java - A spell-checker for RSyntaxTextArea.
 *
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.ZipFile;

import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

import org.fife.com.swabunga.spell.engine.Configuration;
import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.engine.Word;
import org.fife.com.swabunga.spell.event.DocumentWordTokenizer;
import org.fife.com.swabunga.spell.event.SpellCheckEvent;
import org.fife.com.swabunga.spell.event.SpellCheckListener;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.fife.com.swabunga.spell.event.StringWordTokenizer;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.spell.event.SpellingParserEvent;
import org.fife.ui.rsyntaxtextarea.spell.event.SpellingParserListener;


/**
 * A parser that spell-checks documents.  The spelling engine is a lightly
 * modified version of <a href="http://jazzy.sourceforge.net/">Jazzy</a>.
 * All Jazzy source, modified or otherwise, is licensed under the LGPL. just
 * like the entirety of this library.<p>
 *
 * For source code only comments are spell checked.  For plain text files,
 * the entire content is spell checked.<p>
 *
 * This parser can be shared among multiple <code>RSyntaxTextArea</code>
 * instances.<p>
 *
 * Usage:
 * <pre>
 * RSyntaxTextArea textArea = new RSyntaxTextArea(40, 25);
 * File englishZip = new File("english_dic.zip");
 * SpellingParser parser = SpellingParser.createEnglishSpellingParser(englishZip, true);
 * textArea.addParser(parser);
 * </pre>
 *
 * @author Robert Futrell
 * @version 0.5
 */
public class SpellingParser extends AbstractParser
			implements SpellCheckListener, ExtendedHyperlinkListener {

	private DefaultParseResult result;
	private SpellChecker sc;
	private RSyntaxDocument doc;
	private int startOffs;
	private int errorCount;
	private int maxErrorCount;
	private boolean allowAdd = true;
	private boolean allowIgnore = true;
	private Color squiggleUnderlineColor;
	private String noticePrefix;
	private String noticeSuffix;
	private EventListenerList listenerList;
	private SpellCheckableTokenIdentifier spellCheckableTokenIdentifier;


	/**
	 * The "user dictionary."  If this is non-<code>null</code>, then the
	 * user will be able to select "Add word to dictionary" for spelling
	 * errors.  When this option is selected, the word is added to this
	 * file.
	 */
	private File dictionaryFile;

	private static final String MSG = "org.fife.ui.rsyntaxtextarea.spell.SpellingParser";
	private static final ResourceBundle msg = ResourceBundle.getBundle(MSG);

	private static final String ADD			= "add";
	private static final String IGNORE		= "ignore";
	private static final String REPLACE		= "replace";
	private static final String TOOLTIP_TEXT_FORMAT =
		"<html><body dir='{0}'><img src='lightbulb.png' width='16' height='16'>{1}<hr><img src='spellcheck.png' width='16' height='16'>{2}<br>{3}<br>&nbsp;";

	/**
	 * The default maximum number of spelling errors to report for a document.
	 */
	private static final int DEFAULT_MAX_ERROR_COUNT			= 100;


	/**
	 * Constructor.
	 *
	 * @param dict The dictionary to use.
	 */
	public SpellingParser(SpellDictionary dict) {

		result = new DefaultParseResult(this);
		sc = new SpellChecker(dict);
		sc.addSpellCheckListener(this);
		setSquiggleUnderlineColor(Color.BLUE);
		setHyperlinkListener(this);
		setMaxErrorCount(DEFAULT_MAX_ERROR_COUNT);
		setAllowAdd(true);
		setAllowIgnore(true);
		setSpellCheckableTokenIdentifier(
				new DefaultSpellCheckableTokenIdentifier());

		// Since the spelling callback can possibly be called many times
		// per parsing, we're extremely cheap here and pre-split our message
		// format instead of using MessageFormat.
		String temp = msg.getString("IncorrectSpelling");
		int offs = temp.indexOf("{0}");
		noticePrefix = temp.substring(0, offs);
		noticeSuffix = temp.substring(offs+3);

		listenerList = new EventListenerList();

	}


	/**
	 * Adds a listener to this spelling parser.
	 *
	 * @param l The new listener.
	 * @see #removeSpellingParserListener(SpellingParserListener)
	 */
	public void addSpellingParserListener(SpellingParserListener l) {
		listenerList.add(SpellingParserListener.class, l);
	}


    /**
     * A utility method to easily create a parser for American or British
     * English.
     *
     * @param zip The location of the <code>english_dic.zip</code> file
     *        distributed with the spell checker add-on.
     * @param american Whether the parser should be for American (as opposed
     *        to British) English.
     * @return The parser.
     * @throws IOException If an error occurs reading the zip file.
     * @see #createEnglishSpellingParser(File, boolean, boolean)
     */
    public static SpellingParser createEnglishSpellingParser(File zip,
                                                     boolean american) throws IOException {
        // Including programming words by default seems counterintuitive, but it's how we always
        // behaved previously, and this component is typically used in code editors anyway.
        return createEnglishSpellingParser(zip, american, true);
    }


	/**
	 * A utility method to easily create a parser for American or British
	 * English.
	 *
	 * @param zip The location of the <code>english_dic.zip</code> file
	 *        distributed with the spell checker add-on.
	 * @param american Whether the parser should be for American (as opposed
	 *        to British) English.
      * @param programming Whether to include programming-related words and acronyms.
	 * @return The parser.
	 * @throws IOException If an error occurs reading the zip file.
      * @see #createEnglishSpellingParser(File, boolean)
	 */
	public static SpellingParser createEnglishSpellingParser(File zip,
									boolean american, boolean programming) throws IOException {

//		long start = System.currentTimeMillis();

		SpellDictionaryHashMap dict;

		try (ZipFile zf = new ZipFile(zip)) {

			// Words common to American and British English
			InputStream in = zf.getInputStream(zf.getEntry("eng_com.dic"));
			try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
				dict = new SpellDictionaryHashMap(r);
			}

            // Load words specific to the English dialect.
			List<String> others;
			if (american) {
				others = new ArrayList<>(Arrays.asList("color", "labeled", "center", "ize",
						"yze"));
			}
			else { // British
				others = new ArrayList<>(Arrays.asList("colour", "labelled", "centre",
						"ise", "yse"));
			}

			// Miscellaneous
            if (programming) {
                others.add("programming");
            }

			for (String other : others) {
				in = zf.getInputStream(zf.getEntry(other + ".dic"));
				try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
					dict.addDictionary(r);
				}
			}

		}

//		float secs = (System.currentTimeMillis() - start)/1000f;
//		System.out.println("Loading dictionary took " + secs + " seconds");

		return new SpellingParser(dict);

	}


	/**
	 * Notifies all listeners about an event in this parser.
	 *
	 * @param e The event.
	 */
	private void fireSpellingParserEvent(SpellingParserEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SpellingParserListener.class) {
				((SpellingParserListener)listeners[i+1]).spellingParserEvent(e);
			}
		}
	}


	/**
	 * Returns whether an "Add word to dictionary" link is added to tool tips
	 * returned by this parser.  Note that for an add operation to be
	 * successful, a user dictionary must also be defined.
	 *
	 * @return Whether words can be added to the user dictionary.
	 * @see #setAllowAdd(boolean)
	 * @see #setUserDictionary(File)
	 */
	public boolean getAllowAdd() {
		return allowAdd;
	}


	/**
	 * Returns whether an "Ignore this word for this session" link is
	 * added to tool tips returns by this parser.
	 *
	 * @return Whether words can be ignored.
	 * @see #setAllowIgnore(boolean)
	 */
	public boolean getAllowIgnore() {
		return allowIgnore;
	}


	/**
	 * Overridden to return the image base for {@link FocusableTip}s made
	 * from this parser's notices.
	 *
	 * @return The image base.
	 */
	@Override
	public URL getImageBase() {
		return getClass().getResource("/fluent-icons/");
	}


	private int getLineOfOffset(int offs) {
		return doc.getDefaultRootElement().getElementIndex(offs);
	}


	/**
	 * Returns the maximum number of errors this parser will report for a
	 * single document.
	 *
	 * @return The maximum number of errors that will be reported.
	 * @see #setMaxErrorCount(int)
	 */
	public int getMaxErrorCount() {
		return maxErrorCount;
	}


	/**
	 * Returns the strategy to use to identify tokens to spell check.
	 *
	 * @return The strategy.
	 * @see #setSpellCheckableTokenIdentifier(SpellCheckableTokenIdentifier)
	 */
	public SpellCheckableTokenIdentifier getSpellCheckableTokenIdentifier() {
		return spellCheckableTokenIdentifier;
	}


	/**
	 * Returns the color to use when painting spelling errors in an editor.
	 *
	 * @return The color to use.
	 * @see #setSquiggleUnderlineColor(Color)
	 */
	public Color getSquiggleUnderlineColor() {
		return squiggleUnderlineColor;
	}


	/**
	 * Returns the user's dictionary file.
	 *
	 * @return The user's dictionary file, or <code>null</code> if none has
	 *         been set.
	 * @see #setUserDictionary(File)
	 */
	public File getUserDictionary() {
		return dictionaryFile;
	}


	@Override
	public void linkClicked(RSyntaxTextArea textArea, HyperlinkEvent e) {

		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {

			String desc = e.getDescription();
			int temp = desc.indexOf("://");
			String operation = desc.substring(0, temp);
			String[] tokens = desc.substring(temp + 3).split(",");

			switch (operation) {

				case REPLACE:
					int offs = Integer.parseInt(tokens[0]);
					int len = Integer.parseInt(tokens[1]);
					String replacement = tokens[2];
					textArea.replaceRange(replacement, offs, offs + len);
					textArea.setSelectionStart(offs);
					textArea.setSelectionEnd(offs + replacement.length());
					break;

				case ADD:
					if (dictionaryFile == null) {
						// TODO: Add callback for application to prompt to create
						// a user dictionary
						UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					}
					String word = tokens[0];
					if (sc.addToDictionary(word)) {
						textArea.forceReparsing(this);
						SpellingParserEvent se = new SpellingParserEvent(this,
								textArea, SpellingParserEvent.WORD_ADDED, word);
						fireSpellingParserEvent(se);
					} else { // IO error adding the word
						UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					}
					break;

				case IGNORE:
					word = tokens[0];
					sc.ignoreAll(word);
					textArea.forceReparsing(this);
					SpellingParserEvent se = new SpellingParserEvent(this,
							textArea, SpellingParserEvent.WORD_IGNORED, word);
					fireSpellingParserEvent(se);
					break;
			}

		}

	}


	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {

//		long startTime = System.currentTimeMillis();

		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		result.clearNotices();
		// Always spell check all lines, for now.
		result.setParsedLines(0, lineCount-1);
		this.doc = doc;
		errorCount = 0;

		// Use a faster method for spell-checking plain text.
		if (style==null || SyntaxConstants.SYNTAX_STYLE_NONE.equals(style)) {
			startOffs = 0;
			parseEntireDocument(doc);
		}

		else {

			SpellCheckableTokenIdentifier scti =
					getSpellCheckableTokenIdentifier();

			scti.begin();
			try {
				for (Token t : doc) {
					if (scti.isSpellCheckable(t)) {
						startOffs = t.getOffset();
						// TODO: Create a wordTokenizer that uses char[] array
						// to prevent String allocation.
						StringWordTokenizer swt =
									new StringWordTokenizer(t.getLexeme());
						int rc = sc.checkSpelling(swt);
						if (rc==SpellChecker.SPELLCHECK_CANCEL) {
							break; // Stop spell checking comments
						}
					}
				}
			} finally {
				scti.end();
			}

		}

//		float secs = (System.currentTimeMillis() - startTime)/1000f;
//		System.out.println("Spell check completed in: " + secs + " seconds");
//		System.out.println("Error count==" + errorCount);
		return result;

	}


	/**
	 * Spell-checks a plain text document.
	 *
	 * @param doc The document to spell check.
	 */
	private void parseEntireDocument(RSyntaxDocument doc) {
		DocumentWordTokenizer dwt = new DocumentWordTokenizer(doc);
		sc.checkSpelling(dwt);
	}


	/**
	 * Removes a listener from this spelling parser.
	 *
	 * @param l The listener to remove.
	 * @see #addSpellingParserListener(SpellingParserListener)
	 */
	public void removeSpellingParserListener(SpellingParserListener l) {
		listenerList.remove(SpellingParserListener.class, l);
	}


	/**
	 * Sets whether an "Add word to dictionary" link is added to tool tips
	 * returned by this parser.  Note that for an add operation to be
	 * successful, a user dictionary must also be defined.
	 *
	 * @param add Whether the option should be available.
	 * @see #getAllowAdd()
	 * @see #setUserDictionary(File)
	 */
	public void setAllowAdd(boolean add) {
		allowAdd = add;
	}


	/**
	 * Returns whether an "Ignore this word for this session" link is
	 * added to tool tips returns by this parser.
	 *
	 * @param ignore Whether the option should be available.
	 * @see #getAllowIgnore()
	 */
	public void setAllowIgnore(boolean ignore) {
		allowIgnore = ignore;
	}


	/**
	 * Sets the maximum number of spelling errors this parser will report for a
	 * single text file.  Note that the file should be re-parsed after changing
	 * this value.
	 *
	 * @param max The new maximum error count.
	 * @see #getMaxErrorCount()
	 */
	public void setMaxErrorCount(int max) {
		maxErrorCount = max;
	}


	/**
	 * Sets the strategy to use to identify tokens to spell check.
	 *
	 * @param scti The new strategy to use.  This cannot be <code>null</code>.
	 * @see #getSpellCheckableTokenIdentifier()
	 */
	public void setSpellCheckableTokenIdentifier(
			SpellCheckableTokenIdentifier scti) {
		if (scti==null) {
			throw new IllegalArgumentException(
					"SpellCheckableTokenIdentifier cannot be null");
		}
		this.spellCheckableTokenIdentifier = scti;
	}


	/**
	 * Sets the color to use when painting spelling errors in an editor.
	 *
	 * @param color The color to use.
	 * @see #getSquiggleUnderlineColor()
	 */
	public void setSquiggleUnderlineColor(Color color) {
		squiggleUnderlineColor = color;
	}


	/**
	 * Sets the "user dictionary," that is, the dictionary that words can be
	 * added to at runtime.<p>
	 *
	 * If this is non-<code>null</code>, then on the focusable tool tip for
	 * spelling errors, there will be an option available: "Add word to
	 * dictionary."  If this is clicked then the "error" word is added to the
	 * user's dictionary and the document is re-parsed.
	 *
	 * @param dictionaryFile The dictionary file.  If this is <code>null</code>
	 *        then the user will not be able to add words.
	 * @throws IOException If an IO error occurs.
	 * @see #getUserDictionary()
	 */
	public void setUserDictionary(File dictionaryFile) throws IOException {
		SpellDictionaryHashMap userDict;
		if (dictionaryFile!=null) {
			if (!dictionaryFile.exists()) {
				// The file must exist for Jazzy to be happy
				FileWriter w = new FileWriter(dictionaryFile);
				w.close();
			}
			userDict = new SpellDictionaryHashMap(dictionaryFile);
		}
		else {
			// Unfortunately cannot use null, Jazzy won't allow it
			userDict = new SpellDictionaryHashMap();
		}
		sc.setUserDictionary(userDict);
		this.dictionaryFile = dictionaryFile;
	}


	/**
	 * Callback called when a spelling error is found.
	 *
	 * @param e The event.
	 */
	@Override
	public void spellingError(SpellCheckEvent e) {
//		e.ignoreWord(true);
		String word = e.getInvalidWord();
		int offs = startOffs + e.getWordContextPosition();
		int line = getLineOfOffset(offs);
		String text = noticePrefix + word + noticeSuffix;
		SpellingParserNotice notice =
			new SpellingParserNotice(this, text, line, offs, word, sc);
		result.addNotice(notice);
		if (++errorCount>=maxErrorCount) {
			//System.out.println("Cancelling the spelling check!");
			e.cancel();
		}
	}


	/**
	 * The notice type returned by this parser.
	 */
	private static class SpellingParserNotice extends DefaultParserNotice {

		private String word;
		private SpellChecker sc;

		SpellingParserNotice(SpellingParser parser, String msg,
									int line, int offs, String word,
									SpellChecker sc) {
			super(parser, msg, line, offs, word.length());
			setLevel(Level.INFO);
			this.word = word;
			this.sc = sc;
		}

		@Override
		public Color getColor() {
			return ((SpellingParser)getParser()).getSquiggleUnderlineColor();
		}

		@Override
		public String getToolTipText() {

			StringBuilder sb = new StringBuilder();
			String spacing = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			int threshold = sc.getConfiguration().getInteger(Configuration.SPELL_THRESHOLD);
			List<Word> suggestions = sc.getSuggestions(word, threshold);
			if (suggestions==null || suggestions.size()==0) {
				sb.append(spacing).append("&#8226;&nbsp;<em>");
				sb.append(msg.getString("None"));
				sb.append("</em><br><br>");
			}
			else {

				// If the bad word started with an upper-case letter, make sure all our suggestions do.
				if (Character.isUpperCase(word.charAt(0))) {
					for (Word suggestion : suggestions) {
						String oldSug = suggestion.getWord();
						suggestion.setWord(Character.toUpperCase(oldSug.charAt(0)) + oldSug.substring(1));
					}
				}

				sb.append("<center>");
				sb.append("<table width='75%'>");
				for (int i=0; i<suggestions.size(); i++) {
					if ((i%2)==0) {
						sb.append("<tr>");
					}
					sb.append("<td>&#8226;&nbsp;");
					Word suggestion = suggestions.get(i);
					// Surround with double quotes, not single, since
					// replacement words can have single quotes in them.
					sb.append("<a href=\"").append(REPLACE).append("://").
					append(getOffset()).append(',').
					append(getLength()).append(',').
					append(suggestion.getWord()).
					append("\">").
					append(suggestion.getWord()).
					append("</a>").
					append("</td>");
					if ((i&1)==1) {
						sb.append("</tr>");
					}
				}
				if ((suggestions.size()%2)==0) {
					sb.append("<td></td></tr>");
				}
				sb.append("</table>");
				sb.append("</center>");
			}

			SpellingParser sp = (SpellingParser)getParser();
			if (sp.getAllowAdd()) {
				sb.append("<img src='add.png' width='16' height='16'>&nbsp;").
						append("<a href='").append(ADD).
						append("://").append(word).append("'>").
						append(msg.getString("ErrorToolTip.AddToDictionary")).
						append("</a><br>");
			}

			if (sp.getAllowIgnore()) {
				String text = msg.getString("ErrorToolTip.IgnoreWord");
				text = MessageFormat.format(text, word);
				sb.append("<img src='cross.png' width='16' height='16'>&nbsp;").
						append("<a href='").append(IGNORE).
						append("://").append(word).append("'>").
						append(text).append("</a>");
			}

			String firstLine = MessageFormat.format(
					msg.getString("ErrorToolTip.DescHtml"),
					word);
			ComponentOrientation o = ComponentOrientation.getOrientation(
												Locale.getDefault());
			String dirAttr = o.isLeftToRight() ? "ltr" : "rtl";

			return MessageFormat.format(TOOLTIP_TEXT_FORMAT,
					dirAttr,
					firstLine,
					msg.getString("ErrorToolTip.SuggestionsHtml"),
					sb.toString());

		}

		@Override
		public String toString() {
			return "[SpellingParserNotice: " + word + "]";
		}

	}


}

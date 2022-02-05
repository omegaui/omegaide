/**
 * The IDE 's Default Text Editor
 * Copyright (C) 2021 Omega UI

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omega.ui.component;
import omega.ui.window.EditorPreviewWindow;

import omega.ui.panel.AbstractPreviewPanel;
import omega.ui.panel.PreviewPanels;

import omega.instant.support.java.generator.Generator;

import omega.instant.support.java.framework.CodeFramework;
import omega.instant.support.java.framework.ImportFramework;

import omega.instant.support.build.gradle.GradleProcessManager;

import omega.instant.support.SyntaxParsers;
import omega.instant.support.CodeFrameworks;
import omega.instant.support.ContentTokenizers;
import omega.instant.support.IndentationFrameworks;
import omega.instant.support.AbstractJumpToDefinitionPanel;
import omega.instant.support.JumpToDefinitionPanels;

import omega.io.DataManager;
import omega.io.RustTokenMaker;
import omega.io.SnippetBase;
import omega.io.UIManager;
import omega.io.BookmarksManager;
import omega.io.IconManager;

import omega.instant.support.java.misc.JavaJumpToDefinitionPanel;
import omega.instant.support.java.misc.JavaCodeNavigator;
import omega.instant.support.java.misc.JavaCommentMarker;

import omega.instant.support.java.highlighter.JavaErrorPanel;

import omega.ui.dialog.FileSaveDialog;
import omega.ui.dialog.ChoiceDialog;

import omega.instant.support.java.assist.ContentWindow;
import omega.instant.support.java.assist.ContentTokenizer;

import omegaui.listener.KeyStrokeListener;

import omega.plugin.event.PluginReactionEvent;

import omega.Screen;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.fife.ui.rsyntaxtextarea.modes.KotlinTokenMaker;
import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;

import java.awt.Image;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.nio.charset.StandardCharsets;

import static omega.instant.support.java.assist.Assembly.*;
import static java.awt.event.KeyEvent.*;
public class Editor extends RSyntaxTextArea implements KeyListener, MouseListener, MouseMotionListener, SearchListener, FocusListener {
	private static Screen screen;
	private static Theme theme;
	private static String currentTheme = "light";

	private RTextScrollPane scrollPane;
	private FindAndReplace fAndR;

	private volatile String savedText = "";

	public KeyListener keyListener;
	public KeyStrokeListener keyStrokeListener;

	public int keyCache = 0;
	public int lastKeyCode = -1;

	public volatile File currentFile;

	public volatile boolean call = false;

	private volatile boolean initializedJavaKeyStrokes = false;

	private static boolean launched = false;

	public JPanel tabHolderPanel;
	
	public AbstractPreviewPanel previewPanel;
	public EditorPreviewWindow previewWindow;

	public ContentWindow contentWindow;
	public FileSaveDialog fileSaveDialog;

	public JavaErrorPanel javaErrorPanel;

	public AbstractJumpToDefinitionPanel jumpToDefinitionPanel;

	public SearchContext lastSearchContext;

	private static final File ENG_DICTIONARY_FILE = new File(".omega-ide" + File.separator + "dictionary", "english_dic.zip");

	public static SpellingParser englishSpellingParser = null;
	static {
		try{
			englishSpellingParser = SpellingParser.createEnglishSpellingParser(ENG_DICTIONARY_FILE, true);
			englishSpellingParser.setUserDictionary(ENG_DICTIONARY_FILE);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Editor(Screen screen) {
		Editor.screen = screen;
		
		englishSpellingParser.setSquiggleUnderlineColor(omega.io.UIManager.TOOLMENU_COLOR4);
		addParser(englishSpellingParser);

		scrollPane = new RTextScrollPane(this, true, omega.io.UIManager.glow);
		scrollPane.setFoldIndicatorEnabled(true);
		scrollPane.setBackground(UIManager.c2);

		fAndR = new FindAndReplace();

		initView();

		fileSaveDialog = new FileSaveDialog(screen);

		addCaretListener((e)-> {
			String text = getSelectedText();
			if(text == null || text.equals(""))
				screen.getBottomPane().jumpField.setText("Goto Line");
			else
				screen.getBottomPane().jumpField.setText(text.length() + "");
		});

		createNewContent();

		javaErrorPanel = new JavaErrorPanel(this);
		add(javaErrorPanel);
	}

	private void initView() {
		
		setAnimateBracketMatching(true);
		setAntiAliasingEnabled(true);
		setAutoIndentEnabled(true);
		setAutoscrolls(true);
		setBracketMatchingEnabled(true);
		setCloseCurlyBraces(true);
		setPaintMatchedBracketPair(true);
		setHyperlinksEnabled(true);
		setHyperlinkForeground(UIManager.glow);
		setCodeFoldingEnabled(true);
		setFadeCurrentLineHighlight(false);
		setShowMatchedBracketPopup(true);
		setHighlightSecondaryLanguages(true);
		setDragEnabled(true);
		setDropMode(DropMode.USE_SELECTION);
		setTabSize(DataManager.getTabSize());
		UIManager.setData(this);

		getAttachment().getGutter().setIconRowHeaderEnabled(true);
		getAttachment().getGutter().setIconRowHeaderInheritsGutterBackground(true);
		getAttachment().getGutter().iconArea.setBackground(UIManager.back2);

		initKeyStrokes();

		addKeyListener((keyListener = this));
		addMouseListener(this);
		addMouseMotionListener(this);
		addFocusListener(this);

		addCaretListener((e)->findSelection());

		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_EDITOR_CREATED, this, currentFile));
	}

	public void initKeyStrokes(){
		//Initializing KeyStrokeData
		keyStrokeListener = new KeyStrokeListener(this);
		addKeyListener(keyStrokeListener);

		keyStrokeListener.putKeyStroke((e)->saveCurrentFile(), VK_CONTROL, VK_S).setStopKeys(VK_SHIFT);
		keyStrokeListener.putKeyStroke((e)->fAndR.setVisible(!fAndR.isVisible()), VK_CONTROL, VK_SHIFT, VK_F);
		keyStrokeListener.putKeyStroke((e)->doDuplicate(e), VK_CONTROL, VK_D).setStopKeys(VK_SHIFT);

		keyStrokeListener.putKeyStroke((e)->increaseFont(e), VK_CONTROL, VK_SHIFT, VK_EQUALS).setStopKeys(VK_T);
		keyStrokeListener.putKeyStroke((e)->increaseFont(e), VK_CONTROL, VK_SHIFT, VK_PLUS).setStopKeys(VK_T);
		keyStrokeListener.putKeyStroke((e)->decreaseFont(e), VK_CONTROL, VK_SHIFT, VK_MINUS).setStopKeys(VK_T);

		keyStrokeListener.putKeyStroke((e)->increaseTabSize(e), VK_CONTROL, VK_SHIFT, VK_T, VK_EQUALS);
		keyStrokeListener.putKeyStroke((e)->increaseTabSize(e), VK_CONTROL, VK_SHIFT, VK_T, VK_PLUS);
		keyStrokeListener.putKeyStroke((e)->decreaseTabSize(e), VK_CONTROL, VK_SHIFT, VK_T, VK_MINUS);

		keyStrokeListener.putKeyStroke((e)->triggerBuild(e), VK_CONTROL, VK_B).setStopKeys(VK_SHIFT);
		keyStrokeListener.putKeyStroke((e)->triggerRun(e), VK_CONTROL, VK_SHIFT, VK_R);
		keyStrokeListener.putKeyStroke((e)->triggerInstantRun(e), VK_CONTROL, VK_SHIFT, VK_F1);
		keyStrokeListener.putKeyStroke((e)->launchCurrentFile(e), VK_CONTROL, VK_SHIFT, VK_L);
		keyStrokeListener.putKeyStroke((e)->triggerJavaCommentMarker(e), VK_CONTROL, VK_SHIFT, VK_SLASH);
		keyStrokeListener.putKeyStroke((e)->saveImage(), VK_CONTROL, VK_SHIFT, VK_C);
		keyStrokeListener.putKeyStroke((e)->showSearchDialog(e), VK_CONTROL, VK_SHIFT, VK_P);
		keyStrokeListener.putKeyStroke((e)->triggerSnippets(e), VK_TAB).setStopKeys(VK_CONTROL, VK_ALT, VK_WINDOWS);
		keyStrokeListener.putKeyStroke((e)->autoIndent(e), VK_CONTROL, VK_I).setStopKeys(VK_SHIFT);
		keyStrokeListener.putKeyStroke((e)->triggerJumpToDefinition(e), VK_CONTROL, VK_J).setStopKeys(VK_ALT, VK_SHIFT);
		keyStrokeListener.putKeyStroke((e)->ToolMenu.recentsDialog.setVisible(true), VK_CONTROL, VK_SHIFT, VK_M).setStopKeys(VK_ALT);
		keyStrokeListener.putKeyStroke((e)->triggerPreview(e), VK_CONTROL, VK_P).setStopKeys(VK_SHIFT);
	}

	public void initJavaFileKeyStrokes(){
		if(currentFile != null && currentFile.getName().endsWith(".java") && !initializedJavaKeyStrokes){
			initializedJavaKeyStrokes = true;
			keyStrokeListener.putKeyStroke((e)->triggerImportFramework(e), VK_CONTROL, VK_SHIFT, VK_O);
			keyStrokeListener.putKeyStroke((e)->showGSDialog(e), VK_CONTROL, VK_SHIFT, VK_G).useAutoReset();
			keyStrokeListener.putKeyStroke((e)->showIODialog(e), VK_CONTROL, VK_SHIFT, VK_I).useAutoReset();
		}
	}

	private void createNewContent() {
		contentWindow = new ContentWindow(this);
		addKeyListener(contentWindow);
		launchContentAssist();
		setLayout(null);
		add(contentWindow);
	}

	public void launchContentAssist() {
		if(launched)
			return;
		launched = true;
		new Thread(()->{
			long lastTime = System.nanoTime();
			double ns = 1000000000 / 30;
			double delta = 0;
			int updates = 0;
			int frames = 0;
			long timer = System.currentTimeMillis();
			long now = 0;

			while(screen.active){
				now = System.nanoTime();
				delta += (now - lastTime) / ns;
				lastTime = now;
				if(delta >= 1){
					try {
						if(screen.getCurrentEditor() != null){
							screen.getCurrentEditor().readCode();
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					updates++;
					delta--;
				}

				frames++;

				if(System.currentTimeMillis() - timer > 1000){
					timer += 1000;
					updates = 0;
					frames = 0;

					if(DataManager.isParsingEnabled()) {
						SyntaxParsers.parse();
					}
				}
			}
		}).start();
	}

	public void readCode() {
		if(call) {
			call = false;
			if(!CodeFrameworks.isResolving())
				ContentTokenizers.arrangeTokens(this);
		}
	}

	public static void setStyle(Editor e, File f) {
		if(!f.getName().contains(".") || f.getName().endsWith(".txt"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_NONE);
		else if(f.getName().endsWith(".as"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_ACTIONSCRIPT);
		else if(f.getName().endsWith(".asm"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_ASSEMBLER_X86);
		else if(f.getName().endsWith(".asm"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_ASSEMBLER_6502);
		else if(f.getName().endsWith(".html"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_HTML);
		else if(f.getName().endsWith(".c"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_C);
		else if(f.getName().endsWith(".vala"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CPLUSPLUS);
		else if(f.getName().endsWith(".clj") || f.getName().endsWith(".cljs") || f.getName().endsWith(".cljc") || f.getName().endsWith(".edn"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CLOJURE);
		else if(f.getName().endsWith(".cpp"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CPLUSPLUS);
		else if(f.getName().endsWith(".cs"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CSHARP);
		else if(f.getName().endsWith(".css"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CSS);
		else if(f.getName().endsWith(".csv"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_CSV);
		else if(f.getName().endsWith(".d"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_D);
		else if(f.getName().endsWith(".dart"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_DART);
		else if(f.getName().endsWith(".dpr"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_DELPHI);
		else if(f.getName().endsWith(".dtd"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_DTD);
		else if(f.getName().endsWith(".f90"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_FORTRAN);
		else if(f.getName().endsWith(".go"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_GO);
		else if(f.getName().endsWith(".groovy") || f.getName().endsWith(".gradle"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_GROOVY);
		else if(f.getName().endsWith(".html") || f.getName().endsWith(".svg"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_HTML);
		else if(f.getName().endsWith(".ini"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_INI);
		else if(f.getName().endsWith(".java"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JAVA);
		else if(f.getName().endsWith(".kt") || f.getName().endsWith(".kts") || f.getName().endsWith(".ktm"))
			KotlinTokenMaker.apply(e);
		else if(f.getName().endsWith(".js") || f.getName().endsWith(".jsx"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JAVASCRIPT);
		else if(f.getName().endsWith(".ts") || f.getName().endsWith(".tsx"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_TYPESCRIPT);
		else if(f.getName().endsWith(".json"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JSON_WITH_COMMENTS);
		else if(f.getName().endsWith(".hjson"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JSP);
		else if(f.getName().endsWith(".tex"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_LATEX);
		else if(f.getName().endsWith(".less"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_LESS);
		else if(f.getName().endsWith(".lsp"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_LISP);
		else if(f.getName().endsWith(".lua"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_LUA);
		else if(f.getName().endsWith("makefile"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_MAKEFILE);
		else if(f.getName().endsWith(".mxml"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_MXML);
		else if(f.getName().endsWith(".nsi"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_NSIS);
		else if(f.getName().endsWith(".pl"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PERL);
		else if(f.getName().endsWith(".php"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PHP);
		else if(f.getName().endsWith(".property"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PROPERTIES_FILE);
		else if(f.getName().endsWith(".py"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PYTHON);
		else if(f.getName().endsWith(".rb"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_RUBY);
		else if(f.getName().endsWith(".sas"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_SAS);
		else if(f.getName().endsWith(".scala") || f.getName().endsWith(".sc"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_SCALA);
		else if(f.getName().endsWith(".sql"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_SQL);
		else if(f.getName().endsWith(".ts"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_TYPESCRIPT);
		else if(f.getName().endsWith(".sh") || f.getName().endsWith(".run"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_UNIX_SHELL);
		else if(f.getName().endsWith(".bat") || f.getName().endsWith(".cmd"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_WINDOWS_BATCH);
		else if(f.getName().endsWith(".xml") || f.getName().endsWith(".fxml"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_XML);
		else if(f.getName().endsWith(".yaml") || f.getName().endsWith(".yml"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_YAML);
		else if(f.getName().endsWith(".rs"))
			RustTokenMaker.apply(e);
		else if(f.getName().endsWith(".md"))
			MarkdownTokenMaker.apply(e);
	}

	public void loadTheme() {
		try {
			String name = omega.io.UIManager.isDarkMode() ? "dark" : "idea";
			theme = Theme.load(Editor.class.getResourceAsStream("/" + name + ".xml"));
			theme.apply(this);
		}
		catch (Exception e) {

		}
		try {
			screen.getUIManager().loadData();
			setFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
			UIManager.setData(screen.getTabPanel());
			getAttachment().getGutter().iconArea.width = UIManager.fontSize;
		}
		catch(Exception e) {

		}
	}

	public static Theme getTheme() {
		if(theme == null){
			try {
				String name = omega.io.UIManager.isDarkMode() ? "dark" : "idea";
				theme = Theme.load(Editor.class.getResourceAsStream("/" + name + ".xml"));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return theme;
	}

	public synchronized void loadFile(File file) {
		if(file == null)
			return;
		try {
			if(currentFile != null) {
				saveCurrentFile();
			}
			currentFile = file;
			if(!file.exists()) {
				System.out.println("File does not exists");
				return;
			}
			BufferedReader fread = new BufferedReader(new FileReader(file));
			read(fread, file);
			fread.close();
			loadTheme();
			savedText = getText();
			setStyle(this, currentFile);
			setCaretPosition(0);
			JavaCodeNavigator.install(this);
			initJavaFileKeyStrokes();
			BookmarksManager.markAll(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void saveCurrentFile() {
		if(savedText.equals(getText()))
			return;
		if(currentFile == null || !currentFile.exists()){
			int res = ChoiceDialog.makeChoice("Data in the editor does not corresponds to any existing file. Do you want to save it?", "Save", "Lose");
			if(res == ChoiceDialog.CHOICE1)
				saveFileAs();
			return;
		}
		try {
			String text = getText();
			savedText = text;
			PrintWriter writer = new PrintWriter(currentFile, StandardCharsets.UTF_8);
			writer.print(text);
			writer.close();

			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_EDITOR_SAVED, this, currentFile));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void saveImage() {
		try {
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			paint(image.getGraphics() != null ? image.createGraphics() : image.getGraphics());
			String path = Screen.getProjectFile().getProjectPath();
			new File(Screen.getProjectFile().getProjectPath() + File.separator + "out").mkdir();
			path += File.separator + "out" + File.separator + currentFile.getName() + "_lines_" + getLineCount() + ".jpg";
			if(ImageIO.write(image, "JPG", new File(path))) {
				Screen.getProjectFile().getFileTreePanel().refresh();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFileAs() {
		String path = fileSaveDialog.saveFile();
		if(path != null) {
			try {
				PrintWriter writer = new PrintWriter(new File(path), StandardCharsets.UTF_8);
				writer.println(getText());
				writer.close();
				Screen.getProjectFile().getFileTreePanel().refresh();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void closeFile() {
		if(currentFile == null)
			return;
		saveCurrentFile();
		BookmarksManager.saveBookmarks(this);
		currentFile = null;
		setText("");
		savedText = "";
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_EDITOR_CLOSED, this, currentFile));
	}

	public void reloadFile() {
		if(currentFile != null) {
			try {
				BufferedReader fread = new BufferedReader(new FileReader(currentFile));
				read(fread, currentFile);
				fread.close();
				loadTheme();
				savedText = getText();
				setStyle(this, currentFile);
				setCaretPosition(0);

				Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_EDITOR_RELOADED, this, currentFile));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void discardData(){
		setText(savedText);
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_EDITOR_DISCARD, this, currentFile));
	}

	public void deleteFile() {
		try {
			if(currentFile == null)
				return;
			if(!currentFile.exists())
				return;

			int res0 = ChoiceDialog.makeChoice("Do you want to delete " + currentFile.getName() + "?", "Yes", "No!");
			if(res0 != ChoiceDialog.CHOICE1)
				return;

			closeFile();
			if(!currentFile.delete()) {

			}
			else {
				Screen.getProjectFile().getFileTreePanel().refresh();
				savedText = "";
				currentFile = null;
				Screen.getProjectFile().getFileTreePanel().refresh();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteDir(File file) throws Exception {
		if (file.isDirectory()) {
			if (file.list().length == 0)
				file.delete();
			else {
				File files[] = file.listFiles();
				for (File fileDelete : files)
					deleteDir(fileDelete);
				if (file.list().length == 0) {
					file.delete();
				}
			}
		}
		else
			file.delete();
	}

	public static void deleteFile(File currentFile) {
		new Thread(()->{
			try {
				if(currentFile == null)
					return;
				if(currentFile.isDirectory()) {
					int res0 = ChoiceDialog.makeChoice("Do you want to delete " + currentFile.getName() + "?", "Yes", "No!");
					if(res0 != ChoiceDialog.CHOICE1)
						return;
					try {
						deleteDir(currentFile);
						Screen.getProjectFile().getFileTreePanel().refresh();
					}
					catch(Exception e) {

					}
					return;
				}
				if(!currentFile.exists())
					return;
				int res0 = ChoiceDialog.makeChoice("Do you want to delete " + currentFile.getName() + "?", "Yes", "No!");
				if(res0 != ChoiceDialog.CHOICE1)
					return;
				if(currentFile.delete()) {
					Screen.getProjectFile().getFileTreePanel().refresh();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		javaErrorPanel.relocate();
	}

	@Override
	public void setFont(Font font){
		super.setFont(font);
		if(scrollPane != null){
			if(scrollPane.getGutter() != null){
				if(scrollPane.getGutter().lineNumberList != null)
					scrollPane.getGutter().lineNumberList.setFont(font);
				if(scrollPane.getGutter().iconArea != null)
					scrollPane.getGutter().iconArea.setFont(font);
			}
		}
	}

	public void doDuplicate(KeyEvent e){
		if(getSelectedText() == null || getSelectedText().equals("")){
			String text = getText();
			text = text.substring(0, getCaretPosition());
			if(text.contains("\n"))
				text = text.substring(text.lastIndexOf('\n') + 1);
			insert("\n" + text, getCaretPosition());
		}
		else{
			String text = getSelectedText();
			insert(text, getCaretPosition());
		}
		e.consume();
	}

	public void increaseFont(KeyEvent e){
		UIManager.fontSize++;
		screen.getUIManager().save();
		screen.loadThemes();

		e.consume();
	}

	public void decreaseFont(KeyEvent e){
		if(UIManager.fontSize > 8){
			UIManager.fontSize--;
			screen.getUIManager().save();
			screen.loadThemes();

			e.consume();
		}
	}

	public void increaseTabSize(KeyEvent e){
		setTabSize(getTabSize() + 1);
		DataManager.setTabSize(getTabSize());

		e.consume();
	}

	public void decreaseTabSize(KeyEvent e){
		if(getTabSize() > 1){
			setTabSize(getTabSize() - 1);
			DataManager.setTabSize(getTabSize());

			e.consume();
		}
	}

	public void triggerBuild(KeyEvent e){
		if(screen.getToolMenu().buildComp.isClickable()){
			if(GradleProcessManager.isGradleProject())
				GradleProcessManager.build();
			else
				Screen.getProjectBuilder().compileProject();

			e.consume();
		}
	}

	public void triggerRun(KeyEvent e){
		if(screen.getToolMenu().buildComp.isClickable()){
			if(GradleProcessManager.isGradleProject())
				GradleProcessManager.run();
			else
				Screen.getProjectRunner().run();

			e.consume();
		}
	}

	public void triggerInstantRun(KeyEvent e){
		if(screen.getToolMenu().buildComp.isClickable()){
			Screen.getProjectRunner().instantRun();

			e.consume();
		}
	}

	public void showSearchDialog(KeyEvent e){
		Screen.getProjectFile().getSearchWindow().setVisible(true);

		e.consume();
	}

	public void triggerSnippets(KeyEvent e){
		String codeX = getText();
		codeX = codeX.substring(0, getCaretPosition());
		int index = 0;
		if(codeX.contains("\n")){
			index = codeX.lastIndexOf('\n') + 1;
			codeX = codeX.substring(index);
		}
		if(codeX.contains(";")){
			index = codeX.lastIndexOf(';') + 1;
			codeX = codeX.substring(codeX.lastIndexOf(';') + 1);
		}
		String cx = codeX;
		if(codeX.startsWith(" ")) {
			index = codeX.lastIndexOf(' ') + 1;
			codeX = codeX.substring(codeX.lastIndexOf(' ') + 1);
		}
		if(codeX.startsWith("\t")) {
			index += codeX.lastIndexOf('\t') + 1;
			codeX = codeX.substring(codeX.lastIndexOf('\t') + 1);
		}

		codeX = codeX.trim();

		if(SnippetBase.hasSnippet(codeX)){
			SnippetBase.insertSnippet(this, codeX, index = getCaretPosition() - codeX.length(), cx.substring(0, cx.indexOf(codeX)));

			e.consume();
		}
	}

	public void triggerImportFramework(KeyEvent e){
		new Thread(()->ImportFramework.addImports(ImportFramework.findClasses(getText()), this)).start();

		e.consume();
	}

	public void showGSDialog(KeyEvent e){
		Generator.gsView.genView(this);

		e.consume();
	}

	public void showIODialog(KeyEvent e){
		Generator.overView.genView(this);

		e.consume();
	}

	public void autoIndent(KeyEvent e){
		IndentationFrameworks.indent(this);

		e.consume();
	}

	public void launchCurrentFile(KeyEvent e){
		ToolMenu.processWizard.launch(currentFile);

		e.consume();
	}

	public void triggerJavaCommentMarker(KeyEvent e){
		JavaCommentMarker.markSingleLineComment(this, getCaretLineNumber());

		e.consume();
	}

	public void findSelection(){
		String text = getSelectedText();
		if(text != null && text.trim().length() > 0 && !text.contains("\n") && Character.isLetterOrDigit(text.charAt(0))){
			lastSearchContext = new SearchContext(text, true);
			lastSearchContext.setWholeWord(true);
			searchEvent(new SearchEvent(this, SearchEvent.Type.MARK_ALL, lastSearchContext));
		}
	}

	public void triggerJumpToDefinition(KeyEvent e){
		if(jumpToDefinitionPanel == null)
			jumpToDefinitionPanel = JumpToDefinitionPanels.get(this);
		if(jumpToDefinitionPanel != null){
			JumpToDefinitionPanels.putToView(jumpToDefinitionPanel);
		}
		e.consume();
	}

	public void triggerPreview(KeyEvent e){
		new Thread(()->{
			if(tabHolderPanel != null){
				previewPanel = PreviewPanels.getPreviewPanel(this);
				if(previewPanel != null){
					if(previewWindow == null){
						previewWindow = new EditorPreviewWindow();
					}
					previewPanel.genPreview(this, previewWindow);
					previewWindow.showPreview(previewPanel);
				}
				else
					Screen.setStatus("No Preview Generator available for file " + currentFile.getName(), 10, IconManager.fluentinfoImage);
			}
		}).start();

		e.consume();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		synchronized(Editor.class){
			int code = e.getKeyCode();

			if(lastKeyCode != code && code != VK_SHIFT){
				keyCache += code;
				lastKeyCode = code;
			}

			contentWindow.setIgnoreGenViewOnce(keyCache != 0);

			if(code == KeyEvent.VK_BACK_SPACE)
				autoSymbolExclusion(e);
			else
				autoSymbolCompletion(e);

			if(currentFile != null) {
				//Managing KeyBoard Shortcuts

				if(contentWindow.isVisible()) {
					if(e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_END
					|| ";:|\\`~!".contains(e.getKeyChar() + "")) {
						contentWindow.setVisible(false);
						return;
					}
					if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(DataManager.isContentAssistRealTime())
							call = true;
						return;
					}
					if(e.getKeyCode() == KeyEvent.VK_SPACE){
						String codeText = getText();
						codeText = codeText.substring(0, getCaretPosition());
						codeText = codeText.substring(codeText.lastIndexOf('\n') + 1).trim();
						if(ContentTokenizer.isConditionalCode(codeText))
							call = true;
						else
							contentWindow.setVisible(false);
						return;
					}
					if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP || code == KeyEvent.VK_ENTER) {
						if((contentWindow.index == 0 && code == KeyEvent.VK_UP) || ((contentWindow.index == contentWindow.hints.size() - 1) && code == KeyEvent.VK_DOWN)) {
							contentWindow.setVisible(false);
							return;
						}
						e.consume();
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		synchronized(Editor.class){
			if(e.getKeyCode() != VK_SHIFT)
				keyCache -= e.getKeyCode();

			if(keyCache < 0)
				keyCache = 0;

			switch(e.getKeyChar()){
				case ',':
				insert(" ", getCaretPosition());
				return;
				default:
			}

			if(currentFile != null) {
				//Code Assist
				char c = e.getKeyChar();
				if(Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '$' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if(DataManager.isContentAssistRealTime()){
						contentWindow.setIgnoreGenViewOnce(keyCache != 0);
						call = true;
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	//Managing Smart type code completion
	private void autoSymbolExclusion(KeyEvent e) {
		try {
			switch(getText().charAt(getCaretPosition() - 1)) {
				case '\"':
				if(getText().charAt(getCaretPosition()) == '\"')
				getDocument().remove(getCaretPosition(), 1);
				break;
				case '\'':
				if(getText().charAt(getCaretPosition()) == '\'')
				getDocument().remove(getCaretPosition(), 1);
				break;
				case '<':
				if(getText().charAt(getCaretPosition()) == '>')
					getDocument().remove(getCaretPosition(), 1);
				break;
				case '(':
				if(getText().charAt(getCaretPosition()) == ')')
					getDocument().remove(getCaretPosition(), 1);
				break;
				case '[':
				if(getText().charAt(getCaretPosition()) == ']')
					getDocument().remove(getCaretPosition(), 1);
				break;
				default:
			}
		}
		catch(Exception ex){
			//ex.printStackTrace();
		}
	}

	private void autoSymbolCompletion(KeyEvent e) {
		try {
			switch (e.getKeyChar()) {
				case '(':
				if(getText().charAt(getCaretPosition()) != ')'){
					insert(")", getCaretPosition());
					setCaretPosition(getCaretPosition() - 1);
				}
				break;
				case '[':
				if(getText().charAt(getCaretPosition()) != ']'){
					insert("]", getCaretPosition());
					setCaretPosition(getCaretPosition() - 1);
				}
				break;
				case '\"':
				if(getText().charAt(getCaretPosition() - 1) != '\\'){
					insert("\"", getCaretPosition());
					setCaretPosition(getCaretPosition() - 1);
				}
				break;
				case '\'':
				if(getText().charAt(getCaretPosition() - 1) != '\\'){
					insert("\'", getCaretPosition());
					setCaretPosition(getCaretPosition() - 1);
				}
				break;
				default:
				break;
			}
		}
		catch(Exception ex) {
			//ex.printStackTrace();
		}
	}

	public class FindAndReplace extends JComponent{
		public ReplaceToolBar replaceToolBar;
		public FindAndReplace(){
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(400, 60));
			replaceToolBar = new ReplaceToolBar(Editor.this);
			add(replaceToolBar, BorderLayout.CENTER);
			setVisible(false);

		}
		@Override
		public void setVisible(boolean value){
			try{
				replaceToolBar.getSearchContext().setMarkAll(value);
			}
			catch(Exception e){
				System.err.println(e);
			}
			super.setVisible(value);
		}
	}

	@Override
	public void searchEvent(SearchEvent e) {
		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result;
		switch (type) {
			default:
			case MARK_ALL:
			result = SearchEngine.markAll(this, context);
			break;
			case FIND:
			result = SearchEngine.find(this, context);
			if (!result.wasFound() || result.isWrapped()) {
				javax.swing.UIManager.getLookAndFeel().provideErrorFeedback(this);
			}
			break;
			case REPLACE:
			result = SearchEngine.replace(this, context);
			if (!result.wasFound() || result.isWrapped()) {
				javax.swing.UIManager.getLookAndFeel().provideErrorFeedback(this);
			}
			break;
			case REPLACE_ALL:
			result = SearchEngine.replaceAll(this, context);
			break;
		}
		String text;
		if (result.wasFound()) {
			text = "Text found. occurrences marked: " + result.getMarkedCount();
		}
		else if (type == SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount() > 0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			}
			else {
				text = "";
			}
		}
		else {
			text = "Text not found";
		}
		screen.getToolMenu().setTask(text);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		keyCache = 0;
		contentWindow.setVisible(false);
		if(lastSearchContext != null){
			lastSearchContext.setMarkAll(false);
			fAndR.replaceToolBar.fireSearchEvent(new SearchEvent(this, SearchEvent.Type.MARK_ALL, lastSearchContext));
			lastSearchContext = null;
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
	@Override
	public void mouseMoved(MouseEvent arg0) {

	}
	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void focusGained(FocusEvent e){
		keyCache = 0;
		screen.focussedEditor = Editor.this;
	}

	@Override
	public void focusLost(FocusEvent e){
		keyCache = 0;
	}

	public FindAndReplace getFAndR() {
		return fAndR;
	}

	public RTextScrollPane getAttachment() {
		return scrollPane;
	}

	public JPanel getTabHolderPanel(){
		return tabHolderPanel;
	}

	public void setTabHolderPanel(JPanel panel){
		tabHolderPanel = panel;
		tabHolderPanel.add(getFAndR(), BorderLayout.NORTH);
	}
}


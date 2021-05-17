package omega.utils;
import org.fife.ui.rsyntaxtextarea.modes.*;
import omega.token.factory.*;
import omega.highlightUnit.BasicHighlight;
import org.fife.ui.rtextarea.SearchResult;
import java.awt.event.MouseEvent;
import omega.utils.systems.View;
import omega.Screen;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.rsta.ui.search.SearchEvent;
import javax.swing.JScrollPane;
import org.fife.ui.rtextarea.RTextArea;
import java.awt.Dimension;
import java.awt.BorderLayout;
import org.fife.rsta.ui.search.ReplaceToolBar;
import javax.swing.JComponent;
import omega.framework.IndentationFramework;
import omega.gset.Generator;
import omega.framework.ImportFramework;
import omega.snippet.SnippetBase;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.BufferedReader;
import java.awt.Font;
import javax.swing.DropMode;
import java.awt.Color;
import omega.deassembler.*;
import omega.framework.CodeFramework;
import java.awt.Image;
import org.fife.ui.rsyntaxtextarea.Theme;
import javax.swing.JFileChooser;
import java.io.File;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.rsta.ui.search.SearchListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
public class Editor extends RSyntaxTextArea implements KeyListener, MouseListener, SearchListener {
	private static Screen screen;
	private RTextScrollPane scrollPane;
	public volatile File currentFile;
	private static PrintArea printArea;
	private volatile JFileChooser chooser = new JFileChooser();
	private volatile String savedText = "";
	public static KeyListener keyListener;
	private static Theme theme;
	private static String currentTheme = "no-theme";
	private FindAndReplace fAndR;
	private static boolean launched = false;
	private volatile boolean call = false;
	public ContentWindow contentWindow;
	private volatile boolean ctrl;
	private volatile boolean shift;
	private volatile boolean o; // Auto-Imports
	private volatile boolean f; // Find and Replace
	private volatile boolean r; // Run
	private volatile boolean b; // Build
	private volatile boolean s; // Save
	private volatile boolean c; // Click Editor Image
	private volatile boolean g; // getters and setters
	private volatile boolean i; // override methods
	public Editor(Screen screen) {
		super();
		Editor.screen = screen;
          
		scrollPane = new RTextScrollPane(this, true);
		scrollPane.setFoldIndicatorEnabled(true);
		scrollPane.setBackground(UIManager.c2);
          
		fAndR = new FindAndReplace();
		initView();
		printArea = new PrintArea("File Operation Log", screen);
		addCaretListener((e)-> {
			String text = getSelectedText();
			if(text == null || text.equals(""))
				Screen.getScreen().getBottomPane().jumpField.setText("Goto Line");
			else
				Screen.getScreen().getBottomPane().jumpField.setText(text.length() + "");
		});
		createNewContent();
	}
	private void createNewContent() {
		contentWindow = new ContentWindow(this);
		addKeyListener(contentWindow);
		launchContentAssist();
		setLayout(null);
		add(contentWindow);
	}
	public static void launchContentAssist() {
		if(launched) return;
		launched = true;
		new Thread(()->{
			while(screen.active) {
				try {
					if(screen.getCurrentEditor() != null)
						screen.getCurrentEditor().readCode();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public synchronized void readCode() {
		if(call) {
			call = false;
			if(!CodeFramework.resolving) {
				ContentTokenizer.arrangeTokens(this);
			}
		}
	}
	public FindAndReplace getFAndR() {
		return fAndR;
	}
	public RTextScrollPane getAttachment() {
		return scrollPane;
	}
	private void initView() {
		addKeyListener((keyListener = this));
		addMouseListener(this);
		setAnimateBracketMatching(true);
		setAntiAliasingEnabled(true);
		setAutoIndentEnabled(true);
		setAutoscrolls(true);
		setBracketMatchingEnabled(true);
		setCloseCurlyBraces(true);
		setPaintMatchedBracketPair(true);
		setTabsEmulated(true);
		setHyperlinksEnabled(true);
		setHyperlinkForeground(Color.GREEN);
		setCodeFoldingEnabled(true);
		setFadeCurrentLineHighlight(false);
		setShowMatchedBracketPopup(true);
		setHighlightSecondaryLanguages(true);
		setDragEnabled(true);
		setDropMode(DropMode.USE_SELECTION);
		UIManager.setData(this);
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
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_BBCODE);
		else if(f.getName().endsWith(".c"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_C);
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
		else if(f.getName().endsWith(".groovy"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_GROOVY);
		else if(f.getName().endsWith(".html") || f.getName().endsWith(".svg"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_HTML);
		else if(f.getName().endsWith(".ini"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_INI);
		else if(f.getName().endsWith(".java"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JAVA);
		else if(f.getName().endsWith(".js"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JAVASCRIPT);
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
		else if(f.getName().endsWith(".xml"))
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
			String name = omega.utils.UIManager.isDarkMode() ? "dark" : "idea";
			theme = Theme.load(Editor.class.getResourceAsStream("/"+name+".xml"));
			theme.apply(this);
		}
		catch (Exception e) {
			
		}
		try {
			screen.getUIManager().loadData();
			setFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
			UIManager.setData(screen.getTabPanel());
		}
		catch(Exception e) {
			
		}
	}
	
	public static Theme getTheme() {
		if(theme == null){
			try {
				String name = omega.utils.UIManager.isDarkMode() ? "dark" : "idea";
				theme = Theme.load(Editor.class.getResourceAsStream("/"+name+".xml"));
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
			if(currentFile != null)
				{
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
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void saveCurrentFile()
	{
		if(savedText.equals(getText()))
			return;
		if(currentFile == null)
			{
			int res = JOptionPane.showConfirmDialog(screen, "Data in the editor does not corresponds to any existing file. Do you want to save it as a type?", "Save or not?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);;
			if(res == JOptionPane.OK_OPTION)
				saveFileAs();
			return;
		}
		if(!currentFile.exists())
			{
			int res = JOptionPane.showConfirmDialog(screen, "Data in the editor does not corresponds to any existing file. Do you want to save it as a type?", "Save or not?", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);;
			if(res == JOptionPane.OK_OPTION)
				saveFileAs();
			return;
		}
		try {
			String text = getText();
			savedText = text;
			PrintWriter writer = new PrintWriter(new FileOutputStream(currentFile));
			writer.print(text);
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void saveImage() {
		try {
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			paint(image.getGraphics() != null ? image.createGraphics() : image.getGraphics());
			String path = Screen.getFileView().getProjectPath();
			new File(Screen.getFileView().getProjectPath() + File.separator + "out").mkdir();
			path += File.separator + "out" + File.separator + currentFile.getName() + "_lines_" + getLineCount() + ".jpg";
			if(ImageIO.write(image, "JPG", new File(path))) {
				Screen.getProjectView().reload();
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	public void saveFileAs() {
		chooser.setCurrentDirectory(new File(Screen.getFileView().getProjectPath() + File.separator + "src"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int res = chooser.showSaveDialog(screen);
		if(res == JFileChooser.APPROVE_OPTION)
			{
			try {
				File file = chooser.getSelectedFile();
				PrintWriter writer = new PrintWriter(new FileOutputStream(file));
				writer.println(getText());
				writer.close();
				Screen.getProjectView().reload();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void closeFile()
	{
		if(currentFile == null)
			return;
		saveCurrentFile();
		currentFile = null;
		setText("");
		savedText = "";
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
		}catch(Exception e) {System.out.println(e);}
		}
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
				printArea.print("File is Open Somewhere, Unable to delete "+currentFile.getName()+" -Located in \""+currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().lastIndexOf('/'))+"\"");
				printArea.setVisible(true);
			}
			else {
				printArea.print("Successfully Deleted "+currentFile.getName());
				Screen.getProjectView().reload();
				savedText = "";
				currentFile = null;
				Screen.getProjectView().reload();
			}
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
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
		}else file.delete();
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
						Screen.getProjectView().reload();
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
					Screen.getProjectView().reload();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		BasicHighlight.highlightJava(this);
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_CONTROL)
			ctrl = true;
		else if(code == KeyEvent.VK_SHIFT)
			shift = true;
		else if(code == KeyEvent.VK_O)
			o = true;
		else if(code == KeyEvent.VK_F)
			f = true;
		else if(code == KeyEvent.VK_R)
			r = true;
		else if(code == KeyEvent.VK_S)
			s = true;
		else if(code == KeyEvent.VK_B)
			b = true;
		else if(code == KeyEvent.VK_C)
			c = true;
		else if(code == KeyEvent.VK_G)
			g = true;
		else if(code == KeyEvent.VK_I)
			i = true;
		
		if(ctrl && shift && f) {
			fAndR.setVisible(!fAndR.isVisible());
			f = false;
			ctrl = false;
			shift = false;
		}
		if(ctrl && s){
			saveCurrentFile();
			s = false;
			ctrl = false;
			shift = false;
		}
		if(ctrl && b && screen.getToolMenu().buildComp.isClickable()){
			Screen.getBuildView().compileProject();
			b = false;
			ctrl = false;
			shift = false;
		}
		if(ctrl && shift && c){
			saveImage();
			c = false;
			ctrl = false;
			shift = false;
		}
		if(code == KeyEvent.VK_TAB){
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
			if(SnippetBase.hasSnippet(codeX)){
				SnippetBase.insertSnippet(this, codeX, index = getCaretPosition() - codeX.length(), cx.substring(0, cx.indexOf(codeX)));
				e.consume();
			}
		}
		if(code == KeyEvent.VK_BACK_SPACE)
			autoSymbolExclusion(e);
		else
			autoSymbolCompletion(e);
		if(currentFile != null) {
			//Managing KeyBoard Shortcuts
			if(ctrl && shift && o && currentFile.getName().endsWith(".java")) {
				ImportFramework.addImports(ImportFramework.findClasses(getText()), this);
				o = false;
				ctrl = false;
				shift = false;
			}
			
			if(ctrl && shift && g && currentFile.getName().endsWith(".java")) {
				Generator.gsView.genView(this);
				g = false;
				ctrl = false;
				shift = false;
			}
			
			if(ctrl && !shift && i && currentFile.getName().endsWith(".java")) {
				IndentationFramework.indent(this);
				i = false;
				ctrl = false;
				shift = false;
			}
			
			if(ctrl && shift && i && currentFile.getName().endsWith(".java")) {
				Generator.overView.genView(this);
				i = false;
				ctrl = false;
				shift = false;
			}
			if(ctrl && shift && r && screen.getToolMenu().buildComp.isClickable()){
				Screen.getRunView().run();
				r = false;
				ctrl = false;
				shift = false;
			}
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
					contentWindow.setVisible(false);
					return;
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER) {
					if((contentWindow.index == 0 && e.getKeyCode() == KeyEvent.VK_UP) || ((contentWindow.index == contentWindow.hints.size() - 1) && e.getKeyCode() == KeyEvent.VK_DOWN)) {
						contentWindow.setVisible(false);
						return;
					}
					e.consume();
				}
			}
			
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyChar()){
			case ',':
			insert(" ", getCaretPosition());
			return;
			default:
		}
		
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_CONTROL)
			ctrl = false;
		else if(code == KeyEvent.VK_SHIFT)
			shift = false;
		else if(code == KeyEvent.VK_O)
			o = false;
		else if(code == KeyEvent.VK_F)
			f = false;
		else if(code == KeyEvent.VK_R)
			r = false;
		else if(code == KeyEvent.VK_S)
			s = false;
		else if(code == KeyEvent.VK_B)
			b = false;
		else if(code == KeyEvent.VK_C)
			c = false;
		else if(code == KeyEvent.VK_G)
			g = false;
		else if(code == KeyEvent.VK_I)
			i = false;
		if(currentFile != null) {
			if(!screen.isVisible()) {
				return;
			}
			//Code Assist
			char c = e.getKeyChar();
			if(Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '$' || code == KeyEvent.VK_BACK_SPACE) {
				if(DataManager.isContentAssistRealTime())
					call = true;
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
	}catch(Exception ex){ System.err.println(ex); }
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
	}catch(Exception ex) { System.err.println(ex); }
	}
	public class FindAndReplace extends JComponent{
		private ReplaceToolBar replaceToolBar;
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
		}catch(Exception e){ System.err.println(e); }
			super.setVisible(value);
		}
	}
	private class PrintArea extends View {
		private RTextArea textArea;
		public PrintArea(String title, Screen window) {
			super(title, window);
			setModal(false);
			setLayout(new BorderLayout());
			setSize(300, 150);
			setLocationRelativeTo(null);
			init();
		}
		private void init() {
			textArea = new RTextArea("Operation Progress : ");
			textArea.setEditable(false);
			textArea.setAutoscrolls(true);
			textArea.setHighlightCurrentLine(false);
			UIManager.setData(textArea);
			textArea.setFont(omega.settings.Screen.PX16);
			JScrollPane p = new JScrollPane(textArea);
			p.setAutoscrolls(true);
			add(p, BorderLayout.CENTER);
			comps.add(textArea);
		}
		public void print(String text) {
			textArea.append("\n" + text);
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
			text = "Text found; occurrences marked: " + result.getMarkedCount();
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
		screen.getToolMenu().setTask(!text.equals("") ? text : "Hover to see Memory Statistics");
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
		screen.focussedEditor = Editor.this;
		contentWindow.setVisible(false);
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
}

package ide.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreePath;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import codePoint.ImportFramework;
import deassembler.CodeFramework;
import deassembler.ContentTokenizer;
import deassembler.ContentWindow;
import ide.Screen;
import ide.utils.systems.EditorTools;
import ide.utils.systems.View;
import importIO.ImportManager;
import snippet.SnippetBase;

public class Editor extends RSyntaxTextArea implements KeyListener, MouseListener {

	private static Screen screen;
	private RTextScrollPane scrollPane;
	public volatile File currentFile;
	private static volatile PrintArea printArea;
	private volatile JFileChooser chooser = new JFileChooser();
	private volatile String savedText = "";
	public static KeyListener keyListener;
	private static Theme theme;
	private static String currentTheme = "no-theme";
	private FindAndReplace fAndR;
	private static boolean launched = false;
	private volatile boolean call = true;
	public ContentWindow contentWindow;
	private volatile boolean ctrl_pressed;
	private volatile boolean shift_pressed;
	private volatile boolean o_pressed;

     //FindAndReplace is Under Development [last updated Omega IDE v13.1 beta]
	public class FindAndReplace extends JComponent {
		private JButton findBtn;
		private JButton replaceBtn;
		private JButton nextBtn;
		private JButton previousBtn;
		private JTextArea findArea;
		private JTextArea replaceArea;
		private JScrollPane findPane;
		private JScrollPane replacePane;
		private JButton closeBtn;
		private SearchResult sr;

		public FindAndReplace() {
			setVisible(false);
			setLayout(new FlowLayout());
			setSize(10, 30);
			setPreferredSize(getSize());

			findArea = new JTextArea("Search Text Here");

			replaceArea = new JTextArea("Replace Text Here");

			findBtn = new JButton("Find");
			findBtn.addActionListener((e)->{
				sr = SearchEngine.find(Editor.this, new SearchContext(findArea.getText()));
			});
			nextBtn = new JButton("Next");
			previousBtn = new JButton("Previous");
			replaceBtn = new JButton("Replace");
			replaceBtn.addActionListener((e)->{
				SearchEngine.replace(Editor.this, new SearchContext(replaceArea.getText()));
			});
			closeBtn = new JButton("X");
			closeBtn.addActionListener((e)->{
				setVisible(false);
			});

			add(findBtn);
			add((findPane = new JScrollPane(findArea)));
			add(replaceBtn);
			add((replacePane = new JScrollPane(replaceArea)));
			add(nextBtn);
			add(previousBtn);
			add(closeBtn);

			UIManager.setData(findBtn);
			UIManager.setData(replaceBtn);
			UIManager.setData(findArea);
			UIManager.setData(replaceArea);
			UIManager.setData(nextBtn);
			UIManager.setData(previousBtn);
			UIManager.setData(closeBtn);
			UIManager.setData(this);
		}
		@Override
		public void paint(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());

			findBtn.repaint();
			replaceBtn.repaint();
			findArea.repaint();
			replaceArea.repaint();
			nextBtn.repaint();
			previousBtn.repaint();
			closeBtn.repaint();
			findPane.repaint();
			replacePane.repaint();
		}
	}

	public Editor(Screen screen) {
		super();
		Editor.screen = screen;
		scrollPane = new RTextScrollPane(this);
		scrollPane.setLineNumbersEnabled(true);
		scrollPane.setFoldIndicatorEnabled(true);
		fAndR = new FindAndReplace();
		initView();
		printArea = new PrintArea("File Operation Log", screen);
		createNewContent();
	}

	private void createNewContent() {
		contentWindow = new ContentWindow();
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
				}catch(Exception e) {}
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

	private void initView()
	{
		addKeyListener((keyListener = this));
		addMouseListener(this);		
		setAnimateBracketMatching(true);
		setAntiAliasingEnabled(true);
		setAutoIndentEnabled(true);
		setAutoscrolls(true);
		setBracketMatchingEnabled(true);
		setCloseCurlyBraces(true);
		setPaintMatchedBracketPair(true);
		setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
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
		setLayout(null);
	}

	public static void setStyle(Editor e, File f) {
		if(f.getName().endsWith(".html"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_HTML);
		else if(f.getName().endsWith(".js"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JAVASCRIPT);
		else if(f.getName().endsWith(".py"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PYTHON);
		else if(f.getName().endsWith(".json"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_JSON);
		else if(f.getName().endsWith(".php"))
			e.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_PHP);
		else if(f.getName().endsWith(".xml") || f.getName().endsWith(".fxml"))
			e.setSyntaxEditingStyle(SYNTAX_STYLE_XML);
		else if(f.getName().endsWith(".java"))
			e.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
		else if(f.getName().endsWith(".rs"))
			e.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		else if(f.getName().endsWith(".sh") || f.getName().endsWith(".run"))
			e.setSyntaxEditingStyle(SYNTAX_STYLE_UNIX_SHELL);
		else
			e.setSyntaxEditingStyle(null);
	}

	public void loadTheme()
	{
		try {
			if(!currentTheme.equals(DataManager.getEditorColoringScheme()))
			{
				currentTheme = DataManager.getEditorColoringScheme();
				theme = Theme.load(Editor.class.getResourceAsStream("/"+DataManager.getEditorColoringScheme()+".xml"));
			}
			theme.apply(this);
		} catch (Exception e) { }
		try {
			screen.getUIManager().loadData();
			setFont(new Font(UIManager.fontName,Font.BOLD, UIManager.fontSize));
			UIManager.setData(screen.getTabPanel());
		}catch(Exception e) { }

	}
    
	public static Theme getTheme() {
		try {
			if(!currentTheme.equals(DataManager.getEditorColoringScheme()))
			{
				currentTheme = DataManager.getEditorColoringScheme();
				theme = Theme.load(Editor.class.getResourceAsStream("/"+DataManager.getEditorColoringScheme()+".xml"));
			}
		}
		catch(Exception e) {e.printStackTrace();}
		return theme;
	}

	public synchronized void loadFile(File file)
	{
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
		}catch(Exception e) {e.printStackTrace();}
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
		}catch(Exception e) {e.printStackTrace();}
	}

	public void saveImage() {
		try {
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			paint(image.getGraphics() != null ? image.createGraphics() : image.getGraphics());
			String path = Screen.getFileView().getProjectPath();
			path += "/out/" + currentFile.getName() + "_lines_" + getLineCount() + ".jpg";
			if(ImageIO.write(image, "JPG", new File(path))) {
				System.out.println("Created Image " + path);
				Screen.getProjectView().reload();
			}
			else
				System.out.println("Unable to Create Image " + path);
		}catch(Exception e) {System.out.println(e);}
	}

	public void saveFileAs()
	{
		chooser.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()+"/src"));
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
			}catch(Exception e) {e.printStackTrace();}
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

	public void deleteFile()
	{
		try {
			if(currentFile == null)
				return;
			if(!currentFile.exists())
				return;
			Screen.getProjectView().setVisible(false);
			int res0 = JOptionPane.showConfirmDialog(screen, "Do you want to delete "+currentFile.getName()+"?", "Delete or not?", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);;
			if(res0 != JOptionPane.YES_OPTION)
				return;

			printArea.setVisible(true);
			closeFile();
			if(!currentFile.delete()) {
				printArea.print("File is Open Somewhere, Unable to delete "+currentFile.getName()+" -Located in \""+currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().lastIndexOf('/'))+"\"");
			}

			else {
				printArea.print("Successfully Deleted "+currentFile.getName());
				Screen.getProjectView().reload();
				savedText = "";
				currentFile = null;
				ImportManager.readSource(EditorTools.importManager);
				Screen.getProjectView().reload();
			}
		}catch(Exception e) {System.err.println(e.getMessage());}
	}

	public static void deleteDir(File file) throws Exception
	{

		if (file.isDirectory())
		{

			/*
			 * If directory is empty, then delete it
			 */
			if (file.list().length == 0)
			{
				file.delete();
			}
			else
			{
				// list all the directory contents
				File files[] = file.listFiles();

				for (File fileDelete : files)
				{
					/*
					 * Recursive delete
					 */
					deleteDir(fileDelete);
				}

				/*
				 * check the directory again, if empty then 
				 * delete it.
				 */
				if (file.list().length == 0)
				{
					file.delete();
				}
			}

		}else file.delete();
	}

	public static void deleteFile(File currentFile)
	{
		try {
			if(currentFile == null)
				return;
			if(currentFile.isDirectory()) {
				int res0 = JOptionPane.showConfirmDialog(screen, "Do you want to delete "+currentFile.getName()+"?", "Delete or not?", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);;
				if(res0 != JOptionPane.YES_OPTION)
					return;
				printArea.setVisible(true);
				try {
					deleteDir(currentFile);
					printArea.print("Successfully Deleted "+currentFile.getName());
					ImportManager.readSource(EditorTools.importManager);
					Screen.getProjectView().reload();
				}catch(Exception e) {
					printArea.print("File is Open Somewhere, Unable to delete directory "+currentFile.getName()+" -Located in \""+currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().lastIndexOf('/'))+"\"");
				}
				return;
			}
			if(!currentFile.exists())
				return;
			Screen.getProjectView().setVisible(false);
			int res0 = JOptionPane.showConfirmDialog(screen, "Do you want to delete "+currentFile.getName()+"?", "Delete or not?", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);;
			if(res0 != JOptionPane.YES_OPTION)
				return;
			printArea.setVisible(true);
			if(!currentFile.delete()) {
				printArea.print("File is Open Somewhere, Unable to delete "+currentFile.getName()+" -Located in \""+currentFile.getAbsolutePath().substring(0, currentFile.getAbsolutePath().lastIndexOf('/'))+"\"");
			}

			else {
				printArea.print("Successfully Deleted "+currentFile.getName());
				Screen.getProjectView().reload();
				ImportManager.readSource(EditorTools.importManager);
			}
		}catch(Exception e) {System.err.println(e.getMessage());}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(currentFile != null) {
			if(currentFile.getName().endsWith(".java")) {
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_BACK_SPACE)
					autoSymbolExclusion(e);
				else
					autoSymbolCompletion(e);
				//Managing KeyBoard
				if(code == KeyEvent.VK_CONTROL)
					ctrl_pressed = true;
				else if(code == KeyEvent.VK_SHIFT)
					shift_pressed = true;
				else if(code == KeyEvent.VK_O)
					o_pressed = true;

				if(ctrl_pressed && shift_pressed && o_pressed) {
					ImportFramework.addImports(ImportFramework.findClasses(getText()), this);
					ctrl_pressed = false;
					shift_pressed = false;
					o_pressed = false;
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

				if(contentWindow.isVisible()) {
					if(e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_END) {
						contentWindow.setVisible(false);
						return;
					}
					if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(DataManager.isContentAssistRealTime())
							call = true;
						return;
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER) {
						if((contentWindow.pointer == 0 && e.getKeyCode() == KeyEvent.VK_UP) || (contentWindow.pointer == contentWindow.max && e.getKeyCode() == KeyEvent.VK_DOWN)) {
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
		switch(e.getKeyChar()){
		case ',':
			insert(" ", getCaretPosition());
			return;
		default:
		}

		if(currentFile != null) {
			if(currentFile.getName().endsWith(".java")) {
				if(!screen.isVisible()) {
					return;
				}
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_CONTROL)
					ctrl_pressed = false;
				else if(code == KeyEvent.VK_SHIFT)
					shift_pressed = false;
				else if(code == KeyEvent.VK_O)
					o_pressed = false;
				//Shortcut
				if(code == KeyEvent.VK_F2) {
					if(Screen.getScreen().getToolMenu().runComp.isClickable() && Screen.getScreen().getToolMenu().buildComp.isClickable())
						Screen.getRunView().run();
				}
				//Code Assist
				char c = e.getKeyChar();
				if(Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '$' || code == KeyEvent.VK_BACK_SPACE) {
					if(DataManager.isContentAssistRealTime())
						call = true;
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
		}catch(Exception ex){ System.err.println(ex); }
	}

	private void autoSymbolCompletion(KeyEvent e) {
		try {
			switch (e.getKeyChar()) {
			case '(':
				if(getText().charAt(getCaretPosition()) != ')'){
					System.out.println(getText().charAt(getCaretPosition()));
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

	private class PrintArea extends View {

		private RTextArea textArea;

		public PrintArea(String title, Screen window) 
		{
			super(title, window);
			setModal(false);
			setLayout(new BorderLayout());
			setSize(300,150);
			setLocationRelativeTo(null);				
			init();
		}

		private void init()
		{
			textArea = new RTextArea("Operation Progress : ");
			textArea.setEditable(false);
			textArea.setAutoscrolls(true);
			textArea.setHighlightCurrentLine(false);
			UIManager.setData(textArea);
               textArea.setFont(settings.Screen.PX16);
			JScrollPane p = new JScrollPane(textArea);
			p.setAutoscrolls(true);
			add(p, BorderLayout.CENTER);
			comps.add(textArea);

			setAction(()->{
				textArea.setText("Operation Progress : ");
			});
		}

		public void print(String text)
		{
			textArea.append("\n"+text);
		}

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
		contentWindow.setVisible(false);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}

package omega.utils;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import omega.Screen;
import omega.highlightUnit.ErrorHighlighter;
import org.fife.ui.rtextarea.RTextArea;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import omega.highlightUnit.Highlight;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.util.LinkedList;
import javax.swing.UIManager;
import java.awt.Font;
import omega.comp.TextComp;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.JPanel;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class BuildLog extends JPanel {
	private LinkedList<TextComp> fileComps = new LinkedList<>();
	private TextComp headComp;
	private Error currentError;
	private JSplitPane splitPane;
	private JScrollPane fileScrollPane;
	private JScrollPane errorScrollPane;
	private JPanel filePanel;
	private RTextArea errorArea;
	private int block;
	private int maxW;
	public BuildLog(){
		super(new BorderLayout());
		setBackground(c2);
		headComp = new TextComp("Build Resulted in the following Error(s)", c1, c3, c2, null);
		headComp.setFont(PX14);
		headComp.setClickable(false);
		headComp.setArc(0, 0);
		headComp.setPreferredSize(new Dimension(300, 30));
		add(headComp, BorderLayout.NORTH);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(fileScrollPane = new JScrollPane(filePanel = new JPanel(null)));
		splitPane.setRightComponent(errorScrollPane = new JScrollPane(errorArea = new RTextArea()));
		
		filePanel.setBackground(c2);
		errorArea.setBackground(c2);
		errorArea.setForeground(glow);
		errorArea.setFont(PX14);
		errorArea.setEditable(false);
		splitPane.setBackground(c2);
		filePanel.setBackground(c2);
		add(splitPane, BorderLayout.CENTER);
	}
	public void genView(String log){
		fileComps.forEach(filePanel::remove);
		fileComps.clear();
		LinkedList<Error> errors = new LinkedList<>();
		StringTokenizer tok = new StringTokenizer(log, "\n");
		String error = "";
		String filePath = "";
		int lineN = 0;
		boolean justStarted = true;
		while(tok.hasMoreTokens()){
			String line = tok.nextToken();
			if(line.contains(".java:")){
				if(!justStarted)
					errors.add(new Error(filePath, lineN, error));
				justStarted = false;
				filePath = "";
				if(File.separator.equals("\\"))
					filePath = line.substring(0, line.indexOf(':', line.indexOf(':') + 1));
				else
				filePath = line.substring(0, line.indexOf(':'));
				if(!filePath.equals("")){
					if(File.separator.equals("\\")){
						lineN = Integer.parseInt(line.substring(line.indexOf(':', line.indexOf(':') + 1) + 1, line.indexOf(':', line.indexOf(':', line.indexOf(':') + 1))));
						error = line.substring(line.indexOf(':', line.indexOf(':', line.indexOf(':') + 1)) + 1);
					}
					else{
						lineN = Integer.parseInt(line.substring(line.indexOf(':') + 1, line.indexOf(':', line.indexOf(':') + 1)));
						error = line.substring(line.indexOf(':', line.indexOf(':') + 1) + 1);
					}
					error = error.trim() + "\n" + "\n";
				}
			}
			else{
				error += line + "\n";
			}
		}
		if(!filePath.equals(""))
			errors.add(new Error(filePath, lineN, error));
		
		if(errors.isEmpty())
			return;
		block = 0;
		maxW = 0;
		Graphics g = Screen.getScreen().getGraphics();
		g.setFont(PX14);
		errors.forEach(errorSet->{
			int w = g.getFontMetrics().stringWidth(errorSet.getFileName() + " : " + errorSet.line) + 20;
			if(w > maxW)
				maxW = w;
		});
		errors.forEach(errorSet->{
			TextComp errorComp = new TextComp(errorSet.getFileName() + " : " + errorSet.line, color1, c2, isDarkMode() ? ErrorHighlighter.color : color2, null);
			errorComp.setRunnable(()->{
				setView(errorSet);
			});
			errorComp.setBounds(0, block, maxW + 50, 25);
			errorComp.setFont(PX14);
			errorComp.setArc(0, 0);
			errorComp.alignX = 5;
			filePanel.add(errorComp);
			fileComps.add(errorComp);
			block += 25;
			TextComp fixedComp = new TextComp("fixed", c1, c2, c3, null);
			fixedComp.setBounds(maxW, 1, 30, 23);
			fixedComp.setArc(5, 5);
			fixedComp.setFont(PX12);
			fixedComp.setRunnable(()->{
				fixedComp.setClickable(false);
				errorComp.setColors(c1, c3, c2);
				errorSet.solved = true;
			});
			errorComp.add(fixedComp);
		});
		filePanel.setPreferredSize(new Dimension(maxW + 50, block));
		splitPane.setDividerLocation(maxW + 65);
		setView(errors.get(0));
	}
	public void setHeading(String heading){
		headComp.setText(heading);
		repaint();
	}
	public void setView(Error errorSet){
		this.currentError = errorSet;
		String fullLog = "File Path : " + errorSet.filePath + "\n";
		fullLog += "At Line   : " + errorSet.line + "\n\n";
		fullLog += errorSet.log;
		errorArea.setText(fullLog);
		try{
			//Highlighting
			String text = errorArea.getText();
			Highlighter h = errorArea.getHighlighter();
			Color color = isDarkMode() ? Color.WHITE : ErrorHighlighter.color;
			//Highlighting File Path
			h.addHighlight(text.indexOf(':') + 2, text.indexOf('\n'), new DefaultHighlighter.DefaultHighlightPainter(color));
			//Highlighting Line Number
			h.addHighlight(text.indexOf(':', text.indexOf('\n') + 1) + 2, text.indexOf('\n', text.indexOf('\n') + 1), new DefaultHighlighter.DefaultHighlightPainter(color));
			//Highlighting Concluding Error
			h.addHighlight(text.indexOf(':', text.indexOf('\n', text.indexOf('\n', text.indexOf('\n') + 1) + 1)) + 2, text.indexOf('\n', text.indexOf('\n', text.indexOf('\n', text.indexOf('\n', text.indexOf('\n') + 1) + 1) + 1)), new DefaultHighlighter.DefaultHighlightPainter(color));
			new Thread(()->{
				Editor editor = Screen.getScreen().getTabPanel().findEditor(new File(errorSet.filePath));
				if(editor != null){
					String textM = editor.getText();
					int lineN = 0;
					for(int i = 0; i < textM.length(); i++){
						char ch = textM.charAt(i);
						if(ch == '\n')
							lineN++;
						if(lineN == errorSet.line){
							Screen.getScreen().getTabPanel().getTabPane().setSelectedIndex(Screen.getScreen().getTabPanel().getEditors().indexOf(editor));
							editor.setCaretPosition(i);
							break;
						}
					}
				}
			}).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void paint(Graphics graphics){
		if(fileComps.isEmpty() && headComp.getText().contains("following")){
			headComp.setText("Build Process was interrupted before it could finish!");
		}
		if(fileComps.isEmpty()){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(c2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(c3);
			g.setFont(PX18);
			g.drawString(headComp.getText(), getWidth()/2 - g.getFontMetrics().stringWidth(headComp.getText())/2,
			getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		}
		else{
			super.paint(graphics);
		}
	}
	private class Error {
		private String filePath;
		private int line;
		private String log;
		private boolean solved = false;
		public Error(String filePath, int line, String log){
			this.filePath = filePath;
			this.line = line;
			this.log = log;
		}
		public String getFileName(){
			return filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
		}
		@Override
		public String toString(){
			return "File Path : " + filePath + "\n" +
			"At Line : " + line + "\n"+
			"Error : " + log;
		}
	}
}

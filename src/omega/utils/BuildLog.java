/**
* Java Error Logger
* Copyright (C) 2021 Omega UI

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* * the Free Software Foundation, either version 3 of the License, or
* * (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.utils;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;

import omega.Screen;

import javax.sound.sampled.Line;

import omega.highlightUnit.ErrorHighlighter;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.fife.ui.rtextarea.RTextArea;

import omega.comp.TextComp;

import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;

import omega.instant.support.java.JavaSyntaxParser;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
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
		headComp = new TextComp("Build Resulted in the following Error(s)", TOOLMENU_COLOR5_SHADE, TOOLMENU_COLOR5, c2, null);
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
					errors.add(new Error(JavaSyntaxParser.convertToProjectPath(filePath), lineN, error));
				justStarted = false;
				filePath = "";
				if(File.separator.equals("\\"))
					filePath = line.substring(0, line.indexOf(':', line.indexOf(':') + 1));
				else
					filePath = line.substring(0, line.indexOf(':'));
				if(!filePath.equals("")){
					if(File.separator.equals("\\")){
						String[] splitLine = line.split(":");
						lineN = Integer.parseInt(splitLine[2]);
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
			errors.add(new Error(JavaSyntaxParser.convertToProjectPath(filePath), lineN, error));
		
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
			TextComp errorComp = new TextComp(errorSet.getFileName() + " : " + errorSet.line, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
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
			TextComp fixedComp = new TextComp("fixed", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
			fixedComp.setBounds(maxW, 1, 30, 23);
			fixedComp.setArc(5, 5);
			fixedComp.setFont(PX12);
			fixedComp.setRunnable(()->{
				fixedComp.setClickable(false);
				errorComp.setColors(TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2);
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
		Editor editorX = Screen.getScreen().loadFile(new File(errorSet.filePath));
		if(editorX != null)
			editorX.grabFocus();
		String fullLog = "File Path : " + errorSet.filePath + "\n";
		fullLog += "At Line   : " + errorSet.line + "\n\n";
		fullLog += errorSet.log;
		errorArea.setText(fullLog);
		try{
			//Highlighting
			String text = errorArea.getText();
			Highlighter h = errorArea.getHighlighter();
			Color color = isDarkMode() ? omega.utils.UIManager.TOOLMENU_COLOR2_SHADE : ErrorHighlighter.color;
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
		if(fileComps.isEmpty()){
			removeThem();
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(back1);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(TOOLMENU_COLOR2);
			g.setFont(PX18);
			g.drawString(headComp.getText(), getWidth()/2 - g.getFontMetrics().stringWidth(headComp.getText())/2,
			getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		}
		else{
			addThem();
			super.paint(graphics);
			splitPane.setDividerLocation(maxW + 65);
		}
	}
	public void removeThem(){
		remove(headComp);
		remove(splitPane);
	}
	public void addThem(){
		add(headComp, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
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


/*
 * View Markdown 's using commonmark-java
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.ui.panel;
import org.commonmark.node.Node;

import org.commonmark.parser.Parser;

import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.text.html.HTMLEditorKit;

import java.util.Scanner;

import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class MarkdownViewPanel extends JPanel{
	public JEditorPane textArea;

	public File file;

	public static String GITHUB_MARKDOWN_CSS;
	static{
		String text = "";
		try{
			Scanner reader = new Scanner(MarkdownViewPanel.class.getResourceAsStream("/github-markdown-" + (isDarkMode() ? "dark" : "light") + ".css"));
			while(reader.hasNextLine())
				text += reader.nextLine() + "\n";
			reader.close();
			GITHUB_MARKDOWN_CSS = "<style>\n" + text + "</style>";
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public MarkdownViewPanel(File file){
		super(new BorderLayout());
		this.file = file;
		setBackground(c2);
		init();
	}

	public void init(){
		try{
			String text = "";
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine())
				text += reader.nextLine() + "\n";
			reader.close();

			Parser parser = Parser.builder().build();
			Node document = parser.parse(text);
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			
			String parsedText = renderer.render(document);
			parsedText = GITHUB_MARKDOWN_CSS + "\n" + parsedText;
			
			textArea = new JEditorPane(){
				@Override
				public void paint(Graphics graphics){
					Graphics2D g = (Graphics2D)graphics;
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					super.paint(g);
				}
			};
			textArea.setBackground(c2);
			textArea.setEditorKit(new HTMLEditorKit());
			textArea.setEditable(false);
			textArea.setText(parsedText);
			
			JScrollPane scrollPane = null;
			add(scrollPane = new JScrollPane(textArea), BorderLayout.CENTER);

			scrollPane.getVerticalScrollBar().setValue(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

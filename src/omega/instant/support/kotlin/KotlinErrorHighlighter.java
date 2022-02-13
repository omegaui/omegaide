/*
 * Highlights Errors
 * Copyright (C) 2022 Omega UI

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
package omega.instant.support.kotlin;
import omega.io.IconManager;

import omega.ui.component.Editor;

import omega.instant.support.java.framework.CodeFramework;

import omega.instant.support.java.parser.JavaSyntaxParserGutterIconInfo;

import omega.instant.support.AbstractErrorHighlighter;
import omega.instant.support.Highlight;

import omega.Screen;

import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

import javax.swing.ImageIcon;

import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.DefaultHighlighter;

import java.io.File;

import java.awt.Color;
import java.awt.Image;

import java.util.LinkedList;
import java.util.StringTokenizer;
public class KotlinErrorHighlighter implements AbstractErrorHighlighter {

	private LinkedList<Highlight> highlights;
	private LinkedList<JavaSyntaxParserGutterIconInfo> gutterIconInfos;

	public KotlinErrorHighlighter() {
		highlights = new LinkedList<>();
		gutterIconInfos = new LinkedList<>();
	}

	/*
	testDir/src/kode/Main.kt:5:17: error: unresolved reference: getTitle
	println(screen.getTitle())
	^
	testDir/src/kode/Screen.kt:9:20: error: import must be placed on a single line
	import javax.swing.
	^
	 */
	@Override
	public void loadErrors(String errorLog) {
		removeAllHighlights();
		StringTokenizer tokenizer = new StringTokenizer(errorLog, "\n");
		boolean canRecord = false;
		String path = "";
		String code = "";
		String message = "";
		int line = 0;
		try {
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if(!canRecord && CodeFramework.count(token, ':') >= 4 && !token.startsWith(" ")){
					int index;
					path = token.substring(0, index = token.indexOf(':')).trim();
					path = Screen.getProjectFile().getArgumentManager().compileDir + File.separator + path;
					line = Integer.parseInt(token.substring(index + 1, index = token.indexOf(':', index + 1)).trim());
					index = token.indexOf(':', index + 1);
					message = token.substring(index + 1).trim();
					canRecord = true;
				}
				else if(canRecord){
					code = token.trim();

					if(!path.contains(File.separator)){
						path = Screen.getProjectFile().getArgumentManager().compileDir + File.separator + path;
					}

					File file = new File(path);
					if(file.exists()){
						Editor e = Screen.getProjectFile().getScreen().loadFile(file);
						try{
							ImageIcon icon = new ImageIcon(IconManager.fluenterrorImage);
							int size = e.getGraphics().getFontMetrics().getHeight();
							icon = new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
							gutterIconInfos.add(new JavaSyntaxParserGutterIconInfo(e.getAttachment().getGutter().addLineTrackingIcon(line - 1, icon, message), e));
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
						Highlighter h = e.getHighlighter();
						SquiggleUnderlineHighlightPainter painter = new SquiggleUnderlineHighlightPainter(Color.RED);
						String text = e.getText();
						int index = 0;
						int times = 0;
						for(int i = 0; i < text.length(); i++) {
							if(text.charAt(i) == '\n' && times < line-1) {
								index = i;
								times++;
							}
						}
						int start = text.indexOf(code, line == 1 ? 0 : index+1);
						int end = start + code.length();
						h.addHighlight(start, end, painter);
						highlights.add(new Highlight(e, painter, start, end, false));
					}
					canRecord = false;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		int count = 0;
		for(Highlight h : highlights){
			count = 0;
			for(Highlight hx : highlights){
				if(h.editor.equals(hx.editor))
					count++;
			}
			h.editor.javaErrorPanel.setDiagnosticData(count, 0);
		}
	}

	@Override
	public void removeAllHighlights() {
		highlights.forEach(h->{
			h.editor.javaErrorPanel.setDiagnosticData(0, 0);
			h.remove();
		});
		highlights.clear();
		gutterIconInfos.forEach(info->info.editor.getAttachment().getGutter().removeTrackingIcon(info.gutterIconInfo));
		gutterIconInfos.clear();
	}

	public void remove(Editor e) {
		highlights.forEach(h->{
			if(h.editor == e) h.remove();
		});
	}

	public void remove(Editor e, int caretPosition) {
		highlights.forEach(h->{
			if(h.editor == e && caretPosition >= h.start && caretPosition <= h.end) h.remove();
		});
	}
}


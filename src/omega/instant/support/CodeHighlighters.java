/**
 * CodeHighlighters
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

package omega.instant.support;
import java.awt.Color;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;

import omega.instant.support.java.highlighter.BasicCodeHighlighter;

import java.util.LinkedList;
public final class CodeHighlighters {
	public static LinkedList<AbstractCodeHighlighter> codeHighlighters = new LinkedList<>();
	static{
		add(new BasicCodeHighlighter());
	}

	public synchronized static void add(AbstractCodeHighlighter abstractCodeHighlighter){
		if(!codeHighlighters.contains(abstractCodeHighlighter))
			codeHighlighters.add(abstractCodeHighlighter);
	}

	public synchronized static void remove(AbstractCodeHighlighter abstractCodeHighlighter){
		if(codeHighlighters.contains(abstractCodeHighlighter))
			codeHighlighters.remove(abstractCodeHighlighter);
	}

	public synchronized static boolean canComputeForeground(RSyntaxTextArea textArea, Token token){
		for(AbstractCodeHighlighter highlighter : codeHighlighters){
			if(highlighter.canComputeForeground(textArea, token))
				return true;
		}
		return false;
	}

	public synchronized static Color computeForegroundColor(RSyntaxTextArea textArea, Token token){
		for(AbstractCodeHighlighter highlighter : codeHighlighters){
			if(highlighter.canComputeForeground(textArea, token))
				return highlighter.computeForegroundColor(textArea, token);
		}
		return null;
	}

	public synchronized static boolean canComputeBackground(RSyntaxTextArea textArea, Token token){
		for(AbstractCodeHighlighter highlighter : codeHighlighters){
			if(highlighter.canComputeBackground(textArea, token))
				return true;
		}
		return false;
	}

	public synchronized static Color computeBackgroundColor(RSyntaxTextArea textArea, Token token){
		for(AbstractCodeHighlighter highlighter : codeHighlighters){
			if(highlighter.canComputeBackground(textArea, token))
				return highlighter.computeBackgroundColor(textArea, token);
		}
		return null;
	}
}

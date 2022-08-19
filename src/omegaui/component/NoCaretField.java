/*
 * A TextField with no caret navigation
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

package omegaui.component;
import java.io.InputStream;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

import java.util.LinkedList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import static java.awt.event.KeyEvent.*;
public class NoCaretField extends JComponent implements KeyListener, FocusListener{
	public Color color1;
	public Color color2;
	public Color color3;

	private String text;
	private String lastText;
	
	private LinkedList<String> lines;
	
	private final int LINE_GAP = 2;
	private final int BORDER_GAP = 5;
	
	private String message = "Start Typing";
	
	private char[] ignorableCharacters;
	
	private Runnable action;
	
	private volatile boolean ctrl;
	private volatile boolean u;
	private volatile boolean c;
	private volatile boolean v;
	
	private volatile boolean editable = true;

	public NoCaretField(String text, Color c1, Color c2, Color c3){
		this.text = text;
		this.lines = new LinkedList<>();
		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				grabFocus();
			}
		});
		setColors(c1, c2, c3);
	}
	
	public NoCaretField(String text, String message, Color c1, Color c2, Color c3){
		this(text, c1, c2, c3);
		this.message = message == null ? "" : message;
	}
	
	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(color2);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(color3);
		g.setFont(getFont());
		int x = getWidth()/2 - g.getFontMetrics().stringWidth(text)/2;
		int y = getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1;
		if(x > BORDER_GAP){
			g.drawString(text, x, y);
			lastText = text;
		}
		else {
			if(lastText == null || lastText.equals("")){
				lastText = "";
				int i = 0;
				x = getWidth()/2 - g.getFontMetrics().stringWidth(lastText)/2 - 10;
				while(x > BORDER_GAP && i < text.length()){
					x = getWidth()/2 - g.getFontMetrics().stringWidth(lastText)/2 - 10;
					lastText += text.charAt(i++);
				}
			}
			String tempText = ".." + text.substring(text.length() - lastText.length() + 5);
			x = getWidth()/2 - g.getFontMetrics().stringWidth(tempText)/2;
			g.drawString(tempText, x, y);
		}
		g.setColor(color1);
		if(text == null || text.equals("")){
			g.drawString(message, getWidth()/2 - g.getFontMetrics().stringWidth(message)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		}
		else {
			if(!isFocusOwner())
				g.setColor(color3);
			g.fillRoundRect(getWidth()/2 + g.getFontMetrics().stringWidth(lastText)/2 - 1, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1 + BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP);
		}
	}
	public void setColors(Color c1, Color c2, Color c3){
		this.color1 = c1;
		this.color2 = c2;
		this.color3 = c3;
		repaint();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		repaint();
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}
	@Override
	public void keyPressed(KeyEvent e) {
		char ch = e.getKeyChar();
		if(ignorableCharacters != null){
			for(char cx : ignorableCharacters){
				if(ch == cx)
					return;
			}
		}
		int code = e.getKeyCode();
		if(code == VK_CONTROL)
			ctrl = true;
		if(code == VK_C)
			c = true;
		if(code == VK_V)
			v = true;
		if(code == VK_U)
			u = true;
		if(ctrl && u){
			setText("");
			u = false;
			return;
		}
		if(ctrl && c){
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
			c = false;
			return;
		}
		if(ctrl && v){
			Transferable obj = (Transferable)Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if(obj != null){
				try{
					if(!(obj.getTransferData(DataFlavor.stringFlavor) instanceof InputStream))
						setText(text + obj.getTransferData(DataFlavor.stringFlavor).toString());
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			v = false;
			return;
		}
		if(editable){
			if(Character.isLetterOrDigit(ch) || isSymbol(ch))
				text += ch;
			else if(code == VK_BACK_SPACE){
				if(text.length() == 1)
					text = "";
				else if(text.length() > 1)
					text = text.substring(0, text.length() - 1);
			}
			else if(code == VK_ENTER){
				if(action != null)
					new Thread(action).start();
			}
		}
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == VK_CONTROL)
			ctrl = false;
		if(code == VK_C)
			c = false;
		if(code == VK_V)
			v = false;
		if(code == VK_U)
			u = false;
	}
	@Override
	public void focusGained(FocusEvent focusEvent) {
		message = "Start Typing";
		repaint();
	}
	@Override
	public void focusLost(FocusEvent focusEvent) {
		message = "Click Me";
		repaint();
	}
	public Runnable getOnAction() {
		return action;
	}
	public void setOnAction(Runnable action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
		repaint();
	}
	public void notify(String text){
		message = text;
		setText("");
	}
	public char[] getIgnorableCharacters() {
		return ignorableCharacters;
	}
	public void setIgnorableCharacters(char... ignorableCharacters) {
		this.ignorableCharacters = ignorableCharacters;
	}
	public void setEditable(boolean value){
		editable = value;
	}
	public boolean isEditable(){
		return editable;
	}
	public boolean isSymbol(char ch){
		return "`~!@#$%^&*()_+-={}|[]\\:\";\'<>?,./]) ".contains(ch + "");
	}
}


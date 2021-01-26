package deassembler;
/*
    The Content Asisst Window.
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;

public class ContentWindow extends JPanel implements KeyListener{
	private final LinkedList<Hint> hints = new LinkedList<>();
	private JScrollPane scrollPane;
	private JPanel panel;
	private int block;
	public volatile int pointer;
	public static volatile int max;
	private int i;
	public ContentWindow() {
		super(new BorderLayout());
          setSize(600, 200);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				hints.get(pointer).inject();
			}
		});
		panel = new JPanel(null);
          panel.setBackground(ide.utils.UIManager.c2);
		add(scrollPane = new JScrollPane(panel), BorderLayout.CENTER);
		ide.utils.UIManager.setData(panel);
		hints.forEach(h->{
			h.setVisible(false);
			panel.remove(h);
		});
		hints.clear();
	}
	
	public void genView(LinkedList<DataMember> dataMembers) {
		if(dataMembers.isEmpty()) {
			setVisible(false);
			return;
		}
		try {
			sort(dataMembers);
			final Editor e = Screen.getScreen().getCurrentEditor();
			hints.forEach(h->{
				h.setVisible(false);
				panel.remove(h);
			});
			hints.clear();
			block = 0;
			pointer = 0;
			final boolean isDarkMode = ide.utils.UIManager.isDarkMode();
			final Font font = new Font(UIManager.fontName, Font.BOLD, Hint.OPTIMAL_FONT_HEIGHT);
			Font xf = Screen.getScreen().getFont();
			Screen.getScreen().getGraphics().setFont(font);
			max = getWidth();
			dataMembers.forEach(d->{
				if(d.getRepresentableValue() != null) {
					int w = Screen.getScreen().getGraphics().getFontMetrics().stringWidth(d.getRepresentableValue());
					if(max < w) {
						max = w + 1;
					}
					Hint hint = new Hint(d, (dx)->{
						String lCode = CodeFramework.getLastCodeIgnoreDot(Screen.getScreen().getCurrentEditor().getText(), Screen.getScreen().getCurrentEditor().getCaretPosition());
						if(lCode == null) {
							e.insert(d.name, e.getCaretPosition());
						}
						else {
							String part = d.name;
							try {
								part = part.substring(lCode.length());
								e.insert(part, e.getCaretPosition());
								if(d.parameterCount > 0) e.setCaretPosition(e.getCaretPosition() - 1);
							}catch(Exception es) {}
						}
						
					}, d.getRepresentableValue());
					hint.setBounds(0, block, getWidth(), Hint.OPTIMAL_HEIGHT);
					if(isDarkMode) ide.utils.UIManager.setData(hint);
					hint.setFont(font);
					panel.add(hint);
					hints.add(hint);
					block += Hint.OPTIMAL_HEIGHT;
				}
			});
			i = 0;

			final String LCode = CodeFramework.getLastCodeIgnoreDot(Screen.getScreen().getCurrentEditor().getText(), Screen.getScreen().getCurrentEditor().getCaretPosition());
			Screen.getScreen().getGraphics().setFont(xf);
			hints.get(0).focussed(true);
			panel.setPreferredSize(new Dimension(max, block));
			setVisible(true);
			scrollPane.getVerticalScrollBar().setVisible(true);
			repaint();
			scrollPane.getVerticalScrollBar().repaint();
			scrollPane.getHorizontalScrollBar().repaint();
			doLayout();
		}catch(Exception ex) {System.out.println(ex.getMessage()); setVisible(false);}
	}
	
	public static void sort(LinkedList<DataMember> dataMembers) {
		Object[] members = dataMembers.toArray();
		LinkedList<DataMember> vars = new LinkedList<>();
		LinkedList<DataMember> meths = new LinkedList<>();
		for(Object obj : members) {
			DataMember m = (DataMember)obj;
			if(m.parameters == null) vars.add(m);
			else meths.add(m);
		}
		dataMembers.clear();
		Object[] var_ =vars.toArray();
		Object[] meths_ = meths.toArray(); 
		for(int i = 0; i < var_.length; i++) {
			for(int j = 0; j < var_.length - 1 - i; i++) {
				DataMember m = (DataMember)var_[j];
				DataMember n = (DataMember)var_[j + 1];
				if(m.name.compareTo(n.name) > 0) {
					Object o = var_[j];
					var_[j] = var_[j + 1];
					var_[j + 1] = o;
				}
			}
		} 
		for(int i = 0; i < meths_.length; i++) {
			for(int j = 0; j < meths_.length - 1 - i; i++) {
				DataMember m = (DataMember)meths_[j];
				DataMember n = (DataMember)meths_[j + 1];
				if(m.name.compareTo(n.name) > 0) {
					Object o = meths_[j];
					meths_[j] = meths_[j + 1];
					meths_[j + 1] = o;
				}
			}
		}
		for(Object v : var_) {
			dataMembers.add((DataMember)v);
		}
		for(Object v : meths_) {
			dataMembers.add((DataMember)v);
		}
		var_ = null;
		meths_ = null;
		members = null;
	}

	@Override
	public void setVisible(boolean value) {
		if(value) {
               final Editor e = Screen.getScreen().getCurrentEditor();
			Rectangle vRect = e.getAttachment().getVisibleRect();
               int x = e.getCaret().getMagicCaretPosition().x;
               int y = e.getCaret().getMagicCaretPosition().y + e.getFont().getSize();
               int xSep = (x + getWidth()) - (int)(vRect.x + vRect.getWidth());
               int ySep = (y + getHeight()) - (int)(vRect.y + vRect.getHeight());
               if(xSep > 0){
                    x -= xSep;
                    if(x < vRect.x)
                         x = vRect.x;
               }
               if(ySep > 0){
                    y = e.getCaret().getMagicCaretPosition().y - getHeight();
               }
               setLocation(x, y);
               repaint();
		}
		super.setVisible(value);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(!isVisible()) return;
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(pointer + 1 < hints.size()) {
				hints.get(pointer).focussed(false);
				hints.get(++pointer).focussed(true);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + Hint.OPTIMAL_HEIGHT);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(pointer - 1 >= 0) {
				hints.get(pointer).focussed(false);
				hints.get(--pointer).focussed(true);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - Hint.OPTIMAL_HEIGHT);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			setVisible(false);
			hints.get(pointer).inject();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}

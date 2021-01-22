package ui;
/*
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Box extends JComponent{
	private String text;
	private volatile boolean enter = false;
	public volatile boolean disabled = false;
	public volatile boolean rightClick = false;

	public Box(String text, Runnable r){
		this.text = text;
		setFont(settings.Screen.PX14);
		ide.utils.UIManager.setData(this);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(!disabled){
					rightClick = e.getButton() == 3;
					repaint();
					r.run();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e){
				if(!disabled){
					Color c = getBackground();
					setBackground(getForeground());
					setForeground(c);
					enter = true;
					repaint();
				}
			}
			@Override
			public void mouseExited(MouseEvent e){
				if(!disabled){
					Color c = getBackground();
					setBackground(getForeground());
					setForeground(c);
					enter = false;
					repaint();
				}
			}
		});
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		repaint();
	}

	@Override
	public void paint(Graphics graphics){
		super.paint(graphics);
		Graphics2D g = (Graphics2D)graphics;
		g.setFont(getFont());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
		int x = g.getFontMetrics().stringWidth(text);
		int cx = x;
		x = getWidth()/2 - x/2;
		g.setColor(getForeground());
		g.drawString(text, x + 3, getHeight() / 2);
	}

}
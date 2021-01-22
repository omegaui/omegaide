package settings.comp;
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
public class TextComp extends JComponent{
	private volatile boolean enter;
	private volatile boolean press;
	private volatile boolean clickable = true;
	
	public int arcX = 20;
	public int arcY = 20;
	private String dir;
	public Color color1;
	public Color color2;
	public Color color3;
     public Runnable runnable;
	public TextComp(String text, Color color1, Color color2, Color color3, Runnable runnable){
		this.dir = text;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
          this.runnable = runnable;
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				if(!clickable) return;
				enter = true;
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				enter = false;
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				if(!clickable) return;
				press = true;
				repaint();
                    enter = false;
                    press = false;
                    repaint();
                    if(TextComp.this.runnable != null)
                         TextComp.this.runnable.run();
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				press = false;
				repaint();
			}
		});
	}
	
	public TextComp(String text, String toolTip, Color color1, Color color2, Color color3, Runnable runnable) {
		this(text, color1, color2, color3, runnable);
		setToolTipText(toolTip);
	}

     public void setColors(Color c1, Color c2, Color c3){
          color1 = c1;
          color2 = c2;
          color3 = c3;
          repaint();
     }
	
	public void draw(Graphics2D g) {
		
	}

     public void setArc(int x, int y){
          this.arcX = x;
          this.arcY = y;
          repaint();
     }

	public void setText(String text){
		this.dir = text;
		repaint();
	}

	public String getText(){
		return dir;
	}

     public void setRunnable(Runnable runnable){
     	this.runnable = runnable;
     }

	public void setEnter(boolean enter) {
		this.enter = enter;
		repaint();
	}
	
	public boolean isMouseEntered() {
		return enter;
	}
	
	public void setClickable(boolean clickable){
		this.clickable = clickable;
		repaint();
	}
	
	public boolean isClickable() {
		return clickable;
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(getFont());
		int x = getWidth()/2 - g.getFontMetrics().stringWidth(dir)/2;
		int y = getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1;
		g.setColor(color2);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		if(enter || !clickable) paintEnter(g);
		if(press) paintPress(g);
		draw(g, x, y);
		draw(g);
	}

	public void draw(Graphics2D g, int x, int y){
		g.setColor(color3);
		g.drawString(dir, x, y);
	}

	public void paintEnter(Graphics2D g){
		g.setColor(color1);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
	}
	
	public void paintPress(Graphics2D g){
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		g.setColor(color2);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
	}
}

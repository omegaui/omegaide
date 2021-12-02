/**
* The Switch Component
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
package omega.comp;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;

import javax.swing.JComponent;

import static omega.utils.UIManager.*;
public class SwitchComp extends JComponent {
	private volatile boolean enter;
	private volatile boolean on;
	public Color color1;
	public Color color2;
	public Color color3;
	public Color inBallColor;
	public int offset = 2;
	public int arcX = 10;
	public int arcY = 10;
	public ToggleListener toggleListener;
	public SwitchComp(Color c1, Color c2, Color c3, ToggleListener listener){
		setToggleListener(listener);
		setColors(c1, c2, c3);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				on = !on;
				toggleListener.toggle(on);
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent e){
				enter = true;
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				enter = false;
				repaint();
			}
		});
	}
	
	public SwitchComp(boolean on, Color c1, Color c2, Color c3, ToggleListener toggleListener){
		this(c1, c2, c3, toggleListener);
		setOn(on);
	}
	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setPaint(isEnabled() ? new GradientPaint(0, 0, color3, getWidth(), getHeight(), color2) : new GradientPaint(0, 0, color2, getWidth(), getHeight(), color3));
		int part = (getHeight() * 40) / 100;
		g.fillRoundRect(offset, getHeight()/2 - part/2, getWidth() - (2 * offset), part, arcX, arcY);
		part = (getHeight() * 60) / 100;
		if(on){
			if(enter){
				g.setColor(color1);
				paintBall(g, getWidth() - offset + 2 - part - 4, getHeight()/2 - (part + 4)/2, part + 4, part + 4);
			}
			g.setColor(color2);
			paintBall(g, getWidth() - offset - part, getHeight()/2 - part/2, part, part);
		}
		else{
			if(enter){
				g.setColor(color2);
				paintBall(g, offset - 2, getHeight()/2 - (part + 4)/2, part + 4, part + 4);
			}
			g.setColor(color1);
			paintBall(g, offset, getHeight()/2 - part/2, part, part);
		}
	}
	public void paintBall(Graphics2D g, int x, int y, int w, int h){
		g.fillOval(x, y, w, h);
		if(inBallColor != null){
			g.setColor(inBallColor);
			g.fillOval(x + 3, y + 3, w - 6, h - 6);
		}
	}
	
	public boolean isOn() {
		return on;
	}
	
	public void setOn(boolean on) {
		this.on = on;
		repaint();
	}
	
	public void setColors(Color c1, Color c2, Color c3){
		color1 = c1;
		color2 = c2;
		color3 = c3;
		repaint();
	}
	
	public java.awt.Color getInBallColor() {
		return inBallColor;
	}
	public void setInBallColor(java.awt.Color inBallColor) {
		this.inBallColor = inBallColor;
		repaint();
	}
	
	public int getBallOffset() {
		return offset;
	}
	
	public void setBallOffset(int offset) {
		this.offset = offset;
	}
	
	public omega.comp.ToggleListener getToggleListener() {
		return toggleListener;
	}
	
	public void setToggleListener(omega.comp.ToggleListener toggleListener) {
		this.toggleListener = toggleListener;
	}
	
	public void setArc(int x, int y){
		arcX = x;
		arcY = y;
		repaint();
	}
}

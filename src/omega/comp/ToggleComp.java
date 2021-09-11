/**
  * ToggleComp
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

import java.awt.image.BufferedImage;
public class ToggleComp extends TextComp {
	
	private volatile boolean state = false;
	private ToggleListener toggleListener = (value)->{};
	public boolean toggleEnabled = true;
	private BufferedImage image;
	private int w;
	private int h;
	
	public ToggleComp(String text, Color c1, Color c2, Color c3, boolean state){
		super(text, c1, c2, c3, null);
		setArc(6, 6);
		this.state = state;
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(toggleEnabled)
					ToggleComp.this.state = !ToggleComp.this.state;
				repaint();
				if(ToggleComp.this.toggleListener != null && e.getButton() == 1)
					ToggleComp.this.toggleListener.toggle(ToggleComp.this.state);
			}
		});
	}
	public ToggleComp(String text, Color c1, Color c2, Color c3, boolean state, ToggleListener toggleListener){
		this(text, c1, c2, c3, state);
		setOnToggle(toggleListener);
	}
	public ToggleComp(BufferedImage image, int w, int h, String text, Color c1, Color c2, Color c3, boolean state){
		this(text, c1, c2, c3, state);
		setImage(image);
          setImageWidth(w);
          setImageHeight(h);
	}
	public java.awt.image.BufferedImage getImage() {
		return image;
	}
	
	public void setImage(java.awt.image.BufferedImage image) {
		this.image = image;
		repaint();
	}
          
	public void setImageWidth(int w){
		this.w = w;
	}
	
	public void setImageHeight(int h){
		this.h = h;
	}
	public int getImageWidth() {
		return w;
	}
	
	public int getImageHeight() {
		return h;
	}
	
	public void setOnToggle(ToggleListener toggleListener){
		this.toggleListener = toggleListener;
	}
	public boolean isOn(){
		return state == true;
	}
	public boolean isOff(){
		return state == false;
	}
	@Override
	public void paint(Graphics graphics){
		alignX = getHeight() + 4;
		super.paint(graphics);
	}
	@Override
	public void draw(Graphics2D g){
		super.draw(g);
		if(image == null){
			g.setColor(color3);
			g.fillRoundRect(2, 2, getHeight() - 4, getHeight() - 4, arcX, arcY);
		}
		if(image != null){
			g.drawImage(image, getHeight()/2 - w/2, getHeight()/2 - h/2, w, h, this);
		}
		if(state){
			g.fillRect(alignX, getHeight() - 2, g.getFontMetrics().stringWidth(getText()), 2);
			if(image == null){
				g.setColor(color2);
				g.fillRoundRect(8, 8, getHeight() - 16, getHeight() - 16, arcX, arcY);
			}
		}
	}
}


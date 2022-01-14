/**
* SliderComp
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

package omegaui.component;
import omegaui.component.listener.SlideListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
public class SliderComp extends JComponent{

	private int minValue = 0;
	private int maxValue = 100;
	private int value = minValue;
	private int boneHeight = 6;
	private int pointerWidth = 4;
	private int pointerArc = 4;

	private int pointerX;
	private int boneY;

	public Color color1;
	public Color color2;
	public Color color3;
	public Color color4;

	public SlideListener slideListener;

	private Font minMaxValueTextFont;
	private Font valueTextFont;

	private Color minMaxValueTextColor;
	private Color valueTextColor;

	private String valueUnit;

	private volatile boolean enter = false;
	private volatile boolean press = false;
	private volatile boolean paintValuesEnabled = false;
	
	public SliderComp(Color c1, Color c2, Color c3, Color c4){
		this.color1 = c1;
		this.color2 = c2;
		this.color3 = c3;
		this.color4 = c4;

		addMouseListener(new MouseAdapter(){
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
			
			@Override
			public void mousePressed(MouseEvent e){
				press = true;
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				press = false;
				repaint();
				triggerOnSlidingFinishedEvent(e);
			}

			@Override
			public void mouseClicked(MouseEvent e){
				setValue(calculatePrefValue(e.getX()));
				repaint();
			}
		});

		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(press){
					int difference = pointerX - e.getX();
					setValue(calculatePrefValue(pointerX - difference));
					triggerOnSlidingEvent(e);
				}
			}
		});
	}

	private int calculatePrefValue(int pointerX){
		int pointerProgress = (pointerX * 100) / getWidth();
		int value = (pointerProgress * maxValue) / 100;
		if(value >= maxValue)
			return maxValue;
		else if(value <= minValue)
			return minValue;
		return value;
	}

	private int calculatePointerX(){
		int valueProgress = (value * 100) / maxValue;
		int pointerX = (valueProgress * getWidth()) / 100;
		if(pointerX >= getWidth())
			return pointerX - pointerWidth/2;
		else if(pointerX <= 0)
			return pointerWidth/2;
		return pointerX;
	}

	private int calculateBoneY(){
		return getHeight()/2 - boneHeight/2;
	}

	private boolean isXOverPointer(int x){
		return x >= pointerX - pointerWidth/2 && x <= pointerX + pointerWidth/2;
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		pointerX = calculatePointerX();
		boneY = calculateBoneY();
		//Painting Covered Distance
		paintCoveredDistance(g);
		//Painting Uncovered Distance
		paintUncoveredDistance(g);
		//Painting Slider 's Pointer
		paintSliderPointer(g);
		//Painting Text Values
		paintTextValues(g);
	}

	public void paintCoveredDistance(Graphics2D g){
		g.setColor(color3);
		g.fillRect(0, boneY, pointerX, boneHeight);
	}

	public void paintUncoveredDistance(Graphics2D g){
		g.setColor(enter ? color1 : color2);
		g.fillRect(pointerX, boneY, getWidth() - pointerX, boneHeight);
	}

	public void paintSliderPointer(Graphics2D g){
		g.setColor(color4);
		if(press)
			g.fillRoundRect(pointerX - pointerWidth/2, boneY, pointerWidth, boneHeight, pointerArc, pointerArc);
		else
			g.fillRoundRect(pointerX - pointerWidth/2, boneY - boneHeight, pointerWidth, boneHeight * 3, pointerArc, pointerArc);
	}

	public void paintTextValues(Graphics2D g){
		if(!isPaintValuesEnabled())
			return;
		g.setFont(minMaxValueTextFont);
		g.setColor(minMaxValueTextColor);
		String minText = minValue + getValueUnit();
		String maxText = maxValue + getValueUnit();
		int ascentDescent = boneY + (boneHeight * 3) + boneHeight/2;
		g.drawString(minText, 2, ascentDescent);
		g.drawString(maxText, getWidth() - g.getFontMetrics().stringWidth(maxText), ascentDescent);

		g.setFont(valueTextFont);
		g.setColor(value == maxValue ? minMaxValueTextColor : valueTextColor);
		String text = value + getValueUnit();
		ascentDescent = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + boneHeight/2;
		g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2, ascentDescent);
	}

	private void triggerOnSlidingEvent(MouseEvent e){
		if(slideListener == null)
			return;
		slideListener.onSliding(e, value);
	}

	private void triggerOnSlidingFinishedEvent(MouseEvent e){
		if(slideListener == null)
			return;
		slideListener.onSlidingFinished(e, value);
	}

	private void triggerOnValueChangedEvent(){
		if(slideListener == null)
			return;
		slideListener.onValueChanged(value);
	}

	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
		repaint();
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		repaint();
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
		triggerOnValueChangedEvent();
		repaint();
	}

	public int getBoneHeight() {
		return boneHeight;
	}
	
	public void setBoneHeight(int boneHeight) {
		this.boneHeight = boneHeight;
		repaint();
	}

	public int getPointerWidth() {
		return pointerWidth;
	}
	
	public void setPointerWidth(int pointerWidth) {
		this.pointerWidth = pointerWidth;
		repaint();
	}
	
	public int getPointerArc() {
		return pointerArc;
	}
	
	public void setPointerArc(int pointerArc) {
		this.pointerArc = pointerArc;
		repaint();
	}
	
	public java.awt.Font getMinMaxValueTextFont() {
		return minMaxValueTextFont;
	}
	
	public void setMinMaxValueTextFont(java.awt.Font minMaxValueTextFont) {
		this.minMaxValueTextFont = minMaxValueTextFont;
		repaint();
	}
	
	public java.awt.Font getValueTextFont() {
		return valueTextFont;
	}
	
	public void setValueTextFont(java.awt.Font valueTextFont) {
		this.valueTextFont = valueTextFont;
		repaint();
	}
	
	public java.awt.Color getMinMaxValueTextColor() {
		return minMaxValueTextColor;
	}
	
	public void setMinMaxValueTextColor(java.awt.Color minMaxValueTextColor) {
		this.minMaxValueTextColor = minMaxValueTextColor;
		repaint();
	}
	
	public java.awt.Color getValueTextColor() {
		return valueTextColor;
	}
	
	public void setValueTextColor(java.awt.Color valueTextColor) {
		this.valueTextColor = valueTextColor;
		repaint();
	}
	
	public java.lang.String getValueUnit() {
		return valueUnit == null ? "" : valueUnit;
	}
	
	public void setValueUnit(java.lang.String valueUnit) {
		this.valueUnit = valueUnit;
		repaint();
	}
	
	public boolean isPaintValuesEnabled() {
		return paintValuesEnabled && minMaxValueTextFont != null && valueTextFont != null && minMaxValueTextColor != null && valueTextColor != null;
	}
	
	public void setPaintValuesEnabled(boolean paintValuesEnabled) {
		this.paintValuesEnabled = paintValuesEnabled;
		repaint();
	}

	public omegaui.component.listener.SlideListener getSlideListener() {
		return slideListener;
	}
	
	public void setSlideListener(omegaui.component.listener.SlideListener slideListener) {
		this.slideListener = slideListener;
	}
	
}

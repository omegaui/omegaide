/*
 * The Base Component for Rendering Text and Images as a button
 * Copyright (C) 2021 - 22 Omega UI

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
import omegaui.component.animation.AnimationLayer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;
import java.util.HashMap;

import java.awt.image.BufferedImage;

import java.awt.Color;
import java.awt.Window;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.Image;
import java.awt.Cursor;

import javax.swing.JComponent;
public class TextComp extends JComponent{

	public volatile boolean enter;
	public volatile boolean press;
	public volatile boolean topLeftArcVisible = true;
	public volatile boolean bottomLeftArcVisible = true;
	public volatile boolean topRightArcVisible = true;
	public volatile boolean bottomRightArcVisible = true;

	private volatile boolean clickable = true;
	private volatile boolean paintGradientEnabled = false;
	private volatile boolean paintTextGradientEnabled = false;
	private volatile boolean useSpeedMode = false;
	private volatile boolean paintEnterFirst = true;
	private volatile boolean shouldRepaintForegroundOnHighlights = true;
	
	private volatile boolean dragStarted = false;
	private volatile boolean mousePressed = false;

	public static final int GRADIENT_MODE_DEFAULT = 0;
	public static final int GRADIENT_MODE_LINEAR = 1;

	private int gradientMode = GRADIENT_MODE_DEFAULT;

	public int arcX = 10;
	public int arcY = 10;
	public int pressX;
	public int pressY;
	public int imageX = -1;
	public int imageY = -1;
	public int alignX = -1;
	public int w;
	public int h;
	public int textX;
	public int textY;
	public int textWidth;
	public int textHeight;

	private String dir;
	
	public static final int TEXT_ALIGNMENT_CENTER = 0;
	public static final int TEXT_ALIGNMENT_RIGHT = 1;
	public static final int TEXT_ALIGNMENT_LEFT = 2;

	public int textAlignment = TEXT_ALIGNMENT_CENTER;
	
	public int textLeftAlignmentMargin = 5;
	public int textRightAlignmentMargin = 5;

	public AnimationLayer animationLayer;

	public Color color1;
	public Color color2;
	public Color color3;
	public Color colorG;
	public Color colorH;

	public Runnable runnable;
	public Runnable onMouseEntered = ()->{};
	public Runnable onMouseExited = ()->{};

	public BufferedImage image;
	public Image gifImage;

	public Window window;

	public LinkedList<String> highlightTexts = new LinkedList<>();

	public LinkedList<Object> extras = new LinkedList<>();
	public HashMap<Object, Object> map = new HashMap<>();

	public Cursor defaultCursor;
	public Cursor handCursor;

	public float[] fractions = {0.0f, 0.5f, 1f};

	public Color[] gradientColors;

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
				if(handCursor != null)
					setCursor(handCursor);
				enter = true;
				onMouseEntered.run();
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e){
				if(!clickable) return;
				if(defaultCursor != null)
					setCursor(defaultCursor);
				enter = false;
				if(window != null){
					if(!mousePressed)
						dragStarted = false;
				}
				onMouseExited.run();
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e){
				if(window != null){
					pressX = e.getX();
					pressY = e.getY();
					dragStarted = true;
					mousePressed = true;
				}
				if(!clickable)
					return;
				press = true;
				repaint();
				enter = false;
				press = false;
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e){
				press = false;
				if(window != null){
					mousePressed = false;
					dragStarted = false;
				}
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e){
				if(TextComp.this.runnable != null && e.getButton() == 1)
					TextComp.this.runnable.run();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(window != null && dragStarted){
					window.setLocation(e.getXOnScreen() - pressX - getX(), e.getYOnScreen() - pressY - getY());
				}
			}
		});
	}

	public TextComp(String text, String toolTip, Color color1, Color color2, Color color3, Runnable runnable) {
		this(text, color1, color2, color3, runnable);
		setToolTipText(toolTip);
	}
	
	public TextComp(BufferedImage image, int width, int height, Color color1, Color color2, Color color3, Runnable runnable){
		this("", color1, color2, color3, runnable);
		this.image = image;
		w = width;
		h = height;
		if(w == 0){
			w = image.getWidth();
			h = image.getHeight();
		}
	}
	
	public TextComp(BufferedImage image, Color color1, Color color2, Color color3, Runnable runnable){
		this("", color1, color2, color3, runnable);
		this.image = image;
		w = image.getWidth();
		h = image.getHeight();
		if(w == 0){
			w = image.getWidth();
			h = image.getHeight();
		}
	}

	public TextComp(BufferedImage image, int width, int height, String toolTip, Color color1, Color color2, Color color3, Runnable runnable) {
		this("", toolTip, color1, color2, color3, runnable);
		this.image = image;
		w = width;
		h = height;
		if(w == 0){
			w = image.getWidth();
			h = image.getHeight();
		}
	}

	public TextComp(BufferedImage image, String toolTip, Color color1, Color color2, Color color3, Runnable runnable) {
		this("", toolTip, color1, color2, color3, runnable);
		this.image = image;
		w = image.getWidth();
		h = image.getHeight();
		if(w == 0){
			w = image.getWidth();
			h = image.getHeight();
		}
	}

	public void setShowHandCursorOnMouseHover(boolean value){
		if(value){
			if(handCursor == null){
				defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				handCursor = new Cursor(Cursor.HAND_CURSOR);
			}
		}
		else {
			handCursor = defaultCursor = null;
		}
	}

	public void setColors(Color c1, Color c2, Color c3){
		color1 = c1;
		color2 = c2;
		color3 = c3;
		repaint();
	}

	public void setOnMouseEntered(Runnable action){
		onMouseEntered = action;
	}

	public void setOnMouseExited(Runnable action){
		onMouseExited = action;
	}

	public java.awt.Image getGifImage() {
		return gifImage;
	}

	public void setGifImage(java.awt.Image gifImage, int width, int height) {
		this.gifImage = gifImage;
		this.w = width;
		this.h = height;
		repaint();
	}

	public void setGifImage(java.awt.Image gifImage) {
		this.gifImage = gifImage;
		repaint();
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image, int width, int height) {
		this.image = image;
		this.w = width;
		this.h = height;
		repaint();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}

	public void attachDragger(Window window){
		this.window = window;
	}

	public void draw(Graphics2D g) {
		if(canDrawImage()){
			if(isUsingCustomImageCoordinates())
				g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), imageX, imageY, w, h, this);
			else
				g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), getWidth()/2 - w/2, getHeight()/2 - h/2, w, h, this);
		}
	}

	public void drawGif(Graphics2D g) {
		if(canDrawGifImage()){
			g.drawImage(gifImage, getWidth()/2 - w/2, getHeight()/2 - h/2, w, h, this);
		}
	}

	public void setImageCoordinates(int x, int y){
		this.imageX = x;
		this.imageY = y;
		repaint();
	}

	public boolean isUsingCustomImageCoordinates(){
		return imageX != -1 && imageY != -1;
	}

	public boolean canDrawImage(){
		return image != null;
	}

	public boolean canDrawGifImage(){
		return gifImage != null;
	}

	public void setArc(int x, int y){
		this.arcX = x;
		this.arcY = y;
		repaint();
	}

	public void setAnimationLayer(AnimationLayer layer){
		this.animationLayer = layer;
	}

	public void triggerAnimation(){
		animationLayer.animate(this);
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

	public void doClick(){
		if(runnable != null)
			runnable.run();
	}

	public void setHighlightColor(Color colorH){
		this.colorH = colorH;
		repaint();
	}

	public boolean isPaintGradientEnabled() {
		if(!paintGradientEnabled)
			return false;
		if(!(gradientMode >= GRADIENT_MODE_DEFAULT && gradientMode <= GRADIENT_MODE_LINEAR))
			return false;
		if(gradientMode == GRADIENT_MODE_DEFAULT)
			return colorG != null;
		if(gradientMode == GRADIENT_MODE_LINEAR)
			return fractions != null && gradientColors != null && fractions.length == gradientColors.length;
		return true;
	}

	public void setPaintGradientEnabled(boolean paintGradientEnabled) {
		this.paintGradientEnabled = paintGradientEnabled;
		repaint();
	}

	public boolean isPaintEnterFirst() {
		return paintEnterFirst;
	}

	public void setPaintEnterFirst(boolean paintEnterFirst) {
		this.paintEnterFirst = paintEnterFirst;
		repaint();
	}

	public boolean isShouldRepaintForegroundOnHighlights() {
		return shouldRepaintForegroundOnHighlights;
	}
	
	public void setShouldRepaintForegroundOnHighlights(boolean shouldRepaintForegroundOnHighlights) {
		this.shouldRepaintForegroundOnHighlights = shouldRepaintForegroundOnHighlights;
		repaint();
	}
	
	public boolean isPaintTextGradientEnabled() {
		if(!paintTextGradientEnabled)
			return false;
		if(!(gradientMode >= GRADIENT_MODE_DEFAULT && gradientMode <= GRADIENT_MODE_LINEAR))
			return false;
		if(gradientMode == GRADIENT_MODE_DEFAULT)
			return colorG != null;
		if(gradientMode == GRADIENT_MODE_LINEAR)
			return fractions != null && gradientColors != null && fractions.length == gradientColors.length;
		return true;
	}

	public void setPaintTextGradientEnabled(boolean paintTextGradientEnabled) {
		this.paintTextGradientEnabled = paintTextGradientEnabled;
		repaint();
	}

	public boolean isUseSpeedMode() {
		return useSpeedMode;
	}

	public void setUseSpeedMode(boolean useSpeedMode) {
		this.useSpeedMode = useSpeedMode;
		repaint();
	}

	public java.awt.Color getGradientColor() {
		return colorG;
	}

	public void setGradientColor(java.awt.Color colorG) {
		this.colorG = colorG;
		repaint();
	}

	public int getGradientMode() {
		return gradientMode;
	}

	public void setGradientMode(int gradientMode) {
		this.gradientMode = gradientMode;
		repaint();
	}

	public void setLinearGradientFractions(float... fractions){
		this.fractions = fractions;
		repaint();
	}

	public float[] getGradientFractions(){
		return fractions;
	}

	public void setLinearGradientColors(Color... colors){
		this.gradientColors = colors;
		repaint();
	}

	public Color[] getLinearGradientColors(){
		return gradientColors;
	}

	public void setArcVisible(boolean arc1, boolean arc2, boolean arc3, boolean arc4){
		topLeftArcVisible = arc1;
		topRightArcVisible = arc2;
		bottomRightArcVisible = arc3;
		bottomLeftArcVisible = arc4;
		repaint();
	}

	public synchronized void addHighlightText(String... texts){
		highlightTexts.clear();
		for(String text : texts)
			highlightTexts.add(text);
		texts = null;
		repaint();
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, useSpeedMode ? RenderingHints.VALUE_RENDER_SPEED : RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(getFont());

		int x = getWidth()/2 - g.getFontMetrics().stringWidth(dir)/2;
		int y = getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1;

		textX = x;
		textY = y;
		textWidth = g.getFontMetrics().stringWidth(getText());
		textHeight = g.getFontMetrics().getHeight();

		if(textAlignment == TEXT_ALIGNMENT_RIGHT)
			textX = getWidth() - textWidth - textRightAlignmentMargin;
		else if(textAlignment == TEXT_ALIGNMENT_LEFT)
			textX = textLeftAlignmentMargin;

		if(isPaintGradientEnabled()){
			if(gradientMode == GRADIENT_MODE_DEFAULT)
				g.setPaint(new GradientPaint(0, 0, color2, getWidth(), getHeight(), colorG));
			else if(gradientMode == GRADIENT_MODE_LINEAR)
				g.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), fractions, gradientColors));
		}
		else
			g.setColor(color2);

		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		paintArc(g);

		if(paintEnterFirst && (enter || !clickable))
			paintEnter(g);

		if(press) 
			paintPress(g);

		draw(g, x, y);
		draw(g);
		drawGif(g);

		if(!paintEnterFirst && (enter || !clickable))
			paintEnter(g);

		super.paint(graphics);

		g.dispose();
		graphics.dispose();
	}

	public void draw(Graphics2D g, int x, int y){
		g.setFont(getFont());

		if(textAlignment == TEXT_ALIGNMENT_CENTER)
			textX = alignX < 0 ? x : alignX;

		if(isPaintTextGradientEnabled()){
			if(gradientMode == GRADIENT_MODE_DEFAULT)
				g.setPaint(new GradientPaint(textX, y, color3, textWidth, textHeight, colorG));
			else if(gradientMode == GRADIENT_MODE_LINEAR)
				g.setPaint(new LinearGradientPaint(textX, y, textWidth, textHeight, fractions, gradientColors));
		}
		else
			g.setColor(color3);

		if(x < alignX){
			String temp = dir.substring(0, dir.length()/2) + "..";
			x = getWidth()/2 - g.getFontMetrics().stringWidth(temp)/2;
			textX = alignX < 0 ? x : alignX;
			textWidth = g.getFontMetrics().stringWidth(temp);
			if(isPaintTextGradientEnabled()){
				if(gradientMode == GRADIENT_MODE_DEFAULT)
					g.setPaint(new GradientPaint(textX, y, color3, textWidth, textHeight, colorG));
				else if(gradientMode == GRADIENT_MODE_LINEAR)
					g.setPaint(new LinearGradientPaint(textX, y, textWidth, textHeight, fractions, gradientColors));
			}
			g.drawString(temp, textX, y);
			setToolTipText(dir);
			highlight(g, temp);
		}
		else {
			g.drawString(dir, textX, y);
			highlight(g, dir);
		}
	}

	public synchronized void highlight(Graphics2D g, String text){
		//Creating a clone to prevent concurrent modification if the list collides while in the paint highlights loop!
		LinkedList<String> highlightTexts = (LinkedList<String>)this.highlightTexts.clone();
		for(String match : highlightTexts){
			if(text.contains(match)){
				int matchLength = match.length();
				String token = "";
				if(matchLength <= text.length()){
					String lpart = text.substring(0, text.indexOf(match));
					int width = g.getFontMetrics().stringWidth(lpart);
					g.setColor(color2);
					g.fillRect(textX + width, 0, g.getFontMetrics().stringWidth(match), getHeight());
					g.setColor(colorH);
					g.drawString(match, textX + width, textY);
					if(shouldRepaintForegroundOnHighlights && enter){
						g.setColor(color1);
						g.fillRect(textX + width, 0, g.getFontMetrics().stringWidth(match), getHeight());
					}
				}
			}
		}
	}

	public int countOccurence(String line, String text){
		int count = 0;
		while(line.contains(text)){
			count++;
			line = line.substring(line.indexOf(text) + text.length());
		}
		return count;
	}

	public void paintEnter(Graphics2D g){
		if(isPaintGradientEnabled() && !clickable)
			return;
		g.setColor(color1);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		g.setColor(Color.WHITE);
		paintArc(g);
		g.setColor(color2);
		paintArc(g);
		g.setColor(color1);
		paintArc(g);
	}

	public void paintPress(Graphics2D g){
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		g.setColor(color2);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
		paintArc(g);
	}

	public void paintArc(Graphics2D g){
		if(!topLeftArcVisible)
			g.fillRect(0, 0, arcX, arcY);
		if(!bottomLeftArcVisible)
			g.fillRect(0, getHeight() - arcY, arcX, arcY);
		if(!topRightArcVisible)
			g.fillRect(getWidth() - arcX, 0, arcX, arcY);
		if(!bottomRightArcVisible)
			g.fillRect(getWidth() - arcX, getHeight() - arcY, arcX, arcY);
	}

	public LinkedList<Object> getExtras(){
		return extras;
	}

	public Object put(Object key, Object value){
		return map.put(key, value);
	}

	public Object getValue(Object key){
		return map.get(key);
	}

	public int getTextAlignment() {
		return textAlignment;
	}
	
	public void setTextAlignment(int textAlignment) {
		this.textAlignment = textAlignment;
		repaint();
	}
	
	public int getTextLeftAlignmentMargin() {
		return textLeftAlignmentMargin;
	}
	
	public void setTextLeftAlignmentMargin(int textLeftAlignmentMargin) {
		this.textLeftAlignmentMargin = textLeftAlignmentMargin;
		repaint();
	}
	
	public int getTextRightAlignmentMargin() {
		return textRightAlignmentMargin;
	}
	
	public void setTextRightAlignmentMargin(int textRightAlignmentMargin) {
		this.textRightAlignmentMargin = textRightAlignmentMargin;
		repaint();
	}
	
}


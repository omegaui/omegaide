/*
 * 09/13/2005
 *
 * SquiggleUnderlineHighlightPainter.java - Highlighter that draws a squiggle
 * underline under "highlighted" text, similar to error markers in Microsoft
 * Visual Studio or Eclipse.
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.fife.ui.rtextarea.ChangeableHighlightPainter;


/**
 * Highlight painter that paints a squiggly underline underneath text, similar
 * to what popular IDE's such as Visual Studio and Eclipse do to indicate
 * errors, warnings, etc.<p>
 *
 * This class must be used as a <code>LayerPainter</code>.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SquiggleUnderlineHighlightPainter
				extends ChangeableHighlightPainter {

	private static final int AMT = 2;
	private boolean useFlatLine = false;

	/**
	 * Constructor.
	 *
	 * @param color The color of the squiggle.  This cannot be
	 *        <code>null</code>.
	 */
	public SquiggleUnderlineHighlightPainter(Color color) {
		super(color);
		setPaint(color);
	}
	
	public SquiggleUnderlineHighlightPainter(Color color, boolean useFlatLine) {
		super(color);
		setPaint(color);
		this.useFlatLine = useFlatLine;
	}

	public boolean isUsingFlatLine(){
		return useFlatLine;
	}

	public void setUseFlatLine(boolean value){
		this.useFlatLine = value;
	}

	/**
	 * Paints a portion of a highlight.
	 *
	 * @param g the graphics context
	 * @param offs0 the starting model offset &gt;= 0
	 * @param offs1 the ending model offset &gt;= offs1
	 * @param bounds the bounding box of the view, which is not
	 *        necessarily the region to paint.
	 * @param c the editor
	 * @param view View painting for
	 * @return region drawing occurred in
	 */
	@Override
	public Shape paintLayer(Graphics g, int offs0, int offs1,
						Shape bounds, JTextComponent c, View view) {

		g.setColor((Color)getPaint());

		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
			// Contained in view, can just use bounds.
			Rectangle alloc;
			if (bounds instanceof Rectangle) {
				alloc = (Rectangle)bounds;
			}
			else {
				alloc = bounds.getBounds();
			}
			paintSquiggle(g, alloc);
			return alloc;
		}

		// Otherwise, should only render part of View.
		try {
			// --- determine locations ---
			Shape shape = view.modelToView(offs0, Position.Bias.Forward,
                                               offs1,Position.Bias.Backward,
                                               bounds);
			Rectangle r = (shape instanceof Rectangle) ?
						(Rectangle)shape : shape.getBounds();
			paintSquiggle(g, r);
			return r;
		} 
		catch (BadLocationException e) {
			//e.printStackTrace();// can't render
		}

		// Only if exception
		return null;

	}

	/**
	 * Paints a squiggle underneath text in the specified rectangle.
	 *
	 * @param g The graphics context with which to paint.
	 * @param r The rectangle containing the text.
	 */
	protected void paintSquiggle(Graphics g, Rectangle r) {
		if(isUsingFlatLine()){
			g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
			return;
		}
		int x = r.x;
		int y = r.y + r.height - AMT;
		int delta = -AMT;
		while (x < r.x+r.width){
			g.drawLine(x,y, x+AMT,y+delta);
			y += delta;
			delta = -delta;
			x += AMT;
		}
	}


}

package omega.highlightUnit;
import java.awt.*;
import javax.swing.text.*;

public class UnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
	public UnderlinePainter(Color color) {
		super(color);
	}

	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
		Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

		if (r == null) 
			return null;

		Color color = getColor();
		g.setColor(color == null ? c.getSelectionColor() : color);
		g.drawRect(r.x + 1, r.y + r.height - 2, r.width - 2, 2);
		return r;
	}

	private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
			Rectangle alloc;

			if (bounds instanceof Rectangle)
				alloc = (Rectangle)bounds;
			else
				alloc = bounds.getBounds();
			
			return alloc;
		}
		else {
			try {
				Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);
				Rectangle r = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
				return r;
			}
			catch (BadLocationException e) {
				
			}
		}
		return null;
	}
}

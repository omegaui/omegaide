package omega.utils;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class BuildPanel extends JPanel{
	private JScrollPane scrollPane;
	private JPanel panel;
	private String hint;
     private int count = 0;
	
	public BuildPanel(String hint){
		super(new BorderLayout());
		this.hint = hint;
		super.add(scrollPane = new JScrollPane(panel = new JPanel(null)));
		panel.setBackground(c2);
		setVisible(false);
	}
	@Override
	public void paint(Graphics graphics){
		if(count == 0){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               g.setFont(PX14);
			g.setColor(c2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(c3);
			g.drawString(hint, getWidth()/2 - g.getFontMetrics().stringWidth(hint)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		}
		else{
			super.paint(graphics);
		}
	}
	@Override
	public Component add(Component c){
		panel.add(c);
          count++;
		return c;
	}
     @Override
     public void remove(Component c){
     	panel.remove(c);
          count--;
     }
}

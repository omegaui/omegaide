package ide;
import java.awt.*;
import javax.swing.*;
public class Loading extends JComponent{
	private String text;
	private int progress = 0;
	public Loading(String text){
		setText(text);
		setFont(new Font("Ubunut Mono", Font.BOLD, 14));
		if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53) {
			setBackground(contentUI.Click.colorY);
			setForeground(contentUI.Click.colorX);
		}
		else {
			setBackground(Color.WHITE);
		}
	}

	public void setText(String text){
		this.text = text;
		repaint();
	}

	public void setProgress(int progress){
		if(progress > 100)
			this.progress = 100;
		else if(progress < 0)
			this.progress = 0;
		else
			this.progress = progress;
		repaint();
	}

	@Override
	public void setFont(Font f){
		super.setFont(f);
		setSize(getWidth(), getFont().getSize()  + 8);
		setPreferredSize(getSize());
	}

	@Override
	public void paint(Graphics gr){
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		int x = g.getFontMetrics().stringWidth(text);
		x = getWidth()/2 - x/2;
		int load = (progress * getWidth()) / 100;
		g.fillRoundRect(0, getHeight() - 3, load, 2, 5, 5);
		g.drawString(text, x, getHeight()/2 + 2);
		String pro = progress+"%";
		int cx = g.getFontMetrics().stringWidth(pro);
		if(load + cx < x) {
			g.drawString(pro, load, getHeight()/2 + 2);
		}
		else if(load + cx > x + g.getFontMetrics().stringWidth(text)) {
			if(load - cx - 1> x + g.getFontMetrics().stringWidth(text))
				g.drawString(pro, load - cx - 1, getHeight()/2 + 2);
			else
				g.drawString("       "+pro, load, getHeight()/2 + 2);
		}
		else {
			g.drawString(pro, x - cx - 1, getHeight()/2 + 2);
		}
	}
}

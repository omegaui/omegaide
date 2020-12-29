package settings.comp;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Comp extends JComponent{
	private volatile boolean enter;
	private volatile boolean press;
	private volatile boolean clickable = true;
	private volatile boolean toggleON;
	private String text;
	private String originalText;
	private String inactiveText;
	private String activeText;
	public Color color1;
	public Color color2;
	public Color color3;
	public Runnable runnable;
	public Runnable runnable_temp;
	public TextComp leftComp;
	public TextComp rightComp;
	public Comp(String text, Color color1, Color color2, Color color3, Runnable runnable){
		this.text = text;
		this.originalText = text;
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
				if(!clickable) return;
				enter = false;
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e){
				if(!clickable) return;
				press = true;
				repaint();
				Comp.this.runnable.run();
			}
			@Override
			public void mouseReleased(MouseEvent e){
				if(!clickable) return;
				press = false;
				repaint();
			}
		});
	}

	public void setText(String text){
		this.text = text;
		repaint();
	}

	public void setAction(Runnable runnable){
		this.runnable = runnable;
	}

	public void setClickable(boolean value){
		this.clickable = value;
	}

	public void setToggle(boolean toggle){
		this.toggleON = toggle;
		setText(this.toggleON ? activeText : inactiveText);
	}

	public void createToggle(boolean toggleON, String activeText, String inactiveText, ToggleListener tL){
		this.toggleON = toggleON;
		this.inactiveText = inactiveText;
		this.activeText = activeText;
		setText(this.toggleON ? activeText : inactiveText);
		runnable_temp = runnable;
		runnable = ()->{
			this.toggleON = !this.toggleON;
			setText(this.toggleON ? activeText : inactiveText);
			tL.toggle(this.toggleON);
		};
	}

	public void removeToggle(){
		runnable = runnable_temp;
	}

	public void createLeftArrow(int x, int y, int w, int h, Runnable runnable){
		leftComp = new TextComp("<", color1, color2, color3, runnable);
		leftComp.setBounds(x, y, w, h);
		leftComp.setFont(getFont());
		add(leftComp);
		repaint();
	}

	public void removeLeftArrow(){
		if(leftComp != null){
			remove(leftComp);
			repaint();
		}
	}

	public void createRightArrow(int x, int y, int w, int h, Runnable runnable){
		rightComp = new TextComp(">", color1, color2, color3, runnable);
		rightComp.setBounds(x, y, w, h);
		rightComp.setFont(getFont());
		add(rightComp);
		repaint();
	}

	public void removeRightArrow(){
		if(rightComp != null){
			remove(rightComp);
			repaint();
		}
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(color1);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
		g.setColor(Color.WHITE);
		g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
		g.setColor(color2);
		g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
		g.setColor(color3);
		g.setFont(getFont());
		int textLength = g.getFontMetrics().stringWidth(text);
		g.drawString(text, getWidth()/2 - textLength/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent());
		if(enter){
			g.setColor(color3);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
			g.setColor(Color.WHITE);
			g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
			g.setColor(color2);
			g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
			g.setColor(color3);
			g.drawString(text, getWidth()/2 - textLength/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent());
		}
		if(press){
			paintPress(g, textLength);
		}
		super.paint(graphics);
	}

	public void paintPress(Graphics2D g, int textLength){
		g.setColor(color1);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
		g.setColor(Color.WHITE);
		g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
		g.setColor(color2);
		g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, 40, 40);
		g.setColor(color3);
		g.drawString(text, getWidth()/2 - textLength/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent());
	}
}

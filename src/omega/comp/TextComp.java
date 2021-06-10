package omega.comp;
import java.awt.image.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class TextComp extends JComponent{
	private volatile boolean enter;
	private volatile boolean press;
	private volatile boolean clickable = true;
	
	public int arcX = 20;
	public int arcY = 20;
	public int pressX;
	public int pressY;
	public int alignX = -1;
	private String dir;
	public Color color1;
	public Color color2;
	public Color color3;
	public Runnable runnable;
	public BufferedImage image;
	public int w;
	public int h;
	public Window window;
	public LinkedList<Object> extras = new LinkedList<>();
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
                    if(window != null){
                         pressX = e.getX();
                         pressY = e.getY();
                    }
				if(!clickable) return;
				press = true;
				repaint();
				enter = false;
				press = false;
				repaint();
				if(TextComp.this.runnable != null && e.getButton() == 1)
					TextComp.this.runnable.run();
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				press = false;
				repaint();
			}
		});
          addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
               	if(window != null){
                         window.setLocation(e.getXOnScreen() - pressX - getX(), e.getYOnScreen() - pressY - getY());
               	}
               }
          });
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
	public TextComp(String text, String toolTip, Color color1, Color color2, Color color3, Runnable runnable) {
		this(text, color1, color2, color3, runnable);
		setToolTipText(toolTip);
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
	public void setColors(Color c1, Color c2, Color c3){
		color1 = c1;
		color2 = c2;
		color3 = c3;
		repaint();
	}

     public void attachDragger(Window window){
     	this.window = window;
     }
     
	public void draw(Graphics2D g) {
		if(image != null){
			g.drawImage(image, getWidth()/2 - w/2, getHeight()/2 - h/2, w, h, null);
		}
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
	public void doClick(){
		if(runnable != null)
			runnable.run();
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
		super.paint(graphics);
	}
	public void draw(Graphics2D g, int x, int y){
		g.setColor(color3);
		if(x < alignX){
			String temp = ".." + dir.substring(dir.length()/2);
			x = getWidth()/2 - g.getFontMetrics().stringWidth(temp)/2;
			g.drawString(temp, alignX < 0 ? x : alignX, y);
			setToolTipText(dir);
		}
		else
			g.drawString(dir, alignX < 0 ? x : alignX, y);
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

     public LinkedList<Object> getExtras(){
     	return extras;
     }
}


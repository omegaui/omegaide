package deassembler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ide.Screen;
import tabPane.IconManager;
public class Hint extends JComponent{
	public interface InjectionListener{
		void inject(DataMember d);
	}
	private DataMember d;
	private volatile boolean enter;
	private volatile boolean focus;
	private String repValue;
	public static final int OPTIMAL_HEIGHT = 20;
	public static final int OPTIMAL_FONT_HEIGHT = 15;
	private InjectionListener i;
	private BufferedImage typeImage;
	private static final Color CYAN = new Color(20, 20, 160);
	public Hint(DataMember d, InjectionListener i, String repValue){
		this.repValue = repValue;
		this.d = d;
		this.i = i;
		typeImage = d.parameters != null ?(BufferedImage)IconManager.methodIcon.getImage() : (BufferedImage)IconManager.variableIcon.getImage();
		setBackground(Color.WHITE);
		setForeground(CYAN);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				enter = true;
				ide.Screen.reverseColors(Hint.this);
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				enter = false;
				ide.Screen.reverseColors(Hint.this);
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e){
				try {Screen.getScreen().getCurrentEditor().contentWindow.setVisible(false);}catch(Exception ex) {}
				inject();
			}
		});
	}
	
	public void inject() {
		i.inject(Hint.this.d);
	}
	
	public void focussed(boolean value) {
		focus = value;
		ide.Screen.reverseColors(Hint.this);
	}
	
	public boolean isFocussed() {
		return focus;
	}

	@Override
	public void paint(Graphics graphics){
		setSize(ContentWindow.max, getHeight());
		super.paint(graphics);
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		if(enter)
			g.fill3DRect(0, 0, getWidth(), getHeight(), enter);
		else
			g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawString(repValue, 24, (getHeight()/2) + 2);
		g.drawImage(typeImage, 2, 0, 20, 20, this);
	}
}

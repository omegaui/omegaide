package omega.utils;
import omega.Screen;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JWindow;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SandBar extends JWindow{
	public static Color closeWinColor = new Color(255, 0, 0, 150);
	public static Color maximizeWinColor = new Color(0, 255, 0, 150);
	public static Color minimizeWinColor = new Color(255, 0, 255, 150);
	
	private JFrame owner;
	private BufferedImage iconImage;
	private int optimalHeight = 30;
	private Point lastLocation;
	private Dimension lastSize;
	private Point lastBarLocation;
	private Dimension lastBarSize;
	private boolean maximized = false;
	private boolean concentrated = false;
	private boolean concentrateOnMaximize = false;
	private TextComp concentrateComp;
	private TextComp titleComp;
	private int pressX;
	private int pressY;
	private TextComp closeComp;
	private TextComp maximizeComp;
	private TextComp minimizeComp;
	
	public SandBar(JFrame frame, BufferedImage image){
		super(frame);
		this.owner = frame;
		this.iconImage = image;
          setType(JWindow.Type.UTILITY);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		setSize(frame.getWidth(), optimalHeight);
		setLocation(frame.getX(), frame.getY() - optimalHeight);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(!concentrated && pressY > e.getY() && owner.getY() + owner.getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
					setLocation(e.getXOnScreen() - pressX, getY());
					owner.setLocation(e.getXOnScreen() - pressX, owner.getY());
				}
				else {
					setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
					owner.setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY + optimalHeight);
				}
			}
		});
		init();
	}
	public void init(){
		concentrateComp = new TextComp("", c1, c2, c3, null){
			@Override
			public void draw(Graphics2D g){
				if(iconImage != null){
					g.drawImage(iconImage, 0, 0, optimalHeight, optimalHeight, this);
				}
			}
		};
		concentrateComp.setArc(0, 0);
		concentrateComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 1 && e.getClickCount() == 2){
					concentrate();
					return;
				}
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		concentrateComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(!concentrated && pressY < e.getY() && owner.getY() + owner.getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
					setLocation(e.getXOnScreen() - pressX - concentrateComp.getX(), getY());
					owner.setLocation(e.getXOnScreen() - pressX - concentrateComp.getX(), owner.getY());
				}
				else {
					setLocation(e.getXOnScreen() - pressX - concentrateComp.getX(), e.getYOnScreen() - pressY);
					owner.setLocation(e.getXOnScreen() - pressX - concentrateComp.getX(), e.getYOnScreen() - pressY + optimalHeight);
				}
			}
		});
		add(concentrateComp);
		
		titleComp = new TextComp(owner.getTitle(), c1, c3, c2, null);
		titleComp.setClickable(false);
		titleComp.setFont(PX16);
		titleComp.setArc(10, 10);
		titleComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		titleComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(!concentrated && pressY < e.getY() && owner.getY() + owner.getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
					setLocation(e.getXOnScreen() - pressX - titleComp.getX(), getY());
					owner.setLocation(e.getXOnScreen() - pressX - titleComp.getX(), owner.getY());
				}
				else {
					setLocation(e.getXOnScreen() - pressX - titleComp.getX(), e.getYOnScreen() - pressY);
					owner.setLocation(e.getXOnScreen() - pressX - titleComp.getX(), e.getYOnScreen() - pressY + optimalHeight);
				}
			}
		});
		add(titleComp);
		
		closeComp = new TextComp("", c1, c2, c3, this::disposeAll){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(closeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString("x", getWidth()/2 - g.getFontMetrics().stringWidth("x")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(closeComp);
		maximizeComp = new TextComp("", c1, c2, c3, this::maximize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(maximizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString("<>", getWidth()/2 - g.getFontMetrics().stringWidth("<>")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(maximizeComp);
		
		minimizeComp = new TextComp("", c1, c2, c3, this::minimize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(minimizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString("-", getWidth()/2 - g.getFontMetrics().stringWidth("-")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(minimizeComp);
	}
     
	@Override
	public void paint(Graphics graphics){
		titleComp.setText(owner.getTitle());
		concentrateComp.setToolTipText(owner.getTitle());
		resize();
		super.paint(graphics);
	}
	public void recentre(){
		setSize(owner.getWidth(), optimalHeight);
		setLocation(owner.getX(), owner.getY() - optimalHeight);
	}
	
	public void resize(){
		concentrateComp.setBounds(0, 0, optimalHeight, optimalHeight);
		titleComp.setBounds(optimalHeight, 0, getWidth() - (optimalHeight * 4), optimalHeight);
		closeComp.setBounds(getWidth() - optimalHeight, 0, optimalHeight, optimalHeight);
		maximizeComp.setBounds(getWidth() - (optimalHeight * 2), 0, optimalHeight, optimalHeight);
		minimizeComp.setBounds(getWidth() - (optimalHeight * 3), 0, optimalHeight, optimalHeight);
	}
	public void setOptimalHeight(int h){
		if(h < 20)
			h = 20;
		if(optimalHeight == h)
			return;
		this.optimalHeight = h;
		resize();
	}
	public int getOptimalHeight(){
		return optimalHeight;
	}
	public void concentrate(){
		if(!maximized)
			return;
		if(!concentrated){
			lastBarLocation = getLocation();
			lastBarSize = getSize();
			setSize(optimalHeight, optimalHeight);
			setLocation(owner.getX() + 6, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
		}
		else{
			setLocation(lastBarLocation);
			setSize(lastBarSize);
			maximize();
		}
		concentrated = !concentrated;
	}
	public void maximize(){
		if(!maximized){
			lastLocation = owner.getLocation();
			lastSize = owner.getSize();
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			owner.setSize((int)size.getWidth(), (int)size.getHeight() - optimalHeight);
			setSize((int)size.getWidth(), optimalHeight);
			setLocation(0, 0);
			owner.setLocation(0, optimalHeight - 1);
		}
		else{
			owner.setLocation(lastLocation);
			owner.setSize(lastSize);
			setSize(owner.getWidth(), optimalHeight);
			setLocation(owner.getX(), owner.getY() - optimalHeight);
		}
		maximized = !maximized;
		if(maximized){
			if(concentrateOnMaximize && !concentrated)
				concentrate();
		}
		resize();
	}
	public void minimize(){
		owner.setState(JFrame.ICONIFIED);
	}
	public void disposeAll(){
		owner.dispose();
		dispose();
	}
	public boolean isConcentrateOnMaximize() {
		return concentrateOnMaximize;
	}
	public void setConcentrateOnMaximize(boolean concentrateOnMaximize) {
		this.concentrateOnMaximize = concentrateOnMaximize;
	}
	public boolean isMaximized(){
		return maximized;
	}
	@Override
	public void setVisible(boolean value){
		if(value){
			resize();
			recentre();
		}
		super.setVisible(value);
	}
}

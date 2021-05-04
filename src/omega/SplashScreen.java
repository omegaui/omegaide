package omega;
import javax.swing.*;
import omega.tabPane.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;

import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class SplashScreen extends JFrame{
	private static final String NAME = "Omega IDE";
	private static final String EDITION = "community";
	private static final String VERSION = Screen.VERSION;
	private static String ENCOURAGE = "lets code";
	private static final Color ALPHA = new Color(0, 0, 0, 0);
     private static Color BACK_COLOR;
     private static Color TITLE_COLOR;
     private static Color VERSION_COLOR;
     private static Color EDITION_COLOR;
     private static Color PROGRESS_COLOR;
	private static BufferedImage image = (BufferedImage)omega.utils.IconManager.getImageIcon("/omega_ide_icon128.png").getImage();
	private volatile int progress = 0;
	private int x = 40, y = 163;
	private volatile boolean ground = false;
     private int mouseX;
     private int mouseY;

	public SplashScreen(){
          BACK_COLOR = c2;
          TITLE_COLOR = TOOLMENU_COLOR1;
          VERSION_COLOR = TOOLMENU_COLOR3;
          EDITION_COLOR = TOOLMENU_COLOR3;
          PROGRESS_COLOR = TOOLMENU_COLOR2;
		setUndecorated(true);
		pack();
		createBufferStrategy(3);
		setSize(300, 300);
		setBackground(BACK_COLOR);
          setIconImage(image);
		setLocationRelativeTo(null);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				render();
				paint(getGraphics());
                    mouseX = e.getX();
                    mouseY = e.getY();
			}
		});
          addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
               }
          });
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		setVisible(true);
		new Thread(()->{
			while(progress < 100 && isVisible()){
				render();
				paint(getGraphics());
			}
			setVisible(false);
		}).start();
	}

	public void render(){
		setBackground(ALPHA);
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		Graphics graphics = null;
		try{
			graphics = bs.getDrawGraphics();
		}catch(Exception e){ return; }
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(BACK_COLOR);
		g.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 2, 140, 140);
		g.setColor(TITLE_COLOR);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 140, 140);
		g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 140, 140);
		g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 140, 140);
		g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 140, 140);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 140, 140);
		g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 140, 140);
		g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 140, 140);
		g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 140, 140);
		g.setFont(PX40);
		g.drawString(NAME, getWidth()/2 - g.getFontMetrics().stringWidth(NAME)/2, 190);
		g.setFont(PX26);
          g.setColor(EDITION_COLOR);
		g.drawString(EDITION, getWidth()/2 - g.getFontMetrics().stringWidth(EDITION)/2, 190 + 30);
		g.setFont(PX20);
          g.setColor(VERSION_COLOR);
		g.drawString(VERSION, getWidth()/2 - g.getFontMetrics().stringWidth(VERSION)/2, 190 + 30 + 20);
		g.setFont(PX22);
		g.setColor(PROGRESS_COLOR);
		g.fillRoundRect(getWidth()/2 - 50 - 25, getHeight() - 50, 150, 30, 20, 20);
		g.setColor(BACK_COLOR);
		g.fillRect(getWidth()/2 - progress/2, getHeight() - 25, progress, 5);
		g.drawString(ENCOURAGE, getWidth()/2 - g.getFontMetrics().stringWidth(ENCOURAGE)/2, getHeight() - 30);
		g.setColor(PROGRESS_COLOR);
		if(x == 40) ground = false;
		else if(x == 60) ground = true;
		if(ground) x--;
		else x++;
		int[] X = {x, x - 15, x, x - 5, x};
		int[] Y = {y, y + 15, y + 30, y + 15, y};
		g.fillPolygon(X, Y, X.length);
		int[] _X = {getWidth() - x - 1, getWidth() - x - 15 - 1 + 20, getWidth() - x - 1, getWidth() - x + 15 - 1, getWidth() - x - 1};
		int[] _Y = {y, y + 15, y + 30, y + 15, y};
		g.fillPolygon(_X, _Y, X.length);
          g.setColor(BACK_COLOR);
          //g.fillRect(getWidth()/2 - 64, 20, 128, 128);
		g.drawImage(image, getWidth()/2 - 64, 20, 128, 128, null);
		bs.show();
	}

	@Override
	public void paint(Graphics g){
          
	}

	public void setProgress(int progress, String status){
		this.progress = progress;
		if(progress < 85)
			SplashScreen.ENCOURAGE = status;
		else
			SplashScreen.ENCOURAGE = "lets code";
		repaint();
	}
}
package ide;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import tabPane.IconManager;

import java.awt.event.*;
public class SplashScreen extends JFrame{
	private static final String NAME = "Omega IDE";
	private static final String EDITION = "community";
	private static final String VERSION = "v1.0";
	private static final String ENCOURAGE = "lets code";
	private static final Color ALPHA = new Color(0, 0, 0, 0);
	private static final Font PX40 = new Font("Ubuntu Mono", Font.BOLD, 40);
	private static final Font PX26 = new Font("Ubuntu Mono", Font.BOLD, 26);
	private static final Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static final Font PX22 = new Font("Ubuntu Mono", Font.BOLD, 22);
	private static Color BACK;
	private static Color BLU;
	private static Color DG;
	private static BufferedImage image;
	private volatile int progress = 0;
	private int x = 40, y = 163;
	private volatile boolean ground = false;

	public SplashScreen(){
		boolean isDarkMode = ((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 53;
		BACK = isDarkMode ? Color.BLACK : Color.WHITE;
		BLU = isDarkMode ? Color.GREEN : Color.BLUE;
		DG = isDarkMode ? Color.WHITE : Color.DARK_GRAY;
		try{
			final String NAME = isDarkMode ? "/omega_ide_icon128_dark.png" : "/omega_ide_icon128.png";
			image = ImageIO.read(getClass().getResourceAsStream(NAME));
			setIconImage(IconManager.getImageIcon("/omega_ide_icon64.png").getImage());
		}catch(Exception e){}
		setUndecorated(true);
		pack();
		createBufferStrategy(3);
		setSize(300, 300);
		setBackground(Color.WHITE);
		setLocationRelativeTo(null);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				render();
				paint(getGraphics());
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
			}
			setVisible(false);
		}).start();
		render();
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
		g.setColor(BACK);
		g.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 2, 140, 140);
		g.setColor(BLU);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 140, 140);
		g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 140, 140);
		g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 140, 140);
		g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 140, 140);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 140, 140);
		g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 140, 140);
		g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 140, 140);
		g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 140, 140);
		g.drawImage(image, getWidth()/2 - 64, 20, 128, 128, this);
		g.setFont(PX40);
		g.setColor(DG);
		g.drawString(NAME, getWidth()/2 - g.getFontMetrics().stringWidth(NAME)/2, 190);
		g.setFont(PX26);
		g.drawString(EDITION, getWidth()/2 - g.getFontMetrics().stringWidth(EDITION)/2, 190 + 30);
		g.setFont(PX20);
		g.drawString(VERSION, getWidth()/2 - g.getFontMetrics().stringWidth(VERSION)/2, 190 + 30 + 20);
		g.setFont(PX22);
		g.setColor(BLU);
		g.fillRoundRect(getWidth()/2 - 50 - 25, getHeight() - 50, 150, 30, 20, 20);
		g.setColor(BACK);
		g.fillRect(getWidth()/2 - progress/2, getHeight() - 25, progress, 5);
		g.drawString(ENCOURAGE, getWidth()/2 - g.getFontMetrics().stringWidth(ENCOURAGE)/2, getHeight() - 30);
		g.setColor(BLU);
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
		bs.show();
	}

	@Override
	public void paint(Graphics graphics){
		graphics.drawImage(image, getWidth()/2 - 64, 20, 128, 128, this);
		graphics.drawImage(image, getWidth()/2 - 64, 20, 128, 128, null);
	}

	public void setProgress(int progress){
		this.progress = progress;
		repaint();
	}
}
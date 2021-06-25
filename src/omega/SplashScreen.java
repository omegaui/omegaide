/**
* SplashScreen
* Copyright (C) 2021 Omega UI
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package omega;
import javax.imageio.*;
import javax.swing.*;
import omega.tabPane.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import static omega.Screen.VERSION;
import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class SplashScreen extends JFrame{
	private static final String NAME = "Omega IDE";
	private static final String EDITION = "community";
	private static final String HINT = "Animation not supported on Windows!";
	private static String ENCOURAGE = "lets code";
	private static final Color ALPHA = new Color(0, 0, 0, 0);
	private static Color BACK_COLOR;
	private static Color TITLE_COLOR;
	private static Color VERSION_COLOR;
	private static Color EDITION_COLOR;
	private static Color PROGRESS_COLOR;
	private static Color SHADE = TOOLMENU_GRADIENT;
	private static GradientPaint gradient = new GradientPaint(0, 0, c2, 300, Screen.onWindows() ? 250 : 300, SHADE);
	private static BufferedImage image = (BufferedImage)omega.utils.IconManager.getImageIcon("/omega_ide_icon128.png").getImage();
	private volatile int progress = 0;
	private int x = 40, y = 163;
	private volatile boolean ground = false;
	private int mouseX;
	private int mouseY;
	public SplashScreen() {
		BACK_COLOR = c2;
		TITLE_COLOR = TOOLMENU_COLOR1;
		VERSION_COLOR = TOOLMENU_COLOR3;
		EDITION_COLOR = TOOLMENU_COLOR3;
		PROGRESS_COLOR = TOOLMENU_COLOR2;
		
		setUndecorated(true);
		pack();
		
		if(!Screen.onWindows())
			createBufferStrategy(3);
		
		setSize(300, Screen.onWindows() ? 250 : 300);
		setLocationRelativeTo(null);
		setBackground(BACK_COLOR);
		
		try{
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon500.png")));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
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
		setBackground(Screen.onWindows() ? BACK_COLOR : ALPHA);
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		Graphics graphics = null;
		try{
			graphics = bs.getDrawGraphics();
		}
		catch(Exception e){
			return;
		}
		
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(BACK_COLOR);
		g.setPaint(gradient);
		if(Screen.onWindows())
			g.fillRect(0, 0, getWidth(), getHeight());
		else
			g.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 2, 100, 100);
		g.setColor(TITLE_COLOR);
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
		else 
			x++;
		int[] X = {x, x - 15, x, x - 5, x};
		int[] Y = {y, y + 15, y + 30, y + 15, y};
		g.fillPolygon(X, Y, X.length);
		int[] _X = {getWidth() - x - 1, getWidth() - x - 15 - 1 + 20, getWidth() - x - 1, getWidth() - x + 15 - 1, getWidth() - x - 1};
		int[] _Y = {y, y + 15, y + 30, y + 15, y};
		g.fillPolygon(_X, _Y, X.length);
		g.setColor(BACK_COLOR);
		g.drawImage(image, getWidth()/2 - 64, 20, 128, 128, null);
		bs.show();
	}
	
	@Override
	public void paint(Graphics graphics){
		
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

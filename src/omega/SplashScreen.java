package omega;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
public class SplashScreen extends JFrame{
	private static final String NAME = "Omega IDE";
	private static final String EDITION = "community";
	private static final String VERSION = Screen.VERSION;
	private static String ENCOURAGE = "lets code";
	private static final Color ALPHA = new Color(0, 0, 0, 0);
	private static final Font PX40 = new Font("Ubuntu Mono", Font.BOLD, 40);
	private static final Font PX26 = new Font("Ubuntu Mono", Font.BOLD, 26);
	private static final Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static final Font PX22 = new Font("Ubuntu Mono", Font.BOLD, 22);
	private static Color BACK;
	private static Color BLU;
	private static Color DG;
	private static BufferedImage image = (BufferedImage)omega.tabPane.IconManager.getImageIcon("/omega_ide_icon128.png").getImage();
	private volatile int progress = 0;
	private int x = 40, y = 163;
	private volatile boolean ground = false;
     private int mouseX;
     private int mouseY;

	public SplashScreen(){
		BACK = omega.utils.UIManager.c2;
		BLU = omega.utils.UIManager.c3;
		DG = omega.utils.UIManager.isDarkMode() ? omega.utils.UIManager.c1 : BLU;
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
          g.setColor(BACK);
          //g.fillRect(getWidth()/2 - 64, 20, 128, 128);
		g.drawImage(image, getWidth()/2 - 64, 20, 128, 128, null);
		bs.show();
	}

	@Override
	public void paint(Graphics g){
          //g.setColor(BACK);
          //g.fillRect(getWidth()/2 - 64, 20, 128, 128);
		//g.drawImage(image, getWidth()/2 - 64, 20, 128, 128, null);
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
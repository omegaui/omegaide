/**
* Theme Chooser Window
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

package omega.utils;
import java.awt.geom.RoundRectangle2D;

import omega.Screen;

import javax.imageio.ImageIO;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import omega.comp.TextComp;
import omega.comp.SwitchComp;

import java.awt.image.BufferedImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class ThemePicker extends JDialog {
	private Color c1 = new Color(126, 20, 219, 40);
	private Color c2 = Color.WHITE;
	private Color c3 = new Color(126, 20, 219);
	private Color b1 = Color.decode("#132162");
	private Color b2 = Color.decode("#1e1e1e");
	private Color b3 = Color.decode("#3CE5DD");
	private int pressX;
	private int pressY;
	private BufferedImage image;
	
	//Components
	private JPanel panel;
	private TextComp applyComp;
	private TextComp titleComp;
	private TextComp lightComp;
	private TextComp darkComp;
	
	public boolean lightMode = true;
	
	public ThemePicker(Screen screen){
		super(screen);
		setTitle("Theme Picker");
		setModal(true);
		panel = new JPanel(null){
			@Override
			public void paint(Graphics graphics){
				super.paint(graphics);
				Graphics2D g = (Graphics2D)graphics;
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setColor(lightMode ? c3 : b3);
				if(image == null){
					g.setFont(PX20);
					String msg = "Unable to Read the Preview Image";
					g.drawString(msg, getWidth()/2 - g.getFontMetrics().stringWidth(msg)/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
				else{
					g.drawImage(image, 5, 30, getWidth() - 10, 415, null);
				}
			}
		};
		panel.setBackground(c2);
		setUndecorated(true);
		setLayout(null);
		setSize(720, 455);
		setLocationRelativeTo(null);
		setContentPane(panel);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
		init();
	}
	public void init(){
		applyComp = new TextComp("Apply", c1, c2, TOOLMENU_COLOR2, ()->{
			dispose();
			DataManager.setTheme(lightMode ? "light" : "dark");
			Screen.getDataManager().save();
		});
		applyComp.setBounds(getWidth() - 80, 0, 80, 30);
		applyComp.setFont(PX16);
		applyComp.setArc(0, 0);
		add(applyComp);
		
		titleComp = new TextComp("Choose IDE Theme", c1, c2, c3, null);
		titleComp.setBounds(0, 0, getWidth() - 80 - 240, 30);
		titleComp.setFont(PX16);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
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
				setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
			}
		});
		add(titleComp);
		
		lightComp = new TextComp("<light>", c1, c2, c3, ()->{
			lightMode = true;
			manageTheme();
			loadImage("light.png");
			}){
			@Override
			public void draw(Graphics2D graphics){
				if(lightMode){
					graphics.setColor(c3);
					graphics.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		lightComp.setBounds(getWidth() - 320, 0, 120, 30);
		lightComp.setArc(0, 0);
		lightComp.setFont(PX20);
		add(lightComp);
		
		darkComp = new TextComp("<dark>", c1, c2, c3, ()->{
			lightMode = false;
			manageTheme();
			loadImage("dark.png");
			}){
			@Override
			public void draw(Graphics2D graphics){
				if(!lightMode){
					graphics.setColor(b3);
					graphics.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		darkComp.setBounds(getWidth() - 200, 0, 120, 30);
		darkComp.setArc(0, 0);
		darkComp.setFont(PX20);
		add(darkComp);
	}
	
	public void manageTheme(){
		Color c1 = lightMode ? this.c1 : b1;
		Color c2 = lightMode ? this.c2 : b2;
		Color c3 = lightMode ? this.c3 : b3;
		panel.setBackground(c2);
		applyComp.setColors(c1, c2, c3);
		titleComp.setColors(c1, c2, c3);
		lightComp.setColors(c1, c2, c3);
		darkComp.setColors(c1, c2, c3);
		repaint();
	}
	
	public void loadImage(String name){
		try{
			image = ImageIO.read(getClass().getResourceAsStream("/" + name));
		}
		catch(Exception e) {
			System.err.println(e);
		}
	}
}


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

package omega.ui.dialog;
import omega.Screen;

import javax.imageio.ImageIO;

import omega.io.DataManager;
import omega.io.IconManager;

import java.awt.geom.RoundRectangle2D;

import omegaui.component.TextComp;

import java.awt.image.BufferedImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class ThemePicker extends JDialog {
	private Color c1 = new Color(126, 20, 219, 40);
	private Color c2 = Color.WHITE;
	private Color c3 = new Color(126, 20, 219);
	private Color b1 = Color.decode("#132162");
	private Color b2 = Color.decode("#1e1e1e");
	private Color b3 = Color.decode("#3CE5DD");

	private BufferedImage image;
	
	//Components
	private JPanel panel;
	private TextComp applyComp;
	private TextComp iconComp;
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
					g.drawImage(image, getWidth()/2 - 960/2, getHeight()/2 + 3 - 540/2, 960, 540, null);
				}
			}
		};
		panel.setBackground(c2);
		setUndecorated(true);
		setLayout(null);
		setSize(980, 640);
		setLocationRelativeTo(null);
		setContentPane(panel);
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

		iconComp = new TextComp(IconManager.fluentchangethemeImage, 24, 24, back2, back2, back2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		add(iconComp);
		
		titleComp = new TextComp("Choose IDE Theme", back2, back2, glow, null);
		titleComp.setBounds(30, 0, getWidth() - 110 - 240, 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);
		
		lightComp = new TextComp("<light>", c1, back3, c3, ()->{
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
		
		darkComp = new TextComp("<dark>", c1, back3, c3, ()->{
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
		Color back2 = lightMode ? Color.decode("#fcfcfc") : Color.decode("#262626");
		Color back3 = lightMode ? Color.decode("#eaeaea") : Color.decode("#303030");
		Color glow = lightMode ? Color.BLACK : Color.WHITE;
		panel.setBackground(c2);
		applyComp.setColors(c1, c2, TOOLMENU_COLOR2);
		iconComp.setColors(back2, back2, back2);
		titleComp.setColors(back2, back2, glow);
		lightComp.setColors(c1, back3, c3);
		darkComp.setColors(c1, back3, c3);
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


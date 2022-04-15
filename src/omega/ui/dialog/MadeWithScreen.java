/*
 * MadeWithScreen
 * Copyright (C) 2022 Omega UI

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
import omega.io.IconManager;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;

import java.net.URL;

import java.awt.image.BufferStrategy;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Desktop;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.geom.RoundRectangle2D;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class MadeWithScreen extends JDialog{
	private TextComp rImageComp;
	private TextComp rLinkComp;

	private TextComp lafImageComp;
	private TextComp lafLinkComp;

	private TextComp fluentImageComp;
	private TextComp fluentLinkComp;

	private TextComp jetImageComp;
	private TextComp jetLinkComp;

	private TextComp omegauiImageComp;
	private TextComp omegauiLinkComp;

	public MadeWithScreen(JFrame frame){
		super(frame, true);
		setTitle("Made With");
		setUndecorated(true);
		setSize(400, 460);
		setLocationRelativeTo(null);
		setBackground(back1);
		FlexPanel panel = new FlexPanel(null, back1, back3);
		panel.setPaintGradientEnabled(true);
		panel.setArc(0, 0);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		setContentPane(panel);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				dispose();
			}
		});
		init();
	}

	public void init(){
		TextComp mes = new TextComp("#OpenSourceRULES", getBackground(), getBackground(), glow, null);
		mes.setBounds(0, 0, getWidth(), 30);
		mes.setFont(PX20);
		add(mes);

		rImageComp = new TextComp(IconManager.fluentfileImage, 50, 50, c2, c2, c2, null);
		rImageComp.setBounds(50, 50, 60, 60);
		rImageComp.setClickable(false);
		add(rImageComp);

		rLinkComp = new TextComp("RSyntaxTextArea", "https://github.com/bobbylight/RSyntaxTextArea", c2, c2, TOOLMENU_COLOR2, ()->{
			openURl(rLinkComp.getToolTipText());
		});
		rLinkComp.setBounds(120, 50, getWidth() - 200, 60);
		rLinkComp.setFont(PX20);
		add(rLinkComp);

		TextComp rMessage = new TextComp("with theming tweaks", c2, c2, TOOLMENU_COLOR4, null);
		rMessage.setBounds(0, rLinkComp.getHeight() - 20, rLinkComp.getWidth(), 20);
		rMessage.setArc(0, 0);
		rMessage.setFont(PX14);
		rLinkComp.add(rMessage);

		lafImageComp = new TextComp(IconManager.fluentsettingsImage, 50, 50, c2, c2, c2, null);
		lafImageComp.setBounds(50, 120, 60, 60);
		lafImageComp.setFont(PX22);
		lafImageComp.setClickable(false);
		add(lafImageComp);

		lafLinkComp = new TextComp("FlatLaf", "https://www.formdev.com/flatlaf", c2, c2, TOOLMENU_COLOR1, ()->{
			openURl(lafLinkComp.getToolTipText());
		});
		lafLinkComp.setBounds(120, 120, getWidth() - 200, 60);
		lafLinkComp.setFont(PX20);
		add(lafLinkComp);

		TextComp lafMessage = new TextComp("with minor tweaks", c2, c2, TOOLMENU_COLOR4, null);
		lafMessage.setBounds(0, lafLinkComp.getHeight() - 25, lafLinkComp.getWidth(), 25);
		lafMessage.setArc(0, 0);
		lafMessage.setFont(PX14);
		lafLinkComp.add(lafMessage);

		fluentImageComp = new TextComp(IconManager.fluenticons8Logo, 50, 50, c2, c2, c2, null);
		fluentImageComp.setBounds(50, 190, 60, 60);
		fluentImageComp.setFont(PX22);
		fluentImageComp.setClickable(false);
		add(fluentImageComp);

		fluentLinkComp = new TextComp("Fluent Icons", "https://icons8.com", c2, c2, TOOLMENU_COLOR2, ()->{
			openURl(fluentLinkComp.getToolTipText());
		});
		fluentLinkComp.setBounds(120, 190, getWidth() - 200, 60);
		fluentLinkComp.setFont(PX20);
		add(fluentLinkComp);

		TextComp fluentMessage = new TextComp("without any tweaks", c2, c2, TOOLMENU_COLOR4, null);
		fluentMessage.setBounds(0, fluentLinkComp.getHeight() - 25, fluentLinkComp.getWidth(), 25);
		fluentMessage.setArc(0, 0);
		fluentMessage.setFont(PX14);
		fluentLinkComp.add(fluentMessage);

		jetImageComp = new TextComp(IconManager.fluentjetbrainsLogo, 50, 50, c2, c2, c2, null);
		jetImageComp.setBounds(50, 260, 60, 60);
		jetImageComp.setFont(PX22);
		jetImageComp.setClickable(false);
		add(jetImageComp);

		jetLinkComp = new TextComp("Jediterm", "https://github.com/JetBrains/jediterm", c2, c2, TOOLMENU_COLOR2, ()->{
			openURl(jetLinkComp.getToolTipText());
		});
		jetLinkComp.setBounds(120, 260, getWidth() - 200, 60);
		jetLinkComp.setFont(PX20);
		add(jetLinkComp);

		TextComp jetMessage = new TextComp("with theming tweaks", c2, c2, TOOLMENU_COLOR4, null);
		jetMessage.setBounds(0, fluentLinkComp.getHeight() - 25, fluentLinkComp.getWidth(), 25);
		jetMessage.setArc(0, 0);
		jetMessage.setFont(PX14);
		jetLinkComp.add(jetMessage);

		omegauiImageComp = new TextComp(IconManager.ideImage64, 64, 64, c2, c2, c2, null);
		omegauiImageComp.setBounds(40, 330, 70, 70);
		omegauiImageComp.setClickable(false);
		add(omegauiImageComp);

		omegauiLinkComp = new TextComp("omegaui", "https://github.com/omegaui", c2, c2, TOOLMENU_COLOR3, ()->{
			openURl(omegauiLinkComp.getToolTipText());
		});
		omegauiLinkComp.setBounds(120, 330, getWidth() - 190, 70);
		omegauiLinkComp.setFont(PX26);
		add(omegauiLinkComp);

		TextComp omegaMessage = new TextComp("AddingFeathersToDevelopment", c2, c2, TOOLMENU_COLOR4, null);
		omegaMessage.setBounds(0, omegauiLinkComp.getHeight() - 25, omegauiLinkComp.getWidth(), 25);
		omegaMessage.setArc(0, 0);
		omegaMessage.setFont(PX14);
		omegauiLinkComp.add(omegaMessage);

		TextComp instaComp = new TextComp(IconManager.fluentinstagramImage, 24, 24, c2, c2, c2, ()->{
			openURl("https://www.instagram.com/the_open_source_guy");
		});
		instaComp.setBounds(getWidth()/2 - 30/2 - 30, getHeight() - 50, 30, 30);
		instaComp.setArc(0, 0);
		instaComp.setToolTipText("https://www.instagram.com/the_open_source_guy");
		add(instaComp);

		TextComp githubComp = new TextComp(IconManager.fluentgithubIcon, 24, 24, c2, c2, c2, ()->{
			openURl("https://github.com/omegaui");
		});
		githubComp.setBounds(getWidth()/2 - 30/2, getHeight() - 50, 30, 30);
		githubComp.setArc(0, 0);
		githubComp.setToolTipText("https://github.com/omegaui");
		add(githubComp);

		TextComp youtubeComp = new TextComp(IconManager.fluentyoutubeiconImage, 24, 24, c2, c2, c2, ()->{
			openURl("https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg");
		});
		youtubeComp.setBounds(getWidth()/2 - 30/2 + 30, getHeight() - 50, 30, 30);
		youtubeComp.setArc(0, 0);
		youtubeComp.setToolTipText("https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg");
		add(youtubeComp);

		putAnimationLayer(instaComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(githubComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(youtubeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
	}

	public void openURl(String url){
		new Thread(()->{
			try{
				Desktop.getDesktop().browse(new URL(url).toURI());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}
}


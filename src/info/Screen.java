package info;
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
import settings.comp.Comp;
import static ide.Screen.VERSION;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;

public class Screen extends JDialog{
	private BufferedImage image;
	private static final String TITLE = "Omega IDE";
	private static final String EDITION = "Community";
	private static final String WARRANTY = "This program comes with absolutely no warranty";
	private static final String COMPANY = "Omega UI";
	private static final String CREDIT = "Created by : Arham";
	private static final String COPYRIGHT = "Copyright © 2020 Omega UI. All Right Reserved";
	private static final Font PX28 = new Font("Ubuntu Mono", Font.BOLD, 28);
	private static final Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static final Font PX12 = new Font("Ubuntu Mono", Font.ITALIC, 12);
	private static final Font PX30 = new Font("Ubuntu Mono", Font.BOLD, 30);
	public static byte STATE = 0;
	private static Comp infoBtn;
	private static Comp wnBtn;
	public Screen(ide.Screen screen){
		super(screen, true);
		setLayout(null);
		setTitle("Info");
		try{
			image = (BufferedImage)tabPane.IconManager.getImageIcon("/omega_ide_icon128.png").getImage();
		}catch(Exception e){e.printStackTrace();}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
			}
		});
		setUndecorated(true);
		setSize(300, 350);
		setLocationRelativeTo(null);
          setBackground(ide.utils.UIManager.c2);

		infoBtn = new Comp("About", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
               STATE = 0;
               repaint();
	     });
		infoBtn.setBounds(0, getHeight() - 40, getWidth()/2, 40);
          infoBtn.setArc(0, 0);
		add(infoBtn);

		wnBtn = new Comp("Whats New!", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
               STATE = 1;
               repaint();
	     });
          wnBtn.setArc(0, 0);
		wnBtn.setBounds(getWidth()/2, getHeight() - 40, getWidth()/2, 40);
		add(wnBtn);
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(ide.utils.UIManager.c2);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(STATE == 0){
			g.drawImage(image, getWidth()/2 - image.getWidth()/2, 20, image.getWidth(), image.getHeight(), this);
			//Drawing Text
			g.setColor(ide.utils.UIManager.c3);
			g.setFont(PX28);
			g.drawString(TITLE, getWidth()/2 - g.getFontMetrics().stringWidth(TITLE)/2, image.getHeight() + 44);
			g.setFont(PX20);
			g.drawString(EDITION, getWidth()/2 - g.getFontMetrics().stringWidth(EDITION)/2, image.getHeight() + 44 + 24);
			g.drawString(VERSION, getWidth()/2 - g.getFontMetrics().stringWidth(VERSION)/2, image.getHeight() + 44 + 24 + 20);
			g.setFont(PX12);
			g.drawString(WARRANTY, getWidth()/2 - g.getFontMetrics().stringWidth(WARRANTY)/2, image.getHeight() + 44 + 24 + 20 + 20);
			g.drawString(WARRANTY, getWidth()/2 - g.getFontMetrics().stringWidth(WARRANTY)/2, image.getHeight() + 44 + 24 + 20 + 20);
			g.setFont(PX30);
			g.drawString(COMPANY, getWidth()/2 - g.getFontMetrics().stringWidth(COMPANY)/2, image.getHeight() + 44 + 24 + 20 + 20 + 30);
			g.setFont(PX12);
			g.drawString(CREDIT, getWidth()/2 - g.getFontMetrics().stringWidth(CREDIT)/2, image.getHeight() + 44 + 24 + 20 + 20 + 25 + 20);
			g.drawString(COPYRIGHT, getWidth()/2 - g.getFontMetrics().stringWidth(COPYRIGHT)/2, image.getHeight() + 44 + 24 + 20 + 20 + 25 + 35);
			g.drawString(CREDIT, getWidth()/2 - g.getFontMetrics().stringWidth(CREDIT)/2, image.getHeight() + 44 + 24 + 20 + 20 + 25 + 20);
			g.drawString(COPYRIGHT, getWidth()/2 - g.getFontMetrics().stringWidth(COPYRIGHT)/2, image.getHeight() + 44 + 24 + 20 + 20 + 25 + 35);
		}
		else{
			g.setColor(ide.utils.UIManager.c3);
			g.setFont(PX20);
			g.drawString("BUGS FIXED IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("BUGS FIXED IN THIS RELEASE")/2, 21);
			g.setFont(PX12);
			g.drawString("Major Bug Fixes", getWidth()/2 - g.getFontMetrics().stringWidth("Major Bug Fixes")/2, 21 + 21);

			g.setFont(PX20);
			g.drawString("NEW FEATURES IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("NEW FEATURES IN THIS RELEASE")/2, 21 + 21 + 21 + 21 + 51);
			g.setFont(PX12);
			g.drawString("Structure View", getWidth()/2 - g.getFontMetrics().stringWidth("Structure View")/2, 21 + 21 + 21 + 21 + 21 + 51);
               g.drawString("More Standard Tools", getWidth()/2 - g.getFontMetrics().stringWidth("More Standard Tools")/2, 21 + 21 + 21 + 21 + 21 + 21 + 51);
               g.drawString("Added a basic manual", getWidth()/2 - g.getFontMetrics().stringWidth("Added a basic manual")/2, 21 + 21 + 21 + 21 + 21 + 21 + 21 + 51);
			g.drawImage(image, getWidth()/2 - 32, getHeight() - 110, 64, 64, this);
		}
		infoBtn.repaint();
		wnBtn.repaint();
          super.paint(graphics);
	}

	@Override
	public void setVisible(boolean value) {
		if(value) {
			STATE = 0;
			repaint();
		}
		super.setVisible(value);
	}
}

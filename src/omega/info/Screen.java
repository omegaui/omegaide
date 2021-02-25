package omega.info;
import omega.utils.UIManager;
import omega.tabPane.IconManager;
import omega.comp.*;
import static omega.Screen.VERSION;

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
	private static final String COPYRIGHT = "Copyright © 2020-2021 Omega UI";
	private static final Font PX28 = new Font("Ubuntu Mono", Font.BOLD, 28);
	private static final Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static final Font PX12 = new Font("Ubuntu Mono", Font.ITALIC, 12);
	private static final Font PX30 = new Font("Ubuntu Mono", Font.BOLD, 30);
	public static byte STATE = 0;
	private static Comp infoBtn;
	private static Comp wnBtn;
	public Screen(omega.Screen screen){
		super(screen, true);
		setLayout(null);
		setTitle("Info");
		try{
			image = (BufferedImage)IconManager.getImageIcon("/omega_ide_icon128.png").getImage();
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
          setBackground(omega.utils.UIManager.c2);

		infoBtn = new Comp("About", omega.utils.UIManager.c1, omega.utils.UIManager.c3, omega.utils.UIManager.c2, ()->{
               STATE = 0;
               repaint();
	     });
		infoBtn.setBounds(0, getHeight() - 40, getWidth()/2, 40);
          infoBtn.setArc(0, 0);
		add(infoBtn);

		wnBtn = new Comp("Whats New!", omega.utils.UIManager.c1, omega.utils.UIManager.c3, omega.utils.UIManager.c2, ()->{
               STATE = 1;
               repaint();
	     });
          wnBtn.setArc(0, 0);
		wnBtn.setBounds(getWidth()/2, getHeight() - 40, getWidth()/2, 40);
		add(wnBtn);
	}

	@Override
	public void paint(Graphics graphics){
          super.paint(graphics);
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(omega.utils.UIManager.c2);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(STATE == 0){
			g.drawImage(image, getWidth()/2 - image.getWidth()/2, 20, image.getWidth(), image.getHeight(), this);
			//Drawing Text
			g.setColor(omega.utils.UIManager.c3);
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
			g.setColor(omega.utils.UIManager.c3);
			g.setFont(PX20);
			g.drawString("BUGS FIXED IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("BUGS FIXED IN THIS RELEASE")/2, 21);
			g.setFont(PX12);
			g.drawString("Small Bug Fixes", getWidth()/2 - g.getFontMetrics().stringWidth("Small Bug Fixes")/2, 21 + 21);

			g.setFont(PX20);
			g.drawString("NEW FEATURES IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("NEW FEATURES IN THIS RELEASE")/2, 21 + 21 + 21 + 21 + 51);
			g.setFont(PX12);
			g.drawString("Improved Content Assist UI", getWidth()/2 - g.getFontMetrics().stringWidth("Improved Content Assist UI")/2, 21 + 21 + 21 + 21 + 21 + 51);
               g.drawString("Improved Content Assist Performance", getWidth()/2 - g.getFontMetrics().stringWidth("Improved Content Assist Performance")/2, 21 + 21 + 21 + 21 + 21 + 21 + 51);
               g.drawString("Some Visual Tweaks", getWidth()/2 - g.getFontMetrics().stringWidth("Some Visual Tweaks")/2, 21 + 21 + 21 + 21 + 21 + 21 + 21 + 51);
			g.drawImage(image, getWidth()/2 - 32, getHeight() - 110, 64, 64, this);
		}
		infoBtn.repaint();
		wnBtn.repaint();
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
package info;
import tabPane.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
public class Screen extends JDialog{
	private BufferedImage image;
	private static final String TITLE = "Omega IDE";
	private static final String EDITION = "Community";
	private static final String VERSION = "v1.1";
	private static final String WARRANTY = "This program comes with absolutely no warranty";
	private static final String COMPANY = "Omega UI";
	private static final String CREDIT = "Created by : Arham";
	private static final String COPYRIGHT = "Copyright © 2020 Omega UI. All Right Reserved";
	private static final Font PX28 = new Font("Ubuntu Mono", Font.BOLD, 28);
	private static final Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static final Font PX12 = new Font("Ubuntu Mono", Font.ITALIC, 12);
	private static final Font PX30 = new Font("Ubuntu Mono", Font.BOLD, 30);
     private static byte STATE = 0;
     private static JButton infoBtn;
     private static JButton wnBtn;
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
		if(((Color)(javax.swing.UIManager.get("Button.background"))).getRed() <= 53){
			setForeground(Color.WHITE);
		}
		else{
			setForeground(Color.BLACK);
               setBackground(Color.WHITE);
		}

          infoBtn = new JButton("About");
          infoBtn.setBounds(0, getHeight() - 40, getWidth()/2, 40);
          infoBtn.addActionListener((e)->{
               STATE = 0;
               repaint();
          });
          infoBtn.setBackground(Color.WHITE);
          add(infoBtn);
          
          wnBtn = new JButton("Whats New!");
          wnBtn.setBounds(getWidth()/2, getHeight() - 40, getWidth()/2, 40);
          wnBtn.addActionListener((e)->{
               STATE = 1;
               repaint();
          });
          wnBtn.setBackground(Color.WHITE);
          add(wnBtn);
	}

	@Override
	public void paint(Graphics graphics){
		super.paint(graphics);
		Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
          if(STATE == 0){
               g.drawImage(image, getWidth()/2 - image.getWidth()/2, 20, image.getWidth(), image.getHeight(), this);
     		//Drawing Text
     		g.setColor(getForeground());
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
               g.setColor(getForeground());
               g.setFont(PX20);
               g.drawString("BUGS FIXED IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("BUGS FIXED IN THIS RELEASE")/2, 21);
               g.setFont(PX12);
               g.drawString("TabPane glitch fixed", getWidth()/2 - g.getFontMetrics().stringWidth("TabPane glitch fixed")/2, 21 + 21);
               
               g.setFont(PX20);
               g.drawString("NEW FEATURES IN THIS RELEASE", getWidth()/2 - g.getFontMetrics().stringWidth("NEW FEATURES IN THIS RELEASE")/2, 21 + 21 + 21 + 21 + 51);
               g.setFont(PX12);
               g.drawString("New Plugin Management System", getWidth()/2 - g.getFontMetrics().stringWidth("New Plugin Management System")/2, 21 + 21 + 21 + 21 + 21 + 51);
               g.drawString("Performance Improvements", getWidth()/2 - g.getFontMetrics().stringWidth("Performance Improvements")/2, 21 + 21 + 21 + 21 + 21 + 21 + 51);
               g.drawImage(image, getWidth()/2 - 32, getHeight() - 110, 64, 64, this);
          }
          infoBtn.repaint();
          wnBtn.repaint();
	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) infoBtn.doClick();
		super.setVisible(value);
	}
}

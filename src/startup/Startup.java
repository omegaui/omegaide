package startup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

import ide.Screen;
public class Startup extends JDialog {
	public static final Color BASE_COLOR = new Color(20, 20, 160);
	public static final Font BASE_FONT = new Font("Ubuntu Mono", Font.BOLD, 15);
	public static final Font BASE_FONT_BIG = new Font("Ubuntu Mono", Font.BOLD, 20);
	private static BufferedImage image;
	private static short frame = -1;
	private Click closeButton;
	private Click next_button;
	public Startup(Screen screen){
		super(screen, true);
		try {
			image = (BufferedImage)ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon32.png"));
		}catch(Exception e) {}
		setUndecorated(true);
		setSize(800, 550);
		setLayout(null);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBackground(BASE_COLOR);
		init();
		setVisible(true);
	}

	private void init(){
		closeButton = new Click("X", ()->System.exit(0));
		closeButton.setBounds(getWidth() - 30, 0, 30, 18);
		add(closeButton);
		next_button = new Click("Next", ()->{
			if(frame != 1)
				frame++;
			else{
				try {
					new java.io.File(".firststartup").createNewFile();
				}catch(Exception e) {e.printStackTrace();}
				dispose();
			}
			repaint();
		});
		next_button.setBounds(getWidth()/2 - 30, getHeight() - 28, 60, 28);
		add(next_button);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(BASE_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, 64, 64, null);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setColor(Color.WHITE);
		g2.setFont(BASE_FONT_BIG);
		if(frame == -1)
			firstFrame(g2);
		else if(frame == 0)
			secondFrame(g2);
		else if(frame == 1)
			lastFrame(g2);
	}

	private void firstFrame(Graphics2D g){
		g.drawString("Welcome to the first startup of", 50, 100);
		g.drawString("Omega Integrated Development Environment Community Edition", 50, 150);
		g.drawString("Edition : Community", 50, 200);
		g.drawString("Type : Rolling Release", 50, 250);
		g.drawString("Owner/Author : Omega UI", 50, 300);
		g.drawString("Written in : Java", 50, 350);
		g.drawString("Runtime Needed : Java SE 11", 50, 400);
		g.drawString("Recommended Desktop Environment (For Linux) : Gnome", 50, 450);
		closeButton.repaint();
		next_button.repaint();
	}

	private void secondFrame(Graphics2D g){
		g.setFont(BASE_FONT);
		g.drawString("If you continue this means that you agree :", 50, 100);
		g.drawString("-> The Software is provided \"AS IS\" without warranty of any kind", 50, 150);
		g.drawString("-> In no event shall the author be held liable for any damages", 50, 200);
		g.drawString("      arising from the use of Omega Integrated Development Environment.", 50, 250);
		g.drawString("-> No portion of the Omega Integrated Development Environment binaries may be", 50, 300);
		g.drawString("      disassembled, reverse engineered, decompiled, modified or altered.", 50, 350);
		g.drawString("-> No person or company may distribute separate parts", 50, 400);
		g.drawString("      of Omega Integrated Development Environment.", 50, 450);
		next_button.setText("I accept");
		closeButton.repaint();
	}

	private void lastFrame(Graphics2D g){
		g.setFont(BASE_FONT);
		g.drawString("Some Key Features of Omega IDE Community Edition:", 50, 100);
		g.setFont(BASE_FONT_BIG);
		g.drawString("Omega IDE is an extremely lightweight java IDE.", 50, 150);
		g.drawString("The Content Assist is extremely fast.", 50, 200);
		g.drawString("Provides a simple Way to manage complex settings.", 50, 250);
		g.drawString("Changes theme with change in theme of the system.", 50, 300);
		g.drawString("Provides a faster way to code.", 50, 350);
		g.drawString("Totally Free.", 50, 400);
		next_button.setText("Launch");
		closeButton.repaint();
	}

	@Override
	public Component add(Component c){
		super.add(c);
		c.repaint();
		return c;
	}

	public static void checkStartup(Screen screen) {
		if(!new File(".firststartup").exists())
			new Startup(screen).repaint();
	}
}

package launcher;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ide.Screen;
import ide.utils.RecentsManager;
import ide.utils.ToolMenu;
public class Launcher extends JFrame{
	protected static final BufferedImage icon = getImage("/omega_ide_icon32.png");
	protected final JPanel panel = new JPanel(null);
	protected final JScrollPane scrollPane = new JScrollPane(panel);
	protected CloseButton c;
	private class CloseButton extends JComponent {
		private volatile boolean enter;
		protected CloseButton(){
			addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					System.exit(0);
				}
				@Override
				public void mouseEntered(MouseEvent e){
					enter = true;
					repaint();
				}
				@Override
				public void mouseExited(MouseEvent e){
					enter = false;
					repaint();
				}
			});
			setBackground(Launcher.this.getForeground());
			setForeground(Launcher.this.getBackground());
			setFont(new Font("Ubuntu Mono", Font.BOLD, 20));
		}

		@Override
		public void paint(Graphics g2){
			super.paint(g2);
			Graphics2D g = (Graphics2D)g2;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(getBackground());
			g.fill3DRect(0, 0, getWidth(), getHeight(), !enter);
			String text = "X";
			int size = g.getFontMetrics().stringWidth(text);
			g.setColor(getForeground());
			g.drawString(text, getWidth()/2 - size/2, getHeight()/2 + 5);
		}
	}
	public Launcher(){
		setUndecorated(true);
		setLayout(null);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
		setIconImage(icon);
		if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53) {
			ide.utils.UIManager.setData(this);
		}else {
			setBackground(Color.WHITE);
			setForeground(Color.decode("#4063BF"));
		}
		setFont(new Font("Ubuntu Mono", Font.BOLD, 44));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
		setVisible(true);
	}

	private void init(){
		panel.setBackground(getBackground());
		scrollPane.setBounds(0, 220, getWidth(), getHeight() - 220);
		add(scrollPane);

		c = new CloseButton();
		c.setBounds(getWidth() - 40, 0, 40, 40);
		add(c);
		
		Door openDoor = new Door("/ide/Project/Open", getImage("/project.png"), ()->{
			Screen.getFileView().open("Project");
			setVisible(false);
		});
		openDoor.setBounds(0, 0, getWidth(), 40);
		panel.add(openDoor);
		
		Door newDoor = new Door("/ide/Project/New", getImage("/project.png"), ()->{
			ToolMenu.projectWizard.setVisible(true);
			Screen.hideNotif();
		});
		newDoor.setBounds(0, 40, getWidth(), 40);
		panel.add(newDoor);

		//Creating Doors
		int y = 80;
		for(int i = RecentsManager.RECENTS.size() - 1; i >= 0; i--) {
			String path = RecentsManager.RECENTS.get(i);
			File file = new File(path);
			if(file.exists() && file.isDirectory()) {
				Door door = new Door(path, icon, ()->{
					setVisible(false);
					ide.Screen.getScreen().loadProject(file);
					ide.Screen.getScreen().setVisible(true);
				});
				door.setBounds(0, y, getWidth(), 40);
				panel.add(door);
				y += 40;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), y));
	}

	@Override
	public void paint(Graphics g2){
		super.paint(g2);
		Graphics2D g = (Graphics2D)g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(icon, getWidth()/2 - 128/2, 10, 128, 128, this);
		String text = "Omega IDE Community Edition";
		int size = g.getFontMetrics().stringWidth(text);
		g.setColor(getForeground());
		g.drawString(text, getWidth()/2 - size/2, 180);
		g.fillRect(getWidth()/2 - size/2, 180 + 10, size, 2);
		scrollPane.repaint();
		c.repaint();
	}

	public static BufferedImage getImage(String path){
		return (BufferedImage)tabPane.IconManager.getImageIcon(path).getImage();
	}
}

package launcher;
import java.io.File;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
public class Door extends JComponent{
	protected String path;
	protected String name;
	protected String parent;
	protected BufferedImage image;
	private volatile boolean enter;
	public Door(String path, BufferedImage image, Runnable r){
		super();
		this.image = image;
		this.path = path;
		this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		this.parent = path.substring(0, path.lastIndexOf(File.separatorChar));
		this.parent = "<"+this.parent.substring(parent.lastIndexOf(File.separatorChar) + 1)+">";
		if(ide.utils.UIManager.isDarkMode()) {
			ide.utils.UIManager.setData(this);
		}else {
			setBackground(Color.WHITE);
			setForeground(Color.decode("#4063BF"));
		}
		setFont(new Font("Ubuntu Mono", Font.BOLD, 16));
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				r.run();
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
	}
	
	public String getPath() {
		return path;
	}
	
	public void set(boolean enter) {
		this.enter = enter;
		repaint();
	}

	public void paint(Graphics g2){
		super.paint(g2);
		Graphics2D g = (Graphics2D)g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fill3DRect(0, 0, getWidth(), getHeight(), !enter);
		g.setColor(getForeground());
		g.drawImage(image, 0, getHeight()/2 - 16, 32, 32, this);
		g.drawString(name, 40, 12);
		g.drawString(parent, 40, 27);
	}
}

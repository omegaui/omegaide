package plugin;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import plugin.PluginManager.Plug;
public class Door extends JComponent{
	protected String path;
	protected String name;
	protected String parent;
	protected BufferedImage image;
     protected Plug plug;
	private volatile boolean enter;
     private static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 16);
     //Colors
     private static final Color lightG = new Color(10, 200, 10, 100);
     private static final Color darkG = new Color(10, 200, 10, 200);
     private static final Color lightR = new Color(200, 10, 10, 100);
     private static final Color darkR = new Color(200, 10, 10, 200);
     public Door(Plug plug, String path, BufferedImage image, Runnable r){
		super();
          this.plug = plug;
		this.image = image;
		this.path = path;
		this.name = path.substring(path.lastIndexOf('/') + 1);
		this.parent = path.substring(0, path.lastIndexOf('/'));
		this.parent = "<"+this.parent.substring(parent.lastIndexOf('/') + 1)+">";
		if(!ide.utils.UIManager.isDarkMode()) {
			setBackground(Color.WHITE);
			setForeground(Color.decode("#4063BF"));
		}
		else ide.utils.UIManager.setData(this);
		setFont(FONT);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
                    if(plugin.PluginView.plugin.getName().equals(plug.name)) {
                         plug.enabled = !plug.enabled;
                         if(plug.enabled) {
                        	     ide.Screen.getPluginManager().initPlug(plug.name);
                         }
                         else
                        	 plugin.PluginView.plugin.disable();
                    }
                    r.run();
                    repaint();
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
          if(plug.enabled){
               g.setColor(lightG);
               g.fillRect(getWidth() - 30, getHeight()/2 - 10, 20, 20);
               g.setColor(darkG);
               g.fillRect(getWidth() - 20, getHeight()/2 - 15, 20, 30);
               if(enter){
                    g.drawString("<", getWidth() - 40, getHeight()/2 + 6);
               }
          }
          else{
               g.setColor(lightR);
               g.fillRect(getWidth() - 40, getHeight()/2 - 10, 20, 20);
               g.setColor(darkR); 
               g.fillRect(getWidth() - 20, getHeight()/2 - 15, 20, 30);
               if(enter){
                    g.drawString(">", getWidth() - 48, getHeight()/2 + 6);
               }
          }
	}
}

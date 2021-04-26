package omega.plugin;
import java.io.*;
import java.util.*;
import omega.*;
import omega.utils.*;
import omega.comp.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class Installer extends JDialog {
	private Updater updater;
	
	private TextComp msgComp;
	private TextComp headerComp;
	private TextComp imageComp;
	private String versionInfo;
     private String size;
	private BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
	public Installer(PluginCenter pluginCenter){
		super(pluginCenter, false);
		setTitle("Omega IDE -- Installer");
		setUndecorated(true);
		setLayout(null);
		setBackground(c2);
		setSize(400, 460);
		setLocationRelativeTo(null);
		updater = new Updater(pluginCenter);
		paintImage();
		init();
	}
	public void init(){
		
		headerComp = new TextComp("", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		headerComp.setBounds(0, 0, getWidth(), 30);
		headerComp.setFont(PX14);
		headerComp.setArc(0, 0);
		headerComp.setClickable(false);
		add(headerComp);
		
		msgComp = new TextComp("", TOOLMENU_COLOR4_SHADE, c2, TOOLMENU_COLOR2, ()->{
			setVisible(false);
		});
		msgComp.setBounds(0, 30, getWidth(), 30);
		msgComp.setFont(PX14);
		msgComp.setArc(0, 0);
		msgComp.setClickable(false);
		add(msgComp);
		imageComp = new TextComp("", c2, c2, c2, null){
			@Override
			public void draw(Graphics2D g){
				g.drawImage(image, 0, 0, this);
			}
		};
		imageComp.setBounds(0, 60, getWidth(), 400);
		imageComp.setArc(0, 0);
		imageComp.setClickable(false);
		add(imageComp);
	}
	public void paintImage(){
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//Drawing Levels
		int x = getWidth()/2 - 40;
		int x2 = x + 80;
		int y = 100;
		for(int i = 0; i < 8; i++){
			drawBox(g, x, y, 80, 30);
			y += 30;
		}
		x = getWidth()/2 - 80;
		drawBox(g, x, y, 160, 30);
		for(int i = 0; i < 3; i++){
			x -= 30;
			y -= 30;
			drawBox(g, x, y, 70, 30);
			drawBox(g, x2, y, 70, 30);
			x2 += 30;
		}
		g.dispose();
	}
	public void checkForUpdates(){
		setVisible(true);
		new Thread(()->{
			setHeader("Checking for Updates");
			notify("Reading Release File");
			try{
				Scanner reader = new Scanner(Download.openStream("https://raw.githubusercontent.com/omegaui/omegaide/main/.release"));
				String versionInfo = reader.nextLine();
				double remoteVersion = Double.parseDouble(versionInfo.substring(1));
				double currentVersion = Double.parseDouble(Screen.VERSION.substring(1));
				if(currentVersion < remoteVersion){
					this.versionInfo = remoteVersion + "";
					this.size = reader.nextLine();
					String title = reader.nextLine();
					LinkedList<String> changes = new LinkedList<>();
					while(reader.hasNextLine())
						changes.add(reader.nextLine());
					reader.close();
					updater.genView(title, size, changes, this::update);
				}
				else {
					reader.close();
					setHeader("No Updates Required!");
                         notify("Click to Close");
					enableClose();
				}
			}
			catch(Exception e){
				notify("An Error Occurred");
			}
		}).start();
	}
	public void update(){
          new Thread(()->{
     		updater.setVisible(false);
     		setHeader("Updating to version " + versionInfo);
     		notify("Pulling Java Archive");
     		disableClose();
     		try{
                    BufferedInputStream in = new BufferedInputStream(Download.openStream("https://raw.githubusercontent.com/omegaui/omegaide/main/out/Omega%20IDE.jar"));
                    File file = new File("Omega IDE.jar");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    double length = Double.parseDouble(size.substring(0, size.indexOf(' ')));
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                         fileOutputStream.write(dataBuffer, 0, bytesRead);
                         fileOutputStream.flush();
                         try{
                              double currentLength = file.length() / 1000000;
                              int percentage = (int)((currentLength * 100) / length);
                              notify("Pulling Java Archive " + percentage + "%");
                         }
                         catch(Exception e){
                              e.printStackTrace();
                         }
                    }
                    in.close();
                    fileOutputStream.close();
                    
                    String osName = System.getProperty("os.name");
     			if(osName.contains("inux")){
     				setHeader("Downloaded Omega IDE.jar");
                         enableClose();
                         notify("Click to Close");
                         ChoiceDialog.makeChoice("Move ~/Omega IDE.jar to /usr/bin", "Ok", "Don't Update");
     			}
                    else {
                         setHeader("Updated to version " + versionInfo);
                         enableClose();
                         notify("Click to Close");
                    }
     		}
     		catch(Exception e){
     			setHeader("Falied to Update!");
                    enableClose();
                    notify("Click to Close");
     			e.printStackTrace();
     		}
          }).start();
	}
	public void notify(String msg){
		msgComp.setText(msg);
	}
	public void setHeader(String header){
		headerComp.setText(header);
	}
	public void enableClose(){
		msgComp.setClickable(true);
	}
	public void disableClose(){
		msgComp.setClickable(false);
	}
	public void drawBox(Graphics g, int x, int y, int w, int h){
		g.setColor(TOOLMENU_COLOR3);
		g.fillRect(x, y, w, h);
		g.setColor(TOOLMENU_COLOR4);
		g.drawRect(x, y, w - 1, h - 1);
	}
}

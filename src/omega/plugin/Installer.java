/**
  * Installs Update
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

package omega.plugin;
import omega.Screen;

import java.awt.Graphics;

import omega.utils.ChoiceDialog;
import omega.utils.IconManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.util.LinkedList;
import java.util.Scanner;

import java.awt.image.BufferedImage;

import omega.comp.TextComp;

import javax.swing.JDialog;

import static omega.utils.UIManager.*;
public class Installer extends JDialog {
	private Updater updater;
	
	private TextComp msgComp;
	private TextComp headerComp;
	private TextComp imageComp;
	private String versionInfo;
     private String size;
	private BufferedImage image;
	public Installer(PluginCenter pluginCenter){
		super(pluginCenter, false);
		setTitle("Omega IDE -- Installer");
		setUndecorated(true);
		setLayout(null);
		setBackground(c2);
		setSize(400, 460);
		setLocationRelativeTo(null);
		updater = new Updater(pluginCenter);
		image = IconManager.fluentupdateImage;
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
		imageComp = new TextComp(image, 50, 50, c2, c2, c2, null);
		imageComp.setBounds(0, 60, getWidth(), 400);
		imageComp.setArc(0, 0);
		imageComp.setClickable(false);
		add(imageComp);
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
                    setHeader("Network Error Occured");
				notify("Click to Close");
                    enableClose();
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


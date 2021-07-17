/**
  * PluginComponent
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
import java.util.*;
import omega.utils.*;
import omega.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import omega.comp.*;
import java.awt.image.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import omega.plugin.PluginManager.Plug;
public class PluginComp extends JComponent{
	private BufferedImage image;
	private PluginCenter pluginCenter;
	private Plugin plugin;
	private Plug plug;
	private PlugInfo plugInfo;
	private TextComp imageComp;
	private TextComp nameComp;
	private TextComp versionComp;
	private TextComp sizeComp;
	private TextComp authorComp;
	private TextComp installComp;
	private TextComp enableComp;
	private boolean inStore;
	private static BufferedImage IDE_IMAGE = null;
	static{
		try{
			String ext = isDarkMode() ? "_dark.png" : ".png";
			IDE_IMAGE = ImageIO.read(PluginComp.class.getResourceAsStream("/omega_ide_icon64" + ext));
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public PluginComp(PluginCenter pluginCenter, BufferedImage image, Plugin plugin, Plug plug){
		this.pluginCenter = pluginCenter;
		this.plug = plug;
		this.image = (image == null) ? IDE_IMAGE : image;
		this.plugin = plugin;
		this.inStore = false;
		setLayout(null);
		initPlugin();
	}
	
	public PluginComp(PluginCenter pluginCenter, PlugInfo plugInfo){
		this.pluginCenter = pluginCenter;
		this.plugInfo = plugInfo;
		this.image = IDE_IMAGE;
		this.inStore = true;
		setLayout(null);
		initPlugInfo();
	}
	public void initPlugin(){
		imageComp = new TextComp("", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null){
			@Override
			public void draw(Graphics2D g){
				if(image != null){
					g.setColor(color2);
                         if(image.getWidth() < 90){
     					g.fillRoundRect(100/2 - image.getWidth()/2, 100/2 - image.getHeight()/2, image.getWidth(), image.getHeight(), 40, 40);
     					g.drawImage(image, 100/2 - image.getWidth()/2, 100/2 - image.getHeight()/2, image.getWidth(), image.getHeight(), null);
                         }
                         else {
                              g.fillRoundRect(100/2 - 64/2, 100/2 - 64/2, 64, 64, 40, 40);
                              g.drawImage(image, 100/2 - 64/2, 100/2 - 64/2, 64, 64, null);
                         }
				}
			}
		};
		imageComp.setClickable(false);
		imageComp.setBounds(0, 0, 100, 100);
		add(imageComp);
		nameComp = new TextComp(plugin.getName(), plugin.getDescription(), TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		nameComp.setBounds(110, 10, 200, 30);
		nameComp.setFont(PX14);
		nameComp.setClickable(false);
		add(nameComp);
		versionComp = new TextComp(plugin.getVersion(), TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		versionComp.setBounds(320, 10, 50, 30);
		versionComp.setFont(PX14);
		versionComp.setClickable(false);
		add(versionComp);
		sizeComp = new TextComp(switch(plugin.getSize()){
			case "-1" -> "Needs Restart : " + plugin.needsRestart();
			default -> plugin.getSize();
		}, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		sizeComp.setBounds(380, 10, switch(plugin.getSize()){
			case "-1" -> 150;
			default -> 80;
		}, 30);
		sizeComp.setFont(PX14);
		sizeComp.setClickable(false);
		add(sizeComp);
		authorComp = new TextComp("Author : " + plugin.getAuthor(), plugin.getCopyright(), TOOLMENU_COLOR4_SHADE, c2, TOOLMENU_COLOR4, null);
		authorComp.setBounds(110, 50, 150, 30);
		authorComp.setFont(PX14);
		authorComp.setClickable(false);
		add(authorComp);
		installComp = new TextComp(inStore ? "Install" : "Uninstall", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			String fileName = plug.fileName;
			int res = ChoiceDialog.makeChoice("Do you want to uninstall " + plugin.getName() + "? This Operation requires IDE restart!", "Yes", "No");
			if(res == ChoiceDialog.CHOICE1)
				new File("omega-ide-plugins" + File.separator + fileName).delete();
		});
		installComp.setBounds(780 - 110, 100/2 - 15, 100, 30);
		installComp.setFont(PX14);
		add(installComp);
		enableComp = new TextComp(plug.enabled ? "Disable" : "Enable", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			plug.enabled = !plug.enabled;
			if(plug.enabled){
				if(!plugin.needsRestart()){
					plugin.init();
					plugin.enable();
					Screen.getPluginManager().addInitPlug(plug);
				}
				Screen.getPluginManager().setPlug(plug.name, plug.enabled);
			}
			enableComp.setText(plug.enabled ? "Disable" : "Enable");
		});
		enableComp.setBounds(780 - 220, 100/2 - 15, 100, 30);
		enableComp.setFont(PX14);
		enableComp.setVisible(!inStore);
		enableComp.setClickable(!inStore);
		add(enableComp);
	}
	public void initPlugInfo(){
		imageComp = new TextComp("", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null){
			@Override
			public void draw(Graphics2D g){
				if(image != null){
					g.setColor(color2);
					g.fillRoundRect(100/2 - image.getWidth()/2, 100/2 - image.getHeight()/2, image.getWidth(), image.getHeight(), 40, 40);
					g.drawImage(image, 100/2 - image.getWidth()/2, 100/2 - image.getHeight()/2, image.getWidth(), image.getHeight(), null);
				}
			}
		};
		imageComp.setClickable(false);
		imageComp.setBounds(0, 0, 100, 100);
		add(imageComp);
		nameComp = new TextComp(plugInfo.name, plugInfo.desc, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		nameComp.setBounds(110, 10, 200, 30);
		nameComp.setFont(PX14);
		nameComp.setClickable(false);
		add(nameComp);
		versionComp = new TextComp(plugInfo.version, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		versionComp.setBounds(320, 10, 50, 30);
		versionComp.setFont(PX14);
		versionComp.setClickable(false);
		add(versionComp);
		sizeComp = new TextComp(plugInfo.size, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		sizeComp.setBounds(380, 10, 80, 30);
		sizeComp.setFont(PX14);
		sizeComp.setClickable(false);
		sizeComp.setVisible(!plugInfo.size.equals("-1"));
		add(sizeComp);
		authorComp = new TextComp("Author : " + plugInfo.author, plugInfo.copyright, TOOLMENU_COLOR4_SHADE, c2, TOOLMENU_COLOR4, null);
		authorComp.setBounds(110, 50, 150, 30);
		authorComp.setFont(PX14);
		authorComp.setClickable(false);
		add(authorComp);
		
		installComp = new TextComp(inStore ? "Install" : "Uninstall", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			int res = ChoiceDialog.makeChoice("Do you want to install " + plugInfo.name + "? This Operation requires IDE restart!", "Yes", "No");
			if(res == ChoiceDialog.CHOICE1){
				new Thread(()->{
					try{
						pluginCenter.notify("Downloading Plugin ... ");
						BufferedInputStream in = new BufferedInputStream(Download.openStream("https://raw.githubusercontent.com/omegaui/omegaide-plugins/main/" + plugInfo.fileName));
						File file = new File("omega-ide-plugins" + File.separator + plugInfo.fileName);
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						byte dataBuffer[] = new byte[1024];
						int bytesRead;
						while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
							fileOutputStream.write(dataBuffer, 0, bytesRead);
							fileOutputStream.flush();
							try{
								double currentLength = file.length() / 1000;
								double length = Double.parseDouble(plugInfo.size.substring(0, plugInfo.size.indexOf(' ')));
								if(plugInfo.size.endsWith("MB"))
									currentLength /= 1000;
                                        int percentage = (int)((currentLength * 100) / length);
                                        pluginCenter.notify("Downloading Plugin " + percentage + "%");
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						in.close();
						fileOutputStream.close();
						
						pluginCenter.notify("Done! You should restart the IDE Now!");
					}
					catch(Exception e){
						pluginCenter.notify("Unable to Download Plugin!");
					}
				}).start();
			}
		});
		installComp.setBounds(780 - 110, 100/2 - 15, 100, 30);
		installComp.setFont(PX14);
		add(installComp);
		
		if(!inStore) {
			enableComp = new TextComp(plug.enabled ? "Disable" : "Enable", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
				plug.enabled = !plug.enabled;
				if(plug.enabled){
					if(!plugin.needsRestart()){
						plugin.init();
						plugin.enable();
						Screen.getPluginManager().addInitPlug(plug);
					}
					Screen.getPluginManager().setPlug(plug.name, plug.enabled);
				}
				enableComp.setText(plug.enabled ? "Disable" : "Enable");
			});
			enableComp.setBounds(780 - 220, 100/2 - 15, 100, 30);
			enableComp.setFont(PX14);
			enableComp.setVisible(!inStore);
			enableComp.setClickable(!inStore);
			add(enableComp);
		}
	}
	@Override
	public String toString(){
		if(plugin == null)
			return plugInfo.name + " -- " + plugInfo.desc;
		else
			return plugin.getName() + " -- " + plugin.getDescription();
	}
}


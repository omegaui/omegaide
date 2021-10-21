/**
  * RemotePluginView
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

package omega.plugin.store;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.util.LinkedList;

import java.net.URL;

import omega.plugin.PluginCategory;

import omega.utils.IconManager;

import omega.comp.TextComp;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class RemotePluginView extends JDialog{
	public PluginStore pluginStore;
	public RemotePluginInfo remotePluginInfo;
	public RemotePluginComp remotePluginComp;
	
	public TextComp titleComp;
	public TextComp closeComp;
	public TextComp iconComp;
	public TextComp versionComp;
	public TextComp authorComp;
	public TextComp descriptionComp;
	public TextComp licenseComp;

	public TextComp leftComp;
	public TextComp rightComp;
	public TextComp screenshotComp;

	public int pointer;
	public LinkedList<BufferedImage> screenshots;
	
	public RemotePluginView(RemotePluginComp remotePluginComp){
		super(remotePluginComp.pluginStore, false);
		this.remotePluginComp = remotePluginComp;
		this.pluginStore = remotePluginComp.pluginStore;
		this.remotePluginInfo = remotePluginComp.remotePluginInfo;
		setUndecorated(true);
		JPanel panel = new JPanel(null);
		panel.setBackground(back1);
		setContentPane(panel);
		setLayout(null);
		setSize(550, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		init();
	}

	public void init(){
		iconComp = new TextComp(remotePluginComp.iconComp.image, 30, 30, c2, c2, c2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setArc(0, 0);
		iconComp.setClickable(false);
		add(iconComp);
		
		titleComp = new TextComp(remotePluginInfo.name, back2, back2, glow, null);
		titleComp.setBounds(30, 0, getWidth() - 60, 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);

		closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, back2, c2, TOOLMENU_COLOR2_SHADE, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);

		versionComp = new TextComp("Version : " + remotePluginInfo.version, back1, back1, TOOLMENU_COLOR1, null);
		versionComp.setBounds(5, 40, getWidth() - 10, 25);
		versionComp.setFont(PX14);
		versionComp.setArc(0, 0);
		versionComp.setClickable(false);
		versionComp.alignX = 5;
		versionComp.setPaintTextGradientEnabled(true);
		versionComp.setGradientColor(remotePluginComp.nameComp.color3);
		add(versionComp);

		authorComp = new TextComp("Developed By " + remotePluginInfo.author, back1, back1, TOOLMENU_COLOR3, null);
		authorComp.setBounds(5, 70, getWidth() - 10, 25);
		authorComp.setArc(0, 0);
		authorComp.setFont(PX14);
		authorComp.setClickable(false);
		authorComp.alignX = 5;
		add(authorComp);

		licenseComp = new TextComp("Licensed Under " + remotePluginInfo.license, back1, back1, TOOLMENU_COLOR1, null);
		licenseComp.setBounds(5, 100, getWidth() - 10, 25);
		licenseComp.setFont(PX14);
		licenseComp.setArc(0, 0);
		licenseComp.setClickable(false);
		licenseComp.alignX = 5;
		licenseComp.setPaintTextGradientEnabled(true);
		licenseComp.setGradientColor(TOOLMENU_COLOR4);
		add(licenseComp);

		descriptionComp = new TextComp(remotePluginInfo.description, c2, c2, TOOLMENU_COLOR3, null);
		descriptionComp.setBounds(5, 130, getWidth() - 10, 25);
		descriptionComp.setFont(PX14);
		descriptionComp.setArc(0, 0);
		descriptionComp.setClickable(false);
		add(descriptionComp);

		leftComp = new TextComp("<", TOOLMENU_COLOR2, TOOLMENU_COLOR3, c2, this::moveLeft);
		leftComp.setBounds(2, 160 + (getHeight() - 160)/2 - 30/2, 30, 30);
		leftComp.setFont(PX22);
		leftComp.setArc(2, 2);
		add(leftComp);

		rightComp = new TextComp(">", TOOLMENU_COLOR2, TOOLMENU_COLOR3, c2, this::moveRight);
		rightComp.setBounds(getWidth() - 2 - 30, 160 + (getHeight() - 160)/2 - 30/2, 30, 30);
		rightComp.setFont(PX22);
		rightComp.setArc(2, 2);
		add(rightComp);

		screenshotComp = new TextComp(remotePluginInfo.screenshotsURLs.isEmpty() ? "No Screenshots Available" : "", c2, c2, TOOLMENU_COLOR5, null);
		screenshotComp.setBounds(35, 160 + (getHeight() - 160)/2 - (getHeight() - 200)/2, getWidth() - 70, getHeight() - 200);
		screenshotComp.setFont(PX14);
		screenshotComp.setArc(0, 0);
		screenshotComp.setClickable(false);
		screenshotComp.w = screenshotComp.getWidth();
		screenshotComp.h = screenshotComp.getHeight();
		add(screenshotComp);
	}

	public void moveLeft(){
		if(pointer - 1 >= 0)
			pointer--;
		screenshotComp.image = screenshots.get(pointer);
		screenshotComp.repaint();
	}

	public void moveRight(){
		if(pointer + 1 < screenshots.size())
			pointer++;
		screenshotComp.image = screenshots.get(pointer);
		screenshotComp.repaint();
	}

	public void doPostInit(){
		if(remotePluginInfo.screenshotsURLs.isEmpty())
			return;
		if(screenshots != null)
			return;
		screenshots = new LinkedList<>();
		new Thread(()->{
			try{
				for(int i = 0; i < remotePluginInfo.screenshotsURLs.size(); i++){
					descriptionComp.setText("Fetching Screenshots ... " + (i + 1) + " of " + remotePluginInfo.screenshotsURLs.size());
					screenshots.add(ImageIO.read(remotePluginInfo.screenshotsURLs.get(i)));
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				descriptionComp.setText(remotePluginInfo.description);
			}
			if(!screenshots.isEmpty()){
				pointer = 0;
				screenshotComp.image = screenshots.get(pointer);
				screenshotComp.repaint();
			}
		}).start();
	}

	@Override
	public void setVisible(boolean value){
	     super.setVisible(value);
		if(value){
	          doPostInit();
		}
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}
}

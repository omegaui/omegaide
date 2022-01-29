/**
  * PluginStore
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
import omega.io.IconManager;

import omega.ui.dialog.ChoiceDialog;

import omega.ui.popup.NotificationPopup;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;
import omegaui.component.NoCaretField;

import omega.Screen;

import omega.plugin.management.PluginManager;

import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.LinkedList;

import java.awt.geom.RoundRectangle2D;

import omega.plugin.PluginCategory;
import omega.plugin.Downloader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDialog;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class PluginStore extends JDialog{
	public PluginManager pluginManager;
	public PluginCategory pluginCategory;
	
	public TextComp iconComp;
	public TextComp titleComp;
	public TextComp closeComp;

	public TextComp statusComp;

	public RemotePluginInfoLoader remotePluginInfoLoader;

	public FlexPanel contentPanel;
	public JScrollPane contentScrollPane;
	public JPanel panel;

	public NoCaretField searchField;
	public TextComp categoryComp;

	public volatile boolean loaded = false;

	public String currentCategory = PluginCategory.ANY_CATEGORY;

	public LinkedList<RemotePluginComp> remotePluginComps = new LinkedList<>();

	public static NotificationPopup restartPopup =  NotificationPopup.create(Screen.getScreen())
													.size(500, 120)
													.title("Plugin Management", TOOLMENU_COLOR4)
													.message("A Restart is Required to Add the Newly Installed Plugin!", TOOLMENU_COLOR2)
													.shortMessage("OR You can Continue Downloading Some More!", TOOLMENU_COLOR1)
													.dialogIcon(IconManager.ideImage64)
													.build()
													.locateOnBottomLeft();

	public PluginStore(Screen screen, PluginManager pluginManager){
		super(screen, false);
		this.pluginManager = pluginManager;
		setTitle("Plugin Store");
		setUndecorated(true);
		setSize(550, 450);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(back1);
		setContentPane(panel);
		setLayout(null);
		setResizable(false);
		init();
	}

	public void init(){
		pluginCategory = new PluginCategory(this);
		
		iconComp = new TextComp(IconManager.ideImage64, 25, 25, back2, back2, back2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		add(iconComp);
		
		titleComp = new TextComp("Plugin Store", back2, back2, glow, null);
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

		statusComp = new TextComp("", back1, back1, glow, null);
		statusComp.setBounds(0, getHeight() - 25, getWidth(), 25);
		statusComp.setFont(PX14);
		statusComp.setClickable(false);
		statusComp.setArc(0, 0);
		statusComp.alignX = 10;
		setStatus(null);
		add(statusComp);

		contentPanel = new FlexPanel(null, back3, null);
		contentPanel.setBounds(5, 60, getWidth() - 10, getHeight() - 100);
		contentPanel.setArc(10, 10);
		add(contentPanel);

		contentScrollPane = new JScrollPane(panel = new JPanel(null){
			String hint = "No Plugins Found!";
			@Override
			public void paint(Graphics graphics){
				if(!remotePluginComps.isEmpty()){
					super.paint(graphics);
					return;
				}
				Graphics2D g = (Graphics2D)graphics;
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				if(!loaded){
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());
					g.drawImage(IconManager.fluentloadinginfinityGif, getWidth()/2 - 43/2, getHeight()/2 - 43/2, 43, 43, this);
				}
				else if(remotePluginComps.isEmpty()){
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setFont(PX14);
					g.setColor(TOOLMENU_COLOR1);
					g.drawString(hint, getWidth()/2 - g.getFontMetrics().stringWidth(hint)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		});
		contentScrollPane.setBounds(5, 5, contentPanel.getWidth() - 10, contentPanel.getHeight() - 10);
		contentScrollPane.setBackground(back1);
		contentScrollPane.setBorder(null);
		panel.setBackground(back2);
		contentPanel.add(contentScrollPane);

		searchField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		searchField.setBounds(5, 30, getWidth() - 160, 25);
		searchField.setFont(PX14);
		searchField.setOnAction(()->genView(currentCategory, searchField.getText()));
		add(searchField);

		categoryComp = new TextComp("All", "Click to Select Category!", TOOLMENU_COLOR5_SHADE, back1, glow, ()->{
			String category = pluginCategory.makeChoice(currentCategory);
			if(category != null){
				currentCategory = category;
				genView(currentCategory, "");
				searchField.setText("");
				
				categoryComp.setGradientColor(PluginCategory.getSuitableColor(currentCategory));
				
				if(currentCategory.equals(PluginCategory.ANY_CATEGORY))
					categoryComp.setText("All");
				else
					categoryComp.setText(currentCategory.toUpperCase());
			}
		});
		categoryComp.setBounds(searchField.getX() + searchField.getWidth(), 30, 150, 25);
		categoryComp.setFont(PX14);
		categoryComp.setArc(0, 0);
		categoryComp.setPaintTextGradientEnabled(true);
		categoryComp.setGradientColor(PluginCategory.getSuitableColor(PluginCategory.ANY_CATEGORY));
		add(categoryComp);
	}

	public void genView(String category, String text){
		remotePluginComps.forEach(panel::remove);
		remotePluginComps.clear();
		
		int block = 0;
		for(RemotePluginInfo info : remotePluginInfoLoader.remotePluginInfos){
			if(!category.equals(PluginCategory.ANY_CATEGORY) && !info.category.equalsIgnoreCase(category))
				continue;
			if(!info.name.contains(text) && !info.description.contains(text))
				continue;
			
			RemotePluginComp comp = new RemotePluginComp(this, info, contentScrollPane.getWidth(), 60);
			comp.setLocation(0, block);
			panel.add(comp);
			remotePluginComps.add(comp);

			block += 60;
		}
		
		panel.setPreferredSize(new Dimension(contentScrollPane.getWidth(), block));
		contentScrollPane.getVerticalScrollBar().setVisible(true);
		contentScrollPane.getVerticalScrollBar().setValue(0);
		layout();
		repaint();
		
		new Thread(()->{
			setStatus("Loading Plugin Icons ...");
			remotePluginComps.forEach(comp->{
				comp.loadIcon();
			});
			setStatus(remotePluginInfoLoader.remotePluginInfos.size() + " Plugin(s) Available in the Store!");
		}).start();
	}

	public void doPostInit(){
		if(remotePluginInfoLoader != null){
			loaded = true;
			return;
		}
		loaded = false;
		remotePluginInfoLoader = new RemotePluginInfoLoader(this);
		new Thread(()->{
			remotePluginInfoLoader.loadRemotePluginInfos();
			loaded = true;
			if(!remotePluginInfoLoader.remotePluginInfos.isEmpty()){
				genView(PluginCategory.ANY_CATEGORY, "");
			}
		}).start();
	}

	public void refresh(){
		genView(currentCategory, searchField.getText());
	}

	public synchronized void downloadPlugin(RemotePluginInfo info){
		int choice = ChoiceDialog.makeChoice("Do You Want to Download This Plugin?", "Yes", "No");
		if(choice != ChoiceDialog.CHOICE1)
			return;
		
		new Thread(()->{
			try{
				setStatus("Downloading " + info.name + " ... ");
				BufferedInputStream in = new BufferedInputStream(Downloader.openStream(info.pluginFileURL.toString()));
				File localPluginFile = new File(PluginManager.PLUGINS_DIRECTORY.getAbsolutePath(), info.fileName);
				FileOutputStream out = new FileOutputStream(localPluginFile);
				
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					out.write(dataBuffer, 0, bytesRead);
					out.flush();
					
					double currentLength = localPluginFile.length() / 1000;
					double length = Double.parseDouble(info.size.substring(0, info.size.indexOf("MB")).trim()) * 1000;
	                    int percentage = (int)((currentLength * 100) / length);
	                    setStatus("Downloading " + info.name + " " + percentage + "%");
				}
				in.close();
				out.close();
				restartPopup.locateOnBottomLeft().showIt();
				refresh();
			}
			catch(Exception e){
				setStatus("Unable to Download " + info.name + "!");
				e.printStackTrace();
			}
		}).start();
	}

	public synchronized void setStatus(String text){
		if(text == null)
			statusComp.setText("Copyright 2021 Omega UI. All Right Reserved.");
		else
			statusComp.setText(text);
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}

	@Override
	public void setVisible(boolean value){
	     super.setVisible(value);
		if(value){
	          doPostInit();
		}
	}
}

/**
  * LocalPluginComp
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

package omega.plugin.management;
import java.awt.image.BufferedImage;

import java.util.LinkedList;

import omega.plugin.PluginCategory;
import omega.plugin.Plugin;

import java.awt.Graphics2D;
import java.awt.GradientPaint;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URL;

import omega.utils.IconManager;

import javax.imageio.ImageIO;

import omega.comp.FlexPanel;
import omega.comp.TextComp;

import javax.swing.JComponent;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class LocalPluginComp extends FlexPanel{

	public PluginsView pluginsView;
	
	public Plugin plugin;

	public TextComp iconComp;
	public TextComp nameComp;
	public TextComp versionComp;
	public TextComp enableComp;
	
	public volatile boolean enter = false;

	public int pointer;
	public LinkedList<BufferedImage> screenshots;
	
	public LocalPluginComp(PluginsView pluginsView, Plugin plugin, int width, int height){
		super(null, c2, null);
		setArc(0, 0);
		setBorderColor(glow);
		this.pluginsView = pluginsView;
		this.plugin = plugin;
		setSize(width, height);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				setEnter(true);
			}
			@Override
			public void mouseExited(MouseEvent e){
				setEnter(false);
			}
			@Override
			public void mousePressed(MouseEvent e){
				pluginsView.genView(LocalPluginComp.this);
			}
		});
		init();
	}

	public void init(){
		iconComp = new TextComp(IconManager.ideImage64, getHeight(), getHeight(), c2, c2, c2, null);
		iconComp.setBounds(0, 0, getHeight(), getHeight());
		iconComp.setArc(0, 0);
		iconComp.setClickable(false);
		iconComp.addMouseListener(getMouseListeners()[0]);
		add(iconComp);

		nameComp = new TextComp(plugin.getName(), c2, c2, PluginCategory.getSuitableColor(plugin.getPluginCategory()), null);
		nameComp.setBounds(getHeight(), 2, getWidth() - getHeight() - 4, 25);
		nameComp.setFont(PX14);
		nameComp.setClickable(false);
		nameComp.setPaintTextGradientEnabled(true);
		nameComp.setGradientColor(TOOLMENU_COLOR5);
		nameComp.setArc(0, 0);
		nameComp.alignX = 5;
		nameComp.addMouseListener(getMouseListeners()[0]);
		add(nameComp);

		versionComp = new TextComp(plugin.getVersion(), c2, c2, TOOLMENU_COLOR5, null);
		versionComp.setBounds(getHeight(), 30, 90, 25);
		versionComp.setPaintTextGradientEnabled(true);
		versionComp.setGradientColor(nameComp.color3);
		versionComp.setArc(10, 10);
		versionComp.setClickable(false);
		versionComp.alignX = 5;
		versionComp.setFont(UBUNTU_PX12);
		versionComp.addMouseListener(getMouseListeners()[0]);
		add(versionComp);

		enableComp = new TextComp(pluginsView.pluginManager.isPluginEnabled(plugin) ? "Disable" : "Enable", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, this::toggleEnable);
		enableComp.setBounds(getWidth() - 90, 30, 89, 25);
		enableComp.setFont(PX14);
		enableComp.setArc(2, 2);
		add(enableComp);
	}

	public void toggleEnable(){
		pluginsView.pluginManager.put(plugin, !pluginsView.pluginManager.isPluginEnabled(plugin));
		enableComp.setText(pluginsView.pluginManager.isPluginEnabled(plugin) ? "Disable" : "Enable");
		if(!pluginsView.pluginManager.isPluginEnabled(plugin) && plugin.needsRestart()){
			pluginsView.setStatus("A Restart is required for disabling " + plugin.getName() + " Entirely!");
		}
	}

	public void setEnter(boolean enter){
		this.enter = enter;
		setPaintBorder(enter);
		repaint();
	}

	public void loadIcon(){
		try{
			iconComp.image = ImageIO.read(plugin.getImage());
			iconComp.repaint();
		}
		catch(Exception e){
			System.err.println("Unable to Read Icon of Plugin \"" + plugin.getName() + "\" Dynamically!");
			e.printStackTrace();
		}
	}
}

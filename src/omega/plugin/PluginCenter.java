/**
  * The PluginCenter
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
import java.awt.geom.RoundRectangle2D;

import java.util.*;
import omega.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;
import omega.comp.*;
import java.awt.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
public class PluginCenter extends JDialog{
	private int pressX;
	private int pressY;
	private int block;
	private TextComp titleComp;
	private TextComp closeComp;
	private TextComp updateComp;
	private TextComp manageComp;
	private TextComp sep0;
	private TextComp storeComp;
	private NoCaretField searchField;
	private JPanel managePanel;
	private JPanel storePanel;
	private JScrollPane manageScrollPane;
	private JScrollPane storeScrollPane;
	private Installer installer;
	private LinkedList<PluginComp> manageItems = new LinkedList<>();
	private LinkedList<PluginComp> storeItems = new LinkedList<>();
	private short viewState = 1;
     
	
	public PluginCenter(Screen screen){
		super(screen, true);
		setTitle("Plugin Center");
		setResizable(false);
		setIconImage(screen.getIconImage());
		setUndecorated(true);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		setBackground(c2);
		
		JPanel panel = new JPanel(null);
		panel.setBackground(getBackground());
		setContentPane(panel);
		
		init();
	}
	public void init(){
		installer = new Installer(this);
		
		titleComp = new TextComp("Plugin Center", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 30, 30);
		titleComp.setFont(PX16);
		titleComp.setClickable(false);
		titleComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		titleComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
			}
		});
		titleComp.setArc(0, 0);
		add(titleComp);
		
		closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		updateComp = new TextComp("Update IDE", "Check for IDE Updates", TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR4, c2, installer::checkForUpdates);
		updateComp.setBounds(getWidth() - 100, 40, 90, 25);
		updateComp.setFont(PX14);
		add(updateComp);

		
		manageComp = new TextComp("Manage", "Manage Installed Plugins", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::setManageView){
			@Override
			public void draw(Graphics2D g){
				if(viewState == 1){
					g.setColor(color3);
					g.fillRect(10, getHeight() - 5, getWidth() - 20, 4);
					storeComp.repaint();
				}
			}
		};
		manageComp.setBounds(getWidth()/2 - 110, 40, 100, 35);
		manageComp.setFont(PX16);
		add(manageComp);
		
		sep0 = new TextComp("", TOOLMENU_COLOR3, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep0.setBounds(getWidth()/2, 40, 2, 40);
		add(sep0);
		
		storeComp = new TextComp("Store", "See Available Plugins", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::setStoreView){
			@Override
			public void draw(Graphics2D g){
				if(viewState == 0){
					g.setColor(color3);
					g.fillRect(10, getHeight() - 5, getWidth() - 20, 4);
					manageComp.repaint();
				}
			}
		};
		storeComp.setBounds(getWidth()/2 + 10, 40, 100, 35);
		storeComp.setFont(PX16);
		add(storeComp);
		
		searchField = new NoCaretField("", "search plugins here", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
          searchField.setOnAction(()->search(searchField.getText()));
		searchField.setBounds(getWidth()/2 - 200, 100, 400, 30);
		searchField.setFont(PX16);
		add(searchField);
		addKeyListener(searchField);
		
		managePanel = new JPanel(null){
			String text = "No Plugin to View Yet!";
			@Override
			public void paint(Graphics graphics){
				if(manageItems.isEmpty()){
					Graphics2D g = (Graphics2D)graphics;
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setColor(c2);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(TOOLMENU_COLOR2);
					g.setFont(PX16);
					g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
				else
					super.paint(graphics);
			}
		};
		managePanel.setBackground(c2);
		
		storePanel = new JPanel(null){
			String text = "No Plugin in Store Yet!";
			@Override
			public void paint(Graphics graphics){
				if(storeItems.isEmpty()){
					Graphics2D g = (Graphics2D)graphics;
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setColor(c2);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(TOOLMENU_COLOR2);
					g.setFont(PX16);
					g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
				else
					super.paint(graphics);
			}
		};
		storePanel.setBackground(c2);
      
		manageScrollPane = new JScrollPane(managePanel);
		manageScrollPane.setBounds(10, 150, getWidth() - 20, getHeight() - 160);
          add(manageScrollPane);
        
		storeScrollPane = new JScrollPane(storePanel);
		storeScrollPane.setBounds(10, 150, getWidth() - 20, getHeight() - 160);
          add(storeScrollPane);
	}
	public void loadManageComponents(){
		block = 10;
		PluginManager.plugins.forEach(plugin->{
			PluginComp comp = new PluginComp(PluginCenter.this, plugin.getImage(), plugin, Screen.getPluginManager().getPlug(plugin.getName()));
			comp.setBounds(0, block, manageScrollPane.getWidth(), 100);
			managePanel.add(comp);
			manageItems.add(comp);
			block += 110;
		});
		managePanel.setPreferredSize(new Dimension(manageScrollPane.getWidth(), block));
	}
	public void loadStoreComponents(){
		notify("Checking Plugin List ... ");
		LinkedList<PlugInfo> plugInfos = PluginInfoManager.read(Download.openStream("https://raw.githubusercontent.com/omegaui/omegaide-plugins/main/.plugInfos"));
		block = 10;
		notify("Plotting Components ...");
		plugInfos.forEach(plugInfo->{
			PluginComp comp = new PluginComp(PluginCenter.this, plugInfo);
			comp.setBounds(0, block, storeScrollPane.getWidth(), 100);
			storePanel.add(comp);
			storeItems.add(comp);
			block += 110;
		});
		storePanel.setPreferredSize(new Dimension(storeScrollPane.getWidth(), block));
		notify("Plugin Center");
		repaint();
	}
	public void setManageView(){
		viewState = 1;
          storeScrollPane.setVisible(false);
          manageScrollPane.setVisible(true);
		if(manageItems.isEmpty())
			loadManageComponents();
		repaint();
	}
	public void setStoreView(){
		viewState = 0;
          manageScrollPane.setVisible(false);
          storeScrollPane.setVisible(true);
		repaint();
		if(storeItems.isEmpty())
			new Thread(this::loadStoreComponents).start();
		else
			repaint();
	}
     public void search(String text){
     	if(viewState == 1){
               manageItems.forEach(managePanel::remove);
               block = 10;
               for(PluginComp comp : manageItems){
                    if(comp.toString().contains(text)){
                         comp.setBounds(0, block, manageScrollPane.getWidth(), 100);
                         managePanel.add(comp);
                         block += 110;
                    }
               }
               if(block == 10)
                    notify("No Matches Found!");
               else
                    notify("Plugin Center");
               repaint();
     	}
          else{
               storeItems.forEach(storePanel::remove);
               block = 10;
               for(PluginComp comp : storeItems){
                    if(comp.toString().contains(text)){
                         comp.setBounds(0, block, storeScrollPane.getWidth(), 100);
                         storePanel.add(comp);
                         block += 110;
                    }
               }
               if(block == 10)
                    notify("No Matches Found!");
               else
                    notify("Plugin Center");
               repaint();
          }
     }
	public void notify(String msg){
		titleComp.setText(msg);
	}
	@Override
	public void setVisible(boolean value){
		if(value)
			setManageView();
		else
			Screen.getPluginManager().save();
		super.setVisible(value);
	}
}


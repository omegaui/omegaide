/**
* Updates IDE
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

import omega.Screen;

import java.util.LinkedList;

import omega.comp.TextComp;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.utils.UIManager.*;
public class Updater extends JDialog{

	public static final short INSTALL_OPTION = 0;
	public static final short CANCEL_OPTION = 1;
	
	public short option;
	
	private TextComp titleComp;
	private TextComp changesComp;
	
	private String version;

	private int block;
	
	private LinkedList<TextComp> items = new LinkedList<>();
	private TextComp closeComp;
	private TextComp installComp;
	
	public Updater(PluginCenter pluginCenter){
		super(pluginCenter, true);
		setUndecorated(true);
		setTitle("Omega IDE -- Update Available");
		setSize(600, 500);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
		init();
	}
	
	public void init(){
		titleComp = new TextComp("Omega IDE", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		add(titleComp);
		
		changesComp = new TextComp("What's new in this release", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		changesComp.setBounds(getWidth()/2 - 200, 40, 400, 35);
		changesComp.setFont(PX16);
		changesComp.setClickable(false);
		add(changesComp);
		
		closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(0, getHeight() - 30, getWidth()/2, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		installComp = new TextComp("Install", "Click to begin Download", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR4, null);
		installComp.setBounds(getWidth()/2, getHeight() - 30, getWidth()/2, 30);
		installComp.setFont(PX14);
		installComp.setArc(0, 0);
		add(installComp);
	}
	
	public int genView(String title, String size, LinkedList<String> changes, Runnable installAction){
		option = CANCEL_OPTION;
		installComp.setRunnable(()->{
			option = INSTALL_OPTION;
			installAction.run();
		});
		
		titleComp.setText(title);
		
		items.forEach(this::remove);
		items.clear();
		
		block = 90;
		
		changes.add("Download Size : " + size);
		
		changes.forEach(change->{
			if(Screen.isNotNull(change)){
				TextComp item = new TextComp(change, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
				item.setBounds(5, block, getWidth() - 10, 30);
				item.setFont(PX14);
				item.setClickable(false);
				add(item);
				items.add(item);
				
				block += 40;
			}
		});

		if(!items.isEmpty()){
			items.getLast().setColors(c2, c2, TOOLMENU_COLOR2);
		}
		
		setVisible(true);
		return option;
	}
}


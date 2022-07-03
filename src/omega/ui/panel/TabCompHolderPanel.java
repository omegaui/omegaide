/**
* TabCompHolderPanel
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

package omega.ui.panel;
import omega.io.TabData;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class TabCompHolderPanel extends JPanel{
	
	private TabPanel tabPanel;
	
	private CardLayout cardLayout;
	
	public TabCompHolderPanel(TabPanel tabPanel){
		setLayout(cardLayout = new CardLayout());
		this.tabPanel = tabPanel;

		setBackground(c2);
	}
	
	public void putTab(TabData tabData){
		add(tabData.getUniqueName(), tabData.getTabHolder());
		showTabComponent(tabData);
	}

	public void showTabComponent(TabData tabData){
		cardLayout.show(this, tabData.getUniqueName());
	}

	public void putOffTab(TabData tabData){
		remove(tabData.getTabHolder());
	}
	
}

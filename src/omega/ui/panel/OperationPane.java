/**
 * OperationPane
 * Copyright (C) 2022 Omega UI

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
import omegaui.component.EdgeComp;
import omegaui.component.TextComp;

import omega.ui.popup.OPopupWindow;

import omega.io.TabData;
import omega.io.UIManager;

import omega.ui.listener.TabPanelListener;

import omega.Screen;

import java.awt.image.BufferedImage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JComponent;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class OperationPane extends JPanel{

	private class ActionBar extends JComponent{

		private EdgeComp labelComp;

		private TextComp lockedComp;
		
		private TextComp closeAllComp;
		
		private TextComp collapseComp;

		private TextComp hideComp;

		private int dividerY;

		private volatile boolean collapseMode = false;
		private volatile boolean locked = true;

		public ActionBar(){
			setLayout(null);
			setSize(100, 25);
			setPreferredSize(getSize());
			setBackground(c2);
			init();
		}

		public void init(){
			labelComp = new EdgeComp("Process Panel", TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, TOOLMENU_COLOR3, ()->{});
			labelComp.setBounds(1, 25/2 - 20/2, computeWidth(labelComp.getText(), PX14) + 20, 20);
			labelComp.setFont(PX14);
			labelComp.setUseFlatLineAtBack(true);
			labelComp.setEnabled(false);
			add(labelComp);
			
			lockedComp = new TextComp("", TOOLMENU_COLOR2_SHADE, TOOLMENU_GRADIENT, glow, ()->{
				locked = !locked;
				lockedComp.setText(!locked ? "Unlocked" : "Locked");
				lockedComp.setToolTipText(!locked ? "Panel can be resized!" : "Panel cannot be resized!");
				lockedComp.setColors(locked ? TOOLMENU_COLOR2_SHADE : TOOLMENU_COLOR1_SHADE, 
									locked ? TOOLMENU_COLOR2 : TOOLMENU_COLOR1, 
									Color.WHITE);
			});
			lockedComp.setFont(PX14);
			lockedComp.setArc(5, 5);
			add(lockedComp);
		
			lockedComp.setText(!locked ? "Unlocked" : "Locked");
			lockedComp.setToolTipText(!locked ? "Panel can be resized!" : "Panel cannot be resized!");
			lockedComp.setColors(locked ? TOOLMENU_COLOR2_SHADE : TOOLMENU_COLOR1_SHADE, 
								locked ? TOOLMENU_COLOR2 : TOOLMENU_COLOR1, 
								Color.WHITE);
		
			closeAllComp = new TextComp("Close All", TOOLMENU_COLOR2_SHADE, TOOLMENU_GRADIENT, glow, ()->{
				tabPane.closeAllTabs();
			});
			closeAllComp.setFont(PX14);
			closeAllComp.setArc(5, 5);
			add(closeAllComp);

			collapseComp = new TextComp("Collapse", TOOLMENU_COLOR1_SHADE, TOOLMENU_GRADIENT, glow, ()->{
				collapseMode = !collapseMode;
				collapseComp.setText(collapseMode ? "Expand" : "Collapse");
				resizeDivider();
				if(!collapseMode)
					screen.compilancePane.setDividerLocation(dividerY);
			});
			collapseComp.setFont(PX14);
			collapseComp.setArc(5, 5);
			add(collapseComp);

			hideComp = new TextComp("Hide", TOOLMENU_COLOR4_SHADE, TOOLMENU_GRADIENT, glow, this::hide);
			hideComp.setFont(PX14);
			hideComp.setArc(5, 5);
			add(hideComp);
		}

		public void resizeDivider(){
			if(collapseMode)
				screen.compilancePane.setDividerLocation(screen.compilancePane.getHeight() - getHeight() - UIManager.tabHeight - screen.compilancePane.getDividerSize());
			else if(locked && screen.compilancePane.getDividerLocation() != dividerY){
				screen.compilancePane.setDividerLocation(dividerY);
			}
		}

		public void computeDividerY(){
			dividerY = screen.getHeight() - 400;
		}

		public void hide(){
			OperationPane.this.setVisible(false);
		}

		public void computeBounds(){
			lockedComp.setBounds(getWidth() - 320 - 6, 25/2 - 20/2, 80, 20);
			closeAllComp.setBounds(getWidth() - 240 - 4, 25/2 - 20/2, 80, 20);
			collapseComp.setBounds(getWidth() - 160 - 2, 25/2 - 20/2, 80, 20);
			hideComp.setBounds(getWidth() - 80, 25/2 - 20/2, 80, 20);
			computeDividerY();
			resizeDivider();
		}
	}

	private Screen screen;

	private ActionBar actionBar;

	private static TabPanel tabPane;

	private static final String TITLE = "Process Panel";
	private static final String HINT = "There is no process running";

	public OperationPane(Screen screen) {
		this.screen = screen;
		setLayout(new BorderLayout());

		actionBar = new ActionBar();
		add(actionBar, BorderLayout.NORTH);

		tabPane = new TabPanel(TabPanel.TAB_LOCATION_TOP);

		setVisible(false);
		UIManager.setData(this);

		tabPane.addTabPanelListener(new TabPanelListener(){
			@Override
			public void tabAdded(TabData tabData){
				add(tabPane, BorderLayout.CENTER);
				setVisible(true);
			}

			@Override
			public void tabRemoved(TabData tabData){

			}

			@Override
			public void goneEmpty(TabPanel panel){
				remove(tabPane);
				setVisible(false);
			}

			@Override
			public void tabActivated(TabData tabData){

			}
		});
	}

	public void addTab(String name, BufferedImage image, JComponent c, Runnable r) {
		tabPane.addTab(name, name, "", image, c, r);
	}

	public void addTab(String name, BufferedImage image, JComponent c, Runnable r, OPopupWindow popup) {
		tabPane.addTab(name, name, "", image, c, r, popup);
	}

	public TabData getTabData(JComponent comp){
		return tabPane.getTabData(comp);
	}

	public static int count(String name) {
		int c = -1;
		for(TabData tx : tabPane.getTabs()){
			String var0 = tx.getName();
			if(var0.contains(name))
				c++;
		}
		return c;
	}

	@Override
	public void layout(){
		super.layout();
		if(isVisible())
			actionBar.computeBounds();
	}

	@Override
	public void setVisible(boolean value) {
		if(tabPane.isEmpty() || !value){
			super.setVisible(false);
			return;
		}

		super.setVisible(true);

		try{
			setPreferredSize(new Dimension(screen.getWidth(), getHeight() > 450 ? getHeight() : 450));
			screen.compilancePane.setDividerLocation(screen.getHeight() - 400);
		}
		catch(Exception e) {

		}
	}

	public void removeTab(String name) {
		tabPane.removeTab(tabPane.getTab(name));
	}

	@Override
	public void paint(Graphics graphics){
		if(tabPane.isEmpty()){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(c2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(UIManager.TOOLMENU_COLOR3);
			g.setFont(PX28);
			FontMetrics f = g.getFontMetrics();
			g.drawString(TITLE, getWidth()/2 - f.stringWidth(TITLE)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 1);
			g.drawString(HINT, getWidth()/2 - f.stringWidth(HINT)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 10 + f.getHeight());
			g.dispose();
		}
		else
			super.paint(graphics);
	}

}


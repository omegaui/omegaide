/**
* TabComp
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

package omega.tabPane;
import omega.utils.JetRunPanel;

import omega.comp.TextComp;

import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabComp extends JComponent implements FocusListener{
	private TabPanel tabPanel;
	private TabData tabData;

	private TextComp iconComp;
	private TextComp nameComp;
	private TextComp closeComp;

	private Runnable removeAction;

	private volatile boolean focussed = false;
	private volatile boolean inList = false;
	
	public TabComp(TabPanel tabPanel, TabData tabData, Runnable removeAction){
		this.tabData = tabData;
		this.tabPanel = tabPanel;
		this.removeAction = removeAction;
		
		setSize(27 + computeWidth(tabData.getName(), UBUNTU_PX14) + 4 + 18, tabHeight);
		setBackground(back1);

		registerListeners();
		
		init();
	}

	public void registerListeners(){
		tabData.getComponent().addFocusListener(this);

		if(tabData.getComponent() instanceof RTextScrollPane scrollPane){
			scrollPane.getViewport().getView().addFocusListener(this);
		}

		else if(tabData.getComponent() instanceof JScrollPane scrollPane){
			scrollPane.getViewport().getView().addFocusListener(this);
		}
		
		else if(tabData.getComponent() instanceof JetRunPanel runPanel){
			runPanel.terminalPanel.addFocusListener(this);
		}

		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				showTab();
			}
		});
	}

	public void init(){
		iconComp = new TextComp(tabData.getImage(), 20, 20, getBackground(), getBackground(), getBackground(), null);
		iconComp.setBounds(2, getHeight()/2 - 25/2, 25, 25);
		iconComp.setArc(0, 0);
		add(iconComp);
		
		if(tabData.getPopup() != null){
			tabData.getPopup().invokeOnMouseLeftPress(iconComp, ()->{});
			putAnimationLayer(iconComp, getImageSizeAnimationLayer(20, 5, true), ACTION_MOUSE_ENTERED);
		}

		nameComp = new TextComp(tabData.getName(), tabData.getTooltip(), getBackground(), getBackground(), tabData.getTabTextColor(), this::showTab);
		nameComp.setBounds(27, getHeight()/2 - 25/2, computeWidth(tabData.getName(), UBUNTU_PX14) + 4, 25);
		nameComp.setFont(UBUNTU_PX14);
		nameComp.setArc(5, 5);
		add(nameComp);

		closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR4, this::closeTab);
		closeComp.setBounds(getWidth() - 17, getHeight()/2 - 15/2, 15, 15);
		closeComp.setFont(PX12);
		closeComp.setArc(4, 4);
		add(closeComp);

		setSize(iconComp.getWidth() + nameComp.getWidth() + 2 + iconComp.getWidth(), tabHeight);
		setPreferredSize(getSize());
	}

	public void showTab(){
		tabPanel.setActiveTab(tabData);
		tabData.getComponent().grabFocus();
	}

	public void closeTab(){
		tabData.getOnClose().run();
		tabPanel.removeTab(tabData);
		removeAction.run();
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if(focussed)
			g.setPaint(new GradientPaint(0, 0, TOOLMENU_COLOR1, getWidth(), getHeight(), TOOLMENU_COLOR2));
		else
			g.setPaint(new GradientPaint(0, 0, back3, getWidth(), getHeight(), back1));
		g.fillRect(0, getHeight() - 2, getWidth(), 2);
		g.fillRect(0, 0, 2, getHeight());
		g.fillRect(getWidth() - 2, 0, 2, getHeight());
		g.fillRect(0, 0, getWidth(), 2);
		super.paint(g);
	}

	@Override
	public void focusLost(FocusEvent e){
		focussed = false;
		repaint();
	}

	@Override
	public void focusGained(FocusEvent e){
		focussed = true;
		repaint();
	}

	public boolean isInList() {
		return inList;
	}
	
	public void setInList(boolean inList) {
		this.inList = inList;
	}

	public TabData getTabData() {
		return tabData;
	}
	
	
}

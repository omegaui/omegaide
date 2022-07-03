/*
 * TabPanel
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
import javax.imageio.ImageIO;

import omega.ui.component.Editor;

import omega.ui.popup.OPopupWindow;

import omega.ui.listener.TabPanelListener;

import omega.io.TabHistory;
import omega.io.TabData;
import omega.io.UIManager;
import omega.io.IconManager;
import omega.io.DataManager;

import org.fife.ui.rtextarea.RTextScrollPane;

import omega.Screen;

import java.util.zip.ZipFile;

import java.awt.image.BufferedImage;

import java.io.File;

import java.util.LinkedList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JComponent;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class TabPanel extends JPanel{

	private TabsHolderPanel tabsHolder;
	private TabCompHolderPanel tabCompHolder;
	private TabHistory tabHistory;

	private LinkedList<TabPanelListener> tabPanelListeners = new LinkedList<>();

	private TabData activeTabData;

	private LinkedList<TabData> tabs = new LinkedList<>();

	public static final int TAB_LOCATION_TOP = 0;
	public static final int TAB_LOCATION_BOTTOM = 1;

	private int tabsPosition;

	private volatile boolean hideOnEmpty = false;

	private Image illustration;

	public TabPanel(int tabsPosition){
		super(new BorderLayout());
		this.tabsPosition = tabsPosition;

		setBackground(back3);
		loadIllustration();

		init();
	}

	public void loadIllustration(){
		BufferedImage image = null;

		try{
			image = ImageIO.read(getClass().getResourceAsStream(DataManager.getBackgroundIllustrationPath()));
		}
		catch(Exception e){
			try{
				image = ImageIO.read(new File(DataManager.getBackgroundIllustrationPath()));
			}
			catch(Exception e1){
				System.err.println("Cannot find Image file : " + DataManager.getBackgroundIllustrationPath());
				e1.printStackTrace();
				try{
					image = ImageIO.read(getClass().getResourceAsStream(DataManager.DEFAULT_ILLUSTRATION_PATH));
				}
				catch(Exception e2){
					e2.printStackTrace();
				}
			}
		}

		illustration = image.getScaledInstance(456, 456, BufferedImage.SCALE_SMOOTH);
		repaint();
	}

	public void init(){
		tabsHolder = new TabsHolderPanel(this);
		add(tabsHolder, tabsPosition == TAB_LOCATION_TOP ? BorderLayout.NORTH : BorderLayout.SOUTH);

		tabCompHolder = new TabCompHolderPanel(this);
		add(tabCompHolder, BorderLayout.CENTER);

		tabHistory = new TabHistory(this);
	}

	public JPanel addTab(String name, String fullQualifiedName, String toolTip, BufferedImage image, JComponent component, Runnable removeAction) {
		return addTab(new TabData(name, fullQualifiedName, toolTip, image, component, removeAction));
	}

	public JPanel addTab(String name, String fullQualifiedName, String toolTip, BufferedImage image, JComponent component, Runnable removeAction, OPopupWindow popupWindow) {
		return addTab(new TabData(name, fullQualifiedName, toolTip, image, component, removeAction).setPopup(popupWindow));
	}

	public JPanel addTab(String name, String fullQualifiedName, String toolTip, BufferedImage image, JComponent component, Color tabTextColor, Runnable removeAction, OPopupWindow popupWindow) {
		return addTab(new TabData(name, fullQualifiedName, toolTip, image, component, tabTextColor, removeAction).setPopup(popupWindow));
	}

	public JPanel addTab(TabData tabData){
		if(isTabDataAlreadyPresent(tabData))
			return null;
		
		boolean wasEmpty = tabs.isEmpty();

		tabs.add(tabData);

		tabsHolder.addTabHolder(tabData);
		tabCompHolder.putTab(tabData);

		activeTabData = tabData;

		if(!isVisible())
			setVisible(true);

		if(!tabPanelListeners.isEmpty())
			tabPanelListeners.forEach(listener->listener.tabAdded(tabData));

		if(wasEmpty){
			//omega.tabPane.TabCompHolder is showing an abnormal behavior(sometimes, don't know the exact condition of why it occurs)
			//It does not gets visible on its own whenever the initial tab is added
			//So, it needs to be triggered manually, like this
			//Behaving same as in Blaze' Core
			Screen.getScreen().splitPane.setDividerLocation(Screen.getScreen().splitPane.getDividerLocation() + 1);
			Screen.getScreen().splitPane.setDividerLocation(Screen.getScreen().splitPane.getDividerLocation() - 1);
		}

		return tabData.getTabHolder().getHolderPanel();
	}

	public boolean isTabDataAlreadyPresent(TabData tabData){
		if(tabs.size() <= 0)
			return false;
		for(TabData dx : tabs){
			if(dx.equals(tabData))
				return true;
		}
		return false;
	}

	public void removeTab(TabData tabData){
		if(tabData == null)
			return;
		
		tabsHolder.removeTabHolder(tabData);
		tabCompHolder.putOffTab(tabData);

		tabs.remove(tabData);

		if(hideOnEmpty && isEmpty())
			setVisible(false);

		if(!tabPanelListeners.isEmpty()){
			tabPanelListeners.forEach(listener->listener.tabRemoved(tabData));
			if(isEmpty())
				tabPanelListeners.forEach(listener->listener.goneEmpty(this));
		}
		repaint();
	}

	public void setActiveTab(TabData tabData){
		if(tabData == null)
			return;
		tabsHolder.showTab(tabData);
		tabCompHolder.showTabComponent(tabData);

		activeTabData = tabData;

		tabPanelListeners.forEach(listener->listener.tabActivated(tabData));
	}
	
	public void setActiveTabIndex(int index){
		TabData tabData = getTabDataAt(index);
		if(tabData == null)
			return;
		tabsHolder.showTab(tabData);
		tabCompHolder.showTabComponent(tabData);

		activeTabData = tabData;

		tabPanelListeners.forEach(listener->listener.tabActivated(tabData));
	}

	public boolean contains(JComponent comp){
		for(TabData dx : tabs){
			if(dx.getComponent() == comp)
				return true;
		}
		return false;
	}

	public TabData getTabData(JComponent comp){
		for(TabData dx : tabs){
			if(dx.getComponent() == comp)
				return dx;
		}
		return null;
	}

	public TabData getTabDataAt(int index){
		return (index >= 0 && index < tabs.size()) ? tabs.get(index) : null;
	}

	public void showTab(JComponent comp){
		setActiveTab(getTabData(comp));
	}

	public int getIndexOf(JComponent comp){
		for(TabData dx : tabs){
			if(dx.getComponent() == comp)
				return tabs.indexOf(dx);
		}
		return -1;
	}

	public boolean viewImage(File file) {
		if(observableImage(file)) {
			Screen.openInDesktop(file);
			return true;
		}
		return false;
	}

	public static boolean observableImage(File file){
		String n = file.getName();
		return n.endsWith(".png") || n.endsWith(".jpg")
		|| n.endsWith(".jpeg") || n.endsWith(".bmp") || n.endsWith(".gif");
	}

	public boolean viewArchive(File file) {
		if(observableArchive(file)) {
			Screen.openInDesktop(file);
			return true;
		}
		return false;
	}

	public static boolean observableArchive(File root){
		try{
			ZipFile zipFile = new ZipFile(root);
			zipFile.close();
			return true;
		}
		catch(Exception e){

		}
		return false;
	}

	public Editor findEditor(File file) {
		for(TabData dx : tabs){
			if(dx.getComponent() instanceof RTextScrollPane scrollPane){
				if(scrollPane.getViewport().getView() instanceof Editor e){
					if(e.currentFile != null) {
						if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath()))
							return e;
					}
				}
			}
		}
		return null;
	}

	public Editor getCurrentEditor() {
		if(activeTabData == null)
			return null;
		if(activeTabData.getComponent() instanceof Editor editor)
			return editor;
		return null;
	}

	public LinkedList<Editor> getEditors(){
		LinkedList<Editor> editors = new LinkedList<>();
		for(TabData dx : tabs){
			if(dx.getComponent() instanceof RTextScrollPane scrollPane){
				if(scrollPane.getViewport().getView() instanceof Editor editor)
					editors.add(editor);
			}
		}
		return editors;
	}

	public void closeAllTabs() {
		tabsHolder.triggerAllClose();
		tabs.clear();

		activeTabData = null;
	}

	public boolean isEmpty(){
		return tabs.isEmpty();
	}

	public TabData getTab(String uniqueName){
		for(TabData tx : tabs){
			if(tx.getUniqueName().equals(uniqueName))
				return tx;
		}
		return null;
	}

	public LinkedList<TabData> getTabs(){
		return tabs;
	}

	public boolean isHideOnEmpty() {
		return hideOnEmpty;
	}

	public void setHideOnEmpty(boolean hideOnEmpty) {
		this.hideOnEmpty = hideOnEmpty;
		if(isEmpty())
			setVisible(false);
	}

	public LinkedList<TabPanelListener> getTabPanelListeners() {
		return tabPanelListeners;
	}

	public void addTabPanelListener(TabPanelListener tabPanelListener) {
		if(!tabPanelListeners.contains(tabPanelListener))
			tabPanelListeners.add(tabPanelListener);
	}

	public TabHistory getTabHistory() {
		return tabHistory;
	}

	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(isEmpty()){
			g.setColor(materialTabHolderColor1);
			g.fillRect(0, 0, getWidth(), getHeight());
			if(illustration != null)
				g.drawImage(illustration, getWidth()/2 - 456/2, getHeight()/2 - 456/2, 456, 456, this);
			g.dispose();
		}
		else{
			super.paint(g);
		}
	}

}

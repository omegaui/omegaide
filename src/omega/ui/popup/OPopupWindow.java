/*
 * PopupWindow
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

package omega.ui.popup;
import omega.io.UIManager;

import java.awt.geom.RoundRectangle2D;

import java.awt.image.BufferedImage;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;

import java.awt.Window;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;

import omega.Screen;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class OPopupWindow extends JDialog{
	private int y;
	private String name;
	private Window owner;
	private JPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<OPopupItem> items = new LinkedList<>();
	private int animaTime;

	public static int HEIGHT = 32;
	private boolean scrollable;
	private Runnable closeOperations;
	public OPopupWindow(String name, Window f, int animaTime, boolean scrollable){
		super(f);
		this.name = name;
		this.owner = f;
		this.animaTime = animaTime;
		this.scrollable = scrollable;
		setUndecorated(true);
		setLayout(scrollable ? new BorderLayout() : null);
		setBackground(back1);
		setForeground(TOOLMENU_COLOR1);
		setType(JWindow.Type.POPUP);
		addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e){
				dispose();
			}
		});
		if(scrollable) {
			panel = new JPanel(null);
			panel.setBackground(c2);
			panel.setForeground(TOOLMENU_COLOR1);
			super.add(scrollPane = new JScrollPane(panel), BorderLayout.CENTER);
			scrollPane.setBorder(null);
		}
	}

	public void trash(){
		items.forEach(this::remove);
		items.clear();
	}

	public OPopupWindow createItem(String name, BufferedImage image, Runnable run){
		OPopupItem item = new OPopupItem(this, name, image, run);
		items.add(item);
		add(item);
		return this;
	}

	public OPopupWindow createItem(String name, String hotkey, BufferedImage image, Runnable run){
		OPopupItem item = new OPopupItem(this, name, hotkey, image, run);
		items.add(item);
		add(item);
		return this;
	}

	public OPopupWindow setEnabled(String name, boolean value){
		if(getItem(name) != null)
			getItem(name).setEnabled(value);
		return this;
	}

	public OPopupWindow removeItem(String name){
		for(OPopupItem i : items){
			if(i.getName().equals(name)){
				remove(i);
				items.remove(i);
				break;
			}
		}
		return this;
	}

	public OPopupWindow removeItem(int i){
		remove(items.get(i));
		items.remove(i);
		return this;
	}

	public OPopupWindow addItem(OPopupItem item){
		items.add(item);
		add(item);
		return this;
	}

	public OPopupItem getItem(String name){
		for(OPopupItem item : items){
			if(item.getName().equals(name))
				return item;
		}
		return null;
	}

	public OPopupWindow width(int width){
		setSize(width, getHeight());
		return this;
	}

	public OPopupWindow height(int height){
		setSize(getWidth(), height);
		return this;
	}

	@Override
	public void setVisible(boolean value){
		if(value){
			if(items.isEmpty())
				return;

			y = 0;
			items.forEach((item)->{
				if(item.isEnabled()) {
					item.setBounds(0, y, getWidth(), HEIGHT);
					y += HEIGHT;
				}
				item.setVisible(item.isEnabled());
			});

			if(scrollable){
				panel.setPreferredSize(new Dimension(getWidth() - 15, y));
				scrollPane.repaint();
			}
			else
				setSize(getWidth(), y);

			int dy = getY() + getHeight() - (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int dx = getX() + getWidth() - (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			setLocation(dx > 0 ? (getX() - dx) : getX(), dy > 0 ? (getY() - dy) : (getY() - 30));
		}
		super.setVisible(value);
		if(scrollable){
			scrollPane.getVerticalScrollBar().setVisible(true);
			scrollPane.getVerticalScrollBar().setValue(0);
			scrollPane.getVerticalScrollBar().repaint();
		}
		if(!value && closeOperations != null) closeOperations.run();
		else if(value){
			new Thread(()->{
				if(animaTime <= 0) return;
				items.forEach(i->{
					i.setEnter(true);
					try{
						Thread.sleep(animaTime);
					}
					catch(Exception e){

					}
					i.setEnter(false);
				});
			}).start();
		}
	}

	@Override
	public Component add(Component c){
		if(scrollable)
			return panel.add(c);
		else
			return super.add(c);
	}

	@Override
	public void remove(Component c){
		if(scrollable)
			panel.remove(c);
		else
			super.add(c);
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 10, 10));
	}

	public void setOnClose(Runnable r){
		this.closeOperations = r;
	}

	public static OPopupWindow gen(String name, Window owner, int animaTime, boolean scrollable){
		return new OPopupWindow(name, owner, animaTime, scrollable);
	}

	public void invokeOnMouseLeftPress(Component c, Runnable onPress){
		invokeOnMouseLeftPress(this, c, onPress);
	}

	public void invokeOnMouseRightPress(Component c, Runnable onPress){
		invokeOnMouseRightPress(this, c, onPress);
	}

	public static void invokeOnMouseLeftPress(OPopupWindow popup, Component c, Runnable onPress){
		c.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				onPress.run();
				popup.setLocation(e.getLocationOnScreen());
				popup.setVisible(true);
			}
		});
	}

	public static void invokeOnMouseRightPress(OPopupWindow popup, Component c, Runnable onPress){
		c.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 3){
					onPress.run();
					popup.setLocation(e.getLocationOnScreen());
					popup.setVisible(true);
				}
			}
		});
	}
}


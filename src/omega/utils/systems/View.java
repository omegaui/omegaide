/**
  * <one line to give the program's name and a brief idea of what it does.>
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

package omega.utils.systems;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JDialog;

import omega.Screen;
import omega.utils.UIManager;

public abstract class View extends JDialog {

	public LinkedList<Component> comps = new LinkedList<>();
	private Screen s;
	private Action a = ()->{};
	
	public interface Action {
		void perform();
	}
	
	public View(String title, Screen window) {
		super(window, true);
		s = window;
		setTitle(title);
		setSize(200,200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(window.getIconImage());
		UIManager.setData(this);
	}
	
	public void setAction(Action a) {
		this.a = a;
	}
	
	public Screen getScreen() {
		return s;
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(), getHeight());
		try {comps.forEach(c->c.repaint());}catch(Exception e) {}
	}
	
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			a.perform();
		}
		super.setVisible(value);
	}
}


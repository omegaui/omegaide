/**
 * Undecorated But Resizable Dialog
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

package omegaui.component;

import javax.swing.border.LineBorder;

import omegaui.opensource.community.utils.ComponentResizer;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class UBRDialog extends JDialog{
	private Color borderColor = new Color(0, 0, 0, 0);
	private int borderWidth = 2;

	public ComponentResizer componentResizer;
	private BorderPanel borderPanel;

	private Runnable onResize = () -> {};

	private class BorderPanel extends JPanel{
		public BorderPanel(){
			setLayout(new BorderLayout());
			setBorder(new LineBorder(borderColor, borderWidth));
		}

		public void refreshBorder(){
			setBorder(new LineBorder(borderColor, borderWidth));
		}
	}

	public UBRDialog(Window owner){
		this(owner, new Dimension(100, 100));
	}

	public UBRDialog(Window owner, Dimension minimumSize){
		super(owner);
		initPanels();
		setUndecorated(true);
		setMinimumSize(minimumSize);
		initResizer();
	}

	private void initPanels(){
		borderPanel = new BorderPanel();
		setContentPane(borderPanel);
	}

	private void initResizer(){
		componentResizer = new ComponentResizer();
		componentResizer.onResize = onResize;
		componentResizer.setMinimumSize(getMinimumSize());
		componentResizer.registerComponent(this);
		componentResizer.setSnapSize(new Dimension(10, 10));
		componentResizer.setDragInsets(new Insets(15, 15, 15, 15));
	}

	public void setSnapSize(Dimension d){
		componentResizer.setSnapSize(d);
	}

	public void setOnResize(Runnable r){
		this.onResize = r;
		componentResizer.onResize = onResize;
	}

	public java.awt.Color getBorderColor() {
		return borderColor;
	}
	
	public void setBorderColor(java.awt.Color borderColor) {
		this.borderColor = borderColor;
		borderPanel.refreshBorder();
	}
	
	public int getBorderWidth() {
		return borderWidth;
	}
	
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		borderPanel.refreshBorder();
	}
	
}

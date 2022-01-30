/**
 * EditorPreviewWindow
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omega.ui.window;
import java.awt.image.BufferedImage;

import omega.io.IconManager;

import omegaui.component.FlexPanel;
import omegaui.component.TextComp;

import java.awt.BorderLayout;

import omega.Screen;

import omega.ui.panel.AbstractPreviewPanel;

import javax.swing.JWindow;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class EditorPreviewWindow extends JWindow{
	public AbstractPreviewPanel previewPanel;

	public FlexPanel contentPanel;

	public TextComp iconComp;
	public TextComp titleComp;
	public TextComp closeComp;
	
	public EditorPreviewWindow(){
		super(Screen.getScreen());
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setBackground(c2);
		contentPanel = new FlexPanel(null, TOOLMENU_COLOR1_SHADE, null);
		contentPanel.setArc(0, 0);
		setContentPane(contentPanel);
		init();
	}

	public void init(){
		iconComp = new TextComp(IconManager.fluentanyfileImage, 20, 20, back3, back3, back3, null);
		iconComp.setArc(0, 0);
		iconComp.setClickable(false);
		iconComp.attachDragger(this);
		add(iconComp);

		titleComp = new TextComp("Preview Window", back3, back3, glow, null);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);

		closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, TOOLMENU_COLOR2_SHADE, back3, back3, this::dispose);
		closeComp.setArc(0, 0);
		add(closeComp);

		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
	}

	public void showPreview(AbstractPreviewPanel previewPanel){
		if(this.previewPanel != null)
			remove(this.previewPanel);
		this.previewPanel = previewPanel;
		add(previewPanel);
		setVisible(true);
	}

	public void setTitle(String title){
		titleComp.setText(title);
	}

	public void setImage(BufferedImage image){
		iconComp.setImage(image);
	}

	@Override
	public void layout(){
		iconComp.setBounds(5, 5, 30, 30);
		titleComp.setBounds(35, 5, getWidth() - 70, 30);
		closeComp.setBounds(getWidth() - 35, 5, 30, 30);
		previewPanel.setBounds(5, 35, getWidth() - 10, getHeight() - 40);
		super.layout();
	}
}

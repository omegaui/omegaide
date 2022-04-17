/**
 * ExtendedBuildPanel
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
import omega.ui.dialog.ExtendedBuildPathManager;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;
import omegaui.component.RTextField;

import java.awt.Dimension;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class ExtendedBuildPanel extends JPanel{

	private TextComp flagComp;
	private TextComp valueComp;
	private ExtendedBuildPathManager extendedBuildPathManager;
	private FlexPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<RTextField> flags = new LinkedList<>();
	private int block;

	public ExtendedBuildPanel(ExtendedBuildPathManager extendedBuildPathManager){
		super(null);
		this.extendedBuildPathManager = extendedBuildPathManager;
		setBackground(c2);
		init();
	}

	public void init(){
		flagComp = new TextComp("Add an Agrument", TOOLMENU_COLOR2_SHADE, back3, TOOLMENU_COLOR2, this::addFlagBox);
		flagComp.setBounds(extendedBuildPathManager.getWidth()/2 - extendedBuildPathManager.getWidth()/4, 10, extendedBuildPathManager.getWidth()/2, 25);
		flagComp.setFont(PX14);
		add(flagComp);

		scrollPane = new JScrollPane(panel = new FlexPanel(null, c2, null));
		scrollPane.setBounds(0, 40, extendedBuildPathManager.getWidth(), extendedBuildPathManager.getHeight() - 70 - 40);
		panel.setArc(0, 0);
		add(scrollPane);
	}

	public void addFlagBox(){
		RTextField flagField = new RTextField("Flag", "--", TOOLMENU_COLOR3, c2, glow);
		flagField.setBounds(0, block, extendedBuildPathManager.getWidth(), 25);
		flagField.setFont(PX14);
		flagField.setArc(0, 0);
		panel.add(flagField);
		flags.add(flagField);

		block += 25;

		panel.setPreferredSize(new Dimension(extendedBuildPathManager.getWidth(), block));
		scrollPane.getVerticalScrollBar().setVisible(true);
		repaint();
	}

	public void loadFlags(LinkedList<String> textFlags){
		flags.forEach(panel::remove);
		flags.clear();

		block = 0;

		textFlags.forEach(text->{
			RTextField flagField = new RTextField("Flag", "--", TOOLMENU_COLOR3, c2, glow);
			flagField.setText(text);
			flagField.setBounds(0, block, extendedBuildPathManager.getWidth(), 25);
			flagField.setFont(PX14);
			flagField.setArc(0, 0);
			panel.add(flagField);
			flags.add(flagField);

			block += 25;
		});
		panel.setPreferredSize(new Dimension(extendedBuildPathManager.getWidth(), block));
		scrollPane.getVerticalScrollBar().setVisible(true);
		repaint();
	}

	public LinkedList<String> getFlags(){
		LinkedList<String> textFlags = new LinkedList<>();
		flags.forEach(flag->{
			if(flag.hasText())
				textFlags.add(flag.getText());
		});
		return textFlags;
	}
}

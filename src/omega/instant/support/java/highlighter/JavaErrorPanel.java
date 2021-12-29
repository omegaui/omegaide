/**
  * JavaErrorPanel
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
package omega.instant.support.java.highlighter;
import omega.ui.component.Editor;

import omegaui.component.FlexPanel;
import omegaui.component.TextComp;

import java.awt.Dimension;
import java.awt.Rectangle;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class JavaErrorPanel extends FlexPanel{
	private Editor editor;
	
	private TextComp errorCountComp;
	private TextComp errorLabel;
	private TextComp sep0;
	private TextComp warningCountComp;
	private TextComp warningLabel;
	
	public JavaErrorPanel(Editor editor) {
		super(null, c2, null);
		setPaintBorder(true);
		setBorderColor(TOOLMENU_COLOR2);
		setArc(12, 12);
		this.editor = editor;
		setVisible(false);
		setPreferredSize(new Dimension(220, 40));
		setSize(getPreferredSize());
		init();
		
		editor.getAttachment().getViewport().addChangeListener((e)->relocate());
	}

	public void init(){
		errorCountComp = new TextComp("93", c2, c2, TOOLMENU_COLOR2, null);
		errorCountComp.setBounds(10, 10, 30, getHeight() - 20);
		errorCountComp.setFont(PX14);
		errorCountComp.setClickable(false);
		errorCountComp.setArc(0, 0);
		add(errorCountComp);

		errorLabel = new TextComp("Error(s)", c2, c2, TOOLMENU_COLOR3, null);
		errorLabel.setBounds(errorCountComp.getX() + errorCountComp.getWidth() + 2, 10, 55, getHeight() - 20);
		errorLabel.setFont(PX14);
		errorLabel.setArc(0, 0);
		errorLabel.setClickable(false);
		add(errorLabel);

		sep0 = new TextComp("", TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, null);
		sep0.setBounds(getWidth()/2 - 1, 2, 2, getHeight() - 4);
		add(sep0);

		warningCountComp = new TextComp("2", c2, c2, TOOLMENU_COLOR1, null);
		warningCountComp.setBounds(getWidth()/2 + 1, 10, 30, getHeight() - 20);
		warningCountComp.setFont(PX14);
		warningCountComp.setClickable(false);
		warningCountComp.setArc(0, 0);
		add(warningCountComp);

		warningLabel = new TextComp("Warning(s)", c2, c2, TOOLMENU_COLOR4, null);
		warningLabel.setBounds(warningCountComp.getX() + warningCountComp.getWidth() + 2, 10, 70, getHeight() - 20);
		warningLabel.setFont(PX14);
		warningLabel.setClickable(false);
		warningLabel.setArc(0, 0);
		add(warningLabel);
	}

	public void setDiagnosticData(int errorCount, int warningCount){
		errorCountComp.setText(errorCount + "");
		warningCountComp.setText(warningCount + "");

		if(errorCount == 0 && warningCount == 0){
			setVisible(false);
		}
		else{
			relocate();
			setVisible(true);
		}
	}

	public void relocate(){
		Rectangle rect = editor.getAttachment().getViewport().getViewRect();
		setLocation((rect.x + rect.width) - getWidth() - 20, (rect.y + rect.height) - getHeight() - 15);
	}
}

/**
  * JavaJumpToDefinitionPanel
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

package omega.instant.support.java.misc;
import java.io.File;

import omega.instant.support.java.framework.CodeFramework;

import omega.io.IconManager;

import omega.Screen;

import java.awt.Dimension;

import omegaui.component.EdgeComp;
import omegaui.component.TextComp;
import omegaui.component.NoCaretField;

import java.util.LinkedList;

import omega.ui.component.Editor;

import omega.instant.support.java.assist.SourceReader;
import omega.instant.support.java.assist.DataMember;

import omega.instant.support.AbstractJumpToDefinitionPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class JavaJumpToDefinitionPanel extends AbstractJumpToDefinitionPanel{

	public LinkedList<EdgeComp> dataComps = new LinkedList<>();

	public NoCaretField textField;
	public TextComp reloadComp;

	public int block;

	public String lastMatch = "";
	
	public JavaJumpToDefinitionPanel(Editor editor){
		super(editor);

		textField = new NoCaretField("", "Search Members", TOOLMENU_COLOR2, back2, TOOLMENU_COLOR3);
		textField.setFont(PX14);
		textField.setOnAction(()->{
			reload(textField.getText());
		});
		panel.add(textField);

		reloadComp = new TextComp(IconManager.fluentresourceImage, 20, 20, "Click to Reload", TOOLMENU_COLOR5_SHADE, back2, glow, ()->{
			reload(lastMatch);
		});
		reloadComp.setArc(5, 5);
		reloadComp.setFont(PX14);
		reloadComp.setPaintTextGradientEnabled(true);
		reloadComp.setGradientColor(TOOLMENU_COLOR2);
		panel.add(reloadComp);
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		reload();
	}
	
	@Override
	public boolean canRead(Editor editor) {
		return editor.currentFile != null && editor.currentFile.getName().endsWith(".java") && editor.currentFile.getAbsolutePath().startsWith(Screen.getProjectFile().getProjectPath() + File.separator + "src" + File.separator);
	}
	
	@Override
	public void reload(String match) {
		try{
			//Resetting View
			dataComps.forEach(panel::remove);
			dataComps.clear();
			
			int maxWidth = 100;
			block = 40;
			
			lastMatch = match;
			
			//Reading the source code
			SourceReader reader = new SourceReader(editor.getText());
			
			//Installing Variables
			for(DataMember dx : reader.ownedDataMembers){
				if(!dx.getRepresentableValue().contains(match))
					continue;
				if(!dx.isMethod()){
					EdgeComp comp = new EdgeComp(dx.getRepresentableValue(), TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, ()->jumpTo(dx));
					comp.setBounds(5, block, computeWidth(dx.getRepresentableValue(), PX14) + 20, 25);
					comp.setFont(PX14);
					comp.setUseFlatLineAtBack(true);
					panel.add(comp);
					dataComps.add(comp);

					if(comp.getWidth() > maxWidth)
						maxWidth = comp.getWidth();

					block += 30;
				}
			}
			
			//Installing Methods			
			for(DataMember dx : reader.ownedDataMembers){
				if(!dx.getRepresentableValue().contains(match))
					continue;
				if(dx.isMethod()){
					EdgeComp comp = new EdgeComp(dx.getRepresentableValue(), TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->jumpTo(dx));
					comp.setBounds(5, block, computeWidth(dx.getRepresentableValue(), PX14) + 20, 25);
					comp.setFont(PX14);
					comp.setUseFlatLineAtBack(true);
					panel.add(comp);
					dataComps.add(comp);

					if(comp.getWidth() > maxWidth)
						maxWidth = comp.getWidth();
					
					block += 30;
				}
			}
			
			//Setting Panel
			maxWidth = maxWidth + 20;
			panel.setPreferredSize(new Dimension(maxWidth, block));
			if(maxWidth <= 300)
				Screen.getScreen().splitPane.setDividerLocation(300);
			else if(maxWidth <= 400)
				Screen.getScreen().splitPane.setDividerLocation(maxWidth);
			else
				Screen.getScreen().splitPane.setDividerLocation(400);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void jumpTo(DataMember dx){
		editor.setCaretPosition(JavaCodeNavigator.getLineOffset(editor, dx.lineNumber));
	}

	@Override
	public void layout(){
		textField.setBounds(5, 5, getWidth() - 15 - 40, 30);
		reloadComp.setBounds(getWidth() - 10 - 40, 5, 30, 30);
		super.layout();
	}
	
}

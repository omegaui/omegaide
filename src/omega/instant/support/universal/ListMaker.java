/**
  * The List Maker
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

package omega.instant.support.universal;
import omega.Screen;

import java.util.LinkedList;

import omega.utils.FileSelectionDialog;
import omega.utils.IconManager;

import java.io.File;

import omega.comp.NoCaretField;
import omega.comp.TextComp;

import javax.swing.JComponent;

import static omega.utils.UIManager.*;
public class ListMaker extends JComponent {
     public NoCaretField extField;
     public NoCaretField containerField;
     public TextComp quoteComp;
     public TextComp dirComp;
     public boolean quoted = false;
     
     public ListMaker(){
     	setLayout(null);
          setSize(600, 30);
          setPreferredSize(getSize());
          setBackground(c2);
     	init();
     }

     public ListMaker(String ext, String container, String dir, boolean quoted){
     	this();
     	extField.setText(ext);
     	containerField.setText(container);
     	dirComp.setToolTipText(dir);
     	dirComp.setText(dir.substring(dir.lastIndexOf(File.separator) + 1));
     	this.quoted = quoted;
     	if(quoted)
     		quoteComp.setColors(c2, TOOLMENU_COLOR3_SHADE, c2);
		else
			quoteComp.setColors(TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR2);
     }

     public void init(){
          FileSelectionDialog fc = new FileSelectionDialog(Screen.getScreen());
          fc.setTitle("Select Working Directory");
          
     	extField = new NoCaretField("", "File Extension", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
          extField.setBounds(0, 0, 200, 30);
          extField.setFont(PX14);
          add(extField);
          
     	containerField = new NoCaretField("", "Container Name", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
          containerField.setBounds(210, 0, 150, 30);
          containerField.setFont(PX14);
          add(containerField);

          quoteComp = new TextComp(IconManager.fluentcommaImage, 25, 25, "Surround Paths within quotes", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR2, ()->{
          	quoted = !quoted;
          	if(quoted)
          		quoteComp.setColors(c2, TOOLMENU_COLOR3_SHADE, c2);
     		else
     			quoteComp.setColors(TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR2);
     	});
          quoteComp.setBounds(370, 0, 30, 30);
          add(quoteComp);

          dirComp = new TextComp("Working Directory", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
          	fc.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()));
               LinkedList<File> selections = fc.selectDirectories();
               if(!selections.isEmpty()){
                    dirComp.setToolTipText(selections.get(0).getAbsolutePath());
     			dirComp.setText(dirComp.getToolTipText().substring(dirComp.getToolTipText().lastIndexOf(File.separator) + 1));
               }
     	});
          dirComp.setBounds(410, 0, 150, 30);
          dirComp.setFont(PX14);
          dirComp.setToolTipText("");
          add(dirComp);

          TextComp enableComp = new TextComp(IconManager.fluentcloseImage, 25, 25, "Remove List Maker", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR2, null);
          enableComp.setRunnable(()->{
          	setEnabled(!isEnabled());
          	if(!isEnabled())
          		enableComp.setColors(c2, TOOLMENU_COLOR2_SHADE, c2);
     		else
     			enableComp.setColors(TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR2);
          });
          enableComp.setBounds(dirComp.getX() + dirComp.getWidth(), 0, 30, 30);
          add(enableComp);
     }
     
	public boolean validateListMaker(){
		boolean passed = true;
		if(getFileExtension().equals("")){
			extField.notify("File Extension Required!");
			passed = false;
		}
		if(getContainerName().equals("")){
			containerField.notify("Container Required!");
			passed = false;
		}
		if(getWorkingDirectory().equals("")){
			dirComp.setText("Directory Required");
			passed = false;
		}
		return passed;
	}

    	public String getFileExtension(){
    		return extField.getText();
    	}

    	public String getContainerName(){
    		return containerField.getText();
    	}

    	public boolean isQuoted(){
    		return quoted;
    	}

    	public String getWorkingDirectory(){
    		return dirComp.getToolTipText();
    	}
}

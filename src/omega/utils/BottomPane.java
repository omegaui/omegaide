/**
  * The BottomPane
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

package omega.utils;
import java.awt.image.BufferedImage;

import omega.popup.NotificationPopup;

import omega.Screen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import omega.comp.TextComp;
import omega.comp.RTextField;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class BottomPane extends JPanel {
	private Screen screen;
	public TextComp messageComp;
	public TextComp logComp;
	public RTextField jumpField;
     public TextComp themeComp;
     private Runnable r = ()->{};
	
	public BottomPane(Screen screen) {
		super(null);
		this.screen = screen;
		setBackground(back2);
		setPreferredSize(new Dimension(100, 25));
		init();
	}
	
	public void init(){
		messageComp = new TextComp("Status of any process running will appear here!", TOOLMENU_COLOR1_SHADE, back2, glow, null){
			@Override
			public void draw(Graphics2D g){
				if(image != null){
					g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), 2, getHeight()/2 - h/2, w, h, null);
				}
			}
		};
		messageComp.setFont(PX14);
		messageComp.alignX = 15;
		messageComp.w = 25;
		messageComp.h = 25;
		messageComp.setPreferredSize(new Dimension(100, 25));
		messageComp.setArc(0, 0);
		messageComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 1 && e.getClickCount() == 2)
					r.run();
			}
		});
		add(messageComp);

		logComp = new TextComp("No Logs", TOOLMENU_COLOR4_SHADE, back1, glow, null){
			@Override
			public void draw(Graphics2D g){
				if(image != null){
					g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), 5, getHeight()/2 - h/2, w, h, null);
				}
			}
		};
		logComp.setFont(PX14);
		logComp.setArc(0, 0);
		logComp.image = IconManager.fluentlogImage;
		logComp.w = 20;
		logComp.h = 20;
		logComp.alignX = 30;
		add(logComp);

          jumpField = new RTextField("Goto Line", "", TOOLMENU_COLOR2, back2, glow);
          jumpField.setFont(PX14);
          jumpField.setArc(0, 0);
          jumpField.addActionListener((e)->{
               if(!jumpField.hasText())
                    return;
               String text = jumpField.getText();
               for(char c : text.toCharArray()){
                    if(!Character.isDigit(c))
                         return;
               }
               int line = Integer.parseInt(text);
               String code = Screen.getScreen().getCurrentEditor() != null ? Screen.getScreen().getCurrentEditor().getText() : "";
               if(code.equals(""))
                    return;
               int pos = 0;
               for(char c : code.toCharArray()){
                    if(line <= 0)
                         break;
                    if(c == '\n')
                         line--;
                    pos++;
               }
               Screen.getScreen().getCurrentEditor().setCaretPosition(pos - 1);
          });
          add(jumpField);

          themeComp = new TextComp(DataManager.getTheme(), TOOLMENU_COLOR1_SHADE, back2, glow, null);
          themeComp.setRunnable(()->{
               Screen.pickTheme(DataManager.getTheme());
               if(!themeComp.getText().equals(DataManager.getTheme())){
				NotificationPopup.create(screen)
				.size(300, 120)
				.title("Theme Manager")
				.dialogIcon(IconManager.fluentupdateImage)
				.message("IDE's Restart is Required!", TOOLMENU_COLOR4)
				.shortMessage("Click this to Restart", TOOLMENU_COLOR2)
				.iconButton(IconManager.fluentcloseImage, ()->{
					Screen.notify("Terminating Running Applications");
					try{
						for(Process p : Screen.getRunView().runningApps) {
							if(p.isAlive())
								p.destroyForcibly();
						}
				     }
				     catch(Exception e2) {
		               
			          }
					Screen.notify("Saving UI and Data");
					screen.getUIManager().save();
					screen.getDataManager().saveData();
					Screen.notify("Saving Project");
					screen.saveAllEditors();
			          try{
			               Screen.getFileView().getProjectManager().save();
			          }
			          catch(Exception e2) {
		               
		               }
		
		               new Thread(()->{
		               	try{
		               		if(Screen.onWindows())
		               			new ProcessBuilder("java", "-jar", "Omega IDE.jar").start();
		          			else
		          				new ProcessBuilder("omega-ide").start();
		               	}
		               	catch(Exception e){
		               		e.printStackTrace();
		               	}
		          	}).start();
		          	
		               screen.dispose();
				}, "")
				.build()
				.locateOnBottomLeft()
				.showIt();
			}
               themeComp.setText(DataManager.getTheme());
               Screen.getScreen().getToolMenu().themeComp.setText(DataManager.getTheme());
          });
          themeComp.setFont(PX14);
          themeComp.setArc(0, 0);
          themeComp.setGradientColor(TOOLMENU_GRADIENT);
          add(themeComp);
	}
	
	@Override
	public void layout(){
		messageComp.setBounds(0, 0, getWidth() - 300, 25);
		logComp.setBounds(getWidth() - 300, 0, 130, 25);
          jumpField.setBounds(getWidth() - 170, 0, 100, 25);
          themeComp.setBounds(getWidth() - 70, 0, 70, 25);
		super.layout();
	}

	public void setShowLogAction(Runnable action){
		logComp.setRunnable(action);
		logComp.setText(action == null ? "No Logs" : "Show Logs");
	}
	
	public void setMessage(String text, BufferedImage image){
		messageComp.image = image;
		messageComp.alignX = image != null ? 35 : 15;
		messageComp.setText(text);
	}
	
	public void setDoubleClickAction(Runnable r){
		this.r = r;
	}
}


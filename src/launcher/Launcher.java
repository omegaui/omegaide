package launcher;
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
import java.awt.event.MouseEvent;
import ide.Manuals;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import tabPane.IconManager;
import ide.Screen;
import java.awt.Dimension;
import java.io.File;
import ide.utils.RecentsManager;
import ide.utils.ToolMenu;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import settings.comp.TextComp;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import static ide.utils.UIManager.*;
public class Launcher extends JFrame{
     private static final BufferedImage icon = getImage("/omega_ide_icon32.png");
     private static final BufferedImage drawIcon = getImage("/omega_ide_icon128.png");
	private final JPanel panel = new JPanel(null);
	private final JScrollPane scrollPane = new JScrollPane(panel);
     private TextComp closeComp;
     private TextComp imageComp;
     private TextComp textComp;
     private int mouseX;
     private int mouseY;
	public Launcher(){
          JPanel p = new JPanel(null);
          p.setBackground(c2);
          setContentPane(p);
		setUndecorated(true);
          setBackground(c2);
		setLayout(null);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage(icon);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                  setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
               }
          });
          addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
		init();
		setVisible(true);
	}

	private void init(){
		panel.setBackground(getBackground());
		scrollPane.setBounds(0, 220, getWidth(), getHeight() - 220);
		add(scrollPane);

		closeComp = new TextComp("X", c1, c2, c3, ()->System.exit(0));
		closeComp.setBounds(getWidth() - 40, 0, 40, 40);
          closeComp.setFont(settings.Screen.PX18);
          closeComp.setArc(0, 0);
		add(closeComp);

          imageComp = new TextComp("", c1, c3, c2, ()->{}){
               @Override
               public void paint(Graphics graphics){
               	Graphics2D g = (Graphics2D)graphics;
               	g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               	g.drawImage(drawIcon, 0, 0, null);
               }
          };
          imageComp.setBounds(getWidth()/2 - 64, 10, 128, 128);
          imageComp.setClickable(false);
          add(imageComp);

          textComp = new TextComp("Omega IDE " + Screen.VERSION, c2, c2, c3, ()->{});
          textComp.setBounds(getWidth()/2 - 200, 140, 400, 50);
          textComp.setClickable(false);
          textComp.setFont(new Font("Ubuntu Mono", Font.BOLD, 40));
          textComp.setArc(0, 0);
          add(textComp);
		
		Door openDoor = new Door(File.separator + "ide" + File.separator + "Project" + File.separator + "Open", icon, ()->{
			if(Screen.getFileView().open("Project"))
				setVisible(false);
		});
		openDoor.setBounds(0, 0, getWidth(), 40);
		panel.add(openDoor);
		
          Door newDoor = new Door(File.separator + "ide" + File.separator + "ide" + File.separator + "Project" + File.separator + "Open" + "Project" + File.separator + "New", icon, ()->{
               ToolMenu.projectWizard.setVisible(true);
               Screen.hideNotif();
          });
          newDoor.setBounds(0, 40, getWidth(), 40);
          panel.add(newDoor);
          
          Door bmanDoor = new Door(File.separator + "ide" + File.separator + "ide" + File.separator + "Manual" + File.separator + "Basic" + " Manual" + File.separator + "See Manual", icon, ()->{
               ide.Manuals.showBasicManual();
               Screen.hideNotif();
          });
          bmanDoor.setBounds(0, 80, getWidth(), 40);
          panel.add(bmanDoor);

		//Creating Doors
		int y = 120;
		for(int i = RecentsManager.RECENTS.size() - 1; i >= 0; i--) {
			String path = RecentsManager.RECENTS.get(i);
			File file = new File(path);
			if(file.exists() && file.isDirectory()) {
				Door door = new Door(path, icon, ()->{
					setVisible(false);
					ide.Screen.getScreen().loadProject(file);
					ide.Screen.getScreen().setVisible(true);
				});
				door.setBounds(0, y, getWidth(), 40);
				panel.add(door);
				y += 40;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), y));
	}

     @Override
     public void setVisible(boolean value){
     	super.setVisible(value);
          repaint();
     }

	public static BufferedImage getImage(String path){
		return (BufferedImage)IconManager.getImageIcon(path).getImage();
	}
}

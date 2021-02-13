package startup;
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
import java.util.Scanner;
import ide.utils.UIManager;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import org.fife.ui.rtextarea.RTextArea;
import settings.comp.TextComp;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;

import ide.Screen;
public class Startup extends JDialog {
	private static BufferedImage image;
     private TextComp closeBtn;
     private RTextArea textArea;
     private TextComp acceptComp;
     private static String LICENSE_TEXT = "";
	public Startup(Screen screen){
		super(screen, true);
          try{
               image = ImageIO.read(getClass().getResourceAsStream(ide.utils.UIManager.isDarkMode() ? "/omega_ide_icon64_dark.png" : "/omega_ide_icon64.png"));
               Scanner reader = new Scanner(getClass().getResourceAsStream("/LICENSE"));
               while(reader.hasNextLine()){
                    LICENSE_TEXT += reader.nextLine() + "\n";
               }
               reader.close();
          }catch(Exception e){ e.printStackTrace(); }
		setUndecorated(true);
		setSize(800, 550);
          JPanel panel = new JPanel(null);
          panel.setBackground(ide.utils.UIManager.c2);
          setContentPane(panel);
          setLayout(null);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBackground(ide.utils.UIManager.c2);
		init();
		setVisible(true);
	}

     public void init(){
     	closeBtn = new TextComp("x", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->System.exit(0));
          closeBtn.setBounds(getWidth() - 30, 0, 30, 30);
          closeBtn.setFont(settings.Screen.PX18);
          closeBtn.setArc(0, 0);
          add(closeBtn);

          textArea = new RTextArea(LICENSE_TEXT);
          JScrollPane scrollPane = new JScrollPane(textArea);
          scrollPane.setBounds(50, 100, getWidth() - 100, getHeight() - 200);
          textArea.setBackground(ide.utils.UIManager.c2);
          textArea.setForeground(ide.utils.UIManager.c3);
          textArea.setFont(settings.Screen.PX18);
          textArea.setCaretPosition(0);
          textArea.setEditable(false);
          add(scrollPane);

          acceptComp = new TextComp("I Accept", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               try{
               	new File(".omega-ide" + File.separator + ".firststartup").createNewFile();
                    setVisible(false);
               }catch(Exception e){ e.printStackTrace(); }
          });
          acceptComp.setBounds(getWidth()/2 - 50, getHeight() - 40, 100, 40);
          acceptComp.setFont(settings.Screen.PX16);
          add(acceptComp);

          TextComp imageComp = new TextComp("", ide.utils.UIManager.c2, ide.utils.UIManager.c2, ide.utils.UIManager.c2, ()->{}){
               @Override
               public void paint(Graphics graphics){
                    super.paint(graphics);
                    Graphics2D g = (Graphics2D)graphics;
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.drawImage(image, 1, 1, null);
               }
          };
          imageComp.setBounds(0, 0, 66, 66);
          imageComp.setClickable(false);
          add(imageComp);

          TextComp textComp = new TextComp("Omega IDE", ide.utils.UIManager.c2, ide.utils.UIManager.c3, ide.utils.UIManager.c3, ()->{});
          textComp.setBounds(getWidth()/2 - 165, 0, 330, 50);
          textComp.setClickable(false);
          textComp.setFont(settings.Screen.PX28);
          textComp.setArc(0, 0);
          add(textComp);
          
          TextComp licComp = new TextComp("license agreement", ide.utils.UIManager.c2, ide.utils.UIManager.c3, ide.utils.UIManager.c3, ()->{});
          licComp.setBounds(getWidth()/2 - 150, 50, 300, 30);
          licComp.setClickable(false);
          licComp.setFont(settings.Screen.PX18);
          licComp.setArc(0, 0);
          add(licComp);
     }

     @Override
     public void paint(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRect(0, 0, getWidth(), getHeight());
     }
    
	public static void checkStartup(Screen screen) {
		if(!new File(".omega-ide" + File.separator + ".firststartup").exists()){
			new Startup(screen).repaint();
		}
	}

     public static void writeUIFiles(){
          File f = new File(".omega-ide");
          if(!f.exists()){
              f.mkdir();
          }
          f = new File(".omega-ide" + File.separator + "out");
          if(!f.exists())
               f.mkdir();
     	if(!new File(".omega-ide" + File.separator + ".ui").exists()){
               ide.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".ui", ".omega-ide/.ui");
          }
          if(!new File(".omega-ide" + File.separator + ".preferences").exists()){
               ide.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".preferences", ".omega-ide/.preferences");
          }
          if(!new File(".omega-ide" + File.separator + ".snippets").exists()){
               ide.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".snippets", ".omega-ide/.snippets");
          }
     }
}

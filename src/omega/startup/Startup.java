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

package omega.startup;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import omega.utils.DataManager;
import omega.utils.UIManager;
import omega.Screen;
import omega.comp.TextComp;
import java.util.Scanner;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import org.fife.ui.rtextarea.RTextArea;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import static omega.utils.UIManager.*;
public class Startup extends JDialog {
	private static BufferedImage image;
	private TextComp closeBtn;
	private RTextArea textArea;
	private TextComp acceptComp;
	private static String LICENSE_TEXT = "";
	public Startup(Screen screen){
		super(screen, true);
		try{
			image = ImageIO.read(getClass().getResourceAsStream(omega.utils.UIManager.isDarkMode() ? "/omega_ide_icon64_dark.png" : "/omega_ide_icon64.png"));
			Scanner reader = new Scanner(getClass().getResourceAsStream("/LICENSE"));
			while(reader.hasNextLine()){
				LICENSE_TEXT += reader.nextLine() + "\n";
			}
			reader.close();
		}
		catch(Exception e){
		     e.printStackTrace();
	     }
		setUndecorated(true);
		setSize(800, 550);
		JPanel panel = new JPanel(null);
		panel.setBackground(omega.utils.UIManager.c2);
		setContentPane(panel);
		setLayout(null);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBackground(omega.utils.UIManager.c2);
		init();
		setVisible(true);
	}
	public void init(){
		closeBtn = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->System.exit(0));
		closeBtn.setBounds(getWidth() - 30, 0, 30, 30);
		closeBtn.setFont(omega.settings.Screen.PX18);
		closeBtn.setArc(0, 0);
		add(closeBtn);
      
		textArea = new RTextArea(LICENSE_TEXT);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(50, 100, getWidth() - 100, getHeight() - 200);
		textArea.setBackground(c2);
		textArea.setForeground(TOOLMENU_COLOR3);
		textArea.setFont(omega.settings.Screen.PX18);
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		add(scrollPane);
		acceptComp = new TextComp("I Accept", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			try{
				new File(".omega-ide" + File.separator + ".firststartup").createNewFile();
				setVisible(false);
	          }
		     catch(Exception e){ 
		          e.printStackTrace();
	          }
		});
		acceptComp.setBounds(getWidth()/2 - 50, getHeight() - 40, 100, 40);
		acceptComp.setFont(omega.settings.Screen.PX16);
		add(acceptComp);
          
	     TextComp imageComp = new TextComp("", c2, c2, c2, null){
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
          
		TextComp textComp = new TextComp("Omega IDE", c2, TOOLMENU_COLOR1, TOOLMENU_COLOR1, null);
		textComp.setBounds(getWidth()/2 - 165, 0, 330, 50);
		textComp.setClickable(false);
		textComp.setFont(omega.settings.Screen.PX28);
		textComp.setArc(0, 0);
		add(textComp);
		
		TextComp licComp = new TextComp("license agreement", c2, TOOLMENU_COLOR2, TOOLMENU_COLOR2, ()->{});
		licComp.setBounds(getWidth()/2 - 150, 50, 300, 30);
		licComp.setClickable(false);
		licComp.setFont(omega.settings.Screen.PX18);
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
			Screen.pickTheme(DataManager.getTheme());
			Screen.getUIManager().loadData();
			try{
				if(UIManager.isDarkMode())
					FlatDarkLaf.install();
				else
					FlatLightLaf.install();
			}
			catch(Exception e) {
				System.err.println(e);
			}
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
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".ui", ".omega-ide/.ui");
		}
		if(!new File(".omega-ide" + File.separator + ".preferences").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".preferences", ".omega-ide/.preferences");
		}
		if(!new File(".omega-ide" + File.separator + ".snippets").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".snippets", ".omega-ide/.snippets");
		}
	}
}


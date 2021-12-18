/**
* Checks for license agreement and Writes IDE resources.
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
import omega.plugin.management.PluginManager;

import java.util.Scanner;

import omega.Screen;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import omega.utils.DataManager;
import omega.utils.UIManager;

import java.io.File;

import javax.imageio.ImageIO;

import omega.comp.TextComp;

import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.geom.RoundRectangle2D;

import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import static omega.utils.UIManager.*;
public class Startup extends JDialog {
	private static BufferedImage image;
	private TextComp closeBtn;
	private RSyntaxTextArea textArea;
	private TextComp acceptComp;
	private static String LICENSE_TEXT = "";
	public Startup(Screen screen){
		super(screen, true);
		try{
			image = ImageIO.read(getClass().getResourceAsStream(isDarkMode() ? "/omega_ide_icon64_dark.png" : "/omega_ide_icon64.png"));
			Scanner reader = new Scanner(getClass().getResourceAsStream("/LICENSE"));
			while(reader.hasNextLine()){
				LICENSE_TEXT += reader.nextLine() + "\n";
			}
			reader.close();
			LICENSE_TEXT += "\n**Copyright 2021 Omega UI. All Right Reserved.**\n";
		}
		catch(Exception e){
			e.printStackTrace();
		}
		setUndecorated(true);
		setSize(650, 550);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setBackground(c2);
		init();
		setVisible(true);
	}
	
	public void init(){
		closeBtn = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->System.exit(0));
		closeBtn.setBounds(getWidth() - 30, 0, 30, 30);
		closeBtn.setFont(PX18);
		closeBtn.setArc(0, 0);
		add(closeBtn);
		
		JScrollPane scrollPane = new JScrollPane(textArea = new RSyntaxTextArea(LICENSE_TEXT));
		scrollPane.setBounds(50, 100, getWidth() - 100, getHeight() - 200);
		scrollPane.setBackground(c2);
		
		textArea.setBackground(c2);
		textArea.setForeground(TOOLMENU_COLOR3);
		textArea.setFont(PX14);
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		MarkdownTokenMaker.apply(textArea);
		add(scrollPane);
		
		acceptComp = new TextComp("I Accept", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			try{
				new File(".omega-ide" + File.separator + ".firststartup").createNewFile();
				dispose();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
		acceptComp.setBounds(getWidth()/2 - 50, getHeight() - 40, 100, 40);
		acceptComp.setFont(PX16);
		add(acceptComp);
		
		TextComp imageComp = new TextComp(image, 64, 64, c2, c2, c2, null);
		imageComp.setBounds(0, 0, 66, 66);
		imageComp.setClickable(false);
		add(imageComp);
		
		TextComp textComp = new TextComp("Omega IDE", c2, TOOLMENU_COLOR1, TOOLMENU_COLOR1, null);
		textComp.setBounds(getWidth()/2 - 165, 0, 330, 50);
		textComp.setClickable(false);
		textComp.setFont(PX28);
		textComp.setArc(0, 0);
		add(textComp);
		
		TextComp licComp = new TextComp("license agreement", c2, TOOLMENU_COLOR2, TOOLMENU_COLOR2, ()->{});
		licComp.setBounds(getWidth()/2 - 150, 50, 300, 30);
		licComp.setClickable(false);
		licComp.setFont(PX18);
		licComp.setArc(0, 0);
		add(licComp);
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
		f = new File(".omega-ide", "out");
		if(!f.exists())
			f.mkdir();
		if(!new File(".omega-ide", ".ui").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".ui", ".omega-ide/.ui");
		}
		if(!new File(".omega-ide", ".preferences").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".preferences", ".omega-ide/.preferences");
		}
		if(!new File(".omega-ide", ".snippets").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".snippets", ".omega-ide/.snippets");
		}
		if(!new File(".omega-ide", ".processExecutionData").exists()){
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".processExecutionData", ".omega-ide/.processExecutionData");
		}
		f = new File(".omega-ide" + File.separator + "dictionary", "english_dic.zip");
		if(!f.exists()){
			f.getParentFile().mkdirs();
			omega.utils.UIManager.loadDefaultFile(".omega-ide" + File.separator + "dictionary" + File.separator + "english_dic.zip", ".omega-ide/dictionary/english_dic.zip");
		}
		f = new File(".omega-ide", "buildspace");
		f.mkdir();
		f = new File(".omega-ide" + File.separator + "buildspace", "src");
		f.mkdir();
		f = new File(".omega-ide" + File.separator + "buildspace", "bin");
		f.mkdir();
		if(!PluginManager.PLUGINS_DIRECTORY.exists())
			PluginManager.PLUGINS_DIRECTORY.mkdirs();
		f = new File(".omega-ide" + File.separator + ".generated-pty-native-libs");
		if(!f.exists()){
			System.out.println("Writing Native Files for Terminal Emulation ...");
			System.out.println("It's a one time process!");
			
			loadDefaultFile("linux" + File.separator + "x86" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/linux/x86/libpty.so");
			loadDefaultFile("linux" + File.separator + "x86_64" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/linux/x86_64/libpty.so");
			loadDefaultFile("macosx" + File.separator + "x86" + File.separator + "libpty.dylib", ".omega-ide/pty4j-libs/macosx/x86/libpty.dylib");
			loadDefaultFile("macosx" + File.separator + "x86_64" + File.separator + "libpty.dylib", ".omega-ide/pty4j-libs/macosx/x86_64/libpty.dylib");
			loadDefaultFile("x86" + File.separator + "libwinpty.dll", ".omega-ide/pty4j-libs/x86/libwinpty.dll");
			loadDefaultFile("x86" + File.separator + "winpty-agent.exe", ".omega-ide/pty4j-libs/x86/winpty-agent.exe");
			loadDefaultFile("win" + File.separator + "x86_64" + File.separator + "winpty.dll", ".omega-ide/pty4j-libs/win/x86_64/winpty.dll");
			loadDefaultFile("win" + File.separator + "x86_64" + File.separator + "winpty-agent.exe", ".omega-ide/pty4j-libs/win/x86_64/winpty-agent.exe");
			loadDefaultFile("win" + File.separator + "x86_64" + File.separator + "winpty-debugserver.exe", ".omega-ide/pty4j-libs/win/x86_64/winpty-debugserver.exe");
			
			System.out.println("Writing Native Library for Terminal Emulation ... Done!");

			try{
				f.createNewFile();
			}
			catch(Exception e){
				System.err.println("An Exception occured in generating the \".generated-pty-native-libs\" file.");
				System.err.println("This usually means that you are running the IDE in a non-owned directory");
				System.err.println("due to this the system denied permission for creating the file, ");
				System.err.println("try running the IDE in an owned directory (like your home folder). If still this exception is occuring then, ");
				System.err.println("Please Open an issue with this log message on https://github.com/omegaui/omegaide");
				System.err.println(e);
			}
			System.out.println("Launching...");
		}
	}

	public static void checkDirectory(String path){
		File f = new File(path);
		if(!f.exists())
			f.mkdir();
	}
}


package ide.utils;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class UIManager extends DataBase {

	public static String fontName = "Ubuntu Mono";
	public static int fontSize = 20;
	private static final Font font = new Font("Ubuntu Mono",Font.BOLD, 12);
	public static Color glow;
	public static Color c1;
	public static Color c2;
	public static Color c3;

	public UIManager(Screen screen) {
		super(".omega-ide" + File.separator + ".ui");
          loadData();
	}

     public static void loadHighlight(){
          if(isDarkMode())
               glow = Color.WHITE;
          else glow = Color.BLACK;
     }

	public void loadData() {
		try {
			DataEntry e = getEntryAt("Font", 0);
			if(e == null) return;
			setFontName(e.getValue());
			setFontSize(getEntryAt("Font", 1).getValueAsInt());
			if(!isDarkMode()) {
				c3 = settings.Screen.color4;
				c1 = new Color(0, 0, 255, 20);
				c2 = Color.WHITE;
			}
			else {
                    c1 = new Color(131, 141, 151, 80);
                    c2 = new Color(31, 41, 51);
                    c3 = new Color(247, 155, 25);
			}
		}catch(Exception e) {System.err.println(e.getMessage());}
	}
	
	public static void reset() {
		fontName = "Ubuntu Mono";
		fontSize = 12;
	}

	public static void setData(Editor editor) {
		editor.setFont(new Font(fontName, Font.BOLD, fontSize));
	}

	public void setData(int fontSize, String fontName) {
		UIManager.fontSize = fontSize;
		UIManager.fontName = fontName;
	}

	public static void setData(Component c) {
		c.setBackground(c2);
		c.setForeground(c3);
		c.setFont(font);
	}

	@Override
	public void save() {
		clear();
		addEntry("Font", fontName);
		addEntry("Font", fontSize+"");
		super.save();
	}

     public static File loadDefaultFile(String name){
          File file = new File(name);
     	try{
               if(file.exists()) return file;
               InputStream in = UIManager.class.getResourceAsStream("/" + name);
               OutputStream out = new FileOutputStream(file);
               while(in.available() > 0)
                    out.write(in.read());
               out.close();
               in.close();
     	}catch(Exception e){ System.err.println(e); }
          return file;
     }
	
	public static boolean isDarkMode() {
          return DataManager.getTheme().equals("dark");
		//return ((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 62;
	}

	public static void setFontName(String fontName) {
		UIManager.fontName = fontName;
	}

	public static void setFontSize(int fontSize) {
		UIManager.fontSize = fontSize;
	}

}

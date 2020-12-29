package ide.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class UIManager extends DataBase{

	public static String fontName = "Ubuntu Mono";
	public static int fontSize = 20;
	private static final Font font = new Font("Ubuntu Mono",Font.BOLD, 12);
	public static Color glow = Color.YELLOW;
	public static Color c1;
	public static Color c2;
	public static Color c3;

	public UIManager(Screen screen) 	{
		super(".ui");
          loadData();
	}

     public static void loadHighlight(){
          if(isDarkMode())
               glow = Color.decode("#ffffff");
          else glow = tree.Branch.ANY_COLOR;
     }

	public void loadData()
	{
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
                    c1 = new Color(133, 46, 196);
                    c2 = new Color(5, 6, 16);
                    c3 = new Color(160, 107, 200);
			}
		}catch(Exception e) {System.out.println(e.getMessage());}
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
	public void save()
	{
		clear();
		addEntry("Font", fontName);
		addEntry("Font", fontSize+"");
		super.save();
	}
	
	public static boolean isDarkMode() {
		return ((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 62;
	}

	public static void setFontName(String fontName) {
		UIManager.fontName = fontName;
	}

	public static void setFontSize(int fontSize) {
		UIManager.fontSize = fontSize;
	}

}

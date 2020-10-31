package ide.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import ide.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class UIManager extends DataBase{

	public static String fontName = "Ubuntu Mono";
	public static int fontSize = 14;
	private static final Font font = new Font("Ubunut Mono",Font.BOLD, 12);
	public static Color glow = Color.YELLOW;

	public UIManager(Screen screen) 	{
		super(".ui");
		loadData();
	}

	public void loadData()
	{
		try {
			DataEntry e = getEntryAt("Font", 0);
			if(e == null) return;
			setFontName(e.getValue());
			setFontSize(getEntryAt("Font", 1).getValueAsInt());
			if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53)
				glow = Color.decode("#ffffff");
			else glow = Color.BLUE;
		}catch(Exception e) {System.out.println(e.getMessage());}
	}
	
	public static void reset() {
		fontName = "Ubunut Mono";
		fontSize = 12;
	}

	public static void setData(Editor editor) {
		editor.setFont(new Font(fontName, Font.BOLD, fontSize));
	}

	public void setData(int fontSize, String fontName)
	{
		UIManager.fontSize = fontSize;
		UIManager.fontName = fontName;
	}

	public static void setData(Component c) {
		if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53) {
			c.setBackground(contentUI.Click.colorY);
			c.setForeground(contentUI.Click.colorX);
		}else {
			c.setBackground(Color.WHITE);
		}
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

	public static void setFontName(String fontName) {
		UIManager.fontName = fontName;
	}

	public static void setFontSize(int fontSize) {
		UIManager.fontSize = fontSize;
	}

}

/**
* Manages IDE Theming
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

package omega.io;
import omega.ui.component.Editor;

import omegaui.component.animation.Animations;

import omegaui.dynamic.database.DataBase;
import omegaui.dynamic.database.DataEntry;

import java.awt.image.BufferedImage;

import omega.Screen;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Graphics;

/**
* omega.utils.UIManager class
* This class is responsible for managing the ui colors, theming and editor's font
* extends omega.database.DataBase class
*/

public class UIManager extends DataBase {
	
	/**
	* The field carrying the default font name which was registered in omega.Screen
	*/
	public static volatile boolean animationsActive = true;
	
	/**
	* The field carrying the default tab Height
	*/
	public static int tabHeight = 40;
	
	/**
	* The field carrying the default font name which was registered in omega.Screen
	*/
	public static String fontName = "Ubuntu Mono";
	
	/**
	* The field carrying the default font size
	*/
	public static int fontSize = 16;
	
	/**
	* The field carrying the default font state
	*/
	public static int fontState = Font.BOLD;
	
	/**
	* The Foreground Color of any text area
	*/
	public static Color glow;
	
	/**
	* The Highlight Color of TextComp
	*/
	public static Color highlight;
	
	/**
	* The Base Shade of UI Elements
	*/
	public static Color c1;
	
	/**
	* The Background of UI Elements
	*/
	public static Color c2;
	
	/**
	* The Base Solid Color of UI Elements
	*/
	public static Color c3;
	
	//Image Object to be used for computing text dimension.
	public static BufferedImage testImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	// IDE Component Colors -- Default Mode -- LIGHT
	
	/**
	* The Following Colors are distributed according to the positions of the Buttons along with the separators in the omega.utils.ToolMenu class
	* Containing Five Solid Colors, their shades and gradient
	* By Default they are assigned Light Mode Values so that
	* if the system didn't permits the IDE to read UI files(happens when in a non-owned directory),
	* the IDE will still have its colors and can launch without exceptions.
	*/
	public static Color TOOLMENU_COLOR1 = new Color(26, 36, 219);
	public static Color TOOLMENU_COLOR1_SHADE = new Color(26, 36, 219, 40);
	public static Color TOOLMENU_COLOR2 = new Color(223, 33, 15);
	public static Color TOOLMENU_COLOR2_SHADE = new Color(223, 33, 15, 40);
	public static Color TOOLMENU_COLOR3 = new Color(126, 20, 219);
	public static Color TOOLMENU_COLOR3_SHADE = new Color(126, 20, 219, 40);
	public static Color TOOLMENU_COLOR4 = new Color(200, 103, 0);
	public static Color TOOLMENU_COLOR4_SHADE = new Color(200, 103, 0, 40);
	public static Color TOOLMENU_COLOR5 = new Color(16, 62, 110);
	public static Color TOOLMENU_COLOR5_SHADE = new Color(16, 62, 110, 40);
	public static Color TOOLMENU_GRADIENT = new Color(200, 200, 200, 100);
	public static Color ALPHA = new Color(0, 0, 0, 0);
	
	//Some Extra Colors of RGB shades
	public static final Color color1 = new Color(255, 0, 0, 20);
	public static final Color color2 = new Color(255, 0, 0, 130);
	public static final Color color3 = new Color(255, 255, 200, 220);
	public static final Color color4 = new Color(0, 0, 255, 130);
	public static final Color color5 = new Color(0, 0, 255, 220);
	public static final Color color6 = new Color(0, 255, 0, 20);
	public static final Color color7 = new Color(0, 255, 0, 130);
	
	//Some Backgrounds
	public static Color back1 = Color.decode("#f3f3f3");
	public static Color back2 = Color.decode("#fcfcfc");
	public static Color back3 = Color.decode("#eaeaea");
	
	//IDE Component Fonts -- Finals
	public static final Font PX12 = new Font(fontName, Font.BOLD, 12);
	public static final Font PX14 = new Font(fontName, Font.BOLD, 14);
	public static final Font PX16 = new Font(fontName, Font.BOLD, 16);
	public static final Font PX18 = new Font(fontName, Font.BOLD, 18);
	public static final Font PX20 = new Font(fontName, Font.BOLD, 20);
	public static final Font PX22 = new Font(fontName, Font.BOLD, 22);
	public static final Font PX24 = new Font(fontName, Font.BOLD, 24);
	public static final Font PX26 = new Font(fontName, Font.BOLD, 26);
	public static final Font PX28 = new Font(fontName, Font.BOLD, 28);
	public static final Font PX30 = new Font(fontName, Font.BOLD, 30);
	public static final Font PX36 = new Font(fontName, Font.BOLD, 36);
	public static final Font PX40 = new Font(fontName, Font.BOLD, 40);
	public static final Font UBUNTU_PX12 = new Font("Ubuntu", Font.BOLD, 12);
	public static final Font UBUNTU_PX14 = new Font("Ubuntu", Font.BOLD, 14);
	public static final Font UBUNTU_PX16 = new Font("Ubuntu", Font.BOLD, 16);
	
	public UIManager(Screen screen) {
		super(".omega-ide" + File.separator + ".ui");
		loadData();
	}
	
	public static void loadHighlight(){
		if(isDarkMode()){
			glow = Color.WHITE;
			highlight = Color.decode("#88580B");
		}
		else {
			glow = Color.BLACK;
			highlight = Color.decode("#0000ff");
		}
	}
	
	public void loadData() {
		try {
			DataEntry e = getEntryAt("Font", 0);
			if(e == null)
				return;
			setFontName(e.getValue());
			setFontSize(getEntryAt("Font", 1).getValueAsInt());
			setFontState(getEntryAt("Font", 2).getValueAsInt());
			setAnimationsActive(getEntryAt("Animations On", 0).getValueAsBoolean());
			if(!isDarkMode()) {
				c3 = color4;
				c1 = new Color(0, 0, 255, 40);
				c2 = Color.WHITE;
			}
			else {
				c1 = Color.decode("#132162");
				c2 = Color.decode("#1e1e1e");
				c3 = Color.decode("#3CE5DD");
				TOOLMENU_COLOR1 = Color.decode("#f0b40f");
				TOOLMENU_COLOR1_SHADE = new Color(TOOLMENU_COLOR1.getRed(), TOOLMENU_COLOR1.getGreen(), TOOLMENU_COLOR1.getBlue(), 40);
				TOOLMENU_COLOR2 = Color.decode("#D34D42");
				TOOLMENU_COLOR2_SHADE = new Color(TOOLMENU_COLOR2.getRed(), TOOLMENU_COLOR2.getGreen(), TOOLMENU_COLOR2.getBlue(), 40);
				TOOLMENU_COLOR3 = Color.decode("#22d5d5");
				TOOLMENU_COLOR3_SHADE = new Color(TOOLMENU_COLOR3.getRed(), TOOLMENU_COLOR3.getGreen(), TOOLMENU_COLOR3.getBlue(), 40);
				TOOLMENU_COLOR4 = Color.decode("#EB7201");
				TOOLMENU_COLOR4_SHADE = new Color(TOOLMENU_COLOR4.getRed(), TOOLMENU_COLOR4.getGreen(), TOOLMENU_COLOR4.getBlue(), 40);
				TOOLMENU_COLOR5 = Color.decode("#7f6021");
				TOOLMENU_COLOR5_SHADE = new Color(TOOLMENU_COLOR4.getRed(), TOOLMENU_COLOR4.getGreen(), TOOLMENU_COLOR4.getBlue(), 40);
				TOOLMENU_GRADIENT = new Color(51, 51, 51, 140);
				back1 = Color.decode("#252526");
				back2 = Color.decode("#333333");
				back3 = Color.decode("#303030");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static int computeWidth(String name, Font font){
		Graphics2D g = (Graphics2D)testImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		return g.getFontMetrics().stringWidth(name);
	}

	public static int computeHeight(Font font){
		Graphics g = testImage.getGraphics();
		g.setFont(font);
		return g.getFontMetrics().getHeight();
	}

	public static void drawAtCenter(String text, Graphics2D g, Component c){
		g.drawString(text, c.getWidth()/2 - computeWidth(text, g.getFont())/2, 
			c.getHeight()/2 - computeHeight(g.getFont())/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
	}
	
	/**
	* This methods resets the font data if any exception occurs during reading the database file (.ui)
	*/
	public static void reset() {
		fontName = "Ubuntu Mono";
		fontSize = 16;
		fontState = Font.BOLD;
	}
	
	public static void setData(Editor editor) {
		editor.setFont(new Font(fontName, fontState, fontSize));
	}
	
	public static void setData(int fontSize, String fontName, int fontState) {
		UIManager.fontSize = fontSize;
		UIManager.fontName = fontName;
		UIManager.fontState = fontState;
	}
	
	public static void setData(Component c) {
		c.setBackground(c2);
		c.setForeground(c3);
		c.setFont(PX12);
	}
	
	@Override
	public void save() {
		clear();
		addEntry("Animations On", animationsActive + "");
		addEntry("Font", fontName);
		addEntry("Font", fontSize + "");
		addEntry("Font", fontState + "");
		super.save();
	}
	
	public static File loadDefaultFile(String fileName, String resName){
		File file = new File(fileName);
		try{
			if(file.exists())
				return file;
			else
				file.getParentFile().mkdirs();
			InputStream in = UIManager.class.getResourceAsStream("/" + resName);
			OutputStream out = new FileOutputStream(file);
			while(in.available() > 0)
				out.write(in.read());
			out.close();
			in.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return file;
	}
	
	public static boolean isDarkMode() {
		return DataManager.getTheme().equals("dark");
	}
	
	public static void setFontName(String fontName) {
		UIManager.fontName = fontName;
	}
	
	public static void setFontSize(int fontSize) {
		UIManager.fontSize = fontSize;
	}
	
	public static void setFontState(int fontState) {
		UIManager.fontState = fontState;
	}

	public static boolean isAnimationsActive() {
		return animationsActive;
	}
	
	public static void setAnimationsActive(boolean animationsActive) {
		UIManager.animationsActive = animationsActive;
		Animations.setAnimationsOn(animationsActive);
	}
	
}
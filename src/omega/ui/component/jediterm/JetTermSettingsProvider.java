package omega.ui.component.jediterm;
import omega.io.UIManager;

import com.jediterm.terminal.TextStyle;

import java.awt.Font;
import java.awt.Color;

import com.jediterm.terminal.emulator.ColorPalette;

import javax.swing.KeyStroke;

import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class JetTermSettingsProvider extends DefaultSettingsProvider{

	public static Color[] colors = new Color[16];
	static{
		colors[0] = glow;
		colors[1] = TOOLMENU_COLOR2;
		colors[2] = TOOLMENU_COLOR1;
		colors[3] = TOOLMENU_COLOR5;
		colors[4] = TOOLMENU_COLOR3;
		colors[5] = Color.decode("#FF8C42");
		colors[6] = Color.decode("#FBB13C");
		colors[7] = Color.decode("#D81159");
		colors[8] = Color.decode("#E0777D");
		colors[9] = Color.decode("#8E3B46");
		colors[10] = Color.decode("#A2AD59");
		colors[11] = Color.decode("#92140C");
		colors[12] = Color.decode("#253237");
		colors[13] = Color.decode("#5C6B73");
		colors[14] = Color.decode("#4C2719");
		colors[15] = back1;
	}
	
	@Override
	public Font getTerminalFont() {
		return new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize);
	}
	
	@Override
	public float getTerminalFontSize() {
		return UIManager.fontSize;
	}
	
	@Override
	public boolean useInverseSelectionColor() {
		return true;
	}

	@Override
	public ColorPalette getTerminalColorPalette() {
		return new ColorPalette(){
			@Override
			public Color[] getIndexColors(){
				return colors;
			}
		};
	}
	
	@Override
	public boolean useAntialiasing() {
		return true;
	}
	
	@Override
	public boolean audibleBell() {
		return true;
	}
	
	@Override
	public boolean scrollToBottomOnTyping() {
		return true;
	}
	
}

package omega.terminal.jediterm;
import omega.utils.UIManager;

import com.jediterm.terminal.TextStyle;

import java.awt.Font;

import com.jediterm.terminal.emulator.ColorPalette;

import javax.swing.KeyStroke;

import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
public class JetTermSettingsProvider extends DefaultSettingsProvider{
	
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
	public int getBufferMaxLinesCount() {
		return Integer.MAX_VALUE;
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

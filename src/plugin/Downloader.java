package plugin;
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
import java.awt.*;
import org.fife.ui.rtextarea.*;
import javax.swing.*;
public class Downloader extends JDialog{
	private RTextArea textArea;
	private static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	public Downloader(PluginStore f){
		super(f, "Downloader");
		setSize(600, 500);
		setLocationRelativeTo(f);
		setIconImage(f.getIconImage());
		setLayout(new BorderLayout());
		setModal(false);
		textArea = new RTextArea();
		textArea.setFont(PX14);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	public void print(String text){
		textArea.append("\n" + text);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

	public boolean isErrorOccured() {
		String text = textArea.getText().substring(textArea.getText().lastIndexOf('\n') + 1);
		return text.contains("wget: unable to resolve host") || text.contains("service not known");
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		if(value)
			textArea.setText("Downloading\n");
	}
}

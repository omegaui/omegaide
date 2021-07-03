/**
  * Launcher
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

package omega.launcher;
import java.io.File;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
public class Door extends JComponent{
	protected String path;
	protected String name;
	protected String parent;
	protected BufferedImage image;
	private volatile boolean enter;
	public Door(String path, BufferedImage image, Runnable r){
		super();
		this.image = image;
		this.path = path;
		this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		this.parent = path.substring(0, path.lastIndexOf(File.separatorChar));
		this.parent = "<"+this.parent.substring(parent.lastIndexOf(File.separatorChar) + 1)+">";
		setFont(new Font("Ubuntu Mono", Font.BOLD, 16));
          setBackground(c2);
          setForeground(c3);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				r.run();
			}
			@Override
			public void mouseEntered(MouseEvent e){
				enter = true;
                    Color x = getBackground();
                    setBackground(getForeground());
                    setForeground(x);
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				enter = false;
                    Color x = getBackground();
                    setBackground(getForeground());
                    setForeground(x);
				repaint();
			}
		});
	}
	
	public String getPath() {
		return path;
	}
	
	public void set(boolean enter) {
		this.enter = enter;
          Color x = getBackground();
          setBackground(getForeground());
          setForeground(x);
		repaint();
	}

	public void paint(Graphics g2){
		super.paint(g2);
		Graphics2D g = (Graphics2D)g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(enter ? 34 : 0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawImage(image, 0, getHeight()/2 - 16, 32, 32, this);
		g.drawString(name, 40, 12);
		g.drawString(parent, 40, 27);
	}
}


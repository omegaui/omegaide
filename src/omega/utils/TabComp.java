/**
  * The Tab head component.
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

package omega.utils;
import java.awt.image.BufferedImage;
import omega.tree.*;
import omega.popup.OPopupWindow;
import java.io.File;
import omega.Screen;
import omega.utils.UIManager;
import omega.comp.TextComp;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
public class TabComp extends JComponent {

	private CloseAction closeAction;
	
	public TabComp() {
		setFocusable(false);
		UIManager.setData(this);
          setForeground(UIManager.TOOLMENU_COLOR1);
		final Color fore = getForeground();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Color.ORANGE);
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(fore);
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				setForeground(Color.GREEN);
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				setForeground(fore);
				repaint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				closeAction.onClose();
			}
		});
	}
	
	public TabComp setOnClose(CloseAction closeAction) {
		this.closeAction = closeAction;
		return this;
	}

	@Override
	public void paint(Graphics g2D) {
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.setFont(getFont());
		g.drawString("X", 4, getFont().getSize()-3);
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		setPreferredSize(new Dimension(16, 16));
		setSize(getPreferredSize());
	}

	public interface CloseAction {
		void onClose();
	}
	
	public interface FocusAction {
		void onFocus();
	}

	private static LinkedList<JTextArea> areas = new LinkedList<>();

	public static JPanel create(Component c, String name, CloseAction closeAction, FocusAction focusAction, String toolTip, OPopupWindow popUp) {
		TabComp closeButton = new TabComp().setOnClose(closeAction);
		closeButton.setFont(new Font("Ubuntu", Font.BOLD, 14));

		JTextArea textField = new JTextArea(!toolTip.startsWith("src") ? ("{" + name + "}") : name);
		textField.setForeground(Branch.getColor(name));
          textField.setBackground(UIManager.c2);
		final Color FORE = textField.getForeground();
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				areas.forEach((a)->{
					if(a != textField)
						a.setForeground(FORE);
					else
						a.setForeground(UIManager.glow);
					a.repaint();
				});
				focusAction.onFocus();
			}
		};
		textField.setBorder(null);
		textField.setToolTipText(toolTip);
		textField.setEditable(false);
		textField.setFont(closeButton.getFont());
		textField.addMouseListener(mouseAdapter);
		areas.add(textField);
		
		String baseName = getBaseName(name);
          TextComp iconButton = null;
          
          Color alpha = new Color(FORE.getRed(), FORE.getGreen(), FORE.getBlue(), 40);
          BufferedImage image = null;
          if(c instanceof Editor)
               image = getPreferredImage(((Editor)c).currentFile);
          else
               image = IconManager.fluentshellImage;
          iconButton = new TextComp(image, 25, 25, toolTip, textField.getBackground(), alpha, FORE, ()->{});
		iconButton.setPreferredSize(new Dimension(baseName.length() > 2 ? (baseName.length() > 3 ? 40 : 30) : 20, 28));
		iconButton.setFont(omega.settings.Screen.PX16);
		
		if(popUp != null) {
			iconButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					popUp.setVisible(true);
                         popUp.setLocation(e.getLocationOnScreen());
				}
			});
		}
		JPanel panel = new JPanel();
		UIManager.setData(panel);
		panel.setLayout(new FlowLayout());
		panel.add(iconButton);
		panel.add(textField);
		panel.add(closeButton);
		panel.addMouseListener(mouseAdapter);
		c.addMouseListener(mouseAdapter);
		areas.forEach((a)->{
			if(a != textField) {
				a.setForeground(FORE);
			}
			else
				a.setForeground(UIManager.glow);
			a.repaint();
		});
		return panel;
	}
	
	public static String getBaseName(String ext) {
		if(ext.equals("Compilation"))
			return "JVM";

          if(ext.equals("Building"))
               return "IDE";

          else if(ext.equals("Terminal"))
               return "Shell";
               
          else if(ext.equals("File Operation"))
               return "Task";

          if(ext.contains("Run("))
               return "JVM";
               
          if((ext.contains("Run ") || ext.contains("Run") || ext.contains("Build")) )
               return "IDE";

          if(!ext.contains("."))
               return "?";

          if(ext.equals(".projectInfo") || ext.equals(".sources")|| ext.equals(".args") || ext.equals(".preferences") || ext.equals(".recents") || ext.equals(".firststartup") || ext.equals(".ui") || ext.equals(".plugs") || ext.equals(".snippets"))
               return "IDE";
          
          return "?";
	}
     
     public static BufferedImage getPreferredImage(File file){
          if(file.isDirectory()){
               File[] files = file.listFiles();
               for(File fx : files){
                    if(fx.getName().equals(".projectInfo"))
                         return IconManager.fluentfolderImage;
               }
               return IconManager.fluentplainfolderImage;
          }
          if(file.getName().contains(".")){
               String ext = file.getName().substring(file.getName().lastIndexOf('.'));
               if(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".bmp")
               || ext.equals(".gif") || ext.equals(".svg") || ext.equals(".ico") || ext.equals(".jp2"))
                    return IconManager.fluentimagefileImage;
               else if(ext.equals(".txt") || ext.equals(".java") || ext.equals(".cpp") || ext.equals(".py") || ext.equals(".rs") || ext.equals(".class") || ext.equals(".groovy"))
                    return IconManager.fluentfileImage;
               else if(ext.equals(".js") || ext.equals(".html") || ext.equals(".php") || ext.equals(".css"))
                    return IconManager.fluentwebImage;
               else if(ext.equals(".sh") || ext.equals(".run") || ext.equals(".dll") || ext.equals(".so"))
                    return IconManager.fluentshellImage;
               else if(ext.equalsIgnoreCase(".appimage") || ext.equals(".deb"))
                    return IconManager.fluentlinuxImage;
               else if(ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".exe") || ext.equals(".msi"))
                    return IconManager.fluentwindowsImage;
               else if(ext.equals(".dmg"))
                    return IconManager.fluentmacImage;
          }
          return IconManager.fluentanyfileImage;
     }
}


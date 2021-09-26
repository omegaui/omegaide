/**
  * SDKSelector
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
import java.awt.geom.RoundRectangle2D;

import omega.Screen;

import java.io.File;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import omega.comp.TextComp;

import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;

import static omega.utils.UIManager.*;
public class SDKSelector extends JDialog {
	private static Dimension dimension;
	
	private JScrollPane scrollPane;
	private JPanel panel = new JPanel(null);
	
	private LinkedList<TextComp> boxs = new LinkedList<>();
	private String selection = null;
	
	private int block;
	
	public SDKSelector(JFrame f) {
		super(f, true);
		setUndecorated(true);
		setSize(500, 400);
		setLocationRelativeTo(f);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
		setLayout(null);
          
		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(0, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
          
		TextComp titleComp = new TextComp("Select JDK Version", TOOLMENU_COLOR3, TOOLMENU_COLOR3, c2, null);
		titleComp.setBounds(30, 0, getWidth() - 30, 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);
		
		scrollPane = new JScrollPane(panel){
			@Override
			public void paint(Graphics graphics){
				if(boxs.isEmpty()){
					Graphics2D g = (Graphics2D)graphics;
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setColor(c2);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(TOOLMENU_COLOR3);
					g.setFont(PX14);
					g.drawString("No JDKs found at the specified path!", getWidth()/2 - g.getFontMetrics().stringWidth("No JDKs found at the specified path!")/2,
     					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() - 15);
				}
				else{
					super.paint(graphics);
				}
			}
		};
		scrollPane.setBounds(0, 30, getWidth(), getHeight() - 30);
		scrollPane.setBorder(null);
          panel.setBackground(c2);
		add(scrollPane);
	}
	private void resolvePath() {
		boxs.forEach(box->panel.remove(box));
		boxs.clear();
		block = 0;
		selection = null;
		String pathJava = DataManager.getPathToJava();
		if(pathJava == null)
			return;
		File[] files = new File(pathJava).listFiles();
		if(files == null) {
			omega.Screen.setStatus("No JDKs found in \"" + pathJava + "\"", 10);
			return;
		}
		for(File file : files) {
			if(file.isFile())
				continue;
			String release = getRelease(file.getAbsolutePath());
			if(release != null) {
				TextComp box = new TextComp(file.getName() + "(" + release + ")", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
					selection = file.getAbsolutePath();
					SDKSelector.this.setVisible(false);
				});
				box.setBounds(0, block, getWidth(), 30);
				box.alignX = 5;
				box.setArc(0, 0);
				box.setFont(PX14);
				panel.add(box);
				boxs.add(box);
				block += 30;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), block));
	}
	
	private String getRelease(String path) {
		File releaseFile = new File(path + File.separator + "release");
		if(!releaseFile.exists()) return null;
			try{
			Scanner reader = new Scanner(releaseFile);
			while(reader.hasNextLine()){
				String s = reader.nextLine();
				String cmd = "JAVA_VERSION=";
				if(s.startsWith(cmd)){
					s = s.substring(s.indexOf(cmd) + cmd.length());
					s = s.substring(s.indexOf('\"') + 1, s.lastIndexOf('\"'));
					reader.close();
					return s;
				}
			}
			reader.close();
		}
		catch(Exception e){
			return null;
		}
		return null;
	}
	
	public String getSelection() {
		return selection;
	}

     @Override
     public void paint(Graphics g){
     	super.paint(g);
          scrollPane.repaint();
     }
     
	@Override
	public void setVisible(boolean value) {
		if(value) {
			resolvePath();
		}
		super.setVisible(value);
	}
}


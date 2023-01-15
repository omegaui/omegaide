/*
 * ContentAssist
 * Copyright (C) 2022 Omega UI

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

package omega.instant.support.java.assist;
import omega.instant.support.java.highlighter.BasicCodeHighlighter;


import omega.ui.component.Editor;

import omega.instant.support.java.framework.CodeFramework;

import omega.io.DataManager;
import omega.io.IconManager;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;

import java.awt.image.BufferedImage;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.LinkedList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class ContentWindow extends JPanel implements KeyListener{

	public LinkedList<HintComp> hints = new LinkedList<>();

	private Editor editor;

	private FlexPanel flexPanel;
	private JScrollPane scrollPane;
	private JPanel panel;

	private int block;
	private int gap = 3;
	private int width;
	private int height;

	public int index;

	public static final int MINIMUM_HINT_HEIGHT = 30;

	public int optimalHintHeight = MINIMUM_HINT_HEIGHT;

	private volatile boolean ignoreGenViewOnce = false;

	public static Color highlightColor = glow;
	
	public Color hintHoverColor = TOOLMENU_GRADIENT;

	private class HintComp extends JPanel{
		private DataMember d;
		private String match;

		private TextComp iconComp;
		private TextComp nameComp;

		public HintComp(DataMember d, String match, Runnable action){
			super(null);

			this.d = d;
			this.match = match;

			init();
			nameComp.setRunnable(action);
		}

		public void init(){
			setBackground(c2);
			setBorder(null);

			iconComp = new TextComp(getIcon(), optimalHintHeight - 5, optimalHintHeight - 5, getBackground(), getBackground(), getBackground(), null);
			iconComp.setBounds(0, 0, optimalHintHeight, optimalHintHeight);
			iconComp.setArc(0, 0);
			iconComp.setClickable(false);
			add(iconComp);

			nameComp = new TextComp(d.getRepresentableValue(), hintHoverColor, getBackground(), BasicCodeHighlighter.classColor, null);
			nameComp.setBounds(optimalHintHeight, 0, width, optimalHintHeight);
			nameComp.setFont(DataManager.getHintFont());
			nameComp.setArc(0, 0);
			nameComp.alignX = 2;
			nameComp.setUseSpeedMode(true);
			nameComp.addHighlightText(new String[]{ match });
			nameComp.setHighlightColor(highlight);
			nameComp.setPaintEnterFirst(isDarkMode());
			nameComp.setShouldRepaintForegroundOnHighlights(isDarkMode());
			add(nameComp);
		}

		public void setEnter(boolean value){
			if(value){
				nameComp.setFont(DataManager.getHintFont().deriveFont(Font.BOLD));
			}
			else{
				nameComp.setFont(DataManager.getHintFont());
			}
			nameComp.setEnter(value);
		}

		public void setColor3(Color color){
			nameComp.color3 = color;
			nameComp.repaint();
		}

		public void run(){
			nameComp.runnable.run();
		}

		public BufferedImage getIcon(){
			if(d.modifier.contains("synchronized"))
				return IconManager.fluentsyncImage;
			if(d.modifier.contains("final"))
				return IconManager.fluentconstantImage;
			if(d.modifier.contains("volatile"))
				return IconManager.fluentvolatileImage;
			if(d.modifier.contains("class"))
				return IconManager.fluentclassFileImage;
			return d.isMethod() ? IconManager.fluentmethodImage : IconManager.fluentvariableImage;
		}
	}

	public ContentWindow(Editor editor){
		this.editor = editor;
		setVisible(false);
		setBackground(TOOLMENU_GRADIENT);
		setLayout(null);

		flexPanel = new FlexPanel(null, TOOLMENU_GRADIENT, null);
		flexPanel.setArc(10, 0);
		add(flexPanel);

		flexPanel.add(scrollPane = new JScrollPane(panel = new JPanel(null)));

		scrollPane.setBorder(null);
		scrollPane.setBackground(c2);

		panel.setBackground(c2);

		hintHoverColor = new Color(hintHoverColor.getRed(), hintHoverColor.getGreen(), hintHoverColor.getBlue(), 20);
	}

	public boolean isIgnoreGenViewOnce() {
		return ignoreGenViewOnce;
	}

	public void setIgnoreGenViewOnce(boolean ignoreGenViewOnce) {
		this.ignoreGenViewOnce = ignoreGenViewOnce;
	}

	public synchronized void genView(LinkedList<DataMember> dataMembers){
		if(dataMembers.isEmpty()){
			setVisible(false);
			return;
		}

		hints.forEach(panel::remove);
		hints.clear();

		sort(dataMembers);

		block = gap;
		width = 0;

		Font hintFont = DataManager.getHintFont();

		optimalHintHeight = computeHeight(hintFont) + 6;
		optimalHintHeight = optimalHintHeight <  20 ? MINIMUM_HINT_HEIGHT : optimalHintHeight;
	
		dataMembers.forEach(data->{
			String text = data.getRepresentableValue();
			if(text != null){
				int w = computeWidth(text, hintFont);
				if(w > width)
					width = w;
			}
		});

		width += optimalHintHeight/2;

		final Editor e = editor;
		final String match = CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition());

		if(isIgnoreGenViewOnce()){
			setIgnoreGenViewOnce(false);
			return;
		}

		dataMembers.forEach(d->{
			if(d.getRepresentableValue() != null){
				HintComp hintComp = new HintComp(d, match, ()->{
					ContentWindow.this.setVisible(false);
					String lCode = CodeFramework.getCodeIgnoreDot(editor.getText(), editor.getCaretPosition());
					try {
						e.getDocument().remove(e.getCaretPosition() - match.length(), match.length());

						e.insert(d.name, e.getCaretPosition());

						if(d.parameterCount > 0)
							e.setCaretPosition(e.getCaretPosition() - 1);

						d.getExtendedInsertion().run();
					}
					catch(Exception es) {
						es.printStackTrace();
					}
				});
				hintComp.setName(String.valueOf(d.isMethod()));
				hintComp.setBounds(0, block, width + optimalHintHeight, optimalHintHeight);
				panel.add(hintComp);
				hints.add(hintComp);

				block += gap + optimalHintHeight;
			}
		});

		if(block == 0)
			return;

		if(isIgnoreGenViewOnce()){
			setIgnoreGenViewOnce(false);
			return;
		}

		hints.getFirst().setEnter(true);
		hints.getFirst().setColor3(highlightColor);
		scrollPane.getVerticalScrollBar().setValue(0);

		index = 0;

		width += optimalHintHeight;
		panel.setPreferredSize(new Dimension(width, block));
		scrollPane.setPreferredSize(panel.getPreferredSize());
		doLayout();

		width += optimalHintHeight;
		height = (Math.min(block, 200)) + 8;

		setVisible(false);
		setSize((Math.min(width, 700)), height);
		setMinimumSize(getSize());
		setPreferredSize(getSize());
		flexPanel.setBounds(0, 0, getWidth(), getHeight());
		scrollPane.setBounds(5, 5, getWidth() - 10, flexPanel.getHeight() - 10);
		decideLocation();
		setVisible(true);
		repaint();

		dataMembers.clear();
	}

	public void decideLocation(){
		final Editor e = editor;
		Rectangle vRect = e.getAttachment().getVisibleRect();
		if(e.getCaret().getMagicCaretPosition() == null)
			return;
		int x = e.getCaret().getMagicCaretPosition().x;
		int y = e.getCaret().getMagicCaretPosition().y + e.getFont().getSize();
		int xSep = (x + getWidth()) - (int)(vRect.x + vRect.getWidth());
		int ySep = (y + getHeight()) - (int)(vRect.y + vRect.getHeight());
		if(xSep > 0){
			x -= xSep;
			if(x < vRect.x)
				x = vRect.x;
		}
		if(ySep > 0)
			y = e.getCaret().getMagicCaretPosition().y - getHeight();
		setLocation(x, y);
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!isVisible()) 
			return;
		try{
			if(e.getKeyCode() == KeyEvent.VK_DOWN){
				if(index < hints.size() - 1){
					hints.get(index).setEnter(false);
					hints.get(index).setColor3(BasicCodeHighlighter.classColor);
					hints.get(++index).setEnter(true);
					hints.get(index).setColor3(highlightColor);
					scrollPane.getVerticalScrollBar().setValue(index * optimalHintHeight + (index * gap));
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_UP){
				if(index > 0){
					hints.get(index).setEnter(false);
					hints.get(index).setColor3(BasicCodeHighlighter.classColor);
					hints.get(--index).setEnter(true);
					hints.get(index).setColor3(highlightColor);
					scrollPane.getVerticalScrollBar().setValue(index * optimalHintHeight + (index * gap));
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_ENTER){
				hints.get(index).run();
			}
		}
		catch(Exception ex){
			setVisible(false);
			ex.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {

	}

	public synchronized static void sort(LinkedList<DataMember> dataMembers) {
		Object[] members = dataMembers.toArray();
		LinkedList<DataMember> vars = new LinkedList<>();
		LinkedList<DataMember> meths = new LinkedList<>();
		for(Object obj : members) {
			DataMember m = (DataMember)obj;
			if(m.parameters == null)
				vars.add(m);
			else
				meths.add(m);
		}
		dataMembers.clear();
		Object[] var_ =vars.toArray();
		Object[] meths_ = meths.toArray();
		for(int i = 0; i < var_.length; i++) {
			for(int j = 0; j < var_.length - 1 - i; i++) {
				DataMember m = (DataMember)var_[j];
				DataMember n = (DataMember)var_[j + 1];
				if(m.name.compareTo(n.name) > 0) {
					Object o = var_[j];
					var_[j] = var_[j + 1];
					var_[j + 1] = o;
				}
			}
		}

		for(int i = 0; i < meths_.length; i++) {
			for(int j = 0; j < meths_.length - 1 - i; i++) {
				DataMember m = (DataMember)meths_[j];
				DataMember n = (DataMember)meths_[j + 1];
				if(m.name.compareTo(n.name) > 0) {
					Object o = meths_[j];
					meths_[j] = meths_[j + 1];
					meths_[j + 1] = o;
				}
			}
		}
		for(Object v : var_) {
			dataMembers.add((DataMember)v);
		}
		for(Object v : meths_) {
			dataMembers.add((DataMember)v);
		}
		var_ = null;
		meths_ = null;
		members = null;
	}
}


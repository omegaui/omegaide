package deassembler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import contentUI.ContentManager;
import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;

public class ContentWindow extends JPanel implements KeyListener{
	private final LinkedList<Hint> hints = new LinkedList<>();
	private JScrollPane scrollPane;
	private JPanel panel;
	private int block;
	public volatile int pointer;
	public static volatile int max;
	private int i;
	public ContentWindow() {
		super(new BorderLayout());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				hints.get(pointer).inject();
			}
		});
		panel = new JPanel(null);
		add(scrollPane = new JScrollPane(panel), BorderLayout.CENTER);
		ide.utils.UIManager.setData(panel);
		hints.forEach(h->{
			h.setVisible(false);
			panel.remove(h);
		});
		hints.clear();
	}
	
	public void genView(LinkedList<DataMember> dataMembers) {
		if(dataMembers.isEmpty()) {
			setVisible(false);
			return;
		}
		try {
			sort(dataMembers);
			final Editor e = Screen.getScreen().getCurrentEditor();
			Point point = e.getCaret().getMagicCaretPosition();
			setLocation(point.x, point.y + e.getFont().getSize() + 1);
			final int X = (int)e.getAttachment().getVisibleRect().getWidth(); 
			setSize(X - 65 > 0 ? X - 65 : 500, 200);
			hints.forEach(h->{
				h.setVisible(false);
				panel.remove(h);
			});
			hints.clear();
			block = 0;
			pointer = 0;
			final boolean isDarkMode = ((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 53;
			final Font font = new Font(UIManager.fontName, Font.BOLD, Hint.OPTIMAL_FONT_HEIGHT);
			Font xf = Screen.getScreen().getFont();
			Screen.getScreen().getGraphics().setFont(font);
			max = getWidth();
			dataMembers.forEach(d->{
				if(d.getRepresentableValue() != null) {
					int w = Screen.getScreen().getGraphics().getFontMetrics().stringWidth(d.getRepresentableValue());
					if(max < w) {
						max = w + 1;
					}
					Hint hint = new Hint(d, (dx)->{
						String lCode = CodeFramework.getLastCodeIgnoreDot(Screen.getScreen().getCurrentEditor().getText(), Screen.getScreen().getCurrentEditor().getCaretPosition());
						if(lCode == null) {
							e.insert(d.name, e.getCaretPosition());
						}
						else {
							String part = d.name;
							try {
								part = part.substring(lCode.length());
								e.insert(part, e.getCaretPosition());
								if(d.parameterCount > 0) e.setCaretPosition(e.getCaretPosition() - 1);
							}catch(Exception es) {}
						}
						
					}, d.getRepresentableValue());
					hint.setBounds(0, block, getWidth(), Hint.OPTIMAL_HEIGHT);
					if(isDarkMode) ide.utils.UIManager.setData(hint);
					hint.setFont(font);
					panel.add(hint);
					hints.add(hint);
					block += Hint.OPTIMAL_HEIGHT;
				}
			});
			i = 0;

			final String LCode = CodeFramework.getLastCodeIgnoreDot(Screen.getScreen().getCurrentEditor().getText(), Screen.getScreen().getCurrentEditor().getCaretPosition());
			ContentManager.userCodes.forEach((code)->{
				if(LCode != null && !code.startsWith(LCode)) {}
				else if(LCode == null){
					String type = ContentManager.codeTypes.get(i++);
					DataMember d = new DataMember("custom hint", "", type, code, code.contains("(") ? "" : null);
					Hint hint = new Hint(d, (dx)->{
						String lCode = CodeFramework.getLastCodeIgnoreDot(Screen.getScreen().getCurrentEditor().getText(), Screen.getScreen().getCurrentEditor().getCaretPosition());
						if(lCode == null) {
							e.insert(d.name, e.getCaretPosition());
						}
						else {
							String part = d.name;
							try {
								part = part.substring(lCode.length());
								e.insert(part, e.getCaretPosition());
								if(d.parameterCount > 0) e.setCaretPosition(e.getCaretPosition() - 1);
							}catch(Exception es) {}
						}

					}, d.getRepresentableValue());
					hint.setBounds(0, block, getWidth(), Hint.OPTIMAL_HEIGHT);
					if(isDarkMode) ide.utils.UIManager.setData(hint);
					hint.setFont(font);
					panel.add(hint);
					hints.add(hint);
					block += Hint.OPTIMAL_HEIGHT;
				}
			});
			Screen.getScreen().getGraphics().setFont(xf);
			hints.get(0).focussed(true);
			panel.setPreferredSize(new Dimension(max, block));
			setVisible(true);
			scrollPane.getVerticalScrollBar().setVisible(true);
			repaint();
			scrollPane.getVerticalScrollBar().repaint();
			scrollPane.getHorizontalScrollBar().repaint();
			doLayout();
		}catch(Exception ex) {System.out.println(ex.getMessage()); setVisible(false);}
	}
	
	public static void sort(LinkedList<DataMember> dataMembers) {
		Object[] members = dataMembers.toArray();
		LinkedList<DataMember> vars = new LinkedList<>();
		LinkedList<DataMember> meths = new LinkedList<>();
		for(Object obj : members) {
			DataMember m = (DataMember)obj;
			if(m.parameters == null) vars.add(m);
			else meths.add(m);
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
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			setVisible(false);
			Editor e = Screen.getScreen().getTabPanel().getCurrentEditor();
			setLocation(e.getCaret().getMagicCaretPosition().x, e.getCaret().getMagicCaretPosition().y + e.getFont().getSize() + 1);
			Rectangle vRect = e.getAttachment().getVisibleRect();
			Rectangle cRect = getBounds();
			if(cRect.getY() + cRect.getHeight() > vRect.getY() + vRect.getHeight()) {
				setLocation(getX(), (int)(cRect.getY() - cRect.getHeight() - e.getFont().getSize() - 1));
			}
			setLocation(e.getCaret().getMagicCaretPosition().x - getWidth() > 0 ? e.getCaret().getMagicCaretPosition().x - getWidth() : 0, getY());
			repaint();
		}
		super.setVisible(value);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(!isVisible()) return;
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(pointer + 1 < hints.size()) {
				hints.get(pointer).focussed(false);
				hints.get(++pointer).focussed(true);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + Hint.OPTIMAL_HEIGHT);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(pointer - 1 >= 0) {
				hints.get(pointer).focussed(false);
				hints.get(--pointer).focussed(true);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - Hint.OPTIMAL_HEIGHT);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			setVisible(false);
			hints.get(pointer).inject();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}

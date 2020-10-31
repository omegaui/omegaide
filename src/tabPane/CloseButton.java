package tabPane;

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

import ide.utils.UIManager;

public class CloseButton extends JComponent {

	private CloseAction closeAction;
	
	public CloseButton() {
		setFocusable(false);
		UIManager.setData(this);
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
	
	public CloseButton setOnClose(CloseAction closeAction) {
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
	public static JPanel create(Component c, String name, CloseAction closeAction, FocusAction focusAction, String toolTip, Icon icon, JPopupMenu popUp) {
		CloseButton closeButton = new CloseButton().setOnClose(closeAction);
		closeButton.setFont(new Font("Ubuntu", Font.BOLD, 14));
        
		JTextArea textField = new JTextArea(name);
		UIManager.setData(textField);
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
		IconButton iconButton = new IconButton(icon);
		if(popUp != null) {
			iconButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					popUp.setLocation(e.getLocationOnScreen());
					popUp.setVisible(true);
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

}

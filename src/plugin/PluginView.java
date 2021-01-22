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
import settings.comp.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
public class PluginView extends JDialog{
	private static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);
	private static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	private static LinkedList<Door> doors = new LinkedList<>();
	private static int block;
	private static JPanel leftPanel = new JPanel(null);
	private static JScrollPane leftPane = new JScrollPane(leftPanel);
	public static final int LEFT_OFFSET = 300;
	private BufferedImage image;
	public static Plugin plugin;
	private static TextComp closeBtn;

	//Information View
	private static JButton iconBtn;
	private static JTextField nameField;
	private static JTextField versionField;
	private static JTextArea descriptionArea;
	private static JScrollPane scrollPane = null;
	private static JTextField authorField;
	private static JTextField copyrightField;
	private static TextComp mRBtn;
	private static TextComp mLBtn;
	private static int index = 0;
     private int mouseX;
     private int mouseY;

	public PluginView(ide.Screen screen){
		super(screen);
		setTitle("Plugin Manager");
		setIconImage(screen.getIconImage());
		setModal(false);
		setUndecorated(true);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				setVisible(false);
				dispose();
			}
		});
		image = tabPane.IconManager.ideImage;
		init();
	}

	public void init(){
		closeBtn = new TextComp("X", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               setVisible(false);
               dispose();
	     });
		closeBtn.setFont(PX16);
		closeBtn.setBounds(0, 0, 40, 40);
		closeBtn.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				setLocation(e.getXOnScreen() - 50, e.getYOnScreen() - 20);
			}
		});
          closeBtn.setArc(0, 0);
		add(closeBtn);

		TextComp title = new TextComp("Plugin Manager", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{});
		title.setBounds(40, 0, LEFT_OFFSET - 40, 40);
		title.setFont(PX16);
          title.setArc(0, 0);
          title.setClickable(false);
          title.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - mouseX - 40, e.getYOnScreen() - mouseY);
               }
          });
          title.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
		add(title);

		leftPane.setBounds(0, 40, LEFT_OFFSET, getHeight());
		add(leftPane);

		if(ide.utils.UIManager.isDarkMode()) {
			ide.utils.UIManager.setData(leftPane);
		}
		else
			leftPane.setBackground(Color.WHITE);
		leftPanel.setBackground(leftPane.getBackground());
          
		//Initializing the View
		iconBtn = new JButton();
		iconBtn.setBounds(LEFT_OFFSET, 0, 40, 40);
		add(iconBtn);

		nameField = new JTextField();
		nameField.setFont(PX16);
		nameField.setEditable(false);
		nameField.setBounds(iconBtn.getX() + 40, 0, getWidth() - iconBtn.getX() - 40 - 140, 40);
          nameField.setBackground(ide.utils.UIManager.c2);
          nameField.setForeground(ide.utils.UIManager.c3);
		add(nameField);

		versionField = new JTextField();
		versionField.setFont(PX16);
		versionField.setEditable(false);
		versionField.setBounds(nameField.getX() + nameField.getWidth(), 0, 100, 40);
          versionField.setBackground(ide.utils.UIManager.c2);
          versionField.setForeground(ide.utils.UIManager.c3);
		add(versionField);
		
		TextComp removeBtn = new TextComp("-", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
               if(plugin == null) return;
               String fileName = ide.Screen.getPluginManager().getPlug(plugin.getName()).fileName;
               int res = JOptionPane.showConfirmDialog(PluginView.this, "Do you want to uninstall " + plugin.getName() + "?\nThis Operation requires IDE restart!", "Plugin Manager", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
               if(res == JOptionPane.OK_OPTION) {
                    new java.io.File("omega-ide-plugins" + java.io.File.separator + fileName).delete();
               }
	     });
		removeBtn.setToolTipText("Click to Uninstall this Plugin");
		removeBtn.setFont(PX16);
		removeBtn.setBounds(getWidth() - 40, 0, 40, 40);
          removeBtn.setArc(0, 0);
		add(removeBtn);

		descriptionArea = new JTextArea() {
			@Override
			public void setText(String text) {
				super.setText(text);
				scrollPane.getHorizontalScrollBar().setValue(0);
			}
		};
		scrollPane = new JScrollPane(descriptionArea);
		descriptionArea.setFont(PX14);
		descriptionArea.setEditable(false);
          descriptionArea.setBackground(ide.utils.UIManager.c3);
          descriptionArea.setForeground(ide.utils.UIManager.c2);
		scrollPane.setBounds(LEFT_OFFSET, 40, getWidth() - LEFT_OFFSET, 100);
		add(scrollPane);

		authorField = new JTextField();
		authorField.setFont(PX14);
		authorField.setEditable(false);
		authorField.setBounds(LEFT_OFFSET, 140, 100, 40);
          authorField.setBackground(ide.utils.UIManager.c2);
          authorField.setForeground(ide.utils.UIManager.c3);
		add(authorField);

		copyrightField = new JTextField();
		copyrightField.setFont(PX14);
		copyrightField.setEditable(false);
		copyrightField.setBounds(authorField.getX() + authorField.getWidth(), 140, getWidth() - 80 - LEFT_OFFSET - authorField.getWidth(), 40);
          copyrightField.setBackground(ide.utils.UIManager.c2);
          copyrightField.setForeground(ide.utils.UIManager.c3);
		add(copyrightField);

		mRBtn = new TextComp(">", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               if(plugin == null || plugin.getImages() == null) return;
               if(!plugin.getImages().isEmpty()){
                    if(index < plugin.getImages().size() - 1) index++;
                    else if(index == plugin.getImages().size() - 1) index = 0;
               }
               repaint();
	     });
		mRBtn.setFont(PX14);
		mRBtn.setBounds(getWidth() - 40, 140, 40, 40);
          mRBtn.setArc(0, 0);
		add(mRBtn);

		mLBtn = new TextComp("<", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               if(plugin == null || plugin.getImages() == null) return;
               if(!plugin.getImages().isEmpty()){
                    if(index > 0) index--;
                    else if(index == 0) index = plugin.getImages().size() - 1;
               }
               repaint();
	     });
		mLBtn.setFont(PX14);
		mLBtn.setBounds(getWidth() - 80, 140, 40, 40);
          mLBtn.setArc(0, 0);
		add(mLBtn);
	}

	public void load(){
		doors.forEach(leftPanel::remove);
		doors.clear();
		block = 0;
		PluginManager.plugins.forEach((p)->{
			Door door = new Door(ide.Screen.getPluginManager().getPlug(p.getName()), p.getAuthor() + " - " + p.getVersion() + "/" + p.getName(), p.getImage() == null ? image : p.getImage(), ()->{
				plugin = p;
				showPlug();
			});
			door.setBounds(0, block, LEFT_OFFSET, 40);
			leftPanel.add(door);
			doors.add(door);
			block += 40;
		});
		leftPanel.setPreferredSize(new Dimension(LEFT_OFFSET, block));
		leftPane.getVerticalScrollBar().setVisible(true);
		leftPane.getVerticalScrollBar().setValue(leftPane.getVerticalScrollBar().getValue());
		repaint();
		if(doors.size() != 0){
			plugin = PluginManager.plugins.getFirst();
			showPlug();
		}
	}

	public void showPlug(){
		if(plugin == null) return;
		iconBtn.setIcon(new ImageIcon(plugin.getImage() == null ? image : plugin.getImage()));
		nameField.setText(plugin.getName());
		versionField.setText(plugin.getVersion());
		descriptionArea.setText(plugin.getDescription());
		descriptionArea.setCaretPosition(0);
		authorField.setText(plugin.getAuthor());
		copyrightField.setText(plugin.getCopyright());
		index = 0;
		repaint();
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		leftPanel.repaint();
		closeBtn.repaint();
		mRBtn.repaint();
		mLBtn.repaint();
		if(plugin == null || plugin.getImages() == null) return;
		if(!plugin.getImages().isEmpty()){
			g.drawImage(plugin.getImages().get(index), LEFT_OFFSET, 180, getWidth() - LEFT_OFFSET, getHeight() - 180, this);
		}
	}

	@Override
	public void setVisible(boolean value){
		if(value) load();
		else ide.Screen.getPluginManager().save();
		super.setVisible(value);
	}
}

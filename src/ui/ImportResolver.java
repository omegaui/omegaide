package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import ide.Screen;
import ide.utils.UIManager;
import importIO.Import;

public class ImportResolver extends JDialog {
	private JScrollPane scrollPane;
	private LinkedList<ToggleBox> boxs = new LinkedList<>();
	public static final Font font = new Font("Consolas", Font.BOLD, 12);
	private JPanel panel = new JPanel(null);
	private static Dimension dimension;
	private int y;
	public ImportResolver() {
		super(Screen.getFileView().getScreen(), true);
		ide.utils.UIManager.setData(this);
		setTitle("Choose a Type to import");
		setSize(600, 300);
		setLocationRelativeTo(null);
		setType(Type.UTILITY);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		add((scrollPane = new JScrollPane(panel)), BorderLayout.CENTER);
		UIManager.setData(this);
		UIManager.setData(panel);
		dimension = new Dimension(getWidth(), 30);
	}

	@Override
	public void paint(Graphics g) {
		panel.setPreferredSize(new Dimension(getWidth(), y));
		dimension = new Dimension(getWidth(), 30);
		boxs.forEach(box->{
			box.setPreferredSize(dimension);
			box.setMinimumSize(dimension);
			box.setSize(dimension);
		});
		super.paint(g);
		scrollPane.repaint();
	}
	public LinkedList<Import> resolveImports(LinkedList<Import> imports) {
		if(imports.size() <= 1) {
			imports.clear();
			return imports;
		}
		boxs.forEach(box->panel.remove(box));
		boxs.clear();
		y = 0;
		LinkedList<Import> selections = new LinkedList<>();
		for(Import im : imports) {
			ToggleBox box = new ToggleBox(im.getClassName() + " -"+im.getImport(), (selected)->{
				if(selected) {
					selections.add(im);
				}else {
					selections.remove(im);
				}
			});
			box.setBounds(0, y, panel.getWidth(), 30);
			box.setPreferredSize(dimension);
			box.setMinimumSize(box.getPreferredSize());
			panel.add(box);
			boxs.add(box);
			y += 30;

		}
		panel.setPreferredSize(new Dimension(getWidth() - 20, y));
		setVisible(true);
		setSize(getWidth(),getHeight() - 1);
		setSize(getWidth(),getHeight() + 1);
		repaint();
		return selections;
	}
}

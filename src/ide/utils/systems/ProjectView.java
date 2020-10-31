package ide.utils.systems;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import ide.Screen;
import ide.utils.ResourceView;
import ide.utils.UIManager;
import ide.utils.systems.creators.RefractionManager;
import tree.FileTree;

public class ProjectView extends JDialog{

	private static final long serialVersionUID = 1L;
	public FileTree tree;
	private JFileChooser chooser = new JFileChooser();
	private RefractionManager refractionManager;
	public ResourceView resourceView;
	private Screen screen;

	public ProjectView(String title, Screen window) {
		super(window);
		this.screen = window;
		setTitle(title);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setIconImage(window.getIconImage());
		setSize(600, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		initComponents();
		setAlwaysOnTop(true);
		resourceView = new ResourceView(window);
		UIManager.setData(chooser);
		setModal(false);
	}

	public RefractionManager getRefractor(){
		return refractionManager;
	}

	private void initComponents() {
		refractionManager = new RefractionManager(getScreen());
		tree = new FileTree(null);
		final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					tree.moveUp();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					tree.moveDown();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					tree.forceLocate();
				}
			}
		};
		addKeyListener(keyListener);
		tree.addKeyListener(keyListener);
	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			organizeProjectViewDefaults();
			if(tree.getRoot() == null || !tree.getRoot().getAbsolutePath().equals(Screen.getFileView().getProjectPath())) {
				tree = new FileTree(Screen.getFileView().getProjectPath());
				tree.gen(tree.getRoot());
			}
			repaint();
		}       
		super.setVisible(value);
	}

	public void organizeProjectViewDefaults() {
		if(!getScreen().screenHasProjectView) {
			getScreen().setToNull();
			add(tree, BorderLayout.CENTER);
		}
		else {
			remove(tree);
			getScreen().setToView();
		}
		repaint();
	}
	
	public FileTree getProjectView() {
		return tree;
	}

	public void reload() {
		if(!screen.screenHasProjectView)
			remove(tree);
		else
			screen.setToNull();
		tree = tree.reload();
		organizeProjectViewDefaults();
		Screen.getFileView().getSearchWindow().cleanAndLoad(new File(Screen.getFileView().getProjectPath()));
	}

	public void setTitleMainClass()
	{
		String mainClass = Screen.getRunView().mainClass;
		if(mainClass == null || mainClass.equals(""))
			setTitle("Project Structure -No Main Class");
		else
			setTitle("Project Structure -Main Class : "+ mainClass);
	}	   
	
	public Screen getScreen() {
		return screen;
	}   
}

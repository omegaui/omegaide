package depenUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rtextarea.RTextArea;

import ide.Screen;
import ide.utils.ResourceManager;
public class DependencyPanel extends JPanel{
	private String type;
	private RTextArea pathArea;
	public static final Font font = new Font("Ubuntu Mono", Font.BOLD, 14);
	private class ActionPanel extends JComponent{
		public ActionPanel(String head, Runnable addAction, Runnable rmAction){
			setLayout(null);
			setPreferredSize(new Dimension(600, 40));
			JButton headBtn = new JButton(head);
			headBtn.setBounds(0, 0, 520, 40);
			headBtn.setEnabled(false);
			ide.utils.UIManager.setData(headBtn);
			headBtn.setFont(font);
			add(headBtn);

			JButton addBtn = new JButton("+");
			addBtn.addActionListener((e)->addAction.run());
			addBtn.setBounds(520, 0, 30, 40);
			ide.utils.UIManager.setData(addBtn);
			addBtn.setFont(font);
			add(addBtn);

			JButton rmBtn = new JButton("-");
			rmBtn.addActionListener((e)->rmAction.run());
			rmBtn.setBounds(550, 0, 30, 40);
			ide.utils.UIManager.setData(rmBtn);
			rmBtn.setFont(font);
			add(rmBtn);
		}
	}
	public DependencyPanel(String type){
		super(new BorderLayout());
		this.type = type;
		ide.utils.UIManager.setData(this);
		ActionPanel actionPanel = null;
		final Runnable RM = ()->{
			final int INDEX = pathArea.getCaretLineNumber();
			LinkedList<String> paths = new LinkedList<>();
			StringTokenizer tokenizer = new StringTokenizer(pathArea.getText(), "\n");
			int i = 0;
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				String path = token.substring(token.indexOf('|') + 1).trim();
				if(i != INDEX)
					paths.add(token);
				else {
					if(type.equals("library")) {
						boolean r = Screen.getFileView().getDependencyManager().dependencies.remove(path);
						System.out.println(r);
						Screen.getFileView().getDependencyManager().saveFile();
						Screen.getFileView().getDependencyManager().loadFile();
					}
					else if(type.equals("natives")) {
						Screen.getFileView().getNativesManager().natives.remove(path);
						Screen.getFileView().getNativesManager().saveFile();
						Screen.getFileView().getNativesManager().loadFile();
					}
					else {
						ResourceManager.roots.remove(path);
						Screen.getFileView().getResourceManager().saveData();
						Screen.getFileView().getResourceManager().loadFile();
					}
				}
				i++;
			}
			pathArea.setText("");
			paths.forEach(p->append(p));
		};
		final JFileChooser FCX = new JFileChooser();
		FCX.setMultiSelectionEnabled(true);
		FCX.setApproveButtonText("Add");
		if(type.equals("library")){
			FCX.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "Select Jar File(s) (*.jar)";
				}

				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) return true;
					else if(f.getName().endsWith(".jar")) return true;
					return false;
				}
			});
			FCX.setDialogTitle("Choose Jar to Add to Project Class-Path");
			FCX.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			actionPanel = new ActionPanel("Manage Libraries",  ()->{
				int res = FCX.showOpenDialog(this);
				if(res == JFileChooser.APPROVE_OPTION) {
					for(File file : FCX.getSelectedFiles()) {
						append(file.getName()+" | "+file.getAbsolutePath());
						Screen.getFileView().getDependencyManager().add(file.getAbsolutePath());
					}
					Screen.getFileView().getDependencyManager().saveFile();
					Screen.getFileView().getDependencyManager().loadFile();
				}
			}, RM);
		}
		else if(type.equals("natives")){
			FCX.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "Select a Directory";
				}

				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) return true;
					return false;
				}
			});
			FCX.setDialogTitle("Choose Native Parent-Folder to add Project Class-Path");
			FCX.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			actionPanel = new ActionPanel("Manage Natives",  ()->{
				int res = FCX.showOpenDialog(this);
				if(res == JFileChooser.APPROVE_OPTION) {
					for(File file : FCX.getSelectedFiles()) {
						append(file.getName()+" | "+file.getAbsolutePath());
						Screen.getFileView().getNativesManager().add(file.getAbsolutePath());
					}
					Screen.getFileView().getNativesManager().saveFile();
					Screen.getFileView().getNativesManager().loadFile();
				}
			}, RM);
		}
		else{
			FCX.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "Select a Directory";
				}

				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) return true;
					return false;
				}
			});
			FCX.setDialogTitle("Choose a directory to add Project Resource-Path");
			FCX.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			actionPanel = new ActionPanel("Manage Resources",  ()->{
				int res = FCX.showOpenDialog(this);
				if(res == JFileChooser.APPROVE_OPTION) {
					for(File file : FCX.getSelectedFiles()) {
						append(file.getName()+" | "+file.getAbsolutePath());
						Screen.getFileView().getResourceManager().add(file.getAbsolutePath());
					}
					Screen.getFileView().getResourceManager().saveData();
					Screen.getFileView().getResourceManager().loadFile();
				}
			}, RM);
		}
		add(actionPanel, BorderLayout.NORTH);

		pathArea = new RTextArea("");
		ide.utils.UIManager.setData(pathArea);
		pathArea.setEditable(false);
		pathArea.setFont(font);
		add(new JScrollPane(pathArea), BorderLayout.CENTER);
	}
	
	public void read() {
		if(Screen.getFileView() == null || Screen.getFileView().getProjectManager() == null) return;
		LinkedList<String> paths = null;
		if(type.equals("library"))
			paths = Screen.getFileView().getDependencyManager().dependencies;
		else if(type.equals("natives"))
			paths = Screen.getFileView().getNativesManager().natives;
		else
			paths = ResourceManager.roots;
		pathArea.setText("");
		paths.forEach(p->append(p.substring(p.lastIndexOf('/') + 1) + " | " + p));
	}

	public void append(String text){
		pathArea.append(text+"\n");
	}
}

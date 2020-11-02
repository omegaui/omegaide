package ui;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import ide.Screen;
import ide.utils.DataManager;
public class ProjectPanel extends JPanel{
	public static Box sdkField;
	public static String lastPath = null;
	public ProjectPanel(View view){
		setLayout(null);
        ide.utils.UIManager.setData(this);

		SDKSelector sdkSelector = new SDKSelector(Screen.getScreen());
		FileFilter dependencyFilter = new FileFilter() {

			@Override
			public String getDescription() {
				return "for .jar select file and for .dll select containing folder";
			}

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				String path = f.getAbsolutePath();
				if(path.endsWith(".jar") || path.endsWith(".dll"))
					return true;
				return false;
			}
		};
		FileFilter resourceFilter = new FileFilter() {

			@Override
			public String getDescription() {
				return "Select Resource-Root (containing folder)";
			}

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				return false;
			}
		};
		lastPath = Screen.getFileView().getProjectManager() != null ? Screen.getFileView().getProjectManager().jdkPath : null;
		JFileChooser fileChooser = new JFileChooser();
		sdkField = new Box("Choose a project jdk(Right Click)", ()->{});
		sdkField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3) {
					if(DataManager.getPathToJava() == null || !new File(DataManager.getPathToJava()).exists()) {
						fileChooser.setMultiSelectionEnabled(false);
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fileChooser.setDialogTitle("Choose Path to the folder contaning the jdks");
						fileChooser.setApproveButtonText("Select");
						int res = fileChooser.showOpenDialog(view);
						if(res == JFileChooser.APPROVE_OPTION) {							
							IDEPanel.sdkField.setText(fileChooser.getSelectedFile().getAbsolutePath());
							DataManager.setPathToJava(IDEPanel.sdkField.getText());
						}
					}
					sdkSelector.setVisible(true);
					String sel = sdkSelector.getSelection();
					if(sel != null) {
						if(Screen.getFileView().getProjectManager() != null) {
							lastPath = sel;
							sdkField.setText(sel);
						}
					}
				}
			}
		});
		sdkField.setBounds(1, 2, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(sdkField);

		JFileChooser fileC = fileChooser;
		Box addLib = new Box("Add Dependencies", ()->{
			fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()));
			fileC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileC.setApproveButtonText("Add");
			fileC.setFileFilter(dependencyFilter);
			fileC.setMultiSelectionEnabled(true);
			
			int result = fileC.showOpenDialog(view);
			if(result == JFileChooser.APPROVE_OPTION) {
				for(File f : fileC.getSelectedFiles()) {
					String c = f.getAbsolutePath();
					String res = "";
					StringTokenizer tokenizer = new StringTokenizer(c, "\\");
					while(tokenizer.hasMoreTokens())
					{
						res+=tokenizer.nextToken()+"/";
					}
					res = res.substring(0, res.length() - 1);
					if(!f.isDirectory())
						Screen.getFileView().getDependencyManager().add(res);
					else
						Screen.getFileView().getNativesManager().add(res);
				}
				Screen.getFileView().getDependencyManager().saveFile();
				Screen.getFileView().getNativesManager().saveFile();
				Screen.getFileView().getDependencyManager().loadFile();
				Screen.getFileView().getNativesManager().loadFile();
				Screen.setStatus("Reading Libraries", 40);
				Screen.getFileView().getScreen().getToolPane().initEditorTools();
				Screen.getProjectView().getProjectView().setVisible(true);
			}
		});
		addLib.setBounds(1, sdkField.getY() + sdkField.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(addLib);

		Box vLib = new Box("View/Remove Dependencies", ()->{
			Screen.getFileView().getDependencyView().setVisible(true);
		});
		vLib.setBounds(1, addLib.getY() + addLib.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(vLib);
		
		Box addRes = new Box("Add Resources", ()->{
			fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()));
			fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileC.setAcceptAllFileFilterUsed(false);
			fileC.setApproveButtonText("Add");
			fileC.setFileFilter(resourceFilter);
			fileC.setMultiSelectionEnabled(true);

			int result = fileC.showOpenDialog(Screen.getScreen());
			if(result == JFileChooser.APPROVE_OPTION) {
				for(File f : fileC.getSelectedFiles()) {
					Screen.getFileView().getResourceManager().add(f.getAbsolutePath());
				}
				Screen.getFileView().getResourceManager().saveData();
			}
		});
		addRes.setBounds(1, vLib.getY() + vLib.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(addRes);

		Box vRes = new Box("View/Remove Resource Directories", ()->{
			if(Screen.getFileView().getProjectPath() != null)
				Screen.getProjectView().resourceView.setVisible(true);
		});
		vRes.setBounds(1, addRes.getY() + addRes.getHeight() + 1, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(vRes);

		Box javaBuildPathBox = new Box("From Where to Add Run/Compile time Arguments?", ()->{
			TabPanel.build_path.selected = true;
			view.repaint();
			view.showPanel("BA");
		});
		javaBuildPathBox.setBounds(1, view.getHeight() - 31, view.getWidth() - view.getWidth() / 3 - 2, 30);
		add(javaBuildPathBox);
	}
}

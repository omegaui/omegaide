package creator;
import static creator.ProjectWizard.addHoverEffect;
import static creator.ProjectWizard.createSRCFile;
import static creator.ProjectWizard.setData;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import ide.Screen;
import ide.utils.systems.EditorTools;
import importIO.ImportManager;
public class FileWizard extends JDialog{
	protected JButton typeBtn;
	protected JButton parentRoot;
	public FileWizard(JFrame f){
		super(f, true);
		setLayout(null);
		setUndecorated(true);
		setSize(400, 160);
		setLocationRelativeTo(null);
		setResizable(false);
		init();
	}

	private void init(){
		JTextField nameField = new JTextField();
		addHoverEffect(nameField, "Enter name of the File or Source");
		nameField.setBounds(0, 0, getWidth() - 40, 40);
		add(nameField);

		final JFileChooser fileC = new JFileChooser();
		fileC.setMultiSelectionEnabled(false);
		fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileC.setApproveButtonText("Select");
		fileC.setDialogTitle("Select a directory as source root");
		parentRoot = new JButton(":");
		parentRoot.addActionListener((e)->{
			int res = fileC.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION){
				parentRoot.setToolTipText(fileC.getSelectedFile().getAbsolutePath());
			}
		});
		parentRoot.setBounds(nameField.getWidth(), 0, 40, 40);
		add(parentRoot);

		typeBtn = new JButton("class");
		typeBtn.setBounds(0, nameField.getHeight(), getWidth(), 40);
		final JPopupMenu popup = new JPopupMenu();
		final JMenuItem classItem = new JMenuItem("Class");
		final JMenuItem interItem = new JMenuItem("Interface");
		final JMenuItem annoItem = new JMenuItem("Annotaion");
		final JMenuItem enumItem = new JMenuItem("Enum");
		final JMenuItem fileItem = new JMenuItem("Custom File");
		classItem.addActionListener((e)->typeBtn.setText("class"));
		interItem.addActionListener((e)->typeBtn.setText("interface"));
		annoItem.addActionListener((e)->typeBtn.setText("@interface"));
		enumItem.addActionListener((e)->typeBtn.setText("enum"));
		fileItem.addActionListener((e)->typeBtn.setText("Custom File"));
		popup.add(classItem);
		popup.add(interItem);
		popup.add(annoItem);
		popup.add(enumItem);
		popup.add(fileItem);
		popup.setInvoker(typeBtn);
		typeBtn.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				popup.setLocation(e.getXOnScreen(), e.getYOnScreen());
				popup.setVisible(true);
			}
		});
		add(typeBtn);

		JButton packBtn = new JButton("Create Only Package");
		packBtn.setBounds(0, typeBtn.getY() + typeBtn.getHeight(), getWidth(), 40);
		packBtn.addActionListener((e)->{
			String path = nameField.getText();
			StringTokenizer tokenizer = new StringTokenizer(path, ".");
			path = parentRoot.getToolTipText() + "/";
			while(tokenizer.hasMoreTokens()){
				path += tokenizer.nextToken() + "/";
				File file = new File(path);
				if(!file.exists())
					file.mkdir();
				else{
					nameField.setText("Package Already Exists");
					return;
				}
			}
			setVisible(false);
		});
		add(packBtn);

		JButton cancelBtn = new JButton("Close");
		cancelBtn.setBounds(0, packBtn.getY() + packBtn.getHeight(), getWidth()/2, 40);
		cancelBtn.addActionListener((e)->setVisible(false));
		setData(cancelBtn);
		add(cancelBtn);

		JButton createBtn = new JButton("Create");
		createBtn.setBounds(getWidth()/2, packBtn.getY() + packBtn.getHeight(), getWidth()/2, 40);
		createBtn.addActionListener((e)->{
			if(parentRoot.getToolTipText() == null) return;
			if(!new File(parentRoot.getToolTipText()).exists()) {
				nameField.setText("The Root Directory Does not exists");
				return;
			}
			String type = typeBtn.getText();
			if(!type.equals("Custom File")){
				String text = nameField.getText();
				if(!text.contains(".")){
					nameField.setText("Specify a package as pack.Class");
					return;
				}
				final String PATH = text.substring(0, text.lastIndexOf('.'));
				String path = PATH;
				StringTokenizer tokenizer = new StringTokenizer(path, ".");
				path = parentRoot.getToolTipText() + "/";
				while(tokenizer.hasMoreTokens()){
					path += tokenizer.nextToken() + "/";
					File file = new File(path);
					if(!file.exists())
						file.mkdir();
				}
				File src = new File(path+text.substring(text.lastIndexOf('.') + 1)+".java");
				createSRCFile(src, type, PATH, text.substring(text.lastIndexOf('.') + 1));
				ImportManager.readSource(EditorTools.importManager);
			}else{
				File file = new File(parentRoot.getToolTipText() + "/" + nameField.getText());
				if(!file.exists()){
					try{
						file.createNewFile();
						Screen.getProjectView().reload();
					}catch(Exception ex){nameField.setText("Access Denied");}
				}
				else
					nameField.setText("File Already Exists");
			}
		});
		setData(createBtn);
		add(createBtn);
	}

	public void show(String type){
		typeBtn.setText(type);
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			parentRoot.setToolTipText(Screen.getFileView().getProjectPath()+"/src");
		setVisible(true);
	}

	@Override
	public Component add(Component c){
		super.add(c);
		setData(c);
		return c;
	}
}

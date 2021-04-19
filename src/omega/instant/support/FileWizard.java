package omega.instant.support;
import java.awt.Color;
import omega.comp.NoCaretField;
import omega.popup.*;
import omega.tabPane.IconManager;
import omega.Screen;
import java.awt.event.MouseEvent;
import omega.utils.UIManager;
import java.awt.Component;
import java.util.StringTokenizer;
import java.io.File;
import java.awt.event.MouseAdapter;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JFrame;
import omega.comp.TextComp;
import javax.swing.JDialog;

import static omega.utils.UIManager.*;
import static omega.instant.support.ProjectWizard.addHoverEffect;
import static omega.instant.support.ProjectWizard.createSRCFile;
import static omega.instant.support.ProjectWizard.setData;

public class FileWizard extends JDialog{
	public TextComp parentRoot;
	public TextComp typeBtn;
	public FileWizard(JFrame f){
		super(f, true);
		setLayout(null);
		setUndecorated(true);
		setSize(400, 120);
		setLocationRelativeTo(null);
		setResizable(false);
		init();
	}

	private void init(){
		NoCaretField nameField = new NoCaretField("", "type file name", UIManager.isDarkMode() ? c1 : Color.BLACK, c2, c3);
		nameField.setForeground(UIManager.c3);
		nameField.setToolTipText("Enter name of the File or Source");
		nameField.setBounds(0, 0, getWidth() - 40, 40);
		add(nameField);
          addKeyListener(nameField);

		final JFileChooser fileC = new JFileChooser();
		fileC.setMultiSelectionEnabled(false);
		fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileC.setApproveButtonText("Select");
		fileC.setDialogTitle("Select a directory as source root");
          
		parentRoot = new TextComp(":", c1, c3, c2, ()->{
			int res = fileC.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION){
				parentRoot.setToolTipText(fileC.getSelectedFile().getAbsolutePath());
			}
		});
          parentRoot.setArc(0, 0);
		parentRoot.setBounds(nameField.getWidth(), 0, 40, 40);
		add(parentRoot);

		final OPopupWindow popup = new OPopupWindow("File-Type Menu", this, 0, false).width(210);
		typeBtn = new TextComp("class", c1, c2, c3, ()->{});
		typeBtn.setBounds(0, nameField.getHeight(), getWidth(), 40);
          popup.createItem("Directory", IconManager.projectImage, ()->typeBtn.setText("directory"))
          .createItem("Class", IconManager.classImage, ()->typeBtn.setText("class"))
          .createItem("Record", IconManager.recordImage, ()->typeBtn.setText("record"))
          .createItem("Interface", IconManager.interImage, ()->typeBtn.setText("interface"))
          .createItem("Annotation", IconManager.annImage, ()->typeBtn.setText("@interface"))
          .createItem("Enum", IconManager.enumImage, ()->typeBtn.setText("enum"))
          .createItem("Custom File", IconManager.fileImage, ()->typeBtn.setText("Custom File"));
		typeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popup.setLocation(e.getXOnScreen(), e.getYOnScreen());
				popup.setVisible(true);
			}
		});
          typeBtn.setArc(0, 0);
		add(typeBtn);

		TextComp cancelBtn = new TextComp("Close", c1, c2, c3, ()->setVisible(false));
		cancelBtn.setBounds(0, getHeight() - 40, getWidth()/2, 40);
		setData(cancelBtn);
          cancelBtn.setArc(0, 0);
		add(cancelBtn);

		TextComp createBtn = new TextComp("Create", c1, c2, c3, ()->{
			if(parentRoot.getToolTipText() == null) return;
			if(!new File(parentRoot.getToolTipText()).exists()) {
				nameField.setText("The Root Directory Does not exists");
				return;
			}
			String type = typeBtn.getText();
               if(type.equals("directory")){
                    File dir = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
                    dir.mkdir();
                    Screen.getProjectView().reload();
                    return;
               }
			if(!type.equals("Custom File")){
				String text = nameField.getText();
				if(!text.contains(".")){
					nameField.setText("Specify a package as pack.Class");
					return;
				}
				final String PATH = text.substring(0, text.lastIndexOf('.'));
				String path = PATH;
				StringTokenizer tokenizer = new StringTokenizer(path, ".");
				path = parentRoot.getToolTipText() + File.separator;
				while(tokenizer.hasMoreTokens()){
					path += tokenizer.nextToken() + File.separator;
					File file = new File(path);
					if(!file.exists())
						file.mkdir();
				}
				File src = new File(path+text.substring(text.lastIndexOf('.') + 1)+".java");
				createSRCFile(src, type, PATH, text.substring(text.lastIndexOf('.') + 1));
			}else{
				File file = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
				if(!file.exists()){
					try{
						file.createNewFile();
						Screen.getProjectView().reload();
                              Screen.getScreen().loadFile(file);
					}catch(Exception ex){nameField.setText("Access Denied");}
				}
				else
					nameField.setText("File Already Exists");
			}
		});
		createBtn.setBounds(getWidth()/2, getHeight() - 40, getWidth()/2, 40);
		setData(createBtn);
          createBtn.setArc(0, 0);
		add(createBtn);
	}

	public void show(String type){
		typeBtn.setText(type);
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			parentRoot.setToolTipText(Screen.getFileView().getProjectPath() + File.separator + "src");
		setVisible(true);
	}

	@Override
	public Component add(Component c){
		super.add(c);
		setData(c);
		return c;
	}
}

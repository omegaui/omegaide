package ui;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import ide.Screen;
import ide.utils.DataManager;

public class TabPanel extends JPanel{
	private View view;
	private Switch code_assist;
	private Switch project;
	private Switch ideSwitch;
	public static Switch build_path;
	private CodeAssistPanel codeAssistPanel;
	private ProjectPanel projectPanel;
	private IDEPanel idePanel;
	private PathPanel pathPanel;
	private Box apply;
	private static final Font font = new Font("Ubuntu Mono", Font.BOLD, 26);
	public TabPanel(View view){
		this.view = view;
		setLayout(null);
		setPreferredSize(new Dimension((view.getWidth() / 3) + 10, view.getHeight()));
		setSize(getPreferredSize());
        ide.utils.UIManager.setData(this);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				codeAssistPanel.repaint();
				projectPanel.repaint();
				idePanel.repaint();
				pathPanel.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				codeAssistPanel.repaint();
				projectPanel.repaint();
				idePanel.repaint();
				pathPanel.repaint();
			}
		});
		init();
	}

	private void init(){
		apply = new Box("Apply ", ()->{
			new Thread(()->{
				apply.setText("Appling");
				try {
					//Code Assist Panel
					DataManager.setContentAssistRealTime(CodeAssistPanel.showHintsBox.selected);
					DataManager.setUseStarImports(CodeAssistPanel.starImports.selected);
					//Project
					if(Screen.getFileView().getProjectManager() != null)
						Screen.getFileView().getProjectManager().setJDKPath(ProjectPanel.lastPath);
					//Arguments
					if(Screen.getFileView().getProjectManager() != null) {
						Screen.getFileView().getProjectManager().compile_time_args = PathPanel.cargPane.getText();
						Screen.getFileView().getProjectManager().run_time_args = PathPanel.rargPane.getText();
					}
					View.refresh();
					apply.setText("Apply ");
				}catch(Exception e) {}
			}).start();
		});
		apply.setBounds(getWidth()/2 - 2, view.getHeight() - 41 - 30, getWidth()/2 - 2 + 4, 41);
		add(apply);

		Box discard = new Box("Discard ", ()->{
			View.refresh();
		});
		discard.setBounds(0, view.getHeight() - 41 - 30, getWidth()/2 - 2, 41);
		add(discard);

		Box close = new Box("Close ", ()->{
			Screen.getFileView().getScreen().getDataManager().saveData();
			Screen.getFileView().getScreen().getUIManager().save();
			if(Screen.getFileView().getProjectManager() != null)
				Screen.getFileView().getProjectManager().save();
			view.setVisible(false);
		});
		close.setBounds(0, view.getHeight() - 30, getWidth() , 30);
		add(close);

		codeAssistPanel = new CodeAssistPanel(view);
		view.addPanel("CA", codeAssistPanel);

		projectPanel = new ProjectPanel(view);
		view.addPanel("PA", projectPanel);

		idePanel = new IDEPanel(view);
		view.addPanel("IA", idePanel);

		pathPanel = new PathPanel(view);
		view.addPanel("BA", pathPanel);

		code_assist = new Switch("Code Assist", ()->{
			view.showPanel("CA");
			codeAssistPanel.repaint();
		});
		code_assist.selected = true;
		code_assist.setBounds(0, view.getHeight() / 2 - 80 - 5, getWidth(), 40);
		add(code_assist);

		project = new Switch("Project", ()->{
			if(Screen.getFileView().getProjectPath() == null) {
				project.selected = false;
				code_assist.selected = true;
				view.showPanel("CA");
				projectPanel.repaint();
				codeAssistPanel.repaint();
				project.repaint();
				code_assist.repaint();
				return;
			}
			view.showPanel("PA");
			projectPanel.repaint();
		});
		project.setBounds(0, code_assist.getY() + code_assist.getHeight(), getWidth(), 40);
		add(project);

		ideSwitch = new Switch("IDE", ()->{
			view.showPanel("IA");
			idePanel.repaint();
		});
		ideSwitch.setBounds(0, project.getY() + project.getHeight(), getWidth(), 40);
		add(ideSwitch);

		build_path = new Switch("Arguments", ()->{
			view.showPanel("BA");
		});
		build_path.setBounds(0, ideSwitch.getY() + ideSwitch.getHeight(), getWidth(), 40);
		add(build_path);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(view.getIconImages().get(0), 0, 0, 64, 64 ,null);
        g.setColor(getForeground());
		g2.fillRect(64, 0, getWidth() - 64, 64);
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.setFont(font);
        g.setColor(getBackground());
		g2.drawString("Preferences", 65, 32);
	}

	@Override
	public void setVisible(boolean value){
		if(value){
			codeAssistPanel.repaint();
			projectPanel.repaint();
			idePanel.repaint();
			pathPanel.repaint();
		}
		super.setVisible(value);
	}
}

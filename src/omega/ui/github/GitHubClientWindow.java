package omega.ui.github;
import com.jediterm.terminal.emulator.ColorPalette;

import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;

import java.lang.ProcessBuilder;

import omega.ui.component.jediterm.JetTerminal;

import omega.Screen;
import omega.IDE;

import java.awt.geom.RoundRectangle2D;

import omega.io.IconManager;

import java.io.File;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class GitHubClientWindow extends JDialog{

	public TextComp githubIconComp;

	public TextComp titleComp;

	public TextComp statusComp;

	public TextComp closeComp;

	public TextComp actionsLabelComp;

	public TextComp gitInitComp;
	public TextComp gitAddRemoteURLComp;
	public TextComp gitAddComp;
	public TextComp gitCommitComp;
	public TextComp gitPushComp;
	public TextComp gitGenBranchComp;
	public TextComp gitSwitchBranchComp;

	public File dir;

	public Screen screen;

	public JetTerminal jetTerminal;

	public volatile boolean gitInstalled = false;

	public GitTermSettingsProvider gitTermSettingsProvider;

	public static final String[] CHECK_GIT_VERSION_COMMAND = new String[]{
		"git", "--version"
	};

	public GitHubClientWindow(Screen screen){
		super(screen, false);
		setTitle("Minimal GitHub Client");
		setUndecorated(true);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);

		init();
	}

	public void init(){
		gitTermSettingsProvider = new GitTermSettingsProvider();
		
		githubIconComp = new TextComp(IconManager.fluentgithubLogo, 120, 120, c2, c2, c2, null);
		githubIconComp.setBounds(10, 10, 120, 120);
		githubIconComp.setArc(5, 5);
		githubIconComp.setClickable(false);
		githubIconComp.attachDragger(this);
		add(githubIconComp);

		titleComp = new TextComp("Minimal GitHub Client (fatal : incomplete)", c2, c2, TOOLMENU_COLOR2, null);
		titleComp.setBounds(130, 50, getWidth() - 20 - 130, 30);
		titleComp.setFont(PX20);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		titleComp.setGradientColor(TOOLMENU_COLOR1);
		titleComp.setPaintTextGradientEnabled(true);
		add(titleComp);

		statusComp = new TextComp("", c2, c2, glow, null);
		statusComp.setBounds(0, getHeight() - 25, getWidth(), 25);
		statusComp.setFont(PX14);
		statusComp.setArc(0, 0);
		statusComp.setClickable(false);
		statusComp.setTextAlignment(TextComp.TEXT_ALIGNMENT_LEFT);
		statusComp.setHighlightColor(TOOLMENU_COLOR1);
		statusComp.addHighlightText("git", "commit", "github", "push", "add", "NEW", "Git", "GitHub");
		add(statusComp);

		closeComp = new TextComp("Close", TOOLMENU_COLOR1_SHADE, TOOLMENU_GRADIENT, glow, ()->{
			if(jetTerminal != null && jetTerminal.process != null){
				if(jetTerminal.process.isAlive())
					jetTerminal.process.destroyForcibly();
			}
			dispose();
		});
		closeComp.setBounds(getWidth() - 100, getHeight() - 50, 80, 25);
		closeComp.setFont(PX14);
		closeComp.setArc(5, 5);
		add(closeComp);

		actionsLabelComp = new TextComp("Git Actions", c2, c2, glow, null);
		actionsLabelComp.setBounds(10, 140, 100, 25);
		actionsLabelComp.setFont(PX14);
		actionsLabelComp.setArc(0, 0);
		actionsLabelComp.setClickable(false);
		add(actionsLabelComp);

		gitInitComp = new TextComp("git init", "Initialize Git Here!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitInit);
		gitInitComp.setBounds(10, 170, 100, 25);
		gitInitComp.setFont(PX14);
		gitInitComp.setArc(5, 5);
		add(gitInitComp);

		gitAddRemoteURLComp = new TextComp("git setup", "Link Your Local Repo with Your GitHub account!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitAddRemoteURL);
		gitAddRemoteURLComp.setBounds(10, 200, 100, 25);
		gitAddRemoteURLComp.setFont(PX14);
		gitAddRemoteURLComp.setArc(5, 5);
		add(gitAddRemoteURLComp);

		gitAddComp = new TextComp("git add .", "Add All NEW Files!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitAdd);
		gitAddComp.setBounds(10, 230, 100, 25);
		gitAddComp.setFont(PX14);
		gitAddComp.setArc(5, 5);
		add(gitAddComp);

		gitCommitComp = new TextComp("git commit", "Commit Changes!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitCommit);
		gitCommitComp.setBounds(10, 260, 100, 25);
		gitCommitComp.setFont(PX14);
		gitCommitComp.setArc(5, 5);
		add(gitCommitComp);

		gitPushComp = new TextComp("git push", "Push to GitHub!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitPush);
		gitPushComp.setBounds(10, 290, 100, 25);
		gitPushComp.setFont(PX14);
		gitPushComp.setArc(5, 5);
		add(gitPushComp);

		gitGenBranchComp = new TextComp("new branch", "Create a new Branch!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitNewBranch);
		gitGenBranchComp.setBounds(10, 320, 100, 25);
		gitGenBranchComp.setFont(PX14);
		gitGenBranchComp.setArc(5, 5);
		add(gitGenBranchComp);

		gitSwitchBranchComp = new TextComp("switch branch", "Switch to another existing Branch!", TOOLMENU_COLOR3_SHADE, TOOLMENU_GRADIENT, glow, this::gitSwitchBranch);
		gitSwitchBranchComp.setBounds(10, 350, 100, 25);
		gitSwitchBranchComp.setFont(PX14);
		gitSwitchBranchComp.setArc(5, 5);
		add(gitSwitchBranchComp);
	}

	public void gitInit(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Executing git init ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_init.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitAddRemoteURL(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Setting up GitHub Connection ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_setup_remote.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitAdd(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Adding All NEW Files ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_add.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitCommit(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Executing git commit ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_commit.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitPush(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Executing git push ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_push.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitNewBranch(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Executing git checkout ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_gen_branch.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void gitSwitchBranch(){
		new Thread(()->{
			try{
				hideTerminal();
				setStatus("Executing git branch ...");
				jetTerminal = new JetTerminal(
					new String[]{
						"/home/ubuntu/Documents/Omega IDE/out/git_switch_branch.sh", dir.getAbsolutePath()
					},
					dir.getAbsolutePath(), gitTermSettingsProvider
				);
				jetTerminal.setOnProcessExited(()->{
					setStatus("");
				});
				jetTerminal.start();
				showTerminal();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
	}

	public void setDirectory(File dir){
		this.dir = dir;
		setVisible(true);
	}

	public void setStatus(String text){
		statusComp.setText(text);
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}

	public void checkIsGitInstalled(){
		if(gitInstalled)
			return;
		new Thread(()->{
			setStatus("Checking Git Installation ...");
			try{
				Process p = new ProcessBuilder(CHECK_GIT_VERSION_COMMAND).start();
				while(p.isAlive());
					gitInstalled = p.exitValue() == 0;
				if(!gitInstalled)
					setStatus("Your git installation is broken.");
				else
					setStatus("");
			}
			catch(Exception e){
				setStatus("You must first install git.");
				e.printStackTrace();
			}
		}).start();
	}

	public void showTerminal(){
		jetTerminal.setBounds(130, 130, getWidth() - 140, getHeight() - 130 - 60);
		add(jetTerminal);
		jetTerminal.setVisible(false);
		jetTerminal.setVisible(true);
		repaint();
	}

	public void hideTerminal(){
		if(jetTerminal != null){
			remove(jetTerminal);
			repaint();
		}
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		if(value){
			checkIsGitInstalled();
		}
	}


	public class GitTermSettingsProvider extends DefaultSettingsProvider{

		public static final Font font = new Font("JetBrains Mono", Font.BOLD, 13);

		public static Color[] colors = new Color[16];
		static{
			colors[0] = glow;
			colors[1] = TOOLMENU_COLOR2;
			colors[2] = TOOLMENU_COLOR1;
			colors[3] = TOOLMENU_COLOR5;
			colors[4] = TOOLMENU_COLOR3;
			colors[5] = Color.decode("#FF8C42");
			colors[6] = Color.decode("#118736");
			colors[7] = Color.decode("#D81159");
			colors[8] = Color.decode("#E0777D");
			colors[9] = Color.decode("#8E3B46");
			colors[10] = Color.decode("#A2AD59");
			colors[11] = Color.decode("#92140C");
			colors[12] = Color.decode("#253237");
			colors[13] = Color.decode("#5C6B73");
			colors[14] = Color.decode("#4C2719");
			colors[15] = isDarkMode() ? Color.decode("#242424") : c2;
		}

		@Override
		public Font getTerminalFont() {
			return font;
		}

		@Override
		public float getTerminalFontSize() {
			return 13;
		}

		@Override
		public boolean useInverseSelectionColor() {
			return true;
		}

		@Override
		public ColorPalette getTerminalColorPalette() {
			return new ColorPalette(){
				@Override
				public Color[] getIndexColors(){
					return colors;
				}
			};
		}

		@Override
		public boolean useAntialiasing() {
			return true;
		}

		@Override
		public boolean audibleBell() {
			return true;
		}

		@Override
		public boolean scrollToBottomOnTyping() {
			return true;
		}

	}
}

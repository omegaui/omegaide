package omega.instant.support.build.gradle;
import omega.utils.PrintArea;
import java.util.Scanner;
import omega.utils.Editor;
import omega.utils.UIManager;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.JPanel;
import omega.Screen;
import java.io.File;
public class GradleProcessManager {
	private static PrintArea printArea;
	public static boolean isGradleProject(){
		File settings = new File(Screen.getFileView().getProjectPath(), "settings.gradle");
		return settings.exists();
	}
	public static void init(){
		new Thread(()->{
			try{
				preparePrintArea();
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, ()->printArea.stopProcess());
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : gradle init");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.init(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : gradle init" : "Completed Successfully : gradle init");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void run(){
		new Thread(()->{
			try{
				PrintArea printArea = new PrintArea();
				printArea.launchAsTerminal(GradleProcessManager::run);
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, ()->printArea.stopProcess());
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : gradle run");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.run(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : gradle run" : "Completed Successfully : gradle run");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void build(){
		new Thread(()->{
			try{
				PrintArea printArea = new PrintArea();
				Screen.getScreen().getOperationPanel().addTab("Gradle Task", printArea, ()->printArea.stopProcess());
				if(!Screen.getFileView().getProjectManager().non_java){
					printArea.print("**Changing Project Type ...**");
					Screen.getFileView().getProjectManager().non_java = true;
					Screen.getScreen().manageTools(Screen.getFileView().getProjectManager());
					Screen.getFileView().getProjectManager().save();
					printArea.print("**Changing Project Type ... Done!**");
				}
				printArea.print("# Launching : gradle build");
				printArea.print("--------------------------------------------------");
				Process p = GradleProcessExecutor.build(new File(Screen.getFileView().getProjectPath()));
				printArea.setProcess(p);
				Scanner inputReader = GradleProcessExecutor.getInputScanner(p);
				Scanner errorReader = GradleProcessExecutor.getErrorScanner(p);
				new Thread(()->{
					boolean errorOccured = false;
					while(p.isAlive()){
						while(errorReader.hasNextLine()){
							printArea.print(errorReader.nextLine());
							errorOccured = true;
						}
					}
					errorReader.close();
					printArea.print("--------------------------------------------------");
					printArea.print(errorOccured ? "Error Occured During : gradle build" : "Completed Successfully : gradle build");
				}).start();
				while(p.isAlive()){
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
				}
				inputReader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Screen.getProjectView().reload();
		}).start();
	}
	public static void preparePrintArea(){
		if(printArea == null)
			printArea = new PrintArea();
	}
}

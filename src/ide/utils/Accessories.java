package ide.utils;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import ide.Screen;
import ide.utils.systems.View;

public class Accessories {

	private PrintArea printArea;
	
	public Accessories(Screen screen)
	{
		printArea = new PrintArea("Operation Progress", screen);
	}
	
	public void addDeleteButton(JFileChooser chooser)
	{
		JButton delBtn = new JButton("Delete");
		UIManager.setData(delBtn);
		delBtn.addActionListener((e)->{
			new Thread(()->{

				    File file = chooser.getSelectedFile();
				    String names = file.getName();
				    
				    int del = JOptionPane.showConfirmDialog(chooser, "Do you want to delete "+names+"?", "Delete or not?", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				    
				    if(del == JOptionPane.OK_OPTION)
				    {
					    printArea.setVisible(true);
					    printArea.print("Operation in Progress....");
						    try{
						    	String folder = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('/'));
							    deleteDir(file);
							    printArea.print("Operation Completed!");
							    printArea.requestFocusInWindow();
							    printArea.setVisible(false);
							    chooser.setCurrentDirectory(new File(folder));
						    }catch(Exception e2) {printArea.print(e2.getMessage());}
				    }	
			}).start();
		});
		chooser.setAccessory(delBtn);
	}
	
	public void deleteDir(File file) throws Exception
	{

		if (file.isDirectory())
		{

			/*
			 * If directory is empty, then delete it
			 */
			if (file.list().length == 0)
			{
				deleteEmptyDir(file);
			}
			else
			{
				// list all the directory contents
				File files[] = file.listFiles();

				for (File fileDelete : files)
				{
					/*
					 * Recursive delete
					 */
					deleteDir(fileDelete);
				}

				/*
				 * check the directory again, if empty then 
				 * delete it.
				 */
				if (file.list().length == 0)
				{
					deleteEmptyDir(file);
				}
			}

		}
		else
		{
			/*
			 * if file, then delete it
			 */
			deleteEmptyDir(file);
		}
	}

	private void deleteEmptyDir(File file)
	{
		boolean value = file.delete();
		if(value)
			printArea.print("Successfully Deleted \""+file.getAbsolutePath()+"\"");
		else
			printArea.print("Access is denied, Unable to delete \""+file.getAbsolutePath()+"\"");
	}

	    private class PrintArea extends View {

			private RSyntaxTextArea textArea;
			private JScrollPane p;
			
			public PrintArea(String title, Screen window) 
			{
				super(title, window);
				setModal(false);
				setLayout(new BorderLayout());
				setSize(500,200);
				setLocation(0,0);				
				init();
			}
			
			private void init()
			{
				textArea = new RSyntaxTextArea("list of dependencies");
				textArea.setEditable(false);
				textArea.setAutoscrolls(true);
				textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
				UIManager.setData(textArea);
				p = new JScrollPane(textArea);
				p.setAutoscrolls(true);
				add(p, BorderLayout.CENTER);
				comps.add(textArea);
				
				setAction(()->{
					textArea.setText("------------------------------------------");
					UIManager.setData(PrintArea.this);
					});
			}
			
			public void print(String text)
			{
				textArea.append("\n"+text);
				p.getVerticalScrollBar().setValue(p.getVerticalScrollBar().getMaximum());
			}
			
		}
	
}

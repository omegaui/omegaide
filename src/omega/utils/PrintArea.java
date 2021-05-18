package omega.utils;
import java.io.PrintWriter;
import javax.swing.JTextField;
import omega.comp.TextComp;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JComponent;
import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;
import omega.Screen;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.JPanel;
public class PrintArea extends JPanel {
	private RSyntaxTextArea textArea;
	private Process process;
	private JScrollPane p;
     private PrintWriter writer;
	public PrintArea() {
		setLayout(new BorderLayout());
		setPreferredSize(getSize());
          setBackground(omega.utils.UIManager.c2);
		init();
	}
	private void init() {
		textArea = new RSyntaxTextArea("Build Starting...");
		MarkdownTokenMaker.apply(textArea);
		textArea.setAutoscrolls(true);
		Editor.getTheme().apply(textArea);
		textArea.setFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
		textArea.setHighlightCurrentLine(false);
          
		p = new JScrollPane(textArea);
		p.setAutoscrolls(true);
		add(p, BorderLayout.CENTER);
	}
	public void setProcess(Process p) {
		process = p;
          if(writer != null)
               writer.close();
          writer = new PrintWriter(process.getOutputStream());
	}
	public void stopProcess() {
		if(process != null)
			process.destroyForcibly();
	}
	public String getText() {
		return textArea.getText();
	}
	public synchronized void print(String text) {
		textArea.append("\n" + text);
		p.repaint();
		p.getVerticalScrollBar().setValue(p.getVerticalScrollBar().getMaximum());
	}
	public void clear() {
		textArea.setText("");
	}
	
	@Override
	public void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		textArea.addMouseListener(l);
	}
	
	@Override
	public void setVisible(boolean v) {
		if(v) {
			try {
				Editor.getTheme().apply(textArea);
			}
			catch(Exception e) {
				
			}
		}
		super.setVisible(v);
	}
	public void launchAsTerminal(Runnable action) {
		JTextField inputField = new JTextField();
		inputField.setText("Input? From Here");
		inputField.addActionListener((e)->{
			if(process == null)
				return;
			else if(!process.isAlive())
				return;
			try {
				writer.println(inputField.getText());
				writer.flush();
			}
			catch(Exception e1) {
			     e1.printStackTrace();
		     }
			inputField.setText("");
		});
		inputField.setCaretColor(omega.utils.UIManager.glow);
		inputField.setBackground(omega.utils.UIManager.c2);
		inputField.setForeground(omega.utils.UIManager.glow);
		inputField.setFont(omega.settings.Screen.PX18);
		add(inputField, BorderLayout.SOUTH);
          
		ActionCenter actionCenter = new ActionCenter(()->{
		     stopProcess();
               if(action != null)
                    action.run();
	     } ,()->stopProcess());
		add(actionCenter, BorderLayout.WEST);
	}
	private class ActionCenter extends JComponent{
		protected ActionCenter(Runnable r, Runnable r0) {
               setBackground(omega.utils.UIManager.c2);
			setLayout(new FlowLayout());
			UIManager.setData(this);
			setPreferredSize(new Dimension(40, 100));
			Dimension size = new Dimension(30, 30);
			TextComp runComp = new TextComp(IconManager.fluentrunImage, 25, 25, "Re-Run", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, r);
			runComp.setFont(omega.settings.Screen.PX18);
			runComp.setPreferredSize(size);
			add(runComp);
			
			TextComp clrComp = new TextComp(IconManager.fluentclearImage, 25, 25, "Clear Text", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, ()->textArea.setText(""));
			clrComp.setFont(omega.settings.Screen.PX18);
			clrComp.setPreferredSize(size);
			add(clrComp);
			
			TextComp terComp = new TextComp(IconManager.fluentcloseImage, 25, 25, "Instant Kill", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, r0);
			terComp.setFont(omega.settings.Screen.PX18);
			terComp.setPreferredSize(size);
			add(terComp);
		}
	}
}

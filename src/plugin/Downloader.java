package plugin;
import java.awt.*;
import org.fife.ui.rtextarea.*;
import javax.swing.*;
public class Downloader extends JDialog{
	private RTextArea textArea;
	private static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	public Downloader(PluginStore f){
		super(f, "Downloader");
		setSize(600, 500);
		setLocationRelativeTo(f);
		setIconImage(f.getIconImage());
		setLayout(new BorderLayout());
		setModal(false);
		textArea = new RTextArea();
		textArea.setFont(PX14);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	public void print(String text){
		textArea.append("\n" + text);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

	public boolean isErrorOccured() {
		String text = textArea.getText().substring(textArea.getText().lastIndexOf('\n') + 1);
		return text.contains("wget: unable to resolve host") || text.contains("service not known");
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		if(value)
			textArea.setText("Downloading\n");
	}
}

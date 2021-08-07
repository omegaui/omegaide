package omega.instant.support.java;
import java.awt.Rectangle;
import omega.utils.Editor;
import java.awt.Dimension;
import omega.comp.TextComp;
import omega.comp.FlexPanel;
import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class JavaErrorPanel extends FlexPanel{
	private Editor editor;
	
	private TextComp errorCountComp;
	private TextComp errorLabel;
	private TextComp sep0;
	private TextComp warningCountComp;
	private TextComp warningLabel;
	
	public JavaErrorPanel(Editor editor) {
		super(null, c2, null);
		setPaintBorder(true);
		setBorderColor(TOOLMENU_COLOR2);
		setArc(12, 12);
		this.editor = editor;
		setVisible(false);
		setPreferredSize(new Dimension(220, 40));
		setSize(getPreferredSize());
		init();
		
		editor.getAttachment().getViewport().addChangeListener((e)->relocate());
	}

	public void init(){
		errorCountComp = new TextComp("93", c2, c2, TOOLMENU_COLOR2, null);
		errorCountComp.setBounds(10, 10, 30, getHeight() - 20);
		errorCountComp.setFont(PX14);
		errorCountComp.setClickable(false);
		errorCountComp.setArc(0, 0);
		add(errorCountComp);

		errorLabel = new TextComp("Error(s)", c2, c2, TOOLMENU_COLOR3, null);
		errorLabel.setBounds(errorCountComp.getX() + errorCountComp.getWidth() + 2, 10, 55, getHeight() - 20);
		errorLabel.setFont(PX14);
		errorLabel.setArc(0, 0);
		errorLabel.setClickable(false);
		add(errorLabel);

		sep0 = new TextComp("", TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, null);
		sep0.setBounds(getWidth()/2 - 1, 2, 2, getHeight() - 4);
		add(sep0);

		warningCountComp = new TextComp("2", c2, c2, TOOLMENU_COLOR1, null);
		warningCountComp.setBounds(getWidth()/2 + 1, 10, 30, getHeight() - 20);
		warningCountComp.setFont(PX14);
		warningCountComp.setClickable(false);
		warningCountComp.setArc(0, 0);
		add(warningCountComp);

		warningLabel = new TextComp("Warning(s)", c2, c2, TOOLMENU_COLOR4, null);
		warningLabel.setBounds(warningCountComp.getX() + warningCountComp.getWidth() + 2, 10, 70, getHeight() - 20);
		warningLabel.setFont(PX14);
		warningLabel.setClickable(false);
		warningLabel.setArc(0, 0);
		add(warningLabel);
	}

	public void setDiagnosticData(int errorCount, int warningCount){
		errorCountComp.setText(errorCount + "");
		warningCountComp.setText(warningCount + "");

		if(errorCount == 0 && warningCount == 0){
			setVisible(false);
		}
		else{
			relocate();
			setVisible(true);
		}
	}

	public void relocate(){
		Rectangle rect = editor.getAttachment().getViewport().getViewRect();
		setLocation((rect.x + rect.width) - getWidth() - 20, (rect.y + rect.height) - getHeight() - 15);
	}
}

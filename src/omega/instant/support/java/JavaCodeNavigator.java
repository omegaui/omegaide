package omega.instant.support.java;
import omega.jdk.Import;
import org.fife.ui.rsyntaxtextarea.Token;
import omega.Screen;
import omega.deassembler.DataMember;
import java.io.File;
import omega.jdk.JDKManager;
import omega.framework.CodeFramework;
import omega.deassembler.SourceReader;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import omega.utils.Editor;
public class JavaCodeNavigator implements KeyListener, MouseListener{
	private Editor editor;

	private volatile boolean ctrl;
	
	public JavaCodeNavigator(Editor editor){
		this.editor = editor;
		editor.addMouseListener(this);
		editor.addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		
	}
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
			ctrl = true;
	}
	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
			ctrl = false;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		//Invoking Code Navigation
		if(ctrl){
			Token token = editor.viewToToken(e.getPoint());
			if(isExaminable(token) && editor.currentFile.getName().endsWith(".java")) {
				new Thread(()->{
					try{
						String text = token.getLexeme();
						int offset = token.getOffset();
						if(editor.getText().charAt(token.getEndOffset()) == '(')
							text += "()";
						SourceReader reader = new SourceReader(editor.getText());
						//Checking if the token is a part of an object
						if(editor.getText().charAt(offset - 1) == '.'){
							examineStrangerCode(text, reader, token);
						}
						else {
							examineInEditorCode(text, reader, token);
						}
						//System.out.println(text);
					}
					catch(Exception ex){
						Screen.notify("Code Navigation Encountered an Exception");
					}
				}).start();
			}
		}
	}

	public void examineStrangerCode(String text, SourceReader reader, Token token){
		String code = CodeFramework.getCodeDoNotEliminateDot(editor.getText(), token.getEndOffset());
		if(text.endsWith("()") && !code.endsWith("()"))
			code += "()";
		//Checking if the <base> is a Class
		Import im = null;
		for(Import imj : JDKManager.sources){
			if(imj.toString().equals(code)){
				im = imj;
				break;
			}
		}
		if(im != null){
			String imPath = im.toString();
			if(CodeFramework.isSource(imPath)){
				File file = CodeFramework.getFile(imPath);
				Screen.getScreen().loadFile(file);
			}
		}
		else {
			//Checking if base is a data member
			DataMember d = null;
			for(DataMember dx : reader.dataMembers){
				if(dx.name.equals(text)){
					d = dx;
					break;
				}
			}
			//System.out.println("Code >" + code);
		}
	}

	public void examineInEditorCode(String text, SourceReader reader, Token token){
		//Checking for data members/methods, navigation to local members not included
		DataMember dataMember = null;
		for(DataMember d : reader.dataMembers){
			if(d.name.equals(text)){
				dataMember = d;
				break;
			}
		}
		if(dataMember != null) {
			//Navigating to the DataMember Prototype
			int pos = 0;
			int line = dataMember.lineNumber;
			String wholeText = editor.getText();
			for(char c : wholeText.toCharArray()) {
				if(line <= 0)
					break;
				if(c == '\n')
					line--;
				pos++;
			}
			editor.setCaretPosition(pos - 1);
		}
		else{
			//Checking for Imports
			SourceReader.Import im = null;
			for(SourceReader.Import ix : reader.imports){
				if(ix.name.equals(text)){
					im = ix;
					break;
				}
			}
			if(im != null){
				String imPath = im.toString();
				if(CodeFramework.isSource(imPath)){
					File file = CodeFramework.getFile(imPath);
					Screen.getScreen().loadFile(file);
				}
				else {
					//Nothing Designed for this situation yet!
				}
			}
			else{
				Screen.notify("Couldn't find any matches");
			}
		}
	}

	public boolean isExaminable(Token token){
		return token != null && (token.isIdentifier() || token.getType() == TokenTypes.FUNCTION);
	}

	public static void install(Editor editor){
		if(editor.currentFile != null && editor.currentFile.getName().endsWith(".java")){
			new JavaCodeNavigator(editor);
		}
	}
	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		
	}
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		
	}
	@Override
	public void mouseExited(MouseEvent mouseEvent) {
		
	}
	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
		
	}
}

package contentUI;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JTextField;

import ide.Screen;
import ide.utils.UIManager;
import ide.utils.systems.View;

public class ContentManager extends View{
	
	private static File dataFile = new File(".content");
	public static LinkedList<String> userCodes = new LinkedList<>();
	public static LinkedList<String> codeTypes = new LinkedList<>();
	
	public ContentManager(Screen screen) {
		super("Add New Content", screen);
		setSize(400, 120);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		init();
	}

	private void init() {
		checkFile();
		loadData();
		setUI();
	}
	
	private void setUI() {
		JTextField typeField = new JTextField();
		typeField.setText("Enter type (package path or description)");
		comps.add(typeField);
		
		JTextField codeField = new JTextField();
		codeField.setText("Enter Code (e.g: do{}while();)");
		comps.add(codeField);
		
		JButton addBtn = new JButton("Add to IDE");
		addBtn.addActionListener((e)->{
			String code = codeField.getText();
			String type = typeField.getText();
			if(!addContent(code, type)) {
				codeField.setText("This code already exists.");
			}
			else
				setTitle("Add New Content -Added "+code);
		});
		comps.add(addBtn);
		
		comps.forEach(c->UIManager.setData(c));
		add(addBtn, FlowLayout.LEFT);
		add(typeField, FlowLayout.LEFT);
		add(codeField, FlowLayout.LEFT);
	}
	
	private void loadData() {
		if(!dataFile.exists()) return;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			boolean isCode = true;
			String token;
			while((token = reader.readLine()) != null) {
				if(isCode) {
					userCodes.add(token);
					isCode = false;
				}
				else {
					codeTypes.add(token);
					isCode = true;
				}
			}
			reader.close();
		}catch(Exception e) {e.printStackTrace();}
		//Adding Codes
	}
	
	public static boolean addContent(String code, String type) {
		if(userCodes.indexOf(code) > 0)
			return false;
		userCodes.add(code);
		codeTypes.add(type);
		//Adding Codes
		saveData();
		return true;
	}
	
	private void checkFile() {
		if(!dataFile.exists())
			saveData();
	}
	
	private static void saveData(){
		if(userCodes.isEmpty()) return;
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(dataFile));
			userCodes.forEach(code->{
				writer.println(code);
				writer.println(codeTypes.get(userCodes.indexOf(code)));
			});
			writer.close();
		}catch(Exception e) {e.printStackTrace();}
	}
	
}

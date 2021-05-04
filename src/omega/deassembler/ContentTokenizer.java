package omega.deassembler;
import omega.jdk.Import;
import omega.Screen;
import omega.utils.DataManager;
import omega.framework.CodeFramework;
import omega.utils.Editor;
import java.util.StringTokenizer;
import java.util.LinkedList;
public class ContentTokenizer {
	public static void arrangeTokens(Editor e, String text){
		if(text.equals("")) {
			e.contentWindow.setVisible(false);
			return;
		}
		StringTokenizer tok = new StringTokenizer(e.getText(), "`-=[\\;,.\"\'/]~!@#%^&*()+{}|:<>?)\n ");
		
		LinkedList<DataMember> dataMembers = new LinkedList<>();
		LinkedList<DataMember> tokens = new LinkedList<>();
		while(tok.hasMoreTokens()){
			final String token = tok.nextToken().trim();
			if(token.equals("") || !token.startsWith(text) || token.equals(text)) continue;
				DataMember d = new DataMember("", "", "", token, null){
				@Override
				public String getRepresentableValue(){
					return token;
				}
			};
			tokens.add(d);
		}
		
		ContentWindow.sort(tokens);
		main:
		for(DataMember token : tokens){
			for(DataMember dx : dataMembers){
				if(dx.name.equals(token.name))
					continue main;
			}
			dataMembers.add(token);
		}
		tokens.clear();
		
		if(!dataMembers.isEmpty())
			CodeFramework.gen(dataMembers, e);
		else
		     e.contentWindow.setVisible(false);
	}
	
	public static void arrangeTokens(Editor e) {
		if(!e.currentFile.getName().endsWith(".java") || Screen.getFileView().getProjectManager().non_java || !DataManager.isContentModeJava()){
			arrangeTokens(e, CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition()));
			return;
		}
		String text = CodeFramework.getCodeDoNotEliminateDot(e.getText(), e.getCaretPosition());
          if(text == null || text.equals("")) {
               e.contentWindow.setVisible(false);
               return;
          }
		if(!text.contains(".") || !CodeFramework.think(e, e.getText(), e.getCaretPosition())) {
			SourceReader reader = new SourceReader(e.getText());
			LinkedList<DataMember> dataMembers = new LinkedList<>();
			//Searching whether you need Class names as suggestion
			LinkedList<SourceReader.Import> imports = new LinkedList<>();
			main:
			for(SourceReader.Import im : reader.imports){
				if(!im.isStatic && im.name.startsWith(text)){
					for(SourceReader.Import x : imports){
						if(x.get().equals(im.get()))
							continue main;
					}
					imports.add(im);
				}
			}
			
			SourceReader.Import[] I = new SourceReader.Import[imports.size()];
			int k = 0;
			for(SourceReader.Import im : imports)
				I[k++] = im;
			imports.clear();
			for(int i = 0; i < k; i++){
				for(int j = 0; j < k - i - 1; j++){
					String x = I[j].name;
					String y = I[j + 1].name;
					if(x.compareTo(y) > 0){
						SourceReader.Import ix = I[j];
						I[j] = I[j + 1];
						I[j + 1] = ix;
					}
				}
			}
			for(SourceReader.Import im : I)
				imports.add(im);
			I = null;
			for(SourceReader.Import im : imports){
				dataMembers.add(new DataMember("", "", im.get(), im.name, null));
			}
			
			for(DataMember m : reader.dataMembers) {
				if(m.name.startsWith(text)) {
					dataMembers.add(m);
				}
			}
			String reducedText = e.getText().substring(0, e.getCaretPosition());
			reducedText = CodeFramework.completeCode(reducedText);
			reader = new SourceReader(reducedText);
			DataBlock block = null;
               if(!reader.dataBlocks.isEmpty())
                    block = reader.dataBlocks.getLast();
			if(!reader.recordingInternal) {
				if(block != null) {
					for(DataMember m : block.depthMembers) {
						if(m.name.startsWith(text)) {
							dataMembers.add(m);
						}
					}
				}
			}
			if(!dataMembers.isEmpty())
				CodeFramework.gen(dataMembers, e);
			else {
     			e.contentWindow.setVisible(false);
			}
		}
	}
}

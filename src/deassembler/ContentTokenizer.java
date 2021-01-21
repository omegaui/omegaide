package deassembler;

import java.util.LinkedList;

import ide.utils.Editor;

public class ContentTokenizer {
	public static void arrangeTokens(Editor e) {
		String text = CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition());
		if(text != null && !text.trim().equals("\n") && !text.trim().equals("")) {
			SourceReader reader = new SourceReader(e.getText());
               LinkedList<DataMember> dataMembers = new LinkedList<>();
               //Searching whether you need Class names as suggestion
               LinkedList<SourceReader.Import> imports = new LinkedList<>();
               for(SourceReader.Import im : reader.imports){
                    if(!im.isStatic && im.name.startsWith(text)){
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
			DataBlock block = reader.dataBlocks.getLast();
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
			else 
			     e.contentWindow.setVisible(false);
		}
		else {
			if(!CodeFramework.think(e, e.getText(), e.getCaretPosition()))
				e.contentWindow.setVisible(false);
		}
	}
}

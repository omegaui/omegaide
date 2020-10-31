package deassembler;

import java.util.LinkedList;

import ide.utils.Editor;

public class ContentTokenizer {
	public static void arrangeTokens(Editor e) {
		String text = CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition());
		if(text != null && !text.trim().equals("\n") && !text.trim().equals("")) {
			SourceReader reader = new SourceReader(e.getText());
			LinkedList<DataMember> dataMembers = new LinkedList<>();
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
			else e.contentWindow.setVisible(false);
		}
		else {
			if(!CodeFramework.think(e, e.getText(), e.getCaretPosition()))
				e.contentWindow.setVisible(false);
		}
	}
}

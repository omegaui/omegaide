package deassembler;
/*
    The pace maker of content assist
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import ide.utils.DataManager;
import ide.Screen;

import java.util.StringTokenizer;
import java.util.LinkedList;

import ide.utils.Editor;

public class ContentTokenizer {
     public static void arrangeTokens(Editor e, String text){
          if(text.equals("")) {
               e.contentWindow.setVisible(false);
               return;
          }
     	StringTokenizer tok = new StringTokenizer(e.getText(), "`-=[\\;\',./]~!@#%^&*()+{}|:\"<>?)\n ");
          
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
          if(Screen.getFileView().getProjectManager().non_java || !DataManager.isContentModeJava()){
               arrangeTokens(e, CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition()));
               return;
          }
		String text = CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition());
		if(text != null && !text.trim().equals("\n") && !text.trim().equals("")) {
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

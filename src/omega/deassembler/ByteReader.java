package omega.deassembler;
import omega.jdk.Import;
import omega.jdk.JDKManager;
import omega.utils.*;
import java.io.*;
import java.util.*;
public class ByteReader {
	private String code;
	public String pack;
	public String access;
	public String modifier;
	public String className;
	public String type;
	public String parent = "java.lang.Object";
	public String[] features;
	public LinkedList<DataMember> dataMembers = new LinkedList<>();
	public LinkedList<ByteReader> internalReaders = new LinkedList<>();
	public ByteReader(String className){
		if(!className.contains(" ")) {
			omega.Screen.getScreen().getToolMenu().setTask("Unpacking " + className);
			this.className = className;
			read();
			omega.Screen.getScreen().getToolMenu().setTask("Hover to see Memory Statistics");
		}
	}

	public ByteReader(){}

	private void read(){
		try{
			if(code == null){
                    Import im  = getImport();
				if(im == null) return;
				Process process = null;
                    String javap = omega.Screen.getFileView().getJDKManager().javap;
				if(!im.module)
					process = new ProcessBuilder(javap, "-public", "-cp", im.jarPath, className).start();
				else{
                         String s = im.jarPath;
                         String module_path = s.substring(0, s.lastIndexOf(File.separator));
                         String module_name = s.substring(s.lastIndexOf(File.separator) + 1, s.lastIndexOf('.'));
					process = new ProcessBuilder(javap, "-public", "--module-path", module_path, "--module", module_name, className).start();
				}
				while(process.isAlive());
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String token = reader.readLine();
				if(token == null) return;
				this.code = "";
				while(token != null){
					this.code += token + "\n";
					token = reader.readLine();
				}
			}
			//Unpacking the _code
			dataMembers.clear();
			internalReaders.clear();
			StringTokenizer tok = new StringTokenizer(this.code, "\n");
			boolean recordingInternal = false;
			boolean commentStarts = false;
			int openBracesCount = -1;
			int internalCount = -1;
			String internalCode = "";
			while(tok.hasMoreTokens()){
				String line = tok.nextToken().trim();
				if(line.startsWith("Compiled from")) continue;
				//Skipping Strings and characters
				String cLine = line;
				line = "";
				boolean r = true;
				int in = 0;
				for(char ch : cLine.toCharArray()){
					if((ch == '\"' || ch == '\'') && in > 0 && cLine.charAt(in - 1) != '\\')
						r = !r;
					if(r && ch != '\"' && ch != '\''){
						line += ch;
					}
					in++;
				}
				cLine = "";
				//Skipping Diamonds
				if(line.contains("<") && line.contains(">") && !line.contains("&") && !line.contains("|")){
					int c = -1;
					cLine = line;
					line = "";
					for(char ch : cLine.toCharArray()){
						if(ch == '<')
							c++;
						else if(ch == '>')
							c--;
						if(c == -1 & ch != '<' & ch != '>')
							line += ch;
					}
				}
				//Skipping Comments
				if(line.startsWith("//")) continue;
				if(line.startsWith("/*")){
					commentStarts = true;
					continue;
				}
				else if(line.contains("*/")){
					commentStarts = false;
					continue;
				}
				else if(commentStarts) continue;
				//Reading code
				//Replacing ';' with "{}"
				if(line.endsWith(";") && line.contains("(") && line.contains(")")){
					line = line.substring(0, line.indexOf(';')) + "{}";
				}
				else if(line.endsWith(";")){
					line = line.substring(0, line.indexOf(';'));
				}
				//Counting Braces for InternalReaders
				if(line.contains("{")) internalCount++;
				if(line.contains("}")) internalCount--;
				//Balancing OpenBraces
				if(line.contains("{")) openBracesCount++;
				//System.out.println(line+", count "+openBracesCount);
				//Unpacking Prototype_
				if(line.startsWith("package ") && line.endsWith(";") && pack == null){
					pack = line.substring(line.indexOf(' ') + 1, line.indexOf(';')).trim();
					continue;
				}
				//Unpacking _description
				if(this.type == null){
					String type = "";
					if(line.contains("class "))
						type = "class";
					else if(line.contains("enum "))
						type = "enum";
					else if(line.contains("interface "))
						type = "interface";
					if(line.contains("@interface "))
						type = "@interface";
					if(!type.equals("")){
						this.type = type;
						String part = line.substring(0, line.indexOf(type)).trim();
						if(!line.startsWith(type)){
							if(part.contains(" ")){
								access = part.substring(0, part.indexOf(' ')).trim();
								modifier = part.substring(part.indexOf(' ') + 1).trim();
							}
							else access = part;
						}
						int index = line.indexOf(type) + type.length();
						int indexAfter = line.indexOf('\n');
						if(line.contains("{"))
							indexAfter = line.indexOf("{");
						if(line.indexOf(' ', index + 1) > -1){
							indexAfter = line.indexOf(' ', index + 1);
						}
						className = line.substring(index + 1, indexAfter);
						if(line.contains("extends")){
							int extendsI = line.indexOf("extends") + 7;
							if(line.contains("implements")){
								indexAfter = line.indexOf(' ', extendsI + 1);
								parent = line.substring(extendsI + 1, indexAfter).trim();
								int impI = indexAfter + 1 + 10;
								String x = line.substring(impI).trim();
								if(x.contains("{"))
									x = x.substring(0, x.indexOf('{'));
								else if(x.endsWith("\n"))
									x = x.substring(0, x.indexOf('\n'));
								if(!x.contains(",")){
									features = new String[1];
									features[0] = x;
								}
								else
									features = x.split(",");
								for(int ix = 0; ix < features.length; ix++)
									features[ix] = features[ix].trim();
							}
							else{
								if(line.contains("{"))
									indexAfter = line.indexOf('{');
								else indexAfter = line.indexOf('\n');
								parent = line.substring(extendsI + 1, indexAfter).trim();
							}
						}
						if(line.contains("implements") && !line.contains("extends")){
							int impI = line.indexOf("implements") + 10;
							String x = line.substring(impI).trim();
							if(x.contains("{"))
								x = x.substring(0, x.indexOf('{'));
							else if(x.endsWith("\n"))
								x = x.substring(0, x.indexOf('\n'));
							if(!x.contains(",")){
								features = new String[1];
								features[0] = x;
							}
							else
								features = x.split(",");
							for(int ix = 0; ix < features.length; ix++)
								features[ix] = features[ix].trim();
						}
						continue;
					}
				}
				//Unpacking Internal Classess
				if(internalCount >= 1){
					if(!recordingInternal){
						if(line.contains("class ") || line.contains("enum ") || line.contains("interface ")){
							recordingInternal = true;
						}
					}
					if(recordingInternal){
						internalCode += line + "\n";
					}
				}
				//Closing Internal SourceReader
				if(internalCount == 0 && recordingInternal){
					internalCode += line;
					recordingInternal = false;
					ByteReader byteReader = new ByteReader();
					byteReader.code = code;
					byteReader.read();
					internalReaders.add(byteReader);
					internalCode = "";
				}
				//Unpacking Global _Variables and _Functions
				if(line.contains(" ")){
					String cL = line;
					if(cL.contains("=")) cL = cL.substring(0, cL.indexOf('=')).trim();
					if(cL.contains("(") && openBracesCount <= 1){
						String parameters = cL.substring(cL.indexOf('(') + 1, cL.indexOf(')')).trim();
						cL = cL.substring(0, cL.indexOf('('));
						if(cL.contains(" ")){
							String name = cL.substring(cL.lastIndexOf(' ') + 1).trim();
							cL = cL.substring(0, cL.lastIndexOf(' ')).trim();
							if(cL.contains(" ")){
								String type = cL.substring(cL.lastIndexOf(' ') + 1).trim();
								cL = cL.substring(0, cL.lastIndexOf(' ')).trim();
								if(cL.contains(" ")){
									String mods = cL.substring(cL.indexOf(' ') + 1).trim();
									cL = cL.substring(0, cL.indexOf(' ')).trim();
									String access = cL;
									if(name.equals(className)){
										access = type;
										type = "";
									}
									if(name.equals(className) && name.contains("."))
										name = name.substring(name.lastIndexOf('.') + 1);
									dataMembers.add(new DataMember(access, mods, type, name + "()", parameters));
								}
								else{
									String access = cL;
									if(name.equals(className)){
										access = type;
										type = "";
									}
									dataMembers.add(new DataMember(access, "", type, name + "()", parameters));
								}
							}
							else{
								String type = cL;
								dataMembers.add(new DataMember(name.equals(className) ? type : "", "", name.equals(className) ? "" : type, name + "()", parameters));
							}
						}
					}
					else if(openBracesCount == 0){
						String name = cL.substring(cL.lastIndexOf(' ') + 1).trim();
						cL = cL.substring(0, cL.lastIndexOf(' '));
						if(cL.contains(" ")){
							String type = cL.substring(cL.lastIndexOf(' ') + 1).trim();
							cL = cL.substring(0, cL.lastIndexOf(' '));
							if(cL.contains(" ")){
								String mods = cL.substring(cL.indexOf(' ') + 1).trim();
								cL = cL.substring(0, cL.indexOf(' ')).trim();
								String access = cL;
								dataMembers.add(new DataMember(access, mods, type, name, null));
							}
							else{
								String access = cL;
								dataMembers.add(new DataMember(access, "", type, name, null));
							}
						}
						else{
							String type = cL;
							dataMembers.add(new DataMember("", "", type, name, null));
						}
					}
				}
				//Balancing CloseBraces
				if(line.contains("}")) {
					openBracesCount--;
				}
			}
			if(className.equals("java.lang.Object") || className.equals("Object"))
				parent = "";
			if(!parent.equals("")) {
				ByteReader byteReader = null;
				if(Assembly.has(parent)) byteReader = Assembly.getReader(parent);
				else byteReader = new ByteReader(parent);
				byteReader.dataMembers.forEach(this::offer);
				if(features != null) {
					for(String f : features) {
						ByteReader x = null;
						if(Assembly.has(f)) x = Assembly.getReader(f);
						else x = new ByteReader(f);
						x.dataMembers.forEach(this::offer);
					}
				}
			}
			if(!Assembly.has(className)) Assembly.add(className, this);
		}catch(Exception e){System.err.println(e.getMessage());}
	}
	
	public void offer(DataMember d) {
		for(DataMember dx : dataMembers) {
			if(dx.name.equals(d.name) && dx.parameterCount == d.parameterCount && dx.type.equals(d.type)) {
				if(dx.parameters != null && d.parameters != null && !dx.parameters.equals(d.parameters)) continue;
				return;
			}
		}
		dataMembers.add(d);
	}

	public LinkedList<DataMember> getConstructors(){
		LinkedList<DataMember> constructors = new LinkedList<>();
		String className = this.className;
		if(className.contains("."))
			className = className.substring(className.lastIndexOf('.') + 1);

		for(DataMember d : dataMembers){
			if(d.parameters != null){
				if(d.name.equals(className+"()") && d.type.equals(""))
					constructors.add(d);
			}
		}
		if(constructors.isEmpty()){
			constructors.add(new DataMember("public", "", "", className, ""));
		}
		return constructors;
	}

	public LinkedList<DataMember> getDataMembers(String modifier){
		LinkedList<DataMember> members = new LinkedList<>();
		dataMembers.forEach(d->{
			String mod = d.modifier;
			if(!modifier.equals("") && mod.contains(modifier)){
				members.add(d);
			}
			else if(modifier.equals("") && !mod.contains("static")){
				members.add(d);
			}
		});
		return members;
	}

	@Override
	public String toString(){
		String f = "";
		if(features != null){
			for(String fx : features)
				f += fx + " ";
		}
		return "[type - " + type + ", name - " + className + ", modifier - " + modifier + ", access - " + access + ", parent - " + parent + ", features - " + f.trim() + "]";
	}

	private Import getImport(){
		for(Import m : JDKManager.getAllImports()) {
			if(m.getImport().equals(className)) {
				return m;
			}
		}
		return null;
	}
}
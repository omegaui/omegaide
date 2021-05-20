package omega.instant.support.universal;
public class ProcessData {
     public String fileExt;
     public String executionCommand;
     public ProcessData(String ext, String cmd){
     	this.fileExt = ext;
          this.executionCommand = cmd;
     }

     @Override
     public String toString(){
     	return "*" + fileExt + "* -> **" + executionCommand + "**";
     }
}

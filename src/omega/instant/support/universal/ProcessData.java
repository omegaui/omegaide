/**
  * The Universal Process Launch Data
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.instant.support.universal;
import java.util.LinkedList;
public class ProcessData {
     public String fileExt;
     public LinkedList<String> executionCommand;
     public ProcessData(String ext, LinkedList<String> cmd){
     	this.fileExt = ext;
          this.executionCommand = cmd;
     }

     @Override
     public String toString(){
     	return "*" + fileExt + "* -> **" + executionCommand + "**";
     }
}


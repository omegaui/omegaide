/**
  * File Stream Opener
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

package omega.plugin;
import java.net.*;
import java.io.*;
public class Download {
     public static InputStream openStream(String url){
     	InputStream in = null;
          try{
          	in = new BufferedInputStream(new URL(url).openStream());
          }
          catch(Exception e){ 
          	System.err.println(e); 
          }
          return in;
     }
}


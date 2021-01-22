package deassembler;
/*
    Stores the DataMember with depth in a method block.
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
public class DepthMember extends DataMember{
     public int depth = 0;
     public DepthMember(String access, String modifier, String type, String name, String parameters, int depth){
          super(access, modifier, type, name, parameters);
          this.depth = depth;
     }

     @Override
     public String toString(){
          return super.toString() + ", depth - " + depth;
     }
}
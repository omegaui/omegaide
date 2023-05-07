/**
 * RemotePluginInfo
 * Copyright (C) 2021 Omega UI
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.plugin.store;

import java.net.URL;
import java.util.LinkedList;

public class RemotePluginInfo {
    public String name;
    public String version;
    public String description;
    public String author;
    public String license;
    public String size;
    public String category;
    public String fileName;
    public URL pluginFileURL;
    public URL imageURL;
    public LinkedList<URL> screenshotsURLs = new LinkedList<>();
}

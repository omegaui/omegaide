/**
 * RemotePluginInfoLoader
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

import omega.plugin.Downloader;
import omegaui.dynamic.database.DataBase;
import omegaui.dynamic.database.DataEntry;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

public class RemotePluginInfoLoader {

    public static final String REMOTE_PLUGIN_INFOS_URL = "https://raw.githubusercontent.com/omegaui/omegaide-plugins/main/.remotePluginInfos";
    public static final String LOCAL_PLUGIN_INFOS_PATH = ".omega-ide" + File.separator + ".localPluginInfos";

    public PluginStore pluginStore;

    public LinkedList<RemotePluginInfo> remotePluginInfos = new LinkedList<>();

    public RemotePluginInfoLoader(PluginStore pluginStore) {
        this.pluginStore = pluginStore;
        new File(LOCAL_PLUGIN_INFOS_PATH).delete();
    }

    public void loadRemotePluginInfos() {
        pluginStore.setStatus("Reading Plugin List ... ");
        try (Scanner reader = new Scanner(Downloader.openStream(REMOTE_PLUGIN_INFOS_URL)); PrintWriter writer = new PrintWriter(LOCAL_PLUGIN_INFOS_PATH)) {

            while (reader.hasNextLine()) {
                writer.println(reader.nextLine());
            }

            writer.close();
            reader.close();

            pluginStore.setStatus("Reading Plugin DataBase ... ");
            DataBase pluginInfoDataBase = new DataBase(LOCAL_PLUGIN_INFOS_PATH);
            pluginInfoDataBase.getDataSetNames().forEach((pluginName) -> {
                try {
                    RemotePluginInfo info = new RemotePluginInfo();
                    info.name = pluginName;
                    info.version = pluginInfoDataBase.getEntryAt(pluginName, 0).getValue();
                    info.description = pluginInfoDataBase.getEntryAt(pluginName, 1).getValue();
                    info.author = pluginInfoDataBase.getEntryAt(pluginName, 2).getValue();
                    info.license = pluginInfoDataBase.getEntryAt(pluginName, 3).getValue();
                    info.size = pluginInfoDataBase.getEntryAt(pluginName, 4).getValue();
                    info.category = pluginInfoDataBase.getEntryAt(pluginName, 5).getValue();
                    info.fileName = pluginInfoDataBase.getEntryAt(pluginName, 6).getValue();
                    info.pluginFileURL = new URL(pluginInfoDataBase.getEntryAt(pluginName, 7).getValue());
                    info.imageURL = new URL(pluginInfoDataBase.getEntryAt(pluginName, 8).getValue());
                    LinkedList<DataEntry> entries = pluginInfoDataBase.getEntries(pluginName);
                    if (entries.size() > 9) {
                        for (int i = 9; i < entries.size(); i++) {
                            String url = entries.get(i).getValue();
                            if (!url.equals(""))
                                info.screenshotsURLs.add(new URL(url));
                        }
                    }
                    remotePluginInfos.add(info);
                } catch (Exception ex) {
                    pluginStore.setStatus("Exception Reading Plugin Info ... " + pluginName);
                    ex.printStackTrace();
                }
            });
            pluginStore.setStatus(remotePluginInfos.size() + " Plugin(s) Available in the Store!");
        } catch (Exception e) {
            pluginStore.setStatus("Unable to Read Remote Plugin List, Check your internet connection.");
            e.printStackTrace();
        }
    }

}

/**
 * PluginReactionEvent
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

package omega.plugin.event;

public record PluginReactionEvent(int type, Object source, Object data) {
    public static final int EVENT_TYPE_PROJECT_TAG_CHANGED = 0;
    public static final int EVENT_TYPE_PROJECT_CHANGED = 0;
    public static final int EVENT_TYPE_PROJECT_CLOSED = 1;
    public static final int EVENT_TYPE_PROJECT_DELETED = 2;

    public static final int EVENT_TYPE_IDE_INITIALIZED = 3;
    public static final int EVENT_TYPE_IDE_MINIMIZED = 4;
    public static final int EVENT_TYPE_IDE_MAXIMIZED = 5;
    public static final int EVENT_TYPE_IDE_DO_LAYOUT = 6;
    public static final int EVENT_TYPE_IDE_LANG_TAG_CHANGED = 7;
    public static final int EVENT_TYPE_IDE_RESTORED = 8;
    public static final int EVENT_TYPE_IDE_CLOSING = 9;

    public static final int EVENT_TYPE_EDITOR_CREATED = 10;
    public static final int EVENT_TYPE_EDITOR_SAVED = 11;
    public static final int EVENT_TYPE_EDITOR_RELOADED = 12;
    public static final int EVENT_TYPE_EDITOR_DISCARD = 13;
    public static final int EVENT_TYPE_EDITOR_CLOSED = 14;

    public static boolean isEventTypeValid(int type) {
        return type >= EVENT_TYPE_PROJECT_TAG_CHANGED && type <= EVENT_TYPE_EDITOR_CLOSED;
    }

    public static PluginReactionEvent genNewInstance(int type, Object source, Object data) {
        return new PluginReactionEvent(type, source, data);
    }

}

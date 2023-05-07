/*
 * SyntaxParsers
 * Copyright (C) 2022 Omega UI

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

package omega.instant.support;

import omega.Screen;
import omega.instant.support.java.parser.JavaSyntaxParser;

import java.util.LinkedList;

public final class SyntaxParsers {
    public static JavaSyntaxParser javaSyntaxParser = new JavaSyntaxParser();

    public static LinkedList<AbstractSyntaxParser> syntaxParsers = new LinkedList<>();

    public synchronized static void parse() {
        int langTag = Screen.getProjectFile().getProjectManager().getLanguageTag();
        if (langTag == LanguageTagView.LANGUAGE_TAG_JAVA)
            javaSyntaxParser.parse();
        else {
            for (AbstractSyntaxParser syntaxParser : syntaxParsers) {
                if (syntaxParser.getLanguageTag() == langTag) {
                    syntaxParser.parse();
                    break;
                }
            }
        }
    }

    public static void addSyntaxParser(AbstractSyntaxParser syntaxParser) {
        syntaxParsers.add(syntaxParser);
    }
}


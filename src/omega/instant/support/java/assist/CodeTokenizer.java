/*
 * Splits Large Texts
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

package omega.instant.support.java.assist;

import java.util.LinkedList;

public class CodeTokenizer {
    public static LinkedList<String> tokenize(String code, char s) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token);
                token = "";
            } else
                token += ch;
        }
        return tokens;
    }

    public static LinkedList<String> tokenizeWithoutLoss(String code, char s) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token + s);
                token = "";
            } else
                token += ch;
        }
        return tokens;
    }

    public static LinkedList<String> tokenize(String code, char s, int maxTokenCount) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length() && maxTokenCount != 0; i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token);
                token = "";
                maxTokenCount--;
            } else
                token += ch;
        }
        return tokens;
    }

    public static LinkedList<String> tokenizeWithoutLoss(String code, char s, int maxTokenCount) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length() && maxTokenCount != 0; i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token + s);
                token = "";
                maxTokenCount--;
            } else
                token += ch;
        }
        return tokens;
    }

    public static LinkedList<String> tokenize(String code, char s, String breaker) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token);
                if (token.startsWith(breaker))
                    break;
                token = "";
            } else
                token += ch;
        }
        return tokens;
    }

    public static LinkedList<String> tokenizeWithoutLoss(String code, char s, String breaker) {
        LinkedList<String> tokens = new LinkedList<>();
        String token = "";
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == s) {
                tokens.add(token + s);
                if (token.startsWith(breaker))
                    break;
                token = "";
            } else
                token += ch;
        }
        return tokens;
    }
}


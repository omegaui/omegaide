/**
  * RustTokenMaker
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

package omega.token.factory;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.Token;
public class RustTokenMaker extends AbstractTokenMaker {
	private TokenMap tokenMap;
	public RustTokenMaker(){
		tokenMap = new TokenMap();
		tokenMap.put("as", TokenTypes.RESERVED_WORD);
		tokenMap.put("break", TokenTypes.RESERVED_WORD);
		tokenMap.put("const", TokenTypes.RESERVED_WORD);
		tokenMap.put("continue", TokenTypes.RESERVED_WORD);
		tokenMap.put("crate", TokenTypes.RESERVED_WORD);
		tokenMap.put("else", TokenTypes.RESERVED_WORD);
		tokenMap.put("enum", TokenTypes.RESERVED_WORD);
		tokenMap.put("extern", TokenTypes.RESERVED_WORD);
		tokenMap.put("false", TokenTypes.RESERVED_WORD);
		tokenMap.put("for", TokenTypes.RESERVED_WORD);
		tokenMap.put("fn", TokenTypes.RESERVED_WORD);
		tokenMap.put("if", TokenTypes.RESERVED_WORD);
		tokenMap.put("impl", TokenTypes.RESERVED_WORD);
		tokenMap.put("in", TokenTypes.RESERVED_WORD);
		tokenMap.put("let", TokenTypes.RESERVED_WORD);
		tokenMap.put("loop", TokenTypes.RESERVED_WORD);
		tokenMap.put("match", TokenTypes.RESERVED_WORD);
		tokenMap.put("mod", TokenTypes.RESERVED_WORD);
		tokenMap.put("move", TokenTypes.RESERVED_WORD);
		tokenMap.put("mut", TokenTypes.RESERVED_WORD);
		tokenMap.put("pub", TokenTypes.RESERVED_WORD);
		tokenMap.put("ref", TokenTypes.RESERVED_WORD);
		tokenMap.put("return", TokenTypes.RESERVED_WORD);
		tokenMap.put("self", TokenTypes.RESERVED_WORD);
		tokenMap.put("Self", TokenTypes.RESERVED_WORD);
		tokenMap.put("static", TokenTypes.RESERVED_WORD);
		tokenMap.put("struct", TokenTypes.RESERVED_WORD);
		tokenMap.put("super", TokenTypes.RESERVED_WORD);
		tokenMap.put("trait", TokenTypes.RESERVED_WORD);
		tokenMap.put("true", TokenTypes.RESERVED_WORD);
		tokenMap.put("type", TokenTypes.RESERVED_WORD);
		tokenMap.put("unsafe", TokenTypes.RESERVED_WORD);
		tokenMap.put("use", TokenTypes.RESERVED_WORD);
		tokenMap.put("where", TokenTypes.RESERVED_WORD);
		tokenMap.put("while", TokenTypes.RESERVED_WORD);
		tokenMap.put("async", TokenTypes.RESERVED_WORD);
		tokenMap.put("await", TokenTypes.RESERVED_WORD);
		tokenMap.put("dyn", TokenTypes.RESERVED_WORD);
		tokenMap.put("abstract", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("become", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("box", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("do", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("final", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("macro", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("override", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("priv", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("typeof", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("unsized", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("virtual", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("yield", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("try", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("union", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("'static", TokenTypes.RESERVED_WORD_2);
		wordsToHighlight = tokenMap;
	}
	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		if (tokenType==Token.IDENTIFIER) {
			int value = wordsToHighlight.get(segment, start, end);
			if (value != -1) {
				tokenType = value;
			}
		}
		super.addToken(segment, start, end, tokenType, startOffset);
	}
	public Token getTokenList(Segment text, int startTokenType, int startOffset) {
		resetTokenList();
		char[] array = text.array;
		int offset = text.offset;
		int count = text.count;
		int end = offset + count;
		
		int newStartOffset = startOffset - offset;
		int currentTokenStart = offset;
		int currentTokenType  = startTokenType;
		for (int i=offset; i<end; i++) {
			char c = array[i];
			switch (currentTokenType) {
				case Token.NULL:
				currentTokenStart = i;   // Starting a new token here.
				switch (c) {
					case ' ':
					case '\t':
					currentTokenType = Token.WHITESPACE;
					break;
					case '"':
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;
					case '/':
					currentTokenType = Token.COMMENT_EOL;
					break;
					default:
					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					}
					else if (RSyntaxUtilities.isLetter(c) || c == '#' || c == '_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}
					currentTokenType = Token.IDENTIFIER;
					break;
				}
				break;
				case Token.WHITESPACE:
				switch (c) {
					case ' ':
					case '\t':
					break;   // Still whitespace.
					case '"':
					addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;
					case '/':
					addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.COMMENT_EOL;
					break;
					default:   // Add the whitespace token and start anew.
					addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					if (RSyntaxUtilities.isDigit(c)) {
						currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
						break;
					}
					else if (RSyntaxUtilities.isLetter(c) || c=='#' || c=='_') {
						currentTokenType = Token.IDENTIFIER;
						break;
					}
					currentTokenType = Token.IDENTIFIER;
				}
				break;
				default:
				case Token.IDENTIFIER:
				switch (c) {
					case ' ':
					case '\t':
					addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;
					case '"':
					addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;
					default:
					if (RSyntaxUtilities.isLetterOrDigit(c) || c=='/' || c=='_') {
						break;
					}
					
				}
				break;
				case Token.LITERAL_NUMBER_DECIMAL_INT:
				switch (c) {
					case ' ':
					case '\t':
					addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.WHITESPACE;
					break;
					case '"':
					addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
					currentTokenStart = i;
					currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
					break;
					default:
					if (RSyntaxUtilities.isDigit(c)) {
						break;
					}
					addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
					i--;
					currentTokenType = Token.NULL;
				}
				break;
				case Token.COMMENT_EOL:
				i = end - 1;
				addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
				currentTokenType = Token.NULL;
				break;
				case Token.LITERAL_STRING_DOUBLE_QUOTE:
				if (c == '"') {
     				addToken(text, currentTokenStart,i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset+currentTokenStart);
     				currentTokenType = Token.NULL;
     			}
     			break;
     		}
     		
     	}
     	switch (currentTokenType) {
     		case Token.LITERAL_STRING_DOUBLE_QUOTE:
     		addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
     		break;
     		case Token.NULL:
     		addNullToken();
     		break;
     		default:
     		addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
     		addNullToken();
     	}
     	return firstToken;
     }
     @Override
     public TokenMap getWordsToHighlight() {
     	return tokenMap;
     }
     public static void apply(RSyntaxTextArea textArea){
     	AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
     	atmf.putMapping("text/rust", "omega.token.factory.RustTokenMaker");
     	textArea.setSyntaxEditingStyle("text/rust");
     }
}


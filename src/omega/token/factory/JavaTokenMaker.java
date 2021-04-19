package omega.token.factory;
import javax.swing.text.*;
import org.fife.ui.rsyntaxtextarea.*;
public class JavaTokenMaker extends AbstractTokenMaker {
	private TokenMap tokenMap;
	public JavaTokenMaker(){
		tokenMap = new TokenMap();
		tokenMap.put("public", TokenTypes.RESERVED_WORD);
		tokenMap.put("private", TokenTypes.RESERVED_WORD);
		tokenMap.put("protected", TokenTypes.RESERVED_WORD);
		tokenMap.put("default", TokenTypes.RESERVED_WORD);
		tokenMap.put("record", TokenTypes.RESERVED_WORD);
		tokenMap.put("permits", TokenTypes.RESERVED_WORD);
		tokenMap.put("sealed", TokenTypes.RESERVED_WORD);
		tokenMap.put("abstract", TokenTypes.RESERVED_WORD);
		tokenMap.put("try", TokenTypes.RESERVED_WORD);
		tokenMap.put("catch", TokenTypes.RESERVED_WORD);
		tokenMap.put("while", TokenTypes.RESERVED_WORD);
		tokenMap.put("for", TokenTypes.RESERVED_WORD);
		tokenMap.put("do", TokenTypes.RESERVED_WORD);
		tokenMap.put("var", TokenTypes.RESERVED_WORD);
		tokenMap.put("class", TokenTypes.RESERVED_WORD);
		tokenMap.put("interface", TokenTypes.RESERVED_WORD);
		tokenMap.put("@interface", TokenTypes.RESERVED_WORD);
		tokenMap.put("enum", TokenTypes.RESERVED_WORD);
		tokenMap.put("break", TokenTypes.RESERVED_WORD);
		tokenMap.put("continue", TokenTypes.RESERVED_WORD);
		tokenMap.put("strictfp", TokenTypes.RESERVED_WORD);
		tokenMap.put("assert", TokenTypes.RESERVED_WORD);
		tokenMap.put("transient", TokenTypes.RESERVED_WORD);
		tokenMap.put("native", TokenTypes.RESERVED_WORD);
		tokenMap.put("static", TokenTypes.RESERVED_WORD);
		tokenMap.put("synchronized", TokenTypes.RESERVED_WORD);
		tokenMap.put("void", TokenTypes.RESERVED_WORD);
		tokenMap.put("super", TokenTypes.RESERVED_WORD);
		tokenMap.put("this", TokenTypes.RESERVED_WORD);
		tokenMap.put("finally", TokenTypes.RESERVED_WORD);
		tokenMap.put("new", TokenTypes.RESERVED_WORD);
		tokenMap.put("final", TokenTypes.RESERVED_WORD);
		tokenMap.put("extends", TokenTypes.RESERVED_WORD);
		tokenMap.put("implements", TokenTypes.RESERVED_WORD);
		tokenMap.put("import", TokenTypes.RESERVED_WORD);
		tokenMap.put("package", TokenTypes.RESERVED_WORD);
		tokenMap.put("volatile", TokenTypes.RESERVED_WORD);
		tokenMap.put("byte", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("short", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("int", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("float", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("double", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("long", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("boolean", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("char", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("switch", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("case", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("yield", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("return", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("throw", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("throws", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("if", TokenTypes.RESERVED_WORD_2);
		tokenMap.put("else", TokenTypes.RESERVED_WORD_2);
          tokenMap.put("instanceof", TokenTypes.RESERVED_WORD_2);
          tokenMap.put("String", TokenTypes.FUNCTION);
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
                         if(i + 1 < end){
                              char nextC = array[i + 1];
                              if(nextC == '*'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_MULTILINE;
                              }
                              else if(nextC == '/'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_EOL;
                              }
                         }
					break;
                         case '*':
                         if(i + 1 < end){
                              char nextC = array[i + 1];
                              if(nextC == '/'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_EOL;
                              }
                         }
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
                         if(i + 1 < end){
                              char nextC = array[i + 1];
                              if(nextC == '*'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_MULTILINE;
                              }
                              else if(nextC == '/'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_EOL;
                              }
                         }
                         break;
                         case '*':
                         if(i + 1 < end){
                              char nextC = array[i + 1];
                              if(nextC == '/'){
                                   addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                                   currentTokenStart = i;
                                   currentTokenType = Token.COMMENT_EOL;
                              }
                         }
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
     	atmf.putMapping("text/java16+", "omega.token.factory.JavaTokenMaker");
     	textArea.setSyntaxEditingStyle("text/java16+");
     }
}

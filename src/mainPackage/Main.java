package mainPackage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public enum States {
		STATE_START, STATE_WORD, STATE_NUMBER, STATE_DOT_TRANSITION, STATE_REAL_NUMBER, STATE_SYMBOL, STATE_SYMBOL_EQUAL, STATE_ERROR;
		
		States letters;
		States numbers;
		States dot;
		States symbol;
		States equalsSymbol;

		static {
			STATE_START.letters = STATE_WORD; 				STATE_START.numbers = STATE_NUMBER;			STATE_START.symbol = STATE_SYMBOL;	STATE_START.equalsSymbol = STATE_SYMBOL;
			STATE_WORD.letters = STATE_WORD; 				STATE_WORD.numbers = STATE_WORD;
			STATE_NUMBER.numbers = STATE_NUMBER; 			STATE_NUMBER.dot = STATE_DOT_TRANSITION; 	STATE_NUMBER.letters = STATE_ERROR;
			STATE_DOT_TRANSITION.numbers = STATE_REAL_NUMBER;
			STATE_REAL_NUMBER.numbers = STATE_REAL_NUMBER;
			STATE_ERROR.letters = STATE_ERROR; 				STATE_ERROR.dot = STATE_ERROR; 				STATE_ERROR.numbers = STATE_ERROR;
			STATE_SYMBOL.equalsSymbol = STATE_SYMBOL_EQUAL;
		}
		
		States transition(Character aChar) {
			if (Character.isLetter(aChar)) {
				return this.letters;
			}
			else if (Character.isDigit(aChar)) {
				return this.numbers;
			}
			else if (aChar.equals('.')) {
				return this.dot;
			}
			else if (aChar.equals('=')) {
				return this.equalsSymbol;
			}
			else if (isCharSymbol(aChar)) {
				return this.symbol;
			}
			
			throw new RuntimeException("Symbol <" + aChar + "> is not in the alphabet");
		}
	}
	
	public static List<Token> tokens = new ArrayList<Token>();
	
	public static void main(String[] args) {		
		lexicalAnalysis("src/input_file.txt");
		
		System.out.println("TOKENS:: ");
		for (Token token : tokens) {
			if (token.type != null) {
				System.out.println("Token (" + token.type + "): " + token.value);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void lexicalAnalysis(String filename) {
		String currentToken = "";
		States currentState = States.STATE_START; 
		
		try {
			InputStream in = new FileInputStream(filename);
			Reader r = new InputStreamReader(in, "US-ASCII");
			int intch;
			while ((intch = r.read()) != -1) {
				Character aChar = (char) intch;
				
				System.out.println("STATE (" + currentState + ") => " + aChar);
				if (isCharEndingSymbol(aChar)) {
					Token token = new Token();
					token.value = currentToken;
					token.setTypeWithState(currentState, currentToken);
					token.startingCharacterIndex = 0;
					token.lineNumber = 1;
					
					tokens.add(token);
					
					Token tokenSymbol = new Token();
					tokenSymbol.value = String.valueOf(aChar);
					tokenSymbol.setTypeWithState(States.STATE_SYMBOL, String.valueOf(aChar));
					tokenSymbol.startingCharacterIndex = 0;
					tokenSymbol.lineNumber = 1;
					
					tokens.add(tokenSymbol);
					
					currentToken = "";
					
					currentState = States.STATE_START;
					
					continue;
				}
				
				if (isCharEndingChar(aChar)) {
					if (currentState == States.STATE_START) {
						continue;
					}
					
					Token token = new Token();
					token.value = currentToken;
					token.setTypeWithState(currentState, currentToken);
					token.startingCharacterIndex = 0;
					token.lineNumber = 1;
					
					tokens.add(token);
					currentToken = "";
					
					currentState = States.STATE_START;
					
					continue;
				}
				
				if (currentState == States.STATE_SYMBOL || isCharSymbol(aChar)) {
					if (aChar.equals('=')) {
						currentState = currentState.transition(aChar);
						currentToken += aChar;
						
						continue;
					}
					
					Token token = new Token();
					token.value = currentToken;
					token.setTypeWithState(currentState, currentToken);
					token.startingCharacterIndex = 0;
					token.lineNumber = 1;
					
					tokens.add(token);
					currentToken = "";
					
					currentState = States.STATE_START;
				}
				
				//System.out.println("STATE (" + currentState + ") => " + aChar);
				currentState = currentState.transition(aChar);
				currentToken += aChar;
			}

			if (currentToken != "" && currentState != States.STATE_START) {
				Token token = new Token();
				token.value = currentToken;
				token.setTypeWithState(currentState, currentToken);
				token.startingCharacterIndex = 0;
				token.lineNumber = 1;
				
				tokens.add(token);
				currentToken = "";
				
				currentState = States.STATE_START;
			}
			
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isCharEndingSymbol(Character theChar) {	
		return theChar.equals(';') || theChar.equals(',');
	}
	
	public static boolean isCharEndingChar(Character theChar) {
		return theChar.equals(' ') || theChar.equals('\n') || theChar.equals('\t');
	}
	
	public static boolean isCharSymbol(Character theChar) {
		return theChar.equals('(') || theChar.equals(')') || theChar.equals('{') || 
				theChar.equals('}') || theChar.equals('+') || theChar.equals('-') || 
				theChar.equals('*') || theChar.equals('/') || theChar.equals('^') ||
				theChar.equals('=') || theChar.equals('>') || theChar.equals('<') || 
				theChar.equals('|') || theChar.equals('&') || theChar.equals('!');
	}
}

package mainPackage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mainPackage.Main.States;

public class Token {
	public enum Type {
		INTEGER, REAL, WORD, RESERVED_WORD, SYMBOL, EQUALS_LOGIC_SYMBOL, ERROR;
	}
	
	public final Set<String> RESERVED_WORDS = new HashSet<String>(Arrays.asList("si", "mientras", "principal", "real", "entero", "logico", "verdadero", "falso"));
	
	public Type type;
	public String value;
	public int startingCharacterIndex;
	public int lineNumber;
	
	public void setTypeWithState(States state, String tokenVal) {
		switch (state) {
		case STATE_NUMBER:
			this.type = Type.INTEGER;
			break;
		case STATE_WORD:
			if (RESERVED_WORDS.contains(tokenVal)) {
				this.type = Type.RESERVED_WORD;
			}
			else {
				this.type = Type.WORD;
			}
			break;
		case STATE_REAL_NUMBER:
			this.type = Type.REAL;
			break;
		case STATE_SYMBOL:
			this.type = Type.SYMBOL;
			break;
		case STATE_SYMBOL_EQUAL:
			this.type = Type.EQUALS_LOGIC_SYMBOL;
			break;
		case STATE_ERROR:
			this.type = Type.ERROR;
			break;
		}
	}
}

package parser;

public class TokenizerNode {
	static final int VARIABLE_UNKNOWN = 0;
	static final int VARIABLE_WORD_SET = VARIABLE_UNKNOWN + 1;
	static final int VARIABLE_WORD = VARIABLE_WORD_SET + 1;
	static final int VARIABLE_ALPAHBET_SET = VARIABLE_WORD + 1;
	static final int VARIABLE_ALPAHBET = VARIABLE_ALPAHBET_SET + 1;

	public static String variableTypeName(int value) {
		switch (value) {
		case VARIABLE_WORD_SET:
			return "wordSet";
		case VARIABLE_WORD:
			return "word";
		case VARIABLE_ALPAHBET_SET:
			return "alphabetSet";
		case VARIABLE_ALPAHBET:
			return "alphabet";
		default:
			return "unknown";
		}
	}
}

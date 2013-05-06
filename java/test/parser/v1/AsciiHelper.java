package parser.v1;

import java.io.IOException;

public class AsciiHelper {
	// ...
	// public static final byte BELL = 7; // \a
	public static final byte BACK_SPACE = 8; // \b
	public static final byte HORIZONTAL_TAB = 9; // \t
	public static final byte NEW_LINE = 10; // \n
	// public static final byte VERTICAL_TAB = 11; // \v
	public static final byte FORM_FEED = 12; // \f
	public static final byte CARRIAGE_RETURN = 13; // \r
	// ...
	public static final byte SPACE = 32; //
	public static final byte EXCLAMATION = 33; // ?
	public static final byte DOUBLE_QUOTE = 34; // "
	public static final byte SHARP = 35; // #
	public static final byte DOLLAR = 36; // $
	public static final byte PERCENT = 37; // %
	public static final byte AND = 38; // &
	public static final byte SINGLE_QUOTE = 39; // '
	public static final byte ROUND_BRA = 40; // (
	public static final byte ROUND_KET = 41; // )
	public static final byte STAR = 42; // *
	public static final byte PLUS = 43; // +
	public static final byte COMMA = 44; // ,
	public static final byte MINUS = 45; // -
	public static final byte PERIOD = 46; // .
	public static final byte SOLIDUS = 47; // /
	public static final byte ZERO = 48; // 0
	public static final byte NINE = 57; // 9
	public static final byte COLON = 58; // :
	public static final byte SEMICOLON = 59; // ;
	public static final byte ANGLE_BRA = 60; // <
	public static final byte EQUAL = 61; // =
	public static final byte ANGLE_KET = 62; // >
	public static final byte QUESTION = 63; // ?
	public static final byte AT = 64; // @
	public static final byte AA = 65; // A
	// ...
	public static final byte ZZ = 90; // Z
	public static final byte SQUARE_BRA = 91; // (
	public static final byte BACK_SOLIDUS = 92; // \\
	public static final byte SQUARE_KET = 93; // )
	public static final byte HAT = 94; // ^
	public static final byte UNDERLINE = 95; // _
	public static final byte GRAVE = 96; // `
	public static final byte A = 97; // a
	// ...
	public static final byte Z = 122; // z
	public static final byte CURLY_BRA = 123; // {
	public static final byte VERTICAL_LINE = 124; // |
	public static final byte CURLY_KET = 125; // }
	public static final byte TILDE = 126; // ~

	public static final int BOM_0 = 0xEF;
	public static final int BOM_1 = 0xBB;
	public static final int BOM_2 = 0xBF;

	public static byte charToAscii(char ch, byte def) {
		if (0 <= ch && ch < 128) {
			return (byte) ch;
		}
		return def;
	}
	public static char asciiToChar(byte a) {
		return (char) a;
	}
	public static boolean isAlphabet(int ch) {
		return isSmallAlphabet(ch) || isLargAlphabet(ch);
	}
	public static boolean isSmallAlphabet(int ch) {
		return A <= ch && ch <= Z;
	}
	public static boolean isLargAlphabet(int ch) {
		return AA <= ch && ch <= ZZ;
	}
	public static boolean isDigit(int ch) {
		return ZERO <= ch && ch <= NINE;
	}
	public static boolean isHexDigit(int ch) {
		return (ZERO <= ch && ch <= NINE) || (A <= ch && ch <= (A + 6))
				|| (AA <= ch && ch <= (AA + 6));
	}
	public static String toString(int ch) {
		StringBuilder buffer = new StringBuilder();
		try {
			return toString(buffer, ch).toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static Appendable toString(Appendable output, int ch)
			throws IOException {
		if (SPACE <= ch && ch <= TILDE) {
			switch (ch) {
			case BACK_SPACE:
				return output.append("\\b");
			case HORIZONTAL_TAB:
				return output.append("\\t");
			case NEW_LINE:
				return output.append("\\n");
			case FORM_FEED:
				return output.append("\\f");
			case CARRIAGE_RETURN:
				return output.append("\\r");
			default:
				return output.append((char) ch);
			}
		}
		return output.append("0x").append(Integer.toHexString(ch));
	}
}

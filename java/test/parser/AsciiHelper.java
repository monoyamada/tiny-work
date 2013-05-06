package parser;

import java.io.IOException;
import java.util.Arrays;

import tiny.lang.FileHelper;

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

	static final char[] ASCII_TABLE = { 0 // 0
			, 1 // 1
			, 2 // 2
			, 3 // 3
			, 4 // 4
			, 5 // 5
			, 6 // 6
			, 7 // 7
			, '\b' // 8
			, '\t' // 9
			, '\n' // 10
			, 11 // 11
			, '\f' // 12
			, '\r' // 13
			, 14 // 14
			, 15 // 15
			, 16 // 16
			, 17 // 17
			, 18 // 18
			, 19 // 19
			, 20 // 20
			, 21 // 21
			, 22 // 22
			, 23 // 23
			, 24 // 24
			, 25 // 25
			, 26 // 26
			, 27 // 27
			, 28 // 28
			, 29 // 29
			, 30 // 30
			, 31 // 31
			, ' ' // 32
			, '!' // 33
			, '"' // 34
			, '#' // 35
			, '$' // 36
			, '%' // 37
			, '&' // 38
			, '\'' // 39
			, '(' // 40
			, ')' // 41
			, '*' // 42
			, '+' // 43
			, ',' // 44
			, '-' // 45
			, '.' // 46
			, '/' // 47
			, '0' // 48
			, '1' // 49
			, '2' // 50
			, '3' // 51
			, '4' // 52
			, '5' // 53
			, '6' // 54
			, '7' // 55
			, '8' // 56
			, '9' // 57
			, ':' // 58
			, ';' // 59
			, '<' // 60
			, '=' // 61
			, '>' // 62
			, '?' // 63
			, '@' // 64
			, 'A' // 65
			, 'B' // 66
			, 'C' // 67
			, 'D' // 68
			, 'E' // 69
			, 'F' // 70
			, 'G' // 71
			, 'H' // 72
			, 'I' // 73
			, 'J' // 74
			, 'K' // 75
			, 'L' // 76
			, 'M' // 77
			, 'N' // 78
			, 'O' // 79
			, 'P' // 80
			, 'Q' // 81
			, 'R' // 82
			, 'S' // 83
			, 'T' // 84
			, 'U' // 85
			, 'V' // 86
			, 'W' // 87
			, 'X' // 88
			, 'Y' // 89
			, 'Z' // 90
			, '[' // 91
			, '\\' // 92
			, ']' // 93
			, '^' // 94
			, '_' // 95
			, '`' // 96
			, 'a' // 97
			, 'b' // 98
			, 'c' // 99
			, 'd' // 100
			, 'e' // 101
			, 'f' // 102
			, 'g' // 103
			, 'h' // 104
			, 'i' // 105
			, 'j' // 106
			, 'k' // 107
			, 'l' // 108
			, 'm' // 109
			, 'n' // 110
			, 'o' // 111
			, 'p' // 112
			, 'q' // 113
			, 'r' // 114
			, 's' // 115
			, 't' // 116
			, 'u' // 117
			, 'v' // 118
			, 'w' // 119
			, 'x' // 120
			, 'y' // 121
			, 'z' // 122
			, '{' // 123
			, '|' // 124
			, '}' // 125
			, '~' // 126
	};

	public static byte charToAscii(char ch, byte def) {
		int ind = Arrays.binarySearch(ASCII_TABLE, ch);
		if (ind < 0) {
			return def;
		}
		return (byte) ind;
	}
	public static char asciiToChar(byte a, char def) {
		if (0 <= a) {
			return ASCII_TABLE[a];
		}
		return def;
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
				return output.append(ASCII_TABLE[ch]);
			}
		}
		return output.append("0x").append(Integer.toHexString(ch));
	}
}

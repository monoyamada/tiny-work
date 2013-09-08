package tiny.lang;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;

import tiny.function.Function;

public class StringHelper {
	public static final String EMPTY_STRING = "";
	public static final String DEFAULT_SEPARATOR = ",";
	public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
	public static final Charset CHARSET_ASCII = Charset.forName("ASCII");

	static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8',
			(byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
			(byte) 'f' };

	public static String toHexString(byte value) {
		byte[] hex = new byte[2];
		hex[0] = HEX_CHAR_TABLE[value >>> 4];
		hex[1] = HEX_CHAR_TABLE[value & 0xF];
		return new String(hex, CHARSET_ASCII);
	}
	public static String toHexString(byte[] value) {
		byte[] hex = new byte[2 * value.length];
		int index = 0;
		for (byte b : value) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, CHARSET_ASCII);
	}

	public static class LexicographicalOrder implements Comparator<String> {
		public int compare(String o1, String o2) {
			if (ObjectHelper.equals(o1, o2)) {
				return 0;
			}
			final int n1 = o1.length();
			final int n2 = o2.length();
			if (n1 < n2) {
				return -1;
			} else if (n2 < n1) {
				return 1;
			}
			for (int i = 0; i < n1; ++i) {
				if (o1.charAt(i) < o2.charAt(i)) {
					return -1;
				} else if (o2.charAt(i) < o1.charAt(i)) {
					return 1;
				}
			}
			return 0;
		}
	}

	/**
	 * @param x
	 * @return {@link #EMPTY_STRING} if x is <code>null</code>.
	 */
	public static String toString(Object x) {
		return x == null ? StringHelper.EMPTY_STRING : x.toString();
	}

	public static String join(Iterable<?> array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(Object[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(long[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(int[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(short[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(byte[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(boolean[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(double[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}
	public static String join(float[] array) {
		return StringHelper.join(array, StringHelper.DEFAULT_SEPARATOR);
	}

	public static String join(Iterable<?> array, String delim) {
		return join(array, delim, null);
	}
	public static <T> String join(Iterable<T> array, String delim,
			Function<? super T, ?> fnc) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static <T> String join(T[] array, String delim) {
		return join(array, delim, null);
	}
	public static <T> String join(T[] array, String delim,
			Function<? super T, ?> fnc) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(long[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(int[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(short[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(byte[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(boolean[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(double[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static String join(float[] array, String delim) {
		StringBuilder buffer = new StringBuilder();
		try {
			StringHelper.join(buffer, array, delim);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}

	public static Appendable join(Appendable buffer, Iterable<?> array,
			String delim) throws IOException {
		return StringHelper.join(buffer, array, delim, null);
	}
	public static <T> Appendable join(Appendable output, Iterable<T> array,
			String delim, Function<? super T, ? extends CharSequence> fnc)
			throws IOException {
		Debug.isNotNull(output);
		Debug.isNotNull(array);
		if (delim == null) {
			delim = StringHelper.DEFAULT_SEPARATOR;
		}
		final Iterator<T> p = array.iterator();
		if (fnc == null) {
			for (int i = 0; p.hasNext(); ++i) {
				if (i != 0) {
					output.append(delim);
				}
				output.append(toString(p.next()));
			}
		} else {
			for (int i = 0; p.hasNext(); ++i) {
				if (i != 0) {
					output.append(delim);
				}
				try {
					output.append(fnc.evaluate(p.next()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return output;
	}
	public static<Out extends Appendable> Out join(Out buffer, Object[] array, String delim)
			throws IOException {
		return StringHelper.join(buffer, array, delim, null);
	}
	public static <Out extends Appendable,T> Out join(Out output, T[] array, String delim,
			Function<? super T, ? extends CharSequence> fnc) throws IOException {
		Debug.isNotNull(output);
		Debug.isNotNull(array);
		if (delim == null) {
			delim = StringHelper.DEFAULT_SEPARATOR;
		}
		if (fnc == null) {
			for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
				if (i != 0) {
					output.append(delim);
				}
				output.append(toString(array[i]));
			}
		} else {
			for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
				if (i != 0) {
					output.append(delim);
				}
				try {
					output.append(fnc.evaluate(array[i]));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return output;
	}
	public static Appendable join(Appendable output, long[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Long.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, int[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Integer.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, short[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Short.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, byte[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Byte.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, boolean[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Boolean.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, double[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Double.toString(array[i]));
		}
		return output;
	}
	public static Appendable join(Appendable output, float[] array, String delim)
			throws IOException {
		Debug.isNotNull(output);
		for (int i = 0, n = array != null ? array.length : 0; i < n; ++i) {
			if (i != 0) {
				output.append(delim);
			}
			output.append(Float.toString(array[i]));
		}
		return output;
	}
	
	public static String repeat(String text, int n) {
		if (text == null) {
			String msg = Messages.getNull(text);
			throw new IllegalArgumentException(msg);
		} else if (n < 0) {
			String msg = Messages.getUnexpectedValue("numbe of repeat",
					"positive integer", n);
			throw new IllegalArgumentException(msg);
		}
		switch (n) {
		case 0:
			return "";
		case 1:
			return text;
		case 2:
			return text + text;
		default:
		break;
		}
		final StringBuilder buffer = new StringBuilder();
		while (0 < n--) {
			buffer.append(text);
		}
		return buffer.toString();
	}

	public static boolean startsWith(String text, String part, boolean ignoreCase) {
		if (text == null || part == null || text.length() < part.length()) {
			return false;
		}
		return text.regionMatches(ignoreCase, 0, part, 0, part.length());
	}
	public static boolean startsWith(CharSequence x0, CharSequence x1,
			boolean ignoreCase) {
		if (x1.length() < 1) {
			return true;
		} else if (x0.length() < 1) {
			return false;
		}
		return StringHelper.startsWith(x0, 0, x1, 0, ignoreCase);
	}
	public static boolean startsWith(CharSequence x0, int begin0,
			CharSequence x1, boolean ignoreCase) {
		if (x1.length() < 1) {
			return true;
		} else if (x0.length() - begin0 < 1) {
			return false;
		}
		return StringHelper.startsWith(x0, begin0, x1, 0, ignoreCase);
	}
	public static boolean startsWith(CharSequence x0, int begin0,
			CharSequence x1, int begin1, boolean ignoreCase) {
		final int end0 = x0.length();
		final int end1 = x1.length();
		final int n0 = end0 - begin0;
		final int n1 = end1 - begin1;
		if (n0 < 0 || n1 < 1) {
			if (end0 < begin0) {
				throw new IllegalArgumentException(Messages.getIndexOutOfRange(begin0,
						begin0, end0));
			} else if (end1 < begin1) {
				throw new IllegalArgumentException(Messages.getIndexOutOfRange(begin1,
						begin1, end1));
			}
		} else if (n1 == 0) {
			return true;
		} else if (n0 < n1) {
			return false;
		}
		if (ignoreCase) {
			for (int i = 0, n = n1; i < n; ++i) {
				if (Character.toLowerCase(x0.charAt(begin0 + i)) != Character
						.toLowerCase(x1.charAt(begin1 + i))) {
					return false;
				}
			}
		} else {
			for (int i = 0, n = n1; i < n; ++i) {
				if (x0.charAt(begin0 + i) != x1.charAt(begin1 + i)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean endsWith(String text, String part, boolean ignoreCase) {
		if (text == null || part == null || text.length() < part.length()) {
			return false;
		}
		return text.regionMatches(ignoreCase, text.length() - part.length(), part,
				0, part.length());
	}

	public static int indexOf(CharSequence text, int begin, int end,
			CharSequence part, boolean ignoreCase) {
		final int n = part.length();
		if (n < 1) {
			return -1;
		}
		for (; begin < end; ++begin) {
			if (StringHelper.startsWith(text, begin, part, ignoreCase)) {
				return begin;
			}
		}
		return -1;
	}

	public static int skipSpaces(String text, int begin, int end) {
		for (; begin < end && Character.isWhitespace(text.charAt(begin)); ++begin) {
		}
		return begin;
	}
	public static String trim(String text) {
		if (text == null || text.length() < 1) {
			return text;
		}
		return text.trim();
	}

	public static long parseLong(String token, int def) {
		if (token == null || token.length() < 1) {
			return def;
		}
		try {
			return Long.parseLong(token.trim());
		} catch (NumberFormatException ex) {
		}
		return def;
	}
	public static int parseInt(String token, int def) {
		if (token == null || token.length() < 1) {
			return def;
		}
		try {
			return Integer.parseInt(token.trim());
		} catch (NumberFormatException ex) {
		}
		return def;
	}
	public static double parseDouble(String token, int def) {
		if (token == null || token.length() < 1) {
			return def;
		}
		try {
			return Double.parseDouble(token.trim());
		} catch (NumberFormatException ex) {
		}
		return def;
	}
	public static float parseFloat(String token, int def) {
		if (token == null || token.length() < 1) {
			return def;
		}
		try {
			return Float.parseFloat(token.trim());
		} catch (NumberFormatException ex) {
		}
		return def;
	}

	public static StringBuilder clear(StringBuilder buffer) {
		if (buffer != null && 0 < buffer.length()) {
			buffer.delete(0, buffer.length());
		}
		return buffer;
	}
	public static StringBuffer clear(StringBuffer buffer) {
		if (buffer != null && 0 < buffer.length()) {
			buffer.delete(0, buffer.length());
		}
		return buffer;
	}
}

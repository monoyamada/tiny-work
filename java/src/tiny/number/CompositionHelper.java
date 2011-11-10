package tiny.number;

import java.io.IOException;
import java.util.Comparator;

public class CompositionHelper {
	/**
	 * standard dictionary order.
	 * 
	 * @author shirakata
	 * 
	 */
	public static class DictionaryOrder implements Comparator<int[]> {
		@Override
		public int compare(int[] o1, int[] o2) {
			int sign = o1.length > o2.length ? 1 : o1.length == o2.length ? 0 : -1;
			for (int i = 0, n = sign < 0 ? o1.length : o2.length; i < n; ++i) {
				if (o1[i] > o2[i]) {
					return 1;
				} else if (o2[i] < o1[i]) {
					return -1;
				}
			}
			return sign;
		}
	}

	public static void plus(int[] word, int value) {
		int n = word.length;
		while (0 < n--) {
			word[n] += value;
		}
	}
	public static Appendable writeCsv(Appendable writer, int[][] values)
			throws IOException {
		return writeCsv(writer, values, "\n", ", ");
	}
	public static Appendable writeCsv(Appendable writer, int[][] values,
			String row, String col) throws IOException {
		for (int i = 0, n = values.length; i < n; ++i) {
			if (i != 0) {
				writer.append(row);
			}
			writeCsv(writer, values[i], col);
		}
		return writer;
	}
	public static Appendable writeCsv(Appendable writer, int[] value)
			throws IOException {
		return writeCsv(writer, value, ", ");
	}
	public static Appendable writeCsv(Appendable writer, int[] value, String sep)
			throws IOException {
		for (int i = 0, n = value.length; i < n; ++i) {
			if (i != 0) {
				writer.append(sep);
			}
			writer.append(Integer.toString(value[i]));
		}
		return writer;
	}
}

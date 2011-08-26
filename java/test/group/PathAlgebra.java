package group;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.NumberHelper;
import tiny.primitive.LongArrayList;

/**
 * this class holds only the algebra table. the other information of the algebra
 * will be calculated at instance.
 * 
 * @author shirakata
 * 
 */
public class PathAlgebra {
	public static Log LOGGER = LogFactory.getLog(PathAlgebra.class);
	public static final int[][] EMPTY_INT_ARRAY2 = { {} };
	public static final long[][] EMPTY_LONG_ARRAY2 = { {} };
	static Comparator<long[]> CODE_COMPARATOR = new Comparator<long[]>() {
		@Override
		public int compare(long[] o1, long[] o2) {
			return LexicographicalOrders.compare(o1, o2);
		}
	};

	public static Writer writeAlgebra(Writer writer, PathAlgebra paths)
			throws IOException {
		String[] names = paths.getNames().toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		long[][] basis = paths.getBasis().toArray(EMPTY_LONG_ARRAY2);
		String[][] table = paths.toNames(paths.getTable());
		int size = names.length;
		int nameMax = 0;
		for (int i = 0; i < size; ++i) {
			String name = names[i];
			nameMax = Math.max(name.length(), nameMax);
		}
		writer.write("////// variable definitions");
		writeEol(writer);
		for (int i = 0; i < size; ++i) {
			writeSpace(writer, nameMax - names[i].length());
			writer.write(names[i]);
			writer.write(" = ");
			writer.write(toString(basis[i]));
			writeEol(writer);
		}
		writer.write("////// variables");
		writeEol(writer);
		for (int i = 0; i < size; ++i) {
			if (i != 0) {
				writeSpace(writer, 1);
			}
			writeSpace(writer, nameMax - names[i].length());
			writer.write(names[i]);
		}
		writeEol(writer);
		writer.write("////// table");
		writeEol(writer);
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				if (j != 0) {
					writeSpace(writer, 1);
				}
				writeSpace(writer, nameMax - table[i][j].length());
				writer.write(table[i][j]);
			}
			writeEol(writer);
		}
		int zero = paths.getZero();
		int one = paths.getOne();
		writer.write("////// units");
		writeEol(writer);
		if (0 <= zero) {
			writer.write(names[zero]);
			writer.write(" = 0");
			writeEol(writer);
		}
		if (0 <= one) {
			writer.write(names[one]);
			writer.write(" = 1");
			writeEol(writer);
		}
		int[] invs = paths.getInverses();
		writer.write("////// inverses");
		writeEol(writer);
		int ninvs = 0;
		for (int i = 0, n = invs.length; i < n; ++i) {
			if (invs[i] < 0) {
				continue;
			}
			++ninvs;
		}
		writer.write(Integer.toString(ninvs));
		writer.write(" / ");
		writer.write(Integer.toString(invs.length));
		writeEol(writer);
		for (int i = 0, n = invs.length; i < n; ++i) {
			int j = invs[i];
			if (j < 0) {
				continue;
			}
			writer.write(names[i]);
			writeSpace(writer, 1);
			writer.write(names[j]);
			writeEol(writer);
			invs[j] = -1;
		}
		if (ninvs == invs.length) {
			String[][] cls = paths.toNames(paths.getConjugateClasses());
			Arrays.sort(cls, 0, cls.length, new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					return NumberHelper.compare(o1.length, o2.length);
				}
			});
			writer.write("////// conjugates");
			writeEol(writer);
			for (int r = 0, nr = cls.length; r < nr; ++r) {
				String[] row = cls[r];
				for (int c = 0, nc = row.length; c < nc; ++c) {
					if (c != 0) {
						writeSpace(writer, 1);
					}
					String name = row[c];
					writeSpace(writer, nameMax - name.length());
					writer.write(name);
				}
				writeEol(writer);
			}
		}
		return writer;
	}
	protected static void writeEol(Writer writer) throws IOException {
		writer.write('\n');
		writer.flush();
	}
	protected static void writeSpace(Writer writer, int n) throws IOException {
		while (0 < n--) {
			writer.write(' ');
		}
	}
	protected static long[] encodeIndices(int... indices) {
		if ((indices.length & 1) == 1) {
			String msg = Messages.getUnexpectedValue("#indices", "even", "odd");
			throw new IllegalArgumentException(msg);
		}
		int n = indices.length >> 1;
		long[] pairs = new long[n];
		for (int i = 0; i < n; ++i) {
			int j = i << 1;
			pairs[i] = encode(indices[j], indices[j + 1]);
		}
		Arrays.sort(pairs);
		return pairs;
	}
	protected static long encode(long i, int j) {
		return (i << 32) | j;
	}
	protected static int decode_0(long code) {
		return (int) (code >> 32);
	}
	protected static int decode_1(long code) {
		return (int) code;
	}
	protected static long multiplies(long x1, long x2) {
		if (decode_1(x1) == decode_0(x2)) {
			return encode(decode_0(x1), decode_1(x2));
		}
		return -1;
	}
	protected static void multiplies(LongArrayList output, long[] xs1, long[] xs2) {
		output.removeAll();
		for (int i = 0, m = xs1.length; i < m; ++i) {
			long x1 = xs1[i];
			for (int j = 0, n = xs2.length; j < n; ++j) {
				long y = multiplies(x1, xs2[j]);
				if (0 <= y) {
					output.addLast(y);
				}
			}
		}
		Arrays.sort(output.getArray(), 0, output.getLength());
	}

	protected static String toString(long[] code) {
		StringBuilder buffer = new StringBuilder();
		try {
			toString(buffer, code, code.length);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	protected static String toString(long[] code, int n) throws IOException {
		StringBuilder buffer = new StringBuilder();
		toString(buffer, code, n);
		return buffer.toString();
	}
	protected static void toString(Appendable output, long[] code, int n)
			throws IOException {
		for (int i = 0; i < n; ++i) {
			if (i != 0) {
				// output.append(' ');
				// output.append('+');
				// output.append(' ');
			}
			toString(output, code[i]);
		}
	}
	protected static String toString(long code) throws IOException {
		StringBuilder buffer = new StringBuilder();
		toString(buffer, code);
		return buffer.toString();
	}
	protected static void toString(Appendable output, long code)
			throws IOException {
		output.append('(');
		output.append(Integer.toString(decode_0(code)));
		output.append(',');
		// output.append(' ');
		output.append(Integer.toString(decode_1(code)));
		output.append(')');
	}

	protected static void push(List<List<Integer>> table, int i, int j,
			Integer ind) {
		while (table.size() <= i) {
			table.add(new ArrayList<Integer>());
		}
		List<Integer> col = table.get(i);
		while (col.size() <= j) {
			col.add(null);
		}
		col.set(j, ind);
	}

	private List<long[]> basis;
	private List<String> names;
	private int[][] table;

	public PathAlgebra() {
		this(8);
	}
	public PathAlgebra(int size) {
		if (size < 1) {
			size = 8;
		}
		this.basis = new ArrayList<long[]>(size);
		this.names = new ArrayList<String>(size);
		this.table = EMPTY_INT_ARRAY2;
	}

	public List<long[]> getBasis() {
		return this.basis;
	}
	public List<String> getNames() {
		return this.names;
	}
	public int[][] getTable() {
		return this.table;
	}
	public void addBase(String name, int... base) {
		this.names.add(name);
		this.basis.add(encodeIndices(base));
	}
	/**
	 * creates algebra table.
	 * 
	 * @throws IOException
	 */
	public void constructTable() {
		List<long[]> newBasis = new ArrayList<long[]>(this.basis);
		// List<String> newNames = new ArrayList<String>(this.names);
		Map<long[], Integer> codeIndex = new TreeMap<long[], Integer>(
				CODE_COMPARATOR);
		List<List<Integer>> newTable = new ArrayList<List<Integer>>(8);
		for (int i = 0, n = newBasis.size(); i < n; ++i) {
			codeIndex.put(newBasis.get(i), i);
		}
		LongArrayList buffer = new LongArrayList(8);
		for (int i = 0; i < newBasis.size(); ++i) {
			int oldSize = newBasis.size();
			this.constructNew(newBasis, codeIndex, newTable, buffer, i, i);
			for (int k = oldSize; k < newBasis.size(); ++k) {
				this.constructBack(newBasis, codeIndex, newTable, buffer, k, i);
			}
			for (int j = i + 1; j < newBasis.size(); ++j) {
				oldSize = newBasis.size();
				this.constructNew(newBasis, codeIndex, newTable, buffer, i, j);
				for (int k = oldSize; k < newBasis.size(); ++k) {
					this.constructBack(newBasis, codeIndex, newTable, buffer, k, i);
				}
				oldSize = newBasis.size();
				this.constructNew(newBasis, codeIndex, newTable, buffer, j, i);
				for (int k = oldSize; k < newBasis.size(); ++k) {
					this.constructBack(newBasis, codeIndex, newTable, buffer, k, i);
				}
			}
		}
		int newSize = newTable.size();
		int[][] table = new int[newSize][newSize];
		for (int i = 0; i < newSize; ++i) {
			List<Integer> row = newTable.get(i);
			for (int j = 0; j < newSize; ++j) {
				Integer val = row.get(j);
				int value = val.intValue();
				table[i][j] = value;
			}
		}
		this.table = table;
		this.basis = newBasis;
		if (this.names.size() < newSize) {
			List<String> newNames = new ArrayList<String>(newSize);
			newNames.addAll(this.names);
			for (int i = this.names.size(); i < newSize; ++i) {
				newNames.add("$" + i);
			}
			this.names = newNames;
		}
	}
	protected Integer constructNew(List<long[]> basis,
			Map<long[], Integer> codeIndex, List<List<Integer>> table,
			LongArrayList buffer, int ind1, int ind2) {
		multiplies(buffer, basis.get(ind1), basis.get(ind2));
		long[] y = buffer.toArray();
		Integer ind = codeIndex.get(y);
		if (ind == null) {
			ind = basis.size();
			basis.add(y);
			codeIndex.put(y, ind);
			LOGGER.debug(toString(basis.get(ind1)) + " * "
					+ toString(basis.get(ind2)) + " = " + toString(y) + " new");
		} else {
			LOGGER.debug(toString(basis.get(ind1)) + " * "
					+ toString(basis.get(ind2)) + " = " + toString(y));
		}
		push(table, ind1, ind2, ind);
		return ind;
	}
	protected void constructBack(List<long[]> basis,
			Map<long[], Integer> codeIndex, List<List<Integer>> table,
			LongArrayList buffer, int index, int n) {
		for (int i = 0; i < n; ++i) {
			this.constructNew(basis, codeIndex, table, buffer, i, index);
			this.constructNew(basis, codeIndex, table, buffer, index, i);
		}
	}
	public int getZero() {
		List<long[]> basis = this.getBasis();
		for (int i = 0, n = basis.size(); i < n; ++i) {
			if (basis.get(i).length < 1) {
				return i;
			}
		}
		return -1;
	}
	public int getOne() {
		int[][] table = this.getTable();
		OUTER: for (int i = 0, n = table.length; i < n; ++i) {
			int[] row = table[i];
			for (int j = 0; j < n; ++j) {
				if (row[j] != j) {
					continue OUTER;
				}
			}
			for (int j = 0; j < n; ++j) {
				if (table[j][i] != j) {
					continue OUTER;
				}
			}
			return i;
		}
		return -1;
	}
	public String inverts(String name) {
		return this.toName(this.inverts(this.toIndex(name)));
	}
	public int inverts(int index) {
		int one = this.getOne();
		if (one < 0) {
			return -1;
		}
		return this.inverts(index, one);
	}
	protected int inverts(int index, int one) {
		int[][] table = this.getTable();
		int[] row = table[index];
		for (int j = 0, n = row.length; j <= n; ++j) {
			if (row[j] == one) {
				if (index == j) {
					return j;
				} else if (table[j][index] == one) {
					return j;
				}
			}
		}
		return -1;
	}
	public int[] getInverses() {
		int[] output = new int[this.basis.size()];
		Arrays.fill(output, -1);
		int one = this.getOne();
		if (one < 0) {
			return output;
		}
		this.getInverses(output, one);
		return output;
	}
	protected void getInverses(int[] output, int one) {
		List<long[]> basis = this.getBasis();
		int[][] table = this.getTable();
		OUTER: for (int i = 0, n = basis.size(); i < n; ++i) {
			int[] row = table[i];
			for (int j = 0; j <= i; ++j) {
				if (row[j] == one) {
					if (i == j) {
						output[i] = i;
						continue OUTER;
					} else if (table[j][i] == one) {
						output[i] = j;
						output[j] = i;
						continue OUTER;
					}
				}
			}
		}
	}
	public int[][] getConjugateClasses() {
		int one = this.getOne();
		if (one < 0) {
			return EMPTY_INT_ARRAY2;
		}
		int[] invs = new int[this.getBasis().size()];
		this.getInverses(invs, one);
		for (int i = 0, n = invs.length; i < n; ++i) {
			if (invs[i] < 0) {
				return EMPTY_INT_ARRAY2;
			}
		}
		return this.getConjugateClasses(invs, one);
	}
	protected int[][] getConjugateClasses(int[] invs, int one) {
		List<long[]> basis = this.getBasis();
		int[][] table = this.getTable();
		int n = basis.size();
		BitSet done = new BitSet(n);
		BitSet cl = new BitSet(n);
		int[][] output = new int[n][];
		int size = 0;
		for (int i = 0; i < n; ++i) {
			if (done.get(i)) {
				continue;
			}
			cl.clear();
			for (int j = 0; j < n; ++j) {
				int l = invs[j];
				int r = table[i][j];
				l = table[l][r];
				done.set(l, true);
				cl.set(l, true);
			}
			int[] array = new int[cl.cardinality()];
			toIndices(array, cl);
			output[size++] = array;
		}
		if (output.length == size) {
			return output;
		}
		return ArrayHelper.sub(output, 0, size);
	}
	protected int toIndices(int[] output, BitSet values) {
		int size = 0;
		for (int i = values.nextSetBit(0), n = output.length; 0 <= i && size <= n; i = values
				.nextSetBit(++i)) {
			output[size++] = i;
		}
		return size;
	}
	public int multiplies(int i, int j) {
		return this.getTable()[i][j];
	}
	public String multiplies(String i, String j) {
		return this.toName(this.multiplies(this.toIndex(i), this.toIndex(j)));
	}
	public int conjugates(int i, int j) {
		int one = this.getOne();
		if (one < 0) {
			return -1;
		}
		int[][] table = this.getTable();
		j = table[i][j];
		i = this.inverts(i, one);
		if (i < 0) {
			return -1;
		}
		return table[j][i];
	}
	public String conjugates(String i, String j) {
		return this.toName(this.conjugates(this.toIndex(i), this.toIndex(j)));
	}
	public int toIndex(String name) {
		return this.getNames().indexOf(name);
	}
	public String toName(int index) {
		List<String> names = this.getNames();
		if (index < 0 || names.size() < index) {
			return null;
		}
		return names.get(index);
	}
	public String[][] toNames(int[][] table) {
		int n = table.length;
		String[][] output = new String[n][];
		for (int r = 0, nr = table.length; r < nr; ++r) {
			output[r] = new String[table[r].length];
		}
		this.toNames(output, table);
		return output;
	}
	protected void toNames(String[][] output, int[][] table) {
		List<String> names = this.names;
		int nr = table.length;
		for (int r = 0; r < nr; ++r) {
			int[] row = table[r];
			int nc = row.length;
			for (int c = 0; c < nc; ++c) {
				int index = table[r][c];
				if (index < 0) {
					continue;
				}
				output[r][c] = names.get(index);
			}
		}
	}
}

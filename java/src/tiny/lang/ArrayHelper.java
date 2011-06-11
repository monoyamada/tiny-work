package tiny.lang;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import tiny.function.BinaryPredicate;
import tiny.function.Combinadic;
import tiny.function.ComparableOrder;
import tiny.function.LexicographicalOrder;
import tiny.function.Permutation;

public class ArrayHelper {
	public static final long[] EMPTY_LONG_ARRAY = {};
	public static final int[] EMPTY_INT_ARRAY = {};
	public static final short[] EMPTY_SHORT_ARRAY = {};
	public static final byte[] EMPTY_BYTE_ARRAY = {};
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};
	public static final double[] EMPTY_DOUBLE_ARRAY = {};
	public static final float[] EMPTY_FLOAT_ARRAY = {};
	public static final String[] EMPTY_STRING_ARRAY = {};
	public static final File[] EMPTY_FILE_ARRAY = {};

	private static final ThreadLocal<Random> RANDMO_HOLDER = new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			return new Random();
		}
	};

	public static Random getRandom() {
		return RANDMO_HOLDER.get();
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(T[] array, int size) {
		return (T[]) Array.newInstance(array.getClass().getComponentType(), size);
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}
	@SuppressWarnings("unchecked")
	public static <T> T[][] newArray(Class<T> type, int size0, int size1) {
		return (T[][]) Array.newInstance(type, new int[] { size0, size1 });
	}
	public static Object newArrayN(Class<?> type, int... sizes) {
		return Array.newInstance(type, sizes);
	}

	public static <X> Comparator<X[]> getLexicographicalOrder(Comparator<X> order) {
		return new LexicographicalOrder<X>(order);
	}
	public static <X extends Comparable<? super X>> Comparator<X[]> getLexicographicalOrder() {
		final Comparator<X> order = new ComparableOrder<X>();
		return new LexicographicalOrder<X>(order);
	}

	public static <T> T[] addLast(T[] array, T x) {
		final int n = array.length;
		final T[] newArray = ArrayHelper.newArray(array, n + 1);
		System.arraycopy(array, 0, newArray, 0, n);
		newArray[n] = x;
		return newArray;
	}
	public static <T> T[] addFirst(T x, T[] array) {
		final int n = array.length;
		final T[] newArray = ArrayHelper.newArray(array, n + 1);
		System.arraycopy(array, 0, newArray, 1, n);
		newArray[0] = x;
		return newArray;
	}

	public static <T0, T1> boolean equals(T0[] array0, int begin0, T1[] array1,
			int begin1, int size) {
		return ArrayHelper.equals(array0, begin0, array1, begin1, size,
				ObjectHelper.DEFAULT_EQUALITY);
	}
	public static <T0, T1> boolean equals(T0[] array0, int begin0, T1[] array1,
			int begin1, int size, BinaryPredicate<? super T0, ? super T1> equality) {
		if (equality == null) {
			equality = ObjectHelper.DEFAULT_EQUALITY;
		}
		if (array0 == array1) {
			return true;
		} else if (array0 == null || array1 == null) {
			return false;
		}
		for (int i = 0; i < size; ++i) {
			try {
				if (!equality.evaluateBoolean(array0[begin0 + i], array1[begin1 + i])) {
					return false;
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return true;
	}
	public static <T> T[] clone(T[] array, int begin, int end) {
		final int size = end - begin;
		if (size < 1) {
			return ArrayHelper.newArray(array, 0);
		} else if (begin == 0 && end == array.length) {
			// maybe fast.
			return array.clone();
		}
		final T[] newArray = ArrayHelper.newArray(array, size);
		System.arraycopy(array, begin, newArray, 0, size);
		return newArray;
	}

	public static int[] add(int[] array, int value) {
		assert array != null;
		final int n = array.length;
		switch (n) {
		case 0:
			return new int[] { value };
		case 1:
			return new int[] { array[0], value };
		case 2:
			return new int[] { array[0], array[1], value };
		case 3:
			return new int[] { array[0], array[1], array[2], value };
		case 4:
			return new int[] { array[0], array[1], array[2], array[3], value };
		default:
			break;
		}
		final int[] newArray = new int[n + 1];
		System.arraycopy(array, 0, newArray, 0, n);
		newArray[n] = value;
		return newArray;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] array, T value) {
		assert array != null;
		final int n = array.length;
		final T[] newArray = (T[]) Array.newInstance(array.getClass()
				.getComponentType(), n + 1);
		System.arraycopy(array, 0, newArray, 0, n);
		newArray[n] = value;
		return newArray;
	}

	public static int[] add(int value, int[] array) {
		assert array != null;
		final int n = array.length;
		switch (n) {
		case 0:
			return new int[] { value };
		case 1:
			return new int[] { value, array[0] };
		case 2:
			return new int[] { value, array[0], array[1] };
		case 3:
			return new int[] { value, array[0], array[1], array[2] };
		case 4:
			return new int[] { value, array[0], array[1], array[2], array[3] };
		default:
			break;
		}
		final int[] newArray = new int[n + 1];
		System.arraycopy(array, 0, newArray, 1, n);
		newArray[0] = value;
		return newArray;
	}

	public static int[] addAll(int[] x, int[] y) {
		assert x != null && y != null;
		if (x.length < 1) {
			return y;
		} else if (y.length < 1) {
			return x;
		}
		final int[] z = new int[x.length + y.length];
		System.arraycopy(x, 0, z, 0, x.length);
		System.arraycopy(y, 0, z, x.length, y.length);
		return z;
	}
	public static <T> void addAll(Collection<? super T> output, T[] array) {
		for (int i = 0, n = array == null ? 0 : array.length; i < n; ++i) {
			output.add(array[i]);
		}
	}

	public static <Key, Value> void putAll(Map<? super Key, Value> output,
			Key[] array, Value value) {
		for (int i = 0, n = array == null ? 0 : array.length; i < n; ++i) {
			output.put(array[i], value);
		}
	}

	public static int[] sub(int[] array, int begin, int end) {
		final int n = end - begin;
		if (n < 1) {
			return ArrayHelper.EMPTY_INT_ARRAY;
		} else if (n == array.length) {
			return array.clone();
		}
		final int[] newArray = new int[n];
		System.arraycopy(array, begin, newArray, 0, n);
		return newArray;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] sub(T[] array, int begin, int end) {
		final int n = end - begin;
		if (n == array.length) {
			return array.clone();
		}
		final T[] newArray = (T[]) Array.newInstance(array.getClass()
				.getComponentType(), n);
		System.arraycopy(array, begin, newArray, 0, n);
		return newArray;
	}

	public static void swap(Object[] array, int i, int k) {
		final Object tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(long[] array, int i, int k) {
		final long tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(int[] array, int i, int k) {
		final int tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(short[] array, int i, int k) {
		final short tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(char[] array, int i, int k) {
		final char tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(byte[] array, int i, int k) {
		final byte tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(boolean[] array, int i, int k) {
		final boolean tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(double[] array, int i, int k) {
		final double tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}
	public static void swap(float[] array, int i, int k) {
		final float tmp = array[k];
		array[k] = array[i];
		array[i] = tmp;
	}

	public static void reverse(Object[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(long[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(int[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(short[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(byte[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(boolean[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(double[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}
	public static void reverse(float[] array) {
		ArrayHelper.reverse(array, 0, array.length);
	}

	public static void reverse(Object[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(long[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(int[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(short[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(byte[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(boolean[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(double[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}
	public static void reverse(float[] array, int begin, int end) {
		while (begin < --end) {
			ArrayHelper.swap(array, begin++, end);
		}
	}

	public static <T extends Comparable<? super T>> boolean nextPermution(
			T[] array, int begin, int end) {
		return Permutation.nextPermution(array, begin, end);
	}
	public static <T> boolean nextPermution(T[] array, int begin, int end,
			Comparator<? super T> pred) {
		return Permutation.nextPermution(array, begin, end, pred);
	}
	public static boolean nextPermution(long[] array, int begin, int end) {
		return Permutation.nextPermution(array, begin, end);
	}
	public static boolean nextPermution(int[] array, int begin, int end) {
		return Permutation.nextPermution(array, begin, end);
	}

	public static <T extends Comparable<? super T>> boolean prevPermution(
			T[] array, int begin, int end) {
		return Permutation.prevPermution(array, begin, end);
	}
	public static <T> boolean prevPermution(T[] array, int begin, int end,
			Comparator<? super T> pred) {
		return Permutation.prevPermution(array, begin, end, pred);
	}
	public static boolean prevPermution(long[] array, int begin, int end) {
		return Permutation.prevPermution(array, begin, end);
	}
	public static boolean prevPermution(int[] array, int begin, int end) {
		return Permutation.prevPermution(array, begin, end);
	}

	public static int getCombinationSize(int setSize, int chooseSize) {
		return Combinadic.getCombinationSize(setSize, chooseSize);
	}
	public static void getCombination(int[] array, int setSize, int chooseSize,
			int index) {
		Combinadic.getCombination(array, 0, setSize, chooseSize, index);
	}
	public static void getCombination(int[] array, int begin, int setSize,
			int chooseSize, int index) {
		Combinadic.getCombination(array, setSize, chooseSize, index);
	}

	public static long[] sequence(long begin, long end) {
		final int sign = ObjectHelper.compare(end, begin);
		if (sign == 0) {
			return ArrayHelper.EMPTY_LONG_ARRAY;
		}
		final int size = (int) (0 < sign ? end - begin : begin - end);
		final long[] array = new long[size];
		ArrayHelper.sequence(array, 0, size, sign, begin);
		return array;
	}
	public static void sequence(long[] output, int begin, int end, long scale,
			long value) {
		for (; begin < end; ++begin) {
			output[begin] = scale * (value++);
		}
	}

	public static int[] sequence(int begin, int end) {
		final int sign = ObjectHelper.compare(end, begin);
		if (sign == 0) {
			return ArrayHelper.EMPTY_INT_ARRAY;
		}
		final int size = (int) (0 < sign ? end - begin : begin - end);
		final int[] array = new int[size];
		ArrayHelper.sequence(array, 0, size, sign, begin);
		return array;
	}
	public static void sequence(int[] output, int begin, int end, int scale,
			int value) {
		for (; begin < end; ++begin) {
			output[begin] = scale * (value++);
		}
	}

	/**
	 * @param array
	 * @param value
	 * @return
	 */
	public static int indexOf(int[] array, int value) {
		switch (array.length) {
		case 0:
			return -1;
		case 1:
			return array[0] == value ? 0 : -1;
		case 2:
			return array[0] == value ? 0 : array[1] == value ? 1 : -1;
		case 3:
			return array[0] == value ? 0 : array[1] == value ? 1
					: array[2] == value ? 2 : -1;
		case 4:
			return array[0] == value ? 0 : array[1] == value ? 1
					: array[2] == value ? 2 : array[3] == value ? 3 : -1;
		default:
			return ArrayHelper.indexOf(array, 0, array.length, value);
		}
	}
	/**
	 * @param array
	 * @param value
	 * @return
	 */
	public static int indexOf(char[] array, char value) {
		switch (array.length) {
		case 0:
			return -1;
		case 1:
			return array[0] == value ? 0 : -1;
		case 2:
			return array[0] == value ? 0 : array[1] == value ? 1 : -1;
		case 3:
			return array[0] == value ? 0 : array[1] == value ? 1
					: array[2] == value ? 2 : -1;
		case 4:
			return array[0] == value ? 0 : array[1] == value ? 1
					: array[2] == value ? 2 : array[3] == value ? 3 : -1;
		default:
			return ArrayHelper.indexOf(array, 0, array.length, value);
		}
	}

	/**
	 * @param array
	 * @param begin
	 * @param end
	 * @param value
	 * @return
	 */
	public static int indexOf(int[] array, int begin, int end, int value) {
		for (; begin < end; ++begin) {
			if (array[begin] == value) {
				return begin;
			}
		}
		return -1;
	}
	/**
	 * @param array
	 * @param begin
	 * @param end
	 * @param value
	 * @return
	 */
	public static int indexOf(char[] array, int begin, int end, char value) {
		for (; begin < end; ++begin) {
			if (array[begin] == value) {
				return begin;
			}
		}
		return -1;
	}

	/**
	 * @param array
	 * @param value
	 * @return
	 */
	public static int indexOf(Object[] array, Object value) {
		return ArrayHelper.indexOf(array, 0, array.length, value);
	}
	/**
	 * @param array
	 * @param begin
	 * @param end
	 * @param value
	 * @return
	 */
	public static int indexOf(Object[] array, int begin, int end, Object value) {
		for (; begin < end; ++begin) {
			if (ObjectHelper.equals(array[begin], value)) {
				return begin;
			}
		}
		return -1;
	}

	public static long[] ensureSize(long[] array, int size) {
		final int oldSize = array != null ? array.length : 0;
		if (size <= oldSize) {
			return array;
		}
		final long[] newArray = new long[size];
		if (array != null && 0 < array.length) {
			System.arraycopy(array, 0, newArray, 0, array.length);
		}
		return newArray;
	}

	/**
	 * <code>
	 * array[0] <= ... <= array[index - 1] < value <= array[index] <= ...  <= array[length - 1]
	 * </code>
	 * 
	 * @param array
	 *          sorted array with the order <=.
	 * @param value
	 * @return
	 */
	public static int getLowerBound(int[] array, int value) {
		return getLowerBound(array, 0, array.length, value);
	}
	/**
	 * <code>
	 * array[begin] <= ... <= array[index - 1] < value <= array[index] <= ... <= array[end - 1]
	 * </code>
	 * 
	 * @param array
	 *          sorted array with the order <=.
	 * @param value
	 * @return
	 */
	public static int getLowerBound(int[] array, int begin, int end, int value) {
		int d = end - begin;
		switch (d) {
		case 0:
			return begin;
		case 1:
			return array[begin] < value ? begin + 1 : begin;
		default:
			// if (false) {
			// final int m = begin + (d >> 1);
			// if (array[m] < value) {
			// return getLowerBound(array, m, end, value);
			// } else {
			// return getLowerBound(array, begin, m, value);
			// }
			// }
			break;
		}
		for (int m = 0; 1 < d; d = end - begin) {
			m = begin + (d >> 1);
			if (array[m] < value) {
				begin = m;
			} else {
				end = m;
			}
		}
		return array[begin] < value ? end : begin;
	}

	public static void shuffle(Object[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(Object[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(Object[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(double[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(double[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(double[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(float[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(float[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(float[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(long[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(long[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(long[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(int[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(int[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(int[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(short[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(short[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(short[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(byte[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(byte[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(byte[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}
	public static void shuffle(char[] output) {
		shuffle(output, 0, output.length, getRandom());
	}
	public static void shuffle(char[] output, Random random) {
		shuffle(output, 0, output.length, random);
	}
	public static void shuffle(char[] output, int begin, int end, Random random) {
		for (end -= begin; 0 < --end;) {
			swap(output, begin + random.nextInt(end), end);
		}
	}

	public static void sequence(long[] output, long lowest) {
		sequence(output, 0, output.length, lowest);
	}
	public static void sequence(long[] output, int begin, int end, long lowest) {
		while (begin < end) {
			output[begin++] = lowest++;
		}
	}
	public static void sequence(int[] output, int lowest) {
		sequence(output, 0, output.length, lowest);
	}
	public static void sequence(int[] output, int begin, int end, int lowest) {
		while (begin < end) {
			output[begin++] = lowest++;
		}
	}
	public static void sequence(short[] output, short lowest) {
		sequence(output, 0, output.length, lowest);
	}
	public static void sequence(short[] output, int begin, int end, short lowest) {
		while (begin < end) {
			output[begin++] = lowest++;
		}
	}
	public static void sequence(byte[] output, byte lowest) {
		sequence(output, 0, output.length, lowest);
	}
	public static void sequence(byte[] output, int begin, int end, byte lowest) {
		while (begin < end) {
			output[begin++] = lowest++;
		}
	}
	public static void sequence(char[] output, char lowest) {
		sequence(output, 0, output.length, lowest);
	}
	public static void sequence(char[] output, int begin, int end, char lowest) {
		while (begin < end) {
			output[begin++] = lowest++;
		}
	}

	/**
	 * [1,2,3] |-> [3,1,2]
	 * 
	 * @param array
	 * @param begin
	 * @param end
	 */
	public static void rotate_right(Object[] array, int begin, int end) {
		int n = end - begin;
		switch (n) {
		case 0:
		case 1:
			break;
		case 2:
			swap(array, begin, --end);
			break;
		case 3: {
			Object x = array[--end];
			array[end] = array[end - 1];
			array[end - 1] = array[begin];
			array[begin] = x;
		}
			break;
		default: {
			Object x = array[--end];
			System.arraycopy(array, begin, end, begin + 1, n);
			array[begin] = x;
		}
			break;
		}
	}
}

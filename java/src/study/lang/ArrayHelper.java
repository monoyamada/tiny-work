package study.lang;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import study.function.Combinadic;
import study.function.ComparableOrder;
import study.function.IfBinaryPredicate;
import study.function.LexicographicalOrder;
import study.function.Permutation;

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

	public static Object[] addBack(Object[] array, Object x) {
		final int n = array.length;
		final Object[] newArray = ArrayHelper.newArray(array, n + 1);
		System.arraycopy(array, 0, newArray, 0, n);
		newArray[n] = x;
		return newArray;
	}
	public static Object[] addFront(Object x, Object[] array) {
		final int n = array.length;
		final Object[] newArray = ArrayHelper.newArray(array, n + 1);
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
			int begin1, int size, IfBinaryPredicate<? super T0, ? super T1> equality) {
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

	public static int[] addAll(int[] x, int[] y) {
		assert x!=null&&y!=null;
		if(x.length<1){
			return y;
		}else if(y.length<1){
			return x;
		}
		final int[] z = new int[x.length+y.length];
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
}

package study.function;

import java.util.Comparator;

import study.lang.ArrayHelper;
import study.lang.ObjectHelper;

public class Permutation {
	public static <T extends Comparable<? super T>> boolean nextPermution(
			T[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (ObjectHelper.compare(array[i], array[k]) < 0) {
				int m = end;
				while (ObjectHelper.compare(array[--m], array[i]) <= 0) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static <T> boolean nextPermution(T[] array, int begin, int end,
			Comparator<? super T> pred) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (pred.compare(array[i], array[k]) < 0) {
				int m = end;
				while (pred.compare(array[--m], array[i]) <= 0) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static boolean nextPermution(long[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (array[i] < array[k]) {
				int m = end;
				while (array[--m] <= array[i]) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static boolean nextPermution(int[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (array[i] < array[k]) {
				int m = end;
				while (array[--m] <= array[i]) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}

	public static <T extends Comparable<? super T>> boolean prevPermution(
			T[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (ObjectHelper.compare(array[k], array[i]) < 0) {
				int m = end;
				while (ObjectHelper.compare(array[i], array[--m]) <= 0) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static <T> boolean prevPermution(T[] array, int begin, int end,
			Comparator<? super T> pred) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (pred.compare(array[k], array[i]) < 0) {
				int m = end;
				while (pred.compare(array[i], array[--m]) <= 0) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static boolean prevPermution(long[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (array[k] < array[i]) {
				int m = end;
				while (array[i] <= array[--m]) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
	public static boolean prevPermution(int[] array, int begin, int end) {
		int i = begin + 1;
		if (end <= i) {
			return false;
		}
		i = end - 1;
		for (;;) {
			int k = i--;
			if (array[k] < array[i]) {
				int m = end;
				while (array[i] <= array[--m]) {
				}
				ArrayHelper.swap(array, i, m);
				ArrayHelper.reverse(array, k, end);
				return true;
			}
			if (i == begin) {
				ArrayHelper.reverse(array, begin, end);
				return false;
			}
		}
	}
}

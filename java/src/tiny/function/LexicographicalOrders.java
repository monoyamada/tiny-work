package tiny.function;

import java.util.Comparator;

import tiny.lang.ObjectHelper;

public class LexicographicalOrders {
	public static <X> int compare(X[] array1, X[] array2, Comparator<X> order) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		final int n1 = array1.length;
		final int n2 = array2.length;
		if (n1 > n2) {
			return 1;
		} else if (n1 < n2) {
			return -1;
		}
		return LexicographicalOrders.compare(array1, 0, array2, 0, n1, order);
	}
	public static <X> int compare(X[] array1, int begin1, X[] array2, int begin2,
			int size, Comparator<X> order) {
		if (array1 == array2 || size < 1) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		for (int i = 0; i < size; ++i) {
			final int c = order.compare(array1[begin1 + i], array2[begin2 + i]);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}

	public static <X extends Comparable<? super X>> int compare(X[] array1,
			X[] array2) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		final int n1 = array1.length;
		final int n2 = array2.length;
		if (n1 > n2) {
			return 1;
		} else if (n1 < n2) {
			return -1;
		}
		return LexicographicalOrders.compare(array1, 0, array2, 0, n1);
	}
	public static <X extends Comparable<? super X>> int compare(X[] array1,
			int begin1, X[] array2, int begin2, int size) {
		if (array1 == array2 || size < 1) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		for (int i = 0; i < size; ++i) {
			final int c = ObjectHelper
					.compare(array1[begin1 + i], array2[begin2 + i]);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}

	public static int compare(long[] array1, long[] array2) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		final int n1 = array1.length;
		final int n2 = array2.length;
		if (n1 > n2) {
			return 1;
		} else if (n1 < n2) {
			return -1;
		}
		return LexicographicalOrders.compare(array1, 0, array2, 0, n1);
	}
	public static int compare(int[] array1, int[] array2) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		final int n1 = array1.length;
		final int n2 = array2.length;
		if (n1 > n2) {
			return 1;
		} else if (n1 < n2) {
			return -1;
		}
		return LexicographicalOrders.compare(array1, 0, array2, 0, n1);
	}

	public static int compare(long[] array1, int begin1, long[] array2,
			int begin2, int size) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		for (int i = 0; i < size; ++i) {
			final int c = ObjectHelper
					.compare(array1[begin1 + i], array2[begin2 + i]);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}
	public static int compare(int[] array1, int begin1, int[] array2, int begin2,
			int size) {
		if (array1 == array2) {
			return 0;
		} else if (array1 == null) {
			return 1;
		} else if (array2 == null) {
			return -1;
		}
		for (int i = 0; i < size; ++i) {
			final int c = ObjectHelper
					.compare(array1[begin1 + i], array2[begin2 + i]);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}
}

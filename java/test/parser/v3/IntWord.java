package parser.v3;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.StringHelper;

public abstract class IntWord<T extends IntWord<T>> implements Comparable<T>,
		Cloneable, Monoid<T> {
	public static final Comparator<IntWord<?>> COMPARATOR = new Comparator<IntWord<?>>() {
		@Override
		public int compare(IntWord<?> o1, IntWord<?> o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			}
			return IntWord.compare(o1.word, o2.word);
		}
	};

	public static int compare(int[] xs, int[] ys) {
		if (xs == ys) {
			return 0;
		} else if (xs == null) {
			return 1;
		} else if (ys == null) {
			return -1;
		}
		return LexicographicalOrders.compare(xs, ys);
	}

	public static int[] times(int[] xs, int[] ys) {
		if (xs == null || ys == null) {
			return null;
		} else if (xs.length < 1) {
			return ys;
		} else if (ys.length < 1) {
			return xs;
		}
		int n = xs.length + ys.length;
		int[] word = new int[n];
		IntWord.setAll(word, 0, xs);
		IntWord.setAll(word, xs.length, ys);
		return word;
	}
	public static int[] times(int[] xs, int y) {
		if (xs == null) {
			return null;
		} else if (xs.length < 1) {
			return new int[] { y };
		}
		int[] word = new int[xs.length + 1];
		IntWord.setAll(word, 0, xs);
		word[xs.length] = y;
		return word;
	}
	public static int[] times(int x, int[] ys) {
		if (ys == null) {
			return null;
		} else if (ys.length < 1) {
			return new int[] { x };
		}
		int[] word = new int[ys.length + 1];
		IntWord.setAll(word, 1, ys);
		word[0] = x;
		return word;
	}

	public static int[] plus(int[] xs, int[] ys) {
		if (xs == null || ys == null) {
			return null;
		} else if (xs.length < 1) {
			return ys;
		} else if (ys.length < 1) {
			return xs;
		}
		if (xs.length < ys.length) {
			int[] zs = xs;
			xs = ys;
			ys = zs;
		}
		int[] word = xs.clone();
		for (int yn = ys.length; 0 < yn--;) {
			word[yn] += ys[yn];
		}
		return word;
	}
	public static int[] increment(int[] xs, int y) {
		if (xs == null) {
			return null;
		}
		int[] word = null;
		if (xs.length <= y) {
			word = new int[y + 1];
			IntWord.setAll(word, 0, xs);
		} else {
			word = xs.clone();
		}
		word[y] += 1;
		return word;
	}
	public static int[] increment(int x, int[] ys) {
		return IntWord.increment(ys, x);
	}
	public static int[] negate(int[] xs) {
		if (xs == null) {
			return null;
		}
		int xn = xs.length;
		if (xn < 1) {
			return xs;
		}
		int[] word = xs.clone();
		while (0 < xn--) {
			word[xn] = -xs[xn];
		}
		return word;
	}
	public static int[] setAll(int[] array, int index, int[] values) {
		System.arraycopy(values, 0, array, index, values.length);
		return array;
	}

	final int[] word;

	public IntWord() {
		this(ArrayHelper.EMPTY_INT_ARRAY);
	}
	public IntWord(int[] word) {
		this.word = word != null ? word : ArrayHelper.EMPTY_INT_ARRAY;
	}
	protected T that() {
		return this.toThat(this);
	}
	@SuppressWarnings("unchecked")
	protected T toThat(Object x) {
		return (T) x;
	}
	protected abstract T newThat(int[] word);
	/**
	 * shallow copy
	 */
	@Override
	public T clone() {
		try {
			return this.toThat(super.clone());
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.word);
	}
	@Override
	public boolean equals(Object x) {
		return this.equalWord(this.toThat(x));
	}
	public boolean equalWord(T x) {
		if (x == null) {
			return false;
		} else if (this == x) {
			return true;
		}
		return ArrayHelper.equalArray(this.word, x.word);
	}
	@Override
	public int compareTo(T y) {
		if (y == null) {
			return -1;
		}
		return IntWord.compare(this.word, y.word);
	}
	@Override
	public boolean isOne() {
		return this.word.length < 1;
	}
	@Override
	public T times(T y) {
		if (y == null) {
			return y;
		} else if (this.isOne()) {
			return y;
		} else if (y.isOne()) {
			return this.that();
		}
		int[] word = IntWord.times(this.word, y.word);
		return this.newThat(word);
	}
	public T times(int y) {
		int[] word = IntWord.times(this.word, y);
		return this.newThat(word);
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		try {
			this.toString(buffer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public Appendable toString(Appendable output) throws IOException {
		return StringHelper.join(output.append('['), this.word, ", ").append(']');
	}
	public Appendable toTex(Appendable output) throws IOException {
		return StringHelper.join(output.append("\\word{"), this.word, ",").append(
				'}');
	}
}

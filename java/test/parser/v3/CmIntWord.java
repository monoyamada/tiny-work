package parser.v3;

import java.io.IOException;

import tiny.lang.ArrayHelper;

public abstract class CmIntWord<T extends CmIntWord<T>> extends IntWord<T> implements
		Group<T> {
	protected static int[] trim(int[] word) {
		int n = word.length;
		while (0 < n--) {
			if (word[n] != 0) {
				if (++n < word.length) {
					word = ArrayHelper.sub(word, 0, n);
				}
				return word;
			}
		}
		return ArrayHelper.EMPTY_INT_ARRAY;
	}
	public CmIntWord() {
		this(ArrayHelper.EMPTY_INT_ARRAY);
	}
	public CmIntWord(int[] word) {
		super(CmIntWord.trim(word));
	}
	@Override
	public boolean isInverse(T y) {
		int[] xs = this.word;
		int[] ys = y.word;
		if (xs.length < ys.length) {
			int[] zs = xs;
			xs = ys;
			ys = zs;
		}
		int i = 0;
		for (int n = ys.length; i < n; ++i) {
			if (xs[i] != -ys[i]) {
				return false;
			}
		}
		for (int n = xs.length; i < n; ++i) {
			if (xs[i] != 0) {
				return false;
			}
		}
		return true;
	}
	@Override
	public T times(T y) {
		if (y == null) {
			return null;
		} else if (this.isOne()) {
			return y;
		} else if (y.isOne()) {
			return this.that();
		}
		int[] word = IntWord.plus(this.word, y.word);
		return this.newThat(CmIntWord.trim(word));
	}
	@Override
	public T times(int y) {
		int[] word = IntWord.increment(this.word, y);
		return this.newThat(CmIntWord.trim(word));
	}
	@Override
	public T invert() {
		if (this.isOne()) {
			return this.that();
		}
		int[] word = IntWord.negate(this.word);
		return this.newThat(CmIntWord.trim(word));
	}
	public Appendable toPolynomial(Appendable output, String variable)
			throws IOException {
		output.append('[');
		int[] xs = this.word;
		boolean first = true;
		for (int xn = xs.length; 0 < xn--;) {
			int x = xs[xn];
			if (x == 0) {
				continue;
			} else if (first) {
				first = false;
			} else {
				output.append(" + ");
			}
			if (xn == 0) {
				output.append(Integer.toString(x));
			} else {
				if (x == 1) {
				} else if (x == -1) {
					output.append('-');
				} else {
					output.append(Integer.toString(x));
				}
				output.append(variable);
				if (1 < xn) {
					output.append('^').append(Integer.toString(xn));
				}
			}
		}
		return output.append(']');
	}
}

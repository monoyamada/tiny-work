package tiny.number;

import java.util.ArrayList;
import java.util.List;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public class DictionaryComposition implements NumberComposition {
	public static final int[][] EMPTY_INT_ARRAY2 = {};

	public long size(int n, int k) {
		return DictionaryComposition.binomSimple(n + k - 1, k - 1);
	}
	public static long binomSimple(int n, int k) {
		if (n < 0) {
			String msg = Messages.getUnexpectedValue("n", "non-negative", n);
			throw new IllegalArgumentException(msg);
		} else if (k < 0) {
			String msg = Messages.getUnexpectedValue("k", "non-negative", k);
			throw new IllegalArgumentException(msg);
		} else if (n < k) {
			String msg = Messages.getUnexpectedValue("n-k", "non-negative", n - k);
			throw new IllegalArgumentException(msg);
		}
		if ((n >> 1) < k) {
			k = n - k;
		}
		switch (k) {
		case 0:
			return 1;
		case 1:
			return n;
		case 2:
			return (n * (n - 1)) >> 1;
		default:
			break;
		}
		double rn = n;
		double c = rn-- / k--;
		while (0 < k) {
			c *= rn-- / k--;
		}
		return (long) Math.round(c);
	}
	public static int sum(int[] x) {
		int n = x.length;
		switch (n) {
		case 0:
			return 0;
		case 1:
			return 1;
		default:
			break;
		}
		int sum = x[--n];
		while (0 < n) {
			sum += x[--n];
		}
		return sum;
	}
	public static boolean even(int x){
		return (x & 1) == 0;
	}
	public static boolean odd(int x){
		return (x & 1) != 0;
	}
	public static int hammingDistance(int[] x, int[] y) {
		if (x == null || y == null) {
			String msg = Messages.getNull("x or y");
			throw new IllegalArgumentException(msg);
		} else if (x.length != y.length) {
			String msg = Messages.getUnexpectedValue("y.length", x.length, y.length);
			throw new IllegalArgumentException(msg);
		}
		int d = 0;
		for (int n = x.length; 0 < n--;) {
			d += Math.abs(x[n] - y[n]);
		}
		return d;
	}
	protected static boolean move(int[] value, int from, int to) {
		if (0 < value[from]) {
			if (from != to) {
				value[to] += 1;
				value[from] -= 1;
			}
			return true;
		}
		return false;
	}
	protected static void moveAll(int[] value, int from, int to) {
		if (from != to) {
			value[to] += value[from];
			value[from] = 0;
		}
	}
	/**
	 * @param n
	 *          the number of balls.
	 * @param k
	 *          the number of boxes.
	 * @return initial value of the sequence of this class providing.
	 */
	public int[] first(int n, int k) {
		if (n < 0) {
			String msg = Messages.getUnexpectedValue("n", "non-negative", n);
			throw new IllegalArgumentException(msg);
		} else if (k < 0) {
			String msg = Messages.getUnexpectedValue("k", "non-negative", k);
			throw new IllegalArgumentException(msg);
		}
		switch (k) {
		case 0:
			return ArrayHelper.EMPTY_INT_ARRAY;
		case 1:
			return new int[] { n };
		default:
			break;
		}
		int[] value = new int[k];
		value[k - 1] = n;
		return value;
	}
	/**
	 * @param n
	 *          the number of balls.
	 * @param k
	 *          the number of boxes.
	 * @return initial value of the sequence of this class providing.
	 */
	public int[] last(int n, int k) {
		if (n < 0) {
			String msg = Messages.getUnexpectedValue("n", "non-negative", n);
			throw new IllegalArgumentException(msg);
		} else if (k < 0) {
			String msg = Messages.getUnexpectedValue("k", "non-negative", k);
			throw new IllegalArgumentException(msg);
		}
		switch (k) {
		case 0:
			return ArrayHelper.EMPTY_INT_ARRAY;
		case 1:
			return new int[] { n };
		default:
			break;
		}
		int[] value = new int[k];
		value[0] = n;
		return value;
	}
	/**
	 * @param value
	 *          mutable
	 * @return <code>true</code> if success to get next value <code>false</code>
	 *         otherwise.
	 */
	public boolean next(int[] value) {
		for (int n = value.length; 1 < n--;) {
			if (move(value, n, n - 1)) {
				moveAll(value, n, value.length - 1);
				return true;
			}
		}
		return false;
	}
	/**
	 * @param value
	 *          mutable
	 * @return <code>true</code> if success to get next value <code>false</code>
	 *         otherwise.
	 */
	public boolean prev(int[] value) {
		for (int n = value.length - 1; 0 < n--;) {
			if (move(value, n, n + 1)) {
				moveAll(value, value.length - 1, n + 1);
				return true;
			}
		}
		return false;
	}
	public boolean nextRecursive(int[] value) {
		if (value.length < 1) {
			return false;
		}
		return this.nextRecursive(value, 0);
	}
	protected boolean nextRecursive(int[] value, int index) {
		switch (value.length - index) {
		case 1:
			return false;
		case 2:
			return move(value, index + 1, index);
		default:
			break;
		}
		if (this.nextRecursive(value, index + 1)) {
			return true;
		} else if (move(value, index + 1, index)) {
			moveAll(value, index + 1, value.length - 1);
			return true;
		}
		return false;
	}
	public int[][] listRecursive(int n, int k) {
		ArrayList<int[]> buffer = new ArrayList<int[]>((int) this.size(n, k));
		this.listRecursive(buffer, n, k);
		return buffer.toArray(DictionaryComposition.EMPTY_INT_ARRAY2);
	}
	public void listRecursive(List<int[]> output, int n, int k) {
		this.listRecursive(output, n, k, 0);
	}
	protected void listRecursive(List<int[]> output, int n, int k, int index) {
		switch (k - index) {
		case 0:
			return;
		case 1:
			output.add(this.first(n, k));
			return;
		default:
			break;
		}
		for (int p = 0; p <= n; ++p) {
			int old = output.size();
			this.listRecursive(output, n - p, k, index + 1);
			int now = output.size();
			for (; old < now; ++old) {
				output.get(old)[index] = p;
			}
		}
	}
}

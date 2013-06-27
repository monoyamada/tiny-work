package parser.v2;

import java.util.ArrayList;
import java.util.List;

class Binomial {
	List<int[]> memo;

	List<int[]> memo(boolean anyway) {
		if (this.memo == null && anyway) {
			this.memo = new ArrayList<int[]>();
		}
		return this.memo;
	}
	public int get(int n, int k) {
		if (n < 0 || k < 0) {
			String msg = "n and k must be non-negative";
			throw new IllegalArgumentException(msg);
		}
		if (k == n || k == 0) {
			return 1;
		} else if (n < k) {
			return 0;
		} else if (k == 1) {
			return n;
		}
		return this.get(n)[k];
	}
	public int[] get(int n) {
		if (n < 0) {
			String msg = "n must be non-negative";
			throw new IllegalArgumentException(msg);
		}
		List<int[]> memo = this.memo(true);
		if (memo.size() < 1) {
			memo.add(new int[] { 1 });
		}
		while (memo.size() <= n) {
			int nn = memo.size();
			int[] array = new int[nn + 1];
			int[] prev = memo.get(nn - 1);
			System.arraycopy(prev, 0, array, 0, nn);
			for (int i = 0; i < nn; ++i) {
				array[i + 1] += prev[i];
			}
			memo.add(array);
		}
		return memo.get(n);
	}
	public static int[] binomials(int n) {
		if (n < 0) {
			String msg = "n must be non-negative";
			throw new IllegalArgumentException(msg);
		}
		int[] out = new int[n + 1];
		out[0] = 1;
		for (int i = 1; i <= n; ++i) {
			out[i] = 1;
			for (int j = i - 1; 0 < j; --j) {
				out[j] += out[j - 1];
			}
		}
		return out;
	}
}
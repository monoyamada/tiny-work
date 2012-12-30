package machine;

import java.util.ArrayList;
import java.util.List;

public class Binomial {
	List<Polynomial[]> binomials;

	public Polynomial get(int n, int k) {
		if (n < 0 || k < 0) {
			String msg = "must be 0<=n and 0<=k";
			throw new IllegalArgumentException(msg);
		} else if (n < k) {
			String msg = "must be k<=n";
			throw new IllegalArgumentException(msg);
		}
		return this.doGet(n, k);
	}

	protected Polynomial doGet(int n, int k) {
		Polynomial[] xs = this.getBinomials(n);
		Polynomial x = xs[k];
		if (x != null) {
			return x;
		}
		Polynomial x1 = this.doGet(n - 1, k - 1);
		Polynomial x2 = this.doGet(n - 1, k);
		int d = Math.max(x1.size(), x2.size() + k);
		long[] ks = new long[d];
		x1.get(ks);
		for (int j = x2.size(); 0 < j--;) {
			ks[k + j] += x2.get(j);
		}
		return xs[k] = xs[n - k] = new Polynomial(ks);
	}

	protected Polynomial[] getBinomials(int n) {
		List<Polynomial[]> array = this.getBinomials(true);
		if (array.size() <= n) {
			for (int k = array.size(); k <= n; ++k) {
				Polynomial[] xs = new Polynomial[k + 1];
				xs[0] = xs[k] = Polynomial.getOne();
				array.add(xs);
			}
		}
		return array.get(n);
	}

	protected List<Polynomial[]> getBinomials(boolean anyway) {
		if (this.binomials == null && anyway) {
			this.binomials = new ArrayList<Polynomial[]>(16);
		}
		return this.binomials;
	}
}
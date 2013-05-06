package parser.v2;

import java.util.Arrays;

public abstract class WeakComposition {
	static WeakComposition build(int n, int k) {
		if (n < 0 || k < 0) {
			String msg = "both of n and k must be non-negative.";
			throw new IllegalArgumentException(msg);
		}
		switch (k) {
		case 0:
			return new CaseZero().set(n);
		case 1:
			return new CaseOne().set(n, new int[k]);
		case 2:
			return new CaseTwo().set(n, new int[k]);
		default:
			return new CaseOtherwise().set(n, k, new int[k], new int[k]);
		}
	}

	public abstract int n();
	public abstract int k();
	public abstract int[] get();
	public abstract WeakComposition next();

	static class CaseZero extends WeakComposition {
		private int n;

		@Override
		public int n() {
			return this.n;
		}
		@Override
		public int k() {
			return 0;
		}
		@Override
		public int[] get() {
			return null;
		}
		public CaseZero next() {
			return this;
		}
		public CaseZero set(int n) {
			if (n < 0) {
				String msg = "n must be non-negative.";
				throw new IllegalArgumentException(msg);
			}
			this.n = n;
			return this;
		}
	}

	static class CaseOne extends WeakComposition {
		private int n;
		private boolean done;
		private int[] values;

		@Override
		public int n() {
			return this.n;
		}
		@Override
		public int k() {
			return 1;
		}
		@Override
		public int[] get() {
			return this.done ? null : this.values;
		}
		public CaseOne next() {
			if (this.done) {
				return this;
			}
			this.done = true;
			return this;
		}
		public CaseOne set(int n, int[] values) {
			if (n < 0) {
				String msg = "n must be non-negative.";
				throw new IllegalArgumentException(msg);
			} else if (values == null || values.length < 1) {
				String msg = "array must have length at least " + 1;
				throw new IllegalArgumentException(msg);
			}
			this.n = n;
			this.values = values;
			this.values[0] = n;
			this.done = false;
			return this;
		}
	}

	static class CaseTwo extends WeakComposition {
		private int n;
		private int next;
		private int[] values;

		@Override
		public int n() {
			return this.n;
		}
		@Override
		public int k() {
			return 2;
		}
		@Override
		public int[] get() {
			return this.n < this.next ? null : this.values;
		}
		public CaseTwo next() {
			if (this.n < this.next) {
				return this;
			}
			this.values[0] = ++this.next;
			--this.values[1];
			return this;
		}
		public CaseTwo set(int n, int[] values) {
			if (n < 0) {
				String msg = "n must be non-negative.";
				throw new IllegalArgumentException(msg);
			} else if (values == null || values.length < 2) {
				String msg = "array must have length at least " + 2;
				throw new IllegalArgumentException(msg);
			}
			this.n = n;
			this.values = values;
			this.values[0] = 0;
			this.values[1] = n;
			return this;
		}
	}

	static class CaseOtherwise extends WeakComposition {
		private int n;
		private int k;
		private int[] uppers;
		private int[] values;
		private int index;

		@Override
		public int n() {
			return this.n;
		}
		@Override
		public int k() {
			return this.k;
		}
		@Override
		public int[] get() {
			return this.index < 0 ? null : this.values;
		}
		public CaseOtherwise next() {
			if (this.index < 0) {
				return this;
			}
			while (0 <= this.index
					&& this.uppers[this.index] <= this.values[this.index]) {
				--this.index;
			}
			if (this.index < 0) {
				return this;
			}
			int upper = this.uppers[this.index] - (++this.values[this.index]);
			this.uppers[++this.index] = upper;
			while (this.index < this.k - 1) {
				this.values[this.index] = 0;
				this.uppers[++this.index] = upper;
			}
			this.values[this.index--] = upper;
			return this;
		}
		public CaseOtherwise set(int n, int k, int[] values, int[] uppers) {
			if (n < 0) {
				String msg = "n must be non-negative.";
				throw new IllegalArgumentException(msg);
			} else if (k < 1) {
				String msg = "k must be positive.";
				throw new IllegalArgumentException(msg);
			} else if (values == null || values.length < k || uppers == null
					|| uppers.length < k) {
				String msg = "array must have length at least " + k;
				throw new IllegalArgumentException(msg);
			}
			Arrays.fill(uppers, 0, k, n);
			Arrays.fill(values, 0, k - 1, 0);
			values[k - 1] = n;
			this.n = n;
			this.k = k;
			this.uppers = uppers;
			this.values = values;
			this.index = k - 1;
			return this;
		}
	}
}

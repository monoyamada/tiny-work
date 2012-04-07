package tiny.number;

import tiny.lang.ArrayHelper;

public class LongArrayAdapter {
	private long[] array;

	public LongArrayAdapter() {
		this(null);
	}
	public LongArrayAdapter(long[] array) {
		if (array == null) {
			this.array = ArrayHelper.EMPTY_LONG_ARRAY;
		} else {
			this.array = array.clone();
		}
	}
	public int size() {
		return this.get().length;
	}
	public long[] get() {
		return this.array;
	}
	public void set(long[] array) {
		if (array == null) {
			this.array = ArrayHelper.EMPTY_LONG_ARRAY;
		} else {
			this.array = array.clone();
		}
	}
	public LongArrayAdapter plus(long[] array) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] += array[n];
		}
		return this;
	}
	public LongArrayAdapter plus(long val) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] += val;
		}
		return this;
	}
	public LongArrayAdapter minus(long[] array) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] -= array[n];
		}
		return this;
	}
	public LongArrayAdapter minus(long val) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] -= val;
		}
		return this;
	}
	public LongArrayAdapter minus() {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] = -x[n];
		}
		return this;
	}
	public LongArrayAdapter multiplies(long[] array) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] *= array[n];
		}
		return this;
	}
	public LongArrayAdapter multiplies(long val) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] *= val;
		}
		return this;
	}
	public LongArrayAdapter divides(long[] array) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] /= array[n];
		}
		return this;
	}
	public LongArrayAdapter divides(long val) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] /= val;
		}
		return this;
	}
	public LongArrayAdapter modulo(long[] array) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] %= array[n];
		}
		return this;
	}
	public LongArrayAdapter modulo(long val) {
		long[] x = this.array;
		for (int n = x.length; 0 < n--;) {
			x[n] %= val;
		}
		return this;
	}
	public long inner(long[] array) {
		long[] x = this.array;
		long val = 0;
		for (int n = x.length; 0 < n--;) {
			val += x[n] * array[n];
		}
		return val;
	}
}

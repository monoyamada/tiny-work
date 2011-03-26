package graycode;

public class GrayCodeComposition extends DictionaryComposition {
	@Override
	public boolean next(int[] value) {
		return this.next(value, sum(value));
	}
	protected boolean next(int[] value, int sum) {
		int n = value.length;
		if (n < 2) {
			return false;
		}
		int right = value[--n];
		int left = sum - right;
		while (0 < n) {
			int x = value[--n];
			left -= x;
			if (even(left)) {
				if (0 < right) {
					if (even(x)) {
						value[n + 1] -= 1;
					} else {
						value[value.length - 1] -= 1;
					}
					value[n] += 1;
					return true;
				}
			} else {
				if (0 < x) {
					if (even(x)) {
						value[value.length - 1] += 1;
					} else {
						value[n + 1] += 1;
					}
					value[n] -= 1;
					return true;
				}
			}
			right += x;
		}
		return false;
	}
	@Override
	public boolean prev(int[] value) {
		return this.prev(value, sum(value));
	}
	protected boolean prev(int[] value, int sum) {
		int n = value.length;
		if (n < 2) {
			return false;
		}
		int right = value[--n];
		int left = sum - right;
		while (0 < n) {
			int x = value[--n];
			left -= x;
			if (odd(left)) {
				if (0 < right) {
					if (even(x)) {
						value[n + 1] -= 1;
					} else {
						value[value.length - 1] -= 1;
					}
					value[n] += 1;
					return true;
				}
			} else {
				if (0 < x) {
					if (even(x)) {
						value[value.length - 1] += 1;
					} else {
						value[n + 1] += 1;
					}
					value[n] -= 1;
					return true;
				}
			}
			right += x;
		}
		return false;
	}
}

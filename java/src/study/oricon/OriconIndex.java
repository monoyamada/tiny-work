package study.oricon;

import java.io.IOException;
import java.util.Comparator;

import study.lang.NumberHelper;

public class OriconIndex extends OriconData {
	public static final OriconIndex[] EMPTY_ARRAY = {};
	public static final Comparator<OriconIndex> INDEX_ORDER = new Comparator<OriconIndex>() {
		public int compare(OriconIndex o1, OriconIndex o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			}
			return NumberHelper.compare(o1.getIndex(), o2.getIndex());
		}
	};

	public static <T extends OriconIndex> int indexOf(T[] array, int value) {
		final int index = getLowerBound(array, value);
		if (index < array.length) {
			return array[index].getIndex() == value ? index : -1;
		}
		return -1;
	}
	public static <T extends OriconIndex> int indexOf(T[] array, int begin,
			int end, int value) {
		final int index = getLowerBound(array, begin, end, value);
		if (index < end) {
			return array[index].getIndex() == value ? index : -1;
		}
		return -1;
	}
	/**
	 * <code>
	 * array[0] <= ... <= array[index - 1] < value <= array[index] <= ...  <= array[length - 1]
	 * </code>
	 * 
	 * @param array
	 *          sorted array with the order <=.
	 * @param value
	 * @return
	 */
	public static <T extends OriconIndex> int getLowerBound(T[] array, int value) {
		return getLowerBound(array, 0, array.length, value);
	}
	/**
	 * <code>
	 * array[begin] <= ... <= array[index - 1] < value <= array[index] <= ... <= array[end - 1]
	 * </code>
	 * 
	 * @param array
	 *          sorted array with the order <=.
	 * @param value
	 * @return
	 */
	public static <T extends OriconIndex> int getLowerBound(T[] array, int begin,
			int end, int value) {
		int d = end - begin;
		switch (d) {
		case 0:
			return begin;
		case 1:
			return array[begin].getIndex() < value ? begin + 1 : begin;
		default:
			if (false) {
				final int m = begin + (d >> 1);
				if (array[m].getIndex() < value) {
					return getLowerBound(array, m, end, value);
				} else {
					return getLowerBound(array, begin, m, value);
				}
			}
			break;
		}
		for (int m = 0; 1 < d; d = end - begin) {
			m = begin + (d >> 1);
			if (array[m].getIndex() < value) {
				begin = m;
			} else {
				end = m;
			}
		}
		return array[begin].getIndex() < value ? end : begin;
	}

	public static <T extends OriconIndex> int weave(T[] output, T[] xs0, T[] xs1) {
		return weave(output, xs0, 0, xs0.length, xs1, 0, xs1.length);
	}
	public static <T extends OriconIndex> int weave(T[] output, T[] xs0,
			int begin0, int end0, T[] xs1, int begin1, int end1) {
		if (output.length < 1) {
			return 0;
		}
		int count = 0;
		for (; begin0 < end0; ++begin0) {
			for (; begin1 < end1; ++begin1) {
				if (xs1[begin1].getIndex() < xs0[begin0].getIndex()) {
					output[count++] = xs1[begin1];
					if (output.length <= count) {
						return count;
					}
				} else if (xs1[begin1].getIndex() == xs0[begin0].getIndex()) {
					// skip duplication
					continue;
				} else {
					break;
				}
			}
			output[count++] = xs0[begin0];
			if (output.length <= count) {
				return count;
			}
		}
		for (; begin1 < end1; ++begin1) {
			output[count++] = xs1[begin1];
			if (output.length <= count) {
				return count;
			}
		}
		return count;
	}

	private int index;

	public OriconIndex() {
		this.index = -1;
	}
	public OriconIndex(int index) {
		this.index = index;
	}
	public OriconIndex(OriconIndex x) {
		this.index = x.index;
	}
	public int getIndex() {
		return this.index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public void toString(Appendable output) throws IOException {
		output.append("id=");
		output.append(Integer.toString(this.getIndex()));
	}
	public OriconIndex clone() {
		return (OriconIndex) super.clone();
	}
	public OriconIndex copy(OriconIndex x) {
		this.index = x.index;
		return this;
	}
	public OriconIndex clear() {
		this.index = -1;
		return this;
	}
}

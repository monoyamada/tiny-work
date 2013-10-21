package parser.v2;

import java.io.IOException;
import java.util.Arrays;

import tiny.function.LexicographicalOrders;
import tiny.lang.StringHelper;

public class IntArrayKey implements Cloneable, Comparable<IntArrayKey> {
	final int[] array;

	public IntArrayKey(int[] array) {
		this.array = array;
	}
	@Override
	public IntArrayKey clone() {
		return new IntArrayKey(this.array.clone());
	}
	@Override
	public boolean equals(Object x) {
		if (this == x) {
			return true;
		} else if (x == null) {
			return false;
		}
		IntArrayKey xx = (IntArrayKey) x;
		return Arrays.equals(this.array, xx.array);
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.array);
	}
	@Override
	public int compareTo(IntArrayKey x) {
		if (this == x) {
			return 0;
		} else if (x == null) {
			return -1;
		}
		return LexicographicalOrders.compare(this.array, x.array);
	}
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		try {
			this.toString(buffer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	protected Appendable toString(Appendable output) throws IOException {
		return StringHelper.join(output.append('['), this.array, ", ").append(']');
	}
}

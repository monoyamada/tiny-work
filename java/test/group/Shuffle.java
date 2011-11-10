/**
 * 
 */
package group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tiny.function.Cursor;

public class Shuffle {
	int weight;
	final int[] indices;

	protected Shuffle(int weight, int[] indices) {
		this.weight = weight;
		this.indices = indices;
	}
	public int getWeight() {
		return this.weight;
	}
	public int[] getIndices() {
		return this.indices;
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(this.indices.length + 8);
		this.toString(buffer);
		return buffer.toString();
	}
	protected void toString(StringBuilder buffer) {
		buffer.append(Integer.toString(this.weight));
		buffer.append('[');
		for (int i = 0, n = this.indices.length; i < n; ++i) {
			if (i != 0) {
				buffer.append(',');
			}
			buffer.append(this.indices[i]);
		}
		buffer.append(']');
	}
	public static List<Shuffle> listSuffle(int n0, int n1) {
		List<Shuffle> output = new ArrayList<Shuffle>();
		listShuffle(output, 0, 0, 0, n0, n1);
		return output;
	}
	static void listShuffle(List<Shuffle> output, int k0, int k1, int q, int n0,
			int n1) {
		int n = n0 + n1;
		int k = k0 + k1;
		if (k0 == n0) {
			int[] x = new int[n];
			while (k1 < n1--) {
				x[n0 + n1] = n0 + n1;
			}
			output.add(new Shuffle(q, x));
			return;
		} else if (k1 == n1) {
			int[] x = new int[n0 + n1];
			while (k0 < n0--) {
				x[n1 + n0] = n0;
			}
			output.add(new Shuffle(q, x));
			return;
		}
		n = output.size();
		listShuffle(output, k0 + 1, k1, q, n0, n1);
		for (int i = output.size(); n < i--;) {
			output.get(i).indices[k] = k0;
		}
		n = output.size();
		listShuffle(output, k0, k1 + 1, q + n0 - k0, n0, n1);
		for (int i = output.size(); n < i--;) {
			output.get(i).indices[k] = n0 + k1;
		}
	}
	protected static int[] newIndices(int n) {
		return newIndices(n, 0);
	}
	protected static int[] newIndices(int n, int begin) {
		int[] indices = new int[n];
		while (0 < n--) {
			indices[n] = begin + n;
		}
		return indices;
	}
	public static Traverser cursor(int n0, int n1) {
		if (n0 == 0) {
			return new Traverser(n1);
		} else if (n1 == 0) {
			return new Traverser(n0);
		}
		return new Traverser_1(n0, n1);
		// return new Traverser_2(n0, n1);
	}

	protected static class Traverser extends Shuffle implements Cursor<Shuffle> {
		protected Traverser(int n) {
			super(0, newIndices(n));
		}
		@Override
		public Shuffle getValue() {
			return this;
		}
		@Override
		public boolean move() {
			return false;
		}
	}

	protected static class Traverser_1 extends Traverser {
		final int n0;

		public Traverser_1(int n0, int n1) {
			super(n0 + n1);
			this.n0 = n0;
		}
		@Override
		public boolean move() {
			int[] indices = this.indices;
			int i = indices.length;
			while (0 < i-- && indices[i] < this.n0) {
				// skip 0..(n0 - 1)
			}
			while (0 < i-- && this.n0 <= indices[i]) {
				// skip n0..(n0 + n1 - 1)
			}
			if (i < 0) {
				return false;
			}
			int k0 = indices[i++];
			int k1 = indices[i];
			while (k0 < this.n0) {
				indices[i++] = k0++;
			}
			while (i < indices.length) {
				indices[i++] = k1++;
			}
			return true;
		}
		@Override
		public Shuffle getValue() {
			return this;
		}
	}

	protected static class Traverser_2 extends Traverser_1 {
		final int[] ks0;
		int size;
		final int[] is0;

		public Traverser_2(int n0, int n1) {
			super(n0, n1);
			this.ks0 = new int[n1];
			Arrays.fill(this.ks0, n0);
			this.is0 = new int[n1];
			if (0 < n0 && 0 < n1) {
				this.size = 1;
				this.is0[0] = 0;
			}
		}
		@Override
		public boolean move() {
			if (this.size < 1) {
				return false;
			}
			final int[] indices = this.indices;
			final int n0 = this.n0;
			final int[] ks0 = this.ks0;
			int ind = this.is0[this.size - 1];
			--ks0[ind];
			++this.weight;
			int d = this.indices[ks0[ind]];
			this.indices[ks0[ind]] = this.indices[ks0[ind] + 1];
			this.indices[ks0[ind] + 1] = d;
			if (ks0[ind] <= (ind == 0 ? 0 : ks0[ind - 1])) {
				--this.size;
			}
			if (ks0[ind++] < n0 && ind < ks0.length) {
				this.is0[this.size++] = ind;
				int i = this.indices.length - 1;
				for (int n = this.is0.length; ind < n; ++ind, --i) {
					this.weight -= n0 - ks0[ind];
					ks0[ind] = n0;
					this.indices[i] = i;
				}
			}
			return true;
		}
		@Override
		public Shuffle getValue() {
			return this;
		}
	}
}
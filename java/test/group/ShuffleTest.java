package group;

import group.Shuffle.Traverser;

import java.util.List;

import junit.framework.TestCase;
import tiny.function.Function;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.PrimitiveHelper;

public class ShuffleTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	public void testShuffle() {
		{
			List<Shuffle> indices = Shuffle.listSuffle(2, 2);
			Debug.log().debug(indices);
		}
		{
			List<Shuffle> indices = Shuffle.listSuffle(3, 2);
			Debug.log().debug(indices);
		}
		{
			List<Shuffle> indices = Shuffle.listSuffle(8, 8);
			Debug.log().debug(indices.size());
		}
	}
	static int[][] indices_0(List<Shuffle> words, int n0) {
		int n = words.size();
		if (n < 1) {
			return new int[0][];
		}
		Shuffle sh = words.get(0);
		int[] w = sh.getIndices();
		int[][] output = new int[words.size()][n0];
		output[0] = indices_0(new int[n0], w, n0);
		for (int i = 1; i < n; ++i) {
			output[i] = indices_0(new int[n0], words.get(i).getIndices(), n0);
		}
		return output;
	}
	private static int[] indices_0(int[] output, int[] w, int n0) {
		int ind = 0;
		for (int i = 0, n = w.length; i < n; ++i) {
			if (w[i] < n0) {
				output[ind++] = i;
				if (n0 <= ind) {
					break;
				}
			}
		}
		return output;
	}
	static int[][] indices_1(List<Shuffle> words, int n0) {
		int n = words.size();
		if (n < 1) {
			return new int[0][];
		}
		Shuffle sh = words.get(0);
		int[] w = sh.getIndices();
		final int n1 = w.length - n0;
		int[][] output = new int[words.size()][n1];
		output[0] = indices_1(new int[n1], w, n0);
		for (int i = 1; i < n; ++i) {
			output[i] = indices_1(new int[n1], words.get(i).getIndices(), n0);
		}
		return output;
	}
	private static int[] indices_1(int[] output, int[] w, int n0) {
		final int n1 = w.length - n0;
		int ind = 0;
		for (int i = 0, n = w.length; i < n; ++i) {
			if (n0 <= w[i]) {
				output[ind++] = i;
				if (n1 <= ind) {
					break;
				}
			}
		}
		return output;
	}

	public void testShuffle_1() {
		Function<int[], String> fnc = new Function<int[], String>() {
			@Override
			public String evaluate(int[] source) throws Exception {
				PrimitiveHelper.plus(source, 0, source, 0, source.length, 1);
				return "[" + StringHelper.join(source) + "]";
			}
		};
		{
			final int n0 = 3;
			final int n1 = 2;
			List<Shuffle> words = Shuffle.listSuffle(n0, n1);
			int[][] indices = indices_0(words, n0);
			Debug.log().debug("------- " + n0 + ", " + n1 + " -------");
			Debug.log().debug(StringHelper.join(indices, ", ", fnc));
		}
		if (false) {
			int n0 = 4;
			int n1 = 2;
			List<Shuffle> words = Shuffle.listSuffle(n0, n1);
			int[][] indices = indices_0(words, n0);
			Debug.log().debug("------- " + n0 + ", " + n1 + " -------");
			Debug.log().debug(StringHelper.join(indices, ", ", fnc));
		}
	}
	public void _testTraverser() {
		{
			Debug.log().debug("------- 2, 2 -------");
			Traverser p = Shuffle.cursor(2, 2);
			Debug.log().debug(p.getValue());
			while (p.move()) {
				Debug.log().debug(p.getValue());
			}
		}
		{
			Debug.log().debug("------- 3, 1 -------");
			Traverser p = Shuffle.cursor(3, 1);
			Debug.log().debug(p.getValue());
			while (p.move()) {
				Debug.log().debug(p.getValue());
			}
		}
		{
			Debug.log().debug("------- 1, 3 -------");
			Traverser p = Shuffle.cursor(1, 3);
			Debug.log().debug(p.getValue());
			while (p.move()) {
				Debug.log().debug(p.getValue());
			}
		}
		{
			Debug.log().debug("------- 3, 3 -------");
			Traverser p = Shuffle.cursor(3, 3);
			Debug.log().debug(p.getValue());
			while (p.move()) {
				Debug.log().debug(p.getValue());
			}
		}
		if (false) {
			// too slow
			int n0 = 16;
			int n1 = 16;
			Traverser p1 = new Shuffle.Traverser_1(n0, n1);
			Traverser p2 = new Shuffle.Traverser_2(n0, n1);
			// Debug.log().debug(p.getValue());
			long t0 = System.currentTimeMillis();
			int N1 = 1;
			for (; p1.move(); ++N1) {
			}
			long t1 = System.currentTimeMillis() - t0;
			t0 = System.currentTimeMillis();
			int N2 = 1;
			for (; p2.move(); ++N2) {
			}
			long t2 = System.currentTimeMillis() - t0;
			Debug.log().debug(t1 + ":" + N1 + ", " + t2 + ":" + N2);
		}
	}

	static class Shuffle2 {
		final int n0;
		int weight;
		final int[] indices;
		final int[] ks;

		public Shuffle2(int n0) {
			this.n0 = n0;
			this.indices = Shuffle.newIndices(n0 + 2);
			this.ks = Shuffle.newIndices(2, n0);
		}
		public boolean next() {
			final int[] ks = this.ks;
			if ((this.n0 + 1 - ks[1]) % 2 == 1) {
				if (ks[0] + 1 < ks[1]) {
					ArrayHelper.swap(this.indices, ks[0], ks[0] + 1);
					this.weight -= 1;
					ks[0] += 1;
					return true;
				} else if (0 < ks[0]) {
					ArrayHelper.swap(this.indices, ks[0], ks[0] - 1);
					ArrayHelper.swap(this.indices, ks[1], ks[1] - 1);
					this.weight += 2;
					ks[0] -= 1;
					ks[1] -= 1;
					return true;
				}
			} else if (0 < ks[0]) {
				ArrayHelper.swap(this.indices, ks[0], ks[0] - 1);
				this.weight += 1;
				ks[0] -= 1;
				return true;
			} else if (ks[0] + 1 < ks[1]) {
				ArrayHelper.swap(this.indices, ks[1], ks[1] - 1);
				this.weight += 1;
				ks[1] -= 1;
				return true;
			}
			return false;
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder(this.indices.length + 8);
			this.toString(buffer);
			return buffer.toString();
		}
		protected void toString(StringBuilder buffer) {
			buffer.append('[');
			for (int i = 0, n = this.ks.length; i < n; ++i) {
				if (i != 0) {
					buffer.append(',');
				}
				buffer.append(this.ks[i]);
			}
			buffer.append(']');
			buffer.append(':');
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
	}

	public void test2() {
		if (true) {
			Shuffle2 sh = new Shuffle2(3);
			Debug.log().debug("------- 3, 2 -------");
			Debug.log().debug(sh);
			while (sh.next()) {
				Debug.log().debug(sh);
			}
		}
		{
			Shuffle2 sh = new Shuffle2(4);
			Debug.log().debug("------- 4, 2 -------");
			Debug.log().debug(sh);
			while (sh.next()) {
				Debug.log().debug(sh);
			}
		}
		{
			Shuffle2 sh = new Shuffle2(5);
			Debug.log().debug("------- 5, 2 -------");
			Debug.log().debug(sh);
			while (sh.next()) {
				Debug.log().debug(sh);
			}
		}
	}
}

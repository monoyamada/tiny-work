package tiny.primitive;

public class PrimitiveHelper {
	public static int[] plus(int[] x0, int i0, int[] x1, int i1, int n, int value) {
		for (int i = 0; i < n; ++i) {
			x0[i0 + i] = x1[i0 + i] + value;
		}
		return x0;
	}
}

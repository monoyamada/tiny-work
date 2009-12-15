package study.primitive;

public class PrimitiveHelper {
	public static int[] toIntegerArray(LongList array) {
		final int n = array.getSize();
		final int[] newArray = new int[n];
		for (int i = 0; i < n; ++i) {
			newArray[i] = (int) array.getLong(i);
		}
		return newArray;
	}
}

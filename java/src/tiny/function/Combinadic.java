/**
 *
 */
package tiny.function;

import tiny.lang.ArrayHelper;

public class Combinadic {
	public static int getCombinationSize(int setSize, int chooseSize) {
		if (chooseSize == 0 || setSize <= chooseSize) {
			return 1;
		} else if (chooseSize + 1 == setSize || chooseSize == 1) {
			return setSize;
		} else if (chooseSize + 2 == setSize || chooseSize == 2) {
			return (setSize * (setSize - 1)) >> 1;
		} else if (true) {
			// maybe faster in the case of Java.
			long upper = setSize;
			long lower = chooseSize;
			for (int i = 1; i < chooseSize; ++i) {
				upper *= setSize - i;
				lower *= chooseSize - i;
			}
			return (int) (upper / lower);
		}
		return Combinadic.getCombinationSize(setSize - 1, chooseSize - 1)
				+ Combinadic.getCombinationSize(setSize - 1, chooseSize);
	}
	public static void getCombination(int[] array, int setSize, int chooseSize,
			int index) {
		Combinadic.getCombination(array, 0, setSize, chooseSize, index);
	}
	public static void getCombination(int[] array, int begin, int setSize,
			int chooseSize, int index) {
		int offset = 0;
		for (int value = 0; value < setSize && offset < chooseSize; ++value) {
			final int threshold = Combinadic.getCombinationSize(setSize - value - 1,
					chooseSize - offset - 1);
			if (index < threshold) {
				array[begin + offset++] = value;
			} else if (threshold <= index) {
				index -= threshold;
			}
		}
	}

	public static <T> T[][] shuffleProduct(T[] x1, T[] x2) {
		return Combinadic.shuffleProduct(x1, 0, x1.length, x2, 0, x2.length);
	}
	@SuppressWarnings("unchecked")
	public static <T> T[][] shuffleProduct(T[] x1, int begin1, int end1, T[] x2,
			int begin2, int end2) {
		final Class<?> type = x1.getClass().getComponentType();
		final int n1 = end1 - begin1;
		final int n2 = end2 - begin2;
		final int nProduct = n1 + n2;
		final int nTrem = Combinadic.getCombinationSize(nProduct, n1);
		final int[] indices = new int[n1];
		final T[][] output = (T[][]) ArrayHelper.newArray(type, nTrem, nProduct);
		for (int i = 0; i < nTrem; ++i) {
			Combinadic.getCombination(indices, nProduct, n1, i);
			int last2 = 0;
			int lastOutput = 0;
			T[] term = (T[]) ArrayHelper.newArray(type, nProduct);
			for (int k = 0; k < n1; ++k) {
				final int index = indices[k];
				for (; lastOutput < index;) {
					term[lastOutput++] = x2[begin2 + last2++];
				}
				term[lastOutput++] = x1[begin1 + k];
			}
			for (; lastOutput < nProduct;) {
				term[lastOutput++] = x2[begin2 + last2++];
			}
			output[i] = term;
		}
		return output;
	}
}
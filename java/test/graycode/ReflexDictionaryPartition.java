package graycode;

import java.util.Arrays;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public class ReflexDictionaryPartition implements NumberPartition {
	@Override
	public int[] first(int n, int k) {
		if (n < 0) {
			String msg = Messages.getUnexpectedValue("n", "non-negative", n);
			throw new IllegalArgumentException(msg);
		} else if (k < 0) {
			String msg = Messages.getUnexpectedValue("k", "non-negative", k);
			throw new IllegalArgumentException(msg);
		} else if (n < k) {
			String msg = Messages.getUnexpectedValue("(n,k)", "n=>k", n + "<" + k);
			throw new IllegalArgumentException(msg);
		}
		switch (k) {
		case 0:
			return ArrayHelper.EMPTY_INT_ARRAY;
		case 1:
			return new int[] { n };
		default:
			break;
		}
		int[] word = new int[k];
		Arrays.fill(word, 1);
		word[0] += n - k;
		return word;
	}
	@Override
	public boolean next(int[] word) {
		int n = word.length;
		int i = 0;
		int value = word[i];
		for (int last = n - 1; i < last && value == word[i + 1]; ++i) {
			// find right most of #balls = value
		}
		for (int j = i + 1; j < n; ++j) {
			if (word[j] + 1 < value) {
				word[j] += 1;
				word[i] -= 1;
				value = word[j];
				while (1 < j--) {
					// reset left boxes
					word[0] += word[j] - value;
					word[j] = value;
				}
				return true;
			}
		}
		return false;
	}
}

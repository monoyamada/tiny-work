package tiny.number;

public class ReflexDictionaryComposition extends DictionaryComposition {
	@Override
	public int[] first(int n, int k) {
		return super.last(n, k);
	}
	@Override
	public int[] last(int n, int k) {
		return super.first(n, k);
	}
	@Override
	public boolean next(int[] value) {
		for (int i = 0, last = value.length - 1; i < last; ++i) {
			if (move(value, i, i + 1)) {
				moveAll(value, i, 0);
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean prev(int[] value) {
		for (int i = 1, last = value.length; i < last; ++i) {
			if (move(value, i, i - 1)) {
				moveAll(value, 0, i - 1);
				return true;
			}
		}
		return false;
	}
}

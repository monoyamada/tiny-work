package machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tiny.lang.ArrayHelper;

public class DyckWord_0 extends DyckWord<String> {
	public DyckWord_0(String left, String right) {
		super(left, right);
	}
	public void getProduct(Collection<String> output, int n, int k, String[] x1,
			String[] x2) {
		StringBuilder buffer = new StringBuilder();
		for (int i1 = 0, n1 = x1.length; i1 < n1; ++i1) {
			for (int i2 = 0, n2 = x2.length; i2 < n2; ++i2) {
				buffer.delete(0, buffer.length());
				buffer.append(this.left);
				buffer.append(x1[i1]);
				buffer.append(this.right);
				buffer.append(x2[i2]);
				output.add(buffer.toString());
			}
		}
	}
	protected List<String[]> newWordList() {
		return new ArrayList<String[]>(16);
	}
	protected ArrayList<String> newWordBuffer() {
		return new ArrayList<String>();
	}
	@Override
	protected String[] getEmptyWordArray() {
		return ArrayHelper.EMPTY_STRING_ARRAY;
	}
	@Override
	protected String[] getUnit() {
		return new String[] { "" };
	}
}

package machine;

import java.util.Arrays;
import java.util.Collection;

import tiny.lang.ArrayHelper;
import tiny.primitive.LongArrayList;

public class DyckWord_2 extends DyckWord_1 {
	public DyckWord_2(String left, String right) {
		super(left, right);
	}
	@Override
	public void getProduct(Collection<PolyWord> output, int n, int k,
			PolyWord[] x1, PolyWord[] x2) {
		LongArrayList ks = new LongArrayList();
		StringBuilder buffer = new StringBuilder();
		for (int i1 = 0, n1 = x1.length; i1 < n1; ++i1) {
			for (int i2 = 0, n2 = x2.length; i2 < n2; ++i2) {
				// word
				buffer.delete(0, buffer.length());
				buffer.append(this.left);
				buffer.append(x1[i1].word);
				buffer.append(this.right);
				buffer.append(x2[i2].word);
				String word = buffer.toString();
				// coeff.
				Polynomial binom = this.getBinomial().get(n, k);
				Polynomial ks1 = x1[i1].weight;
				Polynomial ks2 = x2[i2].weight;
				int kn = binom.size() + ks1.size() + ks2.size() - 2 + k;
				ks.removeAll();
				ks.ensureCapacity(kn);
				Arrays.fill(ks.getArray(), 0, kn, 0);
				for (int k0 = 0, m0 = binom.size(); k0 < m0; ++k0) {
					for (int k1 = 0, m1 = ks1.size(); k1 < m1; ++k1) {
						for (int k2 = 0, m2 = ks2.size(); k2 < m2; ++k2) {
							int index = k0 + k1 + k2 + k;
							ks.getArray()[index] += binom.get(k0) * ks1.get(k1) * ks2.get(k2);
						}
					}
				}
				output.add(new PolyWord(word, new Polynomial(ArrayHelper.sub(
						ks.getArray(), 0, kn))));
			}
		}
	}
}

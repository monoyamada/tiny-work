package parser.v2;

import tiny.lang.Debug;
import tiny.lang.StringHelper;
import junit.framework.TestCase;

public class WeakCompositionTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_get() {
		int n = 3;
		for (int k = 0; k < 5; ++k) {
			Debug.log().debug("----- (" + n + ", " + k + ")-------");
			WeakComposition p = WeakComposition.build(n, k);
			int[] xs = p.get();
			while (xs != null) {
				Debug.log().debug(StringHelper.join(xs, ", "));
				xs = p.next().get();
			}
		}
	}
}

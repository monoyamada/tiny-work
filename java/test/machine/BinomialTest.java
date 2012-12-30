package machine;

import tiny.lang.Debug;
import junit.framework.TestCase;

public class BinomialTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		Debug.setLogLevel("debug");
	}
	public void test1() {
		Binomial binoms = new Binomial();
		for (int n = 0; n <= 10; ++n) {
			if (n == 0) {
				continue;
			}
			for (int k = 0; k <= n; ++k) {
				if (k == 0 || k == n) {
					continue;
				}
				System.out.print("[" + n + ", " + k + "] = ");
				System.out.print(binoms.get(n, k).toString("q"));
				System.out.println();
			}
		}
	}
}

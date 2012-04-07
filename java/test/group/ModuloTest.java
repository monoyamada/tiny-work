package group;

import junit.framework.TestCase;
import tiny.lang.Debug;

public class ModuloTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test7() {
		int N = 7;
		for (int i = 1; i < N; ++i) {
			for (int j = 1; j < N; ++j) {
				if (i <= j) {
					if (1 < j) {
						System.out.print(" & ");
					}
					System.out.print(i * j % N);
				} else if (1 < j){
					System.out.print(" &");
				}
			}
			System.out.println(" \\\\");
		}
	}
}

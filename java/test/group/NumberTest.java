package group;

import tiny.lang.Debug;
import tiny.primitive.LongArrayList;
import tiny.primitive.LongList;
import junit.framework.TestCase;

public class NumberTest extends TestCase {
	static void doit(LongList output, int num, int del, int mod) {
		num *= mod;
		while (num < del) {
			output.addLast(0);
			num *= mod;
		}
		for (int i = 0; i < mod; ++i) {
			int q = num / del;
			num -= q * del;
			num *= mod;
			output.addLast(q);
		}
	}
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testDoit() {
		LongArrayList output =new LongArrayList(1024);
		{
			output.removeAll();
			doit(output, 1, 3, 10);
			Debug.log().debug(output);
		}
		{
			output.removeAll();
			doit(output, 1, 6, 10);
			Debug.log().debug(output);
		}
		{
			output.removeAll();
			doit(output, 1, 7, 10);
			Debug.log().debug(output);
		}
	}
}

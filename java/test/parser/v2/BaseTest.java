package parser.v2;

import tiny.lang.Debug;
import junit.framework.TestCase;

public class BaseTest extends TestCase {
	static private final ThreadLocal<StringBuilder> STRING_BUILDER = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	static protected StringBuilder buffer() {
		StringBuilder out = STRING_BUILDER.get();
		out.setLength(0);
		return out;
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
}

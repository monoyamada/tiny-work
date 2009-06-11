package study.algebra;

import junit.framework.TestCase;
import study.algebra.MagmaTestClasses.IfNode;
import study.lang.Debug;
import study.lang.StringHelper;

public class MagmaTest extends TestCase {
	protected static String kakko(Object... array) {
		return "[" + StringHelper.join(array) + "]";
	}
	protected static class StringNode extends MagmaTestClasses.Node {
		final String value;

		public StringNode(String value) {
			this.value = value;
		}
		public String toString() {
			return this.value;
		}
	}
	protected static class Magma extends MagmaTestClasses.BinaryNode {
		public Magma(IfNode chld0, IfNode chld1) {
			super(chld0, chld1);
		}
		public String toString() {
			return kakko(this.getChild0(), this.getChild1());
		}
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
}

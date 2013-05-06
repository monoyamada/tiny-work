package parser.v2;

import java.io.IOException;

import junit.framework.TestCase;
import tiny.lang.Debug;

public class DumpImageTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static class Value<T> {
		T value;
		long position;
	}

	static abstract class DumpImage<Input, Output> {
		public static final int WHICH_S = 0;
		public static final int WHICH_A = WHICH_S + 1;
		public static final int WHICH_a = WHICH_A + 1;
		public static final int WHICH_b = WHICH_a + 1;
		public static final int WHICH_c = WHICH_b + 1;

		protected boolean parse(Value<Output> out, Value<Input> in, int which)
				throws IOException {
			switch (which) {
			case WHICH_A:
				return this.parseA(out, in);
			case WHICH_a:
			case WHICH_b:
				throw new IOException("must be implemented=" + which);
			default:
				throw new IOException("unknown tag=" + which);
			}
		}

		protected boolean parseA(Value<Output> out, Value<Input> in)
				throws IOException {
			Output output = this.newOutput(in.value, WHICH_A);
			long position = in.position;
			if (this.parse(out, in, WHICH_a)) {
				Output a_1 = out.value;
				in.position = out.position;
				if (this.parse(out, in, WHICH_A)) {
					Output A_2 = out.value;
					in.position = out.position;
					if (this.parse(out, in, WHICH_b)) {
						this.addOutput(output, a_1);
						this.addOutput(output, A_2);
						this.addOutput(output, out.value);
						out.value = this.makeSuccess(output, out.position);
						return true;
					} else {
						this.backOutput(out.value);
						this.backOutput(A_2);
						this.backOutput(a_1);
					}
				} else {
					this.backOutput(out.value);
					this.backOutput(a_1);
				}
			} else {
				this.backOutput(out.value);
			}
			in.position = position;
			if (this.parse(out, in, WHICH_b)) {
				this.addOutput(output, out.value);
				out.value = this.makeSuccess(output, out.position);
				return true;
			} else {
				this.backOutput(out.value);
			}
			out.position = position;
			out.value = this.makeFail(output, out.position);
			return false;
		}
		protected abstract Output newOutput(Input input, int which);
		protected abstract Output addOutput(Output parent, Output child);
		protected abstract Output makeSuccess(Output output, long position);
		protected abstract Output makeFail(Output output, long position);
		protected abstract Output backOutput(Output output);
	}
}

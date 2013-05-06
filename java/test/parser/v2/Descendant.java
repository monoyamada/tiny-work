package parser.v2;

import java.io.IOException;

import tiny.lang.Debug;

abstract class Descendant<Input, Output> {
	public static final int VAR_S = 0;
	public static final int VAR_A = 1;
	public static final int VAR_B = 2;
	public static final int VAR_C = 3;
	public static final int VAR_a = 4;
	public static final int VAR_b = 5;
	public static final int VAR_c = 6;

	public Output read(int which, Input input) throws IOException {
		Debug.log().debug(which + ":"+this.position(input));
		switch (which) {
		case VAR_S:
			return this.read_S(input);
		case VAR_A:
			return this.read_A(input);
		case VAR_B:
			return this.read_B(input);
		case VAR_C:
			return this.read_C(input);
		case VAR_a:
			return this.read_a(input);
		case VAR_b:
			return this.read_b(input);
		case VAR_c:
			return this.read_c(input);
		default:
			throw new IllegalArgumentException("unknown tag=" + which);
		}
	}
	protected Output read_S(Input input) throws IOException {
		long position = this.position(input);
		Output a_1 = this.read(VAR_a, input);
		if (a_1 != null) {
			Output S_2 = this.read(VAR_S, input);
			if (S_2 != null) {
				Output A_3 = this.read(VAR_A, input);
				if (A_3 != null) {
					Output out = this.newOutput(VAR_S, position);
					this.addOutput(out, a_1);
					this.addOutput(out, S_2);
					this.addOutput(out, A_3);
					return out;
				} else {
					this.backOutput(a_1);
					this.backOutput(S_2);
				}
			} else {
				this.backOutput(a_1);
			}
		}
		this.position(input, position);
		return this.newOutput(VAR_S, position);
	}
	protected Output read_A(Input input) throws IOException {
		long position = this.position(input);
		Output a_1 = this.read(VAR_a, input);
		if (a_1 != null) {
			Output b_2 = this.read(VAR_b, input);
			if (b_2 != null) {
				Output S_3 = this.read(VAR_S, input);
				if (S_3 != null) {
					Output out = this.newOutput(VAR_A, position);
					this.addOutput(out, a_1);
					this.addOutput(out, b_2);
					this.addOutput(out, S_3);
					return out;
				} else {
					this.backOutput(a_1);
					this.backOutput(b_2);
				}
			} else {
				this.backOutput(a_1);
			}
		}
		this.position(input, position);
		Output c_1 = this.read(VAR_c, input);
		if (c_1 != null) {
			Output out = this.newOutput(VAR_A, position);
			this.addOutput(out, c_1);
			return out;
		}
		this.position(input, position);
		return null;
	}
	protected Output read_B(Input input) throws IOException {
		return null;
	}
	protected Output read_C(Input input) throws IOException {
		long position = this.position(input);
		return this.newOutput(VAR_C, position);
	}

	protected abstract long position(Input input);
	protected abstract Input position(Input input, long position);
	protected abstract Output newOutput(int tag, long position);
	protected abstract Output addOutput(Output parent, Output child);
	protected abstract Descendant<Input, Output> backOutput(Output dicarding);

	protected abstract Output read_a(Input input) throws IOException;
	protected abstract Output read_b(Input input) throws IOException;
	protected abstract Output read_c(Input input) throws IOException;
}

package parser.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tiny.lang.Debug;

import base.TestBase;

public class PikeTest extends ThompsonTest {
	static final int ENTER_CODE = MATCH_CODE + 1;
	static final int LEAVE_CODE = ENTER_CODE  + 1;

	static final int ASSIGN_NODE = CHAR_NODE + 1;
	static final int VARIABLE_NODE = ASSIGN_NODE + 1;

	static class PikeCompiler extends Compiler {
		List<Code> compile(Node eq) {
			List<Code> codes = new ArrayList<Code>();
			this.compile(codes, eq);
			codes.add(Code.newMatch());
			return codes;
		}
		List<Code> compile(List<Code> output, Node eq) {
			switch (eq.type) {
			case PLUS_NODE: {
				if (eq.right == null || eq.left == null) {
					String msg = "imcomplete multiplication";
					throw new IllegalArgumentException(msg);
				}
				int split = output.size();
				output.add(Code.newSplit(split + 1, 0));
				this.compile(output, eq.left);
				int jump = output.size();
				output.add(Code.newJump(0));
				output.get(split).y = jump + 1;
				this.compile(output, eq.right);
				output.get(jump).x = output.size();
			}
			break;
			case MULTIPLIES_NODE: {
				if (eq.right == null || eq.left == null) {
					String msg = "imcomplete multiplication";
					throw new IllegalArgumentException(msg);
				}
				this.compile(output, eq.left);
				this.compile(output, eq.right);
			}
			break;
			case POWER_LIKE_NODE: {
				if (eq.right == null || eq.left == null) {
					String msg = "imcomplete power-like operator";
					throw new IllegalArgumentException(msg);
				}
				switch (eq.right.type) {
				case ANY_MORE_NODE: {
					int split = output.size();
					output.add(Code.newSplit(split + 1, 0));
					this.compile(output, eq.left);
					output.add(Code.newJump(split));
					output.get(split).y = output.size();
				}
				break;
				case ONE_MORE_NODE: {
					int start = output.size();
					this.compile(output, eq.left);
					output.add(Code.newSplit(start, output.size() + 1));
				}
				break;
				case ONE_OR_ZERO_NODE: {
					int split = output.size();
					output.add(Code.newSplit(split + 1, 0));
					this.compile(output, eq.left);
					output.get(split).y = output.size();
				}
				break;
				default:
					throw new Error("unknown node = " + eq.right.type);
				}
				break;
			}
			case CHAR_NODE: {
				output.add(Code.newChar(eq.value));
			}
			break;
			default:
				throw new Error("unknown node = " + eq.right.type);
			}
			return output;
		}
	}

	public void test_1() {
		Node a = Node.newChar('a').oneMore();
		Node b = Node.newChar('b').oneMore();
		Node eq = a.multiplies(b);
		// Node eq = a;
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
	public void test_2() {
		Node a = Node.newChar('a');
		Node a2 = Node.newChar('a').multiplies('a');
		Node eq = a.multiplies(a2).plus (a2.multiplies (a));
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
}

package parser.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.IntArrayList;

public class ThompsonVm {
	static final int NONE_CODE = 0;
	static final int CHAR_CODE = NONE_CODE + 1;
	static final int SPLIT_CODE = CHAR_CODE + 1;
	static final int JUMP_CODE = SPLIT_CODE + 1;
	static final int MATCH_CODE = JUMP_CODE + 1;
	// static final int MULTI_SPLIT_CODE = MATCH_CODE + 1;
	static final Code NONE_CODE_INSTANCE = new Code(NONE_CODE,
			StringHelper.EMPTY_STRING, 0, 0);

	static final int ZERO_NODE = 0;
	static final int PLUS_NODE = ZERO_NODE + 1;
	static final int MULTIPLIES_NODE = PLUS_NODE + 1;
	static final int POWER_LIKE_NODE = MULTIPLIES_NODE + 1;
	static final int ANY_MORE_NODE = POWER_LIKE_NODE + 1;
	static final int ONE_MORE_NODE = ANY_MORE_NODE + 1;
	static final int ONE_OR_ZERO_NODE = ONE_MORE_NODE + 1;
	static final int CHAR_NODE = ONE_OR_ZERO_NODE + 1;

	static Node newBinaryNode(int type, Node left, Node right) {
		return new Node(left, right, type, StringHelper.EMPTY_STRING);
	}
	static Node newCharNode(char value) {
		return new Node(null, null, CHAR_NODE, Character.toString(value));
	}
	static Node newCharNode(String value) {
		return new Node(null, null, CHAR_NODE, value);
	}
	static Node newAnyMoreNode() {
		return new Node(null, null, ANY_MORE_NODE, StringHelper.EMPTY_STRING);
	}
	static Node newOneMoreNode() {
		return new Node(null, null, ONE_MORE_NODE, StringHelper.EMPTY_STRING);
	}
	static Node newOneOrZeroNode() {
		return new Node(null, null, ONE_OR_ZERO_NODE, StringHelper.EMPTY_STRING);
	}

	static Code newNoneCode() {
		return NONE_CODE_INSTANCE;
	}
	static Code newCharCode(char value) {
		return new Code(CHAR_CODE, Character.toString(value), 0, 0);
	}
	static Code newSplitCode(int x, int y) {
		return new Code(SPLIT_CODE, StringHelper.EMPTY_STRING, x, y);
	}
	static Code newJumpCode(int x) {
		return new Code(JUMP_CODE, StringHelper.EMPTY_STRING, x, 0);
	}
	static Code newMatchCode() {
		return new Code(MATCH_CODE, StringHelper.EMPTY_STRING, 0, 0);
	}

	static class Node {
		final Node left;
		final Node right;
		final int type;
		final String value;

		Node(Node left, Node right, int type, String value) {
			this.left = left;
			this.right = right;
			this.type = type;
			this.value = value;
		}
		Node plus(Node right) {
			return newBinaryNode(PLUS_NODE, this, right);
		}
		Node plus(char right) {
			return this.plus(newCharNode(right));
		}
		Node multiplies(Node right) {
			return newBinaryNode(MULTIPLIES_NODE, this, right);
		}
		Node multiplies(char right) {
			return this.multiplies(newCharNode(right));
		}
		Node anyMore() {
			return newBinaryNode(POWER_LIKE_NODE, this, newAnyMoreNode());
		}
		Node oneMore() {
			return newBinaryNode(POWER_LIKE_NODE, this, newOneMoreNode());
		}
		Node oneOrZero() {
			return newBinaryNode(POWER_LIKE_NODE, this, newOneOrZeroNode());
		}

		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable toString(Appendable output) throws IOException {
			switch (this.type) {
			case PLUS_NODE: {
				if (this.left == null) {
					output.append("0 + ");
				} else {
					this.left.toString(output.append('(')).append(") + ");
				}
				if (this.right == null) {
					output.append("0");
				} else {
					this.right.toString(output.append('(')).append(')');
				}
			}
			break;
			case MULTIPLIES_NODE: {
				if (this.left == null) {
					output.append("0 ");
				} else {
					this.left.toString(output.append('(')).append(") ");
				}
				if (this.right == null) {
					output.append("0");
				} else {
					this.right.toString(output.append('(')).append(')');
				}
			}
			break;
			case POWER_LIKE_NODE: {
				if (this.right == null || this.left == null) {
					output.append('0');
				} else {
					this.right.toString(this.left.toString(output).append(" ^ "));
				}
			}
			break;
			case CHAR_NODE: {
				output.append(this.value);
			}
			break;
			case ANY_MORE_NODE: {
				output.append('*');
			}
			break;
			case ONE_MORE_NODE: {
				output.append('+');
			}
			break;
			case ONE_OR_ZERO_NODE: {
				output.append('?');
			}
			break;
			default:
				throw new Error("unknown node = " + this.type);
			}
			return output;
		}
	}

	static class Code implements Cloneable {
		public static final Code[] EMPTY_ARRAY = {};

		int type;
		String value;
		int x;
		int y;

		// int[] xs;

		Code(int type, String value, int x, int y) {
			this.type = type;
			this.value = value;
			this.x = x;
			this.y = y;
		}
		public Code clone() {
			try {
				return (Code) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new Error(ex);
			}
		}

		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable toString(Appendable output) throws IOException {
			switch (this.type) {
			case NONE_CODE:
				output.append("none");
			break;
			case CHAR_CODE:
				output.append("char ").append(this.value);
			break;
			case SPLIT_CODE:
				output.append("split ").append(Integer.toString(this.x)).append(' ')
						.append(Integer.toString(this.y));
			break;
			case JUMP_CODE:
				output.append("jump ").append(Integer.toString(this.x));
			break;
			case MATCH_CODE:
				output.append("match");
			break;
			// case MULTI_SPLIT_CODE:
			// output.append("multi-split");
			// for (int i = 0, n = this.xs.length; i < n; ++i) {
			// output.append(' ').append(Integer.toString(this.xs[i]));
			// }
			// break;
			default:
				throw new Error("unknown code = " + this.type);
			}
			return output;
		}
	}

	static class Compiler {
		private IntArrayList bufferIntList;
		//private List<Code> bufferCodeList;

		List<Code> compile(Node eq) {
			return this.compile(new ArrayList<Code>(), eq);
		}
		List<Code> compile(List<Code> output, Node eq) {
			this.toCode(output, eq);
			output.add(newMatchCode());
			return output;
		}
		List<Code> toCode(List<Code> output, Node eq) {
			switch (eq.type) {
			case PLUS_NODE: {
				if (eq.right == null || eq.left == null) {
					String msg = "imcomplete multiplication";
					throw new IllegalArgumentException(msg);
				}
				int split = output.size();
				output.add(newSplitCode(split + 1, 0));
				this.toCode(output, eq.left);
				int jump = output.size();
				output.add(newJumpCode(0));
				output.get(split).y = jump + 1;
				this.toCode(output, eq.right);
				output.get(jump).x = output.size();
			}
			break;
			case MULTIPLIES_NODE: {
				if (eq.right == null || eq.left == null) {
					String msg = "imcomplete multiplication";
					throw new IllegalArgumentException(msg);
				}
				this.toCode(output, eq.left);
				this.toCode(output, eq.right);
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
					output.add(newSplitCode(split + 1, 0));
					this.toCode(output, eq.left);
					output.add(newJumpCode(split));
					output.get(split).y = output.size();
				}
				break;
				case ONE_MORE_NODE: {
					int start = output.size();
					this.toCode(output, eq.left);
					output.add(newSplitCode(start, output.size() + 1));
				}
				break;
				case ONE_OR_ZERO_NODE: {
					int split = output.size();
					output.add(newSplitCode(split + 1, 0));
					this.toCode(output, eq.left);
					output.get(split).y = output.size();
				}
				break;
				default:
					throw new Error("unknown node = " + eq.right.type);
				}
				break;
			}
			case CHAR_NODE: {
				String value = eq.value;
				for (int i = 0, n = value.length(); i < n; ++i) {
					output.add(newCharCode(value.charAt(i)));
				}
			}
			break;
			default:
				throw new Error("unknown node = " + eq.right.type);
			}
			return output;
		}
		int eliminateJump(List<Code> codes) {
			if (codes.size() < 2) {
				return 0;
			}
			int count = 0;
			for (int n = codes.size(); 0 < n--;) {
				Code code = codes.get(n);
				switch (code.type) {
				case NONE_CODE:
				case CHAR_CODE:
				case MATCH_CODE:
				// case MULTI_SPLIT_CODE:
				break;
				case SPLIT_CODE:
					count += this.eliminateSplitJump(codes, code);
				break;
				case JUMP_CODE:
					count += this.eliminateJumpJump(codes, code);
				break;
				default:
					throw new Error("unknown code = " + code.type);
				}
			}
			return count;
		}
		private int eliminateJumpJump(List<Code> codes, Code code) {
			int count = 0;
			int index = code.x;
			Code next = codes.get(index);
			while (next.type == JUMP_CODE) {
				index = next.x;
				next = codes.get(index);
				++count;
			}
			next = codes.get(index);
			if (next.type == SPLIT_CODE) {
				code.type = next.type;
				code.x = next.x;
				code.y = next.y;
				count += this.eliminateSplitJump(codes, code);
			}
			return count;
		}
		private int eliminateSplitJump(List<Code> codes, Code code) {
			int count = 0;
			int index = code.x;
			Code next = codes.get(index);
			while (next.type == JUMP_CODE) {
				index = next.x;
				next = codes.get(index);
				++count;
			}
			code.x = index;

			index = code.y;
			next = codes.get(index);
			while (next.type == JUMP_CODE) {
				index = next.x;
				next = codes.get(index);
				++count;
			}
			code.y = index;

			return count;
		}

		// int makeMultiSplit(List<Code> codes) {
		// if (codes.size() < 2) {
		// return 0;
		// }
		// int count = 0;
		// for (int n = codes.size(); 0 < n--;) {
		// Code code = codes.get(n);
		// switch (code.type) {
		// case NONE_CODE:
		// case CHAR_CODE:
		// case MATCH_CODE:
		// case MULTI_SPLIT_CODE:
		// break;
		// case SPLIT_CODE:
		// case JUMP_CODE:
		// count += this.makeMultiSplit(codes, code);
		// break;
		// default:
		// throw new Error("unknown code = " + code.type);
		// }
		// }
		// return count;
		// }
		// private int makeMultiSplit(List<Code> codes, Code code) {
		// if (code.type == MULTI_SPLIT_CODE) {
		// return 0;
		// }
		// // this search is first because non-recursive,
		// // but priority order will be broken.
		// IntArrayList indices = this.getBufferIntList().clear();
		// List<Code> stack = this.getBufferCodeList();
		// stack.clear();
		// stack.add(code);
		// for (int i = 0; i < stack.size(); ++i) {
		// Code c = stack.get(i);
		// switch (c.type) {
		// case JUMP_CODE:
		// this.makeMultiSplitPush(indices, stack, codes, c.x);
		// break;
		// case SPLIT_CODE:
		// this.makeMultiSplitPush(indices, stack, codes, c.x);
		// this.makeMultiSplitPush(indices, stack, codes, c.y);
		// break;
		// case MULTI_SPLIT_CODE:
		// for (int ii = 0, nn = c.xs.length; ii < nn; ++ii) {
		// if (!indices.contains(c.xs[ii])) {
		// indices.push(c.xs[ii]);
		// }
		// }
		// break;
		// default:
		// break;
		// }
		// }
		// code.type = MULTI_SPLIT_CODE;
		// code.xs = indices.toArray();
		// return 1;
		// }
		// private void makeMultiSplitPush(IntArrayList indices, List<Code> stack,
		// List<Code> codes, int codeIndex) {
		// Code code = codes.get(codeIndex);
		// switch (code.type) {
		// case CHAR_CODE:
		// case MATCH_CODE:
		// if (!indices.contains(codeIndex)) {
		// indices.push(codeIndex);
		// }
		// break;
		// case JUMP_CODE:
		// case SPLIT_CODE:
		// case MULTI_SPLIT_CODE:
		// if (!stack.contains(code)) {
		// stack.add(code);
		// }
		// break;
		// default:
		// break;
		// }
		// }
		private IntArrayList getBufferIntList() {
			if (this.bufferIntList == null) {
				this.bufferIntList = new IntArrayList(128);
			}
			return this.bufferIntList;
		}
		// private List<Code> getBufferCodeList() {
		// if (this.bufferCodeList == null) {
		// this.bufferCodeList = new ArrayList<Code>(128);
		// }
		// return this.bufferCodeList;
		// }
		public int eliminateUnusedCode(List<Code> codes) {
			IntArrayList buffer = this.getBufferIntList();
			int[] refs = buffer.ensureCapacity(codes.size()).getArray();
			refs[0] += 1;
			for (int n = codes.size(); 0 < n--;) {
				Code code = codes.get(n);
				switch (code.type) {
				case CHAR_CODE:
					++refs[n + 1];
				break;
				case SPLIT_CODE:
					++refs[code.x];
					++refs[code.y];
				break;
				case JUMP_CODE:
					++refs[code.x];
				break;
//				case MULTI_SPLIT_CODE:
//					for (int nn = code.xs.length; 0 < nn--;) {
//						++refs[code.xs[nn]];
//					}
//				break;
				case MATCH_CODE:
				break;
				default:
					throw new Error("unknown code = " + code.type);
				}
			}
			int count = 0;
			for (int n = codes.size(); 0 < n--;) {
				if (refs[n] < 1 && codes.get(n).type != NONE_CODE) {
					codes.get(n).type = NONE_CODE;
					++count;
				}
			}
			return count;
		}
	}
}

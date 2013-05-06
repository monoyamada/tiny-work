package parser;

import java.io.IOException;

import tiny.function.Stack;
import tiny.lang.Messages;
import tiny.primitive.LongStack;

public class SimpleGrammar {
	public static final int ALPHABET_TYPE = 0;
	public static final int VARIABLE_TYPE = ALPHABET_TYPE + 1;
	public static final int PLUS_TYPE = VARIABLE_TYPE + 1;
	public static final int MULTIPLIES_TYPE = PLUS_TYPE + 1;

	protected static ThreadLocal<Stack<Expression>> expressionStack = null;

	protected static Stack<Expression> getExpressionStack() {
		if (expressionStack == null) {
			expressionStack = new ThreadLocal<Stack<Expression>>() {
				@Override
				protected Stack<Expression> initialValue() {
					return new ArrayStack<Expression>();
				}
			};
		}
		return expressionStack.get();
	}

	public static interface Expression {
		Expression[] EMPTY_ARRAY = {};

		int getExpressioinType();
		int getChildSize();
		Expression getChild(int index);
		String getSymbol();
	}

	protected static Expression getFirstChild(Expression parent) {
		if (parent.getChildSize() < 1) {
			return null;
		}
		return parent.getChild(0);
	}

	protected static Expression getNextChild(Expression parent, Expression child) {
		for (int i = 0, n = parent.getChildSize(); i < n; ++i) {
			if (parent.getChild(i) == child) {
				if (i + 1 == n) {
					return null;
				}
				return parent.getChild(i + 1);
			}
		}
		throw new IndexOutOfBoundsException("could not find child");
	}

	protected static void toString(Appendable output, Expression node)
			throws IOException {
		final int down = 0;
		final int up = down + 1;
		int state = down;
		Stack<Expression> stack = getExpressionStack();
		while (node != null) {
			switch (state) {
			case down: {
				output.append(node.getSymbol());
				if (node.getChildSize() < 1) {
					state = up;
				} else {
					output.append('(');
					stack.pushValue(node);
					node = node.getChild(0);
				}
			}
				break;
			default: {
				output.append(')');
				Expression parent = stack.topValue(null);
				node = getNextChild(parent, node);
				if (node == null) {
					node = parent;
				} else {
					state = down;
				}
			}
				break;
			}
		}
	}

	protected static abstract class AbNode implements Expression {
		final String symbol;

		public AbNode(String symbol) {
			this.symbol = symbol;
		}
		@Override
		public String getSymbol() {
			return this.symbol;
		}
		public String toString() {
			StringBuilder buffer = new StringBuilder(128);
			try {
				SimpleGrammar.toString(buffer, this);
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			return buffer.toString();
		}
	}

	protected static abstract class AbNode_0 extends AbNode {
		public AbNode_0(String symbol) {
			super(symbol);
		}
		public int getChildSize() {
			return 0;
		}
		@Override
		public Expression getChild(int index) {
			String msg = Messages.getIndexOutOfRange(0, index, 0);
			throw new IndexOutOfBoundsException(msg);
		}
	}

	protected static abstract class AbNode_1 extends AbNode {
		final Expression child;

		public AbNode_1(String symbol, Expression child) {
			super(symbol);
			this.child = child;
		}
		public int getChildSize() {
			return 1;
		}
		@Override
		public Expression getChild(int index) {
			if (index == 0) {
				return this.child;
			}
			String msg = Messages.getIndexOutOfRange(0, index, 1);
			throw new IndexOutOfBoundsException(msg);
		}
	}

	protected static abstract class AbNode_2 extends AbNode {
		final Expression child_0;
		final Expression child_1;

		public AbNode_2(String symbol, Expression child0, Expression child1) {
			super(symbol);
			this.child_0 = child0;
			this.child_1 = child1;
		}
		public int getChildSize() {
			return 2;
		}
		@Override
		public Expression getChild(int index) {
			switch (index) {
			case 0:
				return this.child_0;
			case 1:
				return this.child_1;
			default: {
				String msg = Messages.getIndexOutOfRange(0, index, 2);
				throw new IndexOutOfBoundsException(msg);
			}
			}
		}
	}

	protected static class Alphabet extends AbNode_0 {
		public Alphabet(String symbol) {
			super(symbol);
		}
		@Override
		public int getExpressioinType() {
			return ALPHABET_TYPE;
		}
	}

	protected static class Variable extends AbNode_1 {
		public Variable(String symbol, Expression child) {
			super(symbol, child);
		}
		@Override
		public int getExpressioinType() {
			return VARIABLE_TYPE;
		}
	}

	protected static class Plus extends AbNode_2 {
		public Plus(Expression child0, Expression child1) {
			super("+", child0, child1);
		}
		@Override
		public int getExpressioinType() {
			return PLUS_TYPE;
		}
	}

	protected static class Multiplies extends AbNode_2 {
		public Multiplies(Expression child0, Expression child1) {
			super(" ", child0, child1);
		}
		@Override
		public int getExpressioinType() {
			return PLUS_TYPE;
		}
	}

	public static Expression alphabet(String symbol) {
		return new Alphabet(symbol);
	}
	public static Expression variable(String symbol, Expression child) {
		return new Variable(symbol, child);
	}
	public static Expression plus(Expression child0, Expression child1) {
		return new Plus(child0, child1);
	}
	public static Expression multiplies(Expression child0, Expression child1) {
		return new Multiplies(child0, child1);
	}
	/**
	 * <code>
	 * E_0 = E_1 E_0^?
	 * E_1 = E_2 ('+' E_2)^?
	 * E_2 = E_3 + '(' E_0 ')'
	 * E_3 = 'a-z' + 'A-Z'
	 * </code>
	 * 
	 * @param expression
	 * @return
	 */
	public static Expression expression(String input) {
		return null;
	}

	protected static final int ZERO = 0;
	protected static final int E_0 = ZERO + 1;
	protected static final int E_1 = E_0 + 1;
	protected static final int E_2 = E_1 + 1;
	protected static final int E_3 = E_2 + 1;

	protected static boolean e_0(LongStack output, String input, int index) {
		char alphabet = input.charAt(index);
		switch (alphabet) {
		}
		return false;
	}
}

package lambda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import tiny.lang.Messages;

public class Lambda {
	public static final int ABSTRACT_TYPE = 1;
	public static final int APPLICATION_TYPE = ABSTRACT_TYPE + 1;
	public static final int VARIABLE_TYPE = APPLICATION_TYPE + 1;

	public static interface Expression {
		Expression[] EMPTY_ARRAY = {};

		int getExpressioinType();
		int getChildSize();
		Expression getChild(int index);
	}

	public static interface Abstract extends Expression {
	}

	public static interface Application extends Expression {
	}

	public static interface Variable extends Expression {
		String getName();
	}

	protected static abstract class AbNode implements Expression {
		public String toString() {
			StringBuilder buffer = new StringBuilder(128);
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
				return ex.getMessage();
			}
			return buffer.toString();
		}
		protected void toString(Appendable output) throws IOException {
			switch (this.getExpressioinType()) {
			case VARIABLE_TYPE: {
				Variable that = (Variable) this;
				output.append(that.getName());
				return;
			}
			case APPLICATION_TYPE: {
				output.append('(');
				AbNode node = (AbNode) this.getChild(0);
				node.toString(output);
				output.append(' ');
				node = (AbNode) this.getChild(1);
				node.toString(output);
				output.append(')');
				return;
			}
			case ABSTRACT_TYPE: {
				output.append('{');
				AbNode node = (AbNode) this.getChild(0);
				node.toString(output);
				output.append(' ');
				node = (AbNode) this.getChild(1);
				node.toString(output);
				output.append('}');
				return;
			}
			default:
				String msg = Messages.getUnexpectedValue("expression type",
						"abstract|application|variable", this.getExpressioinType());
				throw new IOException(msg);
			}
		}
	}

	protected static abstract class AbNode_0 extends AbNode {
		public int getChildSize() {
			return 0;
		}
		@Override
		public Expression getChild(int index) {
			String msg = Messages.getIndexOutOfRange(0, index, 0);
			throw new IndexOutOfBoundsException(msg);
		}
	}

	protected static abstract class AbNode_2 extends AbNode {
		final Expression child_0;
		final Expression child_1;

		public AbNode_2(Expression child0, Expression child1) {
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

	protected static class VariableNode extends AbNode_0 implements Variable {
		final String name;

		public VariableNode(String name) {
			this.name = name;
		}
		@Override
		public int getExpressioinType() {
			return VARIABLE_TYPE;
		}
		@Override
		public String getName() {
			return this.name;
		}
	}

	protected static class AbstractNode extends AbNode_2 implements Abstract {
		public AbstractNode(Variable child0, Expression child1) {
			super(child0, child1);
		}
		@Override
		public int getExpressioinType() {
			return ABSTRACT_TYPE;
		}
		protected String getParameterName() {
			Variable x = (Variable) this.getChild(0);
			return x.getName();
		}
	}

	protected static class ApplicationNode extends AbNode_2 implements
			Application {
		public ApplicationNode(Expression child0, Expression child1) {
			super(child0, child1);
		}
		@Override
		public int getExpressioinType() {
			return APPLICATION_TYPE;
		}
	}

	public static Abstract lambda(String x, Expression y) {
		return new AbstractNode(variable(x), y);
	}
	public static Abstract lambda(String x, String y) {
		return lambda(x, variable(y));
	}
	public static Application apply(Expression x, Expression y) {
		return new ApplicationNode(x, y);
	}
	public static Application apply(String x, Expression y) {
		return apply(variable(x), y);
	}
	public static Application apply(Expression x, String y) {
		return apply(x, variable(y));
	}
	public static Application apply(String x, String y) {
		return apply(variable(x), variable(y));
	}
	public static Variable variable(String name) {
		return new VariableNode(name);
	}
	public static int getParametrSize(Expression expr) {
		int size = 0;
		while (expr != null) {
			switch (expr.getExpressioinType()) {
			case ABSTRACT_TYPE:
				++size;
				expr = expr.getChild(1);
				break;
			default:
				return size;
			}
		}
		return size;
	}

	protected static class Stack<T> extends ArrayList<T> {
		private static final long serialVersionUID = 4886947044690957572L;

		public Stack() {
			super();
		}
		public Stack(Collection<? extends T> c) {
			super(c);
		}
		public Stack(int initialCapacity) {
			super(initialCapacity);
		}
		public T getLast() {
			return this.get(this.size() - 1);
		}
		public T setLast(T value) {
			return this.set(this.size() - 1, value);
		}
		public int pushLast(T value) {
			this.add(value);
			return this.size() - 1;
		}
		public T popLast() {
			return this.remove(this.size() - 1);
		}
	}

	public static Expression parse(Map<String, Expression> env, String text)
			throws IOException {
		final int LEXER_EXPRESSION = 0;
		final int LEXER_VARIABLE = LEXER_EXPRESSION + 1;
		int appDepth = 0;
		Stack<Expression> stack = new Stack<Expression>(1024);
		stack.add(null);
		int begin = 0;
		int state = LEXER_EXPRESSION;
		for (int i = 0, n = text.length(); i < n; ++i) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c)) {
				switch (state) {
				case LEXER_VARIABLE: {
					String name = text.substring(begin, i);
					Expression expr = env.get(name);
					if (expr == null) {
						expr = variable(name);
					}
					Expression lhs = stack.getLast();
					if (lhs == null) {
						stack.setLast(expr);
					} else {
						stack.setLast(apply(lhs, expr));
					}
					state = LEXER_EXPRESSION;
					break;
				}
				default:
					break;
				}
				begin = i + 1;
				continue;
			}
			switch (c) {
			case '(':
				switch (state) {
				case LEXER_VARIABLE: {
					String name = text.substring(begin, i);
					Expression expr = env.get(name);
					if (expr == null) {
						expr = variable(name);
					}
					Expression lhs = stack.getLast();
					if (lhs == null) {
						stack.setLast(expr);
					} else {
						stack.setLast(apply(lhs, expr));
					}
					state = LEXER_EXPRESSION;
					break;
				}
				default:
					break;
				}
				stack.pushLast(null);
				++appDepth;
				begin = i + 1;
				continue;
			case ')': {
				if (appDepth < 1) {
					String msg = "mismatch bracket at " + text.substring(0, i + 1);
					throw new IOException(msg);
				}
				switch (state) {
				case LEXER_VARIABLE: {
					String name = text.substring(begin, i);
					Expression expr = env.get(name);
					if (expr == null) {
						expr = variable(name);
					}
					Expression lhs = stack.getLast();
					if (lhs == null) {
						stack.setLast(expr);
					} else {
						stack.setLast(apply(lhs, expr));
					}
					state = LEXER_EXPRESSION;
					break;
				}
				default:
					break;
				}
				Expression rhs = stack.getLast();
				stack.popLast();
				if (rhs != null) {
					Expression lhs = stack.getLast();
					if (lhs == null) {
						stack.setLast(rhs);
					} else {
						stack.setLast(apply(lhs, rhs));
					}
				}
				--appDepth;
				begin = i + 1;
				continue;
			}
			default:
				switch (state) {
				case LEXER_EXPRESSION:
					begin = i;
					state = LEXER_VARIABLE;
					break;
				default:
					break;
				}
				break;
			}
		}
		if (0 < appDepth) {
			String msg = "mismatch bracket at " + text;
			throw new IOException(msg);
		}
		switch (state) {
		case LEXER_VARIABLE: {
			if (begin < text.length()) {
				String name = text.substring(begin).trim();
				Expression expr = env.get(name);
				if (expr == null) {
					expr = variable(name);
				}
				Expression lhs = stack.getLast();
				if (lhs == null) {
					stack.setLast(expr);
				} else {
					stack.setLast(apply(lhs, expr));
				}
			}
			break;
		}
		default:
			break;
		}
		if (stack.size()!=1){
			String msg = "unexpected stack size=" + stack.size() + " at end of text";
			throw new IOException(msg);
		}
		return stack.getLast();
	}
}

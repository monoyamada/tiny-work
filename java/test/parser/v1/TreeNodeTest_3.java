package parser.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import parser.v1.TreeNodeTest_3.LL.Transition;
import parser.v1.TreeNodeTest_3.LL.TransitionTraverser;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.ObjectHelper;
import tiny.lang.StringHelper;

public class TreeNodeTest_3 extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	/**
	 * semiring
	 * 
	 * @param <T>
	 */
	static abstract class Node<T extends Node<T>> extends TreeNode<T> {
		public static final int ZERO = 0;
		public static final int ONE = ZERO + 1;
		public static final int MULTIPLIES = ONE + 1;
		public static final int PLUS = MULTIPLIES + 1;
		protected static final int NUMBER_OF_TYPES = PLUS + 1;

		private static final Info[] INFOS = Node.newInfo();

		protected interface Info {
			String typeName();
			String prefixName(Object node);
			String infixName(Object node);
			int precedence();
		}

		private static Info[] newInfo() {
			Info[] out = new Info[Node.NUMBER_OF_TYPES];
			return Node.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			out[ZERO] = new Info() {
				public String typeName() {
					return "zero";
				}
				String name(Object node) {
					return node != null ? "0" : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[ONE] = new Info() {
				public String typeName() {
					return "one";
				}
				String name(Object node) {
					return node != null ? "1" : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[MULTIPLIES] = new Info() {
				public String typeName() {
					return "multiplies";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "" : null;
				}
				public int precedence() {
					return 200;
				}
			};
			out[PLUS] = new Info() {
				public String typeName() {
					return "plus";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "+" : null;
				}
				public int precedence() {
					return 100;
				}
			};
			return out;
		}
		static String typeName(int type) {
			return Node.INFOS[type].typeName();
		}

		final int type;

		Node(int type) {
			this.type = type;
		}
		public int type() {
			return this.type;
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public boolean isBinary(int type) {
			return this.isType(type) && this.isBinary();
		}
		public boolean isBinary() {
			T first = this.firstNode();
			T last = this.lastNode();
			if (first == null || last == null) {
				return false;
			}
			return first.nextNode() == last;
		}
		public boolean isUnary(int type) {
			return this.isType(type) && this.isUnary();
		}
		public boolean isUnary() {
			T first = this.firstNode();
			T last = this.lastNode();
			if (first == null || last == null) {
				return false;
			}
			return first == last;
		}
		public boolean isLeaf(int type) {
			return this.isType(type) && this.isLeaf();
		}
		public boolean isLeaf() {
			return this.firstNode() == null;
		}

		@Override
		public String toString() {
			return this.prefixName();
		}
		public String prefixName() {
			return this.nodeInfo().prefixName(this.that());
		}
		public String infixName() {
			return this.nodeInfo().infixName(this.that());
		}
		public String typeName() {
			return this.nodeInfo().typeName();
		}
		protected Info nodeInfo() {
			return Node.INFOS[this.type];
		}

		abstract T newNode(int type);

		T add(T child) {
			return this.addLastNode(child);
		}
		/**
		 * @param rhs
		 * @return cloned both this and rhs.
		 */
		T multiplies(T rhs) {
			switch (this.type()) {
			case ZERO:
				return this.clone();
			case ONE:
				return rhs.clone();
			default:
			break;
			}
			switch (rhs.type()) {
			case ZERO:
				return rhs.clone();
			case ONE:
				return this.clone();
			default:
			break;
			}
			T node = this.newNode(Node.MULTIPLIES);
			return node.add(this.clone()).add(rhs.clone());
		}
		/**
		 * @param rhs
		 * @return cloned both this and rhs.
		 */
		T plus(T rhs) {
			switch (this.type()) {
			case ZERO:
				return rhs.clone();
			default:
			break;
			}
			switch (rhs.type()) {
			case ZERO:
				return this.clone();
			default:
			break;
			}
			T node = this.newNode(Node.PLUS);
			return node.add(this.clone()).add(rhs.clone());
		}
		/**
		 * slow operation.
		 * 
		 * @return
		 */
		public int size() {
			int n = 0;
			T node = this.firstNode();
			for (; node != null; node = node.nextNode()) {
				++n;
			}
			return n;
		}

		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeInfix(Appendable output) throws IOException {
			return this.writeInfix(output, null);
		}
		protected Appendable writeInfix(Appendable output, T parent)
				throws IOException {
			if (this.isLeaf()) {
				return this.writeInfixLeaf(output, parent);
			}
			boolean braket = false;
			if (parent != null) {
				if (this.nodeInfo().precedence() < parent.nodeInfo().precedence()) {
					braket = true;
				}
			}
			if (braket) {
				output.append('(');
			}
			String op = this.infixName();
			T child = this.firstNode();
			child.writeInfix(output, this.that());
			for (child = child.nextNode(); child != null; child = child.nextNode()) {
				if (op.length() < 1) {
					output.append(' ');
				} else {
					output.append(' ');
					output.append(op);
					output.append(' ');
				}
				child.writeInfix(output, this.that());
			}
			if (braket) {
				output.append(')');
			}
			return output;
		}
		protected Appendable writeInfixLeaf(Appendable output, T parent)
				throws IOException {
			return output.append(this.infixName());
		}
	}

	static class GrammarNode extends Node<GrammarNode> {
		public static final int EQUATIONS = Node.NUMBER_OF_TYPES;
		public static final int ASSIGNS = EQUATIONS + 1;
		public static final int VARIABLE = ASSIGNS + 1;
		protected static final int NUMBER_OF_TYPES = VARIABLE + 1;

		private static final Info[] INFOS = GrammarNode.newInfo();

		private static Info[] newInfo() {
			Info[] out = new Info[GrammarNode.NUMBER_OF_TYPES];
			return GrammarNode.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			Node.newInfo(out);
			out[EQUATIONS] = new Info() {
				public String typeName() {
					return "system";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? ";" : null;
				}
				public int precedence() {
					return 5;
				}
			};
			out[ASSIGNS] = new Info() {
				public String typeName() {
					return "assigns";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "=" : null;
				}
				public int precedence() {
					return 10;
				}
			};
			out[VARIABLE] = new Info() {
				public String typeName() {
					return "variable";
				}
				String name(Object node) {
					GrammarNode x = (GrammarNode) node;
					Object y = x.value();
					return y != null ? y.toString() : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			return out;
		}
		static String typeName(int type) {
			return GrammarNode.INFOS[type].typeName();
		}

		Object value;

		GrammarNode(int type) {
			this(type, null);
		}
		GrammarNode(String name) {
			this(VARIABLE, name);
		}
		GrammarNode(int type, Object value) {
			super(type);
			this.value = value;
		}
		@Override
		protected Info nodeInfo() {
			return GrammarNode.INFOS[this.type];
		}

		public Object value() {
			return this.value;
		}
		@Override
		GrammarNode newNode(int type) {
			return new GrammarNode(type);
		}

		/**
		 * @param rhs
		 * @return cloned both this and rhs.
		 */
		GrammarNode assigns(GrammarNode rhs) {
			switch (this.type()) {
			case VARIABLE:
			break;
			default: {
				String msg = Messages.getUnexpectedValue("type",
						GrammarNode.typeName(VARIABLE), this.typeName());
				throw new UnsupportedOperationException(msg);
			}
			}
			GrammarNode node = this.newNode(GrammarNode.ASSIGNS);
			return node.add(this.clone()).add(rhs.clone());
		}
	}

	static class Variable {
		static final int FREE = 0;
		static final int BOUNDED_START = FREE + 1;
		static final int BOUNDED_END = BOUNDED_START + 1;
		static final int NUMBER_OF_TYPES = BOUNDED_END + 1;

		static interface Info {
			String typeName();
		}

		static final Info[] INFOS = Variable.newInfos();
		public static final Variable[] EMPTY_ARRAY = {};

		static Info[] newInfos() {
			Info[] out = new Info[NUMBER_OF_TYPES];
			out[FREE] = new Info() {
				public String typeName() {
					return "free";
				}
			};
			out[BOUNDED_START] = new Info() {
				public String typeName() {
					return "start";
				}
			};
			out[BOUNDED_END] = new Info() {
				public String typeName() {
					return "end";
				}
			};
			return out;
		};

		final String name;
		int type;
		Variable opposite;

		Variable(String name) {
			this(name, FREE);
		}
		Variable(String name, int type) {
			if (name == null) {
				String msg = Messages.getUnexpectedValue("name", "non-null", "null");
				throw new IllegalArgumentException(msg);
			}
			this.name = name;
			this.type = type;
		}
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalVariable((Variable) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalVariable(Variable x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return this.name.equals(x.name) && this.type == x.type;
		}
		public String name() {
			return this.name;
		}
		public int type() {
			return this.type;
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public boolean isBounded() {
			switch (this.type) {
			case BOUNDED_START:
			case BOUNDED_END:
				return true;
			default:
				return false;
			}
		}
		Variable opposite() {
			return this.opposite;
		}
		@Override
		public String toString() {
			return this.name;
		}
		Variable makeOpposite(Variable x) {
			this.opposite = x;
			x.opposite = this;
			return this;
		}
		Info info() {
			return Variable.INFOS[this.type()];
		}
		String typeName() {
			return this.info().typeName();
		}
	}

	static class DyckNode extends Node<DyckNode> {
		public static final int GENERATOR = Node.NUMBER_OF_TYPES;
		protected static final int NUMBER_OF_TYPES = GENERATOR + 1;

		private static final Info[] INFOS = DyckNode.newInfo();

		private static Info[] newInfo() {
			Info[] out = new Info[DyckNode.NUMBER_OF_TYPES];
			return DyckNode.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			Node.newInfo(out);
			out[GENERATOR] = new Info() {
				public String typeName() {
					return "generator";
				}
				String name(Object node) {
					DyckNode x = (DyckNode) node;
					return x.toInfix();
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			return out;
		}
		static String typeName(int type) {
			return DyckNode.INFOS[type].typeName();
		}

		final Variable[] variables;
		final int[] dycks;

		DyckNode(int type) {
			this(type, null, null);
		}
		DyckNode(Variable[] variables) {
			this(DyckNode.GENERATOR, variables, null);
		}
		DyckNode(Variable[] variables, int[] dycks) {
			this(DyckNode.GENERATOR, variables, dycks);
		}
		DyckNode(int type, Variable[] variables, int[] dycks) {
			super(type);
			if (variables == null) {
				variables = Variable.EMPTY_ARRAY;
			}
			if (dycks == null) {
				dycks = ArrayHelper.EMPTY_INT_ARRAY;
			}
			this.variables = variables;
			this.dycks = dycks;
		}
		@Override
		protected Info nodeInfo() {
			return DyckNode.INFOS[this.type];
		}
		@Override
		protected DyckNode newNode(int type) {
			return new DyckNode(type);
		}
		public boolean equalLabel(DyckNode x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			} else if (ArrayHelper.equalArray(this.variables, x.variables)
					&& ArrayHelper.equalArray(this.dycks, x.dycks)) {
				return true;
			}
			return false;
		}
		@Override
		protected Appendable writeInfixLeaf(Appendable output, DyckNode parent)
				throws IOException {
			return this.writeInfixDycks(this.writeInfixVariables(output));
		}
		protected Appendable writeInfixVariables(Appendable output)
				throws IOException {
			switch (this.variables.length) {
			case 0:
				return output;
			case 1:
				return output.append(this.variables[0].name());
			default:
				output.append('(');
				for (int i = 0, n = this.variables.length; i < n; ++i) {
					if (i != 0) {
						output.append(" ");
					}
					output.append(this.variables[i].name());
				}
				return output.append(')');
			}
		}
		protected Appendable writeInfixDycks(Appendable output) throws IOException {
			output.append('[');
			for (int i = 0, n = this.dycks.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(Integer.toString(this.dycks[i]));
			}
			return output.append(']');
		}
		@Override
		public String toString() {
			return this.toInfix();
		}
	}

	static class DyckArrow<V> extends Node<DyckArrow<V>> {
		public static final int ARROW = Node.NUMBER_OF_TYPES;
		protected static final int NUMBER_OF_TYPES = ARROW + 1;

		private static final Info[] INFOS = DyckArrow.newInfo();
		@SuppressWarnings("rawtypes")
		public static final DyckArrow[] EMPTY_ARRAY = {};

		@SuppressWarnings("unchecked")
		public static <T> DyckArrow<T>[] emptyArray() {
			return DyckArrow.EMPTY_ARRAY;
		}

		private static Info[] newInfo() {
			Info[] out = new Info[DyckArrow.NUMBER_OF_TYPES];
			return DyckArrow.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			Node.newInfo(out);
			out[ARROW] = new Info() {
				public String typeName() {
					return "Arrow";
				}
				String name(Object node) {
					@SuppressWarnings("rawtypes")
					DyckArrow x = (DyckArrow) node;
					return x != null ? x.toArrow() : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			return out;
		}

		final V source;
		final V target;
		final DyckNode label;
		// auxiliary field to store miscellaneous information.
		public Object aux;

		DyckArrow(V source, V target, DyckNode label) {
			this(DyckArrow.ARROW, source, target, label);
		}
		DyckArrow(int type, V source, V target, DyckNode label) {
			super(type);
			this.source = source;
			this.target = target;
			this.label = label;
		}
		@Override
		protected Info nodeInfo() {
			return DyckArrow.INFOS[this.type];
		}
		@Override
		final DyckArrow<V> newNode(int type) {
			return this.newNode(type, this.source, this.target, this.label.clone());
		}
		DyckArrow<V> newNode(int type, V source, V target, DyckNode label) {
			return new DyckArrow<V>(type, source, target, label);
		}
		/**
		 * needed at construction.
		 * 
		 * @see Parser#newArrows
		 */
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalArrow((DyckArrow<V>) x);
			} catch (Exception ex) {
			}
			return false;
		}
		protected boolean equalArrow(DyckArrow<V> x) {
			if (x == null) {
				return false;
			} else if (this == x) {
				return true;
			}
			if (this.source.equals(x.source) && this.target.equals(x.target)) {
				if (this.label.equalLabel(x.label)) {
					return true;
				}
			}
			return false;
		}
		public String toArrow() {
			return this.label.toInfix() + ": " + this.source + " -> " + this.target;
		}
		/**
		 * @param rhs
		 * @return cloned both this and rhs.
		 */
		DyckArrow<V> multiplies(DyckArrow<V> rhs) {
			if (this.target.equals(rhs.source)) {
			} else {
				return this.newNode(ZERO);
			}
			switch (this.type()) {
			case ZERO:
				return this.clone();
			case ONE:
				return rhs.clone();
			default:
			break;
			}
			switch (rhs.type()) {
			case ZERO:
				return rhs.clone();
			case ONE:
				return this.clone();
			default:
			break;
			}
			DyckNode label = this.label.multiplies(rhs.label);
			return this.newNode(MULTIPLIES, this.source, rhs.target, label)
					.add(this.clone()).add(rhs.clone());
		}
		/**
		 * @param rhs
		 * @return cloned both this and rhs.
		 */
		DyckArrow<V> plus(DyckArrow<V> rhs) {
			if (this.source.equals(rhs.source) && this.target.equals(rhs.target)) {
			} else {
				return this.newNode(ZERO);
			}
			switch (this.type()) {
			case ZERO:
				return rhs.clone();
			default:
			break;
			}
			switch (rhs.type()) {
			case ZERO:
				return this.clone();
			default:
			break;
			}
			DyckNode label = this.label.plus(rhs.label);
			return this.newNode(PLUS, this.source, rhs.target, label)
					.add(this.clone()).add(rhs.clone());
		}
	}

	@Deprecated
	static class StateNode extends Node<StateNode> {
		public static final int TRANSIENT_STATE = Node.NUMBER_OF_TYPES;
		public static final int START_STATE = TRANSIENT_STATE + 1;
		public static final int END_STATE = START_STATE + 1;
		public static final int START_END_STATE = END_STATE + 1;
		protected static final int NUMBER_OF_TYPES = START_END_STATE + 1;

		private static final Info[] INFOS = StateNode.newInfo();
		public static final StateNode[] EMPTY_ARRAY = {};

		static abstract class StateInfo implements Info {
			String name(Object node) {
				if (node instanceof StateNode) {
					StateNode x = (StateNode) node;
					return x.name();
				}
				return null;
			}
			public String prefixName(Object node) {
				return this.name(node);
			}
			public String infixName(Object node) {
				return this.name(node);
			}
			public int precedence() {
				return Integer.MAX_VALUE;
			}
		}

		private static Info[] newInfo() {
			Info[] out = new Info[StateNode.NUMBER_OF_TYPES];
			return StateNode.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			Node.newInfo(out);
			out[TRANSIENT_STATE] = new StateInfo() {
				public String typeName() {
					return "none";
				}
			};
			out[START_STATE] = new StateInfo() {
				public String typeName() {
					return "start";
				}
			};
			out[END_STATE] = new StateInfo() {
				public String typeName() {
					return "end";
				}
			};
			out[START_END_STATE] = new StateInfo() {
				public String typeName() {
					return "start_end";
				}
			};
			return out;
		}
		static String typeName(int type) {
			return StateNode.INFOS[type].typeName();
		}

		StateNode opposite;
		String name;

		StateNode(int type) {
			this(type, null);
		}
		StateNode(String name) {
			this(StateNode.TRANSIENT_STATE, name);
		}
		StateNode(int type, String name) {
			super(type);
			this.name = name;
		}
		@Override
		protected Info nodeInfo() {
			return StateNode.INFOS[this.type];
		}
		@Override
		StateNode newNode(int type) {
			return new StateNode(type);
		}
		StateNode opposite() {
			return this.opposite;
		}
		StateNode makeOpposite(StateNode x) {
			this.opposite = x;
			x.opposite = this;
			return this;
		}
		String name() {
			return this.name;
		}
		@Override
		public String toString() {
			return this.name;
		}
	}

	@SuppressWarnings("serial")
	static class ParserException extends Exception {
		public ParserException(String msg) {
			super(msg);
		}
		public ParserException(Throwable cause) {
			super(cause);
		}
	}

	static class FlattenEauation {
		static final FlattenEauation ZERO = newZero();
		static final FlattenEauation ONE = newOne();

		static FlattenEauation newZero() {
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.emptyList();
			return out;
		}
		static FlattenEauation newOne() {
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.singletonList(Collections.<Variable> emptyList());
			return out;
		}
		static FlattenEauation newVariable(Variable x) {
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.singletonList(Collections.singletonList(x));
			return out;
		}

		static Variable[] array(List<Variable> vars, int begin, int end) {
			if (end < begin) {
				String msg = "invalid range=[" + begin + ", " + end + ")";
				throw new IllegalArgumentException(msg);
			} else if (begin == end) {
				return Variable.EMPTY_ARRAY;
			}
			Variable[] output = new Variable[end - begin];
			return array(output, 0, vars, begin, end);
		}
		static Variable[] array(Variable[] output, List<Variable> vars, int begin,
				int end) {
			return array(output, 0, vars, begin, end);
		}
		static Variable[] array(Variable[] output, int offset, List<Variable> vars,
				int begin, int end) {
			if (end < begin) {
				String msg = "invalid range=[" + begin + ", " + end + ")";
				throw new IllegalArgumentException(msg);
			}
			for (int i = 0, n = end - begin; i < n; ++i) {
				output[offset + i] = vars.get(begin + i);
			}
			return output;
		}
		static GrammarNode product(List<Variable> vars, int begin, int end) {
			if (end < begin) {
				String msg = "invalid range=[" + begin + ", " + end + ")";
				throw new IllegalArgumentException(msg);
			} else if (begin == end) {
				return new GrammarNode(GrammarNode.ONE);
			}
			GrammarNode X = newGrammarNode(vars.get(begin));
			for (++begin; begin < end; ++begin) {
				GrammarNode Y = newGrammarNode(vars.get(begin));
				X = new GrammarNode(GrammarNode.MULTIPLIES).add(X).add(Y);
			}
			return X;
		}
		static GrammarNode newGrammarNode(Variable x) {
			return new GrammarNode(GrammarNode.VARIABLE, x);
		}

		List<List<Variable>> terms;

		boolean isZero() {
			return this.terms.size() < 1;
		}
		boolean isOne() {
			return this.terms.size() == 1 && this.terms.get(0).size() == 0;
		}
		FlattenEauation plus(FlattenEauation x) {
			if (this.isZero()) {
				return x;
			} else if (x.isZero()) {
				return this;
			}
			FlattenEauation out = new FlattenEauation();
			int n = this.terms.size();
			int xn = x.terms.size();
			out.terms = new ArrayList<List<Variable>>(n + xn);
			out.terms.addAll(this.terms);
			out.terms.addAll(x.terms);
			return out;
		}
		FlattenEauation multiplies(FlattenEauation x) {
			if (this.isZero()) {
				return this;
			} else if (x.isZero()) {
				return x;
			} else if (this.isOne()) {
				return x;
			} else if (x.isOne()) {
				return this;
			}
			FlattenEauation out = new FlattenEauation();
			int n = this.terms.size();
			int xn = x.terms.size();
			out.terms = new ArrayList<List<Variable>>(n * xn);
			for (int i = 0; i < n; ++i) {
				List<Variable> pre = this.terms.get(i);
				for (int xi = 0; xi < xn; ++xi) {
					ArrayList<Variable> term = new ArrayList<Variable>();
					term.addAll(pre);
					term.addAll(x.terms.get(xi));
					out.terms.add(term);
				}
			}
			return out;
		}
	}

	/**
	 * essentially string pair.
	 */
	static class VariablePair implements Cloneable {
		Variable x0;
		Variable x1;

		VariablePair() {
			this(null, null);
		}
		VariablePair(Variable x0, Variable x1) {
			this.x0 = x0;
			this.x1 = x1;
		}
		@Override
		public int hashCode() {
			return this.x0.hashCode() * 3 + this.x1.hashCode();
		}
		@Override
		public boolean equals(Object x) {
			try {
				VariablePair y = (VariablePair) x;
				return this.x0.equals(y.x0) && this.x1.equals(y.x1);
			} catch (Exception ex) {
			}
			return false;
		}
		VariablePair set(Variable x0, Variable x1) {
			this.x0 = x0;
			this.x1 = x1;
			return this;
		}
		@Override
		public VariablePair clone() {
			try {
				return (VariablePair) super.clone();
			} catch (CloneNotSupportedException ex) {
				ex.printStackTrace();
			}
			return new VariablePair(this.x0, this.x1);
		}
		@Override
		public String toString() {
			return "(" + this.x0 + ", " + this.x1 + ")";
		}
	}

	static class Counter {
		final int index;
		int count;

		Counter(int index) {
			this.index = index;
		}
		@Override
		public String toString() {
			return "(" + Integer.toString(this.index) + ", "
					+ Integer.toString(this.count) + ")";
		}
	}

	static class Parser {
		Map<String, Variable> variableMap;
		int arrowCount;

		Parser initializeParse() {
			this.variableMap = null;
			return this;
		}
		Map<String, Variable> variableMap(boolean anyway) {
			if (this.variableMap == null) {
				this.variableMap = new HashMap<String, Variable>();
			}
			return this.variableMap;
		}

		DyckArrow<Variable>[] parse(GrammarNode nodes) throws ParserException {
			this.initializeParse();
			return this.newArrows(nodes);
		}
		DyckArrow<Variable>[] newArrows(GrammarNode nodes) throws ParserException {
			GrammarNode node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				this.fixBoundedVariables(node);
			}
			List<DyckArrow<Variable>> xs = new ArrayList<DyckArrow<Variable>>();
			node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				FlattenEauation eqs = this.flattenEquation(node);
				this.newArrows(xs, node, eqs);
			}
			return xs.toArray(DyckArrow.<Variable> emptyArray());
		}
		private List<DyckArrow<Variable>> newArrows(
				List<DyckArrow<Variable>> output, GrammarNode node, FlattenEauation eqs) {
			final GrammarNode lhs = node.firstNode();
			final Variable variable = this.variable(this.variableName(lhs), true);
			final List<List<Variable>> terms = eqs.terms;
			final VariablePair pair = new VariablePair();
			final Map<VariablePair, Counter> pairs = new HashMap<VariablePair, Counter>();

			for (int i = 0, n = terms.size(); i < n; ++i) {
				List<Variable> term = terms.get(i);
				Variable x = variable;
				int ix = -1;
				int lastIndex = -1;
				for (int iy = 0, ny = term.size(); iy < ny; ++iy) {
					Variable y = term.get(iy);
					if (y.isBounded()) {
						Counter memo = pairs.get(pair.set(x, y));
						{
							if (memo == null) {
								memo = new Counter(this.arrowCount++);
								pairs.put(pair.clone(), memo);
							}
							memo.count += 1;
						}
						{
							Variable[] eq = FlattenEauation.array(term, ix + 1, iy + 1);
							Variable source = x;
							int[] dycks = ArrayHelper.EMPTY_INT_ARRAY;
							if (0 <= lastIndex) {
								source = x.opposite();
								dycks = new int[] { -(lastIndex + 1), memo.index + 1 };
							} else {
								dycks = new int[] { memo.index + 1 };
							}
							DyckNode dyck = new DyckNode(eq, dycks);
							DyckArrow<Variable> arrow = new DyckArrow<Variable>(source, y,
									dyck);
							if (output.contains(arrow)) {
								Debug.log().debug("duplicated arrow=" + arrow);
							} else {
								output.add(arrow);
							}
						}
						{
							ix = iy;
							x = y;
							lastIndex = memo.index;
						}
					}
				}
				{
					Variable[] eq = new Variable[term.size() - ix - 1 + 1];
					FlattenEauation.array(eq, term, ix + 1, term.size());
					eq[eq.length - 1] = variable.opposite();
					int[] dycks = ArrayHelper.EMPTY_INT_ARRAY;
					Variable source = x;
					if (0 <= lastIndex) {
						source = x.opposite();
						dycks = new int[] { -(lastIndex + 1) };
					} else {
						source = variable;
					}
					DyckNode dyck = new DyckNode(eq, dycks);
					Variable target = variable.opposite();
					DyckArrow<Variable> arrow = new DyckArrow<Variable>(source, target,
							dyck);
					if (output.contains(arrow)) {
						Debug.log().debug("duplicated arrow=" + arrow);
					} else {
						output.add(arrow);
					}
				}
			}
			return output;
		}
		private FlattenEauation flattenEquation(GrammarNode node)
				throws ParserException {
			return this.flattenRhs(node.lastNode());
		}
		private FlattenEauation flattenRhs(GrammarNode node) throws ParserException {
			switch (node.type()) {
			case GrammarNode.ZERO:
				return FlattenEauation.ZERO;
			case GrammarNode.ONE:
				return FlattenEauation.ONE;
			case GrammarNode.VARIABLE: {
				Variable x = this.variable(this.variableName(node), true);
				return FlattenEauation.newVariable(x);
			}
			case GrammarNode.MULTIPLIES: {
				GrammarNode child = node.firstNode();
				if (child == null) {
					String msg = "multiplies must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenRhs(child);
					x = x.multiplies(y);
				}
				return x;
			}
			case GrammarNode.PLUS: {
				GrammarNode child = node.firstNode();
				if (child == null) {
					String msg = "plus must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenRhs(child);
					x = x.plus(y);
				}
				return x;
			}
			default: {
				String msg = "unexpected type of node=" + node.typeName();
				throw new ParserException(msg);
			}
			}
		}
		private void fixBoundedVariables(GrammarNode node) throws ParserException {
			if (!node.isBinary(GrammarNode.ASSIGNS)) {
				String msg = Messages.getUnexpectedValue(
						"root of equation must be assigns",
						Node.typeName(GrammarNode.ASSIGNS), node.typeName());
				throw new ParserException(msg);
			}
			GrammarNode lhs = node.firstNode();
			if (!lhs.isLeaf(GrammarNode.VARIABLE)) {
				String msg = Messages.getUnexpectedValue("lhs of assigns",
						Node.typeName(GrammarNode.VARIABLE), lhs.typeName());
				throw new ParserException(msg);
			}

			String name = this.variableName(lhs);
			Variable start = this.variable(name, true);
			Variable end = this.variable(name + "$", true);
			start.type = Variable.BOUNDED_START;
			end.type = Variable.BOUNDED_END;
			start.makeOpposite(end);
		}
		/**
		 * naming convention.
		 * 
		 * @param node
		 * @return
		 */
		String variableName(GrammarNode node) {
			return node.infixName();
		}
		Variable variable(String name, boolean anyway) {
			Map<String, Variable> map = this.variableMap(anyway);
			if (name == null && map == null) {
				return null;
			}
			Variable x = map.get(name);
			if (x == null && anyway) {
				x = new Variable(name);
				map.put(name, x);
			}
			return x;
		}
	}

	public void test_newArrows() throws ParserException {
		if (true) {
			GrammarNode zero = new GrammarNode(GrammarNode.ZERO);
			GrammarNode one = new GrammarNode(GrammarNode.ONE);
			GrammarNode A = new GrammarNode("A");
			GrammarNode a = new GrammarNode("a");
			GrammarNode eqA = A.assigns( //
					a.plus(one).plus(zero) //
					);
			GrammarNode eqs = new GrammarNode(GrammarNode.EQUATIONS);
			eqs.add(eqA);
			Debug.log().debug("---- " + eqs.toInfix());

			Parser parser = new Parser();
			DyckArrow<Variable>[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<Variable> arrow = arrows[i];
				Debug.log().debug(arrow);
			}
		}
		if (true) {
			GrammarNode one = new GrammarNode(GrammarNode.ONE);
			GrammarNode a = new GrammarNode("a");
			GrammarNode b = new GrammarNode("b");
			GrammarNode c = new GrammarNode("c");
			GrammarNode S = new GrammarNode("S");
			GrammarNode A = new GrammarNode("A");
			GrammarNode eqS = S.assigns( //
					a.multiplies(S).multiplies(A).plus(one) //
					);
			GrammarNode eqA = A.assigns( //
					a.multiplies(b).multiplies(S).plus(c) //
					);
			GrammarNode eqs = new GrammarNode(GrammarNode.EQUATIONS);
			eqs.add(eqS).add(eqA);
			Debug.log().debug("---- " + eqs.toInfix());

			Parser parser = new Parser();
			DyckArrow<Variable>[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<Variable> arrow = arrows[i];
				Debug.log().debug(arrow);
			}
		}
		if (true) {
			// GrammarNode one = new GrammarNode(GrammarNode.ONE);
			GrammarNode plus = new GrammarNode("plus");
			GrammarNode minus = new GrammarNode("minus");
			GrammarNode mult = new GrammarNode("times");
			GrammarNode var = new GrammarNode("variable");
			GrammarNode num = new GrammarNode("number");
			GrammarNode bra = new GrammarNode("bra");
			GrammarNode ket = new GrammarNode("ket");

			GrammarNode Eq = new GrammarNode("Eq");
			GrammarNode Binary = new GrammarNode("Binary");
			GrammarNode Unary = new GrammarNode("Unary");
			GrammarNode Primary = new GrammarNode("Primary");

			GrammarNode AdditiveInfix = new GrammarNode("AdditiveInfix");
			GrammarNode MultitiveInfix = new GrammarNode("MultitiveInfix");
			GrammarNode Prefix = new GrammarNode("Prefix");
			GrammarNode Primitive = new GrammarNode("Primitive");
			GrammarNode Unit = new GrammarNode("Unit");

			GrammarNode eqEq = Eq.assigns(Binary);
			GrammarNode eqBinary = Binary.assigns( //
					Binary.multiplies(MultitiveInfix).multiplies(Unary).plus( //
							Binary.multiplies(AdditiveInfix).multiplies(Unary).plus( //
									Unary //
									) //
							) //
					);
			GrammarNode eqUnary = Unary.assigns( //
					Prefix.multiplies(Unary).plus( //
							Primary //
							) //
					);
			GrammarNode eqPrimary = Primary.assigns( //
					bra.multiplies(Eq).multiplies(ket).plus(Primitive) //
					);
			GrammarNode eqPrimitive = Primitive.assigns( //
					Unit.plus(var).plus(num) //
					);
			GrammarNode eqUnit = Unit.assigns( //
					bra.multiplies(ket) //
					);
			GrammarNode eqMultitiveInfix = MultitiveInfix.assigns( //
					mult //
					);
			GrammarNode eqAdditiveInfix = AdditiveInfix.assigns( //
					plus.plus(minus) //
					);
			GrammarNode eqPrefix = Prefix.assigns( //
					plus.plus(minus).plus(num) //
					);
			GrammarNode eqs = new GrammarNode(GrammarNode.EQUATIONS);
			eqs.add(eqEq).add(eqBinary).add(eqUnary).add(eqPrimary).add(eqPrimitive)
					.add(eqUnit).add(eqMultitiveInfix).add(eqAdditiveInfix).add(eqPrefix);
			Debug.log().debug("---- " + eqs.toInfix());

			Parser parser = new Parser();
			DyckArrow<Variable>[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<Variable> arrow = arrows[i];
				Debug.log().debug(arrow);
			}
		}
	}

	static class LL {
		static <V> Map<V, List<DyckArrow<V>>> newGraph(DyckArrow<V>[] arrows) {
			Map<V, List<DyckArrow<V>>> out = new HashMap<V, List<DyckArrow<V>>>();
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<V> arrow = arrows[i];
				List<DyckArrow<V>> targets = out.get(arrow.source);
				if (targets == null) {
					targets = new ArrayList<DyckArrow<V>>();
					out.put(arrow.source, targets);
				}
				targets.add(arrow);
			}
			return out;
		}

		static class ArrowTraverser<V> {
			final Map<V, List<DyckArrow<V>>> adjacentMap;
			DyckArrow<V> arrow;
			Map<DyckArrow<V>, Boolean> visitMap;
			List<List<DyckArrow<V>>> nextsStack;

			ArrowTraverser(Map<V, List<DyckArrow<V>>> adjacentMap) {
				this.adjacentMap = adjacentMap;
			}
			DyckArrow<V> get() {
				return this.arrow;
			}
			ArrowTraverser<V> set(DyckArrow<V> arrow) {
				this.initialize();
				if (arrow == null) {
					this.arrow = arrow;
					return this;
				}
				return this.visit(arrow);
			}
			protected ArrowTraverser<V> initialize() {
				this.visitMap = null;
				this.nextsStack = null;
				return this;
			}
			Map<DyckArrow<V>, Boolean> visitMap(boolean anyway) {
				if (this.visitMap == null && anyway) {
					this.visitMap = new HashMap<DyckArrow<V>, Boolean>();
				}
				return this.visitMap;
			}
			List<List<DyckArrow<V>>> nextsStack(boolean anyway) {
				if (this.nextsStack == null && anyway) {
					this.nextsStack = new ArrayList<List<DyckArrow<V>>>();
				}
				return this.nextsStack;
			}
			List<DyckArrow<V>> nexts(DyckArrow<V> arrow) {
				List<DyckArrow<V>> out = this.adjacentMap.get(arrow.target);
				if (out == null) {
					String msg = "could not find nexts of arrow=" + arrow;
					throw new IllegalArgumentException(msg);
				}
				return out;
			}
			public ArrowTraverser<V> next() {
				if (this.arrow == null) {
					return this;
				}
				List<DyckArrow<V>> nexts = this.nexts(this.arrow);
				DyckArrow<V> next = this.findUnvisited(nexts);
				if (next != null) {
					return this.visit(next);
				}
				return this.skip();
			}
			public ArrowTraverser<V> skip() {
				final List<List<DyckArrow<V>>> stack = this.nextsStack(false);
				if (this.arrow == null || stack == null) {
					return this;
				}
				this.doneVisit();
				this.popStack();
				while (0 < stack.size()) {
					List<DyckArrow<V>> nexts = stack.get(stack.size() - 1);
					DyckArrow<V> next = this.findUnvisited(nexts);
					if (next != null) {
						return this.visit(next);
					}
					this.popStack();
				}
				this.arrow = null;
				return this;
			}
			protected ArrowTraverser<V> pushStack(DyckArrow<V> arrow) {
				this.nextsStack(true).add(this.nexts(arrow));
				return this;
			}
			protected ArrowTraverser<V> popStack() {
				List<List<DyckArrow<V>>> stack = this.nextsStack(false);
				if (stack != null && 0 < stack.size()) {
					stack.remove(stack.size() - 1);
				}
				return this;
			}
			protected ArrowTraverser<V> doneVisit() {
				if (this.arrow != null) {
					this.visitMap(true).put(this.arrow, Boolean.TRUE);
				}
				return this;
			}
			protected ArrowTraverser<V> visit(DyckArrow<V> arrow) {
				this.visitMap(true).put(arrow, Boolean.FALSE);
				this.pushStack(arrow);
				this.arrow = arrow;
				return this;
			}
			protected DyckArrow<V> findUnvisited(List<DyckArrow<V>> arrows) {
				Map<DyckArrow<V>, Boolean> visited = this.visitMap(true);
				for (int i = 0, n = arrows.size(); i < n; ++i) {
					DyckArrow<V> arrow = arrows.get(i);
					if (visited.get(arrow) == null) {
						return arrow;
					}
				}
				return null;
			}
		}

		static class PathTraverser<V> extends ArrowTraverser<V> {
			private List<DyckArrow<V>> pathStack;

			PathTraverser(Map<V, List<DyckArrow<V>>> adjacentMap) {
				super(adjacentMap);
			}
			@Override
			protected PathTraverser<V> pushStack(DyckArrow<V> arrow) {
				if (this.pathStack(true).contains(arrow)) {
					throw new Error("here");
				}
				super.pushStack(arrow);
				this.pathStack(true).add(arrow);
				return this;
			}
			@Override
			protected PathTraverser<V> popStack() {
				super.popStack();
				List<DyckArrow<V>> stack = this.pathStack(false);
				if (stack != null && 0 < stack.size()) {
					stack.remove(stack.size() - 1);
				}
				return this;
			}
			private List<DyckArrow<V>> pathStack(boolean anyway) {
				if (this.pathStack == null && anyway) {
					this.pathStack = new ArrayList<DyckArrow<V>>();
				}
				return this.pathStack;
			}
			protected PathTraverser<V> initialize() {
				super.initialize();
				this.pathStack = null;
				return this;
			}
			public List<DyckArrow<V>> path() {
				return this.pathStack;
			}
		}

		static class TransitionTraverser extends PathTraverser<Variable> {
			List<Transition> transitions;

			TransitionTraverser(Map<Variable, List<DyckArrow<Variable>>> adjacentMap) {
				super(adjacentMap);
			}
			List<Transition> transitions(boolean anyway) {
				if (this.transitions == null && anyway) {
					this.transitions = new ArrayList<Transition>();
				}
				return this.transitions;
			}
			@Override
			protected TransitionTraverser pushStack(DyckArrow<Variable> arrow) {
				super.pushStack(arrow);
				if (arrow == null) {
					throw new Error("here");
				} else if (LL.emptyTransition(arrow)) {
					int ind = LL.firstSource(this.path(), arrow.target);
					if (0 <= ind) {
						Transition x = this.newTransition(true);
						this.transitions(true).add(x);
					}
				} else {
					Transition x = this.newTransition(false);
					this.transitions(true).add(x);
					this.skip();
				}
				return this;
			}
			private Transition newTransition(boolean emptyLoop) {
				List<DyckArrow<Variable>> path = this.path().subList(0,
						this.path().size());
				return new Transition(path.toArray(DyckArrow.<Variable> emptyArray()),
						emptyLoop);
			}
		}

		public static class Transition {
			DyckArrow<Variable>[] path;
			boolean emptyLoop;

			Transition(DyckArrow<Variable>[] path) {
				this(path, false);
			}
			Transition(DyckArrow<Variable>[] path, boolean emptyLoop) {
				this.path = path;
				this.emptyLoop = emptyLoop;
			}
			@Override
			public String toString() {
				return (this.emptyLoop ? "empty-loop " : "non-empty ")
						+ StringHelper.join(this.path, ", ");
			}
		}

		static <V> ArrowTraverser<V> traverser(DyckArrow<V>[] arrows) {
			return new ArrowTraverser<V>(LL.newGraph(arrows));
		}
		static <V> PathTraverser<V> pathTravserser(DyckArrow<V>[] arrows) {
			return new PathTraverser<V>(LL.newGraph(arrows));
		}
		static TransitionTraverser transitionTravserser(DyckArrow<Variable>[] arrows) {
			return new TransitionTraverser(LL.newGraph(arrows));
		}

		public static boolean emptyTransition(DyckArrow<Variable> arrow) {
			Variable[] vars = arrow.label.variables;
			for (int i = 0, n = vars.length; i < n; ++i) {
				if (vars[i].isType(Variable.FREE)) {
					return false;
				}
			}
			return true;
		}
		static <V> int firstSource(List<DyckArrow<Variable>> arrows, V vertex) {
			for (int i = 0, n = arrows.size(); i < n; ++i) {
				if (ObjectHelper.equals(arrows.get(i).source, vertex)) {
					return i;
				}
			}
			return -1;
		}
		static <V> int firstTarget(List<DyckArrow<Variable>> arrows, V vertex) {
			for (int i = 0, n = arrows.size(); i < n; ++i) {
				if (ObjectHelper.equals(arrows.get(i).target, vertex)) {
					return i;
				}
			}
			return -1;
		}
	}

	public void testLL() throws ParserException {
		if (true) {
			GrammarNode one = new GrammarNode(GrammarNode.ONE);
			GrammarNode a = new GrammarNode("a");
			GrammarNode b = new GrammarNode("b");
			GrammarNode c = new GrammarNode("c");
			GrammarNode S = new GrammarNode("S");
			GrammarNode A = new GrammarNode("A");
			GrammarNode eqS = S.assigns( //
					a.multiplies(S).multiplies(A).plus(one) //
					);
			GrammarNode eqA = A.assigns( //
					a.multiplies(b).multiplies(S).plus(c) //
					);
			GrammarNode eqs = new GrammarNode(GrammarNode.EQUATIONS);
			eqs.add(eqS).add(eqA);
			Debug.log().debug("---- " + eqs.toInfix());

			Parser parser = new Parser();
			DyckArrow<Variable>[] arrows = parser.newArrows(eqs);

			// Map<Variable, List<DyckArrow<Variable>>> graph = LL.newGraph(arrows);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<Variable> arrow = arrows[i];
				if (arrow.source.isType(Variable.BOUNDED_START)
						&& LL.emptyTransition(arrow)) {
					Debug.log().debug("empty transition " + arrow);
					TransitionTraverser p = LL.transitionTravserser(arrows);
					p.set(arrow);
					for (p.next(); p.get() != null; p.next()) {
					}
					List<Transition> xs = p.transitions(true);
					for (int ix = 0, nx = xs.size(); ix < nx; ++ix) {
						Debug.log().debug(xs.get(ix));
					}
				}
			}
		}
		if (true) {
			// GrammarNode one = new GrammarNode(GrammarNode.ONE);
			GrammarNode plus = new GrammarNode("plus");
			GrammarNode minus = new GrammarNode("minus");
			GrammarNode mult = new GrammarNode("times");
			GrammarNode var = new GrammarNode("variable");
			GrammarNode num = new GrammarNode("number");
			GrammarNode bra = new GrammarNode("bra");
			GrammarNode ket = new GrammarNode("ket");

			GrammarNode Eq = new GrammarNode("Eq");
			GrammarNode Binary = new GrammarNode("Binary");
			GrammarNode Unary = new GrammarNode("Unary");
			GrammarNode Primary = new GrammarNode("Primary");

			GrammarNode AdditiveInfix = new GrammarNode("AdditiveInfix");
			GrammarNode MultitiveInfix = new GrammarNode("MultitiveInfix");
			GrammarNode Prefix = new GrammarNode("Prefix");
			GrammarNode Primitive = new GrammarNode("Primitive");
			GrammarNode Unit = new GrammarNode("Unit");

			GrammarNode eqEq = Eq.assigns(Binary);
			GrammarNode eqBinary = Binary.assigns( //
					Binary.multiplies(MultitiveInfix).multiplies(Unary).plus( //
							Binary.multiplies(AdditiveInfix).multiplies(Unary).plus( //
									Unary //
									) //
							) //
					);
			GrammarNode eqUnary = Unary.assigns( //
					Prefix.multiplies(Unary).plus( //
							Primary //
							) //
					);
			GrammarNode eqPrimary = Primary.assigns( //
					bra.multiplies(Eq).multiplies(ket).plus(Primitive) //
					);
			GrammarNode eqPrimitive = Primitive.assigns( //
					Unit.plus(var).plus(num) //
					);
			GrammarNode eqUnit = Unit.assigns( //
					bra.multiplies(ket) //
					);
			GrammarNode eqMultitiveInfix = MultitiveInfix.assigns( //
					mult //
					);
			GrammarNode eqAdditiveInfix = AdditiveInfix.assigns( //
					plus.plus(minus) //
					);
			GrammarNode eqPrefix = Prefix.assigns( //
					plus.plus(minus).plus(num) //
					);
			GrammarNode eqs = new GrammarNode(GrammarNode.EQUATIONS);
			eqs.add(eqEq).add(eqBinary).add(eqUnary).add(eqPrimary).add(eqPrimitive)
					.add(eqUnit).add(eqMultitiveInfix).add(eqAdditiveInfix).add(eqPrefix);
			Debug.log().debug("---- " + eqs.toInfix());

			Parser parser = new Parser();
			DyckArrow<Variable>[] arrows = parser.newArrows(eqs);

			// Map<Variable, List<DyckArrow<Variable>>> graph = LL.newGraph(arrows);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				DyckArrow<Variable> arrow = arrows[i];
				if (arrow.source.isType(Variable.BOUNDED_START)
						&& LL.emptyTransition(arrow)) {
					Debug.log().debug("empty transition " + arrow);
					TransitionTraverser p = LL.transitionTravserser(arrows);
					p.set(arrow);
					for (p.next(); p.get() != null; p.next()) {
					}
					List<Transition> xs = p.transitions(true);
					for (int ix = 0, nx = xs.size(); ix < nx; ++ix) {
						Debug.log().debug(xs.get(ix));
					}
				}
			}
		}
	}
}

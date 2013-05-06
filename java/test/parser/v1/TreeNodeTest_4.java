package parser.v1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;

public class TreeNodeTest_4 extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	/**
	 * represents semiring structure.
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
		public static final GrammarNode[] EMPTY_ARRAY = {};

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
					try {
						GrammarNode x = (GrammarNode) node;
						return x.value;
					} catch (Exception ex) {
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
			};
			return out;
		}
		static String typeName(int type) {
			return GrammarNode.INFOS[type].typeName();
		}

		String value;

		GrammarNode(int type) {
			this(type, null);
		}
		GrammarNode(String name) {
			this(GrammarNode.VARIABLE, name);
		}
		GrammarNode(int type, String value) {
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

	static class Variable implements Comparable<Variable> {
		public static final Variable[] EMPTY_ARRAY = {};
		static final int FREE = 0;
		static final int BOUNDED_START = FREE + 1;
		static final int BOUNDED_END = BOUNDED_START + 1;
		static final int NUMBER_OF_TYPES = BOUNDED_END + 1;

		static interface Info {
			String typeName();
		}

		static final Info[] INFOS = Variable.newInfos();

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

		/**
		 * assumes {@link Variable} is unique instance for {@link Variable#name}.
		 * 
		 * @param xs
		 * @param ys
		 * @return
		 */
		public static boolean equalArray(Variable[] xs, Variable[] ys) {
			if (xs == ys) {
				return true;
			} else if (xs == null || ys == null) {
				return false;
			} else if (xs.length != ys.length) {
				return false;
			}
			for (int i = 0, n = xs.length; i < n; ++i) {
				if (xs[i] != ys[i]) {
					return false;
				}
			}
			return true;
		}

		final String name;
		final int index;
		int type;
		Variable opposite;

		Variable(String name, int index) {
			this(name, index, FREE);
		}
		Variable(String name, int index, int type) {
			if (name == null) {
				String msg = Messages.getUnexpectedValue("name", "non-null", "null");
				throw new IllegalArgumentException(msg);
			}
			this.name = name;
			this.index = index;
			this.type = type;
		}
		/*
		 * must be unique instance.
		 * 
		 * @Override public boolean equals(Object x) { try { return
		 * this.equalVariable((Variable) x); } catch (Exception ex) { } return
		 * false; }
		 */
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
		/**
		 * compares by index not but name.
		 */
		@Override
		public int compareTo(Variable x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			} else if (this.index < x.index) {
				return -1;
			} else if (this.index == x.index) {
				return 0;
			}
			return 1;
		}
	}

	/**
	 * represents direct product of free monoid on {@link Variable} and Dyck
	 * monoid.
	 * 
	 */
	static class Label {
		public static final Label[] EMPTY_ARRAY = {};

		public static int countFree(Variable[] variables) {
			if (variables == null) {
				return 0;
			}
			int count = 0;
			for (int i = 0, n = variables.length; i < n; ++i) {
				Variable x = variables[i];
				if (x != null && x.isType(Variable.FREE)) {
					++count;
				}
			}
			return count;
		}
		/**
		 * @param xs
		 *          assuming normal ordered.
		 * @param ys
		 *          assuming normal ordered.
		 * @return the number of contractions.
		 */
		static int countContranction(int[] xs, int[] ys) {
			if (xs == null || ys == null) {
				return -1;
			} else if (xs.length == 0 || ys.length == 0) {
				return 0;
			}
			int n = xs.length;
			if (ys.length < n) {
				n = ys.length;
			}
			for (int i = 0; i < n; ++i) {
				int x = xs[xs.length - i - 1];
				int y = ys[i];
				if (y < 0 && 0 < x) {
					if (x != -y) {
						return -1;
					}
				} else {
					return i;
				}
			}
			return n;
		}

		final Variable[] variables;
		final int[] dycks;
		int freeCount;

		Label(Variable[] variables) {
			this(variables, null);
		}
		Label(Variable[] variables, int[] dycks) {
			if (variables == null) {
				variables = Variable.EMPTY_ARRAY;
			}
			if (dycks == null) {
				dycks = ArrayHelper.EMPTY_INT_ARRAY;
			}
			this.variables = variables;
			this.dycks = dycks;
			this.freeCount = -1;
		}
		/**
		 * @return the number of free variables that this object holds. returning 0
		 *         means this object represents empty transition.
		 */
		int freeVariableSize() {
			if (this.freeCount < 0) {
				this.freeCount = countFree(this.variables);
			}
			return this.freeCount;
		}
		int boundedVariableSize() {
			return this.variables.length - this.freeVariableSize();
		}
		public boolean equalLabel(Label x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			} else if (Variable.equalArray(this.variables, x.variables)
					&& ArrayHelper.equalArray(this.dycks, x.dycks)) {
				return true;
			}
			return false;
		}
		/**
		 * indicates whether the result of {@link #multiplies} is not null. this
		 * method may be used for reducing the cost that {@link #multiplies}
		 * allocate memory.
		 * 
		 * @param x
		 * @return
		 */
		public boolean multiplyable(Label x) {
			if (x == null) {
				return false;
			} else if (this.dycks.length == 0 || x.dycks.length == 0) {
				return true;
			}
			int cn = Label.countContranction(this.dycks, x.dycks);
			if (cn < 0) {
				return false;
			}
			return true;
		}
		/**
		 * 
		 * @param x
		 * @return null represents zero.
		 */
		public Label multiplies(Label x) {
			int[] dycks = this.multipliesDyck(x.dycks);
			if (dycks == null) {
				return null;
			}
			Variable[] vars = this.multipliesVariable(x.variables);
			return this.newLabel(vars, dycks);
		}
		protected Label newLabel(Variable[] vars, int[] dycks) {
			return new Label(vars, dycks);
		}
		private int[] multipliesDyck(int[] dycks) {
			if (this.dycks.length == 0) {
				if (dycks.length == 0) {
					return ArrayHelper.EMPTY_INT_ARRAY;
				}
				return dycks;
			} else if (dycks.length == 0) {
				return this.dycks;
			}
			int cn = Label.countContranction(this.dycks, dycks);
			if (cn < 0) {
				return null;
			} else if (0 < cn) {
				if (this.dycks.length == cn) {
					if (dycks.length == cn) {
						return ArrayHelper.EMPTY_INT_ARRAY;
					}
					return dycks;
				} else if (dycks.length == cn) {
					return this.dycks;
				}
			}
			int xn = this.dycks.length - cn + dycks.length - cn;
			int[] xs = new int[xn];
			xn = this.dycks.length - cn;
			System.arraycopy(this.dycks, 0, xs, 0, xn);
			System.arraycopy(dycks, cn, xs, xn, dycks.length - cn);
			return xs;
		}
		private Variable[] multipliesVariable(Variable[] variables) {
			if (this.variables.length == 0) {
				if (variables.length == 0) {
					return Variable.EMPTY_ARRAY;
				}
				return variables;
			} else if (variables.length == 0) {
				return this.variables;
			}
			int n = this.variables.length + variables.length;
			Variable[] xs = new Variable[n];
			n = this.variables.length;
			System.arraycopy(this.variables, 0, xs, 0, n);
			System.arraycopy(variables, 0, xs, n, variables.length);
			return xs;
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			return this.toStringDycks(this.toStringVariables(output));
		}
		protected Appendable toStringVariables(Appendable output)
				throws IOException {
			switch (this.variables.length) {
			case 0:
				return output;
			case 1:
				return output.append(this.variables[0].name());
			default:
				// output.append('(');
				for (int i = 0, n = this.variables.length; i < n; ++i) {
					if (i != 0) {
						output.append(" ");
					}
					output.append(this.variables[i].name());
				}
				return output;
				// return output.append(')');
			}
		}
		protected Appendable toStringDycks(Appendable output) throws IOException {
			output.append('[');
			for (int i = 0, n = this.dycks.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(Integer.toString(this.dycks[i]));
			}
			return output.append(']');
		}
	}

	static class LabeledArrow {
		public static final LabeledArrow[] EMPTY_ARRAY = {};

		final Variable source;
		final Variable target;
		final Label label;

		LabeledArrow(Variable source, Variable target, Label label) {
			if (source == null) {
				String msg = "source must not be null";
				throw new IllegalArgumentException(msg);
			} else if (target == null) {
				String msg = "target must not be null";
				throw new IllegalArgumentException(msg);
			} else if (label == null) {
				String msg = "label must not be null";
				throw new IllegalArgumentException(msg);
			}
			this.source = source;
			this.target = target;
			this.label = label;
		}
		/**
		 * needed at construction.
		 * 
		 * @see Parser#newArrows
		 */
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalArrow((LabeledArrow) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalArrow(LabeledArrow x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			} else if (this.source != x.source || this.target != x.target) {
				return false;
			}
			return this.label.equalLabel(x.label);
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			return this.label.toString(output).append(": ").append(this.source.name)
					.append(" -> ").append(this.target.name);
		}
		public boolean multiplyable(LabeledArrow x) {
			if (x == null) {
				return false;
			} else if (this.target != x.source) {
				return false;
			}
			return this.label.multiplyable(x.label);
		}
		public LabeledArrow multiplies(LabeledArrow x) {
			if (x == null) {
				return null;
			} else if (this.target != x.source) {
				return null;
			}
			Label label = this.label.multiplies(x.label);
			return this.newArrow(this.source, x.target, label);
		}
		protected LabeledArrow newArrow(Variable source, Variable target,
				Label label) {
			return new LabeledArrow(source, target, label);
		}
	}

	static class LabeledGraph {
		final LabeledArrow[] arrows;
		final Variable[] variables;
		private Map<Variable, List<LabeledArrow>> adjacentMap;

		LabeledGraph(LabeledArrow[] arrows, Variable[] variables) {
			this.arrows = arrows;
			this.variables = variables;
		}
		List<LabeledArrow> adjacentList(Variable x) {
			if (x == null) {
				return null;
			}
			List<LabeledArrow> list = this.adjacentMap(true).get(x);
			if (list == null) {
				return Collections.emptyList();
			}
			return list;
		}
		Map<Variable, List<Label>> adjacentMap(Variable x) {
			if (x == null) {
				return null;
			}
			List<LabeledArrow> list = this.adjacentMap(true).get(x);
			if (list == null) {
				return Collections.emptyMap();
			}
			HashMap<Variable, List<Label>> out = new HashMap<Variable, List<Label>>();
			for (int i = 0, n = list.size(); i < n; ++i) {
				LabeledArrow arrow = list.get(i);
				List<Label> xs = out.get(arrow.target);
				if (xs == null) {
					out.put(arrow.target, xs = Collections.singletonList(arrow.label));
				} else if (xs.size() == 1) {
					xs = new ArrayList<Label>(xs);
					xs.add(arrow.label);
					out.put(arrow.target, xs);
				} else {
					xs.add(arrow.label);
				}
			}
			return out;
		}
		private Map<Variable, List<LabeledArrow>> adjacentMap(boolean anyway) {
			if (this.adjacentMap == null && anyway) {
				this.adjacentMap = this.newAdjacentMap();
			}
			return this.adjacentMap;
		}
		private Map<Variable, List<LabeledArrow>> newAdjacentMap() {
			HashMap<Variable, List<LabeledArrow>> out = new HashMap<Variable, List<LabeledArrow>>();
			for (int i = 0, n = this.arrows.length; i < n; ++i) {
				LabeledArrow arrow = this.arrows[i];
				List<LabeledArrow> xs = out.get(arrow.source);
				if (xs == null) {
					out.put(arrow.source, xs = Collections.singletonList(arrow));
				} else if (xs.size() == 1) {
					xs = new ArrayList<LabeledArrow>(xs);
					xs.add(arrow);
					out.put(arrow.source, xs);
				} else {
					xs.add(arrow);
				}
			}
			return out;
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

	/**
	 * adapts distributive law for free algebra on {@link Variable}. the method
	 * {@link FlattenEauation#multiplies(FlattenEauation)} is valid even when plus
	 * is defined by PEG's choice.
	 */
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
	static class VariablePair implements Cloneable, Comparable<VariablePair> {
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
				return this.equalPair((VariablePair) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalPair(VariablePair x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return (this.x0 == x.x0) && (this.x1 == x.x1);
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
		@Override
		public int compareTo(VariablePair x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			} else if (this.x0.index < x.x0.index) {
				return -1;
			} else if (this.x0.index == x.x0.index) {
				if (this.x1.index < x.x1.index) {
					return -1;
				} else if (x1.index == x.x1.index) {
					return 1;
				}
				return 0;
			}
			return 1;
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
		int equationCount;

		Parser initializeParse() {
			this.variableMap = null;
			this.arrowCount = 0;
			this.equationCount = 0;
			return this;
		}
		Map<String, Variable> variableMap(boolean anyway) {
			if (this.variableMap == null) {
				this.variableMap = new HashMap<String, Variable>();
			}
			return this.variableMap;
		}
		Variable variable(String name, boolean anyway) {
			Map<String, Variable> map = this.variableMap(anyway);
			if (name == null && map == null) {
				return null;
			}
			Variable x = map.get(name);
			if (x == null && anyway) {
				x = new Variable(name, map.size());
				map.put(name, x);
			}
			return x;
		}

		public LabeledGraph parse(GrammarNode nodes) throws ParserException {
			this.initializeParse();
			return this.newGraph(nodes);
		}
		public LabeledGraph newGraph(GrammarNode nodes) throws ParserException {
			LabeledArrow[] arrows = this.newArrows(nodes);
			Variable[] xs = new Variable[this.equationCount << 1];
			Iterator<Variable> p = this.variableMap(true).values().iterator();
			while (p.hasNext()) {
				Variable x = p.next();
				int index = x.index;
				if (index < xs.length) {
					xs[index] = x;
				}
			}
			return new LabeledGraph(arrows, xs);
		}
		LabeledArrow[] newArrows(GrammarNode nodes) throws ParserException {
			GrammarNode node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				this.fixBoundedVariables(node);
				++this.equationCount;
			}
			List<LabeledArrow> xs = new ArrayList<LabeledArrow>();
			node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				FlattenEauation eqs = this.flattenEquation(node);
				this.newArrows(xs, node, eqs);
			}
			return xs.toArray(LabeledArrow.EMPTY_ARRAY);
		}
		private List<LabeledArrow> newArrows(List<LabeledArrow> output,
				GrammarNode node, FlattenEauation eqs) {
			final GrammarNode lhs = node.firstNode();
			final Variable variable = this.variable(lhs.value, true);
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
							Label label = new Label(eq, dycks);
							LabeledArrow arrow = new LabeledArrow(source, y, label);
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
					Label label = new Label(eq, dycks);
					Variable target = variable.opposite();
					LabeledArrow arrow = new LabeledArrow(source, target, label);
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
				String name = node.value;
				if (name == null) {
					String msg = "value of variable must not be null";
					throw new ParserException(msg);
				}
				Variable x = this.variable(name, true);
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

			String name = lhs.value;
			if (name == null) {
				String msg = "value of variable must not be null";
				throw new ParserException(msg);
			}
			Variable start = this.variable(name, true);
			Variable end = this.variable(name + "$", true);
			start.type = Variable.BOUNDED_START;
			end.type = Variable.BOUNDED_END;
			start.makeOpposite(end);
		}
	}

	public void test_countContranction() {
		{
			int[] xs = { 1, 2 };
			int[] ys = { 3, 4 };
			int cn = Label.countContranction(xs, ys);
			Assert.assertEquals("#contraction", 0, cn);
		}
		{
			int[] xs = { 1, 2 };
			int[] ys = { -2, 4 };
			int cn = Label.countContranction(xs, ys);
			Assert.assertEquals("#contraction", 1, cn);
		}
		{
			int[] xs = { 1, 2 };
			int[] ys = { -2, -1 };
			int cn = Label.countContranction(xs, ys);
			Assert.assertEquals("#contraction", 2, cn);
		}
		{
			int[] xs = { 1, 2, 3 };
			int[] ys = { -2, -1 };
			int cn = Label.countContranction(xs, ys);
			Assert.assertEquals("#contraction", -1, cn);
		}
		{
			int[] xs = { 1, 2 };
			int[] ys = { 3, 4 };
			Label x = new Label(Variable.EMPTY_ARRAY, xs);
			Label y = new Label(Variable.EMPTY_ARRAY, ys);
			Label z = x.multiplies(y);
			Debug.log().debug(x + " " + y + " = " + z);
		}
		{
			int[] xs = { 1, 2 };
			int[] ys = { -2, 4 };
			Label x = new Label(Variable.EMPTY_ARRAY, xs);
			Label y = new Label(Variable.EMPTY_ARRAY, ys);
			Label z = x.multiplies(y);
			Debug.log().debug(x + " " + y + " = " + z);
		}
		{
			int[] xs = { 1, 2 };
			int[] ys = { -2, -1 };
			Label x = new Label(Variable.EMPTY_ARRAY, xs);
			Label y = new Label(Variable.EMPTY_ARRAY, ys);
			Label z = x.multiplies(y);
			Debug.log().debug(x + " " + y + " = " + z);
		}
		{
			int[] xs = { 1, 2, 3 };
			int[] ys = { -2, -1 };
			Label x = new Label(Variable.EMPTY_ARRAY, xs);
			Label y = new Label(Variable.EMPTY_ARRAY, ys);
			Label z = x.multiplies(y);
			Debug.log().debug(x + " " + y + " = " + z);
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
			LabeledArrow[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				LabeledArrow arrow = arrows[i];
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
			LabeledArrow[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				LabeledArrow arrow = arrows[i];
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
			LabeledArrow[] arrows = parser.newArrows(eqs);
			for (int i = 0, n = arrows.length; i < n; ++i) {
				LabeledArrow arrow = arrows[i];
				Debug.log().debug(arrow);
			}
		}
	}

	static class DumpXy {
		private static String op(String name) {
			name = name.replaceAll("_", "\\_");
			return "\\op{" + name + "}";
		}
		public static Appendable vertex(Appendable output, Variable x)
				throws IOException {
			if (x.isType(Variable.BOUNDED_END)) {
				return output.append("\\bar{").append(op(x.opposite().name()))
						.append("}");
			}
			return output.append(op(x.name()));
		}

		public static Appendable arrow(Appendable output, Label label)
				throws IOException {
			Variable[] xs = label.variables;
			for (int xi = 0, xn = xs.length; xi < xn; ++xi) {
				Variable x = xs[xi];
				if (x.isType(Variable.BOUNDED_START)) {
					output.append(op(x.name())).append("^{\\ini}");
				} else if (x.isType(Variable.BOUNDED_END)) {
					output.append(op(x.opposite().name())).append("^{\\fin}");
				} else {
					output.append(op(x.name()));
				}
			}
			int[] ys = label.dycks;
			for (int yi = 0, yn = ys.length; yi < yn; ++yi) {
				int y = ys[yi];
				if (0 < y) {
					output.append('[').append(Integer.toString(y)).append(']');
				} else if (y < 0) {
					output.append('[').append(Integer.toString(-y)).append("]^\\dag");
				}
			}
			return output;
		}
		public static Appendable offset(Appendable output, Variable source,
				Variable target) throws IOException {
			if (source.isType(Variable.BOUNDED_START)) {
				if (target.isType(Variable.BOUNDED_START)) {
					return DumpXy.offset(output, 0, (target.index - source.index) >> 1);
				} else {
					return DumpXy.offset(output, 1, (target.index - source.index) >> 1);
				}
			} else {
				if (target.isType(Variable.BOUNDED_START)) {
					return DumpXy.offset(output, -1,
							(target.index - source.index + 1) >> 1);
				} else {
					return DumpXy.offset(output, 0,
							(target.index - source.index + 1) >> 1);
				}
			}
		}
		private static Appendable offset(Appendable output, int row, int col)
				throws IOException {
			if (0 < row) {
				while (0 < row--) {
					output.append('d');
				}
			} else if (row < 0) {
				while (row++ < 0) {
					output.append('u');
				}
			}
			if (0 < col) {
				while (0 < col--) {
					output.append('r');
				}
			} else if (col < 0) {
				while (col++ < 0) {
					output.append('l');
				}
			}
			return output;
		}
		public static Appendable write(Appendable writer, LabeledGraph graph)
				throws IOException {
			Variable[] xs = graph.variables;
			Set<VariablePair> arrows = new HashSet<VariablePair>();
			VariablePair arrow = new VariablePair();
			writer.append("\\xymatrix{\n\t");
			for (int xi = 0, xn = xs.length; xi < xn; ++xi) {
				Variable source = xs[xi];
				Map<Variable, List<Label>> targets = graph.adjacentMap(source);
				if ((xi & 1) == 1) {
					continue;
				} else if (xi != 0) {
					writer.append(" & ");
				}
				DumpXy.vertex(writer, source);
				for (Iterator<Entry<Variable, List<Label>>> p = targets.entrySet()
						.iterator(); p.hasNext();) {
					Entry<Variable, List<Label>> pair = p.next();
					Variable target = pair.getKey();
					arrows.add(arrow.set(source, target).clone());
					List<Label> ys = pair.getValue();
					if (source == target) {
						for (int yi = 0, yn = ys.size(); yi < yn; ++yi) {
							Label label = ys.get(yi);
							if (yi != 0) {
								writer.append(" + ");
							} else {
								writer.append(" ");
							}
							DumpXy.arrow(writer.append("\\ar@(ul,ur)[]^{"), label)
									.append("}");
						}
					} else {
						boolean shift = false;
						if (arrows.contains(arrow.set(target, source))) {
							shift = true;
						}
						for (int yi = 0, yn = ys.size(); yi < yn; ++yi) {
							Label label = ys.get(yi);
							if (yi != 0) {
								writer.append(" + ");
							} else {
								writer.append(" ");
							}
							if (shift) {
								writer.append("\\ar@<1ex>[");
							} else {
								writer.append("\\ar[");
							}
							DumpXy.offset(writer, source, target).append("]^{");
							DumpXy.arrow(writer, label).append("}");
						}
					}
				}
			}
			writer.append(" \\\\\n\t");
			for (int xi = 0, xn = xs.length; xi < xn; ++xi) {
				Variable source = xs[xi];
				Map<Variable, List<Label>> targets = graph.adjacentMap(source);
				if ((xi & 1) == 0) {
					continue;
				} else if (xi != 1) {
					writer.append(" & ");
				}
				DumpXy.vertex(writer, source);
				for (Iterator<Entry<Variable, List<Label>>> p = targets.entrySet()
						.iterator(); p.hasNext();) {
					Entry<Variable, List<Label>> pair = p.next();
					Variable target = pair.getKey();
					arrows.add(arrow.set(source, target).clone());
					List<Label> ys = pair.getValue();
					if (source == target) {
						for (int yi = 0, yn = ys.size(); yi < yn; ++yi) {
							Label label = ys.get(yi);
							if (yi != 0) {
								writer.append(" + ");
							} else {
								writer.append(" ");
							}
							DumpXy.arrow(writer.append("\\ar@(dr,dl)[]_{"), label)
									.append("}");
						}
					} else {
						boolean shift = false;
						if (arrows.contains(arrow.set(target, source))) {
							shift = true;
						}
						for (int yi = 0, yn = ys.size(); yi < yn; ++yi) {
							Label label = ys.get(yi);
							if (yi != 0) {
								writer.append(" + ");
							} else {
								writer.append(" ");
							}
							if (shift) {
								writer.append("\\ar@<1ex>[");
							} else {
								writer.append("\\ar[");
							}
							DumpXy.offset(writer, source, target).append("]^{");
							DumpXy.arrow(writer, label).append("}");
						}
					}
				}
			}
			return writer.append("\n}");
		}
	}

	public void test_dumpXymatrix() throws ParserException, IOException {
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
			LabeledGraph graph = parser.newGraph(eqs);

			Writer writer = new PrintWriter(System.out);
			DumpXy.write(writer, graph);
			writer.flush();
		}
	}
}

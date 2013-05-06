package parser.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;

public class TreeNodeTest_2 extends TestCase {
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
			if (!this.isBinary()) {
				return output;
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
			this.firstNode().writeInfix(output, this.that());
			String op = this.infixName();
			if (op.length() < 1) {
				output.append(' ');
			} else {
				output.append(' ');
				output.append(op);
				output.append(' ');
			}
			this.lastNode().writeInfix(output, this.that());
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
					if (node instanceof GrammarNode) {
						GrammarNode x = (GrammarNode) node;
						if (x.value() != null) {
							return x.value().toString();
						}
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
		static final int FREE_TYPE = 0;
		static final int BOUNDED_TYPE = FREE_TYPE + 1;
		static final int BOUNDED_START_TYPE = BOUNDED_TYPE + 1;
		static final int BOUNDED_END_TYPE = BOUNDED_START_TYPE + 1;
		static final int NUMBER_OF_TYPES = BOUNDED_END_TYPE + 1;

		static interface Info {
			String typeName();
		}

		static final Info[] INFOS = Variable.newInfos();

		static Info[] newInfos() {
			Info[] out = new Info[NUMBER_OF_TYPES];
			out[FREE_TYPE] = new Info() {
				public String typeName() {
					return "free";
				}
			};
			out[BOUNDED_TYPE] = new Info() {
				public String typeName() {
					return "bounded";
				}
			};
			out[BOUNDED_START_TYPE] = new Info() {
				public String typeName() {
					return "bounded.start";
				}
			};
			out[BOUNDED_END_TYPE] = new Info() {
				public String typeName() {
					return "bounded.end";
				}
			};
			return out;
		};

		final String name;
		int type;
		Variable opposite;

		Variable(String name) {
			this(name, FREE_TYPE);
		}
		Variable(String name, int type) {
			this.name = name;
			this.type = type;
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
			case BOUNDED_TYPE:
			case BOUNDED_START_TYPE:
			case BOUNDED_END_TYPE:
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
			return this.name + " {" + INFOS[this.type].typeName() + "}";
		}
		Variable makeOpposite(Variable x) {
			this.opposite = x;
			x.opposite = this;
			return this;
		}
	}

	static class VariableNode extends Node<VariableNode> {
		public static final int VARIABLE = Node.NUMBER_OF_TYPES;
		protected static final int NUMBER_OF_TYPES = VARIABLE + 1;

		private static final Info[] INFOS = VariableNode.newInfo();

		private static Info[] newInfo() {
			Info[] out = new Info[VariableNode.NUMBER_OF_TYPES];
			return VariableNode.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			Node.newInfo(out);
			out[VARIABLE] = new Info() {
				public String typeName() {
					return "variable";
				}
				String name(Object node) {
					if (node instanceof VariableNode) {
						VariableNode x = (VariableNode) node;
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
			};
			return out;
		}
		static String typeName(int type) {
			return VariableNode.INFOS[type].typeName();
		}

		final Variable variable;

		VariableNode(Variable variable) {
			this(variable != null ? VariableNode.VARIABLE : VariableNode.ONE,
					variable);
		}
		VariableNode(int type, Variable variable) {
			super(type);
			this.variable = variable;
		}
		@Override
		protected Info nodeInfo() {
			return VariableNode.INFOS[this.type];
		}
		@Override
		VariableNode newNode(int type) {
			return new VariableNode(type, null);
		}
		@Override
		public boolean equals(Object x) {
			if (this == x) {
				return true;
			}
			try {
				VariableNode y = (VariableNode) x;
				return this.variable == y.variable;
			} catch (Exception ex) {
				return false;
			}
		}

		Variable variable() {
			return this.variable;
		}
		VariableNode add(VariableNode links) {
			return this.addLastNode(links);
		}
		public String name() {
			return this.variable != null ? this.variable.name() : null;
		}
		@Override
		public String toString() {
			return this.toArrow();
		}
		public String toArrow() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeArrow(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeArrow(Appendable output) throws IOException {
			output.append(this.name()).append(" -> ");
			VariableNode x = this.firstNode();
			if (x == null) {
				return output.append("null");
			}
			output.append(x.name());
			for (x = x.nextNode(); x != null; x = x.nextNode()) {
				output.append(" + ").append(x.name());
			}
			return output;
		}
	}

	static class IndexedVarNode extends VariableNode {
		final int index;

		IndexedVarNode(Variable variable, int index) {
			super(variable);
			this.index = index;
		}
		@Override
		public boolean equals(Object x) {
			if (this == x) {
				return true;
			}
			try {
				IndexedVarNode y = (IndexedVarNode) x;
				return this.variable == y.variable && this.index == y.index;
			} catch (Exception ex) {
				return false;
			}
		}

		public int index() {
			return this.index;
		}
		public String indexedName() {
			String name = this.name();
			if (name == null) {
				return name;
			}
			return name + ':' + this.index();
		}
		@Override
		public Appendable writeArrow(Appendable output) throws IOException {
			output.append(this.indexedName()).append(" -> ");
			IndexedVarNode x = (IndexedVarNode) this.firstNode();
			if (x == null) {
				return output.append("null");
			}
			output.append(x.indexedName());
			for (x = (IndexedVarNode) x.nextNode(); x != null; x = (IndexedVarNode) x
					.nextNode()) {
				output.append(" + ").append(x.indexedName());
			}
			return output;
		}
	}

	static class VariableArrow {
		boolean owingUnit;
		List<IndexedVarNode> nodes;
		List<IndexedVarNode> firstNodes;
		List<IndexedVarNode> lastNodes;

		boolean owingUnit() {
			return this.owingUnit;
		}
		VariableArrow owingUnit(boolean set) {
			this.owingUnit = set;
			return this;
		}
		List<IndexedVarNode> nodes(boolean anyway) {
			if (this.nodes == null && anyway) {
				this.nodes = new ArrayList<IndexedVarNode>();
			}
			return this.nodes;
		}
		List<IndexedVarNode> firstNodes(boolean anyway) {
			if (this.firstNodes == null && anyway) {
				this.firstNodes = new ArrayList<IndexedVarNode>();
			}
			return this.firstNodes;
		}
		List<IndexedVarNode> lastNodes(boolean anyway) {
			if (this.lastNodes == null && anyway) {
				this.lastNodes = new ArrayList<IndexedVarNode>();
			}
			return this.lastNodes;
		}

		VariableArrow plus(IndexedVarNode node) {
			this.nodes(true).add(node);
			this.firstNodes(true).add(node);
			this.lastNodes(true).add(node);
			return this;
		}
		VariableArrow plus(VariableArrow node) {
			this.owingUnit |= node.owingUnit;
			this.nodes(true).addAll(node.nodes(true));
			this.firstNodes(true).addAll(node.firstNodes(true));
			this.lastNodes(true).addAll(node.lastNodes(true));
			return this;
		}

		VariableArrow multiplies(IndexedVarNode node) {
			this.nodes(true).add(node);
			List<IndexedVarNode> xs = this.lastNodes(true);
			for (int i = 0, n = xs.size(); i < n; ++i) {
				xs.get(i).add(node);
			}
			if (this.owingUnit) {
				this.firstNodes(true).add(node);
			}
			this.lastNodes(true).clear();
			this.lastNodes(true).add(node);
			this.owingUnit = false;
			return this;
		}
		VariableArrow multiplies(VariableArrow node) {
			this.nodes(true).addAll(node.nodes(true));
			List<IndexedVarNode> xs = this.lastNodes(true);
			for (int i = 0, m = xs.size(); i < m; ++i) {
				IndexedVarNode x = xs.get(i);
				List<IndexedVarNode> ys = node.firstNodes(true);
				for (int j = 0, n = ys.size(); j < n; ++j) {
					x.add(ys.get(j));
				}
			}
			if (this.owingUnit) {
				this.firstNodes(true).addAll(node.firstNodes(true));
			}
			if (node.owingUnit) {
				this.lastNodes(true).addAll(node.lastNodes(true));
			} else {
				this.lastNodes(true).clear();
				this.lastNodes(true).addAll(node.lastNodes(true));
			}
			this.owingUnit &= node.owingUnit;
			return this;
		}
	}

	static class EquationArrow {
		Variable lhs;
		GrammarNode rhs;
		VariableArrow arrow;

		EquationArrow(Variable lhs, GrammarNode rhs, VariableArrow arrow) {
			this.lhs = lhs;
			this.rhs = rhs;
			this.arrow = arrow;
		}
	}

	static class ExtendedArrow extends EquationArrow {
		static IndexedVarNode startNode(VariableArrow arrow) {
			if (arrow != null) {
				List<IndexedVarNode> list = arrow.firstNodes;
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			}
			return null;
		}
		static IndexedVarNode endNode(VariableArrow arrow) {
			if (arrow != null) {
				List<IndexedVarNode> list = arrow.lastNodes;
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			}
			return null;
		}
		static Variable startVariable(VariableArrow arrow) {
			IndexedVarNode node = startNode(arrow);
			if (node != null) {
				return node.variable;
			}
			return null;
		}
		static Variable endVariable(VariableArrow arrow) {
			IndexedVarNode node = endNode(arrow);
			if (node != null) {
				return node.variable;
			}
			return null;
		}

		ExtendedArrow(GrammarNode rhs, VariableArrow arrow) {
			super(ExtendedArrow.startVariable(arrow), rhs, arrow);
		}
		IndexedVarNode startNode() {
			return ExtendedArrow.startNode(this.arrow);
		}
		IndexedVarNode endNode() {
			return ExtendedArrow.endNode(this.arrow);
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
					if (node instanceof DyckNode) {
						DyckNode x = (DyckNode) node;
						return x.toInfix();
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
			return DyckNode.INFOS[type].typeName();
		}

		final Variable variable;
		final int[] dyck;

		DyckNode(int type) {
			this(type, null, null);
		}
		DyckNode(Variable variable) {
			this(DyckNode.GENERATOR, variable, null);
		}
		DyckNode(Variable variable, int[] dyck) {
			this(DyckNode.GENERATOR, variable, dyck);
		}
		DyckNode(int type, Variable variable, int[] dyck) {
			super(type);
			this.variable = variable;
			if (dyck == null) {
				dyck = ArrayHelper.EMPTY_INT_ARRAY;
			}
			this.dyck = dyck;
		}
		@Override
		protected Info nodeInfo() {
			return DyckNode.INFOS[this.type];
		}
		@Override
		protected DyckNode newNode(int type) {
			return new DyckNode(type);
		}
		@Override
		protected Appendable writeInfixLeaf(Appendable output, DyckNode parent)
				throws IOException {
			Variable x = this.variable;
			switch (x.type()) {
			case VariableNode.ONE:
				return this.writeInfixDyck(output);
			default:
				return this.writeInfixDyck(output.append(x.name()));
			}
		}
		protected Appendable writeInfixDyck(Appendable output) throws IOException {
			output.append('[');
			for (int i = 0, n = this.dyck.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(Integer.toString(this.dyck[i]));
			}
			return output.append(']');
		}
		@Override
		public String toString() {
			return this.toInfix();
		}
	}

	static class StateArrow {
		final StateNode target;
		final DyckNode label;

		StateArrow(StateNode target, Variable variable) {
			this(target, new DyckNode(variable, null));
		}
		StateArrow(StateNode target, Variable variable, int[] dyck) {
			this(target, new DyckNode(variable, dyck));
		}
		StateArrow(StateNode target, DyckNode label) {
			this.target = target;
			this.label = label;
		}
		public String toArrow(StateNode source) {
			return this.label.toInfix() + ": " + source + " -> " + this.target;
		}
	}

	static class StateNode extends Node<StateNode> {
		public static final int TRANSIENT_STATE = Node.NUMBER_OF_TYPES;
		public static final int START_STATE = TRANSIENT_STATE + 1;
		public static final int END_STATE = START_STATE + 1;
		public static final int START_END_STATE = END_STATE + 1;
		protected static final int NUMBER_OF_TYPES = START_END_STATE + 1;

		private static final Info[] INFOS = StateNode.newInfo();

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

		List<StateArrow> arrows;
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
		List<StateArrow> arrows(boolean anyway) {
			if (this.arrows == null && anyway) {
				this.arrows = new ArrayList<StateArrow>();
			}
			return this.arrows;
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

	static class Parser {
		Map<String, Variable> variables;
		int nodeSize;

		Map<String, Variable> getVariables(boolean anyway) {
			if (this.variables == null) {
				this.variables = new HashMap<String, Variable>();
			}
			return this.variables;
		}
		Parser initializeParse() {
			this.variables = null;
			this.nodeSize = 0;
			return this;
		}
		Variable getVariable(String name, boolean anyway) {
			if (name == null) {
				return null;
			}
			Map<String, Variable> map = this.getVariables(anyway);
			if (map == null) {
				return null;
			}
			Variable x = map.get(name);
			if (x == null && anyway) {
				x = new Variable(name);
				map.put(name, x);
			}
			return x;
		}
		IndexedVarNode newVariableNode(Variable x) {
			return new IndexedVarNode(x, this.nodeSize++);
		}
		int nodeSize() {
			return this.nodeSize;
		}
		String variableName(GrammarNode node) {
			return node.infixName();
		}

		StateNode[] newStates(GrammarNode nodes) throws ParserException {
			List<ExtendedArrow> arrows = this.newExtendedArrows(nodes);
			StateNode[] states = this.newStateNodes(arrows);
			this.newStateArrows(states, arrows);
			Set<StateNode> visited = new HashSet<StateNode>();
			int count = 0;
			for (int i = 0, n = states.length; i < n; ++i) {
				if (visited.contains(states[i])) {
					states[i] = null;
				} else {
					visited.add(states[i]);
					++count;
				}
			}
			StateNode[] newStates = new StateNode[count];
			count = 0;
			for (int i = 0, n = states.length; i < n; ++i) {
				if (states[i] != null) {
					newStates[count++] = states[i];
				}
			}
			return newStates;
		}

		static class DyckInfo {
			int dyck;
			int maxDyck;
		}

		private StateNode[] newStateArrows(StateNode[] states,
				List<ExtendedArrow> arrows) {
			DyckInfo dycks = new DyckInfo();
			for (int i = 0, n = arrows.size(); i < n; ++i) {
				ExtendedArrow arrow = arrows.get(i);
				IndexedVarNode node = arrow.startNode();
				Set<VariableNode> visited = new HashSet<VariableNode>();
				this.newStateArrows(states, node, dycks, visited);
			}
			return states;
		}
		private StateNode[] newStateArrows(StateNode[] states, VariableNode node,
				DyckInfo dyckInfo, Set<VariableNode> visited) {
			if (visited.contains(node)) {
				// visiting only for traversal
			} else {
				visited.add(node);
			}
			int dyck = dyckInfo.dyck;
			IndexedVarNode x = (IndexedVarNode) node;
			StateNode source = states[x.index()];
			if (0 < dyck && x.variable().isBounded()) {
				source = source.opposite();
			}
			VariableNode next = node.firstNode();
			for (; next != null; next = next.nextNode()) {
				int nextDyck = 0;
				if (next.variable().isType(Variable.BOUNDED_START_TYPE)) {
					nextDyck = ++dyckInfo.maxDyck;
				}
				int[] dycks = null;
				if (0 < dyck) {
					if (0 < nextDyck) {
						dycks = new int[] { -dyck, nextDyck };
					} else {
						dycks = new int[] { -dyck };
					}
				} else if (0 < nextDyck) {
					dycks = new int[] { nextDyck };
				}
				DyckNode label = new DyckNode(next.variable(), dycks);
				x = (IndexedVarNode) next;
				StateNode target = states[x.index()];
				StateArrow arrow = new StateArrow(target, label);
				source.arrows(true).add(arrow);
				dyckInfo.dyck = nextDyck;
				if (visited.contains(next)) {
				} else {
					this.newStateArrows(states, next, dyckInfo, visited);
				}
			}
			return states;
		}
		private StateNode[] newStateNodes(List<ExtendedArrow> arrows) {
			int nNode = this.nodeSize();
			StateNode[] states = new StateNode[nNode];
			Map<Variable, StateNode> stateMap = new HashMap<Variable, StateNode>();
			for (int i = 0, n = arrows.size(); i < n; ++i) {
				ExtendedArrow arrow = arrows.get(i);
				IndexedVarNode start = arrow.startNode();
				List<IndexedVarNode> nodes = arrow.arrow.nodes(true);
				int count = 1;
				for (int ii = 0, nn = nodes.size(); ii < nn; ++ii) {
					IndexedVarNode node = nodes.get(ii);
					StateNode state = null;
					if (node.variable().isBounded()) {
						Variable var = node.variable();
						state = stateMap.get(var);
						if (state == null) {
							StateNode opposite = null;
							if (var.isType(Variable.BOUNDED_START_TYPE)) {
								Variable op = var.opposite;
								state = new StateNode(StateNode.START_STATE, var.name());
								opposite = new StateNode(StateNode.END_STATE, op.name());
							} else {
								Variable op = var.opposite;
								state = new StateNode(StateNode.END_STATE, var.name());
								opposite = new StateNode(StateNode.START_STATE, op.name());
							}
							state.makeOpposite(opposite);
							stateMap.put(var, state);
							stateMap.put(var.opposite(), opposite);
						}
					} else {
						String name = start.name() + "#" + (count++);
						state = new StateNode(name);
					}
					states[node.index()] = state;
				}
			}
			return states;
		}

		List<ExtendedArrow> newExtendedArrows(GrammarNode nodes)
				throws ParserException {
			this.initializeParse();
			List<ExtendedArrow> out = new ArrayList<ExtendedArrow>();
			GrammarNode node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				ExtendedArrow link = this.newExtendedArrow(node);
				out.add(link);
			}
			return out;
		}
		private ExtendedArrow newExtendedArrow(GrammarNode node)
				throws ParserException {
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
			Variable start = this.getVariable(name, true);
			start.type = Variable.BOUNDED_START_TYPE;
			Variable end = this.getVariable(name + "$", true);
			end.type = Variable.BOUNDED_END_TYPE;
			start.makeOpposite(end);
			GrammarNode rhs = node.lastNode();
			rhs = rhs.multiplies(new GrammarNode(end.name()));
			rhs = new GrammarNode(start.name()).multiplies(rhs);
			VariableArrow link = this.newEquationArrowRhs(rhs);
			return new ExtendedArrow(rhs, link);
		}

		List<EquationArrow> newEquationArrows(GrammarNode nodes)
				throws ParserException {
			this.initializeParse();
			List<EquationArrow> out = new ArrayList<EquationArrow>();
			GrammarNode node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				EquationArrow link = this.newEquationArrow(node);
				out.add(link);
			}
			return out;
		}
		private EquationArrow newEquationArrow(GrammarNode node)
				throws ParserException {
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
			Variable var = this.getVariable(name, true);
			var.type = Variable.BOUNDED_TYPE;
			GrammarNode rhs = node.lastNode();
			VariableArrow arrow = this.newEquationArrowRhs(rhs);
			return new EquationArrow(var, rhs, arrow);
		}

		private VariableArrow newEquationArrowRhs(GrammarNode node)
				throws ParserException {
			switch (node.type) {
			case GrammarNode.ONE: {
				if (!node.isLeaf(GrammarNode.ONE)) {
					String msg = "unit is not a leaf.";
					throw new ParserException(msg);
				}
				VariableArrow out = new VariableArrow();
				return out.owingUnit(true);
			}
			case GrammarNode.VARIABLE: {
				if (!node.isLeaf(GrammarNode.VARIABLE)) {
					String msg = "variable=" + this.variableName(node)
							+ " is not a leaf.";
					throw new ParserException(msg);
				}
				Variable x = this.getVariable(this.variableName(node), true);
				return new VariableArrow().plus(this.newVariableNode(x));
			}
			case GrammarNode.MULTIPLIES: {
				GrammarNode child = node.firstNode();
				if (child == null) {
					String msg = "empty " + node.typeName();
					throw new ParserException(msg);
				}
				VariableArrow out = this.newEquationArrowRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					switch (child.type) {
					case GrammarNode.ONE:
					break;
					case GrammarNode.VARIABLE: {
						Variable x = this.getVariable(this.variableName(child), true);
						out.multiplies(this.newVariableNode(x));
					}
					break;
					default: {
						VariableArrow link = this.newEquationArrowRhs(child);
						out.multiplies(link);
					}
					break;
					}
				}
				return out;
			}
			case Node.PLUS: {
				GrammarNode child = node.firstNode();
				if (child == null) {
					String msg = "empty " + node.typeName();
					throw new ParserException(msg);
				}
				VariableArrow out = this.newEquationArrowRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					switch (child.type) {
					case GrammarNode.ONE:
						out.owingUnit = true;
					break;
					case GrammarNode.VARIABLE: {
						Variable x = this.getVariable(this.variableName(child), true);
						out.plus(this.newVariableNode(x));
					}
					break;
					default: {
						VariableArrow arrow = this.newEquationArrowRhs(child);
						out.plus(arrow);
					}
					break;
					}
				}
				return out;
			}
			default:
				String msg = "unexpected node type=" + node.typeName();
				throw new ParserException(msg);
			}
		}
	}

	public void testGrammarNode() throws IOException {
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
			Debug.log().debug(eqs.toInfix());
		}
	}
	public void test_newExtendedArrows() throws ParserException {
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
			Debug.log().debug(eqs.toInfix());

			Parser parser = new Parser();
			List<ExtendedArrow> arrows = parser.newExtendedArrows(eqs);
			for (int i = 0, n = arrows.size(); i < n; ++i) {
				VariableArrow arrow = arrows.get(i).arrow;
				List<IndexedVarNode> nodes = arrow.nodes(true);
				for (int ii = 0, nn = nodes.size(); ii < nn; ++ii) {
					IndexedVarNode node = nodes.get(ii);
					Debug.log().debug(node.toArrow());
				}
			}
		}
	}
	public void test_newStates() throws ParserException {
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
			Debug.log().debug(eqs.toInfix());

			Parser parser = new Parser();
			StateNode[] states = parser.newStates(eqs);
			for (int i = 0, n = states.length; i < n; ++i) {
				StateNode state = states[i];
				Debug.log().debug(state.name() + ": " + state.typeName());
				List<StateArrow> arrows = state.arrows(true);
				for (int ii = 0, nn = arrows.size(); ii < nn; ++ii) {
					StateArrow arrow = arrows.get(ii);
					Debug.log().debug("\t" + arrow.toArrow(state));
				}
			}
		}
	}
}

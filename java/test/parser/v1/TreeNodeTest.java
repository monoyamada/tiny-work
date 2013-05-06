package parser.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.StringHelper;

public class TreeNodeTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static class Node extends TreeNode<Node> {
		private static final int UNIT = 0;
		private static final int VARIABLE = UNIT + 1;
		private static final int ASSIGNS = VARIABLE + 1;
		private static final int MULTIPLIES = ASSIGNS + 1;
		private static final int PLUS = MULTIPLIES + 1;
		private static final int NUMBER_OF_TYPES = PLUS + 1;
		
		private static final Info[] INFOS = Node.newInfo();

		protected	static interface Info {
			String typeName();
			String toPrefix(Node node);
			String toInfix(Node node);
			int precedence();
		}

		private static Info[] newInfo() {
			Info[] out = new Info[NUMBER_OF_TYPES];
			return newInfo(out);

		}
		protected static Info[] newInfo(Info[] out) {
			out[UNIT] = new Info() {
				public String typeName() {
					return "unit";
				}
				String toName(Node node) {
					return node != null ? "1" : null;
				}
				public String toPrefix(Node node) {
					return this.toName(node);
				}
				public String toInfix(Node node) {
					return this.toName(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[VARIABLE] = new Info() {
				public String typeName() {
					return "variable";
				}
				String toName(Node node) {
					return node != null ? node.value != null ? node.value.toString()
							: null : null;
				}
				public String toPrefix(Node node) {
					return this.toName(node);
				}
				public String toInfix(Node node) {
					return this.toName(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[ASSIGNS] = new Info() {
				public String typeName() {
					return "assigns";
				}
				public String toPrefix(Node node) {
					return node != null ? this.typeName() : null;
				}
				public String toInfix(Node node) {
					return node != null ? "=" : null;
				}
				public int precedence() {
					return 10;
				}
			};
			out[MULTIPLIES] = new Info() {
				public String typeName() {
					return "multiplies";
				}
				public String toPrefix(Node node) {
					return node != null ? this.typeName() : null;
				}
				public String toInfix(Node node) {
					return node != null ? "" : null;
				}
				public int precedence() {
					return 30;
				}
			};
			out[PLUS] = new Info() {
				public String typeName() {
					return "plus";
				}
				public String toPrefix(Node node) {
					return node != null ? this.typeName() : null;
				}
				public String toInfix(Node node) {
					return node != null ? "+" : null;
				}
				public int precedence() {
					return 20;
				}
			};
			return out;
		}

		static String typeName(int type) {
			return INFOS[type].typeName();
		}

		int type;
		Object value;

		Node(String name) {
			this(Node.VARIABLE, name);
		}
		Node(int type) {
			this(type, null);
		}
		Node(int type, Object value) {
			this.type = type;
			this.value = value;
		}

		@Override
		public String toString() {
			return this.toPrefix();
		}
		public String toPrefix() {
			return this.nodeInfo().toPrefix(this);
		}
		public String toInfix() {
			return this.nodeInfo().toInfix(this);
		}
		public String typeName() {
			return this.nodeInfo().typeName();
		}
		protected Info nodeInfo() {
			return Node.INFOS[this.type];
		}

		Node add(Node child) {
			return this.addLastNode(child);
		}
		Node assigns(Node rhs) {
			if (this.type != VARIABLE) {
				String msg = "assigns is only supported=" + typeName(Node.VARIABLE)
						+ " but this type=" + this.typeName();
				throw new UnsupportedOperationException(msg);
			}
			Node node = new Node(Node.ASSIGNS);
			return node.add(this).add(rhs);
		}
		Node multiplies(Node rhs) {
			Node node = new Node(Node.MULTIPLIES);
			return node.add(this).add(rhs);
		}
		Node plus(Node rhs) {
			Node node = new Node(Node.PLUS);
			return node.add(this).add(rhs);
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public boolean isBinary(int type) {
			if (this.type != type) {
				return false;
			}
			return this.isBinary();
		}
		public boolean isBinary() {
			Node first = this.firstNode();
			Node last = this.lastNode();
			if (first == null || last == null) {
				return false;
			}
			return first.nextNode() == last;
		}
		public boolean isLeaf(int type) {
			if (this.type != type) {
				return false;
			}
			return this.isLeaf();
		}
		public boolean isLeaf() {
			return this.firstNode() == null;
		}
		/**
		 * slow operation.
		 * 
		 * @return
		 */
		public int size() {
			int n = 0;
			Node node = this.firstNode();
			for (; node != this.lastNode(); node = node.nextNode()) {
				++n;
			}
			return n;
		}

		public Appendable writeInfix(Appendable output) throws IOException {
			return this.writeInfix(output, null);
		}
		private Appendable writeInfix(Appendable output, Node parent)
				throws IOException {
			switch (this.type) {
			case UNIT:
			case VARIABLE:
				return output.append(this.toInfix());
			default:
			break;
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
			this.firstNode().writeInfix(output, this);
			String op = this.toInfix();
			if (op.length() < 1) {
				output.append(' ');
			} else {
				output.append(' ');
				output.append(op);
				output.append(' ');
			}
			this.lastNode().writeInfix(output, this);
			if (braket) {
				output.append(')');
			}
			return output;
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
	}

	static class VariableNode extends TreeNode<VariableNode> {
		final Variable variable;
		final int index;

		VariableNode(Variable variable, int index) {
			this.variable = variable;
			this.index = index;
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
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				this.writeLink(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public String writeLink() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeLink(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeLink(Appendable output) throws IOException {
			output.append(this.indexedName()).append(" -> ");
			VariableNode x = this.firstNode();
			if (x == null) {
				return output.append("null");
			}
			output.append(x.indexedName());
			for (x = x.nextNode(); x != null; x = x.nextNode()) {
				output.append(" + ").append(x.indexedName());
			}
			return output;
		}
	}

	static class VariableLink {
		boolean owingUnit;
		List<VariableNode> nodes;
		List<VariableNode> firstNodes;
		List<VariableNode> lastNodes;

		boolean owingUnit() {
			return this.owingUnit;
		}
		VariableLink owingUnit(boolean set) {
			this.owingUnit = set;
			return this;
		}
		List<VariableNode> nodes(boolean anyway) {
			if (this.nodes == null && anyway) {
				this.nodes = new ArrayList<VariableNode>();
			}
			return this.nodes;
		}
		List<VariableNode> firstNodes(boolean anyway) {
			if (this.firstNodes == null && anyway) {
				this.firstNodes = new ArrayList<VariableNode>();
			}
			return this.firstNodes;
		}
		List<VariableNode> lastNodes(boolean anyway) {
			if (this.lastNodes == null && anyway) {
				this.lastNodes = new ArrayList<VariableNode>();
			}
			return this.lastNodes;
		}

		VariableLink plus(VariableNode node) {
			this.nodes(true).add(node);
			this.firstNodes(true).add(node);
			this.lastNodes(true).add(node);
			return this;
		}
		VariableLink plus(VariableLink node) {
			this.owingUnit |= node.owingUnit;
			this.nodes(true).addAll(node.nodes(true));
			this.firstNodes(true).addAll(node.firstNodes(true));
			this.lastNodes(true).addAll(node.lastNodes(true));
			return this;
		}

		VariableLink multiplies(VariableNode node) {
			this.nodes(true).add(node);
			List<VariableNode> xs = this.lastNodes(true);
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
		VariableLink multiplies(VariableLink node) {
			this.nodes(true).addAll(node.nodes(true));
			List<VariableNode> xs = this.lastNodes(true);
			for (int i = 0, m = xs.size(); i < m; ++i) {
				VariableNode x = xs.get(i);
				List<VariableNode> ys = node.firstNodes(true);
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

	static class EquationLink {
		Variable lhs;
		Node rhs;
		VariableLink link;

		EquationLink(Variable lhs, Node rhs, VariableLink link) {
			this.lhs = lhs;
			this.rhs = rhs;
			this.link = link;
		}
	}

	static class ExtendedLink {
		Node rhs;
		VariableLink link;

		ExtendedLink(Node rhs, VariableLink link) {
			this.rhs = rhs;
			this.link = link;
		}
		VariableNode start() {
			if (this.link != null) {
				List<VariableNode> list = this.link.firstNodes;
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			}
			return null;
		}
		VariableNode end() {
			if (this.link != null) {
				List<VariableNode> list = this.link.lastNodes;
				if (list != null && list.size() == 1) {
					return list.get(0);
				}
			}
			return null;
		}
	}

	static class DyckNode extends TreeNode<DyckNode> {
		final int type;
		Object value;

		DyckNode(int type, Object value) {
			this.type = type;
			this.value = value;
		}
	}

	static class Arrow extends TreeNode<Arrow> {
		Node free;

	}

	static class State extends TreeNode<State> {
		static final int NONE_TYPE = 0;
		static final int START_TYPE = NONE_TYPE + 1;
		static final int END_TYPE = START_TYPE + 1;
		static final int START_END_TYPE = END_TYPE + 1;
		static final int NUMBER_OF_TYPES = START_END_TYPE;
		static final Info[] INFOS = newInfos();

		static interface Info {
			String typeName();
		}

		static Info[] newInfos() {
			Info[] out = new Info[NUMBER_OF_TYPES];
			out[NONE_TYPE] = new Info() {
				@Override
				public String typeName() {
					return "none";
				}
			};
			out[START_TYPE] = new Info() {
				@Override
				public String typeName() {
					return "start";
				}
			};
			out[END_TYPE] = new Info() {
				@Override
				public String typeName() {
					return "end";
				}
			};
			out[START_END_TYPE] = new Info() {
				@Override
				public String typeName() {
					return "start_end";
				}
			};
			return out;
		}

		final int type;
		Object value;

		State(int type) {
			this.type = type;
		}
		State(int type, Object value) {
			this.type = type;
			this.value = value;
		}
		int type() {
			return this.type;
		}
		Object value() {
			return this.value;
		}
		String typeName() {
			return INFOS[this.type].typeName();
		}
		@Override
		public String toString() {
			return this.value() + ":" + this.typeName();
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
		VariableNode newVariableNode(Variable x) {
			return new VariableNode(x, this.nodeSize++);
		}
		int nodeSize() {
			return this.nodeSize;
		}

		List<ExtendedLink> newExtendedLinks(Node nodes) throws ParserException {
			this.initializeParse();
			List<ExtendedLink> out = new ArrayList<ExtendedLink>();
			Node node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				ExtendedLink link = this.newExtendedLink(node);
				out.add(link);
			}
			return out;
		}
		private ExtendedLink newExtendedLink(Node node) throws ParserException {
			if (!node.isBinary(Node.ASSIGNS)) {
				String msg = Messages.getUnexpectedValue(
						"root of equation must be assigns", Node.typeName(Node.ASSIGNS),
						node.typeName());
				throw new ParserException(msg);
			}
			Node lhs = node.firstNode();
			if (!lhs.isLeaf(Node.VARIABLE)) {
				String msg = Messages.getUnexpectedValue("lhs of assigns",
						Node.typeName(Node.VARIABLE), lhs.typeName());
				throw new ParserException(msg);
			}

			String name = lhs.toPrefix();
			Variable start = this.getVariable(name, true);
			start.type = Variable.BOUNDED_START_TYPE;
			Variable end = this.getVariable(name + "$", true);
			end.type = Variable.BOUNDED_END_TYPE;
			start.opposite = end;
			end.opposite = start;
			Node rhs = node.lastNode();
			rhs = rhs.clone().multiplies(new Node(end.name()));
			rhs = new Node(start.name()).multiplies(rhs);
			VariableLink link = this.newEquationLinkRhs(rhs);
			return new ExtendedLink(rhs, link);
		}

		List<EquationLink> newEquationLinks(Node nodes) throws ParserException {
			this.initializeParse();
			List<EquationLink> out = new ArrayList<EquationLink>();
			Node node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				EquationLink link = this.newEquationLink(node);
				out.add(link);
			}
			return out;
		}
		private EquationLink newEquationLink(Node node) throws ParserException {
			if (!node.isBinary(Node.ASSIGNS)) {
				String msg = Messages.getUnexpectedValue(
						"root of equation must be assigns", Node.typeName(Node.ASSIGNS),
						node.typeName());
				throw new ParserException(msg);
			}
			Node lhs = node.firstNode();
			if (!lhs.isLeaf(Node.VARIABLE)) {
				String msg = Messages.getUnexpectedValue("lhs of assigns",
						Node.typeName(Node.VARIABLE), lhs.typeName());
				throw new ParserException(msg);
			}

			Variable var = this.getVariable(lhs.toPrefix(), true);
			var.type = Variable.BOUNDED_TYPE;
			Node rhs = node.lastNode();
			VariableLink link = this.newEquationLinkRhs(rhs);
			return new EquationLink(var, rhs, link);
		}

		private VariableLink newEquationLinkRhs(Node node) throws ParserException {
			switch (node.type) {
			case Node.UNIT: {
				if (!node.isLeaf(Node.UNIT)) {
					String msg = "unit is not a leaf.";
					throw new ParserException(msg);
				}
				VariableLink out = new VariableLink();
				return out.owingUnit(true);
			}
			case Node.VARIABLE: {
				if (!node.isLeaf(Node.VARIABLE)) {
					String msg = "variable=" + node.toPrefix() + " is not a leaf.";
					throw new ParserException(msg);
				}
				Variable x = this.getVariable(node.toPrefix(), true);
				return new VariableLink().plus(this.newVariableNode(x));
			}
			case Node.MULTIPLIES: {
				Node child = node.firstNode();
				if (child == null) {
					String msg = "empty " + node.typeName();
					throw new ParserException(msg);
				}
				VariableLink out = this.newEquationLinkRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					switch (child.type) {
					case Node.UNIT:
					break;
					case Node.VARIABLE: {
						Variable x = this.getVariable(child.toPrefix(), true);
						out.multiplies(this.newVariableNode(x));
					}
					break;
					default: {
						VariableLink link = this.newEquationLinkRhs(child);
						out.multiplies(link);
					}
					break;
					}
				}
				return out;
			}
			case Node.PLUS: {
				Node child = node.firstNode();
				if (child == null) {
					String msg = "empty " + node.typeName();
					throw new ParserException(msg);
				}
				VariableLink out = this.newEquationLinkRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					switch (child.type) {
					case Node.UNIT:
						out.owingUnit = true;
					break;
					case Node.VARIABLE: {
						Variable x = this.getVariable(child.toPrefix(), true);
						out.plus(this.newVariableNode(x));
					}
					break;
					default: {
						VariableLink link = this.newEquationLinkRhs(child);
						out.plus(link);
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

	@SuppressWarnings("unused")
	public void testNode() throws ParserException, IOException {
		Node eqs = null;
		if (true) {
			Node empty = new Node(Node.UNIT);
			Node a = new Node("a");
			Node b = new Node("b");
			Node c = new Node("c");
			Node S = new Node("S");
			Node A = new Node("A");
			Node eqS = S.clone().assigns( //
					a.clone().multiplies(S.clone()).multiplies(A.clone()).plus( //
							empty.clone() //
							) //
					);
			Node eqA = A.clone().assigns( //
					a.clone().multiplies(b.clone()).multiplies(S.clone()).plus( //
							c.clone()));
			eqs = empty.clone();
			eqs.add(eqS).add(eqA);
		}
		if (false) {
			Node empty = new Node(Node.UNIT);
			Node a = new Node("a");
			Node b = new Node("b");
			Node eq = a.clone().assigns(b.clone());
			eqs = empty.clone();
			eqs.add(eq);
		}
		Parser parser = new Parser();
		{
			List<EquationLink> links = parser.newEquationLinks(eqs);
			Debug.log().debug(parser.getVariables(true).values());
			StringBuilder buffer = new StringBuilder();
			for (int i = 0, n = links.size(); i < n; ++i) {
				buffer.setLength(0);
				EquationLink x = links.get(i);
				x.rhs.writeInfix(buffer);
				Debug.log().debug(x.lhs.name() + " = " + buffer.toString());
				VariableLink link = x.link;
				Debug.log().debug("\tunit=" + link.owingUnit);
				List<VariableNode> ys = link.nodes(true);
				for (int ii = 0, nn = ys.size(); ii < nn; ++ii) {
					VariableNode y = ys.get(ii);
					buffer.setLength(0);
					Debug.log().debug("\t" + y.writeLink(buffer).toString());
				}
			}
		}
		{
			List<ExtendedLink> links = parser.newExtendedLinks(eqs);
			Debug.log().debug(parser.getVariables(true).values());
			StringBuilder buffer = new StringBuilder();
			for (int i = 0, n = links.size(); i < n; ++i) {
				buffer.setLength(0);
				ExtendedLink x = links.get(i);
				Debug.log().debug(x.rhs.writeInfix(buffer).toString());
				VariableLink link = x.link;
				List<VariableNode> ys = link.nodes(true);
				for (int ii = 0, nn = ys.size(); ii < nn; ++ii) {
					VariableNode y = ys.get(ii);
					buffer.setLength(0);
					Debug.log().debug("\t" + y.writeLink(buffer).toString());
				}
			}
		}
		{
			Debug.log().debug("---------- state ---------");
			List<ExtendedLink> links = parser.newExtendedLinks(eqs);
			int nNode = parser.nodeSize();
			String[] states = new String[nNode];
			for (int i = 0, n = links.size(); i < n; ++i) {
				ExtendedLink link = links.get(i);
				VariableNode start = link.start();
				List<VariableNode> nodes = link.link.nodes(true);
				int count = 1;
				for (int ii = 0, nn = nodes.size(); ii < nn; ++ii) {
					VariableNode node = nodes.get(ii);
					String name = null;
					if (node.variable().isBounded()) {
						name = node.variable().name();
					} else {
						name = start.name() + "#" + (count++);
					}
					states[node.index()] = name;
				}
			}
			Debug.log().debug(StringHelper.join(states, ", "));

			class DyckNode {
				final VariableNode node;
				final int dyck;

				DyckNode(VariableNode node, int dyck) {
					this.node = node;
					this.dyck = dyck;
				}
			}
			int nDyck = 0;
			for (int i = 0, n = links.size(); i < n; ++i) {
				ExtendedLink link = links.get(i);
				VariableNode start = link.start();
				List<DyckNode> stack = new ArrayList<DyckNode>();
				stack.add(new DyckNode(start, 0));
				Set<VariableNode> visited = new HashSet<VariableNode>();
				while (0 < stack.size()) {
					DyckNode dyckNode = stack.get(stack.size() - 1);
					VariableNode node = dyckNode.node;
					int dyck = dyckNode.dyck;
					if (visited.contains(dyckNode)) {
						// visiting only for traversal
					} else {
						visited.add(node);
					}
					VariableNode next = node.firstNode();
					for (; next != null && visited.contains(next); next = next.nextNode()) {
					}
					if (next == null) {
						stack.remove(stack.size() - 1);
					} else {
						String a = "";
						int nextDyck = 0;
						switch (next.variable().type()) {
						case Variable.BOUNDED_START_TYPE:
							nextDyck = ++nDyck;
						break;
						default:
							a = next.name();
							nextDyck = 0;
						break;
						}
						String d = "";
						if (0 < dyck) {
							if (0 < nextDyck) {
								d = "[" + (-dyck) + "][" + nextDyck + "]";
							} else {
								d = "[" + (-dyck) + "]";
							}
						} else if (0 < nextDyck) {
							d = "[" + nextDyck + "]";
						}
						String state0 = states[node.index()];
						if (0 < dyck && node.variable().isBounded()) {
							state0 = node.variable().opposite().name();
						}
						String state1 = states[next.index()];
						Debug.log().debug(a + d + ": " + state0 + " -> " + state1);
						dyck = nextDyck;
						stack.add(new DyckNode(next, dyck));
					}
				}
			}
		}
	}
}

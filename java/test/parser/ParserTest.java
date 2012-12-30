package parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

import parser.ParserTest.Node;
import parser.ParserTest.Z;

import junit.framework.TestCase;
import tiny.lang.Debug;

public class ParserTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	
	public void testReader() {
		
	}

	static abstract class Node {
		public static final Node[] EMPTY_ARRAY = {};

		static final int ZERO = 0;
		static final int ONE = ZERO + 1;
		static final int INTEGER = ONE + 1;
		static final int VARIABLE = INTEGER + 1;
		static final int PLUS = VARIABLE + 1;
		static final int MULTIPLIES = PLUS + 1;
		static final int POWERS = MULTIPLIES + 1;
		static final int ASSINGS = POWERS + 1;

		static String typeName(Node node) {
			return node != null ? Node.typeName(node.what()) : "null";
		}
		static String typeName(int what) {
			switch (what) {
			case Node.ZERO:
				return "0";
			case Node.ONE:
				return "1";
			case Node.INTEGER:
				return "integer";
			case Node.VARIABLE:
				return "variable";
			case Node.PLUS:
				return "plus";
			case Node.MULTIPLIES:
				return "multiplies";
			case Node.POWERS:
				return "powers";
			case Node.ASSINGS:
				return "assigns";
			default:
				return "unknown";
			}
		}

		@Override
		public Node clone() {
			try {
				return (Node) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new Error(ex);
			}
		}
		abstract int what();
		boolean isZero() {
			return false;
		}
		boolean isOne() {
			return false;
		}
		boolean isPrimitive() {
			return false;
		}
		Node plus(Node x) {
			Debug.isNotNull("Node", x);
			return new Binary(Node.PLUS, this, x);
		}
		Node times(Node x) {
			Debug.isNotNull("Node", x);
			return new Binary(Node.MULTIPLIES, this, x);
		}
		Node powers(Node x) {
			Debug.isNotNull("Z", x);
			return new Binary(Node.POWERS, this, x);
		}
		Node powers(int n) {
			if (n == 0) {
				return One.INSTANCE;
			} else if (n == 1) {
				return this;
			}
			return new Binary(Node.POWERS, this, Z.valueOf(n));
		}
	}

	static class Zero extends Node {
		static final Zero INSTANCE = new Zero();

		@Override
		int what() {
			return Node.ZERO;
		}
		@Override
		boolean isZero() {
			return true;
		}
		@Override
		boolean isPrimitive() {
			return true;
		}
	}

	static class One extends Node {
		static final One INSTANCE = new One();

		@Override
		int what() {
			return Node.ONE;
		}
		@Override
		boolean isOne() {
			return true;
		}
		@Override
		boolean isPrimitive() {
			return true;
		}
	}

	static class Z extends Node {
		final static Z[] cache = new Z[8];

		public static Node valueOf(int n) {
			if (0 <= n && n < cache.length) {
				Z x = cache[n];
				if (x == null) {
					cache[n] = x = new Z(n);
				}
				return x;
			}
			return new Z(n);
		}

		long value;

		public Z(long value) {
			this.value = value;
		}
		@Override
		int what() {
			return Node.INTEGER;
		}
		@Override
		boolean isZero() {
			return this.value == 0;
		}
		@Override
		boolean isOne() {
			return this.value == 1;
		}
		@Override
		boolean isPrimitive() {
			return true;
		}
	}

	static class Variable extends Node {
		String name;

		public Variable(String name) {
			this.name = name;
		}
		@Override
		int what() {
			return Node.VARIABLE;
		}
		@Override
		boolean isPrimitive() {
			return true;
		}
		Node assigns(Node x) {
			Debug.isNotNull("Node", x);
			return new Binary(Node.ASSINGS, this, x);
		}
	}

	static class Binary extends Node {
		int what;
		Node left;
		Node right;

		public Binary(int what) {
			this(what, null, null);
		}
		public Binary(int what, Node left, Node right) {
			this.what = what;
			this.left = left;
			this.right = right;
		}
		@Override
		int what() {
			return this.what;
		}
		public Binary swap() {
			Node x = this.left;
			this.left = this.right;
			this.right = x;
			return this;
		}
	}

	static class EqVariable extends Variable {
		public EqVariable(String name) {
			super(name);
		}
		Node equationNode;
		EqGraph equation;
	}

	static class EqVertex {
		static final EqVertex[] EMPTY_ARRAY = {};
		EqVariable variable;
		List<EqVertex> follows;

		public EqVertex(EqVariable node) {
			this.variable = node;
			this.follows = null;
		}
	}

	static class EqGraph {
		static final EqGraph[] EMPTY_ARRAY = {};

		static Node[] modifyNodes(Node[] eqs) {
			Map<String, EqVariable> varMap = new HashMap<String, EqVariable>();
			List<Node> newEqs = new ArrayList<Node>(eqs.length);
			for (Node node : eqs) {
				node = EqGraph.eliminateZero(node);
				node = EqGraph.replaceVariable(varMap, node);
				node = EqGraph.expandPower(node);
				newEqs.add(node);
			}
			return newEqs.toArray(Node.EMPTY_ARRAY);
		}
		private static Node replaceVariable(Map<String, EqVariable> varMap,
				Node node) {
			switch (node.what()) {
			case Node.ZERO:
			case Node.ONE:
			case Node.INTEGER:
				return node;
			case Node.VARIABLE: {
				Variable oldVar = (Variable) node;
				EqVariable newVar = varMap.get(oldVar);
				if (newVar == null) {
					newVar = new EqVariable(oldVar.name);
					varMap.put(oldVar.name, newVar);
				}
				return newVar;
			}
			case Node.PLUS:
			case Node.MULTIPLIES:
			case Node.POWERS: {
				Binary op = (Binary) node;
				Node left = EqGraph.replaceVariable(varMap, op.left);
				Node right = EqGraph.replaceVariable(varMap, op.right);
				return EqGraph.replaceIfChanged(op, left, right);
			}
			case Node.ASSINGS: {
				Binary op = (Binary) node;
				EqVariable left = (EqVariable) EqGraph.replaceVariable(varMap, op.left);
				Node right = EqGraph.replaceVariable(varMap, op.right);
				
				
				return EqGraph.replaceIfChanged(op, left, right);
				Binary op = (Binary) node;
				Variable oldVar = (Variable) op.left;
				EqVariable newVar = varMap.get(oldVar);
				if (newVar == null) {
					newVar = new EqVariable(oldVar.name);
					varMap.put(newVar.name, newVar);
				} else if (newVar.equation != null) {
					throw new IllegalArgumentException("duplicated definition of "
							+ oldVar.name);
				}
				EqGraph.replaceVariable(varMap, op.right);
			}
			break;
			default:
				throw new IllegalArgumentException("unknown type of node="
						+ Node.typeName(node.what()));
			}
			return node;
		}
		private static Node expandPower(Node node) {
			switch (node.what()) {
			case Node.ZERO:
			case Node.ONE:
			case Node.INTEGER:
			case Node.VARIABLE:
				return node;
			case Node.PLUS:
			case Node.MULTIPLIES:
			case Node.ASSINGS: {
				Binary op = (Binary) node;
				Node left = EqGraph.expandPower(op.left);
				Node right = EqGraph.expandPower(op.right);
				return EqGraph.replaceIfChanged(op, left, right);
			}
			case Node.POWERS: {
				Binary op = (Binary) node;
				node = EqGraph.expandPower(op.left);
				Z power = (Z) op.right;
				if (power.value < 0) {
					return op;
				} else if (power.value == 0) {
					return One.INSTANCE;
				} else if (power.value == 1) {
					return node;
				}
				Node unit = node;
				node = unit;
				for (long n = power.value; 1 < n--;) {
					node = node.times(unit);
				}
				return node;
			}
			default:
				throw new IllegalArgumentException("unknown type of node="
						+ Node.typeName(node.what()));
			}
		}
		private static Node eliminateZero(Node node) {
			switch (node.what()) {
			case Node.ZERO:
			case Node.ONE:
			case Node.INTEGER:
			case Node.VARIABLE:
				return node;
			case Node.PLUS: {
				Binary op = (Binary) node;
				Node left = EqGraph.eliminateZero(op.left);
				if (left.isZero()) {
					return EqGraph.eliminateZero(op.right);
				}
				Node right = EqGraph.eliminateZero(op.right);
				if (right.isZero()) {
					return left;
				}
				return EqGraph.replaceIfChanged(op, left, right);
			}
			case Node.MULTIPLIES: {
				Binary op = (Binary) node;
				Node left = EqGraph.eliminateZero(op.left);
				if (left.isZero()) {
					return left;
				} else if (left.isOne()) {
					return EqGraph.eliminateZero(op.right);
				}
				Node right = EqGraph.eliminateZero(op.right);
				if (right.isZero()) {
					return right;
				} else if (right.isZero()) {
					return left;
				}
				return EqGraph.replaceIfChanged(op, left, right);
			}
			case Node.POWERS: {
				Binary op = (Binary) node;
				node = EqGraph.eliminateZero(op.left);
				Z power = (Z) EqGraph.eliminateZero(op.right);
				if (node.isZero()) {
					return power.isZero() ? One.INSTANCE : node;
				} else if (power.isZero()) {
					return One.INSTANCE;
				} else if (power.isOne()) {
					return node;
				}
				return EqGraph.replaceIfChanged(op, node, power);
			}
			case Node.ASSINGS: {
				Binary op = (Binary) node;
				Node right = EqGraph.eliminateZero(op.right);
				return EqGraph.replaceIfChanged(op, op.left, right);
			}
			default:
				throw new IllegalArgumentException("unknown type of node="
						+ Node.typeName(node.what()));
			}
		}

		private static Node replaceIfChanged(Binary op, Node left, Node right) {
			if (EqGraph.eq(op.left, left) && EqGraph.eq(op.right, right)) {
				return op;
			}
			op = (Binary) op.clone();
			op.left = left;
			op.right = right;
			return op;
		}

		private static boolean eq(Object x, Object y) {
			if(x==y){
				return true;
			}else if(x!=null&&y!=null&&x.equals(y)){
				return true;
			}
			return false;
		}

		Node node;
		List<EqVertex> firsts;
		List<EqVertex> lasts;
		boolean geOne;

		public EqGraph(Node node) {
			this.node = node;
			this.firsts = new ArrayList<EqVertex>(1);
			this.lasts = new ArrayList<EqVertex>(1);
			this.geOne = false;
		}
	}

	public void test1() throws IOException {
		final Map<String, Variable> varMap = new TreeMap<String, Variable>();
		class Op {
			Variable v(String name) {
				return this.getVariable(name, true);
			}
			Variable getVariable(String name, boolean anyway) {
				Variable x = varMap.get(name);
				if (x == null && anyway) {
					x = new Variable(name);
					varMap.put(name, x);
				}
				return x;
			}
			Node[] modifyNodes(Node[] eqs) {
				return EqGraph.modifyNodes(eqs);
			}
		}
		Zero zero = Zero.INSTANCE;
		One one = One.INSTANCE;
		Op op = new Op();
		Node[] eqs = {
				op.v("E")
						.assigns(op.v("A").times(one.plus(op.v("e").times(op.v("E"))))),
				op.v("A")
						.assigns(one.plus(op.v("v").times(op.v("a").times(op.v("P"))))),
				op.v("P")
						.assigns(op.v("T").times(one.plus(op.v("p").times(op.v("P"))))),
				op.v("T")
						.assigns(op.v("W").times(one.plus(op.v("t").times(op.v("T"))))),
				op.v("W")
						.assigns(op.v("U").times(one.plus(op.v("w").times(op.v("d"))))),
				op.v("U").assigns(
						op.v("v").plus(op.v("l").times(op.v("P").times(op.v("r"))))),
				op.v("Test").assigns(
						one.times(op.v("Test").plus(zero).plus(op.v("Test").powers(3)))), };
		if (true) {
			eqs = op.modifyNodes(eqs);
			PrintWriter writer = new PrintWriter(System.out);
			writer.write("free variables = ");
			for (Var v : vertices) {
				if (v.equation == null) {
					writer.write(" " + v.variable.name);
				}
			}
			writer.write('\n');
			writer.flush();
		}
		if (true) {
			PrintWriter writer = new PrintWriter(System.out);
			for (Node node : eqs) {
				toString(writer, node);
				writer.write('\n');
				writer.flush();
			}
		}
	}
	static void toString(Appendable writer, Node node) throws IOException {
		if (node == null) {
			writer.append("null");
		} else {
			switch (node.what()) {
			case Node.ZERO:
				writer.append('0');
			break;
			case Node.ONE:
				writer.append('1');
			break;
			case Node.INTEGER:
				writer.append(Long.toString(((Z) node).value));
			break;
			case Node.VARIABLE:
				writer.append(((Variable) node).name);
			break;
			case Node.PLUS: {
				Binary op = (Binary) node;
				if (op.left.isPrimitive()) {
					toString(writer, op.left);
				} else {
					writer.append('(');
					toString(writer, op.left);
					writer.append(')');
				}
				writer.append(" + ");
				if (op.right.isPrimitive()) {
					toString(writer, op.right);
				} else {
					writer.append('(');
					toString(writer, op.right);
					writer.append(')');
				}
			}
			break;
			case Node.MULTIPLIES: {
				Binary op = (Binary) node;
				if (op.left.isPrimitive()) {
					toString(writer, op.left);
				} else {
					writer.append('(');
					toString(writer, op.left);
					writer.append(')');
				}
				writer.append(" ");
				if (op.right.isPrimitive()) {
					toString(writer, op.right);
				} else {
					writer.append('(');
					toString(writer, op.right);
					writer.append(')');
				}
			}
			break;
			case Node.POWERS: {
				Binary op = (Binary) node;
				if (op.left.isPrimitive()) {
					toString(writer, op.left);
				} else {
					writer.append('(');
					toString(writer, op.left);
					writer.append(')');
				}
				writer.append(" ^ ");
				if (op.right.isPrimitive()) {
					toString(writer, op.right);
				} else {
					writer.append('(');
					toString(writer, op.right);
					writer.append(')');
				}
			}
			break;
			case Node.ASSINGS: {
				Binary op = (Binary) node;
				toString(writer, op.left);
				writer.append(" = ");
				toString(writer, op.right);
			}
			break;
			default:
				throw new IllegalArgumentException("unknown type of node="
						+ Node.typeName(node.what()));
			}
		}
	}
}

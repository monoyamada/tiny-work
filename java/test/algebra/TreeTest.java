package algebra;

import tiny.lang.Messages;
import tiny.lang.ObjectHelper;
import base.TestBase;

interface Node {
	final int LONG_TYPE = 1;
	final int VARIABLE_TYPE = LONG_TYPE + 1;
	final int POWERS_TYPE = VARIABLE_TYPE + 1;
	final int TIMES_TYPE = POWERS_TYPE + 1;
	final int PLUS_TYPE = TIMES_TYPE + 1;

	final LongNode ZERO = new LongNode(0);
	final LongNode ONE = new LongNode(1);

	int type();
	int size();
	Node get(int index);
	boolean isZero();
	boolean isOne();
}

abstract class EmptyNode implements Node {
	@Override
	public int size() {
		return 0;
	}
	@Override
	public Node get(int index) {
		String msg = Messages.getIndexOutOfRange(0, index, 0);
		throw new IndexOutOfBoundsException(msg);
	}
	@Override
	public boolean isZero() {
		return false;
	}
	@Override
	public boolean isOne() {
		return false;
	}
}

class BinaryNode extends EmptyNode {
	final int type;
	final Node node0;
	final Node node1;

	BinaryNode(int type, Node node0, Node node1) {
		this.type = type;
		this.node0 = node0;
		this.node1 = node1;
	}
	@Override
	public int type() {
		return this.type;
	}
	@Override
	public int size() {
		return 2;
	}
	@Override
	public Node get(int index) {
		switch (index) {
		case 0:
			return this.node0;
		case 1:
			return this.node1;
		default:
			String msg = Messages.getIndexOutOfRange(0, index, 2);
			throw new IndexOutOfBoundsException(msg);
		}
	}
}

class VariableNode extends EmptyNode {
	final String value;

	VariableNode(String value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		return ObjectHelper.hashCode(this.value);
	}
	@Override
	public boolean equals(Object obj) {
		try {
			VariableNode y = (VariableNode) obj;
			return ObjectHelper.equals(this.value, y.value);
		} catch (Exception ex) {
			return false;
		}
	}
	@Override
	public int type() {
		return Node.VARIABLE_TYPE;
	}
	public String value() {
		return this.value;
	}
}

class LongNode extends EmptyNode {
	final long value;

	LongNode(long value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		return ObjectHelper.hashCode(this.value);
	}
	@Override
	public boolean equals(Object obj) {
		try {
			LongNode y = (LongNode) obj;
			return this.value == y.value;
		} catch (Exception ex) {
			return false;
		}
	}
	@Override
	public int type() {
		return Node.LONG_TYPE;
	}
	public long value() {
		return this.value;
	}
	@Override
	public boolean isZero() {
		return this.value == 0;
	}
	@Override
	public boolean isOne() {
		return this.value == 1;
	}
	public static Node newNode(long value) {
		if (value == 0) {
			return Node.ZERO;
		} else if (value == 1) {
			return Node.ONE;
		}
		return new LongNode(value);
	}
}

public class TreeTest extends TestBase {
	static String typeName(int type) {
		switch (type) {
		case Node.LONG_TYPE:
			return "long";
		case Node.VARIABLE_TYPE:
			return "variable";
		case Node.POWERS_TYPE:
			return "powers";
		case Node.TIMES_TYPE:
			return "times";
		case Node.PLUS_TYPE:
			return "plus";
		default:
			return "unknown type=" + type;
		}
	}
	static String asString(Node parent, Node child) {
		String text = asString(child);
		if (parent.type() < child.type()) {
			return "(" + text + ")";
		}
		return text;
	}
	static String asString(Node node) {
		switch (node.type()) {
		case Node.LONG_TYPE: {
			LongNode x = (LongNode) node;
			return Long.toString(x.value());
		}
		case Node.VARIABLE_TYPE: {
			VariableNode x = (VariableNode) node;
			return x.value();
		}
		case Node.POWERS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			return asString(x, x.node0) + " ^ " + asString(x, x.node1);
		}
		case Node.TIMES_TYPE: {
			BinaryNode x = (BinaryNode) node;
			return asString(x, x.node0) + " * " + asString(x, x.node1);
		}
		case Node.PLUS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			return asString(x, x.node0) + " + " + asString(x, x.node1);
		}
		default:
			String msg = typeName(node.type()) + " " + node.type();
			throw new IllegalArgumentException(msg);
		}
	}
	static VariableNode variable(String value) {
		return new VariableNode(value);
	}
	static Node minus(long x, Node y) {
		return plus(-x, y);
	}
	static Node minus(Node x, long y) {
		return plus(x, -y);
	}
	static Node minus(Node x, Node y) {
		return plus(x, times(-1, y));
	}
	static Node plus(Node x, Node y) {
		if (x.isZero()) {
			return y;
		} else if (y.isZero()) {
			return x;
		} else if (x.type() == Node.LONG_TYPE && y.type() == Node.LONG_TYPE) {
			LongNode xx = (LongNode) x;
			LongNode yy = (LongNode) y;
			return new LongNode(xx.value() + yy.value());
		}
		return new BinaryNode(Node.PLUS_TYPE, x, y);
	}
	static Node plus(long x, Node y) {
		return plus(y, x);
	}
	static Node plus(Node x, long y) {
		if (x.isZero()) {
			return LongNode.newNode(y);
		} else if (y == 0) {
			return x;
		} else if (x.type() == Node.LONG_TYPE) {
			LongNode xx = (LongNode) x;
			return LongNode.newNode(xx.value() + y);
		}
		return new BinaryNode(Node.PLUS_TYPE, x, LongNode.newNode(y));
	}
	static Node divides(Node x, long y) {
		return times(x, powers(y, -1));
	}
	static Node divides(Node x, Node y) {
		return times(x, powers(y, -1));
	}
	static Node times(long x, Node y) {
		return times(y, x);
	}
	static Node times(Node x, long y) {
		if (x.isZero()) {
			return x;
		} else if (y == 0) {
			return Node.ZERO;
		} else if (x.isOne()) {
			return LongNode.newNode(y);
		} else if (y == 1) {
			return x;
		} else if (x.type() == Node.LONG_TYPE) {
			LongNode xx = (LongNode) x;
			return LongNode.newNode(xx.value() * y);
		}
		return new BinaryNode(Node.TIMES_TYPE, x, LongNode.newNode(y));
	}
	static Node times(Node x, Node y) {
		if (x.isZero()) {
			return x;
		} else if (y.isZero()) {
			return y;
		} else if (x.isOne()) {
			return y;
		} else if (y.isOne()) {
			return x;
		} else if (x.type() == Node.LONG_TYPE && y.type() == Node.LONG_TYPE) {
			LongNode xx = (LongNode) x;
			LongNode yy = (LongNode) y;
			return LongNode.newNode(xx.value() * yy.value());
		}
		return new BinaryNode(Node.TIMES_TYPE, x, y);
	}
	static Node powers(long x, long y) {
		if (y == 0) {
			return Node.ONE;
		} else if (y == 1) {
			return LongNode.newNode(x);
		}
		return new BinaryNode(Node.POWERS_TYPE, LongNode.newNode(x),
				LongNode.newNode(y));
	}
	static Node powers(long x, Node y) {
		if (y.isZero()) {
			return Node.ONE;
		} else if (y.isOne()) {
			return LongNode.newNode(x);
		}
		return new BinaryNode(Node.POWERS_TYPE, LongNode.newNode(x), y);
	}
	static Node powers(Node x, long y) {
		if (y == 0) {
			return Node.ONE;
		} else if (y == 1) {
			return x;
		}
		return new BinaryNode(Node.POWERS_TYPE, x, LongNode.newNode(y));
	}
	static Node powers(Node x, Node y) {
		if (y.isZero()) {
			return Node.ONE;
		} else if (y.isOne()) {
			return x;
		}
		return new BinaryNode(Node.POWERS_TYPE, x, y);
	}

	static boolean isLongPowers(Node node) {
		switch (node.type()) {
		case Node.LONG_TYPE:
		case Node.VARIABLE_TYPE:
			return true;
		case Node.POWERS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			Node y = x.node1;
			return y.type() == Node.LONG_TYPE;
		}
		case Node.TIMES_TYPE:
		case Node.PLUS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			if (!isLongPowers(x.node0)) {
				return false;
			}
			return isLongPowers(x.node1);
		}
		default:
			String msg = typeName(node.type()) + " " + node.type();
			throw new IllegalArgumentException(msg);
		}
	}

	static Node derivative(Node node, VariableNode value) {
		return derivative(node, value.value());
	}
	static Node derivative(Node node, String value) {
		switch (node.type()) {
		case Node.LONG_TYPE:
			return Node.ZERO;
		case Node.VARIABLE_TYPE: {
			VariableNode x = (VariableNode) node;
			if (ObjectHelper.equals(x.value(), value)) {
				return Node.ONE;
			}
			return Node.ZERO;
		}
		case Node.POWERS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			Node x0 = derivative(x.node0, value);
			Node x1 = derivative(x.node1, value);
			if (!x1.isZero()) {
				throw new Error("not yet for powers becase it needs logarithm");
			}
			return times(times(x0, x.node1), powers(x.node0, plus(x.node1, -1)));
		}
		case Node.TIMES_TYPE: {
			BinaryNode x = (BinaryNode) node;
			Node x0 = derivative(x.node0, value);
			Node x1 = derivative(x.node1, value);
			return plus(times(x0, x.node1), times(x.node0, x1));
		}
		case Node.PLUS_TYPE: {
			BinaryNode x = (BinaryNode) node;
			Node x0 = derivative(x.node0, value);
			Node x1 = derivative(x.node1, value);
			return plus(x0, x1);
		}
		default:
			throw new IllegalArgumentException(typeName(node.type()));
		}
	}
	public void testNode() {
		{
			VariableNode x = variable("x");
			VariableNode y = variable("y");
			Node z = plus(powers(x, 2), times(x, y));
			System.out.println(asString(z));
			System.out.println(asString(derivative(z, x)));
		}
		{
			VariableNode k = variable("k");
			VariableNode y = variable("y");
			Node u = minus(divides(minus(k, 1), k), times(2, y));
			Node u_y = derivative(u, y);
			Node lnz_y = divides(divides(u, y), minus(1, y));
			Node lnz_yy = derivative(lnz_y, y);
			
			System.out.println(isLongPowers(powers (y, k)));
			System.out.println(isLongPowers(u_y));
			System.out.println(isLongPowers(lnz_y));
			System.out.println(isLongPowers(lnz_yy));
		}
		{
			VariableNode k = variable("k");
			VariableNode y = variable("y");
			Node u = minus(divides(minus(k, 1), k), times(2, y));
			Node u_y = derivative(u, y);
			Node lnz_y = divides(divides(u, y), minus(1, y));
			Node lnz_yy = derivative(lnz_y, y);

			System.out.println(asString(u_y));
			System.out.println(asString(lnz_y));
			System.out.println(asString(lnz_yy));
		}
	}
}

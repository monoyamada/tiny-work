package tree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import parser.ArrayStack;
import tiny.lang.Debug;
import junit.framework.TestCase;

public class TreeNodeTest extends TestCase {
	static class TestNode extends MutableNode<TestNode> {
		String name;

		public TestNode() {
		}
		public TestNode(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
		protected void setName(String name) {
			this.name = name;
		}
		public TestNode addChild(String name){
			TestNode node = new TestNode(name);
			this.addChild(node);
			return node;
		}
	}

	static void printTree(TreeNode root) throws IOException {
		Writer writer = new PrintWriter(System.out);
		writeTree(writer, root);
	}
	static Writer writeTree(Writer writer, TreeNode root) throws IOException {
		ArrayStack<TreeNode> stack = new ArrayStack<TreeNode>();
		writePreNode(writer, root);
		TreeNode node = root.getFirstChild();
		while (node != null) {
			writePreNode(writer, node);
			TreeNode next = node.getFirstChild();
			if (next != null) {
				stack.pushValue(node);
				node = next;
				continue;
			}
			writePostNode(writer, node);
			node = node.getNext();
			if (node == null && 0 < stack.size()) {
				node = stack.peekValue(null);
				stack.pop();
				writePostNode(writer, node);
				node = node.getNext();
			}
		}
		writePostNode(writer, root);
		writer.flush();
		return writer;
	}
	private static void writePreNode(Writer writer, TreeNode node)
			throws IOException {
		if (node instanceof TestNode) {
			TestNode x = (TestNode) node;
			writer.write(x.name);
		} else {
			writer.write(node.toString());
		}
		writer.write('[');
	}
	private static void writePostNode(Writer writer, TreeNode node)
			throws IOException {
		writer.write(']');
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testWriteTree() throws IOException {
		TestNode root = new TestNode("a");
		TestNode b = root.addChild("b");
		root.addChild("c");
		b.addChild("d");
		printTree(root);
	}
}

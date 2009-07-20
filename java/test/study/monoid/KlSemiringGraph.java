package study.monoid;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import study.lang.ArrayHelper;
import study.monoid.KlSemiringFactory.IfNode;
import study.monoid.KlSemiringFactory.Multiplies;
import study.monoid.KlSemiringFactory.One;
import study.monoid.KlSemiringFactory.Plus;
import study.monoid.KlSemiringFactory.Stars;
import study.monoid.KlSemiringFactory.Symbol;
import study.monoid.KlSemiringFactory.Zero;

public class KlSemiringGraph {
	public static final int BEGIN_NODE = 0;
	public static final int END_NODE = BEGIN_NODE + 1;
	public static final int SYMBOL_NODE = END_NODE + 1;
	public static final String BEGIN_NODE_SYMBOL = "Begin";
	public static final String END_NODE_SYMBOL = "End";

	public static class GraphNode extends EmptyTreeNode<GraphNode> {
		public static final GraphNode[] EMPTY_ARRAY = {};

		protected static void toStringNextIndexArray(StringBuilder output,
				GraphNode node) {
			final GraphNode[] nexts = node.getChildArray();
			output.append('[');
			for (int i = 0, n = nexts.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(nexts[i].getIndex());
			}
			output.append(']');
		}
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			buffer.append(this.getIndex());
			buffer.append(':');
			buffer.append(this.getValue());
			GraphNode.toStringNextIndexArray(buffer, this);
			return buffer.toString();
		}
		public int getNodeType() {
			return KlSemiringGraph.END_NODE;
		}
		public int getIndex() {
			return KlSemiringGraph.END_NODE;
		}
		public String getValue() {
			return KlSemiringGraph.END_NODE_SYMBOL;
		}
		public GraphNode[] getChildArray() {
			return GraphNode.EMPTY_ARRAY;
		}
	}

	public static class EndNode extends GraphNode {
	}

	public static class BeginNode extends GraphNode {
		private final GraphNode[] childs;

		public BeginNode() {
			this.childs = GraphNode.EMPTY_ARRAY;
		}
		public BeginNode(GraphNode[] childs) {
			this.childs = childs;
		}
		@Override
		public int getNodeType() {
			return KlSemiringGraph.BEGIN_NODE;
		}
		@Override
		public int getIndex() {
			return KlSemiringGraph.BEGIN_NODE;
		}
		@Override
		public String getValue() {
			return KlSemiringGraph.BEGIN_NODE_SYMBOL;
		}
		@Override
		public GraphNode[] getChildArray() {
			return this.childs;
		}
		@Override
		public int getChildSize() {
			return this.getChildArray().length;
		}
		@Override
		protected GraphNode doGetChild(int index) {
			return this.getChildArray()[index];
		}
	}

	public static class SymbolNode extends BeginNode {
		private final int index;
		private final String value;

		public SymbolNode(int index, String value) {
			this.index = index;
			this.value = value;
		}
		public SymbolNode(int index, String value, GraphNode[] childs) {
			super(childs);
			this.index = index;
			this.value = value;
		}
		/**
		 * @return the index
		 */
		public int getIndex() {
			return this.index;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}
		public int getNodeType() {
			return KlSemiringGraph.SYMBOL_NODE;
		}
	}

	protected static class GraphData {
		private List<String> nodes;
		private List<int[]> nexts;
		private BitSet nextEnds;
		private int[] begins;
		private int[] ends;
		public boolean zero;
		public boolean geOne;

		public GraphData() {
		}
		protected GraphData(List<String> nodes, List<int[]> nexts, BitSet nextEnds) {
			assert nodes != null && nexts != null && nextEnds != null;
			this.nodes = nodes;
			this.nexts = nexts;
			this.nextEnds = nextEnds;
		}
		public GraphData newData() {
			return new GraphData(this.getNodes(), this.getNexts(), this.getNextEnds());
		}
		/**
		 * @return the nodes
		 */
		public List<String> getNodes() {
			if (this.nodes == null) {
				this.nodes = this.newNodes();
			}
			return this.nodes;
		}
		protected List<String> newNodes() {
			return new ArrayList<String>();
		}
		/**
		 * @return the edges
		 */
		protected List<int[]> getNexts() {
			if (this.nexts == null) {
				this.nexts = this.newNexts();
			}
			return this.nexts;
		}
		protected List<int[]> newNexts() {
			return new ArrayList<int[]>();
		}
		/**
		 * @return the nextEnds
		 */
		protected BitSet getNextEnds() {
			if (this.nextEnds == null) {
				this.nextEnds = this.newNextEnds();
			}
			return this.nextEnds;
		}
		protected BitSet newNextEnds() {
			return new BitSet();
		}
		/**
		 * @return the begins
		 */
		protected int[] getBegins() {
			if (this.begins == null) {
				this.begins = ArrayHelper.EMPTY_INT_ARRAY;
			}
			return this.begins;
		}
		/**
		 * @param begins
		 *          the begins to set
		 */
		protected void setBegins(int[] begins) {
			this.begins = begins;
		}
		/**
		 * @return the ends
		 */
		protected int[] getEnds() {
			if (this.ends == null) {
				this.ends = ArrayHelper.EMPTY_INT_ARRAY;
			}
			return this.ends;
		}
		/**
		 * @param ends
		 *          the ends to set
		 */
		protected void setEnds(int[] ends) {
			this.ends = ends;
		}
	}

	/**
	 * Builds NFA from the given {@link GraphData}.
	 *
	 * @author shirakata
	 *
	 */
	protected static class GraphBuilder {
		public GraphNode[] getGraphNodes(GraphData data) {
			assert data != null;
			final List<String> nodes = data.getNodes();
			final int nSymbol = nodes.size();
			final GraphNode beginNode = this.newBeginNode(data);
			final GraphNode endNode = this.newEndNode();
			final GraphNode[] output = new GraphNode[nSymbol];
			output[KlSemiringGraph.BEGIN_NODE] = beginNode;
			output[KlSemiringGraph.END_NODE] = endNode;
			for (int i = KlSemiringGraph.SYMBOL_NODE; i < nSymbol; ++i) {
				output[i] = this.newSymbolNode(data, i);
			}
			for (int i = KlSemiringGraph.SYMBOL_NODE; i < nSymbol; ++i) {
				final GraphNode node = output[i];
				final int[] nexts = data.getNexts().get(i);
				for (int ii = 0, nn = nexts.length; ii < nn; ++ii) {
					node.getChildArray()[ii] = output[nexts[ii]];
				}
				if (data.getNextEnds().get(i)) {
					node.getChildArray()[nexts.length] = endNode;
				}
			}
			final int[] nexts = data.getBegins();
			for (int i = 0, n = nexts.length; i < n; ++i) {
				beginNode.getChildArray()[i] = output[nexts[i]];
			}
			if (data.geOne) {
				beginNode.getChildArray()[nexts.length] = endNode;
			}
			return output;
		}
		protected GraphNode newSymbolNode(GraphData data, int index) {
			final List<String> values = data.getNodes();
			final int[] nexts = data.getNexts().get(index);
			final boolean end = data.getNextEnds().get(index);
			final GraphNode[] nodes = end ? new GraphNode[nexts.length + 1]
					: new GraphNode[nexts.length];
			return new SymbolNode(index, values.get(index), nodes);
		}
		protected GraphNode newBeginNode(GraphData data) {
			final int[] nexts = data.getBegins();
			final GraphNode[] nodes = data.geOne ? new GraphNode[nexts.length + 1]
					: new GraphNode[nexts.length];
			return new BeginNode(nodes);
		}
		protected GraphNode newEndNode() {
			return new EndNode();
		}
	}

	protected GraphData newGraphData(IfNode node) {
		assert node != null;
		final GraphData data = new GraphData();
		{
			final int index = this.addSymbol(data, KlSemiringGraph.BEGIN_NODE_SYMBOL);
			data.getNextEnds().set(index, true);
			data.getNextEnds().set(KlSemiringGraph.BEGIN_NODE);
		}
		{
			final int index = this.addSymbol(data, KlSemiringGraph.END_NODE_SYMBOL);
			data.getNextEnds().set(index, true);
		}
		data.geOne = true;
		data.zero = false;
		this.getGraphData(data, node);
		return data;
	}
	protected void getGraphData(GraphData data, IfNode node) {
		switch (node.getNodeType()) {
		case KlSemiringFactory.ZERO:
			this.getGraphDataZero(data, (Zero) node);
			break;
		case KlSemiringFactory.ONE:
			this.getGraphDataOne(data, (One) node);
			break;
		case KlSemiringFactory.SYMBOL:
			this.getGraphDataSymbol(data, (Symbol) node);
			break;
		case KlSemiringFactory.STARS:
			this.getGraphDataStars(data, (Stars) node);
			break;
		case KlSemiringFactory.MULTIPLIES:
			this.getGraphDataMultiplies(data, (Multiplies) node);
			break;
		case KlSemiringFactory.PLUS:
			this.getGraphDataPlus(data, (Plus) node);
			break;
		default:
			throw new IllegalArgumentException("uknown node=" + node);
		}
	}
	protected void getGraphDataPlus(GraphData data, Plus node) {
		final GraphData newData0 = data.newData();
		this.getGraphData(newData0, node.getChild0());
		final GraphData newData1 = data.newData();
		this.getGraphData(newData1, node.getChild1());
		if (newData0.zero || newData1.zero) {
			data.zero = true;
			return;
		} else if (newData0.zero) {
			data.setBegins(newData1.getBegins());
			data.setEnds(newData1.getEnds());
			data.geOne = newData1.geOne;
			return;
		} else if (newData1.zero) {
			data.setBegins(newData0.getBegins());
			data.setEnds(newData0.getEnds());
			data.geOne = newData0.geOne;
			return;
		}
		final int[] begin0 = newData0.getBegins();
		final int[] end0 = newData0.getEnds();
		final int[] begin1 = newData1.getBegins();
		final int[] end1 = newData1.getEnds();
		data.setBegins(this.addAll(begin0, begin1));
		data.setEnds(this.addAll(end0, end1));
		data.geOne = newData0.geOne || newData1.geOne;
	}
	protected void getGraphDataMultiplies(GraphData data, Multiplies node) {
		final GraphData newData0 = data.newData();
		this.getGraphData(newData0, node.getChild0());
		final GraphData newData1 = data.newData();
		this.getGraphData(newData1, node.getChild1());
		if (newData0.zero || newData1.zero) {
			data.zero = true;
			return;
		}
		final int[] begin0 = newData0.getBegins();
		final int[] end0 = newData0.getEnds();
		final int[] begin1 = newData1.getBegins();
		final int[] end1 = newData1.getEnds();
		if (newData0.geOne && newData1.geOne) {
			data.setBegins(this.addAll(begin0, begin1));
			data.setEnds(this.addAll(end0, end1));
			data.geOne = true;
		} else if (newData0.geOne) {
			data.setBegins(this.addAll(begin0, begin1));
			data.setEnds(end1);
			data.geOne = false;
			for (int iEnd = 0, nEnd = end0.length; iEnd < nEnd; ++iEnd) {
				final int end = end0[iEnd];
				data.getNextEnds().set(end, false);
			}
		} else if (newData1.geOne) {
			data.setBegins(begin0);
			data.setEnds(this.addAll(end0, end1));
			data.geOne = false;
		} else {
			data.setBegins(begin0);
			data.setEnds(end1);
			data.geOne = false;
			for (int iEnd = 0, nEnd = end0.length; iEnd < nEnd; ++iEnd) {
				final int end = end0[iEnd];
				data.getNextEnds().set(end, false);
			}
		}
		final List<int[]> nexts = data.getNexts();
		for (int iEnd = 0, nEnd = end0.length; iEnd < nEnd; ++iEnd) {
			final int end = end0[iEnd];
			nexts.set(end, this.addAll(nexts.get(end), begin1));
		}
		data.getNexts();
	}
	protected void getGraphDataStars(GraphData data, Stars node) {
		final GraphData newData = data.newData();
		this.getGraphData(newData, node.getChild());
		data.setBegins(newData.getBegins());
		data.setEnds(newData.getEnds());
		data.geOne = true;
		final List<int[]> nexts = data.getNexts();
		final int[] begins = newData.getBegins();
		final int[] ends = newData.getEnds();
		for (int iEnd = 0, nEnd = ends.length; iEnd < nEnd; ++iEnd) {
			final int end = ends[iEnd];
			nexts.set(end, this.addAll(nexts.get(end), begins));
		}
	}
	protected void getGraphDataSymbol(GraphData data, Symbol node) {
		final int index = this.addSymbol(data, node.getValue());
		final int[] indices = { index };
		data.setBegins(indices);
		data.setEnds(indices);
		data.getNextEnds().set(index, true);
		data.geOne = false;
		data.zero = false;
	}
	protected int addSymbol(GraphData data, String value) {
		final int index = data.getNodes().size();
		data.getNodes().add(value);
		data.getNexts().add(ArrayHelper.EMPTY_INT_ARRAY);
		return index;
	}
	protected void getGraphDataOne(GraphData data, One node) {
		data.setBegins(null);
		data.setEnds(null);
		data.geOne = true;
		data.zero = false;
	}
	protected void getGraphDataZero(GraphData data, Zero node) {
		data.setBegins(null);
		data.setEnds(null);
		data.geOne = false;
		data.zero = true;
	}
	protected int[] addAll(int[] x, int[] y) {
		if (x == null || x.length < 1) {
			return y == null ? ArrayHelper.EMPTY_INT_ARRAY : y;
		} else if (y == null || y.length < 1) {
			return x == null ? ArrayHelper.EMPTY_INT_ARRAY : x;
		}
		return ArrayHelper.addAll(x, y);
	}
}

package study.monoid;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import study.lang.ArrayHelper;
import study.lang.StringHelper;
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

		private final GraphData graphData;
		private final GraphNode[] nodeArray;
		private final int nodeIndex;

		protected GraphNode(GraphData graphData, GraphNode[] nodeArray,
				int nodeIndex) {
			this.graphData = graphData;
			this.nodeArray = nodeArray;
			this.nodeIndex = nodeIndex;
		}
		public String toString() {
			final GraphData data = this.getGraphData();
			final int index = this.getNodeIndex();
			final int[] nexts = data.getNexts(index);
			final StringBuilder buffer = new StringBuilder();
			buffer.append(this.getNodeIndex());
			buffer.append(':');
			buffer.append(this.getValue());
			buffer.append('[');
			StringHelper.join(buffer, nexts, ", ");
			buffer.append(']');
			return buffer.toString();
		}
		/**
		 * @return the graphData
		 */
		protected GraphData getGraphData() {
			return this.graphData;
		}
		/**
		 * @return the nodeArray
		 */
		protected GraphNode[] getNodeArray() {
			return this.nodeArray;
		}
		public int getNodeType() {
			switch (this.nodeIndex) {
			case KlSemiringGraph.BEGIN_NODE:
			case KlSemiringGraph.END_NODE:
				return this.nodeIndex;
			default:
				return KlSemiringGraph.SYMBOL_NODE;
			}
		}
		public int getNodeIndex() {
			return this.nodeIndex;
		}
		public String getValue() {
			switch (this.getNodeIndex()) {
			case KlSemiringGraph.BEGIN_NODE:
				return KlSemiringGraph.BEGIN_NODE_SYMBOL;
			case KlSemiringGraph.END_NODE:
				return KlSemiringGraph.END_NODE_SYMBOL;
			default:
				return this.getGraphData().getSymbol(this.getNodeIndex());
			}
		}
		protected int[] getNexts() {
			return this.getGraphData().getNexts(this.getNodeIndex());
		}
		@Override
		public int getChildSize() {
			return this.getNexts().length;
		}
		@Override
		protected GraphNode doGetChild(int index) {
			final GraphNode[] nodes = this.getNodeArray();
			GraphNode node = nodes[this.getNexts()[index]];
			if (node == null) {
				node = this.newNode(index);
			}
			return node;
		}
		protected GraphNode newNode(int index) {
			return new GraphNode(this.getGraphData(), this.getNodeArray(), index);
		}
	}

	protected static class GraphData {
		private final String[] symbols;
		private final int[][] nexts;

		protected GraphData(String[] symbols, int[][] nexts) {
			this.symbols = symbols;
			this.nexts = nexts;
		}
		/**
		 * @return the symbols
		 */
		public String[] getSymbols() {
			return this.symbols;
		}
		public String getSymbol(int index) {
			return this.symbols[index];
		}
		/**
		 * @return the nexts
		 */
		public int[][] getNexts() {
			return this.nexts;
		}
		public int[] getNexts(int index) {
			return this.nexts[index];
		}
		public GraphNode[] getGraphNodes() {
			final int n = this.getSymbols().length;
			final GraphNode[] array = new GraphNode[n];
			for (int i = 0; i < n; ++i) {
				array[i] = new GraphNode(this, array, i);
			}
			return array;
		}
	}

	protected static class GraphData_0 {
		private List<String> symbols;
		private List<int[]> nexts;
		private BitSet nextEnds;
		private int[] begins;
		private int[] ends;
		public boolean zero;
		public boolean geOne;

		protected GraphData_0() {
		}
		protected GraphData_0(List<String> nodes, List<int[]> nexts, BitSet nextEnds) {
			assert nodes != null && nexts != null && nextEnds != null;
			this.symbols = nodes;
			this.nexts = nexts;
			this.nextEnds = nextEnds;
		}
		public GraphData_0 newData() {
			return new GraphData_0(this.getSymbols(), this.getNexts(), this
					.getNextEnds());
		}
		/**
		 * @return the nodes
		 */
		public List<String> getSymbols() {
			if (this.symbols == null) {
				this.symbols = this.newSymbols();
			}
			return this.symbols;
		}
		protected List<String> newSymbols() {
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

	protected GraphData newGraphData(IfNode node) {
		assert node != null;
		final GraphData_0 data = new GraphData_0();
		this.addSymbol(data, KlSemiringGraph.BEGIN_NODE_SYMBOL);
		this.addSymbol(data, KlSemiringGraph.END_NODE_SYMBOL);
		this.getGraphData(data, node);

		final List<String> list = data.getSymbols();
		final List<int[]> nexts = data.getNexts();
		final int[] begins = data.getBegins();
		final BitSet ends = data.getNextEnds();
		final int n = list.size();

		final String[] newSymbols = list.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		final int[][] newNexts = new int[n][];
		for (int i = 0; i < n; ++i) {
			final int[] inds = nexts.get(i);
			if (ends.get(i)) {
				newNexts[i] = ArrayHelper.add(inds, KlSemiringGraph.END_NODE);
			} else {
				newNexts[i] = inds;
			}
		}
		if (data.zero) {
		} else if (data.geOne) {
			newNexts[KlSemiringGraph.BEGIN_NODE] = ArrayHelper.add(begins,
					KlSemiringGraph.END_NODE);
		} else {
			newNexts[KlSemiringGraph.BEGIN_NODE] = begins;
		}
		return new GraphData(newSymbols, newNexts);
	}
	protected void getGraphData(GraphData_0 data, IfNode node) {
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
	protected void getGraphDataPlus(GraphData_0 data, Plus node) {
		final GraphData_0 newData0 = data.newData();
		this.getGraphData(newData0, node.getChild0());
		final GraphData_0 newData1 = data.newData();
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
	protected void getGraphDataMultiplies(GraphData_0 data, Multiplies node) {
		final GraphData_0 newData0 = data.newData();
		this.getGraphData(newData0, node.getChild0());
		final GraphData_0 newData1 = data.newData();
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
	protected void getGraphDataStars(GraphData_0 data, Stars node) {
		final GraphData_0 newData = data.newData();
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
	protected void getGraphDataSymbol(GraphData_0 data, Symbol node) {
		final int index = this.addSymbol(data, node.getValue());
		final int[] indices = { index };
		data.setBegins(indices);
		data.setEnds(indices);
		data.getNextEnds().set(index, true);
		data.geOne = false;
		data.zero = false;
	}
	protected int addSymbol(GraphData_0 data, String value) {
		final int index = data.getSymbols().size();
		data.getSymbols().add(value);
		data.getNexts().add(ArrayHelper.EMPTY_INT_ARRAY);
		return index;
	}
	protected void getGraphDataOne(GraphData_0 data, One node) {
		data.setBegins(null);
		data.setEnds(null);
		data.geOne = true;
		data.zero = false;
	}
	protected void getGraphDataZero(GraphData_0 data, Zero node) {
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
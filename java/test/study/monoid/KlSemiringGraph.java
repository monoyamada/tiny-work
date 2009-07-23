package study.monoid;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import study.function.LexicographicalOrder;
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
	public static final String BEGIN_NODE_SYMBOL = "{";
	public static final String END_NODE_SYMBOL = "}";
	public static final int[][] EMPTY_INT2_ARRAY = { {} };
	public static final String NODE_EXPRESSION_PREFIX = "$";

	protected static void toString(StringBuilder output, int[] array) {
		output.append('[');
		StringHelper.join(output, array, ", ");
		output.append(']');
	}
	protected static void toString(StringBuilder output, Object[] array) {
		output.append('[');
		StringHelper.join(output, array, ", ");
		output.append(']');
	}

	protected static class GraphData {
		private final String[] symbols;
		private final int[][] nexts;

		protected GraphData(GraphData other) {
			this(other.symbols, other.nexts);
		}
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
		public String getSymbol(int nodeIndex) {
			return this.symbols[nodeIndex];
		}
		/**
		 * @return the nexts
		 */
		public int[][] getNexts() {
			return this.nexts;
		}
		public int[] getNexts(int nodeIndex) {
			return this.nexts[nodeIndex];
		}
	}

	public static class NfaNode extends EmptyTreeNode<NfaNode> {
		public static final NfaNode[] EMPTY_ARRAY = {};

		private final NfaBuilder builder;
		private final int nodeIndex;

		protected NfaNode(NfaBuilder builder, int nodeIndex) {
			this.builder = builder;
			this.nodeIndex = nodeIndex;
		}
		public String toString() {
			final NfaBuilder builder = this.getBuilder();
			final int index = this.getNodeIndex();
			final int[] nexts = builder.getNexts(index);
			final StringBuilder buffer = new StringBuilder();
			buffer.append(index);
			buffer.append(':');
			buffer.append(this.getValue());
			KlSemiringGraph.toString(buffer, nexts);
			return buffer.toString();
		}
		/**
		 * @return the graphData
		 */
		protected NfaBuilder getBuilder() {
			return this.builder;
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
			return this.getBuilder().getSymbol(this.getNodeIndex());
		}
		protected int[] getNexts() {
			return this.getBuilder().getNexts(this.getNodeIndex());
		}
		@Override
		public int getChildSize() {
			return this.getNexts().length;
		}
		@Override
		protected NfaNode doGetChild(int index) {
			return this.getBuilder().getNode(this.getNexts()[index]);
		}
	}

	protected static class NfaBuilder extends GraphData {
		private NfaNode[] nodeArray;

		public NfaBuilder(GraphData other) {
			super(other);
		}
		protected NfaNode[] getNodeArray() {
			if (this.nodeArray == null) {
				this.nodeArray = this.newNodeArray();
			}
			return this.nodeArray;
		}
		protected NfaNode[] newNodeArray() {
			return new NfaNode[this.getSymbols().length];
		}
		public int getNodeSize() {
			return this.getNodeArray().length;
		}
		/**
		 * Creates graph with lazy.
		 *
		 * @return
		 */
		public NfaNode getBeginNode() {
			return this.getNode(KlSemiringGraph.BEGIN_NODE);
		}
		/**
		 * Create graph with lazy.
		 *
		 * @param nodeIndex
		 * @return
		 */
		public NfaNode getNode(int nodeIndex) {
			final NfaNode[] array = this.getNodeArray();
			NfaNode node = array[nodeIndex];
			if (node == null) {
				node = this.newNode(nodeIndex);
				array[nodeIndex] = node;
			}
			return node;
		}
		/**
		 * Creates entire graph.
		 *
		 * @return
		 */
		public NfaNode[] getNodes() {
			final NfaNode[] array = this.getNodeArray();
			for (int i = 0, n = array.length; i < n; ++i) {
				if (array[i] == null) {
					array[i] = this.newNode(i);
				}
			}
			return array;
		}
		protected NfaNode newNode(int nodeIndex) {
			return new NfaNode(this, nodeIndex);
		}
	}

	protected static class DfaNext {
		public static final DfaNext[] EMPTY_ARRAY = {};
		public static final int SIMPLE_NEXT = 0;
		private final int symbolIndex;
		private final DfaNode node;

		protected DfaNext(DfaNode node, int symbolIndex) {
			this.node = node;
			this.symbolIndex = symbolIndex;
		}
		public int getNextType() {
			return DfaNext.SIMPLE_NEXT;
		}
		/**
		 * @return the next
		 */
		public DfaNode getNode() {
			return this.node;
		}
		public int getNodeIndex() {
			return this.getNode().getNodeIndex();
		}
		/**
		 * @return the symbolIndex
		 */
		protected int getSymbolIndex() {
			return this.symbolIndex;
		}
		public String getSymbol(DfaBuilder builder) {
			return builder.getSymbolSet()[this.getSymbolIndex()];
		}
		protected void toString(StringBuilder output, DfaBuilder builder) {
			output.append(this.getSymbol(builder));
			output.append(':');
			output.append(this.getNode().getNodeIndex());
		}
	}

	public static class DfaNode extends EmptyTreeNode<DfaNode> {
		private static final DfaNode[] EMPTY_ARRAY = {};
		private final DfaBuilder builder;
		private final int nodeIndex;
		private final int[] state;
		private DfaNext[] nextArray;

		protected DfaNode(DfaBuilder builder, int nodeIndex, int[] state) {
			this.builder = builder;
			this.nodeIndex = nodeIndex;
			this.state = state;
		}
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			this.toString(buffer);
			return buffer.toString();
		}
		protected void toString(StringBuilder output) {
			final DfaBuilder builder = this.getBuilder();
			final int[] state = this.getState();
			final DfaNext[] nexts = this.getNextArray();
			output.append(this.getNodeIndex());
			output.append('[');
			for (int i = 0, n = state.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(builder.getSymbol(state[i]));
			}
			output.append(']');
			output.append('[');
			for (int i = 0, n = nexts.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				nexts[i].toString(output, builder);
			}
			output.append(']');
		}
		/**
		 * @return the builder
		 */
		protected DfaBuilder getBuilder() {
			return this.builder;
		}
		/**
		 * @return the nodeIndex
		 */
		public int getNodeIndex() {
			return this.nodeIndex;
		}
		/**
		 * @return the state
		 */
		protected int[] getState() {
			return this.state;
		}
		@Override
		public int getChildSize() {
			return this.getNextArray().length;
		}
		@Override
		protected DfaNode doGetChild(int index) {
			return this.getNextArray()[index].getNode();
		}
		/**
		 * @return the nextArray
		 */
		protected DfaNext[] getNextArray() {
			if (this.nextArray == null) {
				this.nextArray = this.newNextArray();
			}
			return this.nextArray;
		}
		protected DfaNext[] newNextArray() {
			final DfaBuilder builder = this.getBuilder();
			final int[] state = this.getState();
			final int[] symbols = builder.getAuxArray1(state.length);
			final int[][] states = builder.getAuxArray2(state.length);
			int count = 0;
			for (int i = 0, n = state.length; i < n; ++i) {
				final int node = state[i];
				final int[] nexts = builder.getNexts(node);
				for (int ii = 0, nn = nexts.length; ii < nn; ++ii) {
					final int next = nexts[ii];
					final int symbol = builder.getSymbolIndex(next);
					final int ind = ArrayHelper.indexOf(symbols, 0, count, symbol);
					if (ind < 0) {
						symbols[count] = symbol;
						states[count] = new int[] { next };
						++count;
					} else {
						states[ind] = ArrayHelper.add(states[ind], next);
					}
				}
			}

			if (count < 1) {
				return DfaNext.EMPTY_ARRAY;
			}

			final DfaNext[] array = new DfaNext[count];
			for (int i = 0; i < count; ++i) {
				final int[] next = states[i];
				if (1 < next.length) {
					Arrays.sort(next);
				}
				final DfaNode node = builder.getNode(next, true);
				array[i] = builder.newNext(node, symbols[i]);
			}
			return array;
		}
	}

	protected static class DfaBuilder extends GraphData {
		private int[] symbolIndexArray;
		private String[] symbolSet;
		private Map<int[], DfaNode> stateMap;
		private DfaNode beginNode;
		private DfaNode endNode;
		private int[] auxArray1;
		private int[][] auxArray2;
		private DfaNode[] nodes;
		private Comparator<? super int[]> stateOrder;

		protected DfaBuilder(GraphData other) {
			super(other);
		}
		protected int getSymbolIndex(int nodeIndex) {
			return this.getSymbolIndexArray()[nodeIndex];
		}
		protected int[] getSymbolIndexArray() {
			if (this.symbolIndexArray == null) {
				this.constructSymbolSet();
			}
			return this.symbolIndexArray;
		}
		/**
		 * @param symbolIndexArray
		 *          the symbolIndexArray to set
		 */
		protected void setSymbolIndexArray(int[] symbolIndexArray) {
			this.symbolIndexArray = symbolIndexArray;
		}
		/**
		 * @return the symbolSet
		 */
		protected String[] getSymbolSet() {
			if (this.symbolSet == null) {
				this.constructSymbolSet();
			}
			return this.symbolSet;
		}
		/**
		 * @param symbolSet
		 *          the symbolSet to set
		 */
		protected void setSymbolSet(String[] symbolSet) {
			this.symbolSet = symbolSet;
		}
		protected void constructSymbolSet() {
			final String[] symbols = this.getSymbols();
			final int[] array = new int[symbols.length];
			final Map<String, Number> symbolMap = new TreeMap<String, Number>();
			int count = 0;
			ArrayHelper.putAll(symbolMap, symbols, null);
			for (Iterator<Entry<String, Number>> p = symbolMap.entrySet().iterator(); p
					.hasNext();) {
				final Entry<String, Number> ent = p.next();
				ent.setValue(Integer.valueOf(count++));
			}
			for (int i = 0, n = symbols.length; i < n; ++i) {
				array[i] = symbolMap.get(symbols[i]).intValue();
			}
			this.setSymbolIndexArray(array);
			this.setSymbolSet(symbolMap.keySet().toArray(
					ArrayHelper.EMPTY_STRING_ARRAY));
		}
		protected Map<int[], DfaNode> getStateMap() {
			if (this.stateMap == null) {
				this.stateMap = this.newStateMap();
			}
			return this.stateMap;
		}
		protected Map<int[], DfaNode> newStateMap() {
			return new TreeMap<int[], DfaNode>(this.getStateOrder());
		}
		protected Comparator<? super int[]> getStateOrder() {
			if (this.stateOrder == null) {
				this.stateOrder = this.newStateOrder();
			}
			return this.stateOrder;
		}
		protected Comparator<? super int[]> newStateOrder() {
			return new Comparator<int[]>() {
				@Override
				public int compare(int[] o1, int[] o2) {
					return LexicographicalOrder.compare(o1, o2);
				}
			};
		}
		protected DfaNode getNode(int[] next, boolean anyway) {
			final Map<int[], DfaNode> map = this.getStateMap();
			DfaNode node = map.get(next);
			if (node == null && true) {
				node = this.newNode(map.size(), next);
				map.put(next, node);
			}
			return node;
		}
		protected DfaNode newNode(int nodeIndex, int[] state) {
			return new DfaNode(this, nodeIndex, state);
		}
		public DfaNode[] getNodes() {
			if (this.nodes == null) {
				this.nodes = this.newNodes();
			}
			return this.nodes;
		}
		/**
		 * Creates entire graph.
		 *
		 * @return
		 */
		protected DfaNode[] newNodes() {
			final TreeMap<int[], DfaNode> stack = new TreeMap<int[], DfaNode>(this
					.getStateOrder());
			final TreeMap<int[], DfaNode> done = new TreeMap<int[], DfaNode>(this
					.getStateOrder());
			DfaNode node = this.getBeginNode();
			stack.put(node.getState(), node);
			while (0 < stack.size()) {
				final Entry<int[], DfaNode> ent = stack.firstEntry();
				node = ent.getValue();
				stack.remove(ent.getKey());
				final DfaNext[] nexts = node.getNextArray();
				for (int i = 0, n = nexts.length; i < n; ++i) {
					final DfaNode next = nexts[i].getNode();
					if (done.get(next.getState()) == null) {
						stack.put(next.getState(), next);
					}
				}
				done.put(node.getState(), node);
			}
			return done.values().toArray(DfaNode.EMPTY_ARRAY);
		}
		/**
		 * Creates graph with lazy.
		 *
		 * @return
		 */
		public DfaNode getBeginNode() {
			if (this.beginNode == null) {
				this.constructTerminalNodes();
			}
			return this.beginNode;
		}
		/**
		 * @param beginNode
		 *          the beginNode to set
		 */
		protected void setBeginNode(DfaNode beginNode) {
			this.beginNode = beginNode;
		}
		/**
		 * @return the endNode
		 */
		protected DfaNode getEndNode() {
			if (this.endNode == null) {
				this.constructTerminalNodes();
			}
			return this.endNode;
		}
		/**
		 * @param endNode
		 *          the endNode to set
		 */
		protected void setEndNode(DfaNode endNode) {
			this.endNode = endNode;
		}
		protected void constructTerminalNodes() {
			{
				final int index = KlSemiringGraph.BEGIN_NODE;
				final int[] state = new int[] { index };
				final DfaNode node = this.getNode(state, true);
				this.setBeginNode(node);
			}
			{
				final int index = KlSemiringGraph.END_NODE;
				final int[] state = new int[] { index };
				final DfaNode node = this.getNode(state, true);
				this.setEndNode(node);
			}
		}
		protected int[] getAuxArray1(int length) {
			if (this.auxArray1 == null || this.auxArray1.length < length) {
				this.auxArray1 = new int[this.getCapacity(length)];
			}
			return this.auxArray1;
		}
		protected int[][] getAuxArray2(int length) {
			if (this.auxArray2 == null || this.auxArray2.length < length) {
				this.auxArray2 = new int[this.getCapacity(length)][];
			}
			return this.auxArray2;
		}
		protected int getCapacity(int size) {
			int capacity = (size * 3) / 2 + 1;
			if (capacity < size) {
				capacity = size;
			}
			if (capacity < size + 1) {
				capacity = size + 1;
			}
			if (capacity < 4) {
				capacity = 4;
			}
			return capacity;
		}
		protected DfaNode[] reduceStates() {
			final DfaNode[] nodes = this.getNodes();
			final Map<int[], DfaNode> nextMap = new TreeMap<int[], DfaNode>(this
					.getStateOrder());
			Map<DfaNode, DfaNode> nodeMap = null;
			for (int i = 0, n = nodes.length; i < n; ++i) {
				final DfaNode node = nodes[i];
				final DfaNext[] nexts = node.getNextArray();
				final int[] clique = this.getNodeIndexArray(nexts);
				final DfaNode rep = nextMap.get(clique);
				if (rep == null) {
					nextMap.put(clique, node);
				} else {
					if (nodeMap == null) {
						nodeMap = new HashMap<DfaNode, DfaNode>();
					}
					nodeMap.put(node, rep);
				}
			}
			if (nodeMap == null) {
				return nodes;
			}
			final DfaNode[] newNodes = new DfaNode[nodes.length - nodeMap.size()];
			for (int i = 0, cnt = 0, n = nodes.length; i < n; ++i) {
				final DfaNode node = nodes[i];
				if (nodeMap.containsKey(node)) {
					continue;
				}
				final DfaNext[] nexts = node.getNextArray();
				for (int ii = 0, nn = nexts.length; ii < nn; ++ii) {
					final DfaNode newNode = nodeMap.get(nexts[ii].getNode());
					if (newNode != null) {
						nexts[ii] = this.newNext(newNode, nexts[ii].getSymbolIndex());
					}
				}
				newNodes[cnt++] = node;
			}
			return newNodes;
		}
		protected DfaNext newNext(DfaNode node, int symbolIndex) {
			return new DfaNext(node, symbolIndex);
		}
		protected int[] getNodeIndexArray(DfaNext[] nexts) {
			final int n = nexts != null ? nexts.length : 0;
			int index = 0;
			switch (n) {
			case 0:
				return ArrayHelper.EMPTY_INT_ARRAY;
			case 1:
				return new int[] { nexts[0].getNode().getNodeIndex() };
			case 2:
				index = nexts[0].getNodeIndex();
				final int i1 = nexts[1].getNodeIndex();
				if (index == i1) {
					return new int[] { index };
				} else if (index < i1) {
					return new int[] { index, i1 };
				} else {
					return new int[] { i1, index };
				}
			default:
				break;
			}
			final int[] array = new int[n];
			int count = 0;
			for (int i = 0; i < n; ++i) {
				index = nexts[i].getNodeIndex();
				if (0 <= ArrayHelper.indexOf(array, 0, count, index)) {
					continue;
				}
				array[count++] = index;
			}
			if (1 < count) {
				Arrays.sort(array);
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
				newNexts[i] = ArrayHelper.add(KlSemiringGraph.END_NODE, inds);
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

package study.automata;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.collections.primitives.IntCollection;

import utils.regex.ExpressionFactory.Node;
import utils.regex.ExpressionFactory.SymbolNode;

public class GraphFactory {
	protected static class GraphData {
		int symbolIndex = -1;
		boolean idempotentOne;
	}

	private final Node root;
	private String[] symbols;
	private int[][] follows;

	public GraphFactory(Node root) {
		assert root != null;
		this.root = root;
	}
	public Node getRoot() {
		return root;
	}
	protected String[] getSymbols() {
		if (this.symbols == null) {
			this.symbols = this.newSymbols();
		}
		return this.symbols;
	}
	/*
	 * Collects symbols and assign data {@link GraphData} for all nodes.
	 */
	protected String[] newSymbols() {
		final Map<String, GraphData> symbolMap = new TreeMap<String, GraphData>();
		this.newSymbols(symbolMap, this.getRoot());
		final String[] array = new String[symbolMap.size()];
		final Iterator<Entry<String, GraphData>> p = symbolMap.entrySet()
				.iterator();
		for (int i = 0, n = symbolMap.size(); i < n; ++i) {
			final Entry<String, GraphData> entry = p.next();
			entry.getValue().symbolIndex = i;
			array[i] = entry.getKey();
		}
		return array;
	}
	protected GraphData newSymbols(Map<String, GraphData> output, Node node) {
		switch (node.getNodeType()) {
		case ExpressionFactory.ZERO_NODE:
		case ExpressionFactory.ONE_NODE:
			return this.newSymbolsUnitNode(output, node);
		case ExpressionFactory.SYMBOL_NODE:
			this.newSymbolsSymbolNode(output, (SymbolNode) node);
			break;
		case ExpressionFactory.STAR_NODE:
			this.newSymbolsStarNode(output, node);
			break;
		default:
			for (int i = 0, n = node.getChildSize(); i < n; ++i) {
				this.newSymbols(output, node.getChild(i));
			}
			break;
		}
	}
	protected GraphData newSymbolsUnitNode(Map<String, GraphData> output,
			Node node) {
		final GraphData data = new GraphData();
		data.idempotentOne = node.isIdempotentOne();
		node.setData(data);
		return data;
	}
	protected GraphData newSymbolsSymbolNode(Map<String, GraphData> output,
			SymbolNode node) {
		GraphData data = output.get(node.getValue());
		if (data == null) {
			data = new GraphData();
			output.put(node.getValue(), data);
		}
		node.setData(data);
		return data;
	}
	protected GraphData newSymbolsStarNode(Map<String, GraphData> output, Node node) {
		
		final GraphData data = new GraphData();
		data.idempotentOne = true;
		node.setData(data);
	}
	protected int[][] getFollows() {
		if (this.follows == null) {
			this.follows = this.newFollows();
		}
		return this.follows;
	}
	protected int[][] newFollows() {
		final String[] symbols = this.getSymbols();
		final IntCollection[] array = new IntCollection[symbols.length];
		this.newFollows(array, this.getRoot());
	}
	protected void newFollows(IntCollection[] output, Node node) {
		switch (node.getNodeType()) {
		case ExpressionFactory.ZERO_NODE:
		case ExpressionFactory.ONE_NODE:
			break;
		case ExpressionFactory.SYMBOL_NODE:
			final SymbolNode symbol = (SymbolNode) node;
			output.add(symbol.getValue());
			break;
		default:
			for (int i = 0, n = node.getChildSize(); i < n; ++i) {
				this.newSymbols(output, node.getChild(i));
			}
			break;
		}
	}
}

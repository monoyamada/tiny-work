/**
 *
 */
package study.monoid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import study.monoid.KlSemiringGraph.DfaNext;
import study.monoid.KlSemiringGraph.DfaNode;
import study.primitive.IfLongList;
import study.primitive.LongArrayList;

class DfaExpressionBuilder extends KlSemiringFactory {
	public static final int ASSIGNS = KlSemiringFactory.END;
	public static final int END = ASSIGNS + 1;

	protected static String toSymbol(DfaNode node) {
		return node != null ? KlSemiringGraph.NODE_EXPRESSION_PREFIX
				+ node.getNodeIndex() : null;
	}

	public class Variable extends Symbol {
		final DfaNode node;

		public Variable(String symbol, DfaNode node) {
			super(symbol);
			this.node = node;
		}
		public IfNode assigns(IfNode node) {
			return this.newAssigns(node);
		}
		protected IfNode newAssigns(IfNode node) {
			return DfaExpressionBuilder.this.newAssigns(this, node);
		}
	}

	public class Assigns extends Multiplies {
		public Assigns(IfNode child0, IfNode child1) {
			super(child0, child1);
		}
		@Override
		public int getNodeType() {
			return DfaExpressionBuilder.ASSIGNS;
		}
	}

	private Map<DfaNode, Variable> variableMap;

	public Variable getVariable(DfaNode node, boolean anyway) {
		final Map<DfaNode, Variable> map = this.getVariableMap();
		Variable var = map.get(node);
		if (var == null && anyway) {
			var = this.newVariable(node);
			map.put(node, var);
		}
		return var;
	}
	protected Map<DfaNode, Variable> getVariableMap() {
		if (this.variableMap == null) {
			this.variableMap = this.newVariableMap();
		}
		return this.variableMap;
	}
	protected Map<DfaNode, Variable> newVariableMap() {
		return new HashMap<DfaNode, Variable>();
	}
	protected Variable newVariable(DfaNode node) {
		return new Variable(toSymbol(node), node);
	}
	protected IfNode newAssigns(Variable lhs, IfNode rhs) {
		return new Assigns(lhs, rhs);
	}

	protected IfNode getExpression(DfaNode node) {
		final Variable lhs = this.getVariable(node, true);
		final DfaNext[] nexts = node.getNextArray();
		final int n = nexts.length;
		if (n < 1) {
			return lhs;
		}
		final Map<DfaNode, IfLongList> rhsMap = new HashMap<DfaNode, IfLongList>();
		for (int i = 0; i < n; ++i) {
			final DfaNext next = nexts[i];
			final DfaNode x = next.getNode();
			IfLongList symbols = rhsMap.get(x);
			if (symbols == null) {
				symbols = new LongArrayList(1);
				rhsMap.put(x, symbols);
			}
			final int symbol = next.getSymbolIndex();
			if (symbols.getFirstIndex(symbol) < 0) {
				symbols.addBack(symbol);
			}
		}
		IfNode rhs = null;
		final String[] symbols = node.getBuilder().getSymbolSet();
		for (Iterator<Entry<DfaNode, IfLongList>> p = rhsMap.entrySet()
				.iterator(); p.hasNext();) {
			final Entry<DfaNode, IfLongList> ent = p.next();
			final IfLongList ks = ent.getValue();
			final Variable x = this.getVariable(ent.getKey(), true);
			IfNode k = this.getSymbol(symbols[(int) ks.getLong(0)]);
			for (int ii = 1, nn = ks.size(); ii < nn; ++ii) {
				final IfNode kk = this.getSymbol(symbols[(int) ks.getLong(ii)]);
				k = kk.plus(k);
			}
			if (rhs == null) {
				rhs = k.multiplies(x);
			} else {
				rhs = rhs.plus(k.multiplies(x));
			}
		}
		if (rhs == null) {
			return lhs;
		}
		return lhs.assigns(rhs);
	}
}
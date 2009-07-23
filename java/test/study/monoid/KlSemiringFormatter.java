package study.monoid;

import static study.monoid.KlSemiringFactory.END;
import static study.monoid.KlSemiringFactory.MULTIPLIES;
import static study.monoid.KlSemiringFactory.ONE;
import static study.monoid.KlSemiringFactory.PLUS;
import static study.monoid.KlSemiringFactory.STARS;
import static study.monoid.KlSemiringFactory.SYMBOL;
import static study.monoid.KlSemiringFactory.ZERO;
import study.monoid.KlSemiringFactory.IfNode;

public class KlSemiringFormatter {
	public static final String ZERO_EXPRESSION = "0";
	public static final String ONE_EXPRESSION = "1";
	public static final String STARS_EXPRESSION = "^*";
	public static final String PLUS_EXPRESSION = "+";
	public static final String MULTIPLIES_EXPRESSION = ".";
	public static final String OPEN_EXPRESSION = "(";
	public static final String CLOSE_EXPRESSION = ")";
	public static final String UNKNOWN_EXPRESSION = "???";

	public static class LiteralData {
		final int nodeType;
		final String literal;
		final int order;

		public LiteralData(int nodeType, String literal, int order) {
			this.nodeType = nodeType;
			this.literal = literal;
			this.order = order;
		}
		/**
		 * @return the nodeType
		 */
		public int getNodeType() {
			return this.nodeType;
		}
		/**
		 * @return the literal
		 */
		public String getExpression() {
			return this.literal;
		}
		/**
		 * @return the order
		 */
		public int getOrder() {
			return this.order;
		}
	}

	private LiteralData[] listeralDataArray;

	protected LiteralData[] getLiteralDataArray() {
		if (this.listeralDataArray == null) {
			this.listeralDataArray = this.newLiteralDataArray();
		}
		return this.listeralDataArray;
	}
	protected LiteralData[] newLiteralDataArray() {
		final LiteralData[] orders = new LiteralData[END];
		orders[ZERO] = new LiteralData(ZERO, ZERO_EXPRESSION, 0);
		orders[ONE] = new LiteralData(ONE, ONE_EXPRESSION, 0);
		orders[SYMBOL] = new LiteralData(SYMBOL, null, 0);
		orders[STARS] = new LiteralData(STARS, STARS_EXPRESSION, 100);
		orders[MULTIPLIES] = new LiteralData(MULTIPLIES, MULTIPLIES_EXPRESSION, 200);
		orders[PLUS] = new LiteralData(PLUS, PLUS_EXPRESSION, 300);
		return orders;
	}
	public String format(IfNode node) {
		final StringBuilder buffer = new StringBuilder();
		this.format(buffer, null, node);
		return buffer.toString();
	}
	public void format(StringBuilder output, IfNode node) {
		this.format(output, null, node);
	}
	protected void format(StringBuilder output, IfNode parent, IfNode node) {
		assert output != null;
		if (node == null) {
			return;
		}
		final LiteralData[] orders = this.getLiteralDataArray();
		LiteralData data = null;
		if (node.getNodeType() < orders.length) {
			data = orders[node.getNodeType()];
		}
		boolean brace = parent != null;
		if (brace) {
			final int pt = parent.getNodeType();
			if (pt < orders.length && data != null) {
				brace = orders[pt].getOrder() < data.getOrder();
			}
		}
		final String expr = data != null ? data.getExpression()
				: KlSemiringFormatter.UNKNOWN_EXPRESSION;
		if (brace) {
			output.append(KlSemiringFormatter.OPEN_EXPRESSION);
		}
		switch (node.getNodeType()) {
		case KlSemiringFactory.ZERO:
		case KlSemiringFactory.ONE:
			output.append(expr);
			break;
		case KlSemiringFactory.SYMBOL:
			output.append(node.toString());
			break;
		case KlSemiringFactory.STARS:
			this.format(output, node, node.getChild(0));
			output.append(expr);
			break;
		default:
			for (int i = 0, n = node.getChildSize(); i < n; ++i) {
				if (i != 0) {
					output.append(expr);
				}
				this.format(output, node, node.getChild(i));
			}
			break;
		}
		if (brace) {
			output.append(KlSemiringFormatter.CLOSE_EXPRESSION);
		}
	}
	protected String toStringWrapSpaces(String text) {
		return ' ' + text + ' ';
	}
}

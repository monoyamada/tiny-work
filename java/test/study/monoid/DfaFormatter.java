/**
 *
 */
package study.monoid;

import static study.monoid.KlSemiringFactory.MULTIPLIES;
import static study.monoid.KlSemiringFactory.ONE;
import static study.monoid.KlSemiringFactory.PLUS;
import static study.monoid.KlSemiringFactory.STARS;
import static study.monoid.KlSemiringFactory.SYMBOL;
import static study.monoid.KlSemiringFactory.ZERO;

class DfaFormatter extends KlSemiringFormatter {
	public static final String ASSIGNS_EXPRESSION = "=";

	@Override
	protected LiteralData[] newLiteralDataArray() {
		final LiteralData[] orders = new LiteralData[DfaExpressionBuilder.END];
		orders[ZERO] = new LiteralData(ZERO, ZERO_EXPRESSION, 0);
		orders[ONE] = new LiteralData(ONE, ONE_EXPRESSION, 0);
		orders[SYMBOL] = new LiteralData(SYMBOL, null, 0);
		orders[STARS] = new LiteralData(STARS, STARS_EXPRESSION, 100);
		orders[MULTIPLIES] = new LiteralData(MULTIPLIES, MULTIPLIES_EXPRESSION,
				200);
		orders[PLUS] = new LiteralData(PLUS, PLUS_EXPRESSION, 300);
		orders[DfaExpressionBuilder.ASSIGNS] = new LiteralData(
				DfaExpressionBuilder.ASSIGNS, ASSIGNS_EXPRESSION, 1000);
		return orders;
	}
}
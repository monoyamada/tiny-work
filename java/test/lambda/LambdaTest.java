package lambda;

import static lambda.Lambda.apply;
import static lambda.Lambda.getParametrSize;
import static lambda.Lambda.lambda;
import static lambda.Lambda.parse;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import lambda.Lambda.Abstract;
import lambda.Lambda.Expression;
import tiny.lang.Debug;

public class LambdaTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testSKI() throws Exception {
		Abstract I = lambda("a", "a");
		{
			Debug.log().info("I = " + I);
			Debug.log().info("#params = " + getParametrSize(I));
		}
		Abstract K = lambda("a", lambda("b", "a"));
		{
			Debug.log().info("K = " + K);
			Debug.log().info("#params = " + getParametrSize(K));
		}
		Abstract S = lambda("a", lambda("b", lambda("c", apply(apply("a", "c"),
				apply("b", "c")))));
		{
			Debug.log().info("S = " + S);
			Debug.log().info("#params = " + getParametrSize(S));
		}
		if (false) {
			Map<String, Expression> vars = new TreeMap<String, Expression>();
			vars.put("S", S);
			vars.put("K", K);
			vars.put("I", I);
			parse(vars, "I (I I)");
		}
		if (true) {
			Map<String, Expression> vars = new TreeMap<String, Expression>();
			if (true) {
				vars.put("S", S);
				vars.put("K", K);
				vars.put("I", I);
			}
			String expr = "S (K (S I I)) (S (S (K S) K) (K (S I I)))";
			Expression Y = parse(vars, expr);
			Debug.log().info(expr + " = " + Y);
		}
	}
}

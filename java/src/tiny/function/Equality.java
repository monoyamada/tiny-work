package tiny.function;

import tiny.lang.ObjectHelper;

public class Equality extends AbBooleanBinaryFunction<Object, Object> {
	@Override
	public boolean evaluateBoolean(Object first, Object second) throws Exception {
		return ObjectHelper.equals(first, second);
	}
}

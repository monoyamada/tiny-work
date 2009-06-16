package study.function;

import study.struct.IfPredicate;

public interface IfBooleanBinaryFunction<First, Second> extends
		IfBinaryFunction<First, Second, Boolean>, IfPredicate<First, Second> {
}

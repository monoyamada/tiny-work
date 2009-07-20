package study.function;

public interface IfBooleanFunction<Source> extends IfFunction<Source, Boolean>, IfPredicate<Source> {
	public boolean evaluateBoolean(Source x) throws Exception;
}

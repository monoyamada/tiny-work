package study.function;

public interface BooleanFunction<Source> extends Function<Source, Boolean>, Predicate<Source> {
	public boolean evaluateBoolean(Source x) throws Exception;
}

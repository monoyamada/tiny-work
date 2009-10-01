package study.function;

public interface Predicate<Source> {
	public boolean evaluateBoolean(Source source) throws Exception;
}

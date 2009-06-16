package study.function;

public interface IfBooleanFunction<Source> extends IfFunction<Source, Boolean> {
	public boolean evaluateBoolean(Source x) throws Exception;
}

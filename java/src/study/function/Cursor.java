package study.function;

public interface Cursor<T> {
	public boolean move() throws Exception;
	public T getValue() throws Exception;
}

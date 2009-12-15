package study.primitive;

public interface IfLongArray extends Iterable<Number> {
	public IfNumberIterator iterator();
	public int size();
	public long getLong(int index);
	public long getFirst(long defaultValue);
	public long getLast(long defaultValue);
	public int getFirstIndex(long value);
	public int getLastIndex(long value);
}
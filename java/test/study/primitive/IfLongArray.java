package study.primitive;

public interface IfLongArray extends Iterable<Number> {
	public IfNumberIterator iterator();
	public int size();
	public long get(int index);
	public long front(long defaultValue);
	public long back(long defaultValue);
}
package study.primitive;

public interface LongList extends LongArray {
	public LongList add(int index, long value);
	public LongList addFirst(long value);
	public LongList addLast(long value);
	public LongList remove(int index);
	public LongList removeFirst();
	public LongList removeLast();
	public LongList removeAll();
}

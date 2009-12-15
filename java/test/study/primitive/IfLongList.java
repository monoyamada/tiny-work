package study.primitive;

public interface IfLongList extends IfLongArray {
	public IfLongList add(int index, long value);
	public IfLongList addFront(long value);
	public IfLongList addBack(long value);
	public IfLongList remove(int index);
	public IfLongList removeFront();
	public IfLongList removeBack();
}

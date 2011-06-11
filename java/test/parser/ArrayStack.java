package parser;

import java.util.ArrayList;
import java.util.Collection;

import tiny.function.Stack;

public class ArrayStack<T> extends ArrayList<T> implements Stack<T> {
	private static final long serialVersionUID = 4763509080102967540L;

	public ArrayStack() {
	}
	public ArrayStack(int initialCapacity) {
		super(initialCapacity);
	}
	public ArrayStack(Collection<? extends T> c) {
		super(c);
	}
	@Override
	public T peekValue(T def) {
		if (this.size() < 1) {
			return def;
		}
		return this.get(this.size() - 1);
	}
	public ArrayStack<T> setValue(T value) {
		this.set(this.size() - 1, value);
		return this;
	}
	@Override
	public ArrayStack<T> pushValue(T value) {
		this.add(value);
		return this;
	}
	@Override
	public boolean pop() {
		if (this.size() < 1) {
			return false;
		}
		this.remove(this.size() - 1);
		return true;
	}
	@Override
	public boolean isFull() {
		return this.size() == Integer.MAX_VALUE;
	}
}

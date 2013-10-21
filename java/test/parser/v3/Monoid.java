package parser.v3;

public interface Monoid<T> {
	T times(T y);
	boolean isOne();
}

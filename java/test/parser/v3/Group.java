package parser.v3;

public interface Group<T> extends Monoid<T> {
	boolean isInverse(T y);
	T invert();
}

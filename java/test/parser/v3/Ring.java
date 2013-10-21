package parser.v3;

public interface Ring<T extends Ring<T>> extends Monoid<T> {
	T plus(T y);
	boolean isZero();
}

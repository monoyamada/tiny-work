package study.algebra;

import study.function.Tupple2;
import study.lang.Messages;

public class ArrayTestClasses {
	public static Object[] transform(IfArrayTransform[] transform, Object[] source)
			throws Exception {
		int iT = 0;
		for (int i = 0, n = transform.length; i < n; ++i) {
			final IfArrayTransform t = transform[i];
			iT += t.getTargetDegree();
		}
		final Object[] target = new Object[iT];
		ArrayTestClasses.transform(target, 0, transform, 0, transform.length,
				source, 0);
		return target;
	}
	public static void transform(Object[] target, IfArrayTransform[] transform,
			Object[] source) throws Exception {
		ArrayTestClasses.transform(target, 0, transform, 0, transform.length,
				source, 0);
	}
	public static void transform(Object[] target, int targetBegin,
			IfArrayTransform[] transform, int functionBegin, int functionEnd,
			Object[] source, int sourceBegin) throws Exception {
		final int n = functionEnd - functionBegin;
		int iT = 0;
		int iS = 0;
		for (int i = 0; i < n; ++i) {
			final IfArrayTransform t = transform[i];
			t.evaluate(target, iT, source, iS);
			iT += t.getTargetDegree();
			iS += t.getSourceDegree();
		}
	}

	/**
	 * see usage
	 * {@link ArrayTestClasses#transform(ArrayTestClasses.IfArrayTransform[], Object[]).
	 *
	 * @author shirakata
	 *
	 */
	public static interface IfArrayTransform {
		public int getSourceDegree();
		public int getTargetDegree();
		/**
		 * @param target
		 * @param targetIndex
		 * @param source
		 * @param sourceIndex
		 * @return <code>true</code> if and only if values were changed.
		 *         performance reason.
		 */
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception;
	}

	public static class Identity implements IfArrayTransform {
		@Override
		public int getSourceDegree() {
			return 1;
		}
		@Override
		public int getTargetDegree() {
			return 1;
		}
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			target[targetIndex] = source[sourceIndex];
		}
	}

	public static class CompositTranfrom extends
			Tupple2<IfArrayTransform, IfArrayTransform> implements IfArrayTransform {
		private Object[] buffer;

		public CompositTranfrom(IfArrayTransform value0, IfArrayTransform value1) {
			super(value0, value1);
			if (this.value0.getSourceDegree() != this.value1.getTargetDegree()) {
				String msg = Messages.getUnexpectedValue("degree to composit",
						this.value0.getSourceDegree(), this.value1.getTargetDegree());
				throw new IllegalArgumentException(msg);
			}
		}
		@Override
		public int getSourceDegree() {
			return this.value1.getSourceDegree();
		}
		@Override
		public int getTargetDegree() {
			return this.value0.getTargetDegree();
		}
		public int compositDegree() {
			return this.value1.getTargetDegree();
		}
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			final Object[] buffer = this.getBuffer(this.compositDegree());
			this.value1.evaluate(buffer, 0, source, sourceIndex);
			this.value0.evaluate(target, targetIndex, buffer, 0);
		}
		protected Object[] getBuffer(int size) {
			if (this.buffer == null || this.buffer.length < size) {
				this.buffer = new Object[size];
			}
			return this.buffer;
		}
	}

	public static class Swap implements IfArrayTransform {
		@Override
		public int getSourceDegree() {
			return 2;
		}
		@Override
		public int getTargetDegree() {
			return 2;
		}
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			// use temporary variable to guard for the case<code>target == source &&
			// sourceIndex == targetIndex</code>
			final Object tmp = source[sourceIndex + 1];
			target[targetIndex + 1] = source[sourceIndex];
			target[targetIndex] = tmp;
		}
	}

	public static abstract class AbMagma implements IfArrayTransform {
		@Override
		public int getSourceDegree() {
			return 2;
		}
		@Override
		public int getTargetDegree() {
			return 1;
		}
	}

	public static abstract class AbCoMagma implements IfArrayTransform {
		@Override
		public int getSourceDegree() {
			return 1;
		}
		@Override
		public int getTargetDegree() {
			return 2;
		}
	}

	public static class PrefixCoMagma extends AbCoMagma {
		public final Object prefix;

		public PrefixCoMagma(Object prefix) {
			this.prefix = prefix;
		}
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			target[targetIndex] = this.prefix;
			target[targetIndex + 1] = source[sourceIndex];
		}
	}

	public static class SuffixCoMagma extends AbCoMagma {
		public final Object suffix;

		public SuffixCoMagma(Object suffix) {
			this.suffix = suffix;
		}
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			target[targetIndex] = source[sourceIndex];
			target[targetIndex + 1] = this.suffix;
		}
	}

	public static class StringMagma extends AbMagma {
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			target[targetIndex] = source[sourceIndex] + "" + source[sourceIndex + 1];
		}
	}

	public static abstract class AbNumberMagma extends AbMagma {
		@Override
		public void evaluate(Object[] target, int targetIndex, Object[] source,
				int sourceIndex) throws Exception {
			target[targetIndex] = this.evaluateNumber((Number) source[sourceIndex],
					(Number) source[sourceIndex + 1]);
		}
		protected abstract Object evaluateNumber(Number first, Number second);
	}

	public static class LongAdditiveMagma extends AbNumberMagma {
		@Override
		protected Object evaluateNumber(Number first, Number second) {
			return first.longValue() + second.longValue();
		}
	}

	public static class LongMultiplicativeMagma extends AbNumberMagma {
		@Override
		protected Object evaluateNumber(Number first, Number second) {
			return first.longValue() * second.longValue();
		}
	}
}

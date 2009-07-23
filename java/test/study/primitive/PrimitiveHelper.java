package study.primitive;

public class PrimitiveHelper {
	public static class LongArrayIterator extends AbNumberIterator {
		private final IfLongArray container;
		private int index;

		public LongArrayIterator(IfLongArray container) {
			assert container != null;
			this.container = container;
		}
		@Override
		public boolean hasNext() {
			return this.index < this.container.size();
		}
		@Override
		public double nextDouble() {
			return this.nextLong();
		}
		@Override
		public long nextLong() {
			return this.container.getLong(this.index++);
		}
		@Override
		public Number next() {
			return Long.valueOf(this.nextLong());
		}
	}
}

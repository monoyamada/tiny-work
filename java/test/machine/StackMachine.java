package machine;

import java.util.ArrayList;
import java.util.List;

import tiny.lang.Messages;

public class StackMachine {
	public static interface Function {
		public void execute(StackMachine machine) throws Exception;
	}

	protected static class StackElement {
		public static final byte NONE_TYPE = 0;
		public static final byte LONG_TYPE = NONE_TYPE + 1;
		public static final StackElement NONE_ELEMENT = new StackElement();
		
		public static String getTypeName (StackElement x){
			if(x==null){
				return "null";
			}
			switch(x.type){
			case NONE_TYPE:
				return "none";
			case LONG_TYPE:
				return "long";
			default:
				return "unknown";
			}
		}
		final byte type;

		public StackElement() {
			this(StackElement.NONE_TYPE);
		}
		protected StackElement(byte type) {
			this.type = type;
		}
		public byte getType() {
			return this.type;
		}
		public long getLong(long none) {
			return none;
		}
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			this.toString(buffer);
			return buffer.toString();
		}
		protected void toString(StringBuilder output) {
			output.append(':');
			output.append(StackElement.getTypeName(this));
		}
	}

	protected static class LongElement extends StackElement {
		long value;

		public LongElement() {
			super(StackElement.LONG_TYPE);
		}
		public LongElement(long value) {
			this();
			this.value = value;
		}
		public long getValue() {
			return this.value;
		}
		public void setValue(long value) {
			this.value = value;
		}
		public long getLong(long none) {
			return this.value;
		}
		protected void toString(StringBuilder output) {
			output.append(Long.toString(this.value));
			output.append(':');
			output.append(StackElement.getTypeName(this));
		}
	}

	protected static class Frame {
		final int stackIndex;
		int commandIndex;

		public Frame(int stackIndex) {
			this.stackIndex = stackIndex;
		}
	}

	final List<StackElement> stack;
	final List<Frame> frames;

	public StackMachine() {
		this(32);
	}
	public StackMachine(int capacity) {
		this.stack = new ArrayList<StackElement>(capacity);
		this.frames = new ArrayList<StackMachine.Frame>();
	}
	protected List<StackElement> getStack() {
		return this.stack;
	}
	protected List<Frame> getFrames() {
		return this.frames;
	}
	protected Frame getFrame() {
		int n = this.frames.size();
		if (n < 1) {
			return null;
		}
		return this.frames.get(n - 1);
	}
	protected Frame pushFrame(int n) {
		Frame frame = new Frame(this.getStack().size() - n);
		this.getFrames().add(frame);
		return frame;
	}
	protected Frame popFrame() {
		int n = this.getFrames().size();
		if (n < 1) {
			return null;
		}
		return this.getFrames().remove(n - 1);
	}
	protected int localToGlobal(int index) {
		Frame x = this.getFrame();
		return x != null ? x.stackIndex + index : index;
	}
	protected int globalToLocal(int index) {
		Frame x = this.getFrame();
		return x != null ? index - this.getFrame().stackIndex : index;
	}
	public int size() {
		return this.globalToLocal(this.getStack().size());
	}
	protected StackElement getElement(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Messages.getIndexOutOfRange("stack",
					0, index, this.size()));
		}
		return this.getStack().get(this.localToGlobal(index));
	}
	/**
	 * @param index
	 * @param value
	 * @return an element that was replaced with the specified value.
	 */
	protected StackElement setElement(int index, StackElement value) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Messages.getIndexOutOfRange("stack",
					0, index, this.size()));
		} else if (value == null) {
			throw new IllegalArgumentException(Messages.getNull("stack element"));
		}
		return this.getStack().set(this.localToGlobal(index), value);
	}
	protected StackMachine pushElement(StackElement value) {
		if (value == null) {
			throw new IllegalArgumentException(Messages.getNull("stack element"));
		}
		this.getStack().add(value);
		return this;
	}
	/**
	 * @return <code>null</code> iff stack is empty, an element that was popped
	 *         otherwise.
	 */
	protected StackElement popElement() {
		if (this.size() < 1) {
			return null;
		}
		return this.getStack().remove(this.getStack().size() - 1);
	}

	public byte getType(int index) {
		return this.getElement(index).type;
	}
	public StackMachine pop() {
		return this.pop(1);
	}
	public StackMachine pop(int n) {
		n = Math.min(n, this.size());
		while (0 < n--) {
			this.popElement();
		}
		return this;
	}
	public StackMachine setNone(int index) {
		StackElement x = this.getElement(index);
		if (x.type != StackElement.NONE_TYPE) {
			this.setElement(index, StackElement.NONE_ELEMENT);
		}
		return this;
	}
	public StackMachine pushNone() {
		return this.pushNone(1);
	}
	public StackMachine pushNone(int n) {
		while (0 < n--) {
			this.pushElement(StackElement.NONE_ELEMENT);
		}
		return this;
	}
	public long getLong(int index, long none) {
		return this.getElement(index).getLong(none);
	}
	public StackMachine setLong(int index, long value) {
		StackElement x = this.getElement(index);
		if (x.type == StackElement.LONG_TYPE) {
			LongElement y = (LongElement) x;
			y.value = value;
		} else {
			LongElement y = new LongElement(value);
			this.setElement(index, y);
		}
		return this;
	}
	public StackMachine pushLong(long value) {
		return this.pushElement(new LongElement(value));
	}
	public StackMachine apply(Function value, int nIn, int nOut) throws Exception {
		if (nIn < 0 || this.size() < nIn) {
			throw new IllegalArgumentException(Messages.getIndexOutOfRange(
					"input parameter size", 0, nIn, this.size()));
		} else if (nOut < 0) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					"output parameter size", "non-negative", "negative"));
		} else if (value == null) {
			throw new IllegalArgumentException(Messages.getNull("function"));
		}
		this.pushFrame(nIn);
		value.execute(this);
		int n = this.size();
		if (nOut < n) {
			this.pop(n - nOut);
		} else if (n < nOut) {
			this.pushNone(nOut - n);
		}
		this.popFrame();
		return this;
	}
}

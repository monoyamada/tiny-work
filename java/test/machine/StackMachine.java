package machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiny.lang.Messages;

public class StackMachine {
	public static interface Function {
		public int inputSize();
		public boolean inputMoreThan();
		public int outputSize();
		public void execute(StackMachine machine) throws Exception;
	}

	protected static class StackElement implements Cloneable {
		public static final byte UNUSED_TYPE = 0;
		public static final byte LONG_TYPE = UNUSED_TYPE + 1;

		public static String getTypeName(StackElement x) {
			if (x == null) {
				return "null";
			}
			switch (x.type) {
			case UNUSED_TYPE:
				return "none";
			case LONG_TYPE:
				return "long";
			default:
				return "unknown";
			}
		}
		public static StackElement newUnused() {
			return new StackElement(StackElement.UNUSED_TYPE);
		}
		public static StackElement newLong(long value) {
			StackElement x = new StackElement(StackElement.LONG_TYPE);
			x.longValue = value;
			return x;
		}

		byte type;
		long longValue;

		public StackElement() {
			this(StackElement.UNUSED_TYPE);
		}
		protected StackElement(byte type) {
			this.type = type;
		}
		public StackElement clone() {
			try {
				return (StackElement) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new RuntimeException(ex);
			}
		}
		public byte getType() {
			return this.type;
		}
		public long getLong(long none) {
			if (this.type == StackElement.LONG_TYPE) {
				return this.longValue;
			}
			return none;
		}
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			this.toString(buffer);
			return buffer.toString();
		}
		protected void toString(StringBuilder output) {
			if (this.type == LONG_TYPE) {
				output.append(Long.toString(this.longValue));
			}
			output.append(':');
			output.append(StackElement.getTypeName(this));
		}
	}

	protected static class Frame {
		final int stackIndex;
		Map<String, Integer> variableMap;

		public Frame(int stackIndex) {
			this.stackIndex = stackIndex;
		}
		protected Map<String, Integer> getVariableMap(boolean anyway) {
			if (this.variableMap == null && anyway) {
				this.variableMap = this.newVariableMap();
			}
			return this.variableMap;
		}
		protected Map<String, Integer> newVariableMap() {
			return new HashMap<String, Integer>();
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
		this.pushFrame(0);
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
	protected Frame pushFrame(int globalIndex) {
		Frame frame = new Frame(globalIndex);
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
		return this.localToGlobal(this.getFrame(), index);
	}
	protected int localToGlobal(Frame frame, int index) {
		return frame != null ? frame.stackIndex + index : index;
	}
	protected int globalToLocal(int index) {
		return this.globalToLocal(this.getFrame(), index);
	}
	protected int globalToLocal(Frame frame, int index) {
		return frame != null ? index - frame.stackIndex : index;
	}
	public int size() {
		return this.globalToLocal(this.getStack().size());
	}
	/**
	 * @param index
	 *          local index.
	 * @return a copied element.
	 */
	public StackElement getElement(int index) {
		return this.getElementToWrite(index).clone();
	}
	/**
	 * @param index
	 *          local index.
	 * @return an element in the stack.
	 */
	private StackElement getElementToWrite(int index) {
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
	
	private void setUnused(StackElement x) {
		x.type = StackElement.UNUSED_TYPE;
		x.longValue = 0;
	}
	private void setLong(StackElement x, long value) {
		x.type = StackElement.LONG_TYPE;
		x.longValue = value;
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
	public StackMachine pushUnused() {
		return this.pushUnused(1);
	}
	public StackMachine pushUnused(int n) {
		while (0 < n--) {
			this.pushElement(StackElement.newUnused());
		}
		return this;
	}
	public void setUnused(int index) {
		StackElement x = this.getElementToWrite(index);
		this.setUnused(x);
	}

	public StackMachine pushLong(long value) {
		return this.pushElement(StackElement.newLong(value));
	}
	public void setLong(int index, long value) {
		StackElement x = this.getElementToWrite(index);
		this.setLong(x, value);
	}
	public StackMachine apply(Function value, int index) throws Exception {
		int n = this.size();
		if (index < 0 || n <= index) {
			throw new IllegalArgumentException(Messages.getIndexOutOfRange(
					"stack index", 0, index, n));
		} else if (value == null) {
			throw new IllegalArgumentException(Messages.getNull("function"));
		} else if (n < index + value.inputSize()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("#params",
					value.inputSize() + "+", n - index));
		}
		this.pushFrame(this.localToGlobal(index));
		value.execute(this);
		n = this.size();
		int out = value.outputSize();
		if (out < n) {
			this.pop(n - out);
		} else if (n < out) {
			this.pushUnused(out - n);
		}
		this.popFrame();
		return this;
	}
	public StackMachine makeVariable(String name, int index) {
		int n = this.size();
		if (index < 0 || n <= index) {
			throw new IllegalArgumentException(Messages.getIndexOutOfRange(
					"stack index", 0, index, n));
		}
		Frame frame = this.getFrame();
		Map<String, Integer> vars = frame.getVariableMap(true);
		vars.put(name, index);
		return this;
	}
	private StackElement getVariableToWrite(String name, StackElement none) {
		List<Frame> frames = this.getFrames();
		int n = frames.size();
		while (0 < n--) {
			Frame frame = frames.get(n);
			Map<String, Integer> vars = frame.getVariableMap(false);
			if (vars == null) {
				continue;
			}
			Integer x = vars.get(name);
			if (x != null) {
				return this.getStack().get(this.localToGlobal(frame, x.intValue()));
			}
		}
		return none;
	}
	public StackElement getVariable(String name, StackElement none) {
		StackElement x = this.getVariableToWrite(name, null);
		return x != null ? x.clone() : none;
	}
}

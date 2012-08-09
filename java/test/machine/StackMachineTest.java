package machine;

import junit.framework.TestCase;
import machine.StackMachine.Function;
import machine.StackMachine.StackElement;
import tiny.lang.Debug;
import tiny.lang.Messages;

public class StackMachineTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testBasic() throws Exception {
		class Plus implements Function {
			@Override
			public void execute(StackMachine machine) throws Exception {
				StackElement x0 = machine.getElement(0);
				if (x0.type != StackElement.LONG_TYPE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				StackElement x1 = machine.getElement(1);
				if (x1.type != StackElement.LONG_TYPE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				machine.setLong(0, x0.longValue + x1.longValue);
			}
			@Override
			public int inputSize() {
				return 2;
			}
			@Override
			public boolean inputMoreThan() {
				return false;
			}
			@Override
			public int outputSize() {
				return 1;
			}
		}
		;
		Function divides_2 = new Function() {
			@Override
			public void execute(StackMachine machine) throws Exception {
				StackElement x0 = machine.getElement(0);
				if (x0.type != StackElement.LONG_TYPE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				StackElement x1 = machine.getElement(1);
				if (x1.type != StackElement.LONG_TYPE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				machine.setLong(0, x0.longValue / x1.longValue);
				machine.setLong(1, x0.longValue % x1.longValue);
			}
			@Override
			public int inputSize() {
				return 2;
			}
			@Override
			public boolean inputMoreThan() {
				return false;
			}
			@Override
			public int outputSize() {
				return 2;
			}
		};
		Function plus = new Plus();
		if (true) {
			StackMachine machine = new StackMachine();
			machine.pushLong(3).pushLong(4).apply(plus, 0);
			Debug.log().debug("3 + 4 = " + machine.getStack());
			machine.pushLong(3).apply(divides_2, 0);
			Debug.log().debug("7 / 3 = " + machine.getStack());
		}
		if (true) {
			StackMachine machine = new StackMachine();
			machine.pushLong(3).makeVariable("x", 0);
			machine.pushLong(4).makeVariable("y", 1);
			machine.apply(new Plus() {
				@Override
				public void execute(StackMachine machine) throws Exception {
					StackElement x0 = machine.getVariable("x", null);
					StackElement x1 = machine.getVariable("y", null);
					machine.setLong(0, x0.longValue + x1.longValue);
				}
			}, 0);
			Debug.log().debug("3 + 4 = " + machine.getStack());
		}
	}
}

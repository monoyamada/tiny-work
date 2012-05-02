package machine;

import junit.framework.TestCase;
import machine.StackMachine.Function;
import tiny.lang.Debug;
import tiny.lang.Messages;

public class StackMachineTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testCode() throws Exception {
		Function plus = new Function() {
			@Override
			public void execute(StackMachine machine) throws Exception {
				long x0 = machine.getLong(0, Long.MAX_VALUE);
				if (x0 == Long.MAX_VALUE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				long x1 = machine.getLong(1, Long.MAX_VALUE);
				if (x1 == Long.MAX_VALUE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				machine.setLong(0, x0 + x1);
			}
		};
		Function divides = new Function() {
			@Override
			public void execute(StackMachine machine) throws Exception {
				long x0 = machine.getLong(0, Long.MAX_VALUE);
				if (x0 == Long.MAX_VALUE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				long x1 = machine.getLong(1, Long.MAX_VALUE);
				if (x1 == Long.MAX_VALUE) {
					throw new IllegalStateException(
							Messages.getFailedOperation("may not long"));
				}
				machine.setLong(0, x0 / x1);
				machine.setLong(1, x0 % x1);
			}
		};
		if (true) {
			StackMachine machine = new StackMachine();
			machine.pushLong(3).pushLong(4).apply(plus, 2, 1);
			Debug.log().debug("3 + 4 = " + machine.getStack());
			machine.pushLong(3).apply(divides, 2, 2);
			Debug.log().debug("7 / 3 = " + machine.getStack());
		}
	}
}

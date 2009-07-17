package study.function;


public class Tupple2<Value0, Value1> extends AbArray<Object> {
	public final Value0 value0;
	public final Value1 value1;

	public Tupple2(Value0 value0, Value1 value1) {
		this.value0 = value0;
		this.value1 = value1;
	}
	@Override
	public int getSize() {
		return 2;
	}
	@Override
	protected Object doGetValue(int index) {
		switch (index) {
		case 0:
			return this.value0;
		case 1:
			return this.value1;
		default:
			return null;
		}
	}
}

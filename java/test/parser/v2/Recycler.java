package parser.v2;

import java.util.ArrayList;
import java.util.List;

public class Recycler<Self extends Recycler<Self, Value>, Value> {
	private List<Value> backedValues;

	@SuppressWarnings("unchecked")
	private Self that() {
		return (Self) this;
	}
	protected Self backValue(Value x) {
		if (x != null) {
			this.backedValues(true).add(x);
		}
		return this.that();
	}
	protected List<Value> backedValues(boolean anyway) {
		if (this.backedValues == null && anyway) {
			this.backedValues = new ArrayList<Value>();
		}
		return this.backedValues;
	}
	protected Self clearBackValues() {
		this.backedValues = null;
		return this.that();
	}
}

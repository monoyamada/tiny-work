package study.oricon;

import java.io.IOException;

public class OriconName extends OriconIndex  {
	public static final OriconName[] EMPTY_ARRAY = {};
	private String name;

	public OriconName() {
	}
	public OriconName(int index, String name) {
		super(index);
		this.name = name;
	}
	public OriconName(OriconName x) {
		super(x);
		this.name = x.name;
	}
	public void toString(Appendable output) throws IOException {
		super.toString(output);
		output.append(", name=");
		output.append(this.getName());
	}
	public OriconName clone() {
		return (OriconName) super.clone();
	}
	public OriconName copy(OriconName x) {
		super.copy(x);
		this.name = x.name;
		return this;
	}
	public OriconName clear() {
		super.clear();
		this.name = null;
		return this;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void set(int index, String name) {
		this.setIndex(index);
		this.name = name;
	}
}

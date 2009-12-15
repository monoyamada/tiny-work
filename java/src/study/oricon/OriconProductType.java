package study.oricon;

public enum OriconProductType {
	ALBUM("album", 1), SINGLE("single", 2);
	private final String name;
	private final int value;

	OriconProductType(String name, int value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return this.name;
	}
	public int getValue() {
		return this.value;
	}
	public String toString() {
		return this.getName();
	}
	public static OriconProductType getByName(String name) {
		if (name == null) {
			return null;
		}
		return valueOf(name.toUpperCase());
	}
}

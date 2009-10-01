package study.oricon;

public enum OriconColumn {
	/**
	 * not used but to implement {@link OriconNameData}.
	 */
	INDEX("index", Integer.class)
	/**
	 * for brand index
	 */
	, BRAND("brand", Integer.class) 
	/**
	 * for producct index
	 */
	, PRODUCT("product", Integer.class) //
	, DAY("day", Integer.class) //
	, SALES("sales", Integer.class) //
	, PRODUCT_TYPE("productType", OriconProductType.class) //
	, RELEASE_DAY("releaseDay", Integer.class) //
	, NAME("name", String.class) //
	;

	public static OriconColumn getByName(String name) {
		if (name == null) {
			return null;
		}
		return valueOf(name.toUpperCase());
	}

	private final String name;
	private final Class<?> valueType;

	OriconColumn(String name, Class<?> valueType) {
		this.name = name;
		this.valueType = valueType;
	}
	public String getName() {
		return this.name;
	}
	public Class<?> getValueType() {
		return this.valueType;
	}
	public String toString() {
		return this.getName();
	}
}

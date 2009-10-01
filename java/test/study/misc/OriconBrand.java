package study.misc;

import java.io.IOException;

import study.io.CsvOption;
import study.lang.ArrayHelper;

public class OriconBrand extends OriconName {
	private String[] aliasNames;

	public OriconBrand() {
	}
	public OriconBrand(int id, String name) {
		super(id, name);
	}
	public String[] getAliasNames() {
		if (this.aliasNames == null) {
			this.aliasNames = ArrayHelper.EMPTY_STRING_ARRAY;
		}
		return this.aliasNames;
	}
	public void setAliasNames(String[] aliasNames) {
		this.aliasNames = aliasNames;
	}
	public void addAliasName(String name) {
		final String[] array = ArrayHelper.add(this.getAliasNames(), name);
		this.setAliasNames(array);
	}
	public boolean replaceAliasName(String oldName, String newName) {
		return this.replaceAliasName(oldName, newName, false);
	}
	public boolean replaceAliasName(String oldName, String newName,
			boolean addAnyway) {
		final String[] array = this.getAliasNames();
		final int index = ArrayHelper.indexOf(array, oldName);
		if (0 <= index) {
			array[index] = newName;
			return true;
		} else if (addAnyway) {
			this.addAliasName(newName);
			return true;
		}
		return false;
	}
	public int indexOfAliasName(String name){
		return ArrayHelper.indexOf(this.getAliasNames(), name);
	}
	
	public void writeCsvRow(Appendable writer, CsvOption opt) throws IOException {
		OriconCsvHelper.writeNonNegative(writer, this.getId(), opt);
	}
	public static void writeCsvHeader(Appendable writer, CsvOption opt)
			throws IOException {
		OriconCsvHelper.writeNonEmpty(writer, "id", opt);
	}
}

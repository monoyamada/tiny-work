package study.misc;

import java.io.IOException;

import study.io.CsvOption;

public class OriconName {
	private int id;
	private String name;

	public OriconName() {
	}
	public OriconName(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void writeCsvRow(Appendable writer, CsvOption opt) throws IOException {
		OriconCsvHelper.writeNonNegative(writer, this.getId(), opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, this.getName(), opt);
	}
	public static void writeCsvHeader(Appendable writer, CsvOption opt)
			throws IOException {
		OriconCsvHelper.writeNonEmpty(writer, "id", opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, "name", opt);
	}
}

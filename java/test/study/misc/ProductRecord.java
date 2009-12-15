package study.misc;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import study.io.CsvOption;
import study.lang.Debug;

public class ProductRecord {
	private String brand;
	private String product;
	private String productType;
	private long date;

	public ProductRecord() {
		this.setDate(-1);
	}
	public String getBrand() {
		return this.brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public long getDate() {
		return this.date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getProduct() {
		return this.product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getProductType() {
		return this.productType;
	}
	public void setProductType(String type) {
		if (type != null) {
			type = type.trim();
			if (type.length() < 1) {
				type = null;
			}
		}
		this.doSetProductType(type);
	}
	protected void doSetProductType(String type) {
		this.productType = type;
	}
	public void readCsv(Calendar calendar, String line, CsvOption opt)
			throws IOException {
		final String[] tokens = line.split(opt.getColSeparator());
		if (tokens.length != 4) {
			String msg = "unexpected line: " + line;
			throw new IOException(msg);
		}
		this.setProductType(ProductRecord.readText(tokens[0], opt));
		this.setBrand(ProductRecord.readText(tokens[1], opt));
		this.setProduct(ProductRecord.readText(tokens[2], opt));
		this.setDate(ProductRecord.readDate(calendar, tokens[3]));
		if (false) {
			final Calendar x = Calendar.getInstance();
			x.setTimeInMillis(this.getDate());
			Debug.log().debug(x.getTime());
		}
	}
	protected static long readDate(Calendar calendar, String token) {
		if (token == null) {
			return -1;
		}
		token = token.trim();
		if (token.length() < 1) {
			return -1;
		}
		int year = Integer.parseInt(token);
		final int day = year % 100;
		year /= 100;
		final int month = year % 100;
		year /= 100;
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTimeInMillis();
	}
	protected static String readText(String token, CsvOption opt) {
		if (token == null) {
			return null;
		}
		token = token.trim();
		if (token.length() < 1) {
			return null;
		}
		return opt.removeQuotation(token);
	}
	public void writeCsv(Writer writer, Calendar calendar, CsvOption opt)
			throws IOException {
		ProductRecord.writeText(writer, this.getProductType(), opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, this.getBrand(), opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, this.getProduct(), opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeDate(writer, calendar, this.getDate());
	}
	public static String getDateOfCsv(Calendar calendar, long value) {
		if (value < 0) {
			return null;
		}
		final StringBuilder buffer = new StringBuilder();
		calendar.setTimeInMillis(value);
		int val = calendar.get(Calendar.YEAR);
		buffer.append(Long.toString(val));
		val = calendar.get(Calendar.MONTH)+1;
		if (val < 10) {
			buffer.append('0');
		}
		buffer.append(Long.toString(val));
		val = calendar.get(Calendar.DAY_OF_MONTH);
		if (val < 10) {
			buffer.append('0');
		}
		buffer.append(Long.toString(val));
		return buffer.toString();
	}
	protected static void writeDate(Writer writer, Calendar calendar, long value)
			throws IOException {
		if (0 <= value) {
			writer.write(ProductRecord.getDateOfCsv(calendar, value));
		}
	}
	protected static void writeText(Writer writer, String value, CsvOption opt)
			throws IOException {
		if (value != null || 0 < value.length()) {
			writer.write(opt.addQuotation(value));
		}
	}
	public static void writeNumber(Writer writer, long value, CsvOption opt)
			throws IOException {
		writer.write(Long.toString(value));
	}
	public static void writeNumber(Writer writer, double value, CsvOption opt)
			throws IOException {
		writer.write(Double.toString(value));
	}
	public static void writeCsvHeader(Writer writer, CsvOption opt)
			throws IOException {
		ProductRecord.writeText(writer, "type", opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "brand", opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "product", opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "date", opt);
	}
}

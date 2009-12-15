package study.misc;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import study.io.CsvOption;

public class SalesRecord extends ProductRecord {
	public static long SECOND_IN_MILISECOND = 1000;
	public static long MINUT_IN_MILISECOND = 60 * SECOND_IN_MILISECOND;
	public static long HOUR_IN_MILISECOND = 60 * MINUT_IN_MILISECOND;
	public static long DAY_IN_MILISECOND = 24 * HOUR_IN_MILISECOND;
	public static long WEEK_IN_MILISECOND = 7 * DAY_IN_MILISECOND;
	public static final String DAILY_SALES = "dailySales";
	public static final String WEEKLY_SALES = "weeklySales";

	private long day;
	private int sales;
	private String observation;

	public SalesRecord() {
		this.day = -1;
		this.sales = -1;
	}
	public long getDay() {
		return this.day;
	}
	public void setDay(long day) {
		this.day = day;
	}
	public int getSales() {
		return this.sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
	public String getObservation() {
		return this.observation;
	}
	public void setObservation(String observation) {
		this.observation = observation;
	}
	public void readCsv(Calendar calendar, String line, CsvOption opt)
			throws IOException {
		final String[] tokens = line.split(opt.getColSeparator());
		if (tokens.length != 6) {
			String msg = "unexpected line: " + line;
			throw new IOException(msg);
		}
		this.setProductType(ProductRecord.readText(tokens[0], opt));
		this.setBrand(ProductRecord.readText(tokens[1], opt));
		this.setProduct(ProductRecord.readText(tokens[2], opt));
		this.setDate(ProductRecord.readDate(calendar, tokens[3]));
		this.setDay(Long.parseLong(tokens[4].trim()));
		this.setSales(Integer.parseInt(tokens[5].trim()));
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
		writer.write(opt.getColSeparator());
		if (0 <= this.getDay()) {
			ProductRecord.writeNumber(writer, this.getDay(), opt);
		}
		writer.write(opt.getColSeparator());
		if (0 <= this.getSales()) {
			ProductRecord.writeNumber(writer, this.getSales(), opt);
		}
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, this.getObservation(), opt);
	}
	public static void writeCsvHeader(Writer writer, CsvOption opt)
			throws IOException {
		ProductRecord.writeCsvHeader(writer, opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "day", opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "sales", opt);
		writer.write(opt.getColSeparator());
		ProductRecord.writeText(writer, "observation", opt);
	}
}

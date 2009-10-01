package study.misc;

import java.io.IOException;

import study.io.CsvOption;

public class OriconSales extends OriconCsvHelper {
	private static final boolean CSV_WITH_BRAND = false;
	private int beginDate;
	private int endDate;
	private int days;
	private int brand;
	private int product;
	private int sales;

	public OriconSales() {
		this.beginDate = this.endDate = -1;
		this.days = 0;
		this.sales = -1;
	}
	public void clear() {
		this.beginDate = this.endDate = -1;
		this.days = 0;
		this.sales = -1;
	}
	public int getBeginDate() {
		return this.beginDate;
	}
	public void setBeginDate(int beginDate) {
		this.beginDate = beginDate;
	}
	public int getBrand() {
		return this.brand;
	}
	public void setBrand(int brand) {
		this.brand = brand;
	}
	public int getDays() {
		return this.days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getEndDate() {
		return this.endDate;
	}
	public void setEndDate(int endDate) {
		this.endDate = endDate;
	}
	public int getProduct() {
		return this.product;
	}
	public void setProduct(int product) {
		this.product = product;
	}
	public int getSales() {
		return this.sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}

	public void writeCsvRow(Appendable writer, CsvOption opt) throws IOException {
		if (0 <= this.getBeginDate()) {
			writer.append(Integer.toString(this.getBeginDate()));
		}
		writer.append(opt.getColSeparator());
		if (0 <= this.getEndDate()) {
			writer.append(Integer.toString(this.getEndDate()));
		}
		writer.append(opt.getColSeparator());
		if (0 <= this.getDays()) {
			writer.append(Integer.toString(this.getDays()));
		}
		if (OriconSales.CSV_WITH_BRAND) {
			writer.append(opt.getColSeparator());
			if (0 <= this.getBrand()) {
				writer.append(Integer.toString(this.getBrand()));
			}
		}
		writer.append(opt.getColSeparator());
		if (0 <= this.getProduct()) {
			writer.append(Integer.toString(this.getProduct()));
		}
		writer.append(opt.getColSeparator());
		if (0 <= this.getSales()) {
			writer.append(Integer.toString(this.getSales()));
		}
	}

	public static void writeCsvHeader(Appendable writer, CsvOption opt)
			throws IOException {
		writer.append("date0");
		writer.append(opt.getColSeparator());
		writer.append("date1");
		writer.append(opt.getColSeparator());
		writer.append("days");
		if (OriconSales.CSV_WITH_BRAND) {
			writer.append(opt.getColSeparator());
			writer.append("brand");
		}
		writer.append(opt.getColSeparator());
		writer.append("product");
		writer.append(opt.getColSeparator());
		writer.append("sales");
	}
}

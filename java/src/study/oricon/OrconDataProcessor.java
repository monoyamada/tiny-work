package study.oricon;

import static study.oricon.OriconConstant.CSV_FILE_EXTENSION;
import static study.oricon.OriconConstant.PRODUCT_FILE;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Calendar;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.Messages;
import study.oricon.OriconProductData.Product;
import study.oricon.OriconSalesData.Sales;
import study.primitive.LongArrayList;
import study.primitive.PrimitiveHelper;

public class OrconDataProcessor extends OriconDataBuilder {
	public static final String OUTPUT_DIRECTORY = "work";
	protected static final Column[] DATE_COLUMN_ARRAY = { //
	Column.WEEK_OF_MONTH //
			, Column.MONTH //
	};

	public static enum Column {
		WEEK_OF_MONTH("weekOfMonth") //
		, MONTH("month") //
		;

		private String name;

		private Column(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}

	private File outputDirectory;

	public OrconDataProcessor(OriconWorkspace workspace,
			OriconProductType productType) {
		super(workspace, productType);
	}
	public File getOutputDirectory() {
		if (this.outputDirectory == null) {
			this.outputDirectory = this.getWorkspace().newDirectory(OUTPUT_DIRECTORY);
		}
		return this.outputDirectory;
	}

	public void splitDataByYear() throws IOException {
		final Calendar date = OriconDateHelper.newCalendar();
		final LongArrayList firstDayBuffer = new LongArrayList(1024);
		final OriconSalesData salesData = this.getSalesData();
		OriconSalesData.DataCursor cursor = null;
		Sales data = null;
		try {
			cursor = salesData.getTokenCursor();
			for (; cursor.move();) {
				if (cursor.isDataLine()) {
					if (data == null) {
						data = new Sales();
					}
					cursor.getData(data);
					final int day = data.getDay();
					final long msec = OriconDateHelper.dayToSystemTime(day);
					date.setTimeInMillis(msec);
					break;
				}
			}
		} finally {
			FileHelper.close(cursor);
		}

		while (data != null) {
			firstDayBuffer.pushLong(data.getDay());
			data = this.splitSalesByYear(cursor, date, data);
		}

		if (firstDayBuffer.getSize() < 1) {
			return;
		}

		final int[] firstDays = PrimitiveHelper.toIntegerArray(firstDayBuffer);

		for (int day : firstDays) {
			final int year = OriconDateHelper.getYear(day);
			this.splitProductByYear(year);
		}

		this.writeYearDates(firstDays);
	}
	protected void writeYearDates(int[] firstDays) throws IOException {
		final int[] salesDays = this.getSalesData().getDayArray();
		final int[] deltaDays = new int[firstDays.length];
		for (int iYear = 0, nYear = firstDays.length; iYear < nYear; ++iYear) {
			int delta = 7;
			if (iYear != 0) {
				final int index = Arrays.binarySearch(salesDays, firstDays[iYear]);
				if (index < 0) {
					throw new IOException(Messages.getFailedOperation("find time"));
				}
				delta = salesDays[index] - salesDays[index - 1];
			}
			deltaDays[iYear] = delta;
		}
	}
	protected void splitProductByYear(int year) throws IOException {
		final int day0 = OriconDateHelper.getFirstDay(year);
		final int day1 = OriconDateHelper.getFirstDay(year + 1);
		final File file = new File(this.getOutputDirectory(), this
				.getYearProductFileName(year)
				+ CSV_FILE_EXTENSION);
		final Calendar date = OriconDateHelper.newCalendar();
		final OriconProductData productData = this.getProductData();
		OriconProductData.DataCursor cursor = null;
		Writer writer = null;
		try {
			cursor = productData.getTokenCursor();
			writer = FileHelper.getWriter(file, this.getCsvOption().getEncoding());
			final Product data = new Product();
			this.writeDateOfYearHeader(writer, true);
			productData.writeCsvHeader(writer);
			for (; cursor.move();) {
				if (cursor.isDataLine()) {
					cursor.getData(data);
					final int day = data.getReleaseDay();
					OriconDateHelper.dayToSystemTime(date, day);
					if (day0 <= day && day < day1
							&& data.getProductType() == this.getProductType()) {
						this.writeDateOfYearData(writer, day, date, true);
						productData.writeCsvData(writer, data);
					}
				}
			}
		} finally {
			FileHelper.close(writer);
		}
	}

	protected Sales splitSalesByYear(OriconSalesData.DataCursor cursor,
			Calendar date, Sales data) throws IOException {
		final int year = date.get(Calendar.YEAR);
		final OriconSalesData salesData = this.getSalesData();
		final File file = new File(this.getOutputDirectory(), this
				.getYearSalesFileName(year)
				+ CSV_FILE_EXTENSION);
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(file, this.getCsvOption().getEncoding());
			this.writeDateOfYearHeader(writer, true);
			salesData.writeCsvHeader(writer);
			this.writeDateOfYearData(writer, data.getDay(), date, true);
			salesData.writeCsvData(writer, data);
			for (; cursor.move();) {
				if (cursor.isDataLine()) {
					cursor.getData(data);
					final int day = data.getDay();
					OriconDateHelper.dayToSystemTime(date, day);
					if (year < date.get(Calendar.YEAR)) {
						final File newFile = new File(this.getOutputDirectory(), this
								.getYearSalesFileName(year)
								+ CSV_FILE_EXTENSION);
						file.renameTo(newFile);
						return data;
					}
					this.writeDateOfYearData(writer, day, date, true);
					salesData.writeCsvData(writer, data);
				}
			}
			return null;
		} finally {
			FileHelper.close(writer);
		}
	}

	protected void writeDateOfYearHeader(Writer writer, boolean appendDelim)
			throws IOException {
		final CsvOption opt = this.getCsvOption();
		if (opt.isHeader()) {
			for (int i = 0, n = DATE_COLUMN_ARRAY.length; i < n; ++i) {
				if (i != 0) {
					this.writeColSeparator(writer);
				}
				this.writeText(writer, DATE_COLUMN_ARRAY[i].getName());
			}
			if (appendDelim && 0 < DATE_COLUMN_ARRAY.length) {
				this.writeColSeparator(writer);
			}
		}
	}
	protected void writeDateOfYearData(Writer writer, int day, Calendar date,
			boolean appendDelim) throws IOException {
		for (int i = 0, n = DATE_COLUMN_ARRAY.length; i < n; ++i) {
			if (i != 0) {
				this.writeColSeparator(writer);
			}
			switch (DATE_COLUMN_ARRAY[i]) {
			case WEEK_OF_MONTH:
				writer.write(Integer.toString(date.get(Calendar.WEEK_OF_MONTH)));
				break;
			case MONTH:
				writer.write(Integer.toString(date.get(Calendar.MONTH) + 1));
				break;
			default:
				throw new Error("bug");
			}
		}
		if (appendDelim && 0 < DATE_COLUMN_ARRAY.length) {
			this.writeColSeparator(writer);
		}
	}

	protected void writeRowSeparator(Writer writer) throws IOException {
		final CsvOption opt = this.getCsvOption();
		writer.write(opt.getRowSeparator());
		writer.flush();
	}
	protected void writeColSeparator(Writer writer) throws IOException {
		final CsvOption opt = this.getCsvOption();
		writer.write(opt.getColSeparator());
	}

	protected void writeTexts(Writer writer, String... tokens) throws IOException {
		final CsvOption opt = this.getCsvOption();
		final String delim = opt.getColSeparator();
		for (int i = 0, n = tokens.length; i < n; ++i) {
			if (i != 0 && delim != null) {
				writer.write(delim);
			}
			this.writeText(writer, tokens[i]);
		}
	}
	protected void writeText(Writer writer, String token) throws IOException {
		if (token == null || token.length() < 1) {
			return;
		}
		final String q = this.getCsvOption().getQuotation();
		if (q != null && 0 < q.length()) {
			writer.write(q);
			writer.write(token);
			writer.write(q);
		} else {
			writer.write(token);
		}
	}

	public String getYearProductFileName(int year) {
		return this.getProductType().getName() + "-" + PRODUCT_FILE + "-" + year;
	}
	public String getYearSalesFileName(int year) {
		return this.getSalesDataFileName() + "-" + year;
	}
}

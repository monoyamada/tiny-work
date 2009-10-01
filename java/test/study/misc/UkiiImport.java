package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.Debug;
import study.lang.Messages;

public class UkiiImport {
	public void ukiiToSales(File output, File input, ProductRecordList products,
			CsvOption opt) throws IOException {
		BufferedReader reader = null;
		Writer writer = null;
		try {
			reader = FileHelper.getBufferedReader(input, CsvOption.ENCODING);
			writer = FileHelper.getWriter(output);
			this.ukiiToSales(writer, reader, products, opt);
		} finally {
			FileHelper.close(reader);
			FileHelper.close(writer);
		}
	}
	protected void ukiiToSales(Writer writer, BufferedReader reader,
			ProductRecordList products, CsvOption opt) throws IOException {
		final Calendar calendar = Calendar.getInstance();
		SalesRecord.writeCsvHeader(writer, opt);
		writer.write(opt.getRowSeparator());
		writer.flush();
		String line = reader.readLine();
		int iLine = 1;
		for (; line != null; line = reader.readLine(), ++iLine) {
			line = line.trim();
			if (line.length() < 1 || line.startsWith(CsvOption.COMMENT_MARKER)) {
				continue;
			}
			try {
				this.ukiiToSalesRecord(writer, line, calendar, products, opt);
			} catch (Exception ex) {
				Debug.log().info(ex.getMessage());
				Debug.log().info("[" + iLine + "] " + line);
				continue;
			}
		}
	}
	protected void ukiiToSalesRecord(Writer writer, String line,
			Calendar calendar, ProductRecordList products, CsvOption opt)
			throws IOException {
		final String[] tokens = line.split("\\s+");
		if (tokens.length != 8) {
			String msg = Messages.getUnexpectedValue("number of tokens", 8,
					tokens.length);
			throw new IOException(msg);
		}
		final String brand = tokens[6].trim();
		final String product = tokens[7].trim();
		final ProductRecord data = products.getProductRecord(brand, product);
		if (data == null) {
			String msg = Messages.getNull("product data of "
					+ Messages.getTuple("(", ")", brand, product));
			throw new IOException(msg);
		}
		if (true) {
			final StringWriter buffer = new StringWriter();
			data.writeCsv(buffer, calendar, opt);
			Debug.log().debug(buffer);
		}
		final long date = data.getDate();
		if (false) {
			final Calendar y = Calendar.getInstance();
			y.setTimeInMillis(date);
			Debug.log().debug(
					ProductRecord.getDateOfCsv(calendar, date) + " : " + y.getTime());
		}
		final SalesRecord record = new SalesRecord();
		record.setBrand(brand);
		record.setProduct(product);
		record.setProductType(data.getProductType());
		record.setObservation(SalesRecord.DAILY_SALES);
		for (int i = 0; i < 6; ++i) {
			final int value = UkiiImport.getStarInteger(tokens[i]);
			record.setSales(value);
			record.setDay(i);
			if (false) {
				final long x = date + SalesRecord.DAY_IN_MILISECOND;
				final Calendar y = Calendar.getInstance();
				y.setTimeInMillis(x);
				Debug.log().debug(y.getTime());
			}
			record.setDate(date + SalesRecord.DAY_IN_MILISECOND);
			if (false) {
				final Calendar x = Calendar.getInstance();
				x.setTimeInMillis(record.getDate());
				Debug.log().debug(x.getTime());
			}
			record.writeCsv(writer, calendar, opt);
			writer.write(opt.getRowSeparator());
			writer.flush();
		}
	}
	protected static int getStarInteger(String token) {
		int begin = 0;
		int end = token.length();
		for (; begin < end; ++begin) {
			if (UkiiImport.isStarSpace(token.charAt(begin))) {
				continue;
			}
			break;
		}
		for (; begin < end; --end) {
			if (UkiiImport.isStarSpace(token.charAt(end - 1))) {
				continue;
			}
			break;
		}
		return Integer.parseInt(token.substring(begin, end));
	}
	protected static boolean isStarSpace(int ch) {
		switch (ch) {
		case '*':
		case ' ':
		case '\t':
		case '\n':
		case '\f':
		case 0x0B:
			return true;
		default:
			return false;
		}
	}
}

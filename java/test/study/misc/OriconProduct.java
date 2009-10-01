package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.Messages;
import study.lang.StringHelper;

public class OriconProduct extends OriconName {
	public static final OriconProduct[] EMPTY_ARRAY = {};
	private int brand;
	private long releaseDay;

	public OriconProduct() {
		this.releaseDay = -1;
	}
	public OriconProduct(int id, String name) {
		super(id, name);
		this.releaseDay = -1;
	}
	public int getBrand() {
		return this.brand;
	}
	public void setBrand(int brand) {
		this.brand = brand;
	}

	public long getReleaseDay() {
		return this.releaseDay;
	}
	public void setReleaseDay(long releaseDay) {
		this.releaseDay = releaseDay;
	}
	public void writeCsvRow(Appendable writer, CsvOption opt) throws IOException {
		OriconCsvHelper.writeNonNegative(writer, this.getId(), opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonNegative(writer, this.getBrand(), opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonNegative(writer, this.getReleaseDay(), opt);
	}

	public static void writeCsvHeader(Appendable writer, CsvOption opt)
			throws IOException {
		OriconCsvHelper.writeNonEmpty(writer, "id", opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, "brand", opt);
		writer.append(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, "releaseDay", opt);
	}

	public static void writeCsv(File file, CsvOption opt, OriconProduct[] array)
			throws IOException {
		Writer writer = null;
		try {
			if (opt.isHeader()) {
				writer = FileHelper.getWriter(file, opt.getEncoding());
			}
			OriconProduct.writeCsvHeader(writer, opt);
			OriconCsvHelper.writeRowSeparator(writer, opt);
			for (int i = 0, n = array.length; i < n; ++i) {
				array[i].writeCsvRow(writer, opt);
				OriconCsvHelper.writeRowSeparator(writer, opt);
			}
		} finally {
			FileHelper.close(writer);
		}
	}
	public static OriconProduct[] readCsv(File file, CsvOption opt)
			throws IOException {
		BufferedReader reader = null;
		try {
			final List<OriconProduct> buffer = new ArrayList<OriconProduct>(1024);
			final String[] tokens = new String[3];
			reader = FileHelper.getBufferedReader(file, opt.getEncoding());
			int iLine = 1;
			String line = reader.readLine();
			for (; line != null; line = reader.readLine(), ++iLine) {
				if (opt.isHeader() && iLine == 1) {
					continue;
				} else if (OriconProduct.isCommentLine(line, opt)) {
					continue;
				}
				final int nToken = OriconProduct.split(tokens, 0, tokens.length, line,
						opt);
				if (nToken < tokens.length) {
					throw new IOException(Messages.getUnexpectedValue("#column", 3,
							nToken));
				}
				final OriconProduct data = OriconProduct.newData(tokens[0], tokens[1],
						tokens[2]);
				buffer.add(data);
			}
			return buffer.toArray(OriconProduct.EMPTY_ARRAY);
		} finally {
			FileHelper.close(reader);
		}
	}
	protected static int split(String[] output, int begin, int end, String line,
			CsvOption opt) {
		final String delim = opt.getColSeparator();
		final int n = line.length();
		final int nToken = end - begin;
		int ind = 0;
		int iToken = 0;
		boolean complete = false;
		for (; iToken < nToken && ind < n; ++iToken) {
			int ind1 = line.indexOf(delim, ind);
			if (ind1 < 0) {
				output[begin + iToken] = line.substring(ind);
				ind = n;
				complete = true;
			} else {
				output[begin + iToken] = line.substring(ind, ind1);
				ind = ind1 + delim.length();
			}
		}
		if (!complete && iToken < nToken) {
			output[iToken++] = "";
		}
		return iToken;
	}
	protected static OriconProduct newData(String id, String brand, String release) {
		final OriconProduct data = new OriconProduct();
		data.setId(OriconProduct.parseInteger(id, -1));
		data.setBrand(OriconProduct.parseInteger(brand, -1));
		data.setReleaseDay(OriconProduct.parseInteger(release, -1));
		return data;
	}
	protected static int parseInteger(String token, int def) {
		if (token == null || token.length() < 1) {
			return def;
		}
		try {
			return Integer.parseInt(token.trim());
		} catch (NumberFormatException ex) {
		}
		return def;
	}
	protected static boolean isCommentLine(String line, CsvOption opt) {
		final String mark = opt.getCommentMarker();
		final int n = line.length();
		if (n < 1) {
			return true;
		} else if (mark == null) {
			return false;
		}
		final int ind = OriconProduct.skipSpaces(line, 0, n);
		if (ind == n || StringHelper.startsWith(line, ind, mark, false)) {
			return true;
		}
		return false;
	}
	protected static int skipSpaces(String line, int begin, int end) {
		for (; begin < end && Character.isWhitespace(line.charAt(begin)); ++begin) {
		}
		return begin;
	}
}

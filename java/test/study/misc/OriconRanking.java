package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.ArrayHelper;
import study.lang.Debug;
import study.lang.Messages;
import study.lang.NumberHelper;
import study.lang.ObjectHelper;
import study.lang.StringHelper;

public class OriconRanking implements Cloneable {
	private static final CsvOption CSV_OPTION = new CsvOption();
	protected static final String ORICON_URI = "http://www.oricon.co.jp/";
	protected static final String ORICON_QUERY_URI = ORICON_URI
			+ "search/result.php";
	protected static final String ORICON_DATE_QUERY = "kbn=ja&types=rnk&year=${year}&month=${month}&week=${week}";
	protected static final String ORICON_ALBUM_QUERY = "submit5.x=30&submit5.y=8";
	// "submit5.x=13&submit5.y=12";
	protected static final String ORICON_SINGLE_QUERY = "submit4.x=26&submit4.y=14";
	// "submit4.x=15&submit4.y=8";
	public static final String ORICON_PRODUCT_URI = ORICON_URI
			+ "music/release/d/";
	public static final String ORICON_BRAND_URI = ORICON_URI
	+ "prof/artist/";
	public static final OriconRanking[] EMPTY_ARRAY = {};
	public static final int MAX_RANK = 30;
	public static final int ALBUM_TYPE = 1;
	public static final int SINGLE_TYPE = ALBUM_TYPE << 1;
	public static final long SECOND_IN_MSEC = 1000;
	public static final long MINUT_IN_MSEC = 60 * SECOND_IN_MSEC;
	public static final long HOUR_IN_MSEC = 60 * MINUT_IN_MSEC;
	public static final long DAY_IN_MSEC = 24 * HOUR_IN_MSEC;
	public static final String HTML_FILE_EXTENSION = ".html";
	private static final String ALBUM_TYPE_NAME = "album";
	private static final String SINGLE_TYPE_NAME = "single";
	private static final String BRAND_PATH_PREFIX = "prof/artist/";
	private static final String PRODUCT_PATH_PREFIX = "music/release/d/";
	public static final long BASE_TIME_IN_MSEC = OriconRanking
			.getBaseTimeInMsec();
	public static final String ORICON_HTML_ENCODING = "Shift_JIS";

	public static long getBaseTimeInMsec() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		return calendar.getTimeInMillis();
	}

	private String productName;
	private int productId;
	private String brandName;
	private int brandId;
	private int sales;

	public OriconRanking() {
		this.productId = this.brandId = this.sales = -1;
	}
	public OriconRanking clone() {
		try {
			return (OriconRanking) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	public void copy(OriconRanking x) {
		if (this == x) {
			return;
		}
		this.productId = x.productId;
		this.productName = x.productName;
		this.brandId = x.brandId;
		this.brandName = x.brandName;
		this.sales = x.sales;
	}
	public void clear() {
		this.productId = this.brandId = this.sales = -1;
		this.productName = this.brandName = null;
	}
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		try {
			this.getCsv(buffer, OriconRanking.CSV_OPTION);
		} catch (IOException ex) {
			ex.printStackTrace();
			return StringHelper.repeat(OriconRanking.CSV_OPTION.getColSeparator(), 4);
		}
		return buffer.toString();
	}
	public void getCsv(Appendable output, CsvOption opt) throws IOException {
		if (0 <= this.getProductId()) {
			output.append(Integer.toString(this.getProductId()));
		}
		output.append(opt.getColSeparator());
		output.append(opt.addQuotation(this.getProductName()));
		output.append(opt.getColSeparator());
		if (0 <= this.getBrandId()) {
			output.append(Integer.toString(this.getBrandId()));
		}
		output.append(opt.getColSeparator());
		output.append(opt.addQuotation(this.getBrandName()));
		output.append(opt.getColSeparator());
		if (0 <= this.getSales()) {
			output.append(Integer.toString(this.getSales()));
		}
	}
	public void setBrand(int id, String name) {
		this.setBrandId(id);
		this.setBrandName(name);
	}
	public void setProduct(int id, String name) {
		this.setProductId(id);
		this.setProductName(name);
	}
	public int getBrandId() {
		return this.brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public String getBrandName() {
		return this.brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public int getProductId() {
		return this.productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return this.productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getSales() {
		return this.sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}

	public static OriconRanking[] readRanking(File input) throws IOException {
		final OriconRanking[] ranking = new OriconRanking[OriconRanking.MAX_RANK];
		final int n = OriconRanking.readRanking(ranking, input);
		if (n < ranking.length) {
			return ArrayHelper.sub(ranking, 0, n);
		}
		return ranking;
	}
	public static int readRanking(OriconRanking[] output, File input)
			throws IOException {
		if (output.length < 1) {
			return 0;
		}
		final String table = OriconRanking.getTable(input);
		return OriconRanking.readRanking(output, table);
	}
	protected static int readRanking(OriconRanking[] output, String table)
			throws IOException {
		if (output.length < 1) {
			return 0;
		}
		final String[] tokens = new String[3];
		final String[][] attrs = new String[2][100];
		final OriconRanking ranking = new OriconRanking();
		final int LOOK_ROW = 0;
		final int LOOK_COL = LOOK_ROW + 1;
		final int LOOK_BRAND = LOOK_COL + 1;
		final int LOOK_PRODUCT = LOOK_BRAND + 1;
		final int LOOK_SALES = LOOK_PRODUCT + 1;
		int got = 0;
		int rank = 0;
		int look = LOOK_ROW;
		int iRow = 0;
		OUTER: for (int begin = 0, end = table.length(); begin < end;) {
			begin = OriconRanking.skipSpaces(table, begin, end);
			if (begin == end) {
				break;
			}
			begin = OriconRanking.readTag(tokens, table, begin, end);
			final String tag = tokens[0];
			if (ObjectHelper.equals(tag, "table")) {
			} else if (ObjectHelper.equals(tag, "/table")) {
			} else if (ObjectHelper.equals(tag, "tr")) {
				switch (look) {
				case LOOK_ROW:
					ranking.clear();
					look = LOOK_COL;
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_ROW,
							look));
				}
			} else if (ObjectHelper.equals(tag, "/tr")) {
				switch (look) {
				case LOOK_COL:
					if (((got >> LOOK_BRAND) & (got >> LOOK_PRODUCT)
							& (got >> LOOK_SALES) & 1) == 1) {
						if (rank < output.length) {
							if (output[rank] == null) {
								output[rank++] = ranking.clone();
							} else {
								output[rank++].copy(ranking);
							}
							if (output.length <= rank) {
								break OUTER;
							}
						}
					}
					++iRow;
					got = 0;
					look = LOOK_ROW;
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_COL,
							look));
				}
			} else if (ObjectHelper.equals(tag, "td")) {
				switch (look) {
				case LOOK_COL:
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_COL,
							look));
				}
				final String val = OriconRanking.getAttribute(attrs, tokens[1],
						"class", null);
				if (val == null) {
				} else if (val.equals("artist")) {
					look = LOOK_BRAND;
				} else if (val.equals("title")) {
					look = LOOK_PRODUCT;
				} else if (val.equals("number")) {
					// Debug.log().debug("sales=" + tokens[2]);
					int value = Integer.parseInt(tokens[2].trim());
					ranking.setSales(value);
					got |= 1 << LOOK_SALES;
				} else {
				}
			} else if (ObjectHelper.equals(tag, "/td")) {
				look = LOOK_COL;
			} else if (ObjectHelper.equals(tag, "a")) {
				int id;
				String name;
				switch (look) {
				case LOOK_PRODUCT:
					// Debug.log().debug("product=" + tokens[2]);
					name = OriconRanking.getAttribute(attrs, tokens[1], "href", null);
					id = OriconRanking.pathToProductId(name);
					name = tokens[2].trim();
					ranking.setProduct(id, name);
					got |= 1 << LOOK_PRODUCT;
					look = LOOK_COL;
					break;
				case LOOK_BRAND:
					name = OriconRanking.getAttribute(attrs, tokens[1], "href", null);
					id = OriconRanking.pathToBrandId(name);
					name = tokens[2].trim();
					ranking.setBrand(id, name);
					got |= 1 << LOOK_BRAND;
					look = LOOK_COL;
					break;
				}
			}
		}
		return rank;
	}

	protected static int pathToBrandId(String path) {
		final String prefix = OriconRanking.BRAND_PATH_PREFIX;
		if (path == null || path.length() < prefix.length()) {
			return -1;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.length() < prefix.length()) {
			return -1;
		}
		path = path.substring(prefix.length());
		try {
			final int id = Integer.parseInt(path);
			return 0 <= id ? id : -1;
		} catch (NumberFormatException ex) {
		}
		return -1;
	}
	protected static int pathToProductId(String path) {
		final String prefix = OriconRanking.PRODUCT_PATH_PREFIX;
		if (path == null || path.length() < prefix.length()) {
			return -1;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.length() < prefix.length()) {
			return -1;
		}
		path = path.substring(prefix.length());
		final int ind = path.indexOf('/');
		if (ind < 0) {
			return -1;
		}
		path = path.substring(0, ind);
		try {
			final int id = Integer.parseInt(path);
			return 0 <= id ? id : -1;
		} catch (NumberFormatException ex) {
		}
		return -1;
	}
	protected static String getAttribute(String[][] buffer, String text,
			String name, String def) throws IOException {
		final int n = OriconRanking.getAttributes(buffer, text);
		return OriconRanking.getAttribute(buffer, 0, n, name, def);
	}
	protected static String getAttribute(String[][] attrs, int begin, int end,
			String name, String def) {
		int index = ArrayHelper.indexOf(attrs[0], begin, end, name);
		return 0 <= index ? attrs[1][index] : def;
	}
	protected static int getAttributes(String[][] output, String text)
			throws IOException {
		final int max = output.length;
		int n = 0;
		int begin = 0;
		final int end = text.length();
		for (; begin < end && n < max;) {
			begin = OriconRanking.skipSpaces(text, begin, end);
			if (end <= begin) {
				break;
			}
			int index = text.indexOf('=', begin);
			if (index < 0 || end <= index + 2) {
				throw new IOException(text);
			}
			final String name = text.substring(begin, index);
			begin = index + 1;
			index = text.indexOf('"', begin);
			if (index < 0 || end <= index + 1) {
				throw new IOException(text);
			}
			begin = index + 1;
			index = text.indexOf('"', begin);
			if (index < 0 || end <= index) {
				throw new IOException(text);
			}
			final String value = text.substring(begin, index);
			begin = index + 1;
			output[0][n] = name;
			output[1][n] = value;
			++n;
		}
		return n;
	}
	protected static int readTag(String[] output, String text, int begin, int end)
			throws IOException {
		Arrays.fill(output, null);
		for (; begin < end && text.charAt(begin++) != '<';) {
		}
		if (end <= begin) {
			return end;
		}
		int end1 = text.indexOf('>', begin);
		if (end <= end1) {
			return end;
		}
		final String tag = text.substring(begin, end1);
		final String[] tokens = tag.split("\\s+", 2);
		switch (tokens.length) {
		case 0:
			throw new IOException(tag);
		case 1:
			output[0] = tokens[0].trim();
			break;
		case 2:
			output[0] = OriconRanking.trimToken(tokens[0]);
			output[1] = OriconRanking.trimToken(tokens[1]);
			break;
		default:
			throw new Error("bug");
		}
		begin = ++end1;
		end1 = text.indexOf('<', begin);
		end1 = 0 <= end1 ? end1 : end;
		output[2] = OriconRanking.trimToken(text.substring(begin, end1));
		return end1;
	}
	protected static String trimToken(String token) {
		if (token == null || token.length() < 1) {
			return null;
		}
		token = token.trim();
		return 0 < token.length() ? token : null;
	}
	protected static int skipSpaces(String text, int begin, int end) {
		for (; begin < end && Character.isWhitespace(text.charAt(begin)); ++begin) {
		}
		return begin;
	}
	protected static String getTable(File file) throws IOException {
		final int LOOK_FOR_BEGIN = 1;
		final int LOOK_FOR_END = LOOK_FOR_BEGIN + 1;
		final String eol = "\n";
		final StringBuilder buffer = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = FileHelper.getBufferedReader(file,
					OriconRanking.ORICON_HTML_ENCODING);
			int lookFor = LOOK_FOR_BEGIN;
			String line = reader.readLine();
			int iLine = 1;
			OUTER: for (; line != null; ++iLine, line = reader.readLine()) {
				int ind = -1;
				switch (lookFor) {
				case LOOK_FOR_BEGIN:
					ind = line.indexOf("class=\"search_list\"");
					if (0 <= ind) {
						ind = line.indexOf("<table");
						if (0 <= ind) {
							lookFor = LOOK_FOR_END;
							buffer.append(line.substring(ind) + eol);
						} else {
							String msg = Messages.getUnexpectedLine(file, iLine, line);
							throw new IOException(msg);
						}
					}
					break;
				case LOOK_FOR_END:
					ind = line.indexOf("</table>");
					if (0 <= ind) {
						ind += "</table>".length();
						buffer.append(line.substring(0, ind) + eol);
						break OUTER;
					} else {
						buffer.append(line + eol);
					}
					break;
				default:
					throw new IOException("could not read table");
				}
			}
		} finally {
			FileHelper.close(reader);
		}
		return buffer.toString();
	}
	public static boolean isAlbum(int type) {
		return (type & OriconRanking.ALBUM_TYPE) != 0;
	}
	public static boolean isSingle(int type) {
		return (type & OriconRanking.ALBUM_TYPE) != 0;
	}
	public static String getTypeName(int type) {
		switch (type) {
		case OriconRanking.ALBUM_TYPE:
			return OriconRanking.ALBUM_TYPE_NAME;
		case OriconRanking.SINGLE_TYPE:
			return OriconRanking.SINGLE_TYPE_NAME;
		default:
			return null;
		}
	}
	public static String getTypeQuery(int type) {
		switch (type) {
		case OriconRanking.ALBUM_TYPE:
			return OriconRanking.ORICON_ALBUM_QUERY;
		case OriconRanking.SINGLE_TYPE:
			return OriconRanking.ORICON_SINGLE_QUERY;
		default:
			return null;
		}
	}
	public static String getFileName(int type, int year, int month, int week) {
		final String kind = OriconRanking.getTypeName(type);
		if (kind == null) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("type",
					OriconRanking.ALBUM_TYPE + " or " + OriconRanking.SINGLE_TYPE, type));
		}
		return kind + year + "" + (month < 10 ? "0" + month : month) + "" + week;
	}
	public static long fileToSystemTime(Calendar calendar, File file) {
		return OriconRanking.fileToSystemTime(calendar, file.getName());
	}
	protected static long fileToSystemTime(Calendar calendar, String name) {
		String prefix = null;
		if (StringHelper.startsWith(name, OriconRanking.ALBUM_TYPE_NAME, true)) {
			prefix = OriconRanking.ALBUM_TYPE_NAME;
		} else if (StringHelper.startsWith(name, OriconRanking.SINGLE_TYPE_NAME,
				true)) {
			prefix = OriconRanking.SINGLE_TYPE_NAME;
		} else {
			return -1;
		}
		final String ext = OriconRanking.HTML_FILE_EXTENSION;
		if (!StringHelper.endsWith(name, ext, true)) {
			return -1;
		}
		final String date = name.substring(0, name.length() - ext.length())
				.substring(prefix.length());
		int value = Integer.parseInt(date);
		final int week = value % 10;
		value /= 10;
		final int month = value % 100;
		final int year = value / 100;
		return OriconRanking.getSystemTimeByWeek(calendar, year, month, week);
	}
	public static long fileToCsvTime(Calendar calendar, File file) {
		final long msec = OriconRanking.fileToSystemTime(calendar, file);
		return OriconRanking.systemToCsvTime(msec);
	}
	public static long getSystemTimeByWeek(Calendar calendar, int year,
			int month, int week) {
		final long sunday = OriconRanking.getFirstSundayInMSec(calendar, year,
				month);
		return sunday + (week - 1) * 7 * OriconRanking.DAY_IN_MSEC;
	}
	public static long systemToCsvTime(long msec) {
		return (msec - OriconRanking.BASE_TIME_IN_MSEC) / OriconRanking.DAY_IN_MSEC;
	}
	public static long csvToSystemTime(long day) {
		return (day + OriconRanking.BASE_TIME_IN_MSEC) * OriconRanking.DAY_IN_MSEC;
	}
	protected static long getFirstSundayInMSec(Calendar calendar, int year,
			int month) {
		final int base = 1;
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, base);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.SUNDAY:
			day = base;
			break;
		case Calendar.MONDAY:
			day = base + 6;
			break;
		case Calendar.TUESDAY:
			day = base + 5;
			break;
		case Calendar.WEDNESDAY:
			day = base + 4;
			break;
		case Calendar.THURSDAY:
			day = base + 3;
			break;
		case Calendar.FRIDAY:
			day = base + 2;
			break;
		case Calendar.SATURDAY:
			day = base + 1;
			break;
		default:
			throw new RuntimeException(Messages.getUnexpectedValue("day", "weekday",
					day));
		}
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTimeInMillis();
	}
	public static long toCsvTime(Calendar calendar, int year, int month, int day) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return OriconRanking.systemToCsvTime(calendar.getTimeInMillis());
	}
	protected static Pattern getFileNamePattern(int type) {
		String prefix = null;
		switch (type) {
		case OriconRanking.ALBUM_TYPE:
			prefix = OriconRanking.ALBUM_TYPE_NAME;
			break;
		case OriconRanking.SINGLE_TYPE:
			prefix = OriconRanking.SINGLE_TYPE_NAME;
			break;
		case OriconRanking.ALBUM_TYPE | OriconRanking.SINGLE_TYPE:
			prefix = "(" + OriconRanking.ALBUM_TYPE_NAME + "|"
					+ OriconRanking.SINGLE_TYPE_NAME + ")";
			break;
		default:
			break;
		}
		if (prefix == null) {
			return null;
		}
		return Pattern.compile(prefix + "\\d\\d\\d\\d\\d\\d\\d"
				+ OriconRanking.HTML_FILE_EXTENSION);
	}
	public static FilenameFilter getFileFilter(int type) {
		final Pattern pattern = OriconRanking.getFileNamePattern(type);
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				final String ext = OriconRanking.HTML_FILE_EXTENSION;
				if (name == null || pattern == null || name.length() < ext.length()
						|| !StringHelper.endsWith(name, ext, true)) {
					return false;
				}
				final Matcher matcher = pattern.matcher(name);
				return matcher.matches();
			}
		};
	}

	public static void pullHtml(File directory, int year, int month)
			throws IOException, URISyntaxException {
		OriconRanking.pullHtml(directory, year, month, true);
	}
	public static void pullHtml(File directory, int year, int month,
			boolean compareModifiedDate) throws IOException, URISyntaxException {
		OriconRanking.pullHtml(directory, OriconRanking.ALBUM_TYPE, year, month,
				compareModifiedDate);
		OriconRanking.pullHtml(directory, OriconRanking.SINGLE_TYPE, year, month,
				compareModifiedDate);
	}
	protected static void pullHtml(File directory, int type, int year, int month,
			boolean compareModifiedDate) throws IOException, URISyntaxException {
		if (directory == null) {
			throw new NullPointerException(Messages.getNull("directory"));
		} else if (!directory.isDirectory()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					"directory " + directory.getAbsolutePath(), "existing",
					"not existing"));
		} else if (NumberHelper.xor(OriconRanking.isAlbum(type), OriconRanking
				.isSingle(type))) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("type",
					OriconRanking.ALBUM_TYPE + " or " + OriconRanking.SINGLE_TYPE, type));
		}
		final String monthQuery = ORICON_DATE_QUERY.replaceAll("\\$\\{year\\}",
				Integer.toString(year)).replaceAll("\\$\\{month\\}",
				Integer.toString(month));
		for (int week = 1; week <= 5; ++week) {
			final String weekQuery = monthQuery.replaceAll("\\$\\{week\\}", Integer
					.toString(week));
			final String fileName = OriconRanking
					.getFileName(type, year, month, week);
			final String typeQuery = OriconRanking.getTypeQuery(type);
			final File file = new File(directory, fileName
					+ OriconRanking.HTML_FILE_EXTENSION);
			final URI uri = new URI(OriconRanking.ORICON_QUERY_URI + '?' + weekQuery
					+ '&' + typeQuery);
			final URL url = uri.toURL();
			OriconRanking.pullHtml(file, url, compareModifiedDate);
		}
	}
	public static void pullHtml(File file, URL url, boolean compareModifiedDate)
			throws IOException {
		if (file.isDirectory()) {
			throw new IOException(Messages.getUnexpectedValue("file", "file",
					"directtory"));
		}
		final boolean checkModified = file.isFile() && compareModifiedDate;
		HttpURLConnection connection = null;
		InputStream input = null;
		OutputStream output = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			if (checkModified && connection.getLastModified() < file.lastModified()) {
				Debug.log().debug("skipped=" + file.getAbsolutePath());
				return;
			}
			input = connection.getInputStream();
			output = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 64];
			int n = input.read(buffer);
			while (0 <= n) {
				output.write(buffer, 0, n);
				output.flush();
				n = input.read(buffer);
			}
		} finally {
			FileHelper.close(output);
			FileHelper.close(input);
			FileHelper.close(connection);
		}
		Debug.log().debug("wrote=" + file.getAbsolutePath());
	}
}

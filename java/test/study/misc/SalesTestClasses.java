package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import study.io.FileHelper;
import study.lang.Debug;
import study.lang.Messages;

public class SalesTestClasses {
	public static final String LINE_SEPARATOR = "\n";
	public static final String COLUMN_SEPARATOR = ", ";
	public static long SECOND_IN_MILISECOND = 1000;
	public static long MINUT_IN_MILISECOND = 60 * SECOND_IN_MILISECOND;
	public static long HOUR_IN_MILISECOND = 60 * MINUT_IN_MILISECOND;
	public static long DAY_IN_MILISECOND = 24 * HOUR_IN_MILISECOND;
	public static long WEEK_IN_MILISECOND = 7 * DAY_IN_MILISECOND;

	protected static class BaseData {
		public static final BaseData[] EMPTY_ARRAY = {};
		private static final String NA_STRING = "";
		private static final String NA_TIME = NA_STRING;
		private static final String NA_SALES = "";
		private static Format timeFormat;

		private long time;
		private long day;
		private int salesSum;
		private String brandName;
		private String productName;

		public BaseData() {
			this.time = this.day = -1;
		}
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		public long getDay() {
			return this.day;
		}
		public void setDay(long day) {
			this.day = day;
		}
		public int getSalesSum() {
			return this.salesSum;
		}
		public void setSalesSum(int salesSum) {
			this.salesSum = salesSum;
		}
		public String getBrandName() {
			return this.brandName;
		}
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}
		public String getProductName() {
			return this.productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}

		public static void writeHeader(Writer writer) throws IOException {
			writer.append(BaseData.quote("time"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("day"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("salesSum"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("brand"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("product"));
		}
		public void writeData(Writer writer) throws IOException {
			this.writeTime(writer, this.getTime());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeLong(writer, this.getDay());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeLong(writer, this.getSalesSum());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeName(writer, this.getBrandName());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeName(writer, this.getProductName());
		}
		protected void writeName(Writer writer, String value) throws IOException {
			if (value == null || value.length() < 1) {
				writer.write(BaseData.NA_STRING);
			} else {
				writer.write(BaseData.quote(value));
			}
		}
		protected void writeLong(Writer writer, long value) throws IOException {
			if (value < 0) {
				writer.write(BaseData.NA_SALES);
			} else {
				writer.write(Long.toString(value));
			}
		}
		protected void writeTime(Writer writer, long value) throws IOException {
			final Date date = new Date(0);
			if (value < 0) {
				writer.write(BaseData.NA_TIME);
			} else {
				date.setTime(value);
				writer.write(BaseData.getTimeFormat().format(date));
			}
		}
		protected static Format getTimeFormat() {
			if (BaseData.timeFormat == null) {
				BaseData.timeFormat = new SimpleDateFormat("yyyyMMdd");
			}
			return BaseData.timeFormat;
		}
		protected static String quote(String text) {
			if (text == null || text.length() < 1) {
				return "";
			}
			return '"' + text + '"';
		}
	}

	protected static class UkiiData extends BaseData {
		public static final UkiiData[] EMPTY_ARRAY = {};
		private int salesOfDay;

		public UkiiData() {
			this.salesOfDay = -1;
		}
		public int getSalesOfDay() {
			return this.salesOfDay;
		}
		public void setSalesOfDay(int salesOfDay) {
			this.salesOfDay = salesOfDay;
		}
		public static void writeHeader(Writer writer) throws IOException {
			BaseData.writeHeader(writer);
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("salesOfDay"));
		}
		public void writeData(Writer writer) throws IOException {
			super.writeData(writer);
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeLong(writer, this.getSalesOfDay());
		}
	}

	protected static class GameData extends BaseData {
		public static final GameData[] EMPTY_ARRAY = {};
		private int salesOfWeek;
		private String eventType;
		private String eventName;

		public GameData() {
			this.setBrandName("Perfume");
			this.setProductName("GAME");
			this.setSalesOfWeek(-1);
		}
		public int getSalesOfWeek() {
			return this.salesOfWeek;
		}
		public void setSalesOfWeek(int salesOfWeek) {
			this.salesOfWeek = salesOfWeek;
		}
		public String getEventType() {
			return eventType;
		}
		public void setEventType(String event) {
			this.eventType = event;
		}
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String event) {
			this.eventName = event;
		}
		public static void writeHeader(Writer writer) throws IOException {
			BaseData.writeHeader(writer);
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("salesOfWeek"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("eventType"));
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			writer.append(BaseData.quote("eventName"));
		}
		public void writeData(Writer writer) throws IOException {
			super.writeData(writer);
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeLong(writer, this.getSalesOfWeek());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeName(writer, this.getEventType());
			writer.append(SalesTestClasses.COLUMN_SEPARATOR);
			this.writeName(writer, this.getEventName());
		}
	}

	public static void normalizeUkiiTable(File output, File input)
			throws IOException {
		Writer writer = null;
		BufferedReader reader = null;
		try {
			writer = FileHelper.getWriter(output, FileHelper.UTF_8);
			reader = FileHelper.getBufferedReader(input, FileHelper.UTF_8);
			final Calendar date = Calendar.getInstance();
			date.set(Calendar.YEAR, 0);
			date.set(Calendar.MONTH, 0);
			date.set(Calendar.DAY_OF_MONTH, 1);
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);

			final Pattern delim = Pattern.compile("[ \\t\\n\\x0B\\f\\r\\u3000]+");
			final UkiiData data = new UkiiData();
			int lineCount = 1;

			UkiiData.writeHeader(writer);
			writer.write(LINE_SEPARATOR);
			writer.flush();

			for (String line = reader.readLine(); line != null; line = reader
					.readLine(), ++lineCount) {
				line = line.trim();
				if (line.startsWith("#") || line.length() < 1) {
					continue;
				}
				final String[] tokens = delim.split(line);
				if (tokens.length < 9) {
					String msg = Messages.getUnexpectedValue("unexpected line",
							"2008/11/19 ...", line);
					throw new IOException(msg);
				}
				String brandName = tokens[7];
				String productName = tokens[8];
				if (9 < tokens.length) {
					for (int i = 9, n = tokens.length; i < n; ++i) {
						productName = productName + ' ' + tokens[i];
					}
				}
				final long time = SalesTestClasses.readGameTime(tokens[0], date);
				data.setTime(time - SalesTestClasses.DAY_IN_MILISECOND);
				data.setDay(0);
				data.setBrandName(brandName);
				data.setProductName(productName);
				// Debug.log().debug(productName + ", " + time);
				int lastCount = 0;
				for (int i = 1; i < 7; ++i) {
					final String token = tokens[i].replaceAll("\\*", "").replaceAll(",",
							"");
					final int count = Integer.parseInt(token);
					data.setTime(data.getTime() + SalesTestClasses.DAY_IN_MILISECOND);
					data.setDay(data.getDay() + 1);
					data.setSalesOfDay(count - lastCount);
					data.setSalesSum(count);
					lastCount = count;
					data.writeData(writer);
					writer.write(LINE_SEPARATOR);
					writer.flush();
				}
			}
		} finally {
			FileHelper.close(writer);
			FileHelper.close(reader);
		}
	}
	public static void normalizeGameTable(File output, File input)
			throws IOException {
		Writer writer = null;
		BufferedReader reader = null;
		try {
			writer = FileHelper.getWriter(output, FileHelper.UTF_8);
			reader = FileHelper.getBufferedReader(input, FileHelper.UTF_8);
			final GameData[] dataList = SalesTestClasses.readGameDataList(reader);
			SalesTestClasses.writeDataList(writer, dataList);
		} finally {
			FileHelper.close(writer);
			FileHelper.close(reader);
		}
	}
	private static void writeDataList(Writer writer, GameData[] array)
			throws IOException {
		GameData.writeHeader(writer);
		writer.write(LINE_SEPARATOR);
		writer.flush();
		for (int i = 0, n = array.length; i < n; ++i) {
			final GameData data = array[i];
			data.writeData(writer);
			writer.write(LINE_SEPARATOR);
			writer.flush();
		}
	}
	protected static GameData[] readGameDataList(BufferedReader reader)
			throws IOException {
		final Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, 0);
		date.set(Calendar.MONTH, 0);
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		final Pattern delim = Pattern.compile("[\\t\\n\\x0B\\f\\r\\u3000]+");
		final SortedMap<Number, GameData> dataMap = new TreeMap<Number, GameData>();
		int lineCount = 1;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine(), ++lineCount) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			} else if (line.startsWith("!")) {
				SalesTestClasses.readEvent(dataMap, line.substring(1), date);
			} else {
				final String[] tokens = delim.split(line);
				switch (tokens.length) {
				case 4:
				case 5:
					SalesTestClasses.readGameSales(dataMap, tokens, date);
					break;
				default:
					Debug.log().info("skipped [" + lineCount + "] " + line);
					break;
				}
			}
		}
		final GameData[] array = dataMap.values().toArray(GameData.EMPTY_ARRAY);
		int sum = 0;
		long firstTime = Long.MAX_VALUE;
		for (int i = 0, n = array.length; i < n; ++i) {
			final GameData data = array[i];
			if (i == 0) {
				firstTime = data.getTime();
			}
			final long day = (data.getTime() - firstTime)
					/ SalesTestClasses.DAY_IN_MILISECOND;
			data.setDay(day + 7);
			if (0 <= data.getSalesOfWeek()) {
				sum += data.getSalesOfWeek();
				data.setSalesSum(sum);
			}
		}
		return array;
	}
	private static GameData readEvent(SortedMap<Number, GameData> dataMap,
			String line, Calendar date) {
		final String[] tokens = line.split("\\t");
		if (tokens.length != 3) {
			String msg = Messages.getUnexpectedValue("event format",
					"08/04/28[TAB][event description]", line);
			throw new IllegalArgumentException(msg);
		}
		final long time = SalesTestClasses.readGameTime(tokens[0], date);
		final Number key = Long.valueOf(time);
		final GameData data = SalesTestClasses.getGameData(dataMap, key, true);
		data.setTime(time);
		data.setEventType(tokens[1]);
		data.setEventName(tokens[2]);
		return data;
	}
	private static GameData readGameSales(SortedMap<Number, GameData> dataMap,
			String[] tokens, Calendar date) {
		final long time = SalesTestClasses.readGameTime(tokens[0], date);
		final String token = tokens[2].replaceAll("\\*", "").replaceAll(",", "");
		final int sales = Integer.parseInt(token);
		final Number key = Long.valueOf(time);
		final GameData data = SalesTestClasses.getGameData(dataMap, key, true);
		data.setTime(time);
		data.setSalesOfWeek(sales);
		return data;
	}
	private static GameData getGameData(SortedMap<Number, GameData> map,
			Number key, boolean anyway) {
		GameData data = map.get(key);
		if (data == null && anyway) {
			data = new GameData();
			map.put(key, data);
		}
		return data;
	}
	private static long readGameTime(String token, Calendar date) {
		final String[] x = token.split("/");
		if (x.length != 3) {
			String msg = Messages.getUnexpectedValue("date format", "08/04/28", x);
			throw new IllegalArgumentException(msg);
		}
		int year = Integer.parseInt(x[0].trim());
		if (year < 1000) {
			year += 2000;
		}
		final int month = Integer.parseInt(x[1].trim());
		final int day = Integer.parseInt(x[2].trim());
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month - 1);
		date.set(Calendar.DAY_OF_MONTH, day);
		return date.getTimeInMillis();
	}
}

package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
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

public class OriconTranslator {
	public static final String CSV_FILE_EXTENSION = ".csv";
	private static final String BRAND_FILE_NAME = "brand";
	private static final String PRODUCT_FILE_NAME = "product";
	private static final String BRAND_NAME_FILE_NAME = "brand-name";
	private static final String PRODUCT_NAME_FILE_NAME = "product-name";

	public static class TimeSpan {
		public static final FileTimeSpan[] EMPTY_ARRAY = {};
		private static final int INVALID_DAY = -1;

		public static boolean isValidDay(int day) {
			return 0 <= day;
		}

		private long day0;
		private long day1;

		public TimeSpan() {
			this.day0 = this.day1 = TimeSpan.INVALID_DAY;
		}
		public TimeSpan(long day0, long day1) {
			this.day0 = day0;
			this.day1 = day1;
		}
		public long getDay0() {
			return this.day0;
		}
		public void setDay0(long day0) {
			this.day0 = day0;
		}
		public long getDay1() {
			return this.day1;
		}
		public void setDay1(long day1) {
			this.day1 = day1;
		}
		public long getDayDifference() {
			if (0 <= this.getDay0() && 0 <= this.getDay1()
					&& this.getDay0() <= this.getDay1()) {
				return this.getDay1() - this.getDay0() + 1;
			}
			return TimeSpan.INVALID_DAY;
		}

		public void writeCsvRow(Appendable writer, CsvOption opt)
				throws IOException {
			OriconCsvHelper.writeNonNegative(writer, this.getDay0(), opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonNegative(writer, this.getDay1(), opt);
		}
		public static void writeCsvHeader(Appendable writer, CsvOption opt)
				throws IOException {
			OriconCsvHelper.writeNonEmpty(writer, "day0", opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonEmpty(writer, "day1", opt);
		}
	}

	protected static class SalesData extends TimeSpan {
		public SalesData() {
		}
		public SalesData(long day0, long day1) {
			super(day0, day1);
		}
		public void writeCsvRow(Appendable writer, OriconRanking ranking,
				CsvOption opt) throws IOException {
			super.writeCsvRow(writer, opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonNegative(writer, ranking.getProductId(), opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonNegative(writer, ranking.getSales(), opt);
		}
		public static void writeCsvHeader(Appendable writer, CsvOption opt)
				throws IOException {
			TimeSpan.writeCsvHeader(writer, opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonEmpty(writer, "product", opt);
			writer.append(opt.getColSeparator());
			OriconCsvHelper.writeNonEmpty(writer, "sales", opt);
		}
	}

	public static class FileTimeSpan extends SalesData {
		public static final FileTimeSpan[] EMPTY_ARRAY = {};
		private File file;

		public FileTimeSpan() {
		}
		public FileTimeSpan(File file) {
			this.file = file;
		}
		public FileTimeSpan(File file, long day0, long day1) {
			super(day0, day1);
			this.file = file;
		}
		public long getDay0InMSec() {
			return OriconRanking.csvToSystemTime(this.getDay0());
		}
		public long getDay1InMSec() {
			return OriconRanking.csvToSystemTime(this.getDay1());
		}
		public File getFile() {
			return this.file;
		}
		public void setFile(File file) {
			this.file = file;
		}
	}

	protected void translate(File dataDir, CsvOption opt, File htmlDir)
			throws IOException, URISyntaxException {
		if (htmlDir == null) {
			throw new NullPointerException(Messages.getNull("directory"));
		} else if (!htmlDir.isDirectory()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					"directory " + htmlDir.getAbsolutePath(), "existing", "not existing"));
		} else if (dataDir == null) {
			throw new NullPointerException(Messages.getNull("directory"));
		} else if (!dataDir.isDirectory()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					"directory " + dataDir.getAbsolutePath(), "existing", "not existing"));
		}
		final File[] files = htmlDir.listFiles(OriconRanking
				.getFileFilter(OriconRanking.ALBUM_TYPE));
		if (files.length < 1) {
			return;
		}
		Arrays.sort(files);
		final Calendar calendar = Calendar.getInstance();
		final long minTime = OriconRanking.fileToSystemTime(calendar, files[0]);
		final long maxTime = OriconRanking.fileToSystemTime(calendar,
				files[files.length - 1]);
		calendar.setTimeInMillis(minTime);
		final int minYear = calendar.get(Calendar.YEAR);
		calendar.setTimeInMillis(maxTime);
		final int maxYear = calendar.get(Calendar.YEAR);
		final Map<Integer, OriconBrand> brandMap = new TreeMap<Integer, OriconBrand>();
		final Map<Integer, OriconProduct> productMap = new TreeMap<Integer, OriconProduct>();
		for (int year = minYear; year <= maxYear; ++year) {
			this.translate(dataDir, opt, brandMap, productMap, htmlDir, year,
					OriconRanking.ALBUM_TYPE);
			this.translate(dataDir, opt, brandMap, productMap, htmlDir, year,
					OriconRanking.SINGLE_TYPE);
		}
		{
			Writer writer = null;
			final File file = new File(dataDir, OriconTranslator.BRAND_FILE_NAME
					+ OriconTranslator.CSV_FILE_EXTENSION);
			try {
				writer = FileHelper.getWriter(file, FileHelper.UTF_8);
				OriconBrand.writeCsvHeader(writer, opt);
				OriconCsvHelper.writeRowSeparator(writer, opt);
				for (OriconBrand data : brandMap.values()) {
					data.writeCsvRow(writer, opt);
					OriconCsvHelper.writeRowSeparator(writer, opt);
				}
			} finally {
				FileHelper.close(writer);
			}
		}
		{
			Writer writer = null;
			final File file = new File(dataDir, OriconTranslator.BRAND_NAME_FILE_NAME
					+ OriconTranslator.CSV_FILE_EXTENSION);
			try {
				writer = FileHelper.getWriter(file, FileHelper.UTF_8);
				this.writeNameFileHeader(writer, opt);
				OriconCsvHelper.writeRowSeparator(writer, opt);
				for (Map.Entry<Integer, OriconBrand> entry : brandMap.entrySet()) {
					final Integer key = entry.getKey();
					final OriconBrand value = entry.getValue();
					this.writeNameFileRow(writer, opt, key, value.getName());
					OriconCsvHelper.writeRowSeparator(writer, opt);
					final String[] names = value.getAliasNames();
					for (int i = 0, n = names.length; i < n; ++i) {
						this.writeNameFileRow(writer, opt, key, names[i]);
						OriconCsvHelper.writeRowSeparator(writer, opt);
					}
				}
			} finally {
				FileHelper.close(writer);
			}
		}
		{
			Writer writer = null;
			final File file = new File(dataDir,
					OriconTranslator.PRODUCT_NAME_FILE_NAME
							+ OriconTranslator.CSV_FILE_EXTENSION);
			try {
				writer = FileHelper.getWriter(file, FileHelper.UTF_8);
				this.writeNameFileHeader(writer, opt);
				OriconCsvHelper.writeRowSeparator(writer, opt);
				for (Map.Entry<Integer, OriconProduct> entry : productMap.entrySet()) {
					final Integer key = entry.getKey();
					final OriconProduct value = entry.getValue();
					this.writeNameFileRow(writer, opt, key, value.getName());
					OriconCsvHelper.writeRowSeparator(writer, opt);
				}
			} finally {
				FileHelper.close(writer);
			}
		}
		final File productFile = new File(dataDir,
				OriconTranslator.PRODUCT_FILE_NAME
						+ OriconTranslator.CSV_FILE_EXTENSION);
		OriconProduct.writeCsv(productFile, opt, productMap.values().toArray(
				OriconProduct.EMPTY_ARRAY));

		this.translateProductRelease(htmlDir, opt, productFile);
	}
	/**
	 * @param htmlDir
	 *          the directory to store HTML files.
	 * @param opt
	 * @param productFile
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	protected void translateProductRelease(File htmlDir, CsvOption opt,
			File productFile) throws IOException, URISyntaxException {
		final Calendar calendar = Calendar.getInstance();
		final OriconProduct[] products = OriconProduct.readCsv(productFile, opt);
		for (int iData = 0, nData = products.length; iData < nData; ++iData) {
			final OriconProduct data = products[iData];
			final int id = data.getId();
			if (id < 0) {
				continue;
			}
			if (data.getReleaseDay() < 0) {
				this.readProductReleaseDate(data, htmlDir, calendar);
			}
		}
		OriconProduct.writeCsv(productFile, opt, products);
	}
	protected void readProductReleaseDate(OriconProduct data, File htmlDir,
			Calendar calendar) throws URISyntaxException, IOException {
		final int id = data.getId();
		final String name = OriconTranslator.getProductFileName(id);
		final File file = new File(htmlDir, name
				+ OriconRanking.HTML_FILE_EXTENSION);
		final URI uri = new URI(OriconRanking.ORICON_PRODUCT_URI + +id + "/1/");
		final URL url = uri.toURL();
		OriconRanking.pullHtml(file, url, true);
		if (file.isFile()) {
			this.readProduct(data, calendar, file);
		}
	}
	protected void readProduct(OriconProduct data, Calendar calendar, File file)
			throws IOException {
		long releaseDay = -1;
		BufferedReader reader = null;
		try {
			final String sampleDate = "2000”N01ŒŽ26“ú”­”„";
			final String dateMarker = "“ú”­”„";
			reader = FileHelper.getBufferedReader(file,
					OriconRanking.ORICON_HTML_ENCODING);
			String line = reader.readLine();
			int iLine = 1;
			OUTER: for (; line != null; line = reader.readLine(), ++iLine) {
				if (sampleDate.length() <= line.length()) {
					final int last = line.indexOf(dateMarker);
					if (sampleDate.length() - dateMarker.length() <= last) {
						String segment = line.substring(last - sampleDate.length()
								+ dateMarker.length(), last);
						final Matcher mather = Pattern.compile("\\d++”N\\d++ŒŽ\\d++")
								.matcher(segment);
						if (mather.find()) {
							segment = segment.substring(mather.start(), mather.end());
							final String[] tokens = segment.split("”N|ŒŽ");
							if (tokens.length == 3) {
								final int y = Integer.parseInt(tokens[0]);
								final int m = Integer.parseInt(tokens[1]);
								final int d = Integer.parseInt(tokens[2]);
								releaseDay = OriconRanking.toCsvTime(calendar, y, m, d);
								break OUTER;
							}
						}
					}
				}
			}
		} finally {
			FileHelper.close(reader);
		}
		if (0 <= releaseDay) {
			data.setReleaseDay(releaseDay);
		} else {
			Debug.log().debug("failed read release day at " + file.getAbsolutePath());
		}
	}
	protected static String getProductFileName(int id) {
		return OriconTranslator.PRODUCT_FILE_NAME + id;
	}
	protected void writeNameFileRow(Writer writer, CsvOption opt, int id,
			String name) throws IOException {
		OriconCsvHelper.writeNonNegative(writer, id, opt);
		writer.write(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, name, opt);
	}
	protected void writeNameFileHeader(Writer writer, CsvOption opt)
			throws IOException {
		OriconCsvHelper.writeNonEmpty(writer, "id", opt);
		writer.write(opt.getColSeparator());
		OriconCsvHelper.writeNonEmpty(writer, "name", opt);
	}
	protected void translate(File output, CsvOption opt,
			Map<Integer, OriconBrand> brandMap,
			Map<Integer, OriconProduct> productMap, File input, int year, int type)
			throws IOException {
		final File file = new File(output, OriconTranslator.getFileName(type, year)
				+ OriconTranslator.CSV_FILE_EXTENSION);
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(file, FileHelper.UTF_8);
			this.getTimeSpans(writer, opt, brandMap, productMap, input, year, type);
		} finally {
			FileHelper.close(writer);
		}
	}
	public static String getFileName(int type, int year) {
		final String kind = OriconRanking.getTypeName(type);
		if (kind == null) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("type",
					OriconRanking.ALBUM_TYPE + " or " + OriconRanking.SINGLE_TYPE, type));
		}
		return kind + year;
	}
	protected FileTimeSpan[] getTimeSpans(File directory, int year)
			throws IOException {
		return this.getTimeSpans(null, null, null, null, directory, year,
				OriconRanking.ALBUM_TYPE);
	}
	protected FileTimeSpan[] getTimeSpans(Appendable writer, CsvOption opt,
			Map<Integer, OriconBrand> brandMap,
			Map<Integer, OriconProduct> productMap, File directory, final int year,
			final int type) throws IOException {
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
		final FilenameFilter filter = new FilenameFilter() {
			final Calendar calendar = Calendar.getInstance();

			public boolean accept(File dir, String name) {
				final long msec = OriconRanking.fileToSystemTime(this.calendar, name);
				if (msec < 0
						|| !StringHelper.startsWith(name, OriconRanking.getTypeName(type),
								true)) {
					return false;
				}
				this.calendar.setTimeInMillis(msec);
				return this.calendar.get(Calendar.YEAR) == year;
			}
		};
		if (writer != null && opt != null) {
			FileTimeSpan.writeCsvHeader(writer, opt);
			OriconCsvHelper.writeRowSeparator(writer, opt);
		}
		final File[] files = directory.listFiles(filter);
		final FileTimeSpan[] spans = new FileTimeSpan[files.length];
		final OriconRanking[] rankdings = new OriconRanking[OriconRanking.MAX_RANK];
		Arrays.sort(files);
		final Calendar calendar = Calendar.getInstance();
		long day0 = OriconRanking.getFirstSundayInMSec(calendar, year, 1);
		day0 = OriconRanking.systemToCsvTime(day0) - 7;
		int nSpan = 0;
		OUTER: for (int iFile = 0, nFile = files.length; iFile < nFile; ++iFile) {
			final File file = files[iFile];
			final int nRank = OriconRanking.readRanking(rankdings, file);
			switch (nRank) {
			case 0:
				Debug.log().debug("skiped=" + file.getName());
				continue OUTER;
			case OriconRanking.MAX_RANK:
				break;
			default:
				throw new IOException(Messages.getUnexpectedValue("#ranking",
						OriconRanking.MAX_RANK, nRank));
			}
			final long day1 = OriconRanking.fileToCsvTime(calendar, file);
			if (day1 == 11021) {
				final DateFormat format = new SimpleDateFormat("yyyy/MM/dd(E)W");
				Debug.log().debug("here");
				final long d0 = OriconRanking.csvToSystemTime(day0);
				final long d1 = OriconRanking.csvToSystemTime(day1);
				Debug.log().info(
						format.format(new Date(d0)) + "-" + format.format(new Date(d1)));
			}
			if (day1 <= day0|| (day1 - day0) % 7 != 0) {
				throw new Error("bug at " + file.getName());
			}
			final FileTimeSpan span = new FileTimeSpan(file, day0 + 1, day1);
			spans[nSpan++] = span;
			day0 = day1;
			if (writer != null && opt != null) {
				for (int iRank = 0; iRank < nRank; ++iRank) {
					span.writeCsvRow(writer, rankdings[iRank], opt);
					OriconCsvHelper.writeRowSeparator(writer, opt);
				}
			}
			if (brandMap != null) {
				for (int iRank = 0; iRank < nRank; ++iRank) {
					final OriconRanking ranking = rankdings[iRank];
					final int id = ranking.getBrandId();
					final String name = ranking.getBrandName();
					OriconBrand data = brandMap.get(id);
					if (data == null) {
						data = new OriconBrand(id, name);
						brandMap.put(id, data);
					} else if (!ObjectHelper.equals(data.getName(), name)) {
						data.replaceAliasName(name, name, true);
					}
				}
			}
			if (productMap != null) {
				for (int iRank = 0; iRank < nRank; ++iRank) {
					final OriconRanking ranking = rankdings[iRank];
					final int id = ranking.getProductId();
					final String name = ranking.getProductName();
					final int brand = ranking.getBrandId();
					OriconProduct data = productMap.get(id);
					if (data == null) {
						data = new OriconProduct(id, name);
						data.setBrand(brand);
						productMap.put(id, data);
					} else if (data.getBrand() != brand) {
						throw new IOException(Messages.getUnexpectedValue("product",
								"non-duplicated brand", "duplicated"));
					}
				}
			}
		}
		if (nSpan < spans.length) {
			return ArrayHelper.sub(spans, 0, nSpan);
		}
		return spans;
	}
}

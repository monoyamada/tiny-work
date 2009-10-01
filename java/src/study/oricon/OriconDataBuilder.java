package study.oricon;

import static study.lang.Debug.isNotNull;
import static study.lang.Debug.log;
import static study.lang.Messages.getFailedOperation;
import static study.lang.Messages.getOperating;
import static study.lang.Messages.getSuccessedOperation;
import static study.lang.Messages.getUnexpectedValue;
import static study.oricon.OriconConstant.BRAND_NAME_FILE;
import static study.oricon.OriconConstant.CSV_FILE_EXTENSION;
import static study.oricon.OriconConstant.HTML_FILE_EXTENSION;
import static study.oricon.OriconConstant.ORICON_HOME_URL;
import static study.oricon.OriconConstant.ORICON_RANKING_URL;
import static study.oricon.OriconConstant.PRODUCT_FILE;
import static study.oricon.OriconConstant.PRODUCT_NAME_FILE;
import static study.oricon.OriconConstant.PRODUCT_PATH_PREFIX;
import static study.oricon.OriconConstant.RANKING_SIZE;
import static study.oricon.OriconConstant.SALES_FILE;
import static study.oricon.OriconProductType.ALBUM;
import static study.oricon.OriconProductType.SINGLE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import study.io.FileHelper;
import study.lang.ArrayHelper;
import study.lang.Debug;
import study.lang.Messages;
import study.lang.StringHelper;
import study.oricon.OriconProductData.Product;

public class OriconDataBuilder extends OriconDataWorker{
	public static class DayRanking {
		public static final DayRanking[] EMPTY_ARRAY = {};
		private final int day;
		private final OriconRanking[] ranking;

		public DayRanking(int day, OriconRanking[] ranking) {
			super();
			this.day = day;
			this.ranking = ranking;
		}
		public int getDay() {
			return this.day;
		}
		public OriconRanking[] getRanking() {
			return this.ranking;
		}
	}

	public static int getDayByWeekInYear(int year, int week) {
		return OriconDateHelper.getFirstSunday(year) + (week - 1) * 7;
	}
	/**
	 * Converts from year+week to day to avoid file name duplication.
	 * 
	 * @param type
	 * @param year
	 * @param week
	 * @return
	 */
	protected static String getRankingFileName(OriconProductType type, int year,
			int week) {
		// return type.getName() + year + "" + (week < 10 ? "0" + week : week);
		return type.getName() + OriconDataBuilder.getDayByWeekInYear(year, week);
	}
	protected static String getProductFileName(OriconProductType type, int index) {
		return PRODUCT_FILE + Integer.toString(index);
	}
	public static int getDayByRankingFileName(String name) {
		if (StringHelper.startsWith(name, ALBUM.getName(), true)) {
			return Integer.parseInt(name.substring(ALBUM.getName().length()));
		} else if (StringHelper.startsWith(name, SINGLE.getName(), true)) {
			return Integer.parseInt(name.substring(SINGLE.getName().length()));
		}
		throw new IllegalArgumentException(getUnexpectedValue("name",
				"(album|single)d+", name));
	}
	/**
	 * 
	 * @param type
	 * @param year
	 * @param week
	 * @return
	 */
	public static String getRankingQuery(OriconProductType type, int year,
			int week) {
		switch (type) {
		case ALBUM:
			return "kbn=ja&types=rnk&year=" + year + "&month=" + 1 + "&week=" + week;
		case SINGLE:
			return "kbn=js&types=rnk&year=" + year + "&month=" + 1 + "&week=" + week;
		default:
			throw new Error("bug");
		}
	}

	private final OriconProductType productType;
	private OriconSalesData salesData;
	private OriconNameData productNameData;
	private OriconMultiNameData brandNameData;
	private OriconProductData productData;

	public OriconDataBuilder(OriconWorkspace workspace,
			OriconProductType productType) {
		super(workspace);
		isNotNull("workspace", productType);
		this.productType = productType;
	}

	public OriconProductType getProductType() {
		return this.productType;
	}

	public OriconSalesData getSalesData() {
		if (this.salesData == null) {
			this.salesData = this.newSalesData();
		}
		return this.salesData;
	}
	protected OriconSalesData newSalesData() {
		final File file = new File(this.getCsvDirectory(), this
				.getSalesDataFileName()
				+ CSV_FILE_EXTENSION);
		return new OriconSalesData(file, this.getCsvOption());
	}
	protected String getSalesDataFileName() {
		return this.getProductType().getName() + "-" + SALES_FILE;
	}
	public OriconNameData getProductNameData() {
		if (this.productNameData == null) {
			this.productNameData = this.newProductNameData();
		}
		return this.productNameData;
	}
	protected OriconNameData newProductNameData() {
		final File file = new File(this.getCsvDirectory(), PRODUCT_NAME_FILE
				+ CSV_FILE_EXTENSION);
		return new OriconNameData(file, this.getCsvOption());
	}
	public OriconMultiNameData getBrandNameData() {
		if (this.brandNameData == null) {
			this.brandNameData = this.newBrandNameData();
		}
		return this.brandNameData;
	}
	protected OriconMultiNameData newBrandNameData() {
		final File file = new File(this.getCsvDirectory(), BRAND_NAME_FILE
				+ CSV_FILE_EXTENSION);
		return new OriconMultiNameData(file, this.getCsvOption());
	}
	public OriconProductData getProductData() {
		if (this.productData == null) {
			this.productData = this.newProductData();
		}
		return this.productData;
	}
	protected OriconProductData newProductData() {
		final File file = new File(this.getCsvDirectory(), PRODUCT_FILE
				+ CSV_FILE_EXTENSION);
		return new OriconProductData(file, this.getCsvOption());
	}

	protected void importData(int year) throws MalformedURLException,
			URISyntaxException, IOException {
		// update sales data
		final OriconSalesData sales = this.getSalesData();

		// to avoid duplication of download
		final int today = OriconDateHelper.getToday();
		final int day0 = Math.min(OriconDateHelper.getFirstSunday(year), today);
		final int day1 = Math.min(OriconDateHelper.getFirstSunday(year + 1), today);
		final int nWeek = (day1 - day0) / 7;
		final int[] oldDays = sales.getDayArray();

		final OriconRanking[] ranking = new OriconRanking[RANKING_SIZE];
		final List<DayRanking> rankingBuffer = new ArrayList<DayRanking>(1024);
		for (int week = 0; week < nWeek; ++week) {
			final int day = day0 + week * 7;
			if (Arrays.binarySearch(oldDays, day) < 0) {
				final int result = this.getSalesRanking(ranking, year, week + 1);
				if (result == ranking.length) {
					rankingBuffer.add(new DayRanking(day, ranking.clone()));
					Arrays.fill(ranking, null);
				}
			}
		}

		if (rankingBuffer.size() < 1 && false) {
			// nothing to do
			return;
		}

		final DayRanking[] rankings = rankingBuffer.toArray(DayRanking.EMPTY_ARRAY);
		sales.importData(rankings);

		// update names
		{
			final OriconNameData dataSet = this.getProductNameData();
			final OriconName[] array = this.getProductNameArray(rankings);
			dataSet.importData(array);
		}
		if (true) {
			final OriconMultiNameData dataSet = this.getBrandNameData();
			final Map<Integer, String[]> map = new TreeMap<Integer, String[]>();
			dataSet.getIndexMultiMap(map);
			if (0 < this.getBrandNames(map, rankings)) {
				dataSet.writeIndexMultiMap(map);
			}
		}

		// update product
		{
			final OriconProductData productData = this.getProductData();
			final Product[] oldProducts = productData.getDataArray();
			final Product[] newProducts = this.getProductArray(rankings, oldProducts);
			final Product[] allProducts = new Product[oldProducts.length
					+ newProducts.length];
			OriconIndex.weave(allProducts, oldProducts, newProducts);
			boolean productChanged = 0 < newProducts.length;
			for (Product data : allProducts) {
				int day = data.getReleaseDay();
				if (day < 0) {
					day = this.getProductReleaseDay(data.getIndex());
					if (0 <= day) {
						data.setReleaseDay(day);
						productChanged = true;
					}
				}
			}
			if (productChanged) {
				productData.writeDataArray(allProducts);
			}
		}
	}

	protected Product[] getProductArray(DayRanking[] rankings,
			Product[] oldProducts) {
		final Map<Integer, Product> buffer = new TreeMap<Integer, Product>();
		for (DayRanking day : rankings) {
			for (OriconRanking rank : day.getRanking()) {
				final OriconName product = rank.getProduct();
				final OriconName brand = rank.getBrand();
				final Integer key = product.getIndex();
				if (buffer.get(key) == null
						&& OriconIndex.indexOf(oldProducts, product.getIndex()) < 0) {
					final Product newData = new Product(product.getIndex(), brand
							.getIndex(), this.getProductType());
					buffer.put(key, newData);
				}
			}
		}
		return buffer.values().toArray(Product.EMPTY_ARRAY);
	}
	protected OriconName[] getProductNameArray(DayRanking[] rankings) {
		final Map<Integer, OriconName> buffer = new TreeMap<Integer, OriconName>();
		for (DayRanking day : rankings) {
			for (OriconRanking rank : day.getRanking()) {
				final OriconName data = rank.getProduct();
				buffer.put(data.getIndex(), data);
			}
		}
		return buffer.values().toArray(OriconName.EMPTY_ARRAY);
	}
	protected int getBrandNames(Map<Integer, String[]> output,
			DayRanking[] rankings) {
		int count = 0;
		for (DayRanking day : rankings) {
			for (OriconRanking rank : day.getRanking()) {
				final OriconName data = rank.getBrand();
				final Integer key = data.getIndex();
				final String[] val = output.get(key);
				if (val == null || val.length < 1) {
					output.put(key, new String[] { data.getName() });
					++count;
				} else if (ArrayHelper.indexOf(val, data.getName()) < 0) {
					output.put(key, ArrayHelper.add(val, data.getName()));
					++count;
				}
			}
		}
		return count;
	}

	protected int getProductReleaseDay(int product) throws IOException,
			URISyntaxException {
		final String name = OriconDataBuilder.getProductFileName(this
				.getProductType(), product);
		final String path = name + HTML_FILE_EXTENSION;
		final File html = new File(this.getHtmlDirectory(), path);
		if (html.isFile()) {
			return OriconHtmlHelper.getReleaseDay(html);
		}

		final int nTry = 3;
		final String url = ORICON_HOME_URL + PRODUCT_PATH_PREFIX + product;
		for (int iTry = 1; iTry <= nTry; ++iTry) {
			final String newPath = name + "-" + iTry + HTML_FILE_EXTENSION;
			final File file = new File(this.getTmpDirectory(), newPath);
			if (!file.isFile()) {
				final String suffix = "/" + iTry + '/';
				final URI uri = new URI(url + suffix);
				log().info(getOperating("downloading " + uri));
				try {
					FileHelper.copyFile(file, uri, false);
				} catch (Exception ex) {
					log().info(getFailedOperation("download " + uri));
					continue;
				}
				if (file.isFile()) {
					log().info(getSuccessedOperation("download " + uri));
				} else {
					log().info(getFailedOperation("download " + uri));
					continue;
				}
			}
			log().info(getOperating("parse " + file));
			final int day = OriconHtmlHelper.getReleaseDay(file);
			if (0 <= day) {
				log().info(getSuccessedOperation("parse " + file));
				log().info(getOperating("moving file to " + html));
				if (file.renameTo(html)) {
					log().info(getSuccessedOperation("move " + html));
				} else {
					log().info(getFailedOperation("move " + html));
				}
				return day;
			}
			log().info(getFailedOperation("parse " + file));
		}
		log().info(getFailedOperation("get release day of product " + product));
		return -1;
	}

	protected int getSalesRanking(OriconRanking[] output, int year, int week)
			throws MalformedURLException, URISyntaxException, IOException {
		for (OriconRanking data : output) {
			if (data != null) {
				data.clear();
			}
		}
		final String name = OriconDataBuilder.getRankingFileName(this
				.getProductType(), year, week);
		final String path = name + HTML_FILE_EXTENSION;
		final File html = new File(this.getHtmlDirectory(), path);
		if (html.isFile()) {
			return OriconHtmlHelper.getRankingData(output, html);
		}

		final File file = new File(this.getTmpDirectory(), path);
		if (file.isFile()) {
			log().info(getSuccessedOperation("already downloaded " + file));
		} else {
			final String query = ORICON_RANKING_URL + '?'
					+ getRankingQuery(this.getProductType(), year, week);
			final URI uri = new URI(query);
			log().info(getOperating("downloading " + uri));
			FileHelper.copyFile(file, uri, false);
			if (!file.isFile()) {
				log().info(getFailedOperation("download " + uri));
				return 0;
			}
		}
		int result = 0;
		try {
			result = OriconHtmlHelper.getRankingData(output, file);
		} catch (IOException ex) {
			ex.printStackTrace();
			Debug.log().debug(
					Messages
							.getFailedOperation("to parse file=" + file.getAbsolutePath()));
			throw ex;
		}
		if (0 < result) {
			if (html.isFile()) {
				throw new Error("bug");
			}
			if (file.renameTo(html)) {
				log().info(getSuccessedOperation("download html file to " + html));
			} else {
				log().info(
						getFailedOperation("move html file from " + file.getAbsolutePath()
								+ " to " + html.getAbsolutePath()));
			}
		} else {
			log().info(getSuccessedOperation("empty data in " + file));
		}
		return result;
	}
}

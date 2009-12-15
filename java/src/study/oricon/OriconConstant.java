package study.oricon;

public interface OriconConstant {
	public static final String HTML_DIRECTORY = "html";
	public static final String CSV_DIRECTORY = "data";
	public static final String TMP_DIRECTORY = "tmp";
	public static final String HTML_FILE_EXTENSION = ".html";
	public static final String CSV_FILE_EXTENSION = ".csv";
	// lower(365/7) + 1
	public static final int MAX_WEEK_IN_YEAR = 53;
	/**
	 * Oricon home page.
	 */
	public static final String ORICON_HOME_URL = "http://www.oricon.co.jp/";
	public static final String ORICON_RANKING_URL = ORICON_HOME_URL
			+ "search/result.php";
	/**
	 * path
	 */
	public static final String BRAND_PATH_PREFIX = "prof/artist/";
	/**
	 * path
	 */
	public static final String PRODUCT_PATH_PREFIX = "music/release/d/";
	public static final String PRODUCT_PATH_SUFFIX = "/1/";
	public static final String ORICON_HTML_ENCODING = "Shift_JIS";
	public static final int RANKING_SIZE = 30;
	
	public static final String BRAND_NAME_FILE = "brand-name";
	public static final String PRODUCT_NAME_FILE = "product-name";
	public static final String PRODUCT_FILE = "product";
	public static final String SALES_FILE = "sales";
	public static final String SALES_DATE_FILE = "date";
}

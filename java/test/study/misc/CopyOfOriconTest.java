package study.misc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.Debug;
import study.misc.OriconTranslator.FileTimeSpan;

public class CopyOfOriconTest extends TestCase {
	private static final String ORICON_HTML_DIR = "data/study/misc/oricon/html";

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testHttp() throws IOException, URISyntaxException {
		final File dir = new File("data/tmp");
		FileHelper.ensureDirectory(dir);
		for (int year = 2000; year <= 2000; ++year) {
			for (int month = 1; month <= 12; ++month) {
				OriconRanking.pullHtml(dir, year, month);
			}
		}
	}
	/**
	 * http://www.oricon.co.jp/search/result.php?kbn=js&types=rnk&year=2009&month=9&week=1&submit4.x=20&submit4.y=10
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void _testHttpAll() throws IOException, URISyntaxException {
		final File dir = new File(CopyOfOriconTest.ORICON_HTML_DIR);
		FileHelper.ensureDirectory(dir);
		final Calendar lastDate = Calendar.getInstance();
		lastDate.roll(Calendar.MONTH, -1);
		final int lastYear = lastDate.get(Calendar.YEAR);
		for (int year = 2000; year <= lastYear; ++year) {
			int lastMonth = 12;
			if (year == lastYear) {
				lastMonth = lastDate.get(Calendar.MONTH);
			}
			for (int month = 1; month <= lastMonth; ++month) {
				OriconRanking.pullHtml(dir, year, month);
			}
		}
	}
	public void testPattern() {
		// final Pattern pattern = Pattern.compile("[\\d]+年[\\d]+月[\\d]+日発売");
		final Pattern pattern = Pattern.compile("\\d+年\\d+月\\d+日発売");
		final String text = "				<div class=\"profile\"> <p class=\"paragraph\"><strong>2000年07月05日発売</strong><br>";
		Debug.log().debug(text.replaceAll("\\d+年\\d+月\\d+日発売", "xxx"));
		final Matcher matcher = pattern.matcher(text);
		Debug.log().debug(matcher.replaceAll("yyy"));
		matcher.reset();
		while (matcher.find()) {
			Debug.log().debug(matcher.group());
			for (int i = 0, n = matcher.groupCount(); i < n; ++i) {
				Debug.log().debug(text.substring(matcher.start(), matcher.end()));
			}
		}
	}
	public void testCalendar() throws IOException {
		{
			final DateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss/SSS");
			final Calendar zero = Calendar.getInstance();
			zero.setTimeInMillis(0);
			Debug.log().info("0=" + format.format(zero.getTime()));
		}
		{
			final Calendar future = Calendar.getInstance();
			future.set(Calendar.YEAR, 3000);
			final long days = future.getTimeInMillis() / OriconRanking.DAY_IN_MSEC;
			Debug.log().info("3000year/1day=" + days);
			Debug.log().info("int.max=" + Integer.MAX_VALUE);
		}
		{
			final DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			Debug.log().info("sun.week=" + format.format(calendar.getTime()));
			calendar.set(Calendar.WEEK_OF_MONTH, 1);
			Debug.log().info(format.format(calendar.getTime()));
		}
	}
	public void testTranslation() throws IOException, URISyntaxException {
		final File dir = new File("data/tmp");
		final OriconTranslator translator = new OriconTranslator();
		final CsvOption opt = new CsvOption();
		opt.setQuotation(null);
		translator.translate(dir, opt, dir);
		if (false) {
			final FileTimeSpan[] spans = translator.getTimeSpans(dir, 2000);
			Calendar date = Calendar.getInstance();
			final DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			for (int i = 0, n = spans.length; i < n; ++i) {
				final long day0 = spans[i].getDay0InMSec();
				final long day1 = spans[i].getDay1InMSec();
				date.setTimeInMillis(day0);
				final String s0 = format.format(date.getTime());
				date.setTimeInMillis(day1);
				final String s1 = format.format(date.getTime());
				Debug.log().debug(s1 + "-" + s0 + "=" + spans[i].getDayDifference());
			}
		}
	}
}

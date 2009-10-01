package study.misc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.Debug;

public class OriconTest extends TestCase {
	private static final String ORICON_DIR = "data/tmp/oricon/";

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testHttp() throws IOException, URISyntaxException {
		final File htmlDir = new File(ORICON_DIR + "html");
		FileHelper.ensureDirectory(htmlDir);
		for (int year = 2000; year <= 2000; ++year) {
			for (int month = 1; month <= 12; ++month) {
				OriconRanking.pullHtml(htmlDir, year, month, true);
			}
		}
	}
	public void testTranslator() throws IOException, URISyntaxException {
		final File htmlDir = new File(ORICON_DIR + "html");
		final File dataDir = new File(ORICON_DIR + "data");
		final OriconTranslator translator = new OriconTranslator();
		final CsvOption opt = new CsvOption();
		opt.setQuotation(null);
		translator.translate(dataDir, opt, htmlDir);
	}
}

package study.misc;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;
import study.io.CsvOption;
import study.lang.Debug;

public class ProductRecordTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testReadCsv() throws IOException {
		final File dir = new File("data/study/misc");
		Assert.assertTrue(dir.getAbsolutePath(), dir.isDirectory());

		final CsvOption csvOpt = new CsvOption();
		final ProductRecordList table = new ProductRecordList();
		{
			final File file = new File(dir, "product.txt");
			Assert.assertTrue(file.getAbsolutePath(), file.isFile());
			csvOpt.setQuotation(null);
			table.readCsv(file, csvOpt);
		}
		{
			final File file = new File(dir, "product.csv");
			csvOpt.setQuotation(CsvOption.QUOTATION);
			table.writeCsv(file, csvOpt);
			Debug.log().info("wrote " + file.getAbsolutePath());
		}
	}
	public void testUkiiImport() throws IOException {
		final File dir = new File("data/study/misc");
		Assert.assertTrue(dir.getAbsolutePath(), dir.isDirectory());

		final CsvOption csvOpt = new CsvOption();
		final ProductRecordList table = new ProductRecordList();
		{
			final File file = new File(dir, "product.txt");
			Assert.assertTrue(file.getAbsolutePath(), file.isFile());
			csvOpt.setQuotation("");
			table.readCsv(file, csvOpt);
			csvOpt.setQuotation(CsvOption.QUOTATION);
		}
		if (false) {
			final File file = new File(dir, "dump.csv");
			csvOpt.setQuotation(CsvOption.QUOTATION);
			table.writeCsv(file, csvOpt);
			Debug.log().info("wrote " + file.getAbsolutePath());
		}
		{
			final File input = new File(dir, "ukii.txt");
			Assert.assertTrue(input.getAbsolutePath(), input.isFile());
			final File output = new File(dir, "dump.csv");
			final UkiiImport importer = new UkiiImport();
			importer.ukiiToSales(output, input, table, csvOpt);
			Debug.log().info("wrote " + output.getAbsolutePath());
		}
	}
}

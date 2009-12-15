package study.oricon;

import static study.oricon.OriconDateHelper.getFirstSunday;
import static study.oricon.OriconConstant.*;
import static study.oricon.OriconProductType.ALBUM;
import static study.oricon.OriconProductType.SINGLE;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import study.io.CsvOption;
import study.io.FileHelper;
import study.io.CsvOption.LineCursor;
import study.lang.ArrayHelper;
import study.lang.Debug;
import study.oricon.OriconProductData.Product;

public class OriconDataBuilderTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_() {
		int sun2000 = getFirstSunday(2000);
		int sun2001 = getFirstSunday(2001);
		int week2000 = (sun2001 - sun2000) / 7;
		Debug.log().info("#weeks=" + week2000);
	}
	public void test_zenkakuToHankaku() {
		Debug.log().info(
				OriconHtmlHelper.HANKAKU_MINUS_ZENKAKU + "=" + (' ' - '\u3000'));
	}
	public void test_importData() throws MalformedURLException,
			URISyntaxException, IOException, InterruptedException {
		final File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final OriconDataBuilder album = new OriconDataBuilder(workspace, ALBUM);
		final OriconDataBuilder single = new OriconDataBuilder(workspace, SINGLE);
		album.importData(2000);
		single.importData(2000);
	}
	public void test_importData_more() throws MalformedURLException,
			URISyntaxException, IOException, InterruptedException {
		final File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final OriconDataBuilder album = new OriconDataBuilder(workspace, ALBUM);
		final OriconDataBuilder single = new OriconDataBuilder(workspace, SINGLE);
		album.importData(2000);
		single.importData(2000);
		album.importData(2001);
		single.importData(2001);
	}
	public void _testRepairReleaseDay() throws IOException {
		final File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final OriconDataBuilder album = new OriconDataBuilder(workspace, ALBUM);
		final Product[] array = album.getProductData().getDataArray();
		for (Product data : array) {
			data.setReleaseDay(-1);
		}
		album.getProductData().writeDataArray(array);
	}
	public void testAppOriconDataBuilder() throws MalformedURLException,
			URISyntaxException, IOException {
		AppOriconDataBuilder.main(ArrayHelper.EMPTY_STRING_ARRAY);
	}
	public void test_fixProductName() throws IOException {
		final File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final CsvOption opt = workspace.getCsvOption();
		final File input = new File(workspace.getCsvDirectory(), PRODUCT_NAME_FILE
				+ CSV_FILE_EXTENSION);
		final File tmp = workspace.getTmpFile();
		input.renameTo(tmp);
		LineCursor cursor = null;
		Writer writer = null;
		try {
			int lastProduct = -1;
			writer = FileHelper.getWriter(input, opt.getEncoding());
			cursor = new CsvOption.LineCursor(tmp, opt);
			while (cursor.move()) {
				final String line = cursor.getLine();
				if (cursor.isDataLine()) {
					final int index = line.indexOf(opt.getColSeparator());
					if (0 <= index) {
						int value = Integer.parseInt(line.substring(0, index));
						if (value < lastProduct) {
							// skip
							continue;
						} else {
							writer.write(line);
							lastProduct = value;
						}
					} else {
						writer.write(line);
					}
				} else {
					writer.write(line);
				}
				writer.write(opt.getRowSeparator());
				writer.flush();
			}
		} finally {
			FileHelper.close(cursor);
			FileHelper.close(writer);
		}
	}
}

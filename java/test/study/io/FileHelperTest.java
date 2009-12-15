package study.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;
import junit.framework.TestCase;
import study.lang.Debug;

public class FileHelperTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testCopyFile() throws URISyntaxException, MalformedURLException, IOException {
		final File file = new File("data/tmp/yahoo.html");
		final URI uri = new URI("http://www.yahoo.co.jp");
		if (file.exists()) {
			file.delete();
		}
		boolean result = FileHelper.copyFile(file, uri, false);
		Assert.assertTrue("must be copied", result);
		result = FileHelper.copyFile(file, uri, false);
		Assert.assertTrue("must not be copied", !result);
	}
}

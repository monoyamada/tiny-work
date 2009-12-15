package study.oricon;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import study.lang.Debug;

public class GdpReformTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_toCsv() throws IOException {
		final File home = new File("data/tmp/oricon");
		assertTrue(home.getAbsolutePath(), home.isDirectory());
		final File tabFile = new File(home, "data/gdp-1980q1-2008q1.txt");
		assertTrue(tabFile.getAbsolutePath(), tabFile.isFile());
		final File csvFile = new File(home, "tmp/gdp-1980q1-2008q1.csv");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final GdpReform builder = new GdpReform(workspace);
		builder.toCsv(csvFile, tabFile);
		Debug.log().info("wrote " + csvFile.getAbsolutePath());
	}
}

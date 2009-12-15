package study.misc;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import study.lang.Debug;

public class SalesTest extends TestCase {

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testGame() throws IOException {
		final File dir = new File("data/study/misc");
		assert dir.isDirectory();
		class Doit {
			void apply(String body) throws IOException {
				final File input = new File(dir, body + ".txt");
				final File output = new File(dir, body + ".csv");
				SalesTestClasses.normalizeGameTable(output, input);
				Debug.log().info("wrote " + output.getAbsolutePath());
			}
		}
		final Doit doit = new Doit();
		doit.apply("perfume_game");
		doit.apply("perfume_triangle");
		doit.apply("kato_ring");
	}
	public void testUkii() throws IOException {
		final File dir = new File("data/study/misc");
		assert dir.isDirectory();
		class Doit {
			void apply(String body) throws IOException {
				final File input = new File(dir, body + ".txt");
				final File output = new File(dir, body + ".csv");
				SalesTestClasses.normalizeUkiiTable(output, input);
				Debug.log().info("wrote " + output.getAbsolutePath());
			}
		}
		final Doit doit = new Doit();
		doit.apply("ukii");
	}
}

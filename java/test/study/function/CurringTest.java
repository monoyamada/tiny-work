package study.function;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;
import study.function.CurringTestClasses.Prototype_0;
import study.function.CurringTestClasses.Prototype_1;
import study.lang.Debug;

public class CurringTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
	public void test() throws IOException {
		final String path = "test/study/function/CurringTest.java";
		final File file = new File(path);
		Assert.assertTrue(file.getAbsolutePath() + " is not file", file.isFile());
		final int row = 10;
		final Prototype_0 proto_0 = new Prototype_0();
		final String line_0 = proto_0.readLine(file, row);
		final Prototype_1 proto_1 = new Prototype_1();
		final String line_1 = proto_1.getCursor(file).readLine(row);
		Assert.assertEquals(line_0 + " != " + line_1, line_0, line_1);
	}
}

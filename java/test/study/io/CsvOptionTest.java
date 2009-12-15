package study.io;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import study.io.CsvOption.TokenCursor;
import study.lang.Debug;

public class CsvOptionTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testTokenCursor() throws IOException {
		final File file = new File("data/study/io/test-1.csv");
		final CsvOption opt = new CsvOption();
		opt.setQuotation(null);
		final TokenCursor cursor = new TokenCursor(file, opt);
		
		assertEquals(true, cursor.moveRow());
		assertEquals("a, b, c", cursor.getLine());
		assertEquals(true, cursor.moveCol());
		assertEquals("a", cursor.getToken().trim());
		assertEquals(true, cursor.moveCol());
		assertEquals("b", cursor.getToken().trim());
		assertEquals(true, cursor.moveCol());
		assertEquals("c", cursor.getToken().trim());
		assertEquals(false, cursor.moveCol());
		
		assertEquals(true, cursor.moveRow());
		assertEquals("1, 2, 3, 4", cursor.getLine());
		assertEquals(true, cursor.moveCol());
		assertEquals("1", cursor.getToken().trim());
		assertEquals(true, cursor.moveCol());
		assertEquals("2", cursor.getToken().trim());
		assertEquals(true, cursor.moveCol());
		assertEquals("3", cursor.getToken().trim());
		assertEquals(true, cursor.moveCol());
		assertEquals("4", cursor.getToken().trim());
		assertEquals(false, cursor.moveCol());
		
		assertEquals(false, cursor.moveRow());
	}
}

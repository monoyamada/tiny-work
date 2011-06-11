package parser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.FileHelper;

public class TestParser extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testReader() throws IOException {
		{
			String text = "1st + 2nd + 3rd";
			InputStream input = null;
			try {
				input = new ByteArrayInputStream(text.getBytes(FileHelper.UTF_8));
				int c = input.read();
				while (0 <= c) {
					
				}
			} finally {
				FileHelper.close(input);
			}
		}
	}
}

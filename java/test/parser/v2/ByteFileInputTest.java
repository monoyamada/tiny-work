package parser.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.FileHelper;

public class ByteFileInputTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testGet() throws IOException {
		File file = new File("data/pg76.txt");
		FileChannel input = null;
		try {
			input = new FileInputStream(file).getChannel();
			final int blockSize = 16;
			final	long position = 97;
			final	int length = 10;
			
			ByteFileInput reader = new ByteFileInput(input, blockSize);
			long n = 0;
			while (reader.get(n) != ByteInput.END_OF_INPUT) {
				++n;
			}
			Assert.assertEquals("size in bytes", input.size(), n);

			StringBuilder buffer_1 = new StringBuilder();
			for (long i = 0; i < length; ++i) {
				buffer_1.append((char) reader.get(position + i));
			}
			while (reader.get(n) != ByteInput.END_OF_INPUT) {
				++n;
			}
			StringBuilder buffer_2 = new StringBuilder();
			for (long i = 0; i < length; ++i) {
				buffer_2.append((char) reader.get(position + i));
			}
			Assert.assertEquals("read", buffer_1.toString(), buffer_2.toString());
		} finally {
			FileHelper.close(input);
		}
	}
}

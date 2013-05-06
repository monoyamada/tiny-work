package parser.v1;

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
			int pageSize = 16;
			ByteFileInput reader = new ByteFileInput(input, pageSize);
			long n = 0;
			int ch = reader.get();
			for (; ch != ByteInput.END_OF_INPUT; ++n) {
				ch = reader.next().get();
			}
			Assert.assertEquals("size in bytes", input.size(), n);
		} finally {
			FileHelper.close(input);
		}
	}
	public void testPushMark() throws IOException {
		File file = new File("data/pg76.txt");
		FileChannel input = null;
		try {
			input = new FileInputStream(file).getChannel();
			int pageSize = 4;
			ByteFileInput reader = new ByteFileInput(input, pageSize);
			long n = reader.position();
			int ch = reader.pushMark().get();
			for (; n < 2 * pageSize - 1; ++n) {
				reader.next().get();
			}
			Debug.log().debug(
					"now=" + reader.position() + ", mark=" + reader.getMaxMark());
			Assert.assertEquals("alphabet", ch, reader.popMark().get());
			Assert.assertEquals("#seeks", 0, reader.seekCount);
			
			n = reader.position();
			ch = reader.pushMark().get();
			for (; n < 2 * pageSize; ++n) {
				reader.next().get();
			}
			Debug.log().debug(
					"now=" + reader.position() + ", mark=" + reader.getMaxMark());
			Assert.assertEquals("alphabet", ch, reader.popMark().get());
			Assert.assertEquals("#seeks", 1, reader.seekCount);
		} finally {
			FileHelper.close(input);
		}
	}
}

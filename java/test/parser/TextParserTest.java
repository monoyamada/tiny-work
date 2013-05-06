package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.FileHelper;

public class TextParserTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testParseComment() throws IOException {
		//File outFile = new File("data/testTextParser.log");
		File file = new File("data/testTextParser.txt");
		InputStream reader = null;
		//Writer writer = null;
		try {
			Charset charset = Charset.forName(FileHelper.UTF_8);
			reader = new FileInputStream(file);
			//writer = FileHelper.getWriter(outFile, FileHelper.UTF_8);
			Parser_1 parser = new Parser_1();
			parser.setFileName(file.getName());
			parser.parseComment(reader, charset);
			parser.parseComment(reader, charset);
			String last = FileHelper.readText(reader);
			//writer.append("after: " + last);
			//writer.flush();
			Debug.log().debug("after-comment: " + last);
		} finally {
			FileHelper.close(reader);
			//FileHelper.close(writer);
		}
		//Debug.log().debug("wrote=" + outFile);
	}
}

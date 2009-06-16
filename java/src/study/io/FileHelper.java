package study.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import study.lang.Debug;

public class FileHelper {
	public static String getSystemEncoding() {
		return System.getProperty("file.encoding");
	}
	public static String avoidNullEncoding(String encoding) {
		if (encoding != null) {
			return encoding;
		}
		encoding = FileHelper.getSystemEncoding();
		if (encoding != null) {
			return encoding;
		}
		return "UTF8";
	}

	public static void close(Closeable value) {
		if (value != null) {
			try {
				value.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Writer getWriter(File file) throws IOException {
		return FileHelper.getWriter(file, null, false);
	}
	public static Writer getWriter(File file, String encoding) throws IOException {
		return FileHelper.getWriter(file, encoding, false);
	}
	public static Writer getWriter(File file, String encoding, boolean append)
			throws IOException {
		encoding = FileHelper.avoidNullEncoding(encoding);
		final OutputStream output = new FileOutputStream(file, append);
		return new OutputStreamWriter(output, encoding);
	}

	public static PrintWriter getPrintWriter(File file) throws IOException {
		return FileHelper.getPrintWriter(file, null, false);
	}
	public static PrintWriter getPrintWriter(File file, String encoding)
			throws IOException {
		return FileHelper.getPrintWriter(file, encoding, false);
	}
	public static PrintWriter getPrintWriter(File file, String encoding,
			boolean append) throws IOException {
		final Writer writer = FileHelper.getWriter(file, encoding, append);
		return new PrintWriter(writer);
	}

	public static BufferedReader getBufferedReader(File file) throws IOException {
		return FileHelper.getBufferedReader(file, null);
	}
	public static BufferedReader getBufferedReader(File file, String encoding)
			throws IOException {
		encoding = FileHelper.avoidNullEncoding(encoding);
		final InputStream input = new FileInputStream(file);
		final InputStreamReader reader = new InputStreamReader(input, encoding);
		return new BufferedReader(reader);
	}
	public static void ensureDirectory(File directory) throws IOException {
		Debug.isNotNull(directory);
		if (directory.exists()) {
			if (directory.isFile()) {
				String msg = "the specified directory=" + directory.getAbsolutePath()
						+ " is a file";
				throw new IOException(msg);
			} else if (directory.isDirectory()) {
				return;
			} else {
				String msg = "the specified directory=" + directory.getAbsolutePath()
						+ " is not known type";
				throw new IOException(msg);
			}
		} else {
			if (!directory.mkdirs()) {
				String msg = "failed to create the specified directory="
						+ directory.getAbsolutePath();
				throw new IOException(msg);
			}
		}
	}
}

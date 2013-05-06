package tiny.lang;

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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

public class FileHelper {
	public static final String UTF_8 = "UTF-8";
	public static final String UTF_16 = "UTF-16";;
	public static final char FILE_EXTENSION_SEPARATOR = '.';
	public static final int BOM_0 = 0xEF;
	public static final int BOM_1 = 0xBB;
	public static final int BOM_2 = 0xBF;

	public static String getSystemEncoding() {
		return System.getProperty("file.encoding");
	}

	public static String getCurrentDirectory() {
		return System.getProperty("user.dir");
	}

	public static String avoidNullEncoding(String encoding) {
		if (encoding != null) {
			return encoding;
		}
		return FileHelper.UTF_8;
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
	public static void close(URLConnection value) {
		if (value instanceof HttpURLConnection) {
			final HttpURLConnection http = (HttpURLConnection) value;
			http.disconnect();
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

	public static Reader getReader(File file) throws IOException {
		return FileHelper.getReader(file, null);
	}
	public static Reader getReader(File file, String encoding) throws IOException {
		encoding = FileHelper.avoidNullEncoding(encoding);
		final InputStream input = new FileInputStream(file);
		return new InputStreamReader(input, encoding);
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

	public static String readText(File file) throws IOException {
		return FileHelper.readText(file, null);
	}
	public static String readText(File file, String encoding) throws IOException {
		final StringBuilder buffer = new StringBuilder();
		FileHelper.readText(buffer, file, encoding);
		return buffer.toString();
	}
	public static void readText(Appendable output, File file, String encoding)
			throws IOException {
		Reader reader = null;
		try {
			reader = FileHelper.getReader(file, encoding);
			FileHelper.readText(output, reader);
		} finally {
			FileHelper.close(reader);
		}
	}
	public static String readText(InputStream input) throws IOException {
		try {
			return FileHelper.readText(input, null);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	public static String readText(InputStream input, String encoding)
			throws IOException {
		final StringBuilder buffer = new StringBuilder();
		FileHelper.readText(buffer, input, encoding);
		return buffer.toString();
	}
	public static void readText(Appendable output, InputStream input,
			String encoding) throws IOException {
		encoding = FileHelper.avoidNullEncoding(encoding);
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(input, encoding);
			FileHelper.readText(output, reader);
		} finally {
			FileHelper.close(reader);
		}
	}
	public static String readText(Reader reader) throws IOException {
		StringBuilder buffer = new StringBuilder(1024);
		FileHelper.readText(buffer, reader);
		return buffer.toString();
	}
	public static void readText(Appendable output, Reader reader)
			throws IOException {
		final CharBuffer buffer = CharBuffer.allocate(1024);
		while (0 <= reader.read(buffer)) {
			output.append((CharSequence) buffer.flip());
			buffer.clear();
		}
	}

	public static void writeText(File file, String text) throws IOException {
		FileHelper.writeText(file, text, null);
	}
	public static void writeText(File file, String text, String encoding)
			throws IOException {
		FileHelper.writeText(file, text, encoding, false);
	}
	public static void writeText(File file, String text, String encoding,
			boolean append) throws IOException {
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(file, encoding, append);
			writer.write(text);
			writer.flush();
		} finally {
			FileHelper.close(writer);
		}
	}

	public static URL getURL(String url) throws URISyntaxException,
			MalformedURLException {
		final URI uri = new URI(url);
		return uri.toURL();
	}

	public static boolean copyFile(File output, URI input,
			boolean ignoreModifiedTime) throws MalformedURLException, IOException {
		return FileHelper.copyFile(output, input.toURL(), ignoreModifiedTime);
	}
	/**
	 * 
	 * @param output
	 *          not a directory.
	 * @param input
	 * @param ignoreModifiedTime
	 * @return <code>true</code> iff copied.
	 * @throws IOException
	 */
	public static boolean copyFile(File output, URL input,
			boolean ignoreModifiedTime) throws IOException {
		if (output.isDirectory()) {
			throw new IOException(Messages.getUnexpectedValue(
					output.getAbsolutePath(), "file", "directtory"));
		}
		URLConnection cnn = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			cnn = input.openConnection();
			if (!ignoreModifiedTime && output.exists()
					&& cnn.getLastModified() < output.lastModified()) {
				return false;
			}
			if (cnn instanceof HttpURLConnection) {
				final HttpURLConnection x = (HttpURLConnection) cnn;
				x.setRequestMethod("GET");
			}
			in = cnn.getInputStream();
			out = new FileOutputStream(output);
			byte[] buffer = new byte[1024 * 32];
			int n = in.read(buffer);
			while (0 <= n) {
				out.write(buffer, 0, n);
				out.flush();
				n = in.read(buffer);
			}
			return true;
		} finally {
			FileHelper.close(out);
			FileHelper.close(in);
			FileHelper.close(cnn);
		}
	}

	public static boolean copyFile(File outputFile, File inputFile,
			boolean overwrite, boolean ignoreModfiedTime) throws IOException {
		if (inputFile == null) {
			throw new NullPointerException(Messages.getNull("input file"));
		} else if (!inputFile.isFile()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					inputFile.getAbsolutePath(), "file", "not file"));
		} else if (outputFile == null) {
			throw new NullPointerException(Messages.getNull("output file"));
		} else if (inputFile.isDirectory()) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue(
					outputFile.getAbsolutePath(), "is not directory", "directory"));
		}

		if (inputFile.equals(outputFile)) {
			return true;
		} else if (outputFile.isFile() && !overwrite) {
			return false;
		}

		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(inputFile);
			output = new FileOutputStream(outputFile);
			final FileChannel in = input.getChannel();
			final FileChannel out = output.getChannel();
			in.transferTo(0, in.size(), out);
		} finally {
			FileHelper.close(input);
			FileHelper.close(output);
		}
		return true;
	}

	public static String getFileExtension(String name, char delim) {
		if (name == null || name.length() < 1) {
			return null;
		}
		int ind = name.lastIndexOf(delim);
		if (ind < 0 || name.length() <= ind) {
			return null;
		}
		return name.substring(ind + 1);
	}
	public static String getFileExtension(String name) {
		return FileHelper.getFileExtension(name,
				FileHelper.FILE_EXTENSION_SEPARATOR);
	}
	public static String getFileBody(String name, char delim) {
		if (name == null || name.length() < 1) {
			return null;
		}
		int ind = name.lastIndexOf(delim);
		if (ind < 0) {
			return name;
		}
		return name.substring(0, ind);
	}
	public static String getFileBody(String name) {
		return FileHelper.getFileBody(name, FileHelper.FILE_EXTENSION_SEPARATOR);
	}
}

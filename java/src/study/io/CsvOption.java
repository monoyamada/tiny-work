package study.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import study.function.Cursor;
import study.lang.Debug;
import study.lang.Messages;
import study.lang.StringHelper;

public class CsvOption {
	public static final String COLUMN_SEPARATOR = ",";
	public static final String ROW_SEPARATOR = "\n";
	public static final String COMMENT_MARKER = "#";
	public static final String ENCODING = "UTF-8";
	public static final String QUOTATION = "\"";
	public static final boolean HEADER = true;

	public static class LineCursor implements Cursor<String>, Closeable {
		public static final int COMMENT_LINE_TYPE = 1;
		public static final int HEADER_LINE_TYPE = COMMENT_LINE_TYPE << 1;
		public static final int DATA_LINE_TYPE = HEADER_LINE_TYPE << 1;
		private final File file;
		private final CsvOption csvOption;
		private BufferedReader reader;
		private int lineType;
		private boolean firstLine;
		private String line;

		public LineCursor(File file, CsvOption option) {
			this.file = file;
			this.csvOption = option;
		}
		public File getFile() {
			return this.file;
		}
		public CsvOption getCsvOption() {
			return this.csvOption;
		}
		public int getLineType() {
			return this.lineType;
		}
		protected void setLineType(int lineType) {
			this.lineType = lineType;
		}
		protected boolean isFirstLine() {
			return this.firstLine;
		}
		protected void setFirstLine(boolean firstLine) {
			this.firstLine = firstLine;
		}
		public boolean isCommentLine() {
			return (this.getLineType() & COMMENT_LINE_TYPE) != 0;
		}
		public boolean isHeaderLine() {
			return (this.getLineType() & HEADER_LINE_TYPE) != 0;
		}
		public boolean isDataLine() {
			return (this.getLineType() & DATA_LINE_TYPE) != 0;
		}
		protected BufferedReader getReader(boolean anyway) throws IOException {
			if (this.reader == null && anyway) {
				this.reader = this.newReader();
				this.setFirstLine(true);
			}
			return this.reader;
		}
		protected BufferedReader newReader() throws IOException {
			return FileHelper.getBufferedReader(this.getFile(), this.getCsvOption()
					.getEncoding());
		}
		protected void setReader(BufferedReader reader) {
			this.reader = reader;
		}
		public String getLine() {
			return this.line;
		}
		protected void setLine(String line) {
			this.line = line;
		}

		public boolean move() throws IOException {
			final BufferedReader reader = this.getReader(true);
			final String line = reader.readLine();
			if (line == null) {
				return false;
			}
			final CsvOption opt = this.getCsvOption();
			int lineType = 0;
			if (this.isCommentLine(line)) {
				lineType |= COMMENT_LINE_TYPE;
			} else if (this.isFirstLine()) {
				this.setFirstLine(false);
				if (opt.isHeader()) {
					lineType |= HEADER_LINE_TYPE;
				}
			} else {
				lineType |= DATA_LINE_TYPE;
			}
			this.setLineType(lineType);
			this.setLine(line);
			return true;
		}
		protected boolean isCommentLine(String line) {
			final int index = StringHelper.skipSpaces(line, 0, line.length());
			if (index == line.length()) {
				return true;
			}
			return StringHelper.startsWith(line, index, this.getCsvOption()
					.getCommentMarker(), 0, false);
		}
		public String getValue() {
			return this.getLine();
		}
		public void close() throws IOException {
			if (this.getReader(false) != null) {
				FileHelper.close(this.getReader(false));
				this.setReader(null);
			}
		}
	}

	public static class TokenCursor extends LineCursor {
		private Tokenizer<String> tokenizer;

		public TokenCursor(File file, CsvOption option) {
			super(file, option);
		}
		public boolean moveRow() throws IOException {
			final Tokenizer<String> tokenizer = this.getTokenizer();
			if (!this.move()) {
				tokenizer.setLine(null);
				return false;
			}
			switch (this.getLineType()) {
			case HEADER_LINE_TYPE:
			case DATA_LINE_TYPE:
				tokenizer.setLine(this.getLine());
				break;
			default:
				tokenizer.setLine(null);
				break;
			}
			return true;
		}
		public boolean moveCol() {
			final Tokenizer<String> tokenizer = this.getTokenizer();
			if (tokenizer.getLine() == null) {
				return false;
			}
			return this.getTokenizer().move();
		}
		public String getToken() {
			final Tokenizer<String> tokenizer = this.getTokenizer();
			return tokenizer.getToken();
		}
		protected Tokenizer<String> getTokenizer() {
			if (this.tokenizer == null) {
				this.tokenizer = this.newTokenizer();
			}
			return this.tokenizer;
		}
		protected Tokenizer<String> newTokenizer() {
			return new Tokenizer<String>(this.getCsvOption().getColSeparator());
		}
	}

	/**
	 * @deprecated
	 * @author shirakata
	 */
	public static class FileReader {
		private static final int COMMENT_LINE_TYPE = 1;
		private static final int HEADER_LINE_TYPE = COMMENT_LINE_TYPE << 1;
		private static final int DATA_LINE_TYPE = HEADER_LINE_TYPE << 1;
		private final File file;
		private final CsvOption csvOption;
		private int lineType;
		private Tokenizer<String> tokenizer;

		public FileReader(File file, CsvOption option) {
			this.file = file;
			this.csvOption = option;
		}
		public File getFile() {
			return this.file;
		}
		public CsvOption getCsvOption() {
			return this.csvOption;
		}
		public int getLineType() {
			return this.lineType;
		}
		protected void setLineType(int lineType) {
			this.lineType = lineType;
		}
		protected Tokenizer<String> getTokenizer() {
			if (this.tokenizer == null) {
				this.tokenizer = this.newTokenizer();
			}
			return this.tokenizer;
		}
		protected Tokenizer<String> newTokenizer() {
			return new Tokenizer<String>(this.getCsvOption().getColSeparator());
		}
		public boolean isCommentLine() {
			return (this.getLineType() & COMMENT_LINE_TYPE) != 0;
		}
		public boolean isHeaderLine() {
			return (this.getLineType() & HEADER_LINE_TYPE) != 0;
		}
		public boolean isDataLine() {
			return (this.getLineType() & DATA_LINE_TYPE) != 0;
		}
		public void readFile() throws IOException {
			final CsvOption opt = this.getCsvOption();
			BufferedReader reader = null;
			try {
				reader = FileHelper
						.getBufferedReader(this.getFile(), opt.getEncoding());
				String line = reader.readLine();
				boolean first = true;
				for (int iLine = 0; line != null; ++iLine) {
					// for (int iLine = 0; line != null; line = reader.readLine(),
					// ++iLine) {
					int lineType = 0;
					if (this.isCommentLine(line)) {
						lineType |= COMMENT_LINE_TYPE;
					} else if (first) {
						first = false;
						if (opt.isHeader()) {
							lineType |= HEADER_LINE_TYPE;
						}
					} else {
						lineType |= DATA_LINE_TYPE;
					}
					this.setLineType(lineType);
					if (!this.readLine(iLine, line)) {
						break;
					}
					line = reader.readLine();
				}
			} finally {
				FileHelper.close(reader);
			}
		}
		protected boolean readLine(int lineNumber, String line) throws IOException {
			final Tokenizer<String> tokenizer = this.getTokenizer();
			tokenizer.setLine(line);
			while (tokenizer.hasNext()) {
				final String token = tokenizer.next();
				Debug.log().debug(lineNumber + ": " + token);
			}
			return false;
		}
		protected boolean isCommentLine(String line) {
			final int index = StringHelper.skipSpaces(line, 0, line.length());
			if (index == line.length()) {
				return true;
			}
			return StringHelper.startsWith(line, index, this.getCsvOption()
					.getCommentMarker(), 0, false);
		}
	}

	public static class Tokenizer<T extends CharSequence> implements Iterator<T> {
		private final String deliminator;
		private T line;
		private int begin;
		private int end;

		public Tokenizer(String deliminator) {
			this.deliminator = deliminator;
		}
		public String getDeliminator() {
			return this.deliminator;
		}
		public int getBegin() {
			return this.begin;
		}
		protected void setBegin(int begin) {
			this.begin = begin;
		}
		public int getEnd() {
			return this.end;
		}
		protected void setEnd(int end) {
			this.end = end;
		}
		public T getLine() {
			return this.line;
		}
		public void setLine(T line) {
			this.line = line;
			this.initialize();
		}
		/**
		 * @see #move()
		 */
		public void initialize() {
			this.begin = 0;
			this.end = -1;
		}
		/**
		 * states is represented by the pair of integer (begin, end) as followings:
		 * <code>
		 * start = (0, -1)
		 * end   = (begin, line.length) with 0 <= begin <= line.length
		 * move: (begin, end) |-> (end + line.length, 'next column end' or 'end of line')
		 * </code>
		 *
		 */
		public boolean move() {
			if (!this.hasNext()) {
				return false;
			}
			final String delim = this.getDeliminator();
			final T line = this.line;
			int index = 0;
			if (this.begin <= this.end) {
				this.begin = this.end + delim.length();
			}
			index = this.begin;
			index = StringHelper.indexOf(line, index, line.length(), delim, false);
			if (index < 0) {
				this.end = line.length();
			} else {
				this.end = index;
			}
			return true;
		}
		
		public boolean hasNext() {
			return this.line != null ? this.end < this.line.length() : false;
		}
		public T next() {
			this.move();
			return this.getToken();
		}
		public T rest() {
			final String delim = this.getDeliminator();
			final T line = this.getLine();
			if (this.begin <= this.end) {
				this.begin = this.end + delim.length();
			}
			this.end = line.length();
			return this.getToken();
		}
		@SuppressWarnings("unchecked")
		public T getToken() {
			return (T) this.getLine().subSequence(this.getBegin(), this.getEnd());
		}
		public void remove() {
			throw new UnsupportedOperationException(Messages.getUnSupportedMethod(
					this.getClass(), "remove"));
		}
	}

	private String encoding;
	private String rowSeparator;
	private String colSeparator;
	private String quotation;
	private String commentMarker;
	private boolean header;

	public CsvOption() {
		this.encoding = CsvOption.ENCODING;
		this.rowSeparator = CsvOption.ROW_SEPARATOR;
		this.colSeparator = CsvOption.COLUMN_SEPARATOR;
		this.quotation = CsvOption.QUOTATION;
		this.commentMarker = CsvOption.COMMENT_MARKER;
		this.header = CsvOption.HEADER;
	}
	public String getColSeparator() {
		return this.colSeparator;
	}
	public void setColSeparator(String colSeparator) {
		this.colSeparator = colSeparator;
	}
	public String getQuotation() {
		return this.quotation;
	}
	public void setQuotation(String quotation) {
		this.quotation = quotation;
	}
	public String getRowSeparator() {
		return this.rowSeparator;
	}
	public void setRowSeparator(String rowSeparator) {
		this.rowSeparator = rowSeparator;
	}
	public String getEncoding() {
		return this.encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public boolean hasQuotation(String token) {
		final String q = this.getQuotation();
		final int n = q != null ? q.length() : 0;
		if (token == null || n < 1) {
			return false;
		} else if (token.startsWith(q) && token.endsWith(q)) {
			return true;
		}
		return false;
	}
	public String removeQuotation(String token) {
		final String q = this.getQuotation();
		final int n = q != null ? q.length() : 0;
		if (token == null || n < 1) {
			return token;
		} else if (token.startsWith(q) && token.endsWith(q)) {
			return token.substring(n, token.length() - n);
		}
		return token;
	}
	public String addQuotation(String token) {
		final String q = this.getQuotation();
		final int n = q != null ? q.length() : 0;
		if (token == null || n < 1) {
			return token;
		}
		return q + token + q;
	}
	public boolean isHeader() {
		return this.header;
	}
	public void setHeader(boolean header) {
		this.header = header;
	}
	public String getCommentMarker() {
		return this.commentMarker;
	}
	public void setCommentMarker(String commentMarker) {
		this.commentMarker = commentMarker;
	}

	/**
	 * This is sample method to read CSV file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void readCsv(File file) throws IOException {
		final FileReader reader = new FileReader(file, this);
		reader.readFile();
	}
	public void writeTokens(Writer writer, String... tokens) throws IOException {
		final String delim = this.getColSeparator();
		for (int i = 0, n = tokens.length; i < n; ++i) {
			if (i != 0 && delim != null) {
				writer.write(delim);
			}
			this.writeToken(writer, tokens[i]);
		}
	}
	public void writeToken(Writer writer, String token) throws IOException {
		if (token == null || token.length() < 1) {
			return;
		}
		final String q = this.getQuotation();
		if (q != null && 0 < q.length()) {
			writer.write(q);
			writer.write(token);
			writer.write(q);
		} else {
			writer.write(token);
		}
	}
}

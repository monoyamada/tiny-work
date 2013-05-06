package parser;

import java.io.IOException;
import java.io.Reader;

public class TextParser implements Cloneable {
	protected static final int CH_EOF = -1;
	protected static final int CH_EOL = '\n';

	@SuppressWarnings("serial")
	static class ParserException extends IOException {
		final private TextParser parser;

		static String wrapMessage(TextParser parser, String msg) {
			if (parser == null) {
				return msg;
			}
			try {
				StringBuilder buffer = new StringBuilder(64);
				buffer.append("[");
				parser.getReport(buffer);
				buffer.append("] ");
				buffer.append(msg);
				return buffer.toString();
			} catch (IOException ex) {
				ex.printStackTrace();
				return msg;
			}
		}
		ParserException(TextParser parser, String msg) {
			this(parser, msg, null);
		}
		ParserException(TextParser parser, String msg, Throwable cause) {
			super(wrapMessage(parser, msg), cause);
			this.parser = parser;
		}
		public TextParser getParser() {
			return this.parser;
		}
	}

	private String fileName;
	private int count;

	public TextParser() {
	}
	public TextParser(TextParser that) {
		if (that != null) {
			this.fileName = that.fileName;
			this.count = that.count;
		}
	}
	@Override
	protected TextParser clone() {
		try {
			return (TextParser) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			throw new Error(ex);
		}
	}
	public String getReport() {
		try {
			StringBuilder buffer = new StringBuilder(64);
			this.getReport(buffer);
			return buffer.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return this.toString();
	}
	public Appendable getReport(Appendable output) throws IOException {
		if (this.fileName != null) {
			output.append(this.fileName);
		}
		output.append(":");
		output.append(Integer.toString(this.count));
		return output;
	}
	protected void initialize() {
		this.setCount(0);
	}
	public String getFileName() {
		return this.fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getCount() {
		return this.count;
	}
	protected int setCount(int count) {
		return this.count = count;
	}
	protected int plusCount(int n) {
		return this.count += n;
	}

	public void parseText(Reader reader) throws IOException {
		this.initialize();
		boolean parsing = true;
		while (parsing) {
			int ch = reader.read();
			this.plusCount(1);
			switch (ch) {
			case CH_EOF:
				parsing = false;
			break;
			default:
				break;
			}
		}
	}
}

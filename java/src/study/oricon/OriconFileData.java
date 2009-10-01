package study.oricon;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import study.io.CsvOption;
import study.io.FileHelper;
import study.io.CsvOption.TokenCursor;

public class OriconFileData extends OriconData {
	private final File file;
	private final CsvOption csvOption;

	public OriconFileData(File file, CsvOption option) {
		this.file = file;
		this.csvOption = option;
	}
	public File getFile() {
		return this.file;
	}
	public CsvOption getCsvOption() {
		return this.csvOption;
	}

	/**
	 * <code>
	 * TokenCursor cursor = this.getTokenCursor();
	 * while cursor.moveRow() {
	 *   while cursor.moveCol() {
	 *     System.out.println cursor.getToken();
	 *   }
	 * }
	 * </code>
	 * 
	 * @return
	 */
	public TokenCursor getTokenCursor() {
		return new TokenCursor(this.getFile(), this.getCsvOption());
	}

	protected Writer getWriter() throws IOException {
		return this.getWriter(this.getFile());
	}
	protected Writer getWriter(File file) throws IOException {
		return FileHelper.getWriter(file, this.getCsvOption().getEncoding());
	}
	
	protected void writeRowSeparator(Writer writer) throws IOException {
		final CsvOption opt = this.getCsvOption();
		writer.write(opt.getRowSeparator());
		writer.flush();
	}
	protected void writeColSeparator(Writer writer) throws IOException {
		final CsvOption opt = this.getCsvOption();
		writer.write(opt.getColSeparator());
	}

	protected void writeTexts(Writer writer, String... tokens) throws IOException {
		final CsvOption opt = this.getCsvOption();
		final String delim = opt.getColSeparator();
		for (int i = 0, n = tokens.length; i < n; ++i) {
			if (i != 0 && delim != null) {
				writer.write(delim);
			}
			this.writeText(writer, tokens[i]);
		}
	}
	protected void writeText(Writer writer, String token) throws IOException {
		if (token == null || token.length() < 1) {
			return;
		}
		final String q = this.getCsvOption().getQuotation();
		if (q != null && 0 < q.length()) {
			writer.write(q);
			writer.write(token);
			writer.write(q);
		} else {
			writer.write(token);
		}
	}
	protected String parseText(String token, String dfv) {
		if (token == null) {
			return dfv;
		}
		return this.getCsvOption().removeQuotation(token.trim());
	}

	protected void writePositive(Writer writer, long value) throws IOException {
		if (0 <= value) {
			writer.write(Long.toString(value));
		}
	}

	protected void writeProductType(Writer writer, OriconProductType value)
			throws IOException {
		if (value != null) {
			this.writeText(writer, value.getName());
		}
	}
	protected OriconProductType parseProductType(String token,
			OriconProductType dfv) {
		if (token == null) {
			return dfv;
		}
		token = this.getCsvOption().removeQuotation(token.trim());
		try {
			return OriconProductType.getByName(token);
		} catch (RuntimeException ex) {
			return dfv;
		}
	}
}

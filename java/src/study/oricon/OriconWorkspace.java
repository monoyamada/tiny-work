package study.oricon;

import static study.lang.Debug.isNotNull;
import static study.lang.Debug.isTrue;
import static study.lang.Messages.getUnexpectedValue;
import static study.oricon.OriconConstant.CSV_DIRECTORY;
import static study.oricon.OriconConstant.HTML_DIRECTORY;
import static study.oricon.OriconConstant.TMP_DIRECTORY;

import java.io.File;
import java.io.IOException;

import study.io.CsvOption;
import study.io.FileHelper;

public class OriconWorkspace {
	private final File baseDirectory;
	private File htmlDirectory;
	private File csvDirectory;
	private File tmpDirectory;
	private CsvOption csvOption;

	public OriconWorkspace(File directory) {
		isNotNull("directory", directory);
		isTrue("directory", directory.isDirectory());
		this.baseDirectory = directory;
	}
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	protected File newDirectory(String path) {
		final File dir = new File(this.getBaseDirectory(), path);
		if (dir.isFile()) {
			throw new IllegalStateException(getUnexpectedValue(dir.getAbsolutePath(),
					"not a file", "file"));
		} else if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IllegalStateException(getUnexpectedValue(dir
						.getAbsolutePath(), "successed to create directory", "failed"));
			}
		}
		return dir;
	}
	public File getCsvDirectory() {
		if (this.csvDirectory == null) {
			this.csvDirectory = this.newDirectory(CSV_DIRECTORY);
		}
		return this.csvDirectory;
	}
	public File getHtmlDirectory() {
		if (this.htmlDirectory == null) {
			this.htmlDirectory = this.newDirectory(HTML_DIRECTORY);
		}
		return this.htmlDirectory;
	}
	public File getTmpDirectory() {
		if (this.tmpDirectory == null) {
			this.tmpDirectory = this.newDirectory(TMP_DIRECTORY);
		}
		return this.tmpDirectory;
	}

	public CsvOption getCsvOption() {
		if (this.csvOption == null) {
			this.csvOption = this.newCsvOption();
		}
		return this.csvOption;
	}
	protected CsvOption newCsvOption() {
		final CsvOption opt = new CsvOption();
		opt.setEncoding(FileHelper.UTF_8);
		opt.setHeader(true);
		opt.setQuotation(null);
		return opt;
	}
	public void setCsvOption(CsvOption csvOption) {
		this.csvOption = csvOption;
	}
	public File getTmpFile() throws IOException {
		return this.getTmpFile(null);
	}
	public File getTmpFile(String prefix) throws IOException {
		prefix = prefix != null && 0 < prefix.length() ? prefix : "tmp";
		return File.createTempFile(prefix, ".tmp", this.getTmpDirectory());
	}
}

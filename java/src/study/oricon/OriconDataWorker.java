package study.oricon;

import static study.lang.Debug.isNotNull;
import static study.oricon.OriconConstant.PRODUCT_FILE;

import java.io.File;

import study.io.CsvOption;

public class OriconDataWorker {
	protected static String getProductFileName(OriconProductType type, int index) {
		return PRODUCT_FILE + Integer.toString(index);
	}

	private final OriconWorkspace workspace;

	public OriconDataWorker(OriconWorkspace workspace) {
		isNotNull("workspace", workspace);
		this.workspace = workspace;
	}
	public OriconWorkspace getWorkspace() {
		return this.workspace;
	}
	public File getBaseDirectory() {
		return this.getWorkspace().getBaseDirectory();
	}
	public File getCsvDirectory() {
		return this.getWorkspace().getCsvDirectory();
	}
	public File getHtmlDirectory() {
		return this.getWorkspace().getHtmlDirectory();
	}
	public File getTmpDirectory() {
		return this.getWorkspace().getTmpDirectory();
	}
	public CsvOption getCsvOption() {
		return this.getWorkspace().getCsvOption();
	}
}

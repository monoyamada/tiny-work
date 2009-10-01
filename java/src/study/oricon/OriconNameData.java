package study.oricon;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import study.io.CsvOption;
import study.io.FileHelper;
import study.io.CsvOption.TokenCursor;
import study.io.CsvOption.Tokenizer;
import study.lang.StringHelper;

public class OriconNameData extends OriconFileData {
	protected static final OriconColumn[] COLUMN_ARRAY = { //
	OriconColumn.INDEX //
			, OriconColumn.NAME //
	};
	public static final EnumSet<OriconColumn> COLUMN_SET = EnumSet.of( //
			OriconColumn.INDEX //
			, OriconColumn.NAME //
			);

	/**
	 * @author shirakata
	 */
	public class DataCursor extends TokenCursor {
		public DataCursor(File file, CsvOption option) {
			super(file, option);
		}
		public boolean getData(OriconName output) throws IOException {
			final String line = this.getLine();
			if (!this.isDataLine()) {
				return false;
			}
			final Tokenizer<String> tokenizer = this.getTokenizer();
			tokenizer.setLine(line);
			readCsvData(output, tokenizer);
			return true;
		}
	}

	public OriconNameData(File file, CsvOption opt) {
		super(file, opt);
	}

	public DataCursor getTokenCursor() {
		return new DataCursor(this.getFile(), this.getCsvOption());
	}

	public OriconName[] getDataArray() throws IOException {
		final File file = this.getFile();
		if (!file.isFile()) {
			return OriconName.EMPTY_ARRAY;
		}
		DataCursor cursor = null;
		try {
			final List<OriconName> buffer = new ArrayList<OriconName>(1024);
			cursor = this.getTokenCursor();
			while (cursor.moveRow()) {
				if (cursor.isDataLine()) {
					final OriconName data = new OriconName();
					cursor.getData(data);
					buffer.add(data);
				}
			}
			return buffer.toArray(OriconName.EMPTY_ARRAY);
		} finally {
			FileHelper.close(cursor);
		}
	}

	public void getIndexMap(final Map<Integer, String> output) throws IOException {
		final File file = this.getFile();
		if (!file.isFile()) {
			return;
		}
		DataCursor cursor = null;
		try {
			final OriconName data = new OriconName();
			cursor = this.getTokenCursor();
			while (cursor.moveRow()) {
				if (cursor.isDataLine()) {
					cursor.getData(data);
					output.put(data.getIndex(), data.getName());
				}
			}
		} finally {
			FileHelper.close(cursor);
		}
	}

	public void writeIndexMap(Map<Integer, String> map) throws IOException {
		Writer writer = null;
		try {
			writer = this.getWriter();
			writeCsvHeader(writer);
			for (Entry<Integer, String> data : map.entrySet()) {
				writeCsvData(writer, data.getKey(), data.getValue());
			}
		} finally {
			FileHelper.close(writer);
		}
	}

	public int importData(OriconName[] newArray) throws IOException {
		if (newArray == null || newArray.length < 1) {
			return 0;
		}
		final OriconName[] oldArray = this.getDataArray();
		Writer writer = null;
		try {
			final int newSize = newArray.length;
			writer = this.getWriter();
			int count = 0;
			int newIndex = 0;
			this.writeCsvHeader(writer);
			for (int i = 0, n = oldArray.length; i < n; ++i) {
				final OriconName old = oldArray[i];
				final int oldIndex = old.getIndex();
				for (; newIndex < newSize && newArray[newIndex].getIndex() < oldIndex; ++newIndex) {
					this.writeCsvData(writer, newArray[newIndex]);
					++count;
				}
				for (; newIndex < newSize && newArray[newIndex].getIndex() == oldIndex; ++newIndex) {
					// skip duplication
				}
				this.writeCsvData(writer, old);
			}
			for (; newIndex < newSize; ++newIndex) {
				this.writeCsvData(writer, newArray[newIndex]);
				++count;
			}
			return count;
		} finally {
			FileHelper.close(writer);
		}
	}

	protected void writeCsvHeader(Writer writer) throws IOException {
		if (this.getCsvOption().isHeader()) {
			for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
				if (i != 0) {
					this.writeColSeparator(writer);
				}
				this.writeText(writer, COLUMN_ARRAY[i].getName());
			}
			this.writeRowSeparator(writer);
		}
	}
	protected void writeCsvData(Writer writer, OriconName data)
			throws IOException {
		this.writeCsvData(writer, data.getIndex(), data.getName());
	}
	protected void writeCsvData(Writer writer, int index, String name)
			throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			if (i != 0) {
				this.writeColSeparator(writer);
			}
			switch (COLUMN_ARRAY[i]) {
			case INDEX:
				this.writePositive(writer, index);
				break;
			case NAME:
				this.writeText(writer, name);
				break;
			default:
				throw new Error("bug");
			}
		}
		this.writeRowSeparator(writer);
	}
	protected void readCsvData(OriconName output, Tokenizer<String> tokenizer)
			throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			final OriconColumn col = COLUMN_ARRAY[i];
			switch (col) {
			case INDEX:
				if (tokenizer.move()) {
					output.setIndex(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName() + " "
							+ tokenizer.getLine());
				}
				break;
			case NAME:
				if (tokenizer.move()) {
					output.setName(this.parseText(tokenizer.getToken(), null));
				} else {
					throw new IOException("failed to read " + col.getName() + " "
							+ tokenizer.getLine());
				}
				break;
			default:
				throw new Error("bug");
			}
		}
	}
}

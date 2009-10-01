package study.oricon;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.ArrayHelper;

public class OriconMultiNameData extends OriconNameData {
	public OriconMultiNameData(File file, CsvOption opt) {
		super(file, opt);
	}
	public void getIndexMultiMap(final Map<Integer, String[]> output)
			throws IOException {
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
					final Integer key = data.getIndex();
					String[] value = output.get(key);
					if (value == null || value.length < 1) {
						value = new String[] { data.getName() };
						output.put(key, value);
					} else if (ArrayHelper.indexOf(value, data.getName()) < 0) {
						value = ArrayHelper.add(value, data.getName());
						output.put(key, value);
					}
				}
			}
		} finally {
			FileHelper.close(cursor);
		}
	}
	public void writeIndexMultiMap(Map<Integer, String[]> map) throws IOException {
		Writer writer = null;
		try {
			writer = this.getWriter();
			writeCsvHeader(writer);
			for (Entry<Integer, String[]> data : map.entrySet()) {
				final int index = data.getKey();
				for (String name : data.getValue()) {
					this.writeCsvData(writer, index, name);
				}
			}
		} finally {
			FileHelper.close(writer);
		}
	}
}

package study.oricon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import study.io.FileHelper;
import study.primitive.LongArrayList;

public class GdpReform extends OriconDataWorker {
	protected static class NamedObject<T> {
		private final String name;
		private final T value;

		public NamedObject(String name, T value) {
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return this.name;
		}
		public T getValue() {
			return this.value;
		}
	}

	public GdpReform(OriconWorkspace workspace) {
		super(workspace);
	}
	protected void toCsv(File csvFile, File tabFile) throws IOException {
		final List<NamedObject<long[]>> columnBuffer = new ArrayList<NamedObject<long[]>>(
				1024);
		BufferedReader reader = null;
		try {
			final LongArrayList valueBuffer = new LongArrayList(1024);
			reader = FileHelper.getBufferedReader(tabFile, "MS932");
			String line = reader.readLine();
			for (int iRow = 0; line != null; line = reader.readLine(), ++iRow) {
				if (line.startsWith("#")) {
					continue;
				}
				valueBuffer.removeAll();
				String name = null;
				int begin = 0;
				int end = line.indexOf('\t', begin);
				for (int iCol = 0; 0 <= end; end = line.indexOf('\t', begin), ++iCol) {
					final String token = line.substring(begin, end);
					if (iCol < 1) {
						name = token.trim();
					} else {
						final double value = GdpReform.toDouble(token, Double.NaN);
						valueBuffer.addLast(Double.doubleToLongBits(value));
					}
					begin = end + 1;
				}
				final String token = line.substring(begin);
				final double value = GdpReform.toDouble(token, Double.NaN);
				valueBuffer.addLast(Double.doubleToLongBits(value));
				final NamedObject<long[]> column = new NamedObject<long[]>(name,
						valueBuffer.toArray());
				columnBuffer.add(column);
			}
		} finally {
			FileHelper.close(reader);
		}
		Writer writer = null;
		int nRow = 0;
		for (int i = 0, n = columnBuffer.size(); i < n; ++i) {
			nRow = Math.max(columnBuffer.get(i).getValue().length, nRow);
		}
		try {
			writer = FileHelper.getWriter(csvFile, FileHelper.UTF_8);
			writer.write("Year");
			GdpReform.writeColSeparator(writer);
			writer.write("Quoter");
			for (int iCol = 0, nCol = columnBuffer.size(); iCol < nCol; ++iCol) {
				final NamedObject<long[]> data = columnBuffer.get(iCol);
				GdpReform.writeColSeparator(writer);
				writer.write(data.getName());
			}
			GdpReform.writeRowSeparator(writer);
			for (int iRow = 0; iRow < nRow; ++iRow) {
				final int year = 1980 + iRow / 4;
				final int quoter = iRow % 4 + 1;
				writer.write(Integer.toString(year));
				GdpReform.writeColSeparator(writer);
				writer.write(Integer.toString(quoter));
				for (int iCol = 0, nCol = columnBuffer.size(); iCol < nCol; ++iCol) {
					final NamedObject<long[]> data = columnBuffer.get(iCol);
					final long[] value = data.getValue();
					GdpReform.writeColSeparator(writer);
					if (iRow < value.length) {
						writer.write(Double.toString(Double.longBitsToDouble(value[iRow])));
					}
				}
				GdpReform.writeRowSeparator(writer);
			}
		} finally {
			FileHelper.close(writer);
		}
	}
	protected static void writeRowSeparator(Writer writer) throws IOException {
		writer.write('\n');
		writer.flush();
	}
	protected static void writeColSeparator(Writer writer) throws IOException {
		writer.write(", ");
	}
	protected static double toDouble(String token, double dfv) {
		token = token.trim();
		if (token.startsWith("\"") && token.endsWith("\"") && 2 <= token.length()) {
			token = token.substring(1, token.length() - 1);
		}
		token = token.replaceAll(",", "");
		try {
			return Double.parseDouble(token);
		} catch (NumberFormatException ex) {
			return dfv;
		}
	}
}

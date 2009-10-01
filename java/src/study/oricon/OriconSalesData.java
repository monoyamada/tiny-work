package study.oricon;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import study.io.CsvOption;
import study.io.FileHelper;
import study.io.CsvOption.TokenCursor;
import study.io.CsvOption.Tokenizer;
import study.lang.ArrayHelper;
import study.lang.StringHelper;
import study.oricon.OriconDataBuilder.DayRanking;
import study.primitive.LongArrayList;
import study.primitive.LongList;
import study.primitive.PrimitiveHelper;

public class OriconSalesData extends OriconFileData {
	protected static final OriconColumn[] COLUMN_ARRAY = { //
	OriconColumn.DAY //
			, OriconColumn.PRODUCT //
			, OriconColumn.SALES //
	};
	public static final EnumSet<OriconColumn> COLUMN_SET = EnumSet.of(//
			OriconColumn.DAY //
			, OriconColumn.PRODUCT //
			, OriconColumn.SALES //
			);

	public static class Sales extends OriconData {
		public static final Sales[] EMPTY_ARRAY = {};
		private int day;
		private int product;
		private int sales;

		public Sales() {
			this.day = -1;
			this.product = -1;
			this.sales = -1;
		}
		public Sales(int day, int product, int sales) {
			this.day = day;
			this.product = product;
			this.sales = sales;
		}
		public int getDay() {
			return this.day;
		}
		protected void setDay(int day) {
			this.day = day;
		}
		public int getProduct() {
			return this.product;
		}
		protected void setProduct(int product) {
			this.product = product;
		}
		public int getSales() {
			return this.sales;
		}
		protected void setSales(int sales) {
			this.sales = sales;
		}
	}

	/**
	 * @author shirakata
	 */
	public class DataCursor extends TokenCursor {
		public DataCursor(File file, CsvOption option) {
			super(file, option);
		}
		public boolean getData(Sales output) throws IOException {
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

	public OriconSalesData(File file, CsvOption option) {
		super(file, option);
	}

	public DataCursor getTokenCursor() {
		super.getTokenCursor();
		return new DataCursor(this.getFile(), this.getCsvOption());
	}
	public Sales[] getDataArray() throws IOException {
		final File file = this.getFile();
		if (!file.isFile()) {
			return Sales.EMPTY_ARRAY;
		}
		DataCursor cursor = null;
		try {
			final List<Sales> buffer = new ArrayList<Sales>(1024);
			cursor = this.getTokenCursor();
			while (cursor.moveRow()) {
				if (cursor.isDataLine()) {
					final Sales data = new Sales();
					cursor.getData(data);
					buffer.add(data);
				}
			}
			return buffer.toArray(Sales.EMPTY_ARRAY);
		} finally {
			FileHelper.close(cursor);
		}
	}
	public int[] getDayArray() throws IOException {
		final File file = this.getFile();
		if (!file.isFile()) {
			return ArrayHelper.EMPTY_INT_ARRAY;
		}
		DataCursor cursor = null;
		try {
			final LongList buffer = new LongArrayList(1024);
			cursor = this.getTokenCursor();
			final Sales data = new Sales();
			while (cursor.moveRow()) {
				if (cursor.isDataLine()) {
					cursor.getData(data);
					final int day = data.getDay();
					if (buffer.getSize() < 1 || buffer.getLast(-1) < day) {
						buffer.addLast(day);
					}
				}
			}
			return PrimitiveHelper.toIntegerArray(buffer);
		} finally {
			FileHelper.close(cursor);
		}
	}

	public int importData(final DayRanking[] newArray) throws IOException {
		if (newArray == null || newArray.length < 1) {
			return 0;
		}
		final Sales[] oldArray = this.getDataArray();
		Writer writer = null;
		try {
			final int newSize = newArray.length;
			writer = this.getWriter();
			int count = 0;
			int newIndex = 0;
			this.writeCsvHeader(writer);
			for (int iOld = 0, nOld = oldArray.length; iOld < nOld; ++iOld) {
				final Sales oldData = oldArray[iOld];
				final int oldDay = oldData.getDay();
				for (; newIndex < newSize && newArray[newIndex].getDay() < oldDay; ++newIndex) {
					this.writeCsvData(writer, newArray[newIndex]);
					++count;
				}
				for (; newIndex < newSize && newArray[newIndex].getDay() == oldDay; ++newIndex) {
					// skip duplication
				}
				this.writeCsvData(writer, oldData);
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
	protected void writeCsvData(Writer writer, DayRanking ranking)
			throws IOException {
		final int day = ranking.getDay();
		final OriconRanking[] xs = ranking.getRanking();
		for (int i = 0, n = xs.length; i < n; ++i) {
			writeCsvData(writer, day, xs[i].getProduct().getIndex(), xs[i].getSales());
		}
	}
	protected void writeCsvData(Writer writer, Sales data) throws IOException {
		this
				.writeCsvData(writer, data.getDay(), data.getProduct(), data.getSales());
	}
	protected void writeCsvData(Writer writer, int day, int product, int sales)
			throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			if (i != 0) {
				this.writeColSeparator(writer);
			}
			switch (COLUMN_ARRAY[i]) {
			case DAY:
				this.writePositive(writer, day);
				break;
			case PRODUCT:
				this.writePositive(writer, product);
				break;
			case SALES:
				this.writePositive(writer, sales);
				break;
			default:
				throw new Error("bug");
			}
		}
		this.writeRowSeparator(writer);
	}

	protected void readCsvData(Sales output, Tokenizer<String> tokenizer)
			throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			final OriconColumn col = COLUMN_ARRAY[i];
			switch (col) {
			case DAY:
				if (tokenizer.move()) {
					output.setDay(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName() + " "
							+ tokenizer.getLine());
				}
				break;
			case PRODUCT:
				if (tokenizer.move()) {
					output.setProduct(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName() + " "
							+ tokenizer.getLine());
				}
				break;
			case SALES:
				if (tokenizer.move()) {
					output.setSales(StringHelper.parseInt(tokenizer.getToken(), -1));
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

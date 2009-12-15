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
import study.lang.StringHelper;

public class OriconProductData extends OriconFileData {
	protected static final OriconColumn[] COLUMN_ARRAY = { //
	OriconColumn.INDEX //
			, OriconColumn.BRAND //
			, OriconColumn.PRODUCT_TYPE //
			, OriconColumn.RELEASE_DAY //
	};
	public static final EnumSet<OriconColumn> COLUMN_SET = EnumSet.of(//
			OriconColumn.INDEX //
			, OriconColumn.BRAND //
			, OriconColumn.PRODUCT_TYPE //
			, OriconColumn.RELEASE_DAY //
			);
	
	public static class Product extends OriconIndex {
		public static final Product[] EMPTY_ARRAY = {};
		private int brand;
		private OriconProductType productType;
		private int releaseDay;

		public Product() {
			this(-1, -1, null, -1);
		}
		public Product(int index) {
			this(index, -1, null, -1);
		}
		public Product(int index, int brand) {
			this(index, brand, null, -1);
		}
		public Product(int index, int brand, OriconProductType productType) {
			this(index, brand, productType, -1);
		}
		public Product(int index, int brand, OriconProductType productType,
				int releaseDay) {
			super(index);
			this.brand = brand;
			this.productType = productType;
			this.releaseDay = releaseDay;
		}
		public int getBrand() {
			return this.brand;
		}
		public void setBrand(int brand) {
			this.brand = brand;
		}
		public OriconProductType getProductType() {
			return this.productType;
		}
		public void setProductType(OriconProductType productType) {
			this.productType = productType;
		}
		public int getReleaseDay() {
			return this.releaseDay;
		}
		public void setReleaseDay(int releaseDay) {
			this.releaseDay = releaseDay;
		}
	}
	
	/**
	 * @author shirakata
	 */
	public class DataCursor extends TokenCursor {
		public DataCursor(File file, CsvOption option) {
			super(file, option);
		}
		public boolean getData(Product output) throws IOException {
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

	public OriconProductData(File file, CsvOption option) {
		super(file, option);
	}
	
	public DataCursor getTokenCursor() {
		return new DataCursor(this.getFile(), this.getCsvOption());
	}
	
	public Product[] getDataArray() throws IOException {
		final File file = this.getFile();
		if (!file.isFile()) {
			return Product.EMPTY_ARRAY;
		}
		DataCursor cursor = null;
		try {
			final List<Product> buffer = new ArrayList<Product>(1024);
			cursor = this.getTokenCursor();
			while (cursor.moveRow()) {
				if (cursor.isDataLine()) {
					final Product data = new Product();
					cursor.getData(data);
					buffer.add(data);
				}
			}
			return buffer.toArray(Product.EMPTY_ARRAY);
		} finally {
			FileHelper.close(cursor);
		}
	}

	public void writeDataArray(Product[] array) throws IOException {
		Writer writer = null;
		try {
			writer = this.getWriter();
			writeCsvHeader(writer);
			for (Product data : array) {
				writeCsvData(writer, data);
			}
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
	protected void writeCsvData(Writer writer, Product data) throws IOException {
		this.writeCsvData(writer, data.getIndex(), data.getBrand(), data
				.getProductType(), data.getReleaseDay());
	}
	protected void writeCsvData(Writer writer, int index, int brand,
			OriconProductType productType, int releaseDay) throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			if (i != 0) {
				this.writeColSeparator(writer);
			}
			switch (COLUMN_ARRAY[i]) {
			case INDEX:
				this.writePositive(writer, index);
				break;
			case BRAND:
				this.writePositive(writer, brand);
				break;
			case PRODUCT_TYPE:
				this.writeProductType(writer, productType);
				break;
			case RELEASE_DAY:
				this.writePositive(writer, releaseDay);
				break;
			default:
				throw new Error("bug");
			}
		}
		this.writeRowSeparator(writer);
	}
	protected void readCsvData(Product output, Tokenizer<String> tokenizer)
			throws IOException {
		for (int i = 0, n = COLUMN_ARRAY.length; i < n; ++i) {
			final OriconColumn col = COLUMN_ARRAY[i];
			switch (col) {
			case INDEX:
				if (tokenizer.move()) {
					output.setIndex(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName());
				}
				break;
			case BRAND:
				if (tokenizer.move()) {
					output.setBrand(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName());
				}
				break;
			case PRODUCT_TYPE:
				if (tokenizer.move()) {
					output.setProductType(this.parseProductType(tokenizer.getToken(),
							null));
				} else {
					throw new IOException("failed to read " + col.getName());
				}
				break;
			case RELEASE_DAY:
				if (tokenizer.move()) {
					output.setReleaseDay(StringHelper.parseInt(tokenizer.getToken(), -1));
				} else {
					throw new IOException("failed to read " + col.getName());
				}
				break;
			default:
				throw new Error("bug");
			}
		}
	}
}

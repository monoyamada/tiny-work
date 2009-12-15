package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import study.io.CsvOption;
import study.io.FileHelper;
import study.lang.ObjectHelper;

public class ProductRecordList {
	private List<ProductRecord> list;
	private Map<String, BitSet> brandMap;
	private Map<String, BitSet> productMap;

	public ProductRecord getProductRecord(String brand, String product) {
		final BitSet products = this.getProductMap(true).get(product);
		if (products == null || products.cardinality() < 1) {
			return null;
		}
		final List<ProductRecord> list = this.getList(true);
		int index = 0;
		for (int i = 0, n = products.cardinality(); i < n; ++i) {
			index = products.nextSetBit(index);
			final ProductRecord x = list.get(index++);
			if (ObjectHelper.equals(x.getBrand(), brand)) {
				return x;
			}
		}
		return null;
	}

	protected List<ProductRecord> getList(boolean anyway) {
		if (this.list == null && anyway) {
			this.list = this.newList();
		}
		return this.list;
	}
	protected List<ProductRecord> newList() {
		return new ArrayList<ProductRecord>();
	}
	public List<ProductRecord> getList() {
		return this.getList(true);
	}
	protected void setList(List<ProductRecord> list) {
		this.list = list;
	}
	protected Map<String, BitSet> newNameIndexMap() {
		return new HashMap<String, BitSet>();
	}
	protected Map<String, BitSet> getBrandMap(boolean anyway) {
		if (this.brandMap == null && anyway) {
			this.brandMap = this.newBrandMap();
		}
		return this.brandMap;
	}
	protected Map<String, BitSet> newBrandMap() {
		final Map<String, BitSet> map = this.newNameIndexMap();
		final List<ProductRecord> list = this.getList(true);
		for (int i = 0, n = list.size(); i < n; ++i) {
			final ProductRecord x = list.get(i);
			final String name = x.getBrand();
			BitSet value = map.get(name);
			if (value == null) {
				value = new BitSet();
				map.put(name, value);
			}
			value.set(i, true);
		}
		return map;
	}
	protected void setBrandMap(Map<String, BitSet> brandMap) {
		this.brandMap = brandMap;
	}
	protected Map<String, BitSet> getProductMap(boolean anyway) {
		if (this.productMap == null && anyway) {
			this.productMap = this.newProductMap();
		}
		return this.productMap;
	}
	protected Map<String, BitSet> newProductMap() {
		final Map<String, BitSet> map = this.newNameIndexMap();
		final List<ProductRecord> list = this.getList(true);
		for (int i = 0, n = list.size(); i < n; ++i) {
			final ProductRecord x = list.get(i);
			final String name = x.getProduct();
			BitSet value = map.get(name);
			if (value == null) {
				value = new BitSet();
				map.put(name, value);
			}
			value.set(i, true);
		}
		return map;
	}
	protected void setProductMap(Map<String, BitSet> productMap) {
		this.productMap = productMap;
	}

	public void readCsv(File file, CsvOption opt) throws IOException {
		BufferedReader reader = null;
		try {
			reader = FileHelper.getBufferedReader(file, opt.getEncoding());
			this.readCsv(reader, opt);
		} finally {
			FileHelper.close(reader);
		}
	}
	public void readCsv(Reader reader, CsvOption opt) throws IOException {
		BufferedReader lineReader;
		if (reader instanceof BufferedReader) {
			lineReader = (BufferedReader) reader;
		} else {
			lineReader = new BufferedReader(reader);
		}
		this.readCsv1(lineReader, opt);
	}
	protected void readCsv1(BufferedReader reader, CsvOption opt)
			throws IOException {
		final Calendar calendar = Calendar.getInstance();
		final ArrayList<ProductRecord> list = new ArrayList<ProductRecord>();
		String line = reader.readLine();
		boolean first = true;
		int iLine = 1;
		for (; line != null; line = reader.readLine(), ++iLine) {
			line = line.trim();
			if (line.length() < 1 || line.startsWith(opt.getCommentMarker())) {
				continue;
			} else if (first) {
				first = false;
				if (opt.isHeader()) {
					continue;
				}
			}
			final ProductRecord record = new ProductRecord();
			record.readCsv(calendar, line, opt);
			list.add(record);
		}
		this.setList(list);
		this.setBrandMap(null);
		this.setProductMap(null);
	}
	public void writeCsv(File file, CsvOption opt) throws IOException {
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(file, opt.getEncoding());
			this.writeCsv(writer, opt);
		} finally {
			FileHelper.close(writer);
		}
	}
	public void writeCsv(Writer writer, CsvOption opt) throws IOException {
		final Calendar calendar = Calendar.getInstance();
		final List<ProductRecord> list = this.getList(true);
		if (opt.isHeader()) {
			ProductRecord.writeCsvHeader(writer, opt);
		}
		writer.write(opt.getRowSeparator());
		writer.flush();
		for (int i = 0, n = list.size(); i < n; ++i) {
			final ProductRecord record = list.get(i);
			record.writeCsv(writer, calendar, opt);
			writer.write(opt.getRowSeparator());
			writer.flush();
		}
	}
}

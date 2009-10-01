package study.misc;

import java.io.Flushable;
import java.io.IOException;

import study.io.CsvOption;

public class OriconCsvHelper {
	public static void writeRowSeparator(Appendable writer, CsvOption opt)
			throws IOException {
		writer.append(opt.getRowSeparator());
		if(writer instanceof Flushable){
			final Flushable x = (Flushable) writer;
			x.flush();
		}
	}
	public static void writeNonNegative(Appendable writer, long value,
			CsvOption opt) throws IOException {
		if (0 <= value) {
			writer.append(Long.toString(value));
		}
	}
	public static void writeNonEmpty(Appendable writer, String value,
			CsvOption opt) throws IOException {
		if (value != null && 0 < value.length()) {
			value = value.trim();
			if (0 < value.length()) {
				value = opt.addQuotation(value);
				writer.append(value);
			}
		}
	}
}

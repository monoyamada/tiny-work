package study.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.util.Calendar;

import study.io.FileHelper;
import study.lang.Debug;
import study.lang.Messages;

public class SalesTestClasses {
	public static final String EOL = "\n";
	public static long SECOND_IN_MILISECOND = 1000;
	public static long MINUT_IN_MILISECOND = 60 * SECOND_IN_MILISECOND;
	public static long HOUR_IN_MILISECOND = 60 * MINUT_IN_MILISECOND;
	public static long DAY_IN_MILISECOND = 24 * HOUR_IN_MILISECOND;

	public static void normalizeSalesTable(File output, File input)
			throws IOException {
		Writer writer = null;
		BufferedReader reader = null;
		try {
			writer = FileHelper.getWriter(output, FileHelper.UTF_8);
			reader = FileHelper.getBufferedReader(input, FileHelper.UTF_8);
			SalesTestClasses.normalizeSalesTable(writer, reader);
		} finally {
			FileHelper.close(writer);
			FileHelper.close(reader);
		}
	}
	protected static void normalizeSalesTable(Writer writer, BufferedReader reader)
			throws IOException {
		long firstTime = -1;
		String line = reader.readLine();
		while (line != null) {
			final String[] tokens = line.split("\\w");
			if (tokens.length == 5) {
				long time = SalesTestClasses.toTime(tokens[0]);
				if (firstTime < 0) {
					firstTime = time;
				} else {
					time -= firstTime;
				}
				final int day = (int) (time / DAY_IN_MILISECOND);
				final int sales = SalesTestClasses.toSales(tokens[2]);
				writer.write(Integer.toString(day));
				writer.write(", ");
				writer.write(Integer.toString(sales));
				writer.write(EOL);
			}
			line = reader.readLine();
		}
	}
	private static int toSales(String token) {
		final String value = token.replaceFirst("*", "").replaceFirst(",", "");
		return Integer.parseInt(value);
	}
	private static long toTime(String token) {
		final String[] tokens = token.split("/");
		if (tokens.length != 3) {
			String msg = Messages.getUnexpectedValue("date", "08/04/28", token);
			throw new IllegalArgumentException(msg);
		}
		final int year = Integer.parseInt(tokens[0]);
		final int month = Integer.parseInt(tokens[1]);
		final int day = Integer.parseInt(tokens[2]);
		final Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date.getTimeInMillis();
	}
}

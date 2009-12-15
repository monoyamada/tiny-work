package study.oricon;

import static study.oricon.OriconProductType.ALBUM;
import static study.oricon.OriconProductType.SINGLE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Calendar;

import study.function.AbFunction;
import study.function.Function;
import study.io.CsvOption;
import study.io.FileHelper;
import study.io.CsvOption.LineCursor;
import study.lang.Debug;

public class OriconSqlite extends OriconDataWorker {
	public static final String SQLITE_FILE = "oricon.db";
	public static final String SQLITE_DIRECTORY = "sqlite";

	private final File sqliteExe;
	private File sqliteFile;
	private File sqliteDirectory;

	public OriconSqlite(OriconWorkspace workspace, File sqliteExe) {
		super(workspace);
		this.sqliteExe = sqliteExe;
	}
	public File getSqliteExe() {
		return this.sqliteExe;
	}
	public File getSqliteFile() throws IOException {
		if (this.sqliteFile == null) {
			this.sqliteFile = this.newSqliteFile();
		}
		return this.sqliteFile;
	}
	protected File newSqliteFile() throws IOException {
		return new File(this.getSqliteDirectory(), OriconSqlite.SQLITE_FILE);
	}
	public File getSqliteDirectory() throws IOException {
		if (this.sqliteDirectory == null) {
			this.sqliteDirectory = this.newSqliteDirectory();
		}
		return this.sqliteDirectory;
	}
	protected File newSqliteDirectory() throws IOException {
		final File dir = new File(this.getBaseDirectory(),
				OriconSqlite.SQLITE_DIRECTORY);
		FileHelper.ensureDirectory(dir);
		return dir;
	}
	
	public void makeSqliteFile() throws IOException {
		final String exe = this.getSqliteExe().getAbsolutePath();
		final File csvDir = this.getCsvDirectory();
		final File sqliteDir = this.getSqliteDirectory();
		final File sqliteFile = this.getSqliteFile();
		if (sqliteFile.isFile()) {
			sqliteFile.delete();
		}
		final Function<String, String> nameModifier = new AbFunction<String, String>() {
			public String evaluate(String source) throws Exception {
				return source;
			}
		};
		if (true) {
			final String tableName = "ProductTypes";
			String cmd = "create table " + tableName
					+ " (Name text primary key, ProductType integer not null unique);";
			cmd += "insert into " + tableName + " values('albumCD',"
					+ ALBUM.getValue() + ");";
			cmd += "insert into " + tableName + " values('singleCD',"
					+ SINGLE.getValue() + ");";
			final ProcessBuilder builder = new ProcessBuilder(exe, sqliteFile
					.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);
		}
		if (true) {
			final String tableName = "BrandNames";
			String cmd = "create table " + tableName
					+ " (Brand integer not null, Name text not null);";
			ProcessBuilder builder = new ProcessBuilder(exe, sqliteFile.getName(),
					cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			final File input = new File(csvDir, "brand-name.csv");
			final File output = new File(sqliteDir, input.getName());
			makeDataFile(output, input, nameModifier);

			cmd = ".import " + output.getName() + " " + tableName;
			builder = new ProcessBuilder(exe, "-separator", ",",
					sqliteFile.getName(), "\"" + cmd + "\"");
			builder.directory(sqliteDir);
			this.runProcess(builder);

			String colName = "Brand";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);
		}
		if (true) {
			final String tableName = "ProductNames";
			String cmd = "create table " + tableName
					+ " (Product integer not null, Name text not null);";
			ProcessBuilder builder = new ProcessBuilder(exe, sqliteFile.getName(),
					cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			final File input = new File(csvDir, "product-name.csv");
			final File output = new File(sqliteDir, input.getName());
			makeDataFile(output, input, nameModifier);

			cmd = ".import " + output.getName() + " " + tableName;
			builder = new ProcessBuilder(exe, "-separator", ",",
					sqliteFile.getName(), "\"" + cmd + "\"");
			builder.directory(sqliteDir);
			this.runProcess(builder);

			String colName = "Product";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);
		}
		if (true) {
			final String tableName = "Products";
			String cmd = "create table "
					+ tableName
					+ " (Product integer primary key, Brand integer not null, ProductType integer not null, ReleaseDay integer);";
			ProcessBuilder builder = new ProcessBuilder(exe, sqliteFile.getName(),
					cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			final File input = new File(csvDir, "product.csv");
			final File output = new File(sqliteDir, input.getName());
			makeDataFile(output, input, new AbFunction<String, String>() {
				public String evaluate(String source) throws Exception {
					final char delim = ',';
					int begin = 0;
					int end = source.indexOf(delim, begin);// product
					end = source.indexOf(delim, begin = end + 1);// brand
					end = source.indexOf(delim, begin = end + 1);// productType
					final String token = source.substring(begin, end).trim();
					final OriconProductType type = OriconProductType.getByName(token);
					return source.substring(0, begin) + type.getValue()
							+ source.substring(end);
				}
			});
			cmd = ".import " + output.getName() + " " + tableName;
			builder = new ProcessBuilder(exe, "-separator", ",",
					sqliteFile.getName(), "\"" + cmd + "\"");
			builder.directory(sqliteDir);
			this.runProcess(builder);

			String colName = "Product";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			colName = "Brand";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			colName = "ReleaseDay";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);
		}
		if (true) {
			final String tableName = "Sales";
			String cmd = "create table "
					+ tableName
					+ " (BeginDay integer not null, EndDay integer not null, Product integer not null, Sales integer not null);";
			ProcessBuilder builder = new ProcessBuilder(exe, sqliteFile.getName(),
					cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);

			final File album = new File(csvDir, "album-sales.csv");
			final File single = new File(csvDir, "single-sales.csv");
			final File output = new File(sqliteDir, "sales.csv");
			this.makeSalesFile(output, album, single);
			cmd = ".import " + output.getName() + " " + tableName;
			builder = new ProcessBuilder(exe, "-separator", ",",
					sqliteFile.getName(), "\"" + cmd + "\"");
			builder.directory(sqliteDir);
			this.runProcess(builder);

			String colName = "Product";
			cmd = "create index " + this.IndexName(tableName, colName) + " on "
					+ tableName + "(" + colName + ")";
			builder = new ProcessBuilder(exe, sqliteFile.getName(), cmd);
			builder.directory(sqliteDir);
			this.runProcess(builder);
		}
	}
	private String IndexName(String tableName, String colName) {
		return tableName + "_" + colName;
	}
	private void runProcess(ProcessBuilder builder) throws IOException {
		final Process proc = builder.start();
		InputStream in = null;
		try {
			in = proc.getErrorStream();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = reader.readLine();
			for (; line != null; line = reader.readLine()) {
				Debug.log().error(line);
			}
		} finally {
			FileHelper.close(in);
		}
	}
	protected void makeSalesFile(File output, File... inputs) throws IOException {
		final CsvOption opt = this.getCsvOption();
		final String delim = opt.getColSeparator();
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(output, opt.getEncoding());
			for (File input : inputs) {
				LineCursor cursor = null;
				try {
					final Calendar date = OriconDateHelper.newCalendar();
					final int dayOffset = -7;
					int lastDay = -1;
					int rank = 0;
					cursor = new LineCursor(input, opt);
					while (cursor.move()) {
						if (cursor.isDataLine()) {
							final String line = cursor.getLine();
							final int index = line.indexOf(delim);
							final int day = Integer.parseInt(line.substring(0, index).trim());
							OriconDateHelper.dayToSystemTime(date, day);
							switch (date.get(Calendar.MONTH)) {
							case 0:
								if (lastDay < 0) {
									writer.write(Integer.toString(day - 6 + dayOffset));
								} else {
									writer.write(Integer.toString(lastDay + 1 + dayOffset));
								}
								break;
							default:
								writer.write(Integer.toString(day - 6 + dayOffset));
								break;
							}
							writer.write(delim);
							writer.write(Integer.toString(day + 1 + dayOffset));
							writer.write(line.substring(index));
							writer.write(opt.getRowSeparator());
							writer.flush();
							rank += 1;
							if (rank % 30 == 0) {
								lastDay = day;
							}
						}
					}
				} finally {
					FileHelper.close(cursor);
				}
			}
		} finally {
			FileHelper.close(writer);
		}
	}
	protected void makeDataFile(File output, File input,
			Function<String, String> fnc) throws IOException {
		final CsvOption opt = this.getCsvOption();
		Writer writer = null;
		LineCursor cursor = null;
		try {
			writer = FileHelper.getWriter(output, opt.getEncoding());
			cursor = new LineCursor(input, opt);
			while (cursor.move()) {
				if (cursor.isDataLine()) {
					String line = null;
					try {
						line = fnc.evaluate(cursor.getLine());
					} catch (IOException ex) {
						throw ex;
					} catch (Exception ex) {
						new IOException(ex);
					}
					if (line != null) {
						writer.write(line);
						writer.write(opt.getRowSeparator());
						writer.flush();
					}
				}
			}
		} finally {
			FileHelper.close(writer);
			FileHelper.close(cursor);
		}
	}
	protected void makeDataFile(File output, File input) throws IOException {
		final CsvOption opt = this.getCsvOption();
		Writer writer = null;
		LineCursor cursor = null;
		try {
			writer = FileHelper.getWriter(output, opt.getEncoding());
			cursor = new LineCursor(input, opt);
			while (cursor.move()) {
				if (cursor.isDataLine()) {
					writer.write(cursor.getLine());
					writer.write(opt.getRowSeparator());
					writer.flush();
				}
			}
		} finally {
			FileHelper.close(writer);
			FileHelper.close(cursor);
		}
	}
}

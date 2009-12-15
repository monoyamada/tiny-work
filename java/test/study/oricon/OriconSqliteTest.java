package study.oricon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import junit.framework.TestCase;
import study.io.FileHelper;
import study.lang.Debug;
import study.lang.StringHelper;

public class OriconSqliteTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void _testCreate() throws IOException, InterruptedException {
		final File exeFile = new File(
				"E:/home/programs/sqlite/sqlite-3_6_18/sqlite3.exe");
		assertTrue(exeFile.getAbsolutePath(), exeFile.isFile());
		final File home = new File("data/tmp/oricon");
		assertTrue(home.getAbsolutePath(), home.isDirectory());
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final File dbDir = new File(home, "sqlite");
		FileHelper.ensureDirectory(dbDir);
		final File dbFile = new File(dbDir, "oricon.db");
		{
			String sql = "create table sales (" + StringHelper.join(new String[] { //
					"day integer not null" //
							, "product integer not null" //
							, "sales integer not null" //
							, "productType integer not null" //
					}, ", ") + ");";
			final ProcessBuilder runner = new ProcessBuilder(new String[] {
					exeFile.getAbsolutePath(), dbFile.getName(), sql });
			runner.directory(dbDir);
			final Process proc = runner.start();
			InputStream in = null;
			try {
				in = proc.getErrorStream();
				final byte[] buffer = new byte[1024 * 64];
				int n = in.read(buffer);
				for (; 0 <= n; n = in.read(buffer)) {
					Debug.log().info(new String(buffer, 0, n));
				}
			} finally {
				FileHelper.close(in);
			}
			Debug.log().info("created=" + proc.exitValue());
		}
		{
			final File tmpFile = makeSalesData(workspace);
			String cmd = ".import " + tmpFile.getAbsolutePath().replace('\\', '/')
					+ " sales";
			// sqlite3 -separator , work/album.db ".import data/album-sales.csv sales"
			final ProcessBuilder runner = new ProcessBuilder(new String[] {
					exeFile.getAbsolutePath(), "-separator", ",", dbFile.getName(),
					"\"" + cmd + "\"" });
			runner.directory(dbDir);
			final Process proc = runner.start();
			InputStream in = null;
			try {
				in = proc.getErrorStream();
				final byte[] buffer = new byte[1024 * 64];
				int n = in.read(buffer);
				for (; 0 <= n; n = in.read(buffer)) {
					Debug.log().info(new String(buffer, 0, n));
				}
			} finally {
				FileHelper.close(in);
			}
			Debug.log().info("created album=" + proc.exitValue());
		}
	}
	private File makeSalesData(OriconWorkspace workspace) throws IOException {
		final File csvDir = workspace.getCsvDirectory();
		final File output = workspace.getTmpFile();
		final OriconProductType[] types = { OriconProductType.ALBUM,
				OriconProductType.SINGLE };
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(output, FileHelper.UTF_8);
			for (OriconProductType type : types) {
				BufferedReader reader = null;
				try {
					final File input = new File(csvDir, type.getName() + "-sales.csv");
					reader = FileHelper.getBufferedReader(input);
					String line = reader.readLine();
					for (int iLine = 0; line != null; ++iLine, line = reader.readLine()) {
						if (iLine != 0) {
							writer.write(line);
							writer.write(",");
							writer.write(Integer.toString(type.getValue()));
							writer.write("\n");
							writer.flush();
						}
					}
				} finally {
					FileHelper.close(reader);
				}
			}
		} finally {
			FileHelper.close(writer);

		}
		return output;
	}
	public void test_makeSqliteFile() throws IOException {
		final File exeFile = new File(
				"E:/home/programs/sqlite/sqlite-3_6_18/sqlite3.exe");
		assertTrue(exeFile.getAbsolutePath(), exeFile.isFile());
		final File home = new File("data/tmp/oricon");
		assertTrue(home.getAbsolutePath(), home.isDirectory());
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final OriconSqlite builder = new OriconSqlite(workspace, exeFile);
		builder.makeSqliteFile();
	}
}

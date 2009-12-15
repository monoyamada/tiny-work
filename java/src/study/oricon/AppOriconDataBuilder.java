package study.oricon;

import static study.oricon.OriconProductType.ALBUM;
import static study.oricon.OriconProductType.SINGLE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Calendar;

import study.lang.Debug;
import study.lang.Messages;

public class AppOriconDataBuilder {
	/**
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException,
			URISyntaxException, IOException {
		File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final Calendar today = Calendar.getInstance();
		final int year = 1988;
		if(false){
			importData(year, 2000, new OriconDataBuilder(workspace, ALBUM));
			importData(year, 2000, new OriconDataBuilder(workspace, SINGLE));
			return;
		}
		final int thisYear = today.get(Calendar.YEAR);
		importData(year, thisYear, new OriconDataBuilder(workspace, ALBUM));
		importData(year, thisYear, new OriconDataBuilder(workspace, SINGLE));
	}
	protected static void importData(int firstYear, int lastYear,
			OriconDataBuilder builder) throws MalformedURLException,
			URISyntaxException, IOException {
		if (true) {
			final String name = builder.getSalesDataFileName()
					+ OriconConstant.CSV_FILE_EXTENSION;
			final File file = new File(builder.getCsvDirectory(), name);
			file.delete();
		}
		for (int iYear = firstYear; iYear <= lastYear; ++iYear) {
			Debug.log().info(
					Messages.getOperating("importing "
							+ builder.getProductType().getName() + " " + iYear));
			builder.importData(iYear);
			Debug.log().info(
					Messages.getSuccessedOperation("import "
							+ builder.getProductType().getName() + " " + iYear));
		}
	}
}

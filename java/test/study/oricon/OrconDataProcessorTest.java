package study.oricon;

import static study.oricon.OriconProductType.ALBUM;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import junit.framework.TestCase;
import study.lang.Debug;

import static study.oricon.OriconDateHelper.*;
import static study.lang.Debug.*;

public class OrconDataProcessorTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_() {
		Calendar date = newCalendar();
		{
			dayToSystemTime(date, 0);
			Debug.log().info("0" + "=" + format(date));
			int day = systemTimeToDay(0);
			dayToSystemTime(date, day);
			Debug.log().info(day + "=" + format(date));
		}
		{
			int first = getFirstDay(2000);
			dayToSystemTime(date, first);
			assertEquals(2000, date.get(Calendar.YEAR));
			assertEquals(1, date.get(Calendar.MONTH) + 1);
			assertEquals(1, date.get(Calendar.DAY_OF_MONTH));
			assertEquals(Calendar.SATURDAY, date.get(Calendar.DAY_OF_WEEK));
		}
		{
			int sun = getFirstSunday(2000);
			dayToSystemTime(date, sun);
			assertEquals(2, date.get(Calendar.DAY_OF_MONTH));
		}
		{
			int sun = getFirstSunday(2001);
			dayToSystemTime(date, sun);
			assertEquals(7, date.get(Calendar.DAY_OF_MONTH));
		}
		{
			int sun = getFirstSunday(2002);
			dayToSystemTime(date, sun);
			assertEquals(6, date.get(Calendar.DAY_OF_MONTH));
		}
		{
			//10958
			dayToSystemTime(date, 10958);
			log().info(format(date));
		}
	}
	public void test_splitDataByYear() throws IOException {
		final File home = new File("data/tmp/oricon");
		final OriconWorkspace workspace = new OriconWorkspace(home);
		final OrconDataProcessor album = new OrconDataProcessor(workspace, ALBUM);
		// final OrconDataProcessor single = new OrconDataProcessor(workspace,
		// SINGLE);
		album.splitDataByYear();
		// single.importData(2000);
	}
}

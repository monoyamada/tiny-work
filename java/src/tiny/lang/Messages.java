package tiny.lang;

import java.io.File;

public class Messages {
	public static String getTuple(String open, String close, long... x) {
		open = ObjectHelper.avoidNull(open, "");
		close = ObjectHelper.avoidNull(close, "");
		return open + StringHelper.join(x) + close;
	}
	public static String getTuple(String open, String close, Object... x) {
		open = ObjectHelper.avoidNull(open, "");
		close = ObjectHelper.avoidNull(close, "");
		return open + StringHelper.join(x) + close;
	}
	public static String getIndexOutOfRange(int begin, int index, int end) {
		return "index out of range "
				+ Messages.getTuple("[", ")", begin, index, end);
	}
	public static String getUnexpectedValue(String what, Object expected,
			Object actual) {
		what = what == null ? "situation" : what;
		return "unexpected " + what + " was occured, expected=" + expected
				+ " but actual=" + actual;
	}
	public static String getNull(String what) {
		what = what == null ? "situation" : what;
		return "unexpected null " + what + " was occured";
	}
	public static String getUnSupportedMethod(Class<?> clazz, String methodName) {
		return "unsupported method was invoked, method="
				+ (clazz != null ? clazz.getCanonicalName() : null) + "#" + methodName;
	}
	public static String getUnexpectedLine(File file, int iLine, String line) {
		final String name = file != null ? file.getName() : null;
		return Messages.getUnexpectedLine(name, iLine, line);
	}
	public static String getUnexpectedLine(String path, int iLine, String line) {
		return "error [" + path + ":" + iLine + "] " + line;
	}
	public static String getSuccessedOperation(String what) {
		return "done to " + what;
	}
	public static String getFailedOperation(String what) {
		return "failed to " + what;
	}
	public static String getOperating(String what) {
		return what + " ...";
	}
}

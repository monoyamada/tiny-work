package study.lang;

public class Messages {
	public static String getTuple(String open, String close, long... x) {
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
	public static String getUnSupportedMethod(Class<?> clazz, String methodName) {
		return "unsupported method was invoked, method="
				+ (clazz != null ? clazz.getCanonicalName() : null) + "#" + methodName;
	}
}

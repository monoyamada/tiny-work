package study.lang;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;

public class Debug {
	private static Log logger;

	public static boolean setLogLevel(String level) {
		Log log = Debug.log();
		if (log instanceof Log4JLogger) {
			String beanName = "org.apache.log4j.Logger";
			Method method = Debug.getStaticMethod(beanName, "getLogger",
					new Class[] { Class.class });
			if (method == null) {
				return false;
			}
			Object logger = null;
			try {
				logger = method.invoke(null, new Object[] { Debug.class });
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			beanName = "org.apache.log4j.Level";
			method = Debug.getStaticMethod(beanName, "toLevel",
					new Class[] { String.class });
			if (method == null) {
				return false;
			}
			Object value = null;
			try {
				value = method.invoke(null, new Object[] { level });
			} catch (Exception ex) {
				return false;
			}
			try {
				BeanHelper.setProperty(logger, "level", value);
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}
	protected static Method getStaticMethod(String className, String methodName,
			Class<?>[] params) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> beanClass;
		try {
			beanClass = loader.loadClass(className);
		} catch (Exception ex) {
			return null;
		}
		try {
			return beanClass.getMethod(methodName, params);
		} catch (Exception ex) {
			return null;
		}
	}
	public static boolean isDebugEnabled() {
		final Log logger = Debug.getLogger();
		return logger.isDebugEnabled();
	}
	public static boolean isDebug() {
		return Debug.isDebugEnabled();
	}
	public static Log getLogger() {
		if (Debug.logger == null) {
			Debug.logger = LogFactory.getLog(Debug.class);
		}
		return Debug.logger;
	}
	public static Log log() {
		return Debug.getLogger();
	}
	public static void isNotNull(Object value) {
		Debug.isNotNull("parameter", value);
	}
	public static void isNotNull(String what, Object value) {
		if (value == null) {
			String msg = Messages.getUnexpectedValue(what, "not null", null);
			throw new NullPointerException(msg);
		}
	}
	public static void isTrue(boolean value) {
		Debug.isTrue("parameter", value);
	}
	public static void isTrue(String what, Object value) {
		if (value == null) {
			String msg = Messages.getUnexpectedValue(what, "is true", null);
			throw new NullPointerException(msg);
		}
	}
}

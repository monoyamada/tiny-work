/**
 *
 */
package study.lang;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * helper class.
 *
 * @author H. Shirakata / SOARS Project
 *
 */
public class BeanHelper {
	public static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTOR_ARRAY = {};
	protected static String getNoSuchPropertyMessage(String name, Class<?> clazz) {
		return "could not find poperty="+name+" of class="+(clazz!=null?clazz.getCanonicalName():null);
	}
	protected static String getUnsupportedPropertyMessage(String name,
			Class<?> clazz) {
		return "not supported poperty="+name+" of class="+(clazz!=null?clazz.getCanonicalName():null);
	}
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz)
			throws IntrospectionException {
		final BeanInfo info = Introspector.getBeanInfo(clazz);
		return info.getPropertyDescriptors();
	}
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz,
			String name) throws IntrospectionException {
		final PropertyDescriptor[] array = BeanHelper.getPropertyDescriptors(clazz);
		for (int i = 0, n = array.length; i < n; ++i) {
			if (array[i].getName().equals(name)) {
				return array[i];
			}
		}
		return null;
	}
	public static Object getProperty(Object bean, String name)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, IntrospectionException {
		Debug.isNotNull("bean", bean);
		final Class<?> clazz = bean.getClass();
		final PropertyDescriptor pd = BeanHelper.getPropertyDescriptor(clazz, name);
		if (pd == null) {
			final String msg = BeanHelper.getNoSuchPropertyMessage(name, clazz);
			throw new NoSuchMethodException(msg);
		} else if (pd instanceof IndexedPropertyDescriptor) {
			final	String msg = BeanHelper.getUnsupportedPropertyMessage(name,clazz);
			throw new IllegalArgumentException(msg);
		}
		Method getter = pd.getReadMethod();
		if (getter == null) {
			final String msg = BeanHelper.getNoSuchPropertyMessage(name, clazz);
			throw new NoSuchMethodException(msg);
		}
		return getter.invoke(bean, (Object[]) null);
	}
	public static void setProperty(Object bean, String name, Object value)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, IntrospectionException {
		Debug.isNotNull("bean", bean);
		final Class<?> clazz = bean.getClass();
		final PropertyDescriptor pd = BeanHelper.getPropertyDescriptor(clazz, name);
		if (pd == null) {
			final String msg = BeanHelper.getNoSuchPropertyMessage(name, clazz);
			throw new NoSuchMethodException(msg);
		} else if (pd instanceof IndexedPropertyDescriptor) {
			String msg = "can not handle IndexedPropertyDescriptor";
			throw new IllegalArgumentException(msg);
		}
		Method mt = pd.getWriteMethod();
		if (mt == null) {
			final String msg = BeanHelper.getNoSuchPropertyMessage(name, clazz);
			throw new NoSuchMethodException(msg);
		}
		mt.invoke(bean, new Object[] { value });
	}
	/**
	 * @param bean
	 * @param properties
	 * @return Map
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 */
	public static Map<String, Object> getPropertyMap(Object bean,
			String[] properties) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IntrospectionException {
		properties = properties != null ? properties
				: ArrayHelper.EMPTY_STRING_ARRAY;
		final Class<?> clazz = bean.getClass();
		Map<String, Object> map = null;
		for (int i = 0, n = properties.length; i < n; ++i) {
			final String name = properties[i];
			final PropertyDescriptor pd = BeanHelper.getPropertyDescriptor(clazz,
					name);
			if (pd == null) {
				String msg = "could not get property=" + name;
				throw new NoSuchMethodException(msg);
			}
			final Method method = pd.getReadMethod();
			if (method == null) {
				String msg = "could not get getter=" + name;
				throw new NoSuchMethodException(msg);
			}
			final Object value = method.invoke(bean, (Object[]) null);
			if (map == null) {
				map = new TreeMap<String, Object>();
			}
			map.put(name, value);
		}
		if (map == null) {
			return Collections.emptyMap();
		}
		return map;
	}
	public static Map<String, Object> getPropertyMap(Object bean)
			throws IntrospectionException {
		final Class<?> clazz = bean.getClass();
		final PropertyDescriptor[] array = BeanHelper.getPropertyDescriptors(clazz);
		Map<String, Object> map = null;
		for (int i = 0, n = array.length; i < n; ++i) {
			PropertyDescriptor pd = array[i];
			Method method = pd.getReadMethod();
			if (method == null) {
				continue;
			}
			try {
				Object value = method.invoke(bean, (Object[]) null);
				if (map == null) {
					map = new TreeMap<String, Object>();
				}
				map.put(pd.getName(), value);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (map == null) {
			return Collections.emptyMap();
		}
		return map;
	}
}

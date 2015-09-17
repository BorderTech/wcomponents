package com.github.bordertech.wcomponents.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides helpers for invoking objects using the java.lang.reflect package to invoke objects.
 *
 * @author Francis Naoum
 * @since 1.0.0
 */
public final class ReflectionUtil {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private ReflectionUtil() {
	}

	/**
	 * Invoke the <code>public</code> method on the given object.
	 *
	 * @param obj is the Object to invoke
	 * @param methodName is the name of the method to invoke
	 * @param params are the param to pass to the method
	 * @param paramTypes are the types of parameters to be passed.
	 * @return the result of the method call.
	 */
	public static Object invokeMethod(final Object obj, final String methodName,
			final Object[] params, final Class[] paramTypes) {
		Object res = null;

		try {
			Class cls = obj.getClass();
			Method method = cls.getMethod(methodName, paramTypes);
			res = method.invoke(obj, params);
		} catch (SecurityException e) {
			throw new SystemException(e);
		} catch (NoSuchMethodException e) {
			throw new SystemException(e);
		} catch (IllegalAccessException e) {
			throw new SystemException(e);
		} catch (InvocationTargetException e) {
			throw new SystemException(e);
		}

		return res;
	}

	/**
	 * Invoke a <code>public</code> static method on the given class.
	 *
	 * @param cls is the class where the static method lives.
	 * @param staticMethod is the name of the method to invoke
	 * @param params are the param to pass to the method
	 * @param paramTypes are the types of parameters to be passed.
	 * @return the result of the method call.
	 */
	public static Object invokeStaticMethod(final Class cls, final String staticMethod,
			final Object[] params, final Class[] paramTypes) {
		Object res = null;

		try {
			Method method = cls.getMethod(staticMethod, paramTypes);
			res = method.invoke(null, params);
		} catch (SecurityException e) {
			throw new SystemException(e);
		} catch (NoSuchMethodException e) {
			throw new SystemException(e);
		} catch (IllegalArgumentException e) {
			throw new SystemException(e);
		} catch (IllegalAccessException e) {
			throw new SystemException(e);
		} catch (InvocationTargetException e) {
			throw new SystemException(e);
		}

		return res;
	}

	/**
	 * Retrieves all the fields contained in the given object and its superclasses.
	 *
	 * @param obj the object to examine
	 * @param excludeStatic if true, static fields will be omitted
	 * @param excludeTransient if true, transient fields will be omitted
	 * @return a list of fields for the given object
	 */
	public static List getAllFields(final Object obj, final boolean excludeStatic,
			final boolean excludeTransient) {
		List fieldList = new ArrayList();

		for (Class clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			Field[] declaredFields = clazz.getDeclaredFields();

			for (int i = 0; i < declaredFields.length; i++) {
				int mods = declaredFields[i].getModifiers();

				if ((!excludeStatic || !Modifier.isStatic(mods))
						&& (!excludeTransient || !Modifier.isTransient(mods))) {
					declaredFields[i].setAccessible(true);
					fieldList.add(declaredFields[i]);
				}
			}
		}

		return fieldList;
	}

	/**
	 * This method sets a property on an object via reflection.
	 *
	 * @param object The object on which the property is to be set.
	 * @param property The name of the property to be set.
	 * @param propertyType The type of the property being set.
	 * @param value The value of the property being set.
	 */
	public static void setProperty(final Object object, final String property,
			final Class propertyType, final Object value) {
		Class[] paramTypes = new Class[]{propertyType};
		Object[] params = new Object[]{value};
		String methodName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);

		ReflectionUtil.invokeMethod(object, methodName, params, paramTypes);
	}

	/**
	 * This method gets a property from an object via reflection.
	 *
	 * @param object The object from which the property is to be retrieved.
	 * @param property The name of the property to be retrieved.
	 *
	 * @return The value of the specified <em>property</em>.
	 */
	public static Object getProperty(final Object object, final String property) {
		Class[] paramTypes = new Class[]{};
		Object[] params = new Object[]{};
		String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);

		return ReflectionUtil.invokeMethod(object, methodName, params, paramTypes);
	}
}

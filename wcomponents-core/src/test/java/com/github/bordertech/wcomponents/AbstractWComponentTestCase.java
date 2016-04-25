package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Duplet;
import com.github.bordertech.wcomponents.util.ReflectionUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;

/**
 * This class includes features useful for the testing of WComponents.
 *
 * @author Ming Gao
 * @author Martin Shevchenko
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractWComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractWComponentTestCase.class);

	/**
	 * Creates a UI Context.
	 *
	 * @return a new UIContext.
	 */
	protected UIContext createUIContext() {
		UIContext uic = new UIContextImpl();
		return uic;
	}

	/**
	 * Sets the given context to be the active one.
	 *
	 * @param uic the context to set as active.
	 */
	protected void setActiveContext(final UIContext uic) {
		resetContext();
		UIContextHolder.pushContext(uic);
	}

	/**
	 * Resets the UIContext stack after each test method.
	 */
	@After
	public void resetContext() {
		UIContextHolder.reset();
	}

	/**
	 * This method will test that the getter/setter methods on a component are returning the correct values in its (i)
	 * initial state (ii) default state and (iii) user context.
	 * <p>
	 * Note that the component will be left in a dirty state after this method is invoked and the UIContext will be
	 * reset.
	 * </p>
	 *
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param initValue the initial value expected from the component
	 * @param defaultValue the default value to be used on the shared model
	 * @param userContextValue the value to be used with a user context
	 */
	protected void assertAccessorsCorrect(final WComponent component, final String method,
			final Object initValue,
			final Object defaultValue, final Object userContextValue) {

		assertAccessorsCorrect(component, method, initValue, defaultValue, userContextValue, null);
	}

	/**
	 * This method will test that the getter/setter methods on a component are returning the correct values in its (i)
	 * initial state (ii) default state and (iii) user context.
	 * <p>
	 * If a setter method on the component has a variable argument, then an array of the variable argument type needs to
	 * be provided via the setterArgs parameter.
	 * </p>
	 * <p>
	 * For example, if the variable argument is a type {@link Serializable} then the setterArgs parameter would be set
	 * as new Serializable[]{}. Values can also be passed in via the array.
	 * </p>
	 * <p>
	 * Note that the component will be left in a dirty state after this method is invoked and the UIContext will be
	 * reset.
	 * </p>
	 *
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param initValue the initial value expected from the component
	 * @param defaultValue the default value to be used on the shared model
	 * @param userContextValue the value to be used with a user context
	 * @param setterArgs array matching the variable argument type
	 */
	protected void assertAccessorsCorrect(final WComponent component, final String method,
			final Object initValue,
			final Object defaultValue, final Object userContextValue, final Object[] setterArgs) {
		try {
			// Check initial value
			Object getvalue = invokeGetMethod(component, method);
			checkValue(method, "Initial value.", initValue, getvalue);

			// Set default value
			invokeSetMethod(component, method, defaultValue, setterArgs);

			// Check default value set correctly
			getvalue = invokeGetMethod(component, method);
			checkValue(method, "Default value.", defaultValue, getvalue);

			// The component passed in might be a child component so find the top component to lock
			WebUtilities.getTop(component).setLocked(true);

			// Create a user context
			setActiveContext(createUIContext());

			// Check default value returned for user context
			getvalue = invokeGetMethod(component, method);
			checkValue(method, "User default value.", defaultValue, getvalue);

			// Set user value
			invokeSetMethod(component, method, userContextValue, setterArgs);

			// Check user value
			getvalue = invokeGetMethod(component, method);
			checkValue(method, "User value.", userContextValue, getvalue);

			// Reset the context
			resetContext();

			// Check default value still correct
			getvalue = invokeGetMethod(component, method);
			checkValue(method, "Reset.", defaultValue, getvalue);
		} finally {
			resetContext();
		}
	}

	/**
	 * @param component the component to invoke the getter method on
	 * @param methodName the name of the method
	 * @return the value returned by the getter method
	 */
	private Object invokeGetMethod(final WComponent component, final String methodName) {
		// Try property utils first
		try {
			return PropertyUtils.getProperty(component, methodName);
		} catch (Exception e) {
			throw new SystemException(
					"Failed to get value on component for method " + methodName + " on "
					+ component.getClass(), e);
		}

	}

	/**
	 * @param component the component to invoke the setter method on
	 * @param methodName the name of the method
	 * @param value the value to pass into the setter method
	 * @param args if required the variable args
	 */
	private void invokeSetMethod(final WComponent component, final String methodName,
			final Object value, final Object[] args) {
		try {
			if (args == null) {
				PropertyUtils.setProperty(component, methodName, value);
			} else {
				// Invoke specifying the variable arg type as propertyUtils cannot handle this
				String setter = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
				Class[] argTypes = new Class[]{value.getClass(), args.getClass()};
				Method method = component.getClass().getMethod(setter, argTypes);
				method.invoke(component, value, args);
			}
		} catch (Exception e) {
			throw new SystemException(
					"Failed to set value on component for method " + methodName + " on "
					+ component.getClass(), e);
		}
	}

	/**
	 * Times the given runnable, using the best available "guess" for the CPU time.
	 *
	 * @param runnable the runnable to run.
	 * @return an approximation of the CPU time taken, in nanoseconds.
	 */
	protected long time(final Runnable runnable) {
		final long[] result = new long[1];
		final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
		final boolean cpuTimeSupported = threadMxBean.isCurrentThreadCpuTimeSupported();

		Thread runThread = new Thread() {
			@Override
			public void run() {
				if (cpuTimeSupported) {
					threadMxBean.setThreadCpuTimeEnabled(true);
					runnable.run();
					result[0] = threadMxBean.getCurrentThreadCpuTime();
				} else {
					LOG.warn("Thread CPU time not supported, result may be inaccurate.");
					long start = System.currentTimeMillis();
					runnable.run();
					long end = System.currentTimeMillis();
					result[0] = (end - start) * 1000000; // convert millis to nanos
				}
			}
		};

		try {
			runThread.start();
			runThread.join();
		} catch (Exception e) {
			LOG.error("Failed to run runnable", e);
			Assert.fail(e.toString());
		}

		return result[0];
	}

	/**
	 * Modifies the component's flags. This is necessary for testing as some of the setter methods are intentionally not
	 * visible in the public API.
	 *
	 * @param component the component to set the modify the flag for.
	 * @param mask the flags to set/clear.
	 * @param flag flag value
	 */
	protected void setFlag(final AbstractWComponent component, final int mask, final boolean flag) {
		ComponentModel model = component.getOrCreateComponentModel();
		int flags = model.getFlags();
		int newFlags = flag ? (flags | mask) : (flags & ~mask);
		model.setFlags(newFlags);
	}

	/**
	 * @param method the method name
	 * @param prefix the test description
	 * @param expected the expected value
	 * @param actual the actual value
	 */
	private void checkValue(final String method, final String prefix, final Object expected,
			final Object actual) {
		if (expected instanceof Object[]) {
			Assert.assertTrue("(Array) Incorrect value for method " + method + " on " + prefix,
					Arrays.equals((Object[]) expected, (Object[]) actual));
		} else if (expected instanceof int[]) {
			Assert.assertTrue("(Int Array) Incorrect value for method " + method + " on " + prefix,
					Arrays.equals((int[]) expected, (int[]) actual));
		} else {
			Assert.assertEquals("Incorrect value for method " + method + " on " + prefix, expected,
					actual);
		}
	}

	/**
	 * This method will test that the methods used for a map property.
	 * <p>
	 * Assumes the following methods for "name":-
	 * <ul>
	 * <li>addName(key, value)</li>
	 * <li>removeName(key)</li>
	 * <li>removeName(value) - Optional</li>
	 * <li>removeAllNames()</li>
	 * <li>getNames()</li>
	 * </ul>
	 * </p>
	 *
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param keyClass the map key class
	 * @param valueClass the map value class
	 * @param defaultItem the default item to be added
	 * @param userContextItem the item to be added with a user context
	 * @param removeByValue test remove by value method
	 */
	protected void assertMapAccessorsCorrect(final WComponent component, final String method, final Class keyClass, final Class valueClass,
			final Duplet defaultItem, final Duplet userContextItem, final boolean removeByValue) {
		try {

			final Map initValue = Collections.EMPTY_MAP;
			final Map defaultValue = new HashMap();
			defaultValue.put(defaultItem.getFirst(), defaultItem.getSecond());
			final Map userContextValue = new HashMap();
			userContextValue.put(defaultItem.getFirst(), defaultItem.getSecond());
			userContextValue.put(userContextItem.getFirst(), userContextItem.getSecond());

			// Check initial value
			Map getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "Initial value.", initValue, getvalue);

			// Add default value
			invokeMapAddMethod(component, method, keyClass, valueClass, defaultItem);

			// Check default add value set correctly
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "Default value after add.", defaultValue, getvalue);

			// The component passed in might be a child component so find the top component to lock
			WebUtilities.getTop(component).setLocked(true);

			// Create a user context
			setActiveContext(createUIContext());

			// Check default value returned for user context
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "User default value.", defaultValue, getvalue);

			// Add user value
			invokeMapAddMethod(component, method, keyClass, valueClass, userContextItem);
			// Check user value
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "User value after add.", userContextValue, getvalue);

			// Reset the context
			resetContext();
			// Check default value still correct
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "Reset after add method.", defaultValue, getvalue);

			//--------
			// Test Remove By key
			// Create a user context
			setActiveContext(createUIContext());
			// Remove default value by Key
			invokeMapRemoveMethodByKey(component, method, keyClass, defaultItem);
			// Should be empty
			getvalue = invokeMapGetMethod(component, method);
			Assert.assertTrue("Map should be empty after remove item by key", getvalue.isEmpty());
			// Reset the context
			resetContext();
			// Check default value still correct
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "Reset after remove by key.", defaultValue, getvalue);

			//--------
			// Test Remove By Value
			if (removeByValue) {
				// Create a user context
				setActiveContext(createUIContext());
				// Remove default value by Value
				invokeMapRemoveMethodByValue(component, method, valueClass, defaultItem);
				// Should be empty
				getvalue = invokeMapGetMethod(component, method);
				Assert.assertTrue("Map should be empty after remove item by value", getvalue.isEmpty());
				// Reset the context
				resetContext();
				// Check default value still correct
				getvalue = invokeMapGetMethod(component, method);
				checkValue(method, "Reset after remove by value.", defaultValue, getvalue);
			}

			// Test remove all
			// Create a user context
			setActiveContext(createUIContext());
			// Remove all
			invokeMapRemoveAllMethod(component, method);
			// Should be empty
			getvalue = invokeMapGetMethod(component, method);
			Assert.assertTrue("Map should be empty after remove all", getvalue.isEmpty());
			// Reset the context
			resetContext();
			// Check default value still correct
			getvalue = invokeMapGetMethod(component, method);
			checkValue(method, "Reset after remove all.", defaultValue, getvalue);

		} finally {
			resetContext();
		}
	}

	/**
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param keyClass the map key class
	 * @param valueClass the map value class
	 * @param item the item to add
	 */
	private void invokeMapAddMethod(final Object component, final String method, final Class keyClass, final Class valueClass, final Duplet<?, ?> item) {
		String methodName = "add" + method.substring(0, 1).toUpperCase() + method.substring(1);
		Class[] paramTypes = new Class[]{keyClass, valueClass};
		Object[] params = new Object[]{item.getFirst(), item.getSecond()};
		ReflectionUtil.invokeMethod(component, methodName, params, paramTypes);
	}

	/**
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param keyClass the map key class
	 * @param item the item to remove
	 */
	private void invokeMapRemoveMethodByKey(final Object component, final String method, final Class keyClass, final Duplet<?, ?> item) {
		String methodName = "remove" + method.substring(0, 1).toUpperCase() + method.substring(1);
		Class[] paramTypes = new Class[]{keyClass};
		Object[] params = new Object[]{item.getFirst()};

		ReflectionUtil.invokeMethod(component, methodName, params, paramTypes);
	}

	/**
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @param valueClass the map value class
	 * @param item the item to remove
	 */
	private void invokeMapRemoveMethodByValue(final Object component, final String method, final Class valueClass, final Duplet<?, ?> item) {
		String methodName = "remove" + method.substring(0, 1).toUpperCase() + method.substring(1);
		Class[] paramTypes = new Class[]{valueClass};
		Object[] params = new Object[]{item.getSecond()};
		ReflectionUtil.invokeMethod(component, methodName, params, paramTypes);
	}

	/**
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 */
	private void invokeMapRemoveAllMethod(final Object component, final String method) {
		String methodName = "removeAll" + method.substring(0, 1).toUpperCase() + method.substring(1) + "s";
		Class[] paramTypes = new Class[]{};
		Object[] params = new Object[]{};
		ReflectionUtil.invokeMethod(component, methodName, params, paramTypes);
	}

	/**
	 * @param component the component to test the accessors on
	 * @param method the method to test
	 * @return the map or null
	 */
	private Map invokeMapGetMethod(final Object component, final String method) {
		String methodName = "get" + method.substring(0, 1).toUpperCase() + method.substring(1) + "s";
		Class[] paramTypes = new Class[]{};
		Object[] params = new Object[]{};
		return (Map) ReflectionUtil.invokeMethod(component, methodName, params, paramTypes);
	}

}

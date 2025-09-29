package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Duplet;
import com.github.bordertech.wcomponents.util.ReflectionUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;

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
	 * The number of repetitions to use for testing serialization time. This should be set to be greater than the
	 * minimum number of invocations required to trigger JIT compilation.
	 */
	public static final int NUM_REPETITIONS = 2000;

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
	 * @param <C> the type of the component being tested
	 * @param <T> the type of the property being tested
	 * @param component the component to test the accessors on
	 * @param getter the property's getter to be tested
	 * @param setter the property's setter to be tested
	 * @param initValue the initial value expected from the component
	 * @param defaultValue the default value to be used on the shared model
	 * @param userContextValue the value to be used with a user context
	 */
	protected <C extends WComponent, T> void assertAccessorsCorrect(
			final C component,
			final Function<C, T> getter,
			final BiConsumer<C, T> setter,
			final T initValue,
			final T defaultValue, final T userContextValue) {
		try {
			// Check initial value
			checkValue("", "Initial value.", initValue, getter.apply(component));

			// Set default value
			setter.accept(component, defaultValue);

			// Check default value set correctly
			checkValue("", "Default value.", defaultValue, getter.apply(component));

			// The component passed in might be a child component so find the top component to lock
			WebUtilities.getTop(component).setLocked(true);

			// Create a user context
			setActiveContext(createUIContext());

			// Check default value returned for user context
			checkValue("", "User default value.", defaultValue, getter.apply(component));

			// Set user value
			setter.accept(component, userContextValue);

			// Check user value
			checkValue("", "User value.", userContextValue, getter.apply(component));

			// Reset the context
			resetContext();

			// Check default value still correct
			checkValue("", "Reset.", defaultValue, getter.apply(component));

		} finally {
			resetContext();
		}
	}

	/**
	 * This method will test that the getter/setter methods on a component are returning the correct values in its (i)
	 * initial state (ii) default state and (iii) user context.
	 * <p>
	 * Note that the component will be left in a dirty state after this method is invoked and the UIContext will be
	 * reset.
	 * </p>
	 *
	 * @param <C> the type of the component being tested
	 * @param <T> the type of the property being tested
	 * @param <U> the type of the setter's second argument
	 * @param component the component to test the accessors on
	 * @param getter the property's getter to be tested
	 * @param setter the property's setter to be tested
	 * @param initValue the initial value expected from the component
	 * @param defaultValue the default value to be used on the shared model
	 * @param userContextValue the value to be used with a user context
	 * @param setterArgs the value of the setter's second argument
	 */
	protected <C extends WComponent, T, U> void assertAccessorsCorrect(
			final C component,
			final Function<C, T> getter,
			final TriConsumer<C, T, U> setter,
			final T initValue,
			final T defaultValue,
			final T userContextValue,
			final U setterArgs) {
		try {
			// Check initial value
			checkValue("", "Initial value.", initValue, getter.apply(component));

			// Set default value
			setter.accept(component, defaultValue, setterArgs);

			// Check default value set correctly
			checkValue("", "Default value.", defaultValue, getter.apply(component));

			// The component passed in might be a child component so find the top component to lock
			WebUtilities.getTop(component).setLocked(true);

			// Create a user context
			setActiveContext(createUIContext());

			// Check default value returned for user context
			checkValue("", "User default value.", defaultValue, getter.apply(component));

			// Set user value
			setter.accept(component, userContextValue, setterArgs);

			// Check user value
			checkValue("", "User value.", userContextValue, getter.apply(component));

			// Reset the context
			resetContext();

			// Check default value still correct
			checkValue("", "Reset.", defaultValue, getter.apply(component));

		} finally {
			resetContext();
		}
	}

	/**
	 * This method checks that the a component model uses the default model on creation of a component.
	 *
	 * @param wComponent the component to test the model
	 */
	private void assertComponentModelUsesDefaultOnCreation(AbstractWComponent wComponent) {
		ComponentModel shared = wComponent.getDefaultModel();
		wComponent.setLocked(true);
		ComponentModel model = wComponent.getComponentModel();
		org.junit.Assert.assertSame(model, shared);
	}

	/**
	 * This method checks that the a component model uses the default model when the same value is set as the same in
	 * the default model, rather than creating a new model.
	 *
	 * @param wComponent the component the model is being created from
	 * @param method the method used to change the model
	 * @param userContextValue the value to be used within the user context
	 * @param setterArgs array matching the variable argument type
	 */
	private void assertComponentModelUsesDefaultOnSameValue(AbstractWComponent wComponent, String method,
			Object userContextValue, Object setterArgs[]) {
		wComponent.setLocked(false);
		// Set default model
		invokeSetMethod(wComponent, method, userContextValue, setterArgs);
		ComponentModel shared = wComponent.getDefaultModel();
		wComponent.setLocked(true);

		// No new value has been set, so just test the getComponentModel does indeed get the default model
		ComponentModel model = wComponent.getComponentModel();
		org.junit.Assert.assertSame(model, shared);

		// Set UI context, then set the value to the same value so that we can test that a new model isn't created but
		// just uses the default model
		setActiveContext(createUIContext());
		invokeSetMethod(wComponent, method, userContextValue, setterArgs);
		model = wComponent.getComponentModel();
		org.junit.Assert.assertSame(shared, model);

		resetContext();
	}

	/**
	 * This method checks that the component model is not still using the default model after a value has been set in a
	 * user context that is different to the one in the default model.
	 *
	 * @param wComponent the component the model is being created from
	 * @param method the method used to change the model
	 * @param userContextValue the value to be used within the user context. Must be different to the value stored in
	 * the default model for this test.
	 * @param setterArgs array matching the variable argument type
	 */
	private void assertComponentModelDoesNotUseDefaultOnDifferentValue(AbstractWComponent wComponent, String method,
			Object userContextValue, Object setterArgs[]) {
		// Create a default model using whatever values are set for the wComponent
		wComponent.setLocked(true);
		ComponentModel shared = wComponent.getDefaultModel();

		// Set the UI context, set different values to the default model and test that it doesn't use the default
		setActiveContext(createUIContext());
		invokeSetMethod(wComponent, method, userContextValue, setterArgs);
		ComponentModel model = wComponent.getComponentModel();
		org.junit.Assert.assertNotEquals(model, shared);

		resetContext();
	}

	/**
	 * This method checks that a model created in a user context does not create a duplicate model when a value is set
	 * to the same value in the current model.
	 *
	 * @param wComponent the component the model is being created from
	 * @param method the method used to change the model
	 * @param userContextValue the value to be used within the user context
	 * @param setterArgs array matching the variable argument type
	 */
	private void assertDuplicateUserModelNotCreatedOnSameValue(AbstractWComponent wComponent, String method,
			Object userContextValue, Object setterArgs[]) {
		wComponent.setLocked(true);

		// Set the UI context and set a value to the current model
		setActiveContext(createUIContext());
		ComponentModel shared = wComponent.getDefaultModel();
		invokeSetMethod(wComponent, method, userContextValue, setterArgs);
		ComponentModel modelBefore = wComponent.getComponentModel();

		// Set the same value and test that the component model is the same
		invokeSetMethod(wComponent, method, userContextValue, setterArgs);
		ComponentModel modelAfter = wComponent.getComponentModel();
		org.junit.Assert.assertSame(modelBefore, modelAfter);

		// Test that the model wasn't using the default
		org.junit.Assert.assertNotEquals(modelAfter, shared);

		resetContext();
	}

	/**
	 * All-in-one method for various duplicate component model tests.
	 *
	 * @param wComponent the component the model is being created from
	 * @param method the method used to change the model
	 * @param userContextValue the value to be used within the user context
	 * @param setterArgs array matching the variable argument type
	 */
	protected void assertNoDuplicateComponentModels(AbstractWComponent wComponent, String method,
			Object userContextValue, Object[] setterArgs) {
		assertComponentModelUsesDefaultOnCreation(wComponent);
		assertComponentModelDoesNotUseDefaultOnDifferentValue(wComponent, method, userContextValue, setterArgs);
		assertDuplicateUserModelNotCreatedOnSameValue(wComponent, method, userContextValue, setterArgs);
		assertComponentModelUsesDefaultOnSameValue(wComponent, method, userContextValue, setterArgs);
	}

	/**
	 * Overloading method for
	 * {@link AbstractWComponentTestCase#assertNoDuplicateComponentModels(AbstractWComponent, String, Object, Object[])}.
	 *
	 * @param wComponent the component the model is being created from
	 * @param method the method used to change the model
	 * @param userContextValue the value to be used within the user context
	 */
	protected void assertNoDuplicateComponentModels(AbstractWComponent wComponent, String method,
			Object userContextValue) {
		assertNoDuplicateComponentModels(wComponent, method, userContextValue, null);
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
	 * Asserts that <code>first</code> is less than <code>second</code>.
	 *
	 * @param text the assertion text.
	 * @param first the first parameter to check.
	 * @param second the second parameter to check.
	 */
	protected void assertLessThan(final String text, final long first, final long second) {
		Assert.assertTrue(text + ": " + first + " < " + second, first < second);
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
	 * </p>
	 * <ul>
	 * <li>addName(key, value)</li>
	 * <li>removeName(key)</li>
	 * <li>removeName(value) - Optional</li>
	 * <li>removeAllNames()</li>
	 * <li>getNames()</li>
	 * </ul>
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

package com.github.bordertech.wcomponents.util;

import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the functionality of the {@link ReflectionUtil} class.
 *
 * @author Francis Naoum
 * @since 1.0.0
 */
public class ReflectionUtil_Test {

	/**
	 * A dummy class to test the ReflectionUtil class with.
	 *
	 * @author francis.naoum
	 */
	private static class DummyObject {

		/**
		 * Invoked void number of parameters.
		 */
		private boolean invokedVoidNoParams = false;

		/**
		 * Invoked void parameters.
		 */
		private Object invokedVoidParams = null;

		/**
		 * Static invoked void number of parameters.
		 */
		private static boolean staticInvokedVoidNoParams = false;

		/**
		 * Static invoked void parameters.
		 */
		private static Object staticInvokedVoidParams = null;

		/**
		 * Hold a property.
		 */
		private Object someProperty;

		/**
		 * @return some property
		 */
		public Object getSomeProperty() {
			return someProperty;
		}

		/**
		 * @param someProperty set some property
		 */
		public void setSomeProperty(final Object someProperty) {
			this.someProperty = someProperty;
		}

		/**
		 * A void parameter with no parameters.
		 */
		public void voidNoParams() {
			invokedVoidNoParams = true;
		}

		/**
		 * A void method, which accepts parameters.
		 *
		 * @param param a parameter
		 */
		public void voidParams(final Object param) {
			invokedVoidParams = param;
		}

		/**
		 * A non void method, with no parameters.
		 *
		 * @return an object
		 */
		public Object nonvoidNoParams() {
			return new Object();
		}

		/**
		 * A non void method, with parameters.
		 *
		 * @param param a parameter
		 * @return an object
		 */
		public Object nonvoidParams(final Object param) {
			return param;
		}

		/**
		 * A private method.
		 *
		 * @return an object
		 */
		private Object privateMethod() {
			return new Object();
		}

		/**
		 * An exception throwing static method.
		 *
		 * @return nothing but throw an exception.
		 */
		public Object exceptionThrower() {
			throw new UnsupportedOperationException();
		}

		/**
		 * A void static method, with void params.
		 *
		 */
		public static void staticVoidNoParams() {
			staticInvokedVoidNoParams = true;
		}

		/**
		 * A void static method, which accepts parameters.
		 *
		 * @param param a parameter
		 */
		public static void staticVoidParams(final Object param) {
			staticInvokedVoidParams = param;
		}

		/**
		 * A non void static method, with no parameters.
		 *
		 * @return an object
		 */
		public static Object staticNonvoidNoParams() {
			return new Object();
		}

		/**
		 * A non void static method which accepts params.
		 *
		 * @param param a parameter
		 * @return the parameter
		 */
		public static Object staticNonvoidParams(final Object param) {
			return param;
		}

		/**
		 * A private static method.
		 *
		 * @return an object
		 */
		private static Object staticPrivateMethod() {
			return new Object();
		}

		/**
		 * An exception throwing method.
		 *
		 * @return nothing but throw an exception
		 */
		public static Object staticExceptionThrower() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Test the void void method which takes no parameters.
	 */
	@Test
	public void testVoidVoid() {
		DummyObject dummyObj = new DummyObject();
		Object returned = ReflectionUtil.invokeMethod(dummyObj, "voidNoParams", null, null);

		Assert.assertNull("voidNoParams should not return anything", returned);
		Assert.assertTrue("voidNoParams was not invoked", dummyObj.invokedVoidNoParams);
	}

	/**
	 * Test the void void method by accidently giving parameters.
	 */
	@Test
	public void testVoidVoidInvalidParams() {
		DummyObject dummyObj = new DummyObject();

		try {
			ReflectionUtil.invokeMethod(dummyObj, "voidNoParams",
					new Object[]{new Object()}, new Class[]{Object.class});

			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test the void params method.
	 */
	@Test
	public void testVoidParams() {
		DummyObject dummyObj = new DummyObject();
		Object param = new Object();
		Object returned = ReflectionUtil.invokeMethod(dummyObj, "voidParams",
				new Object[]{param}, new Class[]{Object.class});

		Assert.assertNull("voidParams should not return anything", returned);
		Assert.assertEquals("voidParams was not invoked", param, dummyObj.invokedVoidParams);
	}

	/**
	 * Test the non void with no params method.
	 */
	@Test
	public void testNonvoidNoParams() {
		DummyObject dummyObj = new DummyObject();
		Object returned = ReflectionUtil.invokeMethod(dummyObj, "nonvoidNoParams",
				null, null);

		Assert.assertNotNull("nonvoidNoParams should have returned a value", returned);
	}

	/**
	 * Test the non void with params method.
	 */
	@Test
	public void testNonvoidParams() {
		DummyObject dummyObj = new DummyObject();
		Object param = new Object();
		Object returned = ReflectionUtil.invokeMethod(dummyObj, "nonvoidParams",
				new Object[]{param}, new Class[]{Object.class});

		Assert.assertNotNull("nonvoidParams should have returned a value", returned);

		// make sure the returned is the same as the param we gave
		Assert.assertEquals("Incorrect returned value for nonvoidParams", param, returned);
	}

	/**
	 * Test when an invalid name is provided.
	 */
	@Test
	public void testInvalidMethodName() {
		DummyObject dummyObj = new DummyObject();

		try {
			ReflectionUtil.invokeMethod(dummyObj, "doesntExist", null, null);
			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test when the method invoked is private.
	 */
	@Test
	public void testInvokePrivate() {
		DummyObject dummyObj = new DummyObject();

		try {
			ReflectionUtil.invokeMethod(dummyObj, "privateMethod", null, null);
			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test when the method invoked throws an exception.
	 */
	@Test
	public void testInvokeExceptionThrower() {
		DummyObject dummyObj = new DummyObject();

		try {
			ReflectionUtil.invokeMethod(dummyObj, "exceptionThrower", null, null);
			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					InvocationTargetException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test the void static method which takes no parameters.
	 */
	@Test
	public void testStaticVoidVoid() {
		Object returned = ReflectionUtil.invokeStaticMethod(DummyObject.class,
				"staticVoidNoParams", null, null);

		Assert.assertNull("staticVoidNoParams should not return a value", returned);
		Assert.assertTrue("staticVoidNoParams was not invoked",
				DummyObject.staticInvokedVoidNoParams);
	}

	/**
	 * Test the void void static method by accidently giving parameters.
	 */
	@Test
	public void testStaticVoidVoidInvalidParams() {
		try {
			ReflectionUtil.invokeStaticMethod(
					DummyObject.class, "staticVoidNoParams",
					new Object[]{new Object()}, new Class[]{Object.class});

			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test the void params static method.
	 */
	@Test
	public void testStaticVoidParams() {
		Object param = new Object();
		Object returned = ReflectionUtil.invokeStaticMethod(DummyObject.class,
				"staticVoidParams", new Object[]{param},
				new Class[]{Object.class});

		Assert.assertNull("staticVoidParams should not return a value", returned);
		Assert.assertEquals("staticVoidParams was not invoked", param,
				DummyObject.staticInvokedVoidParams);
	}

	/**
	 * Test the non void with no params static method.
	 */
	@Test
	public void testStaticNonvoidNoParams() {
		Object returned = ReflectionUtil.invokeStaticMethod(DummyObject.class,
				"staticNonvoidNoParams", null, null);

		Assert.assertNotNull("staticNonvoidNoParams should have returned a value", returned);
	}

	/**
	 * Test the non void with params static method.
	 */
	@Test
	public void testStaticNonvoidParams() {
		Object param = new Object();
		Object returned = ReflectionUtil.invokeStaticMethod(DummyObject.class,
				"staticNonvoidParams", new Object[]{param},
				new Class[]{Object.class});

		Assert.assertNotNull("staticNonvoidParams should have returned a value", returned);

		// make sure the returned is the same as the param we gave
		Assert.assertEquals("Incorrect return value for staticNonvoidParams", param, returned);
	}

	/**
	 * Test when an invalid name is provided.
	 */
	@Test
	public void testStaticInvalidMethodName() {
		try {
			ReflectionUtil.invokeStaticMethod(DummyObject.class, "doesntExist",
					null, null);

			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test when the static method invoked is private.
	 */
	@Test
	public void testStaticInvokePrivate() {
		try {
			ReflectionUtil.invokeStaticMethod(DummyObject.class,
					"staticPrivateMethod", null, null);

			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test when the static method invoked throws an exception.
	 */
	@Test
	public void testStaticInvokeExceptionThrower() {
		try {
			ReflectionUtil.invokeStaticMethod(DummyObject.class,
					"staticExceptionThrower", null, null);

			Assert.fail("Should have thrown a SystemException");
		} catch (SystemException e) {
			Assert.assertEquals("Incorrect exception cause",
					InvocationTargetException.class, e.getCause().getClass());
		}
	}

	/**
	 * Test the setProperty static method.
	 */
	@Test
	public void testSetProperty() {
		final String value = "ReflectionUtil_Test.testSetProperty.value";
		DummyObject object = new DummyObject();
		ReflectionUtil.setProperty(object, "someProperty", Object.class, value);
		Assert.assertEquals("Incorrect property value", value, object.getSomeProperty());
	}

	/**
	 * Test the getProperty static method.
	 */
	@Test
	public void testGetProperty() {
		final String value = "ReflectionUtil_Test.testGetProperty.value";
		DummyObject object = new DummyObject();
		object.setSomeProperty(value);

		Object result = ReflectionUtil.getProperty(object, "someProperty");
		Assert.assertEquals("Incorrect property value", value, result);
	}
}

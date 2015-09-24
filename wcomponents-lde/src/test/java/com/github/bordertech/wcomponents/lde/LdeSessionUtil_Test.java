package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import junit.framework.Assert;
import org.junit.Test;

/**
 * LdeSessionUtil_Test - unit tests for {@link LdeSessionUtil}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class LdeSessionUtil_Test {

	/**
	 * Session key for a test string value.
	 */
	private static final String STRING_KEY = "LdeSessionUtil_Test.stringKey";
	/**
	 * A test string value.
	 */
	private static final String STRING_VALUE = "LdeSessionUtil_Test.stringValue";

	/**
	 * Session key for a test integer value.
	 */
	private static final String INT_KEY = "LdeSessionUtil_Test.intKey";
	/**
	 * A test integer value.
	 */
	private static final Integer INT_VALUE = new Integer(123456);

	/**
	 * Session key for a test non-serializable value.
	 */
	private static final String NON_SERIALIZABLE_KEY = "LdeSessionUtil_Test.nonSerializableKey";
	/**
	 * A test non-serializable value.
	 */
	private static final NonSerializable NON_SERIALIZABLE_VALUE = new NonSerializable();

	/**
	 * Test for serializeSessionAttributes / deserializeSessionAttributes.
	 */
	@Test
	public void testSessionSerialization() {
		MockHttpSession session1 = new MockHttpSession();
		MockHttpSession session2 = new MockHttpSession();

		session1.setAttribute(STRING_KEY, STRING_VALUE);
		session1.setAttribute(INT_KEY, INT_VALUE);
		session1.setAttribute(NON_SERIALIZABLE_KEY, NON_SERIALIZABLE_VALUE);

		LdeSessionUtil.serializeSessionAttributes(session1);
		LdeSessionUtil.deserializeSessionAttributes(session2);

		Assert.assertEquals("Incorrect number of deserialized session attribute", 2,
				session2.getAttributes().size());
		Assert.assertEquals("Incorrect value of String attribute", STRING_VALUE,
				session2.getAttribute(STRING_KEY));
		Assert.assertEquals("Incorrect value of Integer attribute", INT_VALUE,
				session2.getAttribute(INT_KEY));
		Assert.assertFalse("Session should not contain", session2.getAttributes().containsKey(
				NON_SERIALIZABLE_KEY));
	}

	/**
	 * A non-serializable class for testing.
	 */
	private static final class NonSerializable {
	}
}

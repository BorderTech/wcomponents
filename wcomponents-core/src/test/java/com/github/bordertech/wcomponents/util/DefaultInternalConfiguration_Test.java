package com.github.bordertech.wcomponents.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.ConversionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultInternalConfiguration_Test - JUnit tests for {@link DefaultInternalConfiguration}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class DefaultInternalConfiguration_Test {

	/**
	 * The configuration to test.
	 */
	private DefaultInternalConfiguration config;

	/**
	 * A value for this property should not exist.
	 */
	private static final String MISSING_PROPERTY_KEY = "simple.nonExistantProperty";

	/**
	 * Used in conjunction with MISSING_PROPERTY_KEY.
	 */
	private static final int MISSING_PROPERTY_VAL = 234;

	/**
	 * The value for this property should be an empty string.
	 */
	private static final String EMPTY_PROPERTY_KEY = "simple.emptyPropertyKey";

	/**
	 * The value for this property should be "simplePropertyValue".
	 */
	private static final String STRING_PROPERTY_KEY = "simple.stringPropertyKey";

	/**
	 * The value for this property should be INT_PROPERTY_VAL.
	 */
	private static final String INT_PROPERTY_KEY = "simple.intPropertyKey";

	/**
	 * Used in conjunction with INT_PROPERTY_KEY.
	 */
	private static final int INT_PROPERTY_VAL = 123;

	/**
	 * The value for this property should be "true".
	 */
	private static final String BOOLEAN_TRUE_PROPERTY_KEY = "simple.booleanTruePropertyKey";

	/**
	 * The value for this property should be "false".
	 */
	private static final String BOOLEAN_FALSE_PROPERTY_KEY = "simple.booleanFalsePropertyKey";

	@Before
	public void loadProperties() {
		config = new DefaultInternalConfiguration(
				"com/github/bordertech/wcomponents/util/DefaultInternalConfiguration_Test.properties");
	}

	@Test
	public void testSimpleProperties() {
		Assert.assertNull("Missing properties should be null", config.get(
				"simple.nonExistantPropertyKey"));
		Assert.assertEquals("Incorrect default value for missing property", "defaultValue", config.
				getString("simple.nonExistantPropertyKey", "defaultValue"));
		assertPropertyEquals(EMPTY_PROPERTY_KEY, "");
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue");
		assertPropertyEquals(INT_PROPERTY_KEY, "123");
		assertPropertyEquals(BOOLEAN_TRUE_PROPERTY_KEY, "true");
		assertPropertyEquals(BOOLEAN_FALSE_PROPERTY_KEY, "false");
		assertPropertyEquals("simple.listPropertyKey", "item1,item2,item3");
	}

	@Test
	public void testSubstitution() {
		assertPropertyEquals("substitute.missingKey", "${substitute.nonExistantKey}");
		assertPropertyEquals("substitute.part1And2Key", "part1Value+part2Value");
		assertPropertyEquals("substitute.part1And2And3Key", "part1Value+part2Value+part3Value");
		assertPropertyEquals("substitute.combinedKey", "multiPart1ValuemultiPart2Value");
		assertPropertyEquals("substitute.reurse", "${substitute.recurse}");
	}

	@Test
	public void testIncludes() {
		assertPropertyEquals("test.definedBeforeInclude", "includeValue");
		assertPropertyEquals("test.definedAfterInclude", "mainValue");
		assertPropertyEquals("test.definedBeforeIncludeAfter", "includeAfterValue");
		assertPropertyEquals("test.definedAfterIncludeAfter", "includeAfterValue");
	}

	@Test
	public void testGetSubProperties() {
		final int propertyCount = 7;
		// Test without the prefix truncated
		Properties props = config.getSubProperties("simple.", false);
		Assert.assertEquals("Incorrect number of properties", propertyCount, props.size());
		assertPropertyEquals(EMPTY_PROPERTY_KEY, "", props);
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue", props);
		assertPropertyEquals(INT_PROPERTY_KEY, "123", props);
		assertPropertyEquals(BOOLEAN_TRUE_PROPERTY_KEY, "true", props);
		assertPropertyEquals(BOOLEAN_FALSE_PROPERTY_KEY, "false", props);
		assertPropertyEquals("simple.listPropertyKey", "item1,item2,item3", props);
		assertPropertyEquals("simple.propertiesPropertyKey", "key1=value1,key2=value2,key3=value3",
				props);

		// Now test with the prefix truncated
		props = config.getSubProperties("simple.", true);
		Assert.assertEquals("Incorrect number of properties", propertyCount, props.size());
		assertPropertyEquals("emptyPropertyKey", "", props);
		assertPropertyEquals("stringPropertyKey", "simplePropertyValue", props);
		assertPropertyEquals("intPropertyKey", "123", props);
		assertPropertyEquals("booleanTruePropertyKey", "true", props);
		assertPropertyEquals("booleanFalsePropertyKey", "false", props);
		assertPropertyEquals("listPropertyKey", "item1,item2,item3", props);
		assertPropertyEquals("propertiesPropertyKey", "key1=value1,key2=value2,key3=value3", props);
	}

	@Test
	public void testSetProperty() {
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue");
		config.setProperty(STRING_PROPERTY_KEY, "changedValue");
		assertPropertyEquals(STRING_PROPERTY_KEY, "changedValue");
	}

	@Test
	public void testAddProperty() {
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue");
		config.addProperty(STRING_PROPERTY_KEY, "addedValue");
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue,addedValue");
	}

	@Test(expected = SystemException.class)
	public void testSetPropertyNullKey() {
		config.setProperty(null, "x");
	}

	@Test(expected = SystemException.class)
	public void testSetPropertyEmptyKey() {
		config.setProperty("", "x");
	}

	@Test(expected = SystemException.class)
	public void testSetPropertyNullValue() {
		config.setProperty("x", null);
	}

	@Test
	public void testGetLong() {
		Assert.assertEquals("Incorrect long value for " + INT_PROPERTY_KEY, INT_PROPERTY_VAL, config.getLong(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect long value for missing key", 0, config.getLong(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default long value for missing key", MISSING_PROPERTY_VAL,
				config.getLong(MISSING_PROPERTY_KEY, MISSING_PROPERTY_VAL));

		Assert.assertEquals("Incorrect default long value for missing key",
				Long.valueOf(MISSING_PROPERTY_VAL), config.getLong(MISSING_PROPERTY_KEY, Long.valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidLong() {
		config.getLong(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetInt() {
		Assert.assertEquals("Incorrect int value for " + INT_PROPERTY_KEY, INT_PROPERTY_VAL, config.getInt(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect int value for missing key", 0, config.getInt(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default int value for missing key", MISSING_PROPERTY_VAL,
				config.getInt(MISSING_PROPERTY_KEY, MISSING_PROPERTY_VAL));

		Assert.assertEquals("Incorrect default integer value for missing key",
				Integer.valueOf(MISSING_PROPERTY_VAL), config.getInteger(MISSING_PROPERTY_KEY, Integer.valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidInt() {
		config.getInt(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetShort() {
		Assert.assertEquals("Incorrect short value for " + INT_PROPERTY_KEY, INT_PROPERTY_VAL, config.getShort(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect short value for missing key", 0, config.getShort(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default short value for missing key", MISSING_PROPERTY_VAL, config.getShort(MISSING_PROPERTY_KEY,
				(short) MISSING_PROPERTY_VAL));

		Assert.assertEquals("Incorrect default short value for missing key",
				Short.valueOf((short) MISSING_PROPERTY_VAL), config.getShort(MISSING_PROPERTY_KEY, Short.valueOf((short) MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidShort() {
		config.getShort(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetByte() {
		final int expectedVal = 111;
		Assert.assertEquals("Incorrect byte value for " + INT_PROPERTY_KEY, INT_PROPERTY_VAL, config.getByte(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect byte value for missing key", 0, config.getByte(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default byte value for missing key", expectedVal, config.getByte(MISSING_PROPERTY_KEY, (byte) expectedVal));

		Assert.assertEquals("Incorrect default byte value for missing key",
				Byte.valueOf((byte) expectedVal), config.getByte(MISSING_PROPERTY_KEY, Byte.valueOf(
				(byte) expectedVal)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidByte() {
		config.getByte(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetBigDecimal() {
		Assert.assertEquals("Incorrect BigDecimal value for " + INT_PROPERTY_KEY,
				BigDecimal.valueOf(INT_PROPERTY_VAL), config.getBigDecimal(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect BigDecimal value for missing key",
				BigDecimal.valueOf(0.0), config.getBigDecimal(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default BigDecimal value for missing key",
				BigDecimal.valueOf(MISSING_PROPERTY_VAL), config.getBigDecimal(MISSING_PROPERTY_KEY, BigDecimal.
				valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidBigDecimal() {
		config.getBigDecimal(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetBigInteger() {
		Assert.assertEquals("Incorrect BigInteger value for " + INT_PROPERTY_KEY,
				BigInteger.valueOf(INT_PROPERTY_VAL), config.getBigInteger(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect BigInteger value for missing key",
				BigInteger.valueOf(0), config.getBigInteger(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default BigInteger value for missing key",
				BigInteger.valueOf(MISSING_PROPERTY_VAL), config.getBigInteger(MISSING_PROPERTY_KEY, BigInteger.
				valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidBigInteger() {
		config.getBigInteger(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetBoolean() {
		Assert.assertEquals("Incorrect boolean value for " + BOOLEAN_TRUE_PROPERTY_KEY,
				true, config.getBoolean(BOOLEAN_TRUE_PROPERTY_KEY));

		Assert.assertEquals("Incorrect boolean value for " + BOOLEAN_FALSE_PROPERTY_KEY,
				false, config.getBoolean(BOOLEAN_FALSE_PROPERTY_KEY));

		Assert.assertEquals("Incorrect boolean value for missing key",
				false, config.getBoolean(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default boolean value for missing key",
				true, config.getBoolean(MISSING_PROPERTY_KEY, true));

		Assert.assertEquals("Incorrect default boolean value for missing key",
				Boolean.TRUE, config.getBoolean(MISSING_PROPERTY_KEY, Boolean.TRUE));
	}

	@Test
	public void testGetFloat() {
		final float expectedVal = 234.0f;
		Assert.assertEquals("Incorrect float value for " + INT_PROPERTY_KEY,
				Float.parseFloat("123"), config.getFloat(INT_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect float value for missing key",
				new Float(0.0f), config.getFloat(MISSING_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect default float value for missing key",
				expectedVal, config.getFloat(MISSING_PROPERTY_KEY, expectedVal), 0.0);

		Assert.assertEquals("Incorrect default float value for missing key",
				Float.valueOf(MISSING_PROPERTY_VAL), config.getFloat(MISSING_PROPERTY_KEY, Float.valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidFloat() {
		config.getFloat(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetDouble() {
		final double expectedVal = 234.0;
		Assert.assertEquals("Incorrect double value for " + INT_PROPERTY_KEY,
				Double.parseDouble("123"), config.getDouble(INT_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect double value for missing key",
				0.0, config.getDouble(MISSING_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect default double value for missing key",
				expectedVal, config.getDouble(MISSING_PROPERTY_KEY, MISSING_PROPERTY_VAL), 0.0);

		Assert.assertEquals("Incorrect default double value for missing key",
				Double.valueOf(MISSING_PROPERTY_VAL), config.getDouble(MISSING_PROPERTY_KEY, Double.valueOf(MISSING_PROPERTY_VAL)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidDouble() {
		config.getDouble(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetList() {
		Assert.assertEquals("Incorrect list value for " + STRING_PROPERTY_KEY,
				Arrays.asList(new String[]{"simplePropertyValue"}), config.getList(
				STRING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect list value for simple.listPropertyKey",
				Arrays.asList(new String[]{"item1", "item2", "item3"}), config.getList(
				"simple.listPropertyKey"));

		List<String> defaultList = Arrays.asList(new String[]{"default1", "default2"});
		Assert.assertEquals("Incorrect default list value for missing key",
				defaultList, config.getList(MISSING_PROPERTY_KEY, defaultList));
	}

	@Test
	public void testGetProperties() {
		final int expectedProps = 3;
		Properties props = config.getProperties("simple.propertiesPropertyKey");
		Assert.assertEquals("Incorrect number of properties", expectedProps, props.size());
		assertPropertyEquals("key1", "value1", props);
		assertPropertyEquals("key2", "value2", props);
		assertPropertyEquals("key3", "value3", props);
	}

	/**
	 * Asserts that the configuration contains the given key/value.
	 *
	 * @param key the property key
	 * @param expected the expected property value.
	 */
	private void assertPropertyEquals(final String key, final Object expected) {
		Assert.assertEquals("Incorrect value for " + key, expected, config.get(key));
	}

	/**
	 * Asserts that the given properties contains the given key/value.
	 *
	 * @param key the property key
	 * @param expected the expected property value.
	 * @param props the properties to search in.
	 */
	private void assertPropertyEquals(final String key, final Object expected,
			final Properties props) {
		Assert.assertEquals("Incorrect value for " + key, expected, props.get(key));
	}
}

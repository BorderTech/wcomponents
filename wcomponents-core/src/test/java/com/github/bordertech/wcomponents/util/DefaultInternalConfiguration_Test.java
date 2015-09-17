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
	 * The value for this property should be an empty string.
	 */
	private static final String EMPTY_PROPERTY_KEY = "simple.emptyPropertyKey";

	/**
	 * The value for this property should be "simplePropertyValue".
	 */
	private static final String STRING_PROPERTY_KEY = "simple.stringPropertyKey";

	/**
	 * The value for this property should be an 123.
	 */
	private static final String INT_PROPERTY_KEY = "simple.intPropertyKey";

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
		config = new DefaultInternalConfiguration("com/github/bordertech/wcomponents/util/DefaultInternalConfiguration_Test.properties");
	}

	@Test
	public void testSimpleProperties() {
		Assert.assertNull("Missing properties should be null", config.get("simple.nonExistantPropertyKey"));
		Assert.assertEquals("Incorrect default value for missing property", "defaultValue", config.getString("simple.nonExistantPropertyKey", "defaultValue"));
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
		// Test without the prefix truncated
		Properties props = config.getSubProperties("simple.", false);
		Assert.assertEquals("Incorrect number of properties", 7, props.size());
		assertPropertyEquals(EMPTY_PROPERTY_KEY, "", props);
		assertPropertyEquals(STRING_PROPERTY_KEY, "simplePropertyValue", props);
		assertPropertyEquals(INT_PROPERTY_KEY, "123", props);
		assertPropertyEquals(BOOLEAN_TRUE_PROPERTY_KEY, "true", props);
		assertPropertyEquals(BOOLEAN_FALSE_PROPERTY_KEY, "false", props);
		assertPropertyEquals("simple.listPropertyKey", "item1,item2,item3", props);
		assertPropertyEquals("simple.propertiesPropertyKey", "key1=value1,key2=value2,key3=value3", props);

		// Now test with the prefix truncated
		props = config.getSubProperties("simple.", true);
		Assert.assertEquals("Incorrect number of properties", 7, props.size());
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
		Assert.assertEquals("Incorrect long value for " + INT_PROPERTY_KEY,
				123, config.getLong(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect long value for missing key", 0,
				config.getLong(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default long value for missing key",
				234, config.getLong(MISSING_PROPERTY_KEY, 234));

		Assert.assertEquals("Incorrect default long value for missing key",
				Long.valueOf(234), config.getLong(MISSING_PROPERTY_KEY, Long.valueOf(234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidLong() {
		config.getLong(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetInt() {
		Assert.assertEquals("Incorrect int value for " + INT_PROPERTY_KEY,
				123, config.getInt(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect int value for missing key",
				0, config.getInt(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default int value for missing key",
				234, config.getInt(MISSING_PROPERTY_KEY, 234));

		Assert.assertEquals("Incorrect default integer value for missing key",
				Integer.valueOf(234), config.getInteger(MISSING_PROPERTY_KEY, Integer.valueOf(234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidInt() {
		config.getInt(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetShort() {
		Assert.assertEquals("Incorrect short value for " + INT_PROPERTY_KEY,
				123, config.getShort(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect short value for missing key",
				0, config.getShort(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default short value for missing key",
				234, config.getShort(MISSING_PROPERTY_KEY, (short) 234));

		Assert.assertEquals("Incorrect default short value for missing key",
				Short.valueOf((short) 234), config.getShort(MISSING_PROPERTY_KEY, Short.valueOf((short) 234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidShort() {
		config.getShort(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetByte() {
		Assert.assertEquals("Incorrect byte value for " + INT_PROPERTY_KEY,
				123, config.getByte(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect byte value for missing key",
				0, config.getByte(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default byte value for missing key",
				111, config.getByte(MISSING_PROPERTY_KEY, (byte) 111));

		Assert.assertEquals("Incorrect default byte value for missing key",
				Byte.valueOf((byte) 111), config.getByte(MISSING_PROPERTY_KEY, Byte.valueOf((byte) 111)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidByte() {
		config.getByte(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetBigDecimal() {
		Assert.assertEquals("Incorrect BigDecimal value for " + INT_PROPERTY_KEY,
				BigDecimal.valueOf(123), config.getBigDecimal(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect BigDecimal value for missing key",
				BigDecimal.valueOf(0.0), config.getBigDecimal(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default BigDecimal value for missing key",
				BigDecimal.valueOf(234), config.getBigDecimal(MISSING_PROPERTY_KEY, BigDecimal.valueOf(234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidBigDecimal() {
		config.getBigDecimal(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetBigInteger() {
		Assert.assertEquals("Incorrect BigInteger value for " + INT_PROPERTY_KEY,
				BigInteger.valueOf(123), config.getBigInteger(INT_PROPERTY_KEY));

		Assert.assertEquals("Incorrect BigInteger value for missing key",
				BigInteger.valueOf(0), config.getBigInteger(MISSING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect default BigInteger value for missing key",
				BigInteger.valueOf(234), config.getBigInteger(MISSING_PROPERTY_KEY, BigInteger.valueOf(234)));
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
		Assert.assertEquals("Incorrect float value for " + INT_PROPERTY_KEY,
				Float.parseFloat("123"), config.getFloat(INT_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect float value for missing key",
				new Float(0.0f), config.getFloat(MISSING_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect default float value for missing key",
				234.0f, config.getFloat(MISSING_PROPERTY_KEY, 234.0f), 0.0);

		Assert.assertEquals("Incorrect default float value for missing key",
				Float.valueOf(234), config.getFloat(MISSING_PROPERTY_KEY, Float.valueOf(234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidFloat() {
		config.getFloat(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetDouble() {
		Assert.assertEquals("Incorrect double value for " + INT_PROPERTY_KEY,
				Double.parseDouble("123"), config.getDouble(INT_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect double value for missing key",
				0.0, config.getDouble(MISSING_PROPERTY_KEY), 0.0);

		Assert.assertEquals("Incorrect default double value for missing key",
				234.0, config.getDouble(MISSING_PROPERTY_KEY, 234), 0.0);

		Assert.assertEquals("Incorrect default double value for missing key",
				Double.valueOf(234), config.getDouble(MISSING_PROPERTY_KEY, Double.valueOf(234)));
	}

	@Test(expected = ConversionException.class)
	public void testGetInvalidDouble() {
		config.getDouble(STRING_PROPERTY_KEY);
	}

	@Test
	public void testGetList() {
		Assert.assertEquals("Incorrect list value for " + STRING_PROPERTY_KEY,
				Arrays.asList(new String[]{"simplePropertyValue"}), config.getList(STRING_PROPERTY_KEY));

		Assert.assertEquals("Incorrect list value for simple.listPropertyKey",
				Arrays.asList(new String[]{"item1", "item2", "item3"}), config.getList("simple.listPropertyKey"));

		List<String> defaultList = Arrays.asList(new String[]{"default1", "default2"});
		Assert.assertEquals("Incorrect default list value for missing key",
				defaultList, config.getList(MISSING_PROPERTY_KEY, defaultList));
	}

	@Test
	public void testGetProperties() {
		Properties props = config.getProperties("simple.propertiesPropertyKey");
		Assert.assertEquals("Incorrect number of properties", 3, props.size());
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
	private void assertPropertyEquals(final String key, final Object expected, final Properties props) {
		Assert.assertEquals("Incorrect value for " + key, expected, props.get(key));
	}
}

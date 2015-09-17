package com.github.bordertech.wcomponents.examples.petstore;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CostRenderer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class CostRenderer_Test {

	/**
	 * expected test value - empty case.
	 */
	private static final String EXPECTED_VALUE_FROM_NULL_BEAN_VALUE = "$-.--";

	/**
	 * test getText - where beanValue is null.
	 */
	@Test
	public void testGetTextNull() {
		CostRenderer renderer = new CostRenderer();

		// not setting the bean
		String renderedText = renderer.getText();
		Assert.assertEquals("should return $string without values filled",
				EXPECTED_VALUE_FROM_NULL_BEAN_VALUE, renderedText);
	}

	/**
	 * test getText - where beanValue is an Integer.
	 */
	@Test
	public void testGetTextInteger() {
		final int testInt1 = 3456;
		final String expectedStr1 = "$34.56";

		CostRenderer renderer = new CostRenderer();
		renderer.setBean(Integer.valueOf(testInt1));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return correctly rendered dollar value", expectedStr1,
				renderedText);
	}

	/**
	 * test getText - where beanValue is not an Integer - but not null.
	 */
	@Test
	public void testGetTextNonInteger() {
		final double testDouble1 = 42.42;

		CostRenderer renderer = new CostRenderer();
		renderer.setBean(Double.valueOf(testDouble1));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return $string without values filled",
				EXPECTED_VALUE_FROM_NULL_BEAN_VALUE, renderedText);
	}
}

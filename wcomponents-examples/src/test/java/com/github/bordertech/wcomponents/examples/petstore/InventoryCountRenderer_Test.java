package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InventoryCountRenderer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class InventoryCountRenderer_Test {

	/**
	 * Expected test value - empty case.
	 */
	private static final String EXPECTED_VALUE_FROM_NULL_BEAN_VALUE = "";

	/**
	 * String test result 1 - not directly available in class being tested.
	 */
	private static final String TEST_RESULT_STATUS_NO_LONGER_AVALIABLE = "No longer available";

	/**
	 * String test result 2 - not directly available in class being tested.
	 */
	private static final String TEST_RESULT_STATUS_NEW = "Coming soon";

	/**
	 * String test result 3 - not directly available in class being tested.
	 */
	private static final String TEST_RESULT_STATUS_OTHER = "Sold out";

	/**
	 * Test getText - where beanValue is null.
	 */
	@Test
	public void testGetTextNull() {
		InventoryCountRenderer renderer = new InventoryCountRenderer();

		// not setting the bean
		String renderedText = renderer.getText();
		Assert.assertEquals("should return $string without values filled",
				EXPECTED_VALUE_FROM_NULL_BEAN_VALUE, renderedText);
	}

	/**
	 * Test getText - NonZero Count.
	 */
	@Test
	public void testGetTextPositiveCount() {
		InventoryCountRenderer renderer = new InventoryCountRenderer();

		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_SPECIAL;
		final int testCount = 12;
		final int testUnitCost = 5432;
		final String expectedTestResult = String.valueOf(testCount);

		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return testCount as String", expectedTestResult, renderedText);
	}

	/**
	 * Test getText - ZeroCount - Status NoLonger Available.
	 */
	@Test
	public void testGetTextZeroCountNoLongerAvailable() {
		InventoryCountRenderer renderer = new InventoryCountRenderer();

		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_NO_LONGER_AVAILABLE;
		final int testCount = 0;
		final int testUnitCost = 5432;
		final String expectedTestResult = TEST_RESULT_STATUS_NO_LONGER_AVALIABLE;

		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return warning 1", expectedTestResult, renderedText);
	}

	/**
	 * Test getText - ZeroCount - Status New.
	 */
	@Test
	public void testGetTextZeroCountNew() {
		InventoryCountRenderer renderer = new InventoryCountRenderer();

		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_NEW;
		final int testCount = 0;
		final int testUnitCost = 5432;
		final String expectedTestResult = TEST_RESULT_STATUS_NEW;

		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return warning 2", expectedTestResult, renderedText);
	}

	/**
	 * Test getText - ZeroCount - Status Other - Sold Out.
	 */
	@Test
	public void testGetTextZeroCountSoldOut() {
		InventoryCountRenderer renderer = new InventoryCountRenderer();

		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_SPECIAL;
		final int testCount = 0;
		final int testUnitCost = 5432;
		final String expectedTestResult = TEST_RESULT_STATUS_OTHER;

		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderer.getText();
		Assert.assertEquals("should return warning 3", expectedTestResult, renderedText);
	}
}

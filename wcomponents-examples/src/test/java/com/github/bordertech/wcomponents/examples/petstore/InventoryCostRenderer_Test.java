package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InventoryCostRenderer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class InventoryCostRenderer_Test {

	/**
	 * expected test value - empty case.
	 */
	private static final String EXPECTED_VALUE_FROM_NULL_BEAN_VALUE = "$-.--";

	/**
	 * add on test string - private in class being tested.
	 */
	private static final String SPECIAL_STATUS_ALERT = System.getProperty("line.separator") + "<ui:text type=\"emphasised\"> -- on special!</ui:text>";

	/**
	 * Test getText - where beanValue is null.
	 */
	@Test
	public void testGetTextNull() {
		InventoryCostRenderer renderer = new InventoryCostRenderer();

		// not setting the bean
		String renderedText = renderToString(renderer);
		Assert.assertEquals("should return $string without values filled",
				EXPECTED_VALUE_FROM_NULL_BEAN_VALUE, renderedText);
	}

	/**
	 * Test getText - normal inventoryBean set.
	 */
	@Test
	public void testGetTextInventoryBean() {
		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_AVAILABLE;
		final int testCount = 12;
		final int testUnitCost = 2456;
		final String expectedTestResult = "$24.56";

		InventoryCostRenderer renderer = new InventoryCostRenderer();
		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderToString(renderer);
		Assert.assertEquals("should return expected dollar value", expectedTestResult, renderedText);
	}

	/**
	 * Test getText - special status inventoryBean set.
	 */
	@Test
	public void testGetTextInventoryBeanStatusSpecial() {
		final int testProductId = 1;
		final int testStatus = InventoryBean.STATUS_SPECIAL;
		final int testCount = 12;
		final int testUnitCost = 5432;
		final String expectedTestResult = "$54.32" + SPECIAL_STATUS_ALERT;

		InventoryCostRenderer renderer = new InventoryCostRenderer();
		renderer.setBean(new InventoryBean(testProductId, testStatus, testCount, testUnitCost));
		renderer.setBeanProperty(".");

		String renderedText = renderToString(renderer);
		Assert.assertEquals("should return expected dollar value", expectedTestResult, renderedText);
	}

	/**
	 * @param renderer the inventory cost renderer
	 * @return the string output of renderer
	 */
	private String renderToString(final InventoryCostRenderer renderer) {
		return WebUtilities.render(new MockRequest(), renderer);
	}
}

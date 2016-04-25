package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CartBean}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class CartBean_Test {

	/**
	 * Test constructor.
	 */
	@Test
	public void testConstructor() {
		final int testCountInit = 0;
		final int testProductIdInit = 0;

		CartBean bean = new CartBean();

		Assert.assertEquals("count should be default value", testCountInit, bean.getCount());
		Assert.assertEquals("productId should be default value", testProductIdInit, bean.
				getProductId());
	}

	/**
	 * Test constructor - set productId and count.
	 */
	@Test
	public void testConstructorProductCount() {
		final int testCount = 42;
		final int testProductId = 17;

		CartBean bean = new CartBean(testProductId, testCount);

		Assert.assertEquals("count should be value set", testCount, bean.getCount());
		Assert.assertEquals("productId should be value set", testProductId, bean.getProductId());
	}

	/**
	 * Test setCount.
	 */
	@Test
	public void testSetCount() {
		final int testCount = 47;

		CartBean bean = new CartBean();
		bean.setCount(testCount);

		Assert.assertEquals("count should be value set", testCount, bean.getCount());
	}

	/**
	 * Test setproductId.
	 */
	@Test
	public void testSetProductId() {
		final int testProductId = 49;

		CartBean bean = new CartBean();
		bean.setProductId(testProductId);

		Assert.assertEquals("productId should be value set", testProductId, bean.getProductId());
	}

	/**
	 * Test getItem.
	 */
	@Test
	public void testGetItem() {
		final int testCount = 42;
		final int testProductId = 2;

		CartBean bean = new CartBean(testProductId, testCount);
		ProductBean productBean = bean.getItem();

		Assert.assertEquals("should get product bean requested", testProductId, productBean.getId());
	}

	/**
	 * Test getSubTotal.
	 */
	@Test
	public void testGetSubTotal() {
		final int testCount = 1;
		final int testProductId = 1;

		CartBean bean = new CartBean(testProductId, testCount);
		int subTotal = bean.getSubTotal();

		// one dog at $10.00 each - or 10000 cents in total
		int expectedSubTotal = 10000;

		Assert.assertEquals("should return subtotal for testCount number of testProductId item",
				expectedSubTotal, subTotal);
	}
}

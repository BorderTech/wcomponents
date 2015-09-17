package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InventoryBean}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class InventoryBean_Test {

	/**
	 * Test constructor - empty.
	 */
	@Test
	public void testConstructor() {
		InventoryBean bean = new InventoryBean();

		Assert.assertEquals("productId should equal default", 0, bean.getProductId());
		Assert.assertEquals("status should equal default", 0, bean.getStatus());
		Assert.assertEquals("count should equal default", 0, bean.getCount());
		Assert.assertEquals("unitCost should equal default", 0, bean.getUnitCost());
	}

	/**
	 * Test constructor - all params input.
	 */
	@Test
	public void testConstructorAll() {
		final int testProductId = 2;
		final int testStatus = 55;
		final int testCount = 3;
		final int testUnitCost = 23000;

		InventoryBean bean = new InventoryBean(testProductId, testStatus, testCount, testUnitCost);

		Assert.assertEquals("productId should equal value set", testProductId, bean.getProductId());
		Assert.assertEquals("status should equal value set", testStatus, bean.getStatus());
		Assert.assertEquals("count should equal value set", testCount, bean.getCount());
		Assert.assertEquals("unitCost should equal value set", testUnitCost, bean.getUnitCost());
	}

	/**
	 * Test setCount.
	 */
	@Test
	public void testSetCount() {
		final int testCount = 3;

		InventoryBean bean = new InventoryBean();
		bean.setCount(testCount);

		Assert.assertEquals("count should equal value set", testCount, bean.getCount());
	}

	/**
	 * Test setProductId.
	 */
	@Test
	public void testSetProductId() {
		final int testProductId = 1;

		InventoryBean bean = new InventoryBean();
		bean.setProductId(testProductId);

		Assert.assertEquals("productId should equal value set", testProductId, bean.getProductId());
	}

	/**
	 * Test setStatus.
	 */
	@Test
	public void testSetStatus() {
		final int testStatus1 = -67;
		final int testStatus2 = InventoryBean.STATUS_NO_LONGER_AVAILABLE;
		final int testStatus3 = InventoryBean.STATUS_AVAILABLE;
		final int testStatus4 = InventoryBean.STATUS_NEW;
		final int testStatus5 = InventoryBean.STATUS_SPECIAL;

		int[] statuses = new int[]{testStatus1, testStatus2, testStatus3, testStatus4, testStatus5};

		InventoryBean bean = new InventoryBean();
		for (int i = 0; i < statuses.length; i++) {
			bean.setStatus(statuses[i]);
			Assert.assertEquals("status should equal value set", statuses[i], bean.getStatus());
		}
	}

	/**
	 * Test getProduct.
	 */
	@Test
	public void testGetProduct() {
		final int testProductId = 1;

		InventoryBean bean = new InventoryBean(testProductId, 42, 43, 44);
		ProductBean product = bean.getProduct();

		Assert.assertEquals("should return product with ID testProductId", testProductId, product.
				getId());
	}

	/**
	 * Test setUnitCost.
	 */
	@Test
	public void testSetUnitCost() {
		final int testUnitCost = 995;

		InventoryBean bean = new InventoryBean();
		bean.setUnitCost(testUnitCost);

		Assert.assertEquals("unitCost should equal value set", testUnitCost, bean.getUnitCost());
	}

	/**
	 * Test hashCode.
	 */
	@Test
	public void testHashCode() {
		final int testProductId = 12;

		InventoryBean bean = new InventoryBean(testProductId, 42, 43, 44);

		Assert.assertEquals("hasCode should be productId", testProductId, bean.hashCode());
	}

	/**
	 * Test equals.
	 */
	@Test
	public void testEquals() {
		InventoryBean bean1 = new InventoryBean(1, 42, 43, 44);
		AddressBean bean2 = new AddressBean();
		Assert.assertFalse("bean1 not equal bean2", bean1.equals(bean2));

		InventoryBean bean3 = new InventoryBean(1, 42, 43, 44);
		InventoryBean bean4 = new InventoryBean(2, 62, 63, 64);
		Assert.assertFalse("bean3 not equal bean4 by ID", bean3.equals(bean4));

		InventoryBean bean5 = new InventoryBean(5, 142, 143, 144);
		InventoryBean bean6 = new InventoryBean(5, 162, 163, 164);
		Assert.assertEquals("bean3 equals bean4 by ID", bean5, bean6);
	}
}

package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AddressBean}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class AddressBean_Test {

	/**
	 * Test constructor.
	 */
	@Test
	public void testConstructor() {
		AddressBean bean = new AddressBean();

		Assert.assertNull("line1 should be null", bean.getLine1());
		Assert.assertNull("line2 should be null", bean.getLine2());
		Assert.assertNull("suburb should be null", bean.getSuburb());
		Assert.assertNull("state should be null", bean.getState());
		Assert.assertNull("postcode should be null", bean.getPostcode());
		Assert.assertNull("country should be null", bean.getCountry());
	}

	/**
	 * Test setCountry.
	 */
	@Test
	public void testSetCountry() {
		final String testCountry = "Brazil";

		AddressBean bean = new AddressBean();
		bean.setCountry(testCountry);

		Assert.assertEquals("should retrieve country set", testCountry, bean.getCountry());
	}

	/**
	 * Test setLine1.
	 */
	@Test
	public void testSetLine1() {
		final String testLine1 = "this is the first line";

		AddressBean bean = new AddressBean();
		bean.setLine1(testLine1);

		Assert.assertEquals("should retrieve line1 set", testLine1, bean.getLine1());
	}

	/**
	 * Test setLine2.
	 */
	@Test
	public void testSetLine2() {
		final String testLine2 = "this is the second line";

		AddressBean bean = new AddressBean();
		bean.setLine2(testLine2);

		Assert.assertEquals("should retrieve line2 set", testLine2, bean.getLine2());
	}

	/**
	 * Test setPostcode.
	 */
	@Test
	public void testSetPostcode() {
		final String testPostcode = "6666";

		AddressBean bean = new AddressBean();
		bean.setPostcode(testPostcode);

		Assert.assertEquals("should retrieve postCode set", testPostcode, bean.getPostcode());
	}

	/**
	 * Test setState.
	 */
	@Test
	public void testSetState() {
		final String testState = "TAS";

		AddressBean bean = new AddressBean();
		bean.setState(testState);

		Assert.assertEquals("should retrieve state set", testState, bean.getState());
	}

	/**
	 * Test setSuburb.
	 */
	@Test
	public void testSetSuburb() {
		final String testSuburb = "Kensington";

		AddressBean bean = new AddressBean();
		bean.setSuburb(testSuburb);

		Assert.assertEquals("should retrieve suburb set", testSuburb, bean.getSuburb());
	}

}

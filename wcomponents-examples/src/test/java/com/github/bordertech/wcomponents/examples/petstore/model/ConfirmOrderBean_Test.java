package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ConfirmOrderBean}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ConfirmOrderBean_Test {

	/**
	 * Test empty constructor.
	 */
	@Test
	public void testConstructor() {
		ConfirmOrderBean bean = new ConfirmOrderBean();

		Assert.assertNull("first name should be null", bean.getFirstName());
		Assert.assertNull("last name should be null", bean.getLastName());
		Assert.assertNull("home phone should be null", bean.getHomePhone());
		Assert.assertNull("work phone should be null", bean.getWorkPhone());
		Assert.assertNull("email address hsould be null", bean.getEmailAddress());
		Assert.assertNull("payment type should be null", bean.getPaymentType());

		Assert.assertNotNull("address should NOT be null", bean.getAddress());
		Assert.assertNull("address - line1 - should be null", bean.getAddress().getLine1());
		Assert.assertNull("address - line2 should be null", bean.getAddress().getLine2());
		Assert.assertNull("address - suburb should be null", bean.getAddress().getSuburb());
		Assert.assertNull("address - state should be null", bean.getAddress().getState());
		Assert.assertNull("address - postcode should be null", bean.getAddress().getPostcode());
		Assert.assertNull("address - country should be null", bean.getAddress().getCountry());
	}

	/**
	 * Test setAddress.
	 */
	@Test
	public void testSetAddress() {
		final AddressBean address = new AddressBean();
		address.setLine1("timbuktu");
		address.setCountry("Mali");

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setAddress(address);

		Assert.assertEquals("should retrieve addressBean set", address, bean.getAddress());
	}

	/**
	 * Test setAddress - null.
	 */
	@Test
	public void testSetAddressNull() {
		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setAddress(null);

		AddressBean address = bean.getAddress();
		Assert.assertNotNull("should retrieve new empty address bean", address);

		Assert.assertNull("address - line1 - should be null", address.getLine1());
		Assert.assertNull("address - line2 should be null", address.getLine2());
		Assert.assertNull("address - suburb should be null", address.getSuburb());
		Assert.assertNull("address - state should be null", address.getState());
		Assert.assertNull("address - postcode should be null", address.getPostcode());
		Assert.assertNull("address - country should be null", address.getCountry());
	}

	/**
	 * Test setFirstName.
	 */
	@Test
	public void testSetFirstName() {
		final String testFirstName = "Fred";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setFirstName(testFirstName);

		Assert.assertEquals("should return testFirstName set", testFirstName, bean.getFirstName());
	}

	/**
	 * Test setHomePhone.
	 */
	@Test
	public void testSetHomePhone() {
		final String testHomePhone = "0388882222";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setHomePhone(testHomePhone);

		Assert.assertEquals("should return testHomePhone set", testHomePhone, bean.getHomePhone());
	}

	/**
	 * Test setLastName.
	 */
	@Test
	public void testSetLastName() {
		final String testLastName = "Flinstone";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setLastName(testLastName);

		Assert.assertEquals("should return testLastName set", testLastName, bean.getLastName());
	}

	/**
	 * Test setPaymentType.
	 */
	@Test
	public void testSetPaymentType() {
		final String testPaymentType = "CREDIT CARD";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setPaymentType(testPaymentType);

		Assert.assertEquals("should return testPaymentType set", testPaymentType, bean.
				getPaymentType());
	}

	/**
	 * Test setWorkPhone.
	 */
	@Test
	public void testSetWorkPhone() {
		final String testWorkPhone = "0355552222";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setWorkPhone(testWorkPhone);

		Assert.assertEquals("should return testWorkPhone set", testWorkPhone, bean.getWorkPhone());

	}

	/**
	 * Test setEmailAddress.
	 */
	@Test
	public void testSetEmailAddress() {
		final String testEmailAddress = "fred@bedrock.com";

		ConfirmOrderBean bean = new ConfirmOrderBean();
		bean.setEmailAddress(testEmailAddress);

		Assert.assertEquals("should return testEmailAddress set", testEmailAddress, bean.
				getEmailAddress());
	}
}

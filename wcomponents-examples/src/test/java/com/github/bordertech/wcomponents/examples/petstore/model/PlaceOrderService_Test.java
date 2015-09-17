package com.github.bordertech.wcomponents.examples.petstore.model;

import com.github.bordertech.wcomponents.examples.petstore.model.PlaceOrderService.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link PlaceOrderService}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class PlaceOrderService_Test {

	/**
	 * orderSequenceNumber from PlaceOrderService.
	 */
	private static final int FIXED_INITIAL_ORDER_SEQUENCE_NUMBER = 12345;
	private static int orderSequenceNumber = -1;

	/**
	 * Helper for these unit tests. Manages the expected sequence number `orderSequenceNumber` which can be used in unit
	 * tests.
	 *
	 * @param cart The cart to pass to PlaceOrderService
	 * @param clientDetails The clientDetails to pass to PlaceOrderService
	 * @return The resulting order status.
	 */
	private static OrderStatus placeOrder(final List<CartBean> cart,
			final ConfirmOrderBean clientDetails) {
		PlaceOrderService service = PlaceOrderService.getInstance();
		OrderStatus result = service.placeOrder(cart, clientDetails);
		if (result.getStatus() == PlaceOrderService.OrderStatus.SUCCESS) {
			if (orderSequenceNumber < 0) {
				orderSequenceNumber = FIXED_INITIAL_ORDER_SEQUENCE_NUMBER;
			} else {
				orderSequenceNumber++;
			}
		}
		return result;
	}

	/**
	 * Test getInstance.
	 */
	@Test
	public void testGetInstance() {
		PlaceOrderService service = PlaceOrderService.getInstance();
		Assert.assertNotNull("should return non null service", service);
	}

	/**
	 * Test placeOrder - successfully.
	 */
	@Test
	public void testPlaceOrder() {
		CartBean cartBean1 = new CartBean(0, 1); // cat - 1
		CartBean cartBean2 = new CartBean(1, 1); // dog - 1
		List<CartBean> cart = new ArrayList<>();
		cart.add(cartBean1);
		cart.add(cartBean2);
		ConfirmOrderBean clientDetails = new ConfirmOrderBean();
		clientDetails.setFirstName("Fred");
		clientDetails.setLastName("Flinstone");

		OrderStatus result = placeOrder(cart, clientDetails);
		Assert.assertEquals("should be successful", PlaceOrderService.OrderStatus.SUCCESS, result.
				getStatus());
		Assert.assertEquals("should get correct sequence number", Integer.valueOf(
				orderSequenceNumber), result.getDetails());

		result = placeOrder(cart, clientDetails);
		Assert.assertEquals("should be successful", PlaceOrderService.OrderStatus.SUCCESS, result.
				getStatus());
		Assert.assertEquals("should get correct sequence number", Integer.valueOf(
				orderSequenceNumber), result.getDetails());
	}

	/**
	 * Test placeOrder - unsuccessfully.
	 */
	@Test
	public void testPlaceOrderUnsuccessfully() {
		CartBean cartBean1 = new CartBean(0, 500); // cats - 500
		CartBean cartBean2 = new CartBean(1, 1); // dog - 1
		List<CartBean> cart = new ArrayList<>();
		cart.add(cartBean1);
		cart.add(cartBean2);
		ConfirmOrderBean clientDetails = new ConfirmOrderBean();
		clientDetails.setFirstName("Fred");
		clientDetails.setLastName("Flinstone");

		OrderStatus result = placeOrder(cart, clientDetails);
		Assert.assertEquals("should get insufficient stock",
				PlaceOrderService.OrderStatus.INSUFFICIENT_STOCK, result.getStatus());
		Assert.assertEquals("should get cartBean1 details", cartBean1, result.getDetails()); // the cat item failed
	}

	/**
	 * Test placeOrder - for too many fish.
	 */
	@Test
	public void testPlaceOrderTooMany() {
		ConfirmOrderBean clientDetails = new ConfirmOrderBean();
		clientDetails.setFirstName("Fred");
		clientDetails.setLastName("Flinstone");

		CartBean cartBean1 = new CartBean(2, 30); // fish - 30 - out of 50 available
		CartBean cartBean2 = new CartBean(1, 1); // dog - 1
		List<CartBean> cart = new ArrayList<>();
		cart.add(cartBean1);
		cart.add(cartBean2);

		OrderStatus result = placeOrder(cart, clientDetails);
		Assert.assertEquals("should be successful", PlaceOrderService.OrderStatus.SUCCESS, result.
				getStatus());
		Assert.assertEquals("should get correct sequence number", Integer.valueOf(
				orderSequenceNumber), result.getDetails());

		CartBean cartBean3 = new CartBean(2, 21); // fish - 21 - out of only 20 now available
		List<CartBean> cart2 = new ArrayList<>();
		cart2.add(cartBean3);

		result = placeOrder(cart2, clientDetails);
		Assert.assertEquals("should get Insuffient stock",
				PlaceOrderService.OrderStatus.INSUFFICIENT_STOCK, result.getStatus());
		Assert.assertEquals("should get cartbean3 details", cartBean3, result.getDetails());
	}

	/**
	 * Test orderStatus constructor and static status values.
	 */
	@Test
	public void testOrderStatusConstructor() {
		final int status = 27;
		final Object details = new String("details of order");
		final String userMessage = "user message";

		PlaceOrderService.OrderStatus orderStatus = new PlaceOrderService.OrderStatus(status,
				details, userMessage);

		Assert.assertEquals("status should be value set", status, orderStatus.getStatus());
		Assert.assertEquals("details should be object set", details, orderStatus.getDetails());
		Assert.assertEquals("userMessage should be value set", userMessage, orderStatus.
				getUserMessage());

		orderStatus = new PlaceOrderService.OrderStatus(PlaceOrderService.OrderStatus.UNKOWN_FAILURE,
				details,
				userMessage);
		Assert.assertEquals("status should be static value set",
				PlaceOrderService.OrderStatus.UNKOWN_FAILURE,
				orderStatus.getStatus());

		orderStatus = new PlaceOrderService.OrderStatus(PlaceOrderService.OrderStatus.SUCCESS,
				details, userMessage);
		Assert.assertEquals("status should be static value set",
				PlaceOrderService.OrderStatus.SUCCESS, orderStatus.getStatus());

		orderStatus = new PlaceOrderService.OrderStatus(
				PlaceOrderService.OrderStatus.INSUFFICIENT_STOCK, details,
				userMessage);
		Assert.assertEquals("status should be static value set",
				PlaceOrderService.OrderStatus.INSUFFICIENT_STOCK,
				orderStatus.getStatus());
	}
}

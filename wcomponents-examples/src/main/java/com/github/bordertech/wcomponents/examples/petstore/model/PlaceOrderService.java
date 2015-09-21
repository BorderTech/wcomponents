package com.github.bordertech.wcomponents.examples.petstore.model;

import java.util.List;

/**
 * A service to place an order. For a real application, this should be an EJB or ESB/point-to-point service, accessed
 * through a DMS.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class PlaceOrderService {

	/**
	 * The "order sequence" used to generate the order number.
	 */
	private int orderSequenceNumber = 12345;

	/**
	 * Singleton instance.
	 */
	private static final PlaceOrderService INSTANCE = new PlaceOrderService();

	/**
	 * For simple synchronisation.
	 */
	private final Object lock = new Object();

	/**
	 * Prevent instantation from outside this class.
	 */
	private PlaceOrderService() {
	}

	/**
	 * @return the singleton PlaceOrderService instance.
	 */
	public static PlaceOrderService getInstance() {
		return INSTANCE;
	}

	/**
	 * Places an order.
	 *
	 * @param cart a list of CartBeans.
	 * @param clientDetails the client details.
	 * @return the result of order placement.
	 */
	public OrderStatus placeOrder(final List<CartBean> cart, final ConfirmOrderBean clientDetails) {
		// We synchronize this block to ensure that stock levels are updated
		// correctly in a multi-user environment.
		synchronized (lock) {
			// We only validate the stock levels, as we can safely assume that
			// the data has been validated by the front-end. Obviously, a real
			// back-end should be validating all data.
			InventoryBean[] inventory = new InventoryBean[cart.size()];
			CartBean[] cartBeans = cart.toArray(new CartBean[cart.size()]);

			for (int i = 0; i < cart.size(); i++) {
				inventory[i] = PetStoreDao.readInventory(cartBeans[i].getProductId());

				if (inventory[i].getCount() < cartBeans[i].getCount()) {
					return new OrderStatus(OrderStatus.INSUFFICIENT_STOCK,
							cartBeans[i], "Insufficient stock to meet order.");
				}
			}

			// "Place the order".
			for (int i = 0; i < cart.size(); i++) {
				inventory[i].setCount(inventory[i].getCount() - cartBeans[i].getCount());
				PetStoreDao.writeInventory(inventory[i]);
			}

			return new OrderStatus(OrderStatus.SUCCESS, orderSequenceNumber++, null);
		}
	}

	/**
	 * A mock "order service result" object.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class OrderStatus {

		/**
		 * Indicates that the order failed, for unknown reasons.
		 */
		public static final int UNKOWN_FAILURE = 0;
		/**
		 * Indicates that the order was successfully placed.
		 */
		public static final int SUCCESS = 1;
		/**
		 * Indicates that the order failed, due to insufficient stock.
		 */
		public static final int INSUFFICIENT_STOCK = 2;

		/**
		 * The order status.
		 */
		private final int status;
		/**
		 * Details of the order transaction, if applicable.
		 */
		private final Object details;
		/**
		 * The message to display to the user.
		 */
		private final String userMessage;

		/**
		 * Creates an OrderStatus.
		 *
		 * @param status the order status
		 * @param details the details of the order, or failure
		 * @param userMessage a message to display to the user, if appropriate.
		 */
		public OrderStatus(final int status, final Object details, final String userMessage) {
			this.status = status;
			this.details = details;
			this.userMessage = userMessage;
		}

		/**
		 * @return the order status.
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * @return the details of the order transaction, if applicable.
		 */
		public Object getDetails() {
			return details;
		}

		/**
		 * @return the message to dispaly to the user.
		 */
		public String getUserMessage() {
			return userMessage;
		}
	}
}

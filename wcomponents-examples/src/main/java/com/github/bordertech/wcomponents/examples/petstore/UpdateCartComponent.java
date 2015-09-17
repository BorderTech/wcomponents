package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.petstore.model.CartBean;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;
import java.util.List;

/**
 * A component to update the quantity of an item in the user's shopping cart. Expects the bean to be the Integer item
 * id.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class UpdateCartComponent extends WBeanContainer {

	/**
	 * The entry field used to enter the quantity of items they wish to purchase.
	 */
	private final WTextField amount = new WTextField() {
		@Override
		public String getAccessibleText() {
			return "Order amount for " + getItemDescription();
		}
	};

	/**
	 * Creates an UpdateCartComponent.
	 */
	public UpdateCartComponent() {
		// We want the amount to defer to the model
		amount.setBeanProvider(new BeanProvider() {
			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				CartBean bean = getCartBean();

				if (bean != null) {
					return String.valueOf(bean.getCount());
				}

				return "";
			}
		});

		amount.setColumns(3);
		amount.setMaxLength(3);
		amount.setBeanProperty(".");

		add(new WFieldErrorIndicator(amount));
		add(amount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateBeanValue() {
		updateCart();
	}

	/**
	 * Updates the contents of the user's shopping cart.
	 */
	public void updateCart() {
		PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
		Integer productId = (Integer) getBeanValue();

		if (productId != null && app != null) {
			String amountText = amount.getText();

			int quantity = Util.empty(amountText) ? 0 : Integer.parseInt(amountText);
			updateCart(productId.intValue(), quantity, app.getCart());
			amount.reset();
		}
	}

	/**
	 * Updates the contents of a user's shopping cart and displays a confirmation message to the user.
	 *
	 * @param productId the id of the product that is being updated.
	 * @param quantity the quantity being set.
	 * @param cart the cart to update.
	 */
	private void updateCart(final int productId, final int quantity, final List<CartBean> cart) {
		for (int i = 0; i < cart.size(); i++) {
			CartBean cartItem = cart.get(i);

			if (cartItem.getProductId() == productId) {
				String itemDescription = PetStoreDao.readProduct(productId).getShortTitle();

				// Found it, update the cart item
				if (quantity == 0) {
					WMessages.getInstance(this).info("Removed " + itemDescription + " from cart.");
					cart.remove(i);
				} else if (cartItem.getCount() != quantity) {
					WMessages.getInstance(this).info("Cart contents updated.");
					cartItem.setCount(quantity);
				}

				return;
			}
		}

		// Not found in cart, are we adding a new item to the cart?
		if (quantity != 0) {
			String itemDescription = PetStoreDao.readProduct(productId).getShortTitle();

			WMessages.getInstance(this).info("Added " + itemDescription + " to cart.");
			cart.add(new CartBean(productId, quantity));
		}
	}

	/**
	 * Retrieves the cart contents that correspond to this item.
	 *
	 * @return the cart contents that correspond to this item, may be null.
	 */
	private CartBean getCartBean() {
		PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
		int productId = ((Integer) getBeanValue()).intValue();

		for (CartBean cartItem : app.getCart()) {
			if (cartItem.getProductId() == productId) {
				return cartItem;
			}
		}

		return null;
	}

	/**
	 * Retrieves the short description of the product for this component.
	 *
	 * @return the item description.
	 */
	private String getItemDescription() {
		int productId = ((Integer) getBeanValue()).intValue();
		return PetStoreDao.readProduct(productId).getShortTitle();
	}

	/**
	 * Override isVisible to disable the purchasing of items that are not in stock.
	 *
	 * @return true if this component is visible, false if invisible.
	 */
	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}

		Integer productId = (Integer) getBeanValue();
		InventoryBean inventory = PetStoreDao.readInventory(productId.intValue());

		boolean disabled = getCartBean() == null
				&& (inventory.getCount() == 0 || inventory.getStatus() == InventoryBean.STATUS_NO_LONGER_AVAILABLE);

		return !disabled;
	}

	/**
	 * Validates the component.
	 *
	 * @param diags the list of diagnostics to add validation errors to.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		String text = amount.getText();

		if (!Util.empty(text)) {
			try {
				int orderQuantity = Integer.parseInt(text);

				Integer productId = (Integer) getBeanValue();
				InventoryBean inventory = PetStoreDao.readInventory(productId.intValue());

				if (orderQuantity > inventory.getCount()) {
					diags.add(createErrorDiagnostic(amount, "Not enough stock to fill order."));
				}
			} catch (NumberFormatException e) {
				diags.add(createErrorDiagnostic(amount, "Order quantity must be an integer."));
			}
		}
	}
}

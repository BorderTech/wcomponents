package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * The navigation bar for the PetStore application.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class NavigationBar extends WPanel {

	/**
	 * The button used to navigate to the cart screen.
	 */
	private final WButton cartButton = new CartButton();
	/**
	 * The button used to navigate to the order confirmation screen.
	 */
	private final WButton checkOutButton = new CheckOutButton();
	/**
	 * The button used to navigate to the main product listing.
	 */
	private final WButton continueShoppingButton = new ContinueShoppingButton();

	/**
	 * Creates a NavigationBar.
	 */
	public NavigationBar() {
		setLayout(new FlowLayout(Alignment.RIGHT, 5, 0));

		add(cartButton);
		add(continueShoppingButton);
		add(checkOutButton);
	}

	/**
	 * @return the "view cart" button.
	 */
	public WButton getCartButton() {
		return cartButton;
	}

	/**
	 * @return the "continue shopping" button.
	 */
	public WButton getContinueShoppingButton() {
		return continueShoppingButton;
	}

	/**
	 * @return the "check out" button.
	 */
	public WButton getCheckOutButton() {
		return checkOutButton;
	}

	/**
	 * The "Continue shopping" button.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class ContinueShoppingButton extends WButton {

		/**
		 * Creates a ContinueShoppingButton.
		 */
		private ContinueShoppingButton() {
			super("Continue shopping");
			setRenderAsLink(true);

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class,
							ContinueShoppingButton.this);

					if (app != null) {
						app.showProductListing();
					}
				}
			});
		}

		/**
		 * Override isVisible to dynamically change the visibility without needing to store anything in the session.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
			return app != null && !(app.getActiveSection() instanceof ProductTable);
		}
	}

	/**
	 * The "Show cart" button.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class CartButton extends WButton {

		/**
		 * Creates a CartButton.
		 */
		private CartButton() {
			super("Show cart");
			setRenderAsLink(true);

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class,
							CartButton.this);

					if (app != null) {
						app.showCart();
					}
				}
			});
		}

		/**
		 * Override isDisabled to dynamically enable/disable navigation depending on the contents of the cart.
		 *
		 * @return true if the button is disabled.
		 */
		@Override
		public boolean isDisabled() {
			PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
			return app == null || app.getCart().isEmpty();
		}

		/**
		 * Override isVisible to dynamically change the visibility without needing to store anything in the session.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
			return app != null && !(app.getActiveSection() instanceof CartPanel);
		}
	}

	/**
	 * The "Check out" button.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class CheckOutButton extends WButton {

		/**
		 * Creates a CheckOutButton.
		 */
		private CheckOutButton() {
			super("Check out");
			setRenderAsLink(true);

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class,
							CheckOutButton.this);

					if (app != null) {
						app.showOrderConfirmation();
					}
				}
			});
		}

		/**
		 * Override isDisabled to dynamically enable/disable navigation depending on the contents of the cart.
		 *
		 * @return true if the button is disabled.
		 */
		@Override
		public boolean isDisabled() {
			PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
			return app == null || app.getCart().isEmpty();
		}

		/**
		 * Override isVisible to dynamically change the visibility without needing to store anything in the session.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
			return app != null && !(app.getActiveSection() instanceof ConfirmOrderPanel);
		}
	}
}

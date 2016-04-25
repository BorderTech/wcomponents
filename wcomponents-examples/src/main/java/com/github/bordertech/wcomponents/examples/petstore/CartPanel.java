package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanAndProviderBoundComponentModel;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMessagesValidatingAction;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.petstore.ProductTable.ProductLink;
import com.github.bordertech.wcomponents.examples.petstore.model.CartBean;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * CartPanel displays the contents of the "shoppping cart" and allows the user to add/remove items and "check out"
 * (complete) the order.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CartPanel extends WContainer {

	/**
	 * The "check out" button, used to complete the order.
	 */
	private final WButton checkOutButton = new WButton("Check out");
	/**
	 * The "update cart" button, used to update the cart contents.
	 */
	private final WButton updateCartButton = new WButton("Update cart");

	/**
	 * Creates a CartView.
	 */
	public CartPanel() {
		final WDataTable table = new WDataTable();

		table.setCaption("Items in your cart.");
		table.setSummary("Items in your cart.");
		table.setNoDataMessage("No items in cart.");

		table.addColumn(new WTableColumn("Product", new ProductLink()));
		table.addColumn(new WTableColumn("Cost", new CostRenderer()));
		table.addColumn(new WTableColumn("Order quantity", new UpdateCartComponent()));
		table.setDataModel(new CartTableModel());
		table.setBeanProvider(new BeanProvider() {
			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				List<CartBean> cart = getCart();
				List<InventoryBean> beanList = new ArrayList<>(cart.size());

				for (int i = 0; i < cart.size(); i++) {
					CartBean bean = cart.get(i);
					beanList.add(PetStoreDao.readInventory(bean.getProductId()));
				}

				return beanList;
			}
		});
		add(table);

		checkOutButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, CartPanel.this);

				if (app != null) {
					app.showOrderConfirmation();
				}
			}
		});

		updateCartButton.setAction(new WMessagesValidatingAction(table) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Since we aren't using an updateable data model, we can't ask the table to update the data.
				// We therefore go straight to the repeater, which will use the renderers for each column.
				WebUtilities.updateBeanValue(table.getRepeater());
				table.reset();
			}
		});

		WStyledText orderTotal = new WStyledText(null, WStyledText.Type.EMPHASISED) {
			@Override
			public String getText() {
				return "Order total: " + new DecimalFormat("$0.00").format(getOrderTotal() / 100.0);
			}
		};

		WPanel buttonPanel = new WPanel();
		buttonPanel.setLayout(new FlowLayout(Alignment.RIGHT));
		buttonPanel.add(updateCartButton);
		add(buttonPanel);

		add(new WHorizontalRule());

		WPanel bottomPanel = new WPanel();
		bottomPanel.setLayout(new FlowLayout(Alignment.RIGHT, 5, 0));
		bottomPanel.add(orderTotal);
		bottomPanel.add(checkOutButton);
		add(bottomPanel);
	}

	/**
	 * The data model for the cart table.
	 */
	public static final class CartTableModel extends SimpleBeanBoundTableDataModel {

		/**
		 * Creates a cart data model.
		 */
		public CartTableModel() {
			super(new String[]{"product", "unitCost", "productId"});
		}
	}

	/**
	 * Override preparePaintComponent to dynamically disable/enable the buttons depending on the cart contents.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		checkOutButton.setDisabled(getCart().isEmpty());
		updateCartButton.setDisabled(getCart().isEmpty());

		super.preparePaintComponent(request);
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new CartModel.
	 */
	@Override
	public CartModel newComponentModel() {
		return new CartModel();
	}

	/**
	 * Retrieves a user's cart.
	 *
	 * @return a list of CartBeans, may be empty.
	 */
	public List<CartBean> getCart() {
		return ((CartModel) getOrCreateComponentModel()).getCart();
	}

	/**
	 * Retrieves the total cost of a user's cart contents in cents.
	 *
	 * @return the total cost, in cents.
	 */
	public int getOrderTotal() {
		return ((CartModel) getComponentModel()).getOrderTotal();
	}

	/**
	 * The cart component model.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class CartModel extends BeanAndProviderBoundComponentModel {

		/**
		 * List of CartBeans.
		 */
		private final List<CartBean> cart = new ArrayList<>();

		/**
		 * Creates a CartModel.
		 */
		public CartModel() {
			// Need to define a public constructor for Externalizable
		}

		/**
		 * @return the cart.
		 */
		public List<CartBean> getCart() {
			return cart;
		}

		/**
		 * Retrieves the total cost of the cart contents in cents.
		 *
		 * @return the total cost, in cents.
		 */
		public int getOrderTotal() {
			int total = 0;

			for (CartBean cartBean : cart) {
				InventoryBean item = PetStoreDao.readInventory(cartBean.getProductId());
				total += item.getUnitCost() * cartBean.getCount();
			}

			return total;
		}
	}
}

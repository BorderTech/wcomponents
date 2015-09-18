package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableDataModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WMessagesValidatingAction;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.petstore.beanprovider.InventoryBeanProvider;
import com.github.bordertech.wcomponents.examples.petstore.model.ProductBean;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Displays the list of products in the store and allows a user to view a product or add it to their shopping cart.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ProductTable extends WContainer {

	/**
	 * The WTable used to render the repeating data.
	 */
	private final WDataTable table = new WDataTable();

	/**
	 * Creates a ProductTable.
	 */
	public ProductTable() {
		table.setCaption("Product listing.");
		table.setSummary("Product listing.");
		table.setNoDataMessage("No products found.");

		table.addColumn(new WTableColumn("Product", new ProductLink()));
		table.addColumn(new WTableColumn("Cost", new InventoryCostRenderer()));
		table.addColumn(new WTableColumn("Availability", new InventoryCountRenderer()));
		table.addColumn(new WTableColumn("Order quantity", new UpdateCartComponent()));

		table.setRowsPerPage(5);
		table.setPaginationMode(WDataTable.PaginationMode.CLIENT);
		table.setDataModel(new ProductTableModel());
		table.setBeanProvider(InventoryBeanProvider.getInstance());

		add(table);

		WPanel buttonPanel = new WPanel();
		buttonPanel.setLayout(new FlowLayout(Alignment.RIGHT));
		add(buttonPanel);

		WButton updateButton = new WButton("Update cart");
		buttonPanel.add(updateButton);

		updateButton.setAction(new WMessagesValidatingAction(table) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Since we aren't using an updateable data model, we can't ask the table to update the data.
				// We therefore go straight to the repeater, which will use the renderers for each column.
				WebUtilities.updateBeanValue(table.getRepeater());
			}
		});
	}

	/**
	 * The data model for the product table. Data is sourced from a bean provider so that it is not stored in the
	 * session.
	 */
	public static final class ProductTableModel extends SimpleBeanBoundTableDataModel {

		/**
		 * Creates a ProductTableModel.
		 */
		public ProductTableModel() {
			super(new String[]{"product", ".", ".", "productId"});
		}
	}

	/**
	 * A custom WButton that obtains its text from the bean and views a product when clicked.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class ProductLink extends WButton {

		/**
		 * Creates a ProductLink.
		 */
		public ProductLink() {
			setBeanProperty("shortTitle"); // render property
			setRenderAsLink(true);

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class,
							ProductLink.this);
					ProductBean inventory = (ProductBean) getBean();

					if (inventory != null && app != null) {
						app.showProductDetails(inventory.getId());
					}
				}
			});
		}

		/**
		 * Override WButton's getText so that the button text can be obtained from the bean rather than the ButtonModel.
		 *
		 * @return the text to display on the button.
		 */
		@Override
		public String getText() {
			return (String) getBeanValue();
		}
	}
}

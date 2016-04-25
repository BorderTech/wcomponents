package com.github.bordertech.wcomponents.examples.repeater;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WRepeater;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An example of a repeater used to output a list of products.
 *
 * @author Ming Gao
 */
public class RepeaterExample extends WContainer {

	/**
	 * The repeater used in the example.
	 */
	private final WRepeater productRepeater = new WRepeater(new CollapsibleRenderer());

	/**
	 * Creates a RepeaterExample.
	 */
	public RepeaterExample() {

		add(productRepeater);
	}

	/**
	 * Override to initialise some data.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		if (!this.isInitialised()) {
			// Give the repeater the list of data to display.
			productRepeater.setData(fetchProductData());

			// Remember that we've done the initialisation.
			this.setInitialised(true);
		}
	}

	/**
	 * The renderer used to render the row data.
	 */
	static class CollapsibleRenderer extends WDataRenderer {

		private final WCollapsible collapsible;
		private final ProductItemRenderer itemRender;

		/**
		 * Creates a CollapsibleRenderer.
		 */
		CollapsibleRenderer() {
			itemRender = new ProductItemRenderer();
			collapsible = new WCollapsible(itemRender, "Collapsible Client Side Example");
			add(collapsible);
		}

		/**
		 * Updates the component with new data.
		 *
		 * @param data the data to set on the component.
		 */
		@Override
		public void updateComponent(final Object data) {
			String name = ((ProductItemBean) data).getName();
			collapsible.getDecoratedLabel().setText(name);
		}
	}

	/**
	 * Product item renderer.
	 */
	static class ProductItemRenderer extends WBeanContainer {

		/**
		 * Creates a ProductItemRenderer.
		 */
		ProductItemRenderer() {
			setTemplate(ProductItemRenderer.class);

			// get the whole bean from the parent component
			setBeanProperty(".");
		}
	}

	/**
	 * @return a list of dummy data.
	 */
	private List<ProductItemBean> fetchProductData() {
		ProductItemBean entry;
		List<ProductItemBean> list = new ArrayList<>();

		// Add dummy category entry
		entry = new ProductItemBean();

		entry.setName("Unknown CD No.1");
		entry.setDescription("This is a CD from unknown resource.");
		entry.setItemAvailable(17);
		entry.setId(1001);

		entry.setCategory("CD");

		list.add(entry);

		// Add dummy category entry
		entry = new ProductItemBean();

		entry.setName("Unknown CD No.2");
		entry.setDescription("This is another CD from unknown resource.");
		entry.setItemAvailable(25);
		entry.setId(1002);

		entry.setCategory("CD");

		list.add(entry);

		// Add dummy category entry
		entry = new ProductItemBean();

		entry.setName("Unknown toy No.1");
		entry.setDescription("This is a toy from unknown resource.");
		entry.setItemAvailable(12);
		entry.setId(2001);

		entry.setCategory("TOY");

		list.add(entry);

		// Add dummy category entry
		entry = new ProductItemBean();

		entry.setName("Unknown toy No.2");
		entry.setDescription("This is another toy from unknown resource.");
		entry.setItemAvailable(22);
		entry.setId(2002);

		entry.setCategory("TOY");

		// Add dummy category entry
		list.add(entry);

		return list;
	}

	/**
	 * A simple data object.
	 */
	public static final class ProductItemBean implements Serializable {

		private String category;
		private String name;
		private String description;

		/**
		 * The id used to identify the product.
		 */
		private int id;

		/**
		 * Number of item available.
		 */
		private int itemAvailable;

		/**
		 * @return Returns the category.
		 */
		public String getCategory() {
			return category;
		}

		/**
		 * @param category The category to set.
		 */
		public void setCategory(final String category) {
			this.category = category;
		}

		/**
		 * @return Returns the description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description The description to set.
		 */
		public void setDescription(final String description) {
			this.description = description;
		}

		/**
		 * @return Returns the id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id The id to set.
		 */
		public void setId(final int id) {
			this.id = id;
		}

		/**
		 * @return Returns the itemAvailable.
		 */
		public int getItemAvailable() {
			return itemAvailable;
		}

		/**
		 * @param itemAvailable The itemAvailable to set.
		 */
		public void setItemAvailable(final int itemAvailable) {
			this.itemAvailable = itemAvailable;
		}

		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name The name to set.
		 */
		public void setName(final String name) {
			this.name = name;
		}
	}
}

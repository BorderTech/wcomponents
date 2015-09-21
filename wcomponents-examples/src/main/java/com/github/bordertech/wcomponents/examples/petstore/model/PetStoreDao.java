package com.github.bordertech.wcomponents.examples.petstore.model;

/**
 * A dummy DAO for the PetStore. There is only one copy of the hard-coded data, so that the data at least appears to be
 * updated when a single user interacts with the app.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class PetStoreDao {

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private PetStoreDao() {
	}

	/**
	 * Writes a single product.
	 *
	 * @param product the product to write.
	 */
	public static void writeProduct(final ProductBean product) {
		DummyData.PRODUCTS[product.getId()].setDescription(product.getDescription());
		DummyData.PRODUCTS[product.getId()].setShortTitle(product.getShortTitle());
		DummyData.PRODUCTS[product.getId()].setImage(product.getImage());
	}

	/**
	 * Writes a single inventory item.
	 *
	 * @param inventory the inventory to write.
	 */
	public static void writeInventory(final InventoryBean inventory) {
		DummyData.INVENTORY[inventory.getProductId()].setCount(inventory.getCount());
		DummyData.INVENTORY[inventory.getProductId()].setStatus(inventory.getStatus());
		DummyData.INVENTORY[inventory.getProductId()].setUnitCost(inventory.getUnitCost());
	}

	/**
	 * Retrieves a single item.
	 *
	 * @param productId the product id.
	 * @return the product with the given id.
	 */
	public static ProductBean readProduct(final int productId) {
		return DummyData.getProducts()[productId];
	}

	/**
	 * Retrieves a single inventory item.
	 *
	 * @param productId the product id.
	 * @return the inventory item with the given id.
	 */
	public static InventoryBean readInventory(final int productId) {
		return DummyData.getInventory()[productId];
	}

	/**
	 * Retrieves multiple inventory items.
	 *
	 * @param ids the item ids.
	 * @return the items with the given ids, in the same order as the ids.
	 */
	public static InventoryBean[] readInventory(final int[] ids) {
		InventoryBean[] beans = new InventoryBean[ids.length];

		for (int i = 0; i < ids.length; i++) {
			beans[i] = DummyData.getInventory()[ids[i]];
		}

		return beans;
	}

	/**
	 * Retrieves the entire inventory.
	 *
	 * @return the entire store inventory.
	 */
	public static InventoryBean[] readInventory() {
		return DummyData.getInventory();
	}

	/**
	 * The example data for the Petstore app.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class DummyData {

		/**
		 * A dummy description for all the products, just to show what a large block of text would look like.
		 */
		private static final String DUMMY_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In tristique pellentesque massa, et placerat justo ullamcorper vel. Nunc scelerisque, sem ut hendrerit pharetra, tellus erat dictum felis, at facilisis metus odio ac justo. Curabitur rutrum lacus in nulla iaculis at vestibulum metus facilisis. Aenean id nulla massa. Suspendisse vitae nunc nec urna laoreet elementum. Duis in orci ac leo elementum sagittis ac non massa. Sed vel massa purus, eu facilisis ipsum. Maecenas quis mi non metus scelerisque sagittis quis ac lacus. Fusce faucibus, urna ut viverra vulputate, tellus metus venenatis enim, eget mollis neque libero a turpis. Nullam convallis, lacus vel gravida suscipit, ipsum ante interdum libero, placerat laoreet dui magna et odio.\n\nPhasellus interdum placerat risus ut aliquam. In hac habitasse platea dictumst. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Fusce varius sem sit amet lorem commodo at ornare dui ultricies. Morbi consequat nunc sit amet magna facilisis luctus. Sed sit amet dapibus mi. Donec non quam tortor, sed tincidunt felis. Cras pulvinar ultrices elit in molestie. Morbi sapien nisi, porta in tempor et, dignissim quis nisl. Phasellus facilisis commodo mauris, in tristique velit semper in. Nullam vehicula, urna vel gravida molestie, lectus arcu semper urna, eget feugiat est augue id diam. Nulla dapibus eleifend justo, et malesuada erat accumsan vitae.";

		/**
		 * Hide the constructor as there are no instance methods.
		 */
		private DummyData() {
		}

		/**
		 * The products "database table".
		 */
		private static final ProductBean[] PRODUCTS = new ProductBean[]{
			new ProductBean(0, "Cat", "cat.gif", DUMMY_DESCRIPTION),
			new ProductBean(1, "Dog", "dog.gif", DUMMY_DESCRIPTION),
			new ProductBean(2, "Fish", "fish.gif", DUMMY_DESCRIPTION),
			new ProductBean(3, "Goat", "goat.gif", DUMMY_DESCRIPTION),
			new ProductBean(4, "Llama", "llama.gif", DUMMY_DESCRIPTION),
			new ProductBean(5, "Rabbit", "rabbit.gif", DUMMY_DESCRIPTION)
		};

		/**
		 * The inventory "database table".
		 */
		private static final InventoryBean[] INVENTORY = new InventoryBean[]{
			new InventoryBean(0, InventoryBean.STATUS_SPECIAL, 3, 9999),
			new InventoryBean(1, InventoryBean.STATUS_AVAILABLE, 5, 10000),
			new InventoryBean(2, InventoryBean.STATUS_AVAILABLE, 50, 10000),
			new InventoryBean(3, InventoryBean.STATUS_NO_LONGER_AVAILABLE, 0, 75000),
			new InventoryBean(4, InventoryBean.STATUS_NEW, 0, 150000),
			new InventoryBean(5, InventoryBean.STATUS_AVAILABLE, 10, 10000)
		};

		/**
		 * Returns the list of products. New instances of each product are returned, to emulate data expiring from the
		 * cache.
		 *
		 * @return the list of products.
		 */
		private static ProductBean[] getProducts() {
			ProductBean[] products = new ProductBean[PRODUCTS.length];

			for (int i = 0; i < PRODUCTS.length; i++) {
				products[i] = getProduct(i);
			}

			return products;
		}

		/**
		 * Returns the list of inventory. New instances of each inventory are returned, to emulate data expiring from
		 * the cache.
		 *
		 * @return the list of inventory.
		 */
		private static InventoryBean[] getInventory() {
			InventoryBean[] inventory = new InventoryBean[INVENTORY.length];

			for (int i = 0; i < INVENTORY.length; i++) {
				inventory[i] = getInventory(i);
			}

			return inventory;
		}

		/**
		 * Returns the given product. A new instance is returned, to emulate data expiring from the cache.
		 *
		 * @param productId the product id
		 * @return the product with the given id.
		 */
		private static ProductBean getProduct(final int productId) {
			return new ProductBean(PRODUCTS[productId].getId(), PRODUCTS[productId].getShortTitle(),
					PRODUCTS[productId].getImage(), PRODUCTS[productId].getDescription());
		}

		/**
		 * Returns the given inventory. A new instance is returned, to emulate data expiring from the cache.
		 *
		 * @param productId the product id
		 * @return the inventory with the given id.
		 */
		private static InventoryBean getInventory(final int productId) {
			return new InventoryBean(INVENTORY[productId].getProductId(), INVENTORY[productId].
					getStatus(),
					INVENTORY[productId].getCount(), INVENTORY[productId].getUnitCost());
		}
	}
}

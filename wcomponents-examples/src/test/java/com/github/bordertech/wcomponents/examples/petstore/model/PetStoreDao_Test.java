package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link PetStoreDAO}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class PetStoreDao_Test {

	/**
	 * expectedInventorySize from private PetStoreDao.DummyData.
	 */
	private static final int EXPECTED_INVENTORY_SIZE = 6;

	private static final int GOAT_ID = 3;

	/**
	 * A dummy description for all the products, private in the class being tested.
	 */
	private static final String DUMMY_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In tristique pellentesque massa, et placerat justo ullamcorper vel. Nunc scelerisque, sem ut hendrerit pharetra, tellus erat dictum felis, at facilisis metus odio ac justo. Curabitur rutrum lacus in nulla iaculis at vestibulum metus facilisis. Aenean id nulla massa. Suspendisse vitae nunc nec urna laoreet elementum. Duis in orci ac leo elementum sagittis ac non massa. Sed vel massa purus, eu facilisis ipsum. Maecenas quis mi non metus scelerisque sagittis quis ac lacus. Fusce faucibus, urna ut viverra vulputate, tellus metus venenatis enim, eget mollis neque libero a turpis. Nullam convallis, lacus vel gravida suscipit, ipsum ante interdum libero, placerat laoreet dui magna et odio.\n\nPhasellus interdum placerat risus ut aliquam. In hac habitasse platea dictumst. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Fusce varius sem sit amet lorem commodo at ornare dui ultricies. Morbi consequat nunc sit amet magna facilisis luctus. Sed sit amet dapibus mi. Donec non quam tortor, sed tincidunt felis. Cras pulvinar ultrices elit in molestie. Morbi sapien nisi, porta in tempor et, dignissim quis nisl. Phasellus facilisis commodo mauris, in tristique velit semper in. Nullam vehicula, urna vel gravida molestie, lectus arcu semper urna, eget feugiat est augue id diam. Nulla dapibus eleifend justo, et malesuada erat accumsan vitae.";

	/**
	 * Test writeProduct.
	 */
	@Test
	public void testWriteProduct() {
		final int testProductId = GOAT_ID;

		ProductBean oldProduct = PetStoreDao.readProduct(testProductId);  // save this so we can restore it once the test is complete

		try {
			final String newShortTitle = "lemming";
			final String newImage = "lemming.gif";
			final String newDescription = "has been modified";

			PetStoreDao.writeProduct(new ProductBean(testProductId, newShortTitle, newImage,
					newDescription));

			ProductBean newProduct = PetStoreDao.readProduct(testProductId);
			Assert.assertEquals("should get productId requested", testProductId, newProduct.getId());
			Assert.assertEquals("should get title for productdId " + testProductId, newShortTitle,
					newProduct.getShortTitle());
			Assert.assertEquals("should get image for productId " + testProductId, newImage,
					newProduct.getImage());
			Assert.assertEquals("should get description for productId " + testProductId,
					newDescription, newProduct.getDescription());
		} finally {
			PetStoreDao.writeProduct(oldProduct);  // restore the original product
		}
	}

	/**
	 * TestReadInventory.
	 */
	@Test
	public void testReadInventory() {
		InventoryBean[] inventory = PetStoreDao.readInventory();

		Assert.assertEquals("should get correct inventory size", EXPECTED_INVENTORY_SIZE,
				inventory.length);
		for (int i = 0; i < EXPECTED_INVENTORY_SIZE; i++) {
			Assert.assertEquals("should get productId equal to index", i, inventory[i].
					getProductId());
		}
	}

	/**
	 * TestReadInventory - subset matching array.
	 */
	@Test
	public void testReadInventorySubset() {
		int[] inventoryIds = new int[]{0, 2, 4};
		InventoryBean[] inventory = PetStoreDao.readInventory(inventoryIds);

		Assert.assertEquals("should get correct inventory size", inventoryIds.length,
				inventory.length);
		for (int i = 0; i < inventoryIds.length; i++) {
			Assert.assertEquals("should get productId twice the index", i * 2, inventory[i].
					getProductId());
		}
	}

	/**
	 * Test readInventory - single item.
	 */
	@Test
	public void testReadInventoryItem() {
		final int expectedStatus = InventoryBean.STATUS_NO_LONGER_AVAILABLE;
		final int expectedCount = 0;
		final int expectedCost = 75000;

		final int testProductId = GOAT_ID;
		InventoryBean item = PetStoreDao.readInventory(testProductId);

		Assert.assertEquals("should get expected  productId", testProductId, item.getProductId());
		Assert.assertEquals("should get status for productId " + testProductId, expectedStatus,
				item.getStatus());
		Assert.assertEquals("should get count for productId " + testProductId, expectedCount, item.
				getCount());
		Assert.assertEquals("should get cost for productId " + testProductId, expectedCost, item.
				getUnitCost());
	}

	/**
	 * Test readProduct.
	 */
	@Test
	public void testReadProduct() {
		final String expectedTitle = "Goat";
		final String expectedImage = "goat.gif";
		final String expectedDescription = DUMMY_DESCRIPTION;

		final int testProductId = GOAT_ID;
		ProductBean product = PetStoreDao.readProduct(testProductId);

		Assert.assertEquals("should get productId requested", testProductId, product.getId());
		Assert.assertEquals("should get title for productdId " + testProductId, expectedTitle,
				product.getShortTitle());
		Assert.assertEquals("should get image for productId " + testProductId, expectedImage,
				product.getImage());
		Assert.assertEquals("should get description for productId " + testProductId,
				expectedDescription, product.getDescription());
	}

	/**
	 * Test readProduct - index out of bounds.
	 */
	@Test
	public void testReadProductOutOfBounds() {
		try {
			final int testProductId = 17; // doesnt exist in fixed test data
			PetStoreDao.readProduct(testProductId);
			Assert.fail("should throw ArrayOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertNotNull("ArrayOutOfBoundsException message", e.getMessage());
		}
	}

	/**
	 * Test writeInventory.
	 */
	@Test
	public void testWriteInventory() {
		final int testProductId = GOAT_ID; // the goat again

		final int newStatus = InventoryBean.STATUS_NEW;
		final int newCount = 99;
		final int newUnitCost = 1995;

		PetStoreDao.writeInventory(
				new InventoryBean(testProductId, newStatus, newCount, newUnitCost));

		InventoryBean item = PetStoreDao.readInventory(testProductId);

		Assert.assertEquals("should get expected  productId", testProductId, item.getProductId());
		Assert.assertEquals("should get new status for productId " + testProductId, newStatus, item.
				getStatus());
		Assert.assertEquals("should get new count for productId " + testProductId, newCount, item.
				getCount());
		Assert.assertEquals("should get new cost for productId " + testProductId, newUnitCost, item.
				getUnitCost());
	}
}

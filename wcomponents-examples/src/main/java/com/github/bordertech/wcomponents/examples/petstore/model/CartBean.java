package com.github.bordertech.wcomponents.examples.petstore.model;

import java.io.Serializable;

/**
 * Represents an item of inventory in a cart.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CartBean implements Serializable {

	/**
	 * The id of the product in the cart.
	 */
	private int productId;
	/**
	 * The number of this type of product in the cart.
	 */
	private int count;

	/**
	 * Creates a CartBean.
	 */
	public CartBean() {
	}

	/**
	 * Creates a CartBean with the specified item and count.
	 *
	 * @param productId the item id.
	 * @param count the number of items.
	 */
	public CartBean(final int productId, final int count) {
		this.productId = productId;
		this.count = count;
	}

	/**
	 * @return Returns the count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count The count to set.
	 */
	public void setCount(final int count) {
		this.count = count;
	}

	/**
	 * @return Returns the productId.
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @param productId The productId to set.
	 */
	public void setProductId(final int productId) {
		this.productId = productId;
	}

	/**
	 * Added as a convenience method, so that the bean examples are a bit better.
	 *
	 * @return the item
	 */
	public ProductBean getItem() {
		return PetStoreDao.readProduct(productId);
	}

	/**
	 * Added as a convenience method.
	 *
	 * @return the subtotal for this item in the cart.
	 */
	public int getSubTotal() {
		InventoryBean item = PetStoreDao.readInventory(productId);
		return item.getUnitCost() * count;
	}
}

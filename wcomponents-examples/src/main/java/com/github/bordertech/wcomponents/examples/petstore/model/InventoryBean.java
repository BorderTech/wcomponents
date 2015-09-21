package com.github.bordertech.wcomponents.examples.petstore.model;

/**
 * Represents an item of inventory.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class InventoryBean {

	/**
	 * Indicates that the item is no longer available.
	 */
	public static final int STATUS_NO_LONGER_AVAILABLE = 0;

	/**
	 * Indicates that the item is available.
	 */
	public static final int STATUS_AVAILABLE = 1;

	/**
	 * Indicates that the item is is "new".
	 */
	public static final int STATUS_NEW = 2;

	/**
	 * Indicates that the item is is "new".
	 */
	public static final int STATUS_SPECIAL = 3;

	/**
	 * The id of the product that this inventory refers to.
	 */
	private int productId;
	/**
	 * The inventory status.
	 */
	private int status;
	/**
	 * The inventory stock level.
	 */
	private int count;
	/**
	 * The sale price per item of inventory.
	 */
	private int unitCost;

	/**
	 * Creates an InventoryBean.
	 */
	public InventoryBean() {
	}

	/**
	 * Creates an InventoryBean with the specified data.
	 *
	 * @param productId the id of the item.
	 * @param status the stock status.
	 * @param count the number of items in stock.
	 * @param unitCost the item unit cost, in cents.
	 */
	public InventoryBean(final int productId, final int status, final int count, final int unitCost) {
		this.productId = productId;
		this.status = status;
		this.count = count;
		this.unitCost = unitCost;
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
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(final int status) {
		this.status = status;
	}

	/**
	 * Added as a convenience method, so that the bean examples are a bit better.
	 *
	 * @return the item
	 */
	public ProductBean getProduct() {
		return PetStoreDao.readProduct(productId);
	}

	/**
	 * @return Returns the unit cost.
	 */
	public int getUnitCost() {
		return unitCost;
	}

	/**
	 * @param unitCost The unit cost to set.
	 */
	public void setUnitCost(final int unitCost) {
		this.unitCost = unitCost;
	}

	/**
	 * @return the productId as the hash-code.
	 */
	@Override
	public int hashCode() {
		return productId;
	}

	/**
	 * Indicates whether this InventoryBean is equal to the given object.
	 *
	 * @param obj the object to test for equivalence.
	 * @return true if the object is a InventoryBean and is equal to this bean.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof InventoryBean) {
			return productId == ((InventoryBean) obj).productId;
		}

		return false;
	}
}

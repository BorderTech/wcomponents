package com.github.bordertech.wcomponents.examples.petstore.model;

/**
 * Bean describing an item of inventory.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ProductBean {

	/**
	 * The product id.
	 */
	private int id;
	/**
	 * The product short title, to be displayed in links, headings etc.
	 */
	private String shortTitle;
	/**
	 * The full product description.
	 */
	private String description;
	/**
	 * The relative file-name for the product image/photo.
	 */
	private String image;

	/**
	 * Creates a ProductBean.
	 */
	public ProductBean() {
	}

	/**
	 * Creates a ProductBean with the specified data.
	 *
	 * @param productId the product id.
	 * @param shortTitle the short title.
	 * @param image the image id (if available).
	 * @param description the long description.
	 */
	public ProductBean(final int productId, final String shortTitle, final String image,
			final String description) {
		this.id = productId;
		this.shortTitle = shortTitle;
		this.image = image;
		this.description = description;
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
	 * @return Returns the image.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image The image to set.
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * @return Returns the shortTitle.
	 */
	public String getShortTitle() {
		return shortTitle;
	}

	/**
	 * @param shortTitle The shortTitle to set.
	 */
	public void setShortTitle(final String shortTitle) {
		this.shortTitle = shortTitle;
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
	 * @return the id as the hash-code.
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Indicates whether this ProductBean is equal to the given object.
	 *
	 * @param obj the object to test for equivalence.
	 * @return true if the object is a ProductBean and is equal to this bean.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ProductBean) {
			return id == ((ProductBean) obj).id;
		}

		return false;
	}
}

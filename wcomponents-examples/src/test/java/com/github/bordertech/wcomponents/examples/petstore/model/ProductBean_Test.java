package com.github.bordertech.wcomponents.examples.petstore.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ProductBean}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ProductBean_Test {

	/**
	 * Test product ID.
	 */
	private static final int TEST_PRODUCT_ID = 2;

	/**
	 * Test shortTitle.
	 */
	private static final String TEST_SHORT_TITLE = "freds blog";

	/**
	 * Test image.
	 */
	private static final String TEST_IMAGE = "test image";

	/**
	 * Test description.
	 */
	private static final String TEST_DESCRIPTION = "description of test image";

	@Test
	public void testEmptyConstructor() {
		ProductBean bean = new ProductBean();

		Assert.assertEquals("id should be default value", 0, bean.getId());
		Assert.assertNull("short title should be null", bean.getShortTitle());
		Assert.assertNull("image should be null", bean.getImage());
		Assert.assertNull("description should be null", bean.getDescription());
	}

	@Test
	public void testConstructorAllParams() {
		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);

		Assert.assertEquals("id should be value set", TEST_PRODUCT_ID, bean.getId());
		Assert.assertEquals("short title should be value set", TEST_SHORT_TITLE, bean.
				getShortTitle());
		Assert.assertEquals("image should be value set", TEST_IMAGE, bean.getImage());
		Assert.assertEquals("description should be value set", TEST_DESCRIPTION, bean.
				getDescription());
	}

	@Test
	public void testSetDescription() {
		final String testNewDescription = "new description";

		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);
		bean.setDescription(testNewDescription);

		Assert.assertEquals("id should be value set", TEST_PRODUCT_ID, bean.getId());
		Assert.assertEquals("short title should be value set", TEST_SHORT_TITLE, bean.
				getShortTitle());
		Assert.assertEquals("image should be value set", TEST_IMAGE, bean.getImage());
		Assert.assertEquals("description should be value set", testNewDescription, bean.
				getDescription());
	}

	@Test
	public void testSetImage() {
		final String testNewImage = "new image";

		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);
		bean.setImage(testNewImage);

		Assert.assertEquals("id should be value set", TEST_PRODUCT_ID, bean.getId());
		Assert.assertEquals("short title should be value set", TEST_SHORT_TITLE, bean.
				getShortTitle());
		Assert.assertEquals("image should be value set", testNewImage, bean.getImage());
		Assert.assertEquals("description should be value set", TEST_DESCRIPTION, bean.
				getDescription());
	}

	@Test
	public void testSetShortTitle() {
		final String testNewShortTitle = "new short Title";

		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);
		bean.setShortTitle(testNewShortTitle);

		Assert.assertEquals("id should be value set", TEST_PRODUCT_ID, bean.getId());
		Assert.assertEquals("short title should be value set", testNewShortTitle, bean.
				getShortTitle());
		Assert.assertEquals("image should be value set", TEST_IMAGE, bean.getImage());
		Assert.assertEquals("description should be value set", TEST_DESCRIPTION, bean.
				getDescription());
	}

	@Test
	public void testSetId() {
		final int testNewProductId = 3;

		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);
		bean.setId(testNewProductId);

		Assert.assertEquals("id should be value set", testNewProductId, bean.getId());
		Assert.assertEquals("short title should be value set", TEST_SHORT_TITLE, bean.
				getShortTitle());
		Assert.assertEquals("image should be value set", TEST_IMAGE, bean.getImage());
		Assert.assertEquals("description should be value set", TEST_DESCRIPTION, bean.
				getDescription());
	}

	@Test
	public void testHashCode() {
		ProductBean bean = new ProductBean(TEST_PRODUCT_ID, TEST_SHORT_TITLE, TEST_IMAGE,
				TEST_DESCRIPTION);

		Assert.assertEquals("hashCode should be ID", TEST_PRODUCT_ID, bean.hashCode());
	}

	@Test
	public void testEquals() {
		ProductBean bean1 = new ProductBean(1, "42", "43", "44");
		AddressBean bean2 = new AddressBean();
		Assert.assertFalse("bean1 not equal bean2", bean1.equals(bean2));

		ProductBean bean3 = new ProductBean(1, "42", "43", "44");
		ProductBean bean4 = new ProductBean(2, "42", "43", "44");
		Assert.assertFalse("bean3 not equal bean4 by ID", bean3.equals(bean4));

		ProductBean bean5 = new ProductBean(5, "142", "143", "144");
		ProductBean bean6 = new ProductBean(5, "142", "143", "144");
		Assert.assertEquals("bean3 equals bean4 by ID", bean5, bean6);
	}

}

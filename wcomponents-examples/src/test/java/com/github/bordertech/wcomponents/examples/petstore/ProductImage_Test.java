package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.examples.petstore.model.ProductBean;
import java.awt.Dimension;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ProductImage}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ProductImage_Test {

	/**
	 * Test constructor - bean null.
	 */
	@Test
	public void testConstructorBeanNull() {
		ProductImage image = new ProductImage(null);
		byte[] bytes = image.getBytes();

		Assert.assertNull("should be null - no bean to look for bytes", bytes);

		Assert.assertNull("should have no description set", image.getDescription());
		Assert.assertNull("should have no mime type set", image.getMimeType());
	}

	/**
	 * Test constructor - bean not null - name null.
	 */
	@Test
	public void testConstructorNameNull() {
		final int testProductId = 7;
		final String testTitle = "title";
		final String testName = null;
		final String testDescription = "description";

		ProductBean testProduct = new ProductBean(testProductId, testTitle, testName,
				testDescription);
		ProductImage image = new ProductImage(testProduct);
		byte[] bytes = image.getBytes();

		Assert.assertNull("should be null - no bean/name to look for bytes", bytes);

		Assert.assertEquals("should have description set to testTitle given", testTitle, image.
				getDescription());
		Assert.assertNull("should have no mime type set", image.getMimeType());
	}

	/**
	 * Test constructor - bean not null, name not null, name/file not found.
	 */
	@Test
	public void testConstructorBytesNotFound() {
		final int testProductId = 7;
		final String testTitle = "title";
		final String testName = "dogXXX.gif"; // this file does not exist in the image directory
		final String testDescription = "description";

		ProductBean testProduct = new ProductBean(testProductId, testTitle, testName,
				testDescription);
		ProductImage image = new ProductImage(testProduct);
		byte[] bytes = image.getBytes();

		Assert.assertNull("should be null - bean/name set but no file found", bytes);

		Assert.assertEquals("should have description set to testTitle given", testTitle, image.
				getDescription());
		Assert.assertNull("should have no mime type set", image.getMimeType());
	}

	/**
	 * Test constructor - bean not null, name not null, name/file found.
	 */
	@Test
	public void testConstructorBytesFound() {
		final int testProductId = 7;
		final String testTitle = "title";
		final String testName = "dog.gif"; // this file DOES exist in the image directory
		final String testDescription = "description";
		final String expectedMimeType = "image/gif";

		ProductBean testProduct = new ProductBean(testProductId, testTitle, testName,
				testDescription);
		ProductImage image = new ProductImage(testProduct);
		byte[] bytes = image.getBytes();

		Assert.assertNotNull("should have found file loaded bytes", bytes);
		Assert.assertTrue("should have bytes found", bytes.length > 0);

		Assert.assertEquals("should have description set to testTitle given", testTitle, image.
				getDescription());
		Assert.assertEquals("should have no mime type set", expectedMimeType, image.getMimeType());
	}

	/**
	 * Test setMimeType.
	 */
	@Test
	public void testSetMimeType() {
		final String testMimeType = "text/html";

		ProductImage image = new ProductImage(new ProductBean());
		image.setMimeType(testMimeType);

		Assert.assertEquals("should return mime type set", testMimeType, image.getMimeType());
	}

	/**
	 * Test setSize.
	 */
	@Test
	public void testSetSize() {
		final Dimension testDim = new Dimension(20, 40);

		ProductImage image = new ProductImage(new ProductBean());
		image.setSize(testDim);

		Assert.assertEquals("should return size set", testDim, image.getSize());
	}

	/**
	 * Test setDescription.
	 */
	@Test
	public void testSetDescription() {
		final String testDescription = "this is a description of itself";

		ProductImage image = new ProductImage(new ProductBean());
		image.setDescription(testDescription);

		Assert.assertEquals("should returnd description set", testDescription, image.
				getDescription());
	}
}

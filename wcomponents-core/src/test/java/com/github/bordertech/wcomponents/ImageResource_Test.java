package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.StreamUtil;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * ImageResource - Unit tests for {@link ImageResource}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ImageResource_Test {

	/**
	 * The test image to use.
	 */
	private static final String TEST_IMAGE = "/content/gif.gif";

	@Test
	public void testResourceConstructor() throws IOException {
		ImageResource res = new ImageResource(TEST_IMAGE);
		Assert.assertNotNull("Description should not be null", res.getDescription());
		Assert.assertNull("Size should be null", res.getSize());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_IMAGE));
		Assert.assertTrue("Incorrect image data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testResourceDescriptionConstructor() throws IOException {
		final String desc = "ImageResource_Test.testResourceDescriptionConstructor.desc";

		ImageResource res = new ImageResource(TEST_IMAGE, desc);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
		Assert.assertNull("Size should be null", res.getSize());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_IMAGE));
		Assert.assertTrue("Incorrect image data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testResourceDescriptionSizeConstructor() throws IOException {
		final String desc = "ImageResource_Test.testResourceDescriptionConstructor.desc";
		final Dimension size = new Dimension(123, 456);

		ImageResource res = new ImageResource(TEST_IMAGE, desc, size);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
		Assert.assertEquals("Incorrect size", size, res.getSize());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_IMAGE));
		Assert.assertTrue("Incorrect image data", Arrays.equals(expected, res.getBytes()));
	}
}

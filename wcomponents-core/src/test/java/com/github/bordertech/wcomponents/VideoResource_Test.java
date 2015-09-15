package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.StreamUtil;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * VideoResource - Unit tests for {@link VideoResource}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class VideoResource_Test {

	/**
	 * The test video to use.
	 */
	private static final String TEST_VIDEO = "/content/ogg.ogg";

	@Test
	public void testResourceConstructor() throws IOException {
		VideoResource res = new VideoResource(TEST_VIDEO);
		Assert.assertNotNull("Description should not be null", res.getDescription());
		Assert.assertNull("Size should be null", res.getSize());
		Assert.assertEquals("Duration should be zero", 0, res.getDuration());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_VIDEO));
		Assert.assertTrue("Incorrect video data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testResourceDescriptionConstructor() throws IOException {
		final String desc = "VideoResource_Test.testResourceDescriptionConstructor.desc";

		VideoResource res = new VideoResource(TEST_VIDEO, desc);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
		Assert.assertNull("Size should be null", res.getSize());
		Assert.assertEquals("Duration should be zero", 0, res.getDuration());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_VIDEO));
		Assert.assertTrue("Incorrect video data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testResourceDescriptionSizeConstructor() throws IOException {
		final String desc = "VideoResource_Test.testResourceDescriptionSizeConstructor.desc";
		final Dimension size = new Dimension(123, 456);

		VideoResource res = new VideoResource(TEST_VIDEO, desc, size);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
		Assert.assertEquals("Incorrect size", size, res.getSize());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_VIDEO));
		Assert.assertTrue("Incorrect video data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testSetDuration() throws IOException {
		final int duration = 1234;

		VideoResource res = new VideoResource(TEST_VIDEO);
		res.setDuration(duration);
		Assert.assertEquals("Incorrect duration", duration, res.getDuration());
	}
}

package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StreamUtil;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * InternalResource - Unit tests for {@link InternalResource}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class InternalResource_Test {

	/**
	 * The test internal to use.
	 */
	private static final String TEST_RESOURCE = "/content/gif.gif";

	@Test
	public void testGetDesc() {
		final String desc = "InternalResource_Test.testResourceDescriptionConstructor.desc";

		InternalResource res = new InternalResource(TEST_RESOURCE, desc);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
	}

	@Test
	public void testGetBytes() throws IOException {
		InternalResource res = new InternalResource(TEST_RESOURCE, "dummy");
		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_RESOURCE));
		Assert.assertTrue("Incorrect internal data", Arrays.equals(expected, res.getBytes()));

		// Incorrect resource should log an error and return empty data
		res = new InternalResource("non-existant.file", "dummy");
		Assert.assertTrue("Incorrect internal data", Arrays.equals(new byte[0], res.getBytes()));
	}

	@Test
	public void testGetStream() throws IOException {
		InternalResource res = new InternalResource(TEST_RESOURCE, "dummy");
		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_RESOURCE));
		byte[] actual = StreamUtil.getBytes(res.getStream());
		Assert.assertTrue("Incorrect internal data", Arrays.equals(expected, actual));
	}

	@Test
	public void testGetMimeType() {
		InternalResource res = new InternalResource(TEST_RESOURCE, "dummy");

		String expected = Config.getInstance().getString("bordertech.wcomponents.mimeType.gif");
		Assert.assertEquals("Incorrect mime type", expected, res.getMimeType());
	}
}

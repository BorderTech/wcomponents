package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.StreamUtil;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * AudioResource - Unit tests for {@link AudioResource}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AudioResource_Test {

	/**
	 * The test Audio to use.
	 */
	private static final String TEST_AUDIO = "/content/ogg.ogg";

	@Test
	public void testResourceConstructor() throws IOException {
		AudioResource res = new AudioResource(TEST_AUDIO);
		Assert.assertNotNull("Description should not be null", res.getDescription());
		Assert.assertEquals("Duration should be zero", 0, res.getDuration());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_AUDIO));
		Assert.assertTrue("Incorrect audio data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testResourceDescriptionConstructor() throws IOException {
		final String desc = "AudioResource_Test.testResourceDescriptionConstructor.desc";

		AudioResource res = new AudioResource(TEST_AUDIO, desc);
		Assert.assertEquals("Incorrect description", desc, res.getDescription());
		Assert.assertEquals("Duration should be zero", 0, res.getDuration());

		byte[] expected = StreamUtil.getBytes(getClass().getResourceAsStream(TEST_AUDIO));
		Assert.assertTrue("Incorrect audio data", Arrays.equals(expected, res.getBytes()));
	}

	@Test
	public void testSetDuration() throws IOException {
		final int duration = 1234;

		AudioResource res = new AudioResource(TEST_AUDIO);
		res.setDuration(duration);
		Assert.assertEquals("Incorrect duration", duration, res.getDuration());
	}
}

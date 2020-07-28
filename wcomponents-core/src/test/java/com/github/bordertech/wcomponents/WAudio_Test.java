package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WAudio}.
 */
public class WAudio_Test extends AbstractWComponentTestCase {

	/**
	 * The character encoding to use when converting Strings to/from byte arrays.
	 */
	private static final String CHAR_ENCODING = "UTF-8";

	@Test
	public void testDefaultConstructor() {
		WAudio audio = new WAudio();
		Assert.assertNull("Should not have audio by default", audio.getAudio());
	}

	@Test
	public void testAudioConstructor() {
		Audio clip = new MockAudio();

		WAudio audio = new WAudio(clip);
		Assert.assertNotNull("Audio should not be null", audio.getAudio());
		Assert.assertEquals("Incorrect number of audio clips", 1, audio.getAudio().length);
		Assert.assertSame("Incorrect default audio", clip, audio.getAudio()[0]);
	}

	@Test
	public void testGetAudio() {
		Audio clip1 = new MockAudio();
		Audio clip2 = new MockAudio();

		// Test default audio
		WAudio audio = new WAudio();
		audio.setAudio(new Audio[]{clip1});
		Assert.assertNotNull("Audio should not be null", audio.getAudio());
		Assert.assertEquals("Incorrect number of audio clips", 1, audio.getAudio().length);
		Assert.assertSame("Incorrect default audio", clip1, audio.getAudio()[0]);

		// Test setting audio per user
		audio.setLocked(true);
		setActiveContext(createUIContext());

		audio.setAudio(clip2);
		Assert.assertEquals("Incorrect number of default audio clips", 1, audio.getAudio().length);

		Assert.assertNotNull("Session 1 audio should not be null", audio.getAudio());
		Assert.assertEquals("Incorrect number of session 1 audio clips", 1, audio.getAudio().length);
		Assert.assertSame("Incorrect session 1 audio", clip2, audio.getAudio()[0]);

		resetContext();
		Assert.assertEquals("Incorrect number of default audio clips", 1, audio.getAudio().length);
		Assert.assertSame("Incorrect default audio", clip1, audio.getAudio()[0]);
	}

	@Test
	public void testHandleRequest()
			throws IOException {
		MockAudio clip1 = new MockAudio();
		clip1.setBytes("WAudio_Test.testHandleRequest.one".getBytes(CHAR_ENCODING));

		MockAudio clip2 = new MockAudio();
		clip2.setBytes("WAudio_Test.testHandleRequest.two".getBytes(CHAR_ENCODING));

		WAudio audio = new WAudio(new Audio[]{clip1, clip2});
		MockRequest request = new MockRequest();
		setActiveContext(createUIContext());

		// Should not do anything when target is not present
		audio.handleRequest(request);

		try {
			request.setParameter(Environment.TARGET_ID, audio.getTargetId());
			request.setParameter("WAudio.index", "0");
			audio.handleRequest(request);

			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(clip1.getBytes(),
					CHAR_ENCODING), output);
			Assert.assertFalse("Cache flag should not be set", escape.isCacheable());
			Assert.assertEquals("Response should have header set for no caching",
					ConfigurationProperties.RESPONSE_DEFAULT_NO_CACHE_SETTINGS,
					response.getHeaders().get("Cache-Control"));
		}

		// Test Cached Response
		audio.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			request.setParameter("WAudio.index", "1");
			audio.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(clip2.getBytes(),
					CHAR_ENCODING), output);
			Assert.assertTrue("Cache flag should be set", escape.isCacheable());
			Assert
					.assertEquals("Response should have header set for caching",
							ConfigurationProperties.RESPONSE_DEFAULT_CACHE_SETTINGS, response.getHeaders().
							get("Cache-Control"));

		}
	}

	@Test
	public void testAltTextAccessors() {
		String defaultText = "WAudio_Test.testGetAltText.defaultText";
		String userText = "WAudio_Test.testGetAltText.userText";
		assertAccessorsCorrect(new WAudio(), WAudio::getAltText, WAudio::setAltText, null, defaultText, userText);
	}

	@Test
	public void testLoopAccessors() {
		assertAccessorsCorrect(new WAudio(), WAudio::isLoop, WAudio::setLoop, false, true, false);
	}

	@Test
	public void testAutoplayAccessors() {
		assertAccessorsCorrect(new WAudio(), WAudio::isAutoplay, WAudio::setAutoplay, false, true, false);
	}

	@Test
	public void testGetControls() {
		WAudio audio = new WAudio();

		Assert.assertNull("Audio should have default controls by default", audio.getControls());

		// Set simple control - shared
		audio.setControls(WAudio.Controls.ALL);
		Assert.assertNotNull("Audio should have controls after setControls", audio.getControls());
		Assert.assertEquals("Incorrect default controls", WAudio.Controls.ALL, audio.getControls());
		Assert.assertTrue(audio.isRenderControls());

		// Set to null - shared
		audio.setControls(null);
		Assert.assertNull("Audio should not have controls", audio.getControls());
		Assert.assertTrue(audio.isRenderControls());

		// Set simple control - session
		audio.setLocked(true);
		setActiveContext(createUIContext());
		audio.setControls(WAudio.Controls.PLAY_PAUSE);
		Assert.assertNotNull("Audio should have controls for affected context", audio.getControls());
		Assert.assertEquals("Incorrect controls for affected context", WAudio.Controls.PLAY_PAUSE, audio.getControls());

		resetContext();
		Assert.assertNull("Audio should not have controls for other contexts", audio.getControls());
	}

	@Test
	public void testRenderControlsAccessors() {
		assertAccessorsCorrect(new WAudio(), WAudio::isRenderControls, WAudio::setRenderControls, true, false, true);
	}

	@Test
	public void testSetControlsAffectsRenderControls() {
		WAudio audio = new WAudio();
		Assert.assertTrue(audio.isRenderControls());

		for (WAudio.Controls c : WAudio.Controls.values()) {
			audio.setControls(c);
			Assert.assertEquals(audio.isRenderControls(), c != WAudio.Controls.NONE);
		}

		// null is not a magic equivalent of NONE
		audio.setControls(WAudio.Controls.NONE);
		Assert.assertFalse(audio.isRenderControls());
		audio.setControls(null);
		Assert.assertTrue(audio.isRenderControls());
	}

	@Test
	public void testDisabledAccessors() {
		assertAccessorsCorrect(new WAudio(), WAudio::isDisabled, WAudio::setDisabled, false, true, false);
	}

	@Test
	public void testPreloadAccessors() {
		WAudio.Preload preload1 = WAudio.Preload.AUTO;
		WAudio.Preload preload2 = WAudio.Preload.META_DATA;
		WAudio audio = new WAudio();
		assertAccessorsCorrect(audio, WAudio::getPreload, WAudio::setPreload, WAudio.Preload.NONE, preload1, preload2);

		resetContext();
		Assert.assertSame("Incorrect default preload", preload1, audio.getPreload());
	}

	@Test
	public void testMediaGroupAccessors() {
		String mediaGroup = "WAudio_Test.testGetMediaGroup.mediaGroup";
		String mediaGroup2 = "WAudio_Test.testGetMediaGroup.mediaGroup";

		assertAccessorsCorrect(new WAudio(), WAudio::getMediaGroup, WAudio::setMediaGroup, null, mediaGroup, mediaGroup2);
	}

	@Test
	public void testCacheKeyAccessors() {
		String cacheKey1 = "WAudio_Test.testGetCacheKey.cacheKey1";
		String cacheKey2 = "WAudio_Test.testGetCacheKey.cacheKey2";

		assertAccessorsCorrect(new WAudio(), WAudio::getCacheKey, WAudio::setCacheKey, null, cacheKey1, cacheKey2);
	}

	@Test
	public void testMutedAccessors() {
		assertAccessorsCorrect(new WAudio(), WAudio::isMuted, WAudio::setMuted, false, true, false);

	}
}

package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WAudio}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
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
					ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
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
							ResponseCacheInterceptor.DEFAULT_CACHE_SETTINGS, response.getHeaders().
							get("Cache-Control"));

		}
	}

	@Test
	public void testGetAltText() {
		WAudio audio = new WAudio();
		Assert.assertNull("Should not have alt text set by default", audio.getAltText());

		String defaultText = "WAudio_Test.testGetAltText.defaultText";
		String userText = "WAudio_Test.testGetAltText.userText";
		audio.setAltText(defaultText);

		Assert.assertEquals("Incorrect default alt text", defaultText, audio.getAltText());

		// Set test for a users session
		audio.setLocked(true);
		setActiveContext(createUIContext());
		audio.setAltText(userText);
		Assert.assertEquals("User session should have session alt text", userText, audio.
				getAltText());

		resetContext();
		Assert.assertEquals("Other sessions should have default alt text", defaultText, audio.
				getAltText());

		//Test nulls
		audio.setAltText("");
		Assert.assertEquals("Alt text should be empty string", "", audio.getAltText());
		audio.setAltText(null);
		Assert.assertNull("Alt text should be null", audio.getAltText());
	}

	@Test
	public void testGetLoop() {
		WAudio audio = new WAudio();
		audio.setLocked(true);

		Assert.assertFalse("Audio should not loop by default", audio.isLoop());

		setActiveContext(createUIContext());
		audio.setLoop(true);
		Assert.assertTrue("Audio should loop for affected context", audio.isLoop());

		resetContext();
		Assert.assertFalse("Audio should not loop for other contexts", audio.isLoop());

		audio = new WAudio();
		audio.setLoop(true);
		audio.setLocked(true);

		Assert.assertTrue("Audio should loop by default", audio.isLoop());
		Assert.assertTrue("Audio should loop by default", audio.isLoop());

		setActiveContext(createUIContext());
		audio.setLoop(false);
		Assert.assertFalse("Audio should not loop for affected context", audio.isLoop());

		resetContext();
		Assert.assertTrue("Audio should loop for other contexts", audio.isLoop());
	}

	@Test
	public void testGetAutoplay() {
		WAudio audio = new WAudio();
		audio.setLocked(true);

		Assert.assertFalse("Audio should not auto-play by default", audio.isAutoplay());

		setActiveContext(createUIContext());
		audio.setAutoplay(true);
		Assert.assertTrue("Audio should auto-play for affected context", audio.isAutoplay());

		resetContext();
		Assert.assertFalse("Audio should not auto-play for other contexts", audio.isAutoplay());

		audio = new WAudio();
		audio.setAutoplay(true);
		audio.setLocked(true);

		Assert.assertTrue("Audio should auto-play by default", audio.isAutoplay());
		Assert.assertTrue("Audio should auto-play by default", audio.isAutoplay());

		setActiveContext(createUIContext());
		audio.setAutoplay(false);
		Assert.assertFalse("Audio should not auto-play for affected context", audio.isAutoplay());

		resetContext();
		Assert.assertTrue("Audio should auto-play for other contexts", audio.isAutoplay());
	}

	@Test
	public void testGetControls() {
		WAudio audio = new WAudio();

		Assert.assertNull("Audio should have default controls by default", audio.getControls());

		// Set simple control - shared
		audio.setControls(WAudio.Controls.ALL);
		Assert.assertNotNull("Audio should have controls after setControls", audio.getControls());
		Assert.assertEquals("Incorrect default controls", WAudio.Controls.ALL, audio.getControls());

		// Set to null - shared
		audio.setControls(null);
		Assert.assertNull("Audio should not have controls", audio.getControls());

		// Set simple control - session
		audio.setLocked(true);
		setActiveContext(createUIContext());
		audio.setControls(WAudio.Controls.PLAY_PAUSE);
		Assert.assertNotNull("Audio should have controls for affected context", audio.getControls());
		Assert.assertEquals("Incorrect controls for affected context", WAudio.Controls.PLAY_PAUSE,
				audio.getControls());

		resetContext();
		Assert.assertNull("Audio should not have controls for other contexts", audio.getControls());
	}

	@Test
	public void testIsDisabled() {
		WAudio audio = new WAudio();
		audio.setLocked(true);

		Assert.assertFalse("Audio should not be disabled by default", audio.isDisabled());

		setActiveContext(createUIContext());
		audio.setDisabled(true);
		Assert.assertTrue("Audio should be disabled for affected context", audio.isDisabled());

		resetContext();
		Assert.assertFalse("Audio should not be disabled for other contexts", audio.isDisabled());

		audio = new WAudio();
		audio.setDisabled(true);
		audio.setLocked(true);

		Assert.assertTrue("Audio should be disabled by default", audio.isDisabled());
		Assert.assertTrue("Audio should be disabled by default", audio.isDisabled());

		setActiveContext(createUIContext());
		audio.setDisabled(false);
		Assert.assertFalse("Audio should not be disabled for affected context", audio.isDisabled());

		resetContext();
		Assert.assertTrue("Audio should be disabled for other contexts", audio.isDisabled());
	}

	@Test
	public void testGetPreload() {
		WAudio.Preload preload1 = WAudio.Preload.AUTO;
		WAudio.Preload preload2 = WAudio.Preload.META_DATA;

		// Test default preload
		WAudio audio = new WAudio();
		Assert.assertEquals("Default preload should be NONE", WAudio.Preload.NONE, audio.
				getPreload());

		// Test set default preload
		audio.setPreload(preload1);
		Assert.assertEquals("Incorrect default preload", preload1, audio.getPreload());

		// Test setting preload per user
		audio.setLocked(true);
		setActiveContext(createUIContext());
		audio.setPreload(preload2);
		Assert.assertSame("Incorrect session 1 preload", preload2, audio.getPreload());

		resetContext();
		Assert.assertSame("Incorrect default preload", preload1, audio.getPreload());
	}

	@Test
	public void testGetMediaGroup() {
		String mediaGroup = "WAudio_Test.testGetMediaGroup.mediaGroup";

		WAudio audio = new WAudio();
		Assert.assertNull("Should not have a media group by default", audio.getMediaGroup());

		audio.setMediaGroup(mediaGroup);
		Assert.assertEquals("Incorrect media group", mediaGroup, audio.getMediaGroup());
	}

	@Test
	public void testGetCacheKey() {
		String cacheKey1 = "WAudio_Test.testGetCacheKey.cacheKey1";
		String cacheKey2 = "WAudio_Test.testGetCacheKey.cacheKey2";

		WAudio audio = new WAudio();
		Assert.assertNull("Should not have a cache key by default", audio.getCacheKey());

		audio.setCacheKey(cacheKey1);
		Assert.assertEquals("Incorrect cache key", cacheKey1, audio.getCacheKey());

		audio.setLocked(true);
		setActiveContext(createUIContext());
		audio.setCacheKey(cacheKey2);
		Assert.assertEquals("Incorrect session cache key", cacheKey2, audio.getCacheKey());

		resetContext();
		Assert.assertEquals("Incorrect default cache key", cacheKey1, audio.getCacheKey());
	}
}

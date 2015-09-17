package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WVideo}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideo_Test extends AbstractWComponentTestCase {

	/**
	 * The character encoding to use when converting Strings to/from byte arrays.
	 */
	private static final String CHAR_ENCODING = "UTF-8";

	@Test
	public void testDefaultConstructor() {
		WVideo video = new WVideo();
		Assert.assertNull("Should not have video by default", video.getVideo());
	}

	@Test
	public void testVideoConstructor() {
		Video clip = new MockVideo();

		WVideo video = new WVideo(clip);
		Assert.assertNotNull("Video should not be null", video.getVideo());
		Assert.assertEquals("Incorrect number of video clips", 1, video.getVideo().length);
		Assert.assertSame("Incorrect default audio", clip, video.getVideo()[0]);
	}

	@Test
	public void testGetVideo() {
		Video clip1 = new MockVideo();
		Video clip2 = new MockVideo();

		// Test default audio
		WVideo video = new WVideo();
		video.setVideo(clip1);
		Assert.assertNotNull("Video should not be null", video.getVideo());
		Assert.assertEquals("Incorrect number of video clips", 1, video.getVideo().length);
		Assert.assertSame("Incorrect default audio", clip1, video.getVideo()[0]);

		// Test setting video per user
		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setVideo(new Video[]{clip2});
		Assert.assertNotNull("Video should not be null", video.getVideo());
		Assert.assertEquals("Incorrect number of video clips", 1, video.getVideo().length);
		Assert.assertSame("Incorrect default video", clip2, video.getVideo()[0]);

		resetContext();
		Assert.assertNotNull("Default video should not be null", video.getVideo());
		Assert.assertEquals("Incorrect number of video clips", 1, video.getVideo().length);
		Assert.assertSame("Default video should not have changed", clip1, video.getVideo()[0]);
	}

	@Test
	public void testHandleVideoRequest()
			throws IOException {
		MockVideo clip1 = new MockVideo();
		clip1.setBytes("WVideo_Test.testHandleVideoRequest.one".getBytes(CHAR_ENCODING));

		MockVideo clip2 = new MockVideo();
		clip2.setBytes("WVideo_Test.testHandleVideoRequest.two".getBytes(CHAR_ENCODING));

		WVideo video = new WVideo(new Video[]{clip1, clip2});
		video.setLocked(true);

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Should not do anything when target is not present
		video.handleRequest(request);

		try {
			request.setParameter(Environment.TARGET_ID, video.getTargetId());
			request.setParameter("WVideo.videoIndex", "0");
			video.handleRequest(request);

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
		video.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			request.setParameter("WVideo.videoIndex", "1");
			video.handleRequest(request);
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
	public void testHandleTrackRequest()
			throws IOException {
		MockTrack track1 = new MockTrack();
		track1.setBytes("WVideo_Test.testHandleTrackRequest.one".getBytes(CHAR_ENCODING));

		MockTrack track2 = new MockTrack();
		track2.setBytes("WVideo_Test.testHandleTrackRequest.two".getBytes(CHAR_ENCODING));

		WVideo video = new WVideo(new MockVideo());
		video.setTracks(new Track[]{track1, track2});

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Should not do anything when target is not present
		video.handleRequest(request);

		try {
			request.setParameter(Environment.TARGET_ID, video.getTargetId());
			request.setParameter("WVideo.trackIndex", "0");
			video.handleRequest(request);

			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(track1.getBytes(),
					CHAR_ENCODING), output);
			Assert.assertFalse("Cache flag should not be set", escape.isCacheable());
			Assert.assertEquals("Response should have header set for no caching",
					ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
					response.getHeaders().get("Cache-Control"));
		}

		// Test Cached Response
		video.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			request.setParameter("WVideo.trackIndex", "1");
			video.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(track2.getBytes(),
					CHAR_ENCODING), output);
			Assert.assertTrue("Cache flag should be set", escape.isCacheable());
			Assert
					.assertEquals("Response should have header set for caching",
							ResponseCacheInterceptor.DEFAULT_CACHE_SETTINGS, response.getHeaders().
							get("Cache-Control"));
		}
	}

	@Test
	public void testHandlePosterRequest()
			throws IOException {
		MockImage poster = new MockImage();
		poster.setBytes("WVideo_Test.testHandlePosterRequest.one".getBytes(CHAR_ENCODING));

		WVideo video = new WVideo(new MockVideo());
		video.setPoster(poster);

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Should not do anything when target is not present
		video.handleRequest(request);

		try {
			request.setParameter(Environment.TARGET_ID, video.getTargetId());
			request.setParameter("WVideo.poster", "x");
			video.handleRequest(request);

			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(poster.getBytes(),
					CHAR_ENCODING), output);
			Assert.assertFalse("Cache flag should not be set", escape.isCacheable());
			Assert.assertEquals("Response should have header set for no caching",
					ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
					response.getHeaders().get("Cache-Control"));
		}

		// Test Cached Response
		video.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			request.setParameter("WVideo.poster", "x");
			video.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(poster.getBytes(),
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
		WVideo video = new WVideo();
		String defaultText = "WVideo_Test.testGetAltText.defaultText";
		String userText = "WVideo_Test.testGetAltText.userText";
		video.setAltText(defaultText);

		Assert.assertEquals("Incorrect default alt text", defaultText, video.getAltText());

		// Set test for a users session
		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setAltText(userText);
		Assert.assertEquals("User session should have session alt text", userText, video.
				getAltText());

		resetContext();
		Assert.assertEquals("Other sessions should have default alt text", defaultText, video.
				getAltText());

		//Test nulls
		video.setAltText("");
		Assert.assertEquals("Alt text should be empty string", "", video.getAltText());
		video.setAltText(null);
		Assert.assertNull("Alt text should be null", video.getAltText());
		video.setAltText(null);
		Assert.assertNull("Alt text should be null", video.getAltText());
	}

	@Test
	public void testGetLoop() {
		WVideo video = new WVideo();
		Assert.assertFalse("Video should not loop by default", video.isLoop());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setLoop(true);
		Assert.assertTrue("Video should loop for affected context", video.isLoop());

		resetContext();
		Assert.assertFalse("Video should not loop for other contexts", video.isLoop());

		video = new WVideo();
		video.setLoop(true);
		Assert.assertTrue("Video should loop by default", video.isLoop());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setLoop(false);
		Assert.assertFalse("Video should not loop for affected context", video.isLoop());

		resetContext();
		Assert.assertTrue("Video should loop for other contexts", video.isLoop());
	}

	@Test
	public void testGetAutoplay() {
		WVideo video = new WVideo();
		Assert.assertFalse("Video should not auto-play by default", video.isAutoplay());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setAutoplay(true);
		Assert.assertTrue("Video should auto-play for affected context", video.isAutoplay());

		resetContext();
		Assert.assertFalse("Video should not auto-play for other contexts", video.isAutoplay());

		video = new WVideo();
		video.setAutoplay(true);
		Assert.assertTrue("Video should auto-play by default", video.isAutoplay());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setAutoplay(false);
		Assert.assertFalse("Video should not auto-play for affected context", video.isAutoplay());

		resetContext();
		Assert.assertTrue("Video should auto-play for other contexts", video.isAutoplay());
	}

	@Test
	public void testGetControls() {
		WVideo video = new WVideo();

		Assert.assertNull("Video should have default controls by default", video.getControls());

		// Set simple control - shared
		video.setControls(WVideo.Controls.ALL);
		Assert.assertNotNull("Video should have controls after setControls", video.getControls());
		Assert.assertEquals("Incorrect default controls", WVideo.Controls.ALL, video.getControls());

		// Set to null - shared
		video.setControls(null);
		Assert.assertNull("Video should not have controls", video.getControls());

		// Set simple control - session
		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setControls(WVideo.Controls.PLAY_PAUSE);
		Assert.assertNotNull("Video should have controls for affected context", video.getControls());
		Assert.assertEquals("Incorrect controls for affected context", WVideo.Controls.PLAY_PAUSE,
				video.getControls());

		resetContext();
		Assert.assertNull("Video should not have controls for other contexts", video.getControls());
	}

	@Test
	public void testIsDisabled() {
		WVideo video = new WVideo();
		Assert.assertFalse("Video should not be disabled by default", video.isDisabled());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setDisabled(true);
		Assert.assertTrue("Video should be disabled for affected context", video.isDisabled());

		resetContext();
		Assert.assertFalse("Video should not be disabled for other contexts", video.isDisabled());

		video = new WVideo();
		video.setDisabled(true);
		Assert.assertTrue("Video should be disabled by default", video.isDisabled());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setDisabled(false);
		Assert.assertFalse("Video should not be disabled for affected context", video.isDisabled());

		resetContext();
		Assert.assertTrue("Video should be disabled for other contexts", video.isDisabled());
	}

	@Test
	public void testGetPreload() {
		WVideo.Preload preload1 = WVideo.Preload.AUTO;
		WVideo.Preload preload2 = WVideo.Preload.META_DATA;

		// Test default preload
		WVideo video = new WVideo();
		Assert.assertEquals("Default preload should be NONE", WVideo.Preload.NONE, video.
				getPreload());

		// Test set default preload
		video.setPreload(preload1);
		Assert.assertEquals("Incorrect default preload", preload1, video.getPreload());

		// Test setting preload per user
		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setPreload(preload2);
		Assert.assertSame("Incorrect session 1 preload", preload2, video.getPreload());

		resetContext();
		Assert.assertSame("Incorrect default preload", preload1, video.getPreload());
	}

	@Test
	public void testGetCacheKey() {
		String cacheKey1 = "WVideo_Test.testGetCacheKey.cacheKey1";
		String cacheKey2 = "WVideo_Test.testGetCacheKey.cacheKey2";

		WVideo video = new WVideo();
		Assert.assertNull("Should not have a cache key by default", video.getCacheKey());

		video.setCacheKey(cacheKey1);
		Assert.assertEquals("Incorrect cache key", cacheKey1, video.getCacheKey());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setCacheKey(cacheKey2);
		Assert.assertEquals("Incorrect session cache key", cacheKey2, video.getCacheKey());

		resetContext();
		Assert.assertEquals("Incorrect default cache key", cacheKey1, video.getCacheKey());
	}

	@Test
	public void testGetMediaGroup() {
		String mediaGroup = "WVideo_Test.testGetMediaGroup.mediaGroup";

		WVideo video = new WVideo();
		Assert.assertNull("Should not have a media group by default", video.getMediaGroup());

		video.setMediaGroup(mediaGroup);
		Assert.assertEquals("Incorrect media group", mediaGroup, video.getMediaGroup());
	}

	@Test
	public void testGetWidth() {
		WVideo video = new WVideo();
		Assert.assertEquals("Width should default to zero", 0, video.getWidth());

		int width1 = 121;
		int width2 = 122;
		video.setWidth(width1);
		Assert.assertEquals("Width accessors incorrect", width1, video.getWidth());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setWidth(width2);
		Assert.assertEquals("Incorrect width for changed context", width2, video.getWidth());

		resetContext();
		Assert.assertEquals("Default width should not have changed", width1, video.getWidth());
	}

	@Test
	public void testGetHeight() {
		WVideo video = new WVideo();
		Assert.assertEquals("Height should default to zero", 0, video.getWidth());

		int height1 = 121;
		int height2 = 122;
		video.setHeight(height1);
		Assert.assertEquals("Height accessors incorrect", height1, video.getHeight());

		video.setLocked(true);
		setActiveContext(createUIContext());
		video.setHeight(height2);
		Assert.assertEquals("Incorrect height for changed context", height2, video.getHeight());

		resetContext();
		Assert.assertEquals("Default height should not have changed", height1, video.getHeight());
	}
}

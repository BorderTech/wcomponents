package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MockImage;
import com.github.bordertech.wcomponents.MockTrack;
import com.github.bordertech.wcomponents.MockVideo;
import com.github.bordertech.wcomponents.Track;
import com.github.bordertech.wcomponents.Video;
import com.github.bordertech.wcomponents.WVideo;
import java.awt.Dimension;
import java.io.IOException;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WVideoRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideoRenderer_Test extends AbstractWebXmlRendererTestCase {

	private WVideo video;
	private MockVideo mockVideo;

	@Before
	public void setUp() {
		mockVideo = new MockVideo();
		mockVideo.setMimeType("video/mpeg");
		mockVideo.setSize(new Dimension(111, 222));
		video = new WVideo(mockVideo);
		video.setCacheKey("x"); // so that the URIs are consistent
	}

	@Test
	public void testRendererCorrectlyConfigured() {
		WVideo video = new WVideo();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(video) instanceof WVideoRenderer);
	}

	@Test
	public void testDoPaint_noMedia() throws IOException, SAXException, XpathException {
		// Test with no video tracks - should not render
		video = new WVideo();
		assertXpathNotExists("//html:video", video);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		setActiveContext(createUIContext());
		assertXpathExists("//html:video", video);
		assertXpathEvaluatesTo(video.getId(), "//html:video/@id", video);
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
		assertXpathNotExists("//html:video/@autoplay", video);
		assertXpathNotExists("//html:video/@loop", video);
		assertXpathNotExists("//html:video/@muted", video);
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
		assertXpathNotExists("//html:video/@hidden", video);
		assertXpathNotExists("//html:video/@tooltip", video);
		assertXpathNotExists("//html:video/@poster", video);
		assertXpathEvaluatesTo("0", "count(//html:video/html:track)", video);
		assertXpathEvaluatesTo("0", "count(//html:video/html:source)", video);
		assertXpathUrlEvaluatesTo(video.getVideoUrls()[0], "//html:video/@src", video);
	}

	@Test
	public void testDoPaint_multiVideo() throws IOException, SAXException, XpathException {

		MockVideo mv2 = new MockVideo();
		mv2.setMimeType("video/avi");
		mv2.setSize(new Dimension(333, 111));
		video.setVideo(new Video[]{mockVideo, mv2});
		setActiveContext(createUIContext());
		assertXpathEvaluatesTo("2", "count(//html:video/html:source)", video);
		assertXpathUrlEvaluatesTo(video.getVideoUrls()[0], "//html:video/html:source/@src", video);
		assertXpathEvaluatesTo(video.getVideo()[0].getMimeType(), "//html:video/html:source/@type", video);
	}

	// negative tests of members which no longer take part in rendering but cannot (yet) be deleted from API as
	// this would require a braking API change.
	@Test
	public void testDoPaint_altDisabledDurationNotImplemented() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@disabled", video);
		assertXpathNotExists("//html:video/@alt", video);
		assertXpathNotExists("//html:video/@duration", video);

		video.setDisabled(true);
		assertXpathNotExists("//html:video/@disabled", video);
		video.setDisabled(false);
		assertXpathNotExists("//html:video/@disabled", video);

		String altText = "no longer used";
		video.setAltText(altText);
		Assert.assertEquals(video.getAltText(), altText);
		assertXpathNotExists("//html:video/@alt", video);

		mockVideo.setDuration(123);
		assertXpathNotExists("//html:video/@duration", video);
	}

	@Test
	public void testHtmlClass() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
		String addOnClass = "new-class";
		video.setHtmlClass(addOnClass);
		assertXpathEvaluatesTo("wc-video ".concat(addOnClass), "//html:video/@class", video);
	}

	@Test
	public void testHtmlClass_emptyDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
		String addOnClass = "";
		video.setHtmlClass(addOnClass);
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
	}

	@Test
	public void testHtmlClass_nullDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
		video.setHtmlClass((String) null);
		assertXpathEvaluatesTo("wc-video", "//html:video/@class", video);
	}

	@Test
	public void testPreload() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("none", "//html:video/@preload", video);
		video.setPreload(WVideo.Preload.META_DATA);
		assertXpathEvaluatesTo("metadata", "//html:video/@preload", video);
		video.setPreload(WVideo.Preload.AUTO);
		assertXpathNotExists("//html:video/@preload", video);
		video.setPreload(WVideo.Preload.NONE);
		assertXpathEvaluatesTo("none", "//html:video/@preload", video);
	}

	@Test
	public void testPreloadNullIsNone() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("none", "//html:video/@preload", video);
		video.setPreload(WVideo.Preload.META_DATA);
		assertXpathEvaluatesTo("metadata", "//html:video/@preload", video);
		video.setPreload(null);
		assertXpathEvaluatesTo("none", "//html:video/@preload", video);
	}

	@Test
	public void testAutoplay() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@autoplay", video);
		video.setAutoplay(true);
		assertXpathEvaluatesTo("true", "//html:video/@autoplay", video);
		video.setAutoplay(false);
		assertXpathNotExists("//html:video/@autoplay", video);
	}

	@Test
	public void testLoop() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@loop", video);
		video.setLoop(true);
		assertXpathEvaluatesTo("true", "//html:video/@loop", video);
		video.setLoop(false);
		assertXpathNotExists("//html:video/@loop", video);
	}

	@Test
	public void testMuted() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@muted", video);
		video.setMuted(true);
		assertXpathEvaluatesTo("true", "//html:video/@muted", video);
		video.setMuted(false);
		assertXpathNotExists("//html:video/@muted", video);
	}

	@Test
	public void testRenderControls() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
		video.setRenderControls(false);
		assertXpathNotExists("//html:video/@controls", video);
		video.setRenderControls(true);
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
	}


	@Test
	public void testControls() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);

		video.setControls(WVideo.Controls.NONE);
		assertXpathNotExists("//html:video/@controls", video);

		// all other values of controls should show the controls attribute
		for (WVideo.Controls c : WVideo.Controls.values()) {
			if (c == WVideo.Controls.NONE) {
				continue;
			}
			// remove controls using known good method
			video.setRenderControls(false);
			assertXpathNotExists("//html:video/@controls", video);

			video.setControls(c);
			assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
		}

		// explicit null check
		// remove controls using known good method
		video.setRenderControls(false);
		assertXpathNotExists("//html:video/@controls", video);

		video.setControls(null);
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
	}

	// explicit Controls == null check
	@Test
	public void testControls_nullIsNone() throws IOException, SAXException, XpathException {
		// remove controls using known good method
		video.setRenderControls(false);
		assertXpathNotExists("//html:video/@controls", video);

		video.setControls(null);
		assertXpathEvaluatesTo("controls", "//html:video/@controls", video);
	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@hidden", video);
		video.setHidden(true);
		assertXpathEvaluatesTo("hidden", "//html:video/@hidden", video);
		video.setHidden(false);
		assertXpathNotExists("//html:video/@hidden", video);
	}

	@Test
	public void testTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@title", video);
		String title = "some title";
		video.setToolTip(title);
		assertXpathEvaluatesTo(title, "//html:video/@title", video);
	}

	@Test
	public void testTitle_emptyStringRemovesTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@title", video);
		String title = "some title";
		video.setToolTip(title);
		assertXpathEvaluatesTo(title, "//html:video/@title", video);
		video.setToolTip("");
		assertXpathNotExists("//html:video/@title", video);
	}

	@Test
	public void testTitle_nullRemovesTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@title", video);
		String title = "some title";
		video.setToolTip(title);
		assertXpathEvaluatesTo(title, "//html:video/@title", video);
		video.setToolTip(null);
		assertXpathNotExists("//html:video/@title", video);
	}

	@Test
	public void testDimensionsFromResource() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().width), "//html:video/@width", video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().height), "//html:video/@height", video);
	}

	@Test
	public void testDimensionsFromResource_ZeroDimensions() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().width), "//html:video/@width", video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().height), "//html:video/@height", video);
		mockVideo.setSize(new Dimension(0, 0));
		assertXpathNotExists("//html:video/@width", video);
		assertXpathNotExists("//html:video/@width", video);
	}

	@Test
	public void testDimensionsFromResource_NullDimensions() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().width), "//html:video/@width", video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().height), "//html:video/@height", video);
		mockVideo.setSize(null);
		assertXpathNotExists("//html:video/@width", video);
		assertXpathNotExists("//html:video/@width", video);
	}

	@Test
	public void testDimensions_WVideoDimensionWins() throws IOException, SAXException, XpathException {
		int width = mockVideo.getSize().width + 50;
		int height = mockVideo.getSize().height + 50;
		video.setWidth(width);
		video.setHeight(height);
		assertXpathEvaluatesTo(String.valueOf(width), "//html:video/@width", video);
		assertXpathEvaluatesTo(String.valueOf(height), "//html:video/@height", video);
	}

	@Test
	public void testDimensions_ZeroWVideoDimensionLoses() throws IOException, SAXException, XpathException {
		video.setWidth(0);
		video.setHeight(0);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().width), "//html:video/@width", video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().height), "//html:video/@height", video);
	}

	@Test
	public void testPoster() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@poster", video);
		setActiveContext(createUIContext());

		video.setPoster(new MockImage());
		assertXpathUrlEvaluatesTo(video.getPosterUrl(), "//html:video/@poster", video);

		video.setPoster(null);
		assertXpathNotExists("//html:video/@poster", video);
	}

	@Test
	public void testMediagroup() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@mediagroup", video);
		String expected = "media-group";
		video.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:video/@mediagroup", video);
	}

	@Test
	public void testMediagroup_emptyStringRemoves() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@mediagroup", video);
		String expected = "media-group";
		video.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:video/@mediagroup", video);
		video.setMediaGroup("");
		assertXpathNotExists("//html:video/@mediagroup", video);
	}

	@Test
	public void testMediagroup_nullRemoves() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:video/@mediagroup", video);
		String expected = "media-group";
		video.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:video/@mediagroup", video);
		video.setMediaGroup(null);
		assertXpathNotExists("//html:video/@mediagroup", video);
	}

	@Test
	public void testTracks() throws IOException, SAXException, XpathException {
		MockTrack track = new MockTrack();
		track.setLanguage("en");
		track.setDescription("trackDesc");
		track.setKind(Track.Kind.SUBTITLES);
		video.setTracks(new Track[]{track});
		setActiveContext(createUIContext());

		assertXpathEvaluatesTo("1", "count(//html:video/html:track)", video);
		assertXpathEvaluatesTo("en", "//html:video/html:track/@lang", video);
		assertXpathEvaluatesTo("subtitles", "//html:video/html:track/@kind", video);
		assertXpathEvaluatesTo("trackDesc", "//html:video/html:track/@desc", video);
		assertXpathUrlEvaluatesTo(video.getTrackUrls()[0], "//html:video/html:track/@src", video);
		track.setKind(Track.Kind.CAPTIONS);
		assertXpathEvaluatesTo("captions", "//html:video/html:track/@kind", video);
		track.setKind(Track.Kind.DESCRIPTIONS);
		assertXpathEvaluatesTo("descriptions", "//html:video/html:track/@kind", video);
		track.setKind(Track.Kind.CHAPTERS);
		assertXpathEvaluatesTo("chapters", "//html:video/html:track/@kind", video);
		track.setKind(Track.Kind.METADATA);
		assertXpathEvaluatesTo("metadata", "//html:video/html:track/@kind", video);
	}
}

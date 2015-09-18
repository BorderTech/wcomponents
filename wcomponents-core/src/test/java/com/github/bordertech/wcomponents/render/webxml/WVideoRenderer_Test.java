package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MockImage;
import com.github.bordertech.wcomponents.MockTrack;
import com.github.bordertech.wcomponents.MockVideo;
import com.github.bordertech.wcomponents.Track;
import com.github.bordertech.wcomponents.WVideo;
import java.awt.Dimension;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WVideoRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideoRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WVideo video = new WVideo();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(video) instanceof WVideoRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		MockVideo mockVideo = new MockVideo();
		mockVideo.setMimeType("video/mpeg");
		mockVideo.setSize(new Dimension(111, 222));

		// Test with no video tracks - should not render
		WVideo video = new WVideo();
		assertSchemaMatch(video);
		assertXpathNotExists("//ui:video", video);

		// Test with minimal options
		video = new WVideo(mockVideo);
		video.setCacheKey("x"); // so that the URIs are consistent
		setActiveContext(createUIContext());

		assertSchemaMatch(video);
		assertXpathExists("//ui:video", video);
		assertXpathEvaluatesTo(video.getId(), "//ui:video/@id", video);
		assertXpathEvaluatesTo("none", "//ui:video/@preload", video);
		assertXpathNotExists("//ui:video/@alt", video);
		assertXpathNotExists("//ui:video/@autoplay", video);
		assertXpathNotExists("//ui:video/@loop", video);
		assertXpathNotExists("//ui:video/@muted", video);
		assertXpathNotExists("//ui:video/@controls", video);
		assertXpathNotExists("//ui:video/@hidden", video);
		assertXpathNotExists("//ui:video/@disabled", video);
		assertXpathNotExists("//ui:video/@tooltip", video);
		assertXpathNotExists("//ui:video/@width", video);
		assertXpathNotExists("//ui:video/@height", video);
		assertXpathNotExists("//ui:video/@duration", video);
		assertXpathNotExists("//ui:video/@poster", video);
		assertXpathEvaluatesTo("0", "count(//ui:video/ui:track)", video);
		assertXpathEvaluatesTo("1", "count(//ui:video/ui:src)", video);
		assertXpathEvaluatesTo(video.getVideoUrls()[0], "//ui:video/ui:src/@uri", video);
		assertXpathEvaluatesTo(video.getVideo()[0].getMimeType(), "//ui:video/ui:src/@type", video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().width), "//ui:video/ui:src/@width",
				video);
		assertXpathEvaluatesTo(String.valueOf(mockVideo.getSize().height),
				"//ui:video/ui:src/@height", video);

		// Test other options, resetting them after each test
		video.setAltText("altText");
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("altText", "//ui:video/@alt", video);
		video.reset();

		video.setPreload(WVideo.Preload.META_DATA);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("metadata", "//ui:video/@preload", video);
		video.reset();

		video.setAutoplay(true);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("true", "//ui:video/@autoplay", video);
		video.reset();

		video.setLoop(true);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("true", "//ui:video/@loop", video);
		video.reset();

		video.setMuted(true);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("true", "//ui:video/@muted", video);
		video.reset();

		video.setControls(WVideo.Controls.NONE);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("none", "//ui:video/@controls", video);
		video.reset();

		video.setControls(WVideo.Controls.ALL);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("all", "//ui:video/@controls", video);
		video.reset();

		video.setControls(WVideo.Controls.PLAY_PAUSE);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("play", "//ui:video/@controls", video);
		video.reset();

		video.setControls(WVideo.Controls.DEFAULT);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("default", "//ui:video/@controls", video);
		video.reset();

		setFlag(video, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("true", "//ui:video/@hidden", video);
		video.reset();

		video.setDisabled(true);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("true", "//ui:video/@disabled", video);
		video.reset();

		video.setToolTip("toolTip");
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("toolTip", "//ui:video/@toolTip", video);
		video.reset();

		video.setWidth(123);
		video.setHeight(456);
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("123", "//ui:video/@width", video);
		assertXpathEvaluatesTo("456", "//ui:video/@height", video);
		video.reset();

		video.setPoster(new MockImage());
		assertXpathEvaluatesTo(video.getPosterUrl(), "//ui:video/@poster", video);
		video.reset();

		MockTrack track = new MockTrack();
		track.setLanguage("en");
		track.setDescription("trackDesc");
		track.setKind(Track.Kind.SUBTITLES);
		video.setTracks(new Track[]{track});
		assertSchemaMatch(video);
		assertXpathEvaluatesTo("1", "count(//ui:video/ui:track)", video);
		assertXpathEvaluatesTo("en", "//ui:video/ui:track/@lang", video);
		assertXpathEvaluatesTo("subtitles", "//ui:video/ui:track/@kind", video);
		assertXpathEvaluatesTo("trackDesc", "//ui:video/ui:track/@desc", video);
		assertXpathEvaluatesTo(video.getTrackUrls()[0], "//ui:video/ui:track/@src", video);
		track.setKind(Track.Kind.CAPTIONS);
		assertXpathEvaluatesTo("captions", "//ui:video/ui:track/@kind", video);
		track.setKind(Track.Kind.DESCRIPTIONS);
		assertXpathEvaluatesTo("descriptions", "//ui:video/ui:track/@kind", video);
		track.setKind(Track.Kind.CHAPTERS);
		assertXpathEvaluatesTo("chapters", "//ui:video/ui:track/@kind", video);
		track.setKind(Track.Kind.METADATA);
		assertXpathEvaluatesTo("metadata", "//ui:video/ui:track/@kind", video);
		video.reset();

		mockVideo.setDuration(123);
		assertXpathEvaluatesTo("123", "//ui:video/@duration", video);
	}
}

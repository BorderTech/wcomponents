package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MockAudio;
import com.github.bordertech.wcomponents.WAudio;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WAudioRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAudioRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WAudio audio = new WAudio();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(audio) instanceof WAudioRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		MockAudio mockAudio = new MockAudio();
		mockAudio.setMimeType("audio/basic");

		// Test with no audio tracks - should not render
		WAudio audio = new WAudio();

		assertSchemaMatch(audio);
		assertXpathNotExists("//ui:audio", audio);

		// Test with minimal options
		audio = new WAudio(mockAudio);
		audio.setCacheKey("x"); // so that the URIs are consistent
		setActiveContext(createUIContext());

		assertSchemaMatch(audio);
		assertXpathExists("//ui:audio", audio);
		assertXpathEvaluatesTo(audio.getId(), "//ui:audio/@id", audio);
		assertXpathEvaluatesTo("none", "//ui:audio/@preload", audio);
		assertXpathNotExists("//ui:audio/@alt", audio);
		assertXpathNotExists("//ui:audio/@autoplay", audio);
		assertXpathNotExists("//ui:audio/@loop", audio);
		assertXpathNotExists("//ui:audio/@muted", audio);
		assertXpathNotExists("//ui:audio/@controls", audio);
		assertXpathNotExists("//ui:audio/@hidden", audio);
		assertXpathNotExists("//ui:audio/@disabled", audio);
		assertXpathNotExists("//ui:audio/@tooltip", audio);
		assertXpathNotExists("//ui:audio/@duration", audio);
		assertXpathEvaluatesTo("1", "count(//ui:audio/ui:src)", audio);
		assertXpathEvaluatesTo(audio.getAudioUrls()[0], "//ui:audio/ui:src/@uri", audio);
		assertXpathEvaluatesTo(audio.getAudio()[0].getMimeType(), "//ui:audio/ui:src/@type", audio);

		// Test other options, resetting them after each test
		audio.setAltText("altText");
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("altText", "//ui:audio/@alt", audio);
		audio.reset();

		audio.setPreload(WAudio.Preload.META_DATA);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("metadata", "//ui:audio/@preload", audio);
		audio.reset();

		audio.setAutoplay(true);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("true", "//ui:audio/@autoplay", audio);
		audio.reset();

		audio.setLoop(true);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("true", "//ui:audio/@loop", audio);
		audio.reset();

		audio.setControls(WAudio.Controls.NONE);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("none", "//ui:audio/@controls", audio);
		audio.reset();

		audio.setControls(WAudio.Controls.ALL);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("all", "//ui:audio/@controls", audio);
		audio.reset();

		audio.setControls(WAudio.Controls.PLAY_PAUSE);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("play", "//ui:audio/@controls", audio);
		audio.reset();

		audio.setControls(WAudio.Controls.DEFAULT);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("default", "//ui:audio/@controls", audio);
		audio.reset();

		setFlag(audio, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("true", "//ui:audio/@hidden", audio);
		audio.reset();

		audio.setDisabled(true);
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("true", "//ui:audio/@disabled", audio);
		audio.reset();

		audio.setToolTip("toolTip");
		assertSchemaMatch(audio);
		assertXpathEvaluatesTo("toolTip", "//ui:audio/@toolTip", audio);
		audio.reset();

		mockAudio.setDuration(123);
		assertXpathEvaluatesTo("123", "//ui:audio/@duration", audio);
	}
}

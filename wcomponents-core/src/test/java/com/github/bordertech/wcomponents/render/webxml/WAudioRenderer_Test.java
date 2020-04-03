package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Audio;
import com.github.bordertech.wcomponents.MockAudio;
import com.github.bordertech.wcomponents.WAudio;
import java.io.IOException;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WAudioRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAudioRenderer_Test extends AbstractWebXmlRendererTestCase {

	private WAudio audio;

	private MockAudio mockAudio;

	@Before
	public void setUp() {
		mockAudio = new MockAudio();
		mockAudio.setMimeType("audio/basic");
		audio = new WAudio(mockAudio);
		audio.setCacheKey("x{}<>"); // so that the URIs are consistent
	}

	@Test
	public void testRendererCorrectlyConfigured() {
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(audio) instanceof WAudioRenderer);
	}

	// Test with no audio tracks - should not render
	@Test
	public void testDoPaint_noAudioNoRender() throws IOException, SAXException, XpathException {
		WAudio emptyAudio = new WAudio();
		assertXpathNotExists("//html:audio", emptyAudio);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		// Test with minimal options
		setActiveContext(createUIContext());

		assertXpathExists("//html:audio", audio);
		assertXpathEvaluatesTo(audio.getId(), "//html:audio/@id", audio);
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
		assertXpathEvaluatesTo("none", "//html:audio/@preload", audio);
		assertXpathNotExists("//html:audio/@autoplay", audio);
		assertXpathNotExists("//html:audio/@loop", audio);
		assertXpathNotExists("//html:audio/@muted", audio);
		assertXpathEvaluatesTo("controls", "//html:audio/@controls", audio);
		assertXpathNotExists("//html:audio/@hidden", audio);
		assertXpathNotExists("//html:audio/@title", audio);
		assertXpathEvaluatesTo("0", "count(//html:audio/html:source)", audio);
		assertXpathEvaluatesTo(audio.getAudioUrls()[0], "//html:audio/@src", audio);
		assertXpathNotExists("//html:audio/@hidden", audio);
	}

	@Test
	public void testPaintMultipleSources() throws IOException, SAXException, XpathException {
		MockAudio mock2 = new MockAudio();
		mock2.setMimeType("audio/ogg");
		audio.setAudio(new Audio[]{mock2, mockAudio});
		setActiveContext(createUIContext());
		assertXpathNotExists("//html:audio/@src", audio);
		assertXpathEvaluatesTo("2", "count(//html:audio/html:source)", audio);
		assertXpathUrlEvaluatesTo(audio.getAudioUrls()[0], "//html:audio/html:source/@src", audio);
		assertXpathEvaluatesTo(audio.getAudio()[0].getMimeType(), "//html:audio/html:source/@type", audio);
	}

	@Test
	public void testHtmlClass() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
		String addOnClass = "new-class";
		audio.setHtmlClass(addOnClass);
		assertXpathEvaluatesTo("wc-audio ".concat(addOnClass), "//html:audio/@class", audio);
	}

	@Test
	public void testHtmlClass_emptyDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
		String addOnClass = "";
		audio.setHtmlClass(addOnClass);
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
	}

	@Test
	public void testHtmlClass_nullDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
		audio.setHtmlClass((String) null);
		assertXpathEvaluatesTo("wc-audio", "//html:audio/@class", audio);
	}

	@Test
	public void testAlt() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@alt", audio);
		// Setting AltText should do nothing
		audio.setAltText("altText");
		assertXpathNotExists("//html:audio/@alt", audio);
	}

	@Test
	public void testPreload() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("none", "//html:audio/@preload", audio);
		audio.setPreload(WAudio.Preload.META_DATA);
		assertXpathEvaluatesTo("metadata", "//html:audio/@preload", audio);
		audio.setPreload(WAudio.Preload.AUTO);
		assertXpathNotExists("//html:audio/@preload", audio);
		audio.setPreload(WAudio.Preload.NONE);
		assertXpathEvaluatesTo("none", "//html:audio/@preload", audio);
	}

	@Test
	public void testAutoplay() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@autoplay", audio);
		audio.setAutoplay(true);
		assertXpathEvaluatesTo("true", "//html:audio/@autoplay", audio);
		audio.setAutoplay(false);
		assertXpathNotExists("//html:audio/@autoplay", audio);
	}

	@Test
	public void testLoop() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@loop", audio);
		audio.setLoop(true);
		assertXpathEvaluatesTo("true", "//html:audio/@loop", audio);
		audio.setLoop(false);
		assertXpathNotExists("//html:audio/@loop", audio);
	}

	@Test
	public void testControls() throws IOException, SAXException, XpathException {
		assertXpathEvaluatesTo("controls", "//html:audio/@controls", audio);
		audio.setRenderControls(false);
		assertXpathNotExists("//html:audio/@controls", audio);
	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@hidden", audio);
		audio.setHidden(true);
		assertXpathEvaluatesTo("hidden", "//html:audio/@hidden", audio);
		audio.setHidden(false);
		assertXpathNotExists("//html:audio/@hidden", audio);
	}

	@Test
	public void testDisabledDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@adisabled", audio);
		audio.setDisabled(true);
		assertXpathNotExists("//html:audio/@adisabled", audio);
	}

	@Test
	public void testTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@title", audio);
		audio.setToolTip("toolTip");
		assertXpathEvaluatesTo("toolTip", "//html:audio/@title", audio);
	}

	@Test
	public void testTitle_emptyStringRemovesTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@title", audio);
		String title = "some title";
		audio.setToolTip(title);
		assertXpathEvaluatesTo(title, "//html:audio/@title", audio);
		audio.setToolTip("");
		assertXpathNotExists("//html:audio/@title", audio);
	}

	@Test
	public void testTitle_nullRemovesTitle() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@title", audio);
		String title = "some title";
		audio.setToolTip(title);
		assertXpathEvaluatesTo(title, "//html:audio/@title", audio);
		audio.setToolTip(null);
		assertXpathNotExists("//html:audio/@title", audio);
	}

	@Test
	public void testDurationDoesNothing() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@duration", audio);
		mockAudio.setDuration(123);
		assertXpathNotExists("//html:audio/@duration", audio);
	}

	@Test
	public void testMediagroup() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@mediagroup", audio);
		String expected = "media-group";
		audio.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:audio/@mediagroup", audio);
	}

	@Test
	public void testMediagroup_emptyStringRemoves() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@mediagroup", audio);
		String expected = "media-group";
		audio.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:audio/@mediagroup", audio);
		audio.setMediaGroup("");
		assertXpathNotExists("//html:audio/@mediagroup", audio);
	}

	@Test
	public void testMediagroup_nullRemoves() throws IOException, SAXException, XpathException {
		assertXpathNotExists("//html:audio/@mediagroup", audio);
		String expected = "media-group";
		audio.setMediaGroup(expected);
		assertXpathEvaluatesTo(expected, "//html:audio/@mediagroup", audio);
		audio.setMediaGroup(null);
		assertXpathNotExists("//html:audio/@mediagroup", audio);
	}
}

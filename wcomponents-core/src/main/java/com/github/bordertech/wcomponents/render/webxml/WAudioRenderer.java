package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Audio;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Renderer} for the {@link WAudio} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WAudioRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WAudioRenderer.class);

	/**
	 * Paints the given WAudio.
	 *
	 * @param component the WAudio to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WAudio audioComponent = (WAudio) component;
		XmlStringBuilder xml = renderContext.getWriter();
		Audio[] audio = audioComponent.getAudio();

		if (audio == null || audio.length == 0) {
			return;
		}

		WAudio.Controls controls = audioComponent.getControls();
		int duration = audio[0].getDuration();

		// Check for alternative text
		String alternativeText = audioComponent.getAltText();

		if (alternativeText == null) {
			LOG.warn("Audio should have a description.");
			alternativeText = null;
		} else {
			alternativeText = I18nUtilities.format(null, alternativeText);
		}

		xml.appendTagOpen("ui:audio");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("alt", alternativeText);
		xml.appendOptionalAttribute("autoplay", audioComponent.isAutoplay(), "true");
		xml.appendOptionalAttribute("mediagroup", audioComponent.getMediaGroup());
		xml.appendOptionalAttribute("loop", audioComponent.isLoop(), "true");
		xml.appendOptionalAttribute("hidden", audioComponent.isHidden(), "true");
		xml.appendOptionalAttribute("disabled", audioComponent.isDisabled(), "true");
		xml.appendOptionalAttribute("toolTip", audioComponent.getToolTip());
		xml.appendOptionalAttribute("duration", duration > 0, duration);

		switch (audioComponent.getPreload()) {
			case NONE:
				xml.appendAttribute("preload", "none");
				break;

			case META_DATA:
				xml.appendAttribute("preload", "metadata");
				break;

			case AUTO:
			default:
				break;
		}

		if (controls != null && !WAudio.Controls.NATIVE.equals(controls)) {
			switch (controls) {
				case NONE:
					xml.appendAttribute("controls", "none");
					break;

				case ALL:
					xml.appendAttribute("controls", "all");
					break;

				case PLAY_PAUSE:
					xml.appendAttribute("controls", "play");
					break;

				case DEFAULT:
					xml.appendAttribute("controls", "default");
					break;

				default:
					LOG.error("Unknown control type: " + controls);
			}
		}

		xml.appendClose();

		String[] urls = audioComponent.getAudioUrls();

		for (int i = 0; i < urls.length; i++) {
			xml.appendTagOpen("ui:src");
			xml.appendAttribute("uri", urls[i]);
			xml.appendOptionalAttribute("type", audio[i].getMimeType());
			xml.appendEnd();
		}

		xml.appendEndTag("ui:audio");
	}
}

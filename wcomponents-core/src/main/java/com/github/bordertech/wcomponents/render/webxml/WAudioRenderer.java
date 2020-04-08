package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Audio;
import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * {@link Renderer} for the {@link WAudio} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
class WAudioRenderer extends AbstractWebXmlRenderer {

	/**
	 * The HTML element to render.
	 */
	private static final String HTML_ELEMENT_NAME = "audio";

	/**
	 * Fixed value used in the HTML class attribute output.
	 */
	private static final String HTML_FIXED_CLASS_NAME = "wc-audio";

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

		xml.appendTagOpen(HTML_ELEMENT_NAME);
		xml.appendAttribute("id", component.getId());

		String htmlClass = component.getHtmlClass();
		htmlClass = (htmlClass == null || "".equals(htmlClass)) ? HTML_FIXED_CLASS_NAME : HTML_FIXED_CLASS_NAME.concat(" ").concat(htmlClass);
		xml.appendAttribute("class", htmlClass);

		xml.appendOptionalAttribute("controls", audioComponent.isRenderControls(), "controls");
		xml.appendOptionalAttribute("preload", preloadToString(audioComponent.getPreload()));

		xml.appendOptionalAttribute("autoplay", audioComponent.isAutoplay(), "true");
		xml.appendOptionalAttribute("loop", audioComponent.isLoop(), "true");
		xml.appendOptionalAttribute("muted", audioComponent.isMuted(), "true");
		xml.appendOptionalAttribute("hidden", audioComponent.isHidden(), "hidden");

		String title = audioComponent.getToolTip();
		if ("".equals(title)) {
			title = null;
		}
		xml.appendOptionalAttribute("title", title);

		String mediaGroup = audioComponent.getMediaGroup();
		if ("".equals(mediaGroup)) {
			mediaGroup = null;
		}
		xml.appendOptionalAttribute("mediagroup", mediaGroup);

		// if only one media source then use src attribute
		String[] urls = audioComponent.getAudioUrls();
		if (urls != null && urls.length == 1) {
			xml.appendAttribute("src", urls[0]);
		}

		xml.appendClose();
		// if more than one media source then use src elements
		if (urls != null && urls.length > 1) {
			for (int i = 0; i < urls.length; i++) {
				xml.appendTagOpen("source");
				xml.appendUrlAttribute("src", urls[i]);
				xml.appendOptionalAttribute("type", audio[i].getMimeType());
				xml.appendEnd();
			}
		}

		xml.appendEndTag(HTML_ELEMENT_NAME);
	}



	/**
	 * Converts Preload to HTMl attribute value with empty preload being none.
	 * @param preload the current WVideo.Preload value
	 * @return the value of the HTML attribute (if any)
	 */
	private String preloadToString(final WAudio.Preload preload) {
		if (preload == null) {
			return "none";
		}
		switch (preload) {
			case NONE:
				return "none";

			case META_DATA:
				return "metadata";

			case AUTO:
			default:
				return null;
		}
	}
}

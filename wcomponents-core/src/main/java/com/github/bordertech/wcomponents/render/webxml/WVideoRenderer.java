package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.Track;
import com.github.bordertech.wcomponents.Video;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.awt.Dimension;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Renderer} for the {@link WVideo} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
class WVideoRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WVideoRenderer.class);

	/**
	 * The HTML element to render.
	 */
	private static final String HTML_ELEMENT = "video";

	/**
	 * The fixed value for the HTML class attribute applied to all videos.
	 */
	private static final String HTML_FIXED_CLASS_NAME = "wc-video";

	/**
	 * Paints the given WVideo.
	 *
	 * @param component the WVideo to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WVideo videoComponent = (WVideo) component;
		XmlStringBuilder xml = renderContext.getWriter();
		Video[] video = videoComponent.getVideo();

		if (video == null || video.length == 0) {
			return;
		}

		int width = videoComponent.getWidth();
		int height = videoComponent.getHeight();
		if (width == 0 || height == 0) {
			// If width or height are not set on the WVideo, get them from the first video that has a Dimension and hope for the best.
			// My first cuts of this were way over engineered with zero checks. This one: if a Dimension has been set then use it.
			// The other (less efficient but maybe better) option is the set them to the maximum dimensions of the resources.
			Dimension d;
			for (Video v : video) {
				d = v.getSize();
				if (d != null) {
					width = d.width;
					height = d.height;
					break;
				}
			}
		}

		xml.appendTagOpen(HTML_ELEMENT);
		xml.appendAttribute("id", component.getId());
		String htmlClass = component.getHtmlClass();
		htmlClass = (htmlClass == null || "".equals(htmlClass)) ? HTML_FIXED_CLASS_NAME : HTML_FIXED_CLASS_NAME.concat(" ").concat(htmlClass);
		xml.appendAttribute("class", htmlClass);
		xml.appendOptionalAttribute("controls", videoComponent.isRenderControls(), "controls");
		xml.appendOptionalUrlAttribute("poster", videoComponent.getPosterUrl());
		xml.appendOptionalAttribute("autoplay", videoComponent.isAutoplay(), "true");
		xml.appendOptionalAttribute("loop", videoComponent.isLoop(), "true");
		xml.appendOptionalAttribute("muted", videoComponent.isMuted(), "true");
		xml.appendOptionalAttribute("hidden", videoComponent.isHidden(), "hidden");

		String title = videoComponent.getToolTip();
		if ("".equals(title)) {
			title = null;
		}
		xml.appendOptionalAttribute("title", title);
		xml.appendOptionalAttribute("width", width > 0, width);
		xml.appendOptionalAttribute("height", height > 0, height);
		xml.appendOptionalAttribute("preload", preloadToString(videoComponent.getPreload()));

		String mediaGroup = videoComponent.getMediaGroup();
		if ("".equals(mediaGroup)) {
			mediaGroup = null;
		}
		xml.appendOptionalAttribute("mediagroup", mediaGroup);

		String[] urls = videoComponent.getVideoUrls();

		if (urls != null && urls.length == 1) {
			xml.appendAttribute("src", urls[0]);
		}

		xml.appendClose();

		if (urls != null && urls.length > 1) {
			for (int i = 0; i < urls.length; i++) {
				xml.appendTagOpen("source");
				xml.appendUrlAttribute("src", urls[i]);
				xml.appendOptionalAttribute("type", video[i].getMimeType());
				xml.appendEnd();
			}
		}

		Track[] tracks = videoComponent.getTracks();
		if (tracks != null && tracks.length > 0) {
			String[] trackUrls = videoComponent.getTrackUrls();

			for (int i = 0; i < tracks.length; i++) {
				xml.appendTagOpen("track");
				xml.appendUrlAttribute("src", trackUrls[i]);
				xml.appendOptionalAttribute("lang", tracks[i].getLanguage());
				xml.appendOptionalAttribute("desc", tracks[i].getDescription());
				xml.appendOptionalAttribute("kind", trackKindToString(tracks[i].getKind()));
				xml.appendEnd();
			}
		}

		xml.appendEndTag(HTML_ELEMENT);
	}

	/**
	 * Converts a Track kind to the client track kind identifier.
	 *
	 * @param kind the Track Kind to convert.
	 * @return a client track kind identifier, or null if it could not be converted.
	 */
	private String trackKindToString(final Track.Kind kind) {
		if (kind == null) {
			return null;
		}

		switch (kind) {
			case SUBTITLES:
				return "subtitles";

			case CAPTIONS:
				return "captions";

			case DESCRIPTIONS:
				return "descriptions";

			case CHAPTERS:
				return "chapters";

			case METADATA:
				return "metadata";

			default:
				LOG.error("Unknown track kind " + kind);
				return null;
		}
	}

	/**
	 * Converts Preload to HTMl attribute value with empty preload being none.
	 * @param preload the current WVideo.Preload value
	 * @return the value of the HTML attribute (if any)
	 */
	private String preloadToString(final WVideo.Preload preload) {
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

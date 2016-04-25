package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.Track;
import com.github.bordertech.wcomponents.Video;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Renderer} for the {@link WVideo} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WVideoRenderer extends AbstractWebXmlRenderer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WVideoRenderer.class);

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

		Track[] tracks = videoComponent.getTracks();
		WVideo.Controls controls = videoComponent.getControls();
		int width = videoComponent.getWidth();
		int height = videoComponent.getHeight();
		int duration = video[0].getDuration();

		// Check for alternative text
		String alternativeText = videoComponent.getAltText();

		if (alternativeText == null) {
			LOG.warn("Video should have a description.");
			alternativeText = null;
		} else {
			alternativeText = I18nUtilities.format(null, alternativeText);
		}

		xml.appendTagOpen("ui:video");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("poster", videoComponent.getPosterUrl());
		xml.appendOptionalAttribute("alt", alternativeText);
		xml.appendOptionalAttribute("autoplay", videoComponent.isAutoplay(), "true");
		xml.appendOptionalAttribute("mediagroup", videoComponent.getMediaGroup());
		xml.appendOptionalAttribute("loop", videoComponent.isLoop(), "true");
		xml.appendOptionalAttribute("muted", videoComponent.isMuted(), "true");
		xml.appendOptionalAttribute("hidden", videoComponent.isHidden(), "true");
		xml.appendOptionalAttribute("disabled", videoComponent.isDisabled(), "true");
		xml.appendOptionalAttribute("toolTip", videoComponent.getToolTip());
		xml.appendOptionalAttribute("width", width > 0, width);
		xml.appendOptionalAttribute("height", height > 0, height);
		xml.appendOptionalAttribute("duration", duration > 0, duration);

		switch (videoComponent.getPreload()) {
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

		if (controls != null && !WVideo.Controls.NATIVE.equals(controls)) {
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

		String[] urls = videoComponent.getVideoUrls();

		for (int i = 0; i < urls.length; i++) {
			xml.appendTagOpen("ui:src");
			xml.appendAttribute("uri", urls[i]);
			xml.appendOptionalAttribute("type", video[i].getMimeType());

			if (video[i].getSize() != null) {
				xml.appendOptionalAttribute("width", video[i].getSize().width > 0, video[i].
						getSize().width);
				xml.appendOptionalAttribute("height", video[i].getSize().height > 0, video[i].
						getSize().height);
			}

			xml.appendEnd();
		}

		if (tracks != null && tracks.length > 0) {
			String[] trackUrls = videoComponent.getTrackUrls();

			for (int i = 0; i < tracks.length; i++) {
				xml.appendTagOpen("ui:track");
				xml.appendAttribute("src", trackUrls[i]);
				xml.appendOptionalAttribute("lang", tracks[i].getLanguage());
				xml.appendOptionalAttribute("desc", tracks[i].getDescription());
				xml.appendOptionalAttribute("kind", trackKindToString(tracks[i].getKind()));
				xml.appendEnd();
			}
		}

		xml.appendEndTag("ui:video");
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
}

package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WVideo is used to display video content on the client.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WVideo extends AbstractWComponent implements Targetable, AjaxTarget, Disableable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WVideo.class);

	/**
	 * This request parameter is used to determine which video clip to serve up.
	 */
	private static final String VIDEO_INDEX_REQUEST_PARAM_KEY = "WVideo.videoIndex";

	/**
	 * This request parameter is used to determine which track to serve up.
	 */
	private static final String TRACK_INDEX_REQUEST_PARAM_KEY = "WVideo.trackIndex";

	/**
	 * This request parameter is used to request the poster image.
	 */
	private static final String POSTER_REQUEST_PARAM_KEY = "WVideo.poster";

	/**
	 * This is used to indicate whether pre-loading of content should occur before the clip is played.
	 */
	public enum Preload {
		/**
		 * Do not pre-load any data.
		 */
		NONE,
		/**
		 * Preload meta-data only.
		 */
		META_DATA,
		/**
		 * Let the client determine what to load.
		 */
		AUTO
	}

	/**
	 * This is used to indicate which playback controls to display for the video.
	 *
	 * <p>
	 * <strong>Note:</strong>
	 * Advancements in video support in clients since this API was first implemented means that most of this is now
	 * redundant. Under most circumstances the UI will display their native video controls. Where a particular WVideo
	 * does not have any source which is able to be played by the client then links to all sources will be provided.
	 * This enum is not worthless as the values NONE and PLAY_PAUSE are used to turn off native video controls in the
	 * client. The value NONE however causes major problems and is incompatible with autoplay for a11y reasons so it
	 * basically makes the media worthless. This enum may be replaced in the future with a simple boolean to trigger
	 * native controls or play/pause only (see https://github.com/BorderTech/wcomponents/issues/503).
	 * </p>
	 */
	public enum Controls {
		/**
		 * Do not display any controls: not recommended. May be incompatible with any of {@link #isAutoplay()} == true,
		 * {@link #isMuted()} == true or {@link #isLoop()} == true. If this value is set then the WVideo control
		 * <strong>MAY NOT WORK AT ALL</strong>.
		 * @deprecated since 1.1.1 as this is incompatible with WCAG requirements.
		 */
		NONE,
		/**
		 * Display all controls.
		 * @deprecated since 1.1.1 as themes use native video controls.
		 */
		ALL,
		/**
		 * A combined play/pause button.
		 */
		PLAY_PAUSE,
		/**
		 * Displays the "default" set of controls for the theme.
		 * @deprecated since 1.1.1 as themes use native video controls.
		 */
		DEFAULT,
		/**
		 * Displays the client's native set of controls.
		 */
		NATIVE
	}

	/**
	 * Creates a WVideo with no video clips. Video clips must be added later by calling one of the setVideo(...)
	 * methods.
	 */
	public WVideo() {
	}

	/**
	 * Creates a WVideo with the given video clip.
	 *
	 * @param video the video clip.
	 */
	public WVideo(final Video video) {
		this(new Video[]{video});
	}

	/**
	 * <p>
	 * Creates a WVideo with the given static content. This is provided as a convenience method for when the video file
	 * is included as static content in the class path rather than in the web application's resources.
	 * </p>
	 * <p>
	 * The mime type for the video clip is looked up from the "mimeType.*" mapping configuration parameters using the
	 * resource's file extension.
	 * </p>
	 *
	 * @param resource the resource path to the video file.
	 */
	public WVideo(final String resource) {
		this(new VideoResource(resource));
	}

	/**
	 * Creates a WVideo with the given video clip in multiple formats. The client will try to load the first video clip,
	 * and if it fails or isn't supported, it will move on to the next video clip. Only the first clip which can be
	 * played on the client will be used.
	 *
	 * @param video multiple formats for the same the video clip.
	 */
	public WVideo(final Video[] video) {
		setVideo(video);
	}

	/**
	 * Sets the video clip.
	 *
	 * @param video the video clip.
	 */
	public void setVideo(final Video video) {
		setVideo(new Video[]{video});
	}

	/**
	 * Sets the video clip in multiple formats. The client will try to load the first video clip, and if it fails or
	 * isn't supported, it will move on to the next video clip. Only the first clip which can be played on the client
	 * will be used.
	 *
	 * @param video multiple formats for the same the video clip.
	 */
	public void setVideo(final Video[] video) {
		List<Video> list = video == null ? null : Arrays.asList(video);
		getOrCreateComponentModel().video = list;
	}

	/**
	 * Retrieves the video clips associated with this WVideo.
	 *
	 * @return the video clips, may be null.
	 */
	public Video[] getVideo() {
		List<Video> list = getComponentModel().video;
		return list == null ? null : list.toArray(new Video[]{});
	}

	/**
	 * Indicates whether the video component is disabled.
	 *
	 * @return true if the component is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the video component is disabled.
	 *
	 * @param disabled if true, the component is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * @return true if the clip should start playing automatically, false for a manual start.
	 */
	public boolean isAutoplay() {
		return getComponentModel().autoplay;
	}

	/**
	 * Sets whether the clip should play automatically.
	 *
	 * @param autoplay true to start playing automatically, false for a manual start.
	 */
	public void setAutoplay(final boolean autoplay) {
		getOrCreateComponentModel().autoplay = autoplay;
	}

	/**
	 * @return the media group name.
	 */
	public String getMediaGroup() {
		return getComponentModel().mediaGroup;
	}

	/**
	 * Sets the media group.
	 *
	 * @param mediaGroup The media group name.
	 */
	public void setMediaGroup(final String mediaGroup) {
		getOrCreateComponentModel().mediaGroup = mediaGroup;
	}

	/**
	 * Indicates whether the video clip playback should loop.
	 *
	 * @return true to loop, false to stop at the end.
	 */
	public boolean isLoop() {
		return getComponentModel().loop;
	}

	/**
	 * Sets whether the video clip playback should loop or stop at the end.
	 *
	 * @param loop true to loop, false to stop at the end.
	 */
	public void setLoop(final boolean loop) {
		getOrCreateComponentModel().loop = loop;
	}

	/**
	 * Indicates whether the video's audio should initially be muted.
	 *
	 * @return true if muted, false otherwise.
	 */
	public boolean isMuted() {
		return getComponentModel().muted;
	}

	/**
	 * Sets whether the video's audio should initially be muted.
	 *
	 * @param muted true to mute the audio, false to play normally.
	 */
	public void setMuted(final boolean muted) {
		getOrCreateComponentModel().muted = muted;
	}

	/**
	 * Indicates which playback controls (e.g. stop/start/pause) to display on the video component.
	 *
	 * @return the playback controls to display.
	 */
	public Controls getControls() {
		return getComponentModel().controls;
	}

	/**
	 * Sets which playback controls (e.g. stop/start/pause) to display on the video component. The values of
	 * {@link Controls#NONE} and {@link Controls#ALL} take precedence over all other values. Passing a null or empty set
	 * of controls will cause the client's default set of controls to be used.
	 *
	 * @param controls the playback controls to display.
	 */
	public void setControls(final Controls controls) {
		getOrCreateComponentModel().controls = controls;
	}

	/**
	 * Indicates how pre-loading of content should occur before the clip is played.
	 *
	 * @return the pre-loading mode.
	 */
	public Preload getPreload() {
		return getComponentModel().preload;
	}

	/**
	 * Sets how pre-loading of content should occur before the clip is played.
	 *
	 * @param preload the pre-loading mode.
	 */
	public void setPreload(final Preload preload) {
		getOrCreateComponentModel().preload = preload;
	}

	/**
	 * @return alternative text to display when the video clip can not be played.
	 */
	public String getAltText() {
		return getComponentModel().altText;
	}

	/**
	 * Sets the alternative text to display when the video clip can not be played.
	 *
	 * @param altText the text to set.
	 */
	public void setAltText(final String altText) {
		getOrCreateComponentModel().altText = altText;
	}

	/**
	 * @return the width of the video playback region on the client, in pixels.
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * Sets the width of the video playback region on the client.
	 *
	 * @param width the width of the video playback region, in pixels.
	 */
	public void setWidth(final int width) {
		getOrCreateComponentModel().width = width;
	}

	/**
	 * @return the height of the video playback region on the client, in pixels.
	 */
	public int getHeight() {
		return getComponentModel().height;
	}

	/**
	 * Sets the height of the video playback region on the client.
	 *
	 * @param height the height of the video playback region, in pixels.
	 */
	public void setHeight(final int height) {
		getOrCreateComponentModel().height = height;
	}

	/**
	 * Retrieves the default poster image. The poster image is displayed by the client when the video is not playing.
	 *
	 * @return the default poster image.
	 */
	public Image getPoster() {
		return getComponentModel().poster;
	}

	/**
	 * Sets the default poster image. The poster image is displayed by the client when the video is not playing.
	 *
	 * @param poster the default poster image.
	 */
	public void setPoster(final Image poster) {
		getOrCreateComponentModel().poster = poster;
	}

	/**
	 * Sets the tracks for the video. The tracks are used to provide additional information relating to the video, for
	 * example subtitles.
	 *
	 * @param tracks additional tracks relating to the video.
	 */
	public void setTracks(final Track[] tracks) {
		List<Track> list = tracks == null ? null : Arrays.asList(tracks);
		getOrCreateComponentModel().tracks = list;
	}

	/**
	 * Retrieves additional tracks associated with the video. The tracks provide additional information relating to the
	 * video, for example subtitles.
	 *
	 * @return the video clips, may be null.
	 */
	public Track[] getTracks() {
		List<Track> list = getComponentModel().tracks;
		return list == null ? null : list.toArray(new Track[]{});
	}

	/**
	 * Creates dynamic URLs that the video clips can be loaded from. In fact the URL points to the main application
	 * servlet, but includes a non-null for the parameter associated with this WComponent (ie, its label). The
	 * handleRequest method below detects this when the browser requests a file.
	 *
	 * @return the urls to load the video files from, or null if there are no clips defined.
	 */
	public String[] getVideoUrls() {
		Video[] video = getVideo();

		if (video == null || video.length == 0) {
			return null;
		}

		String[] urls = new String[video.length];

		// this variable needs to be set in the portlet environment.
		String url = getEnvironment().getWServletPath();
		Map<String, String> parameters = getBaseParameterMap();

		for (int i = 0; i < urls.length; i++) {
			parameters.put(VIDEO_INDEX_REQUEST_PARAM_KEY, String.valueOf(i));
			urls[i] = WebUtilities.getPath(url, parameters, true);
		}

		return urls;
	}

	/**
	 * Creates dynamic URLs that the video clips can be loaded from. In fact the URL points to the main application
	 * servlet, but includes a non-null for the parameter associated with this WComponent (ie, its label). The
	 * handleRequest method below detects this when the browser requests a file.
	 *
	 * @return the urls to load the video files from, or null if there are no clips defined.
	 */
	public String[] getTrackUrls() {
		Track[] tracks = getTracks();

		if (tracks == null || tracks.length == 0) {
			return null;
		}

		String[] urls = new String[tracks.length];

		// this variable needs to be set in the portlet environment.
		String url = getEnvironment().getWServletPath();
		Map<String, String> parameters = getBaseParameterMap();

		for (int i = 0; i < urls.length; i++) {
			parameters.put(TRACK_INDEX_REQUEST_PARAM_KEY, String.valueOf(i));
			urls[i] = WebUtilities.getPath(url, parameters, true);
		}

		return urls;
	}

	/**
	 * Creates a dynamic URL that the poster can be loaded from. In fact the URL points to the main application servlet,
	 * but includes a non-null for the parameter associated with this WComponent (ie, its label). The handleRequest
	 * method below detects this when the browser requests a file.
	 *
	 * @return the url to load the poster from, or null if there is no poster defined.
	 */
	public String getPosterUrl() {
		Image poster = getComponentModel().poster;

		if (poster == null) {
			return null;
		}

		// this variable needs to be set in the portlet environment.
		String url = getEnvironment().getWServletPath();
		Map<String, String> parameters = getBaseParameterMap();
		parameters.put(POSTER_REQUEST_PARAM_KEY, "x");
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * Retrieves the base parameter map for serving content (videos + tracks).
	 *
	 * @return the base map for serving content.
	 */
	private Map<String, String> getBaseParameterMap() {
		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		if (Util.empty(getCacheKey())) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, getCacheKey());
		}

		return parameters;
	}

	/**
	 * Override isVisible to also return false if there are no video clips to play.
	 *
	 * @return true if this component is visible in the given context, otherwise false.
	 */
	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}

		Video[] video = getVideo();
		return video != null && video.length > 0;
	}

	/**
	 * When an video element is rendered to the client, the browser will make a second request to get the video content.
	 * The handleRequest method has been overridden to detect whether the request is the "content fetch" request by
	 * looking for the parameter that we encode in the content url.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);


		String targ = request.getParameter(Environment.TARGET_ID);
		boolean contentReqested = (targ != null && targ.equals(getTargetId()));

		if (contentReqested && request.getParameter(POSTER_REQUEST_PARAM_KEY) != null) {
			handlePosterRequest();
		}

		if (isDisabled()) {
			return;
		}

		if (contentReqested) {
			if (request.getParameter(VIDEO_INDEX_REQUEST_PARAM_KEY) != null) {
				handleVideoRequest(request);
			} else if (request.getParameter(TRACK_INDEX_REQUEST_PARAM_KEY) != null) {
				handleTrackRequest(request);
			}
		}
	}

	/**
	 * Handles a request for the poster.
	 */
	private void handlePosterRequest() {
		Image poster = getComponentModel().poster;

		if (poster != null) {
			ContentEscape escape = new ContentEscape(poster);
			escape.setCacheable(!Util.empty(getCacheKey()));
			throw escape;
		} else {
			LOG.warn("Client requested non-existant poster");
		}
	}

	/**
	 * Handles a request for a video.
	 *
	 * @param request the request being responded to.
	 */
	private void handleVideoRequest(final Request request) {
		String videoRequested = request.getParameter(VIDEO_INDEX_REQUEST_PARAM_KEY);
		int videoFileIndex = 0;

		try {
			videoFileIndex = Integer.parseInt(videoRequested);
		} catch (NumberFormatException e) {
			LOG.error("Failed to parse video index: " + videoFileIndex);
		}

		Video[] video = getVideo();

		if (video != null && videoFileIndex >= 0 && videoFileIndex < video.length) {
			ContentEscape escape = new ContentEscape(video[videoFileIndex]);
			escape.setCacheable(!Util.empty(getCacheKey()));
			throw escape;
		} else {
			LOG.warn("Client requested invalid video clip: " + videoFileIndex);
		}
	}

	/**
	 * Handles a request for an auxillary track.
	 *
	 * @param request the request being responded to.
	 */
	private void handleTrackRequest(final Request request) {
		String trackRequested = request.getParameter(TRACK_INDEX_REQUEST_PARAM_KEY);
		int trackIndex = 0;

		try {
			trackIndex = Integer.parseInt(trackRequested);
		} catch (NumberFormatException e) {
			LOG.error("Failed to parse track index: " + trackIndex);
		}

		Track[] tracks = getTracks();

		if (tracks != null && trackIndex >= 0 && trackIndex < tracks.length) {
			ContentEscape escape = new ContentEscape(tracks[trackIndex]);
			escape.setCacheable(!Util.empty(getCacheKey()));
			throw escape;
		} else {
			LOG.warn("Client requested invalid track: " + trackIndex);
		}
	}

	/**
	 * @return the cacheKey
	 */
	public String getCacheKey() {
		return getComponentModel().cacheKey;
	}

	/**
	 * @param cacheKey the cacheKey to set.
	 */
	public void setCacheKey(final String cacheKey) {
		getOrCreateComponentModel().cacheKey = cacheKey;
	}

	/**
	 * Returns the id to use to target this component.
	 *
	 * @return this component's target id.
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getAltText();
		return toString(text == null ? null : ('"' + text + '"'));
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new VideoModel.
	 */
	@Override
	protected VideoModel newComponentModel() {
		return new VideoModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected VideoModel getComponentModel() {
		return (VideoModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected VideoModel getOrCreateComponentModel() {
		return (VideoModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WVideo.
	 */
	public static class VideoModel extends ComponentModel {

		/**
		 * The various video clips.
		 */
		private List<Video> video;

		/**
		 * Additional tracks relating to the video, e.g. subtitles.
		 */
		private List<Track> tracks;

		/**
		 * The cache key used to control client-side caching.
		 */
		private String cacheKey;

		/**
		 * Indicates whether the video should play immediately after page load.
		 */
		private boolean autoplay;

		/**
		 * Indicates whether playback of the video clip should be looped.
		 */
		private boolean loop;

		/**
		 * Indicates whether audio should initially be muted.
		 */
		private boolean muted;

		/**
		 * Indicates which playback controls to display.
		 */
		private Controls controls;

		/**
		 * Indicates whether pre-loading of content should occur before the clip is played.
		 */
		private Preload preload = Preload.NONE;

		/**
		 * Alternate text to display if the video clip can not be played.
		 */
		private String altText;

		/**
		 * The width of the video playback region on the client, in pixels.
		 */
		private int width;

		/**
		 * The height of the video playback region on the client, in pixels.
		 */
		private int height;

		/**
		 * The poster image is displayed in place of the video, until it is loaded.
		 */
		private Image poster;

		/**
		 * This is used to group related media together, for example to synchronize tracks.
		 */
		private String mediaGroup;
	}
}

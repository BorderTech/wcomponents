package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * Provides a bridge to static image resources which are present in the class path, but not in the web application
 * itself.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class VideoResource extends InternalResource implements Video {

	/**
	 * The video size, if known.
	 */
	private final Dimension size;

	/**
	 * The video length, if known.
	 */
	private int duration;

	/**
	 * Creates a videoResource.
	 *
	 * @param videoResource the resource name.
	 */
	public VideoResource(final String videoResource) {
		this(videoResource, "unknown video", null);
	}

	/**
	 * Creates a videoResource.
	 *
	 * @param videoResource the resource name.
	 * @param description the description of the video.
	 */
	public VideoResource(final String videoResource, final String description) {
		this(videoResource, description, null);
	}

	/**
	 * Creates a videoResource.
	 *
	 * @param videoResource the resource name.
	 * @param description the description of the video.
	 * @param size the video dimensions, or null if unknown.
	 */
	public VideoResource(final String videoResource, final String description, final Dimension size) {
		super(videoResource, description);
		this.size = size;
	}

	/**
	 * @return the video size, or null if unknown.
	 */
	@Override
	public Dimension getSize() {
		return size;
	}

	/**
	 * Retrieves the duration of the video clip. A value of &lt;= 0 indicates that the duration is unknown.
	 *
	 * @return the duration of the video clip.
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the video clip, A value of &lt;= 0 indicates that the duration is unknown.
	 *
	 * @param duration The duration to set.
	 */
	public void setDuration(final int duration) {
		this.duration = duration;
	}
}

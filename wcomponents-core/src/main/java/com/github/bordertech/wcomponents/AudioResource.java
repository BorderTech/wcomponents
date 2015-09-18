package com.github.bordertech.wcomponents;

/**
 * Provides a bridge to static audio resources which are present in the class path, but not in the web application
 * itself.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AudioResource extends InternalResource implements Audio {

	/**
	 * The audio length, if known.
	 */
	private int duration;

	/**
	 * Creates an AudioResource.
	 *
	 * @param audioResource the resource name.
	 */
	public AudioResource(final String audioResource) {
		this(audioResource, "unknown audio clip");
	}

	/**
	 * Creates an AudioResource.
	 *
	 * @param audioResource the resource name.
	 * @param description the description of the audio clip.
	 */
	public AudioResource(final String audioResource, final String description) {
		super(audioResource, description);
	}

	/**
	 * Retrieves the duration of the audio clip. A value of &lt;= 0 indicates that the duration is unknown.
	 *
	 * @return the duration of the audio clip.
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the audio clip, A value of &lt;= 0 indicates that the duration is unknown.
	 *
	 * @param duration The duration to set.
	 */
	public void setDuration(final int duration) {
		this.duration = duration;
	}
}

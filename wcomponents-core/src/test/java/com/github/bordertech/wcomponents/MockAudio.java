package com.github.bordertech.wcomponents;

/**
 * MocKAudio - implementation of Audio useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockAudio extends MockContentStreamAccess implements Audio {

	/**
	 * The audio length, if known.
	 */
	private int duration;

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

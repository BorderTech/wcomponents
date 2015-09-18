package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * MockVideo - implementation of Video useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockVideo extends MockContentStreamAccess implements Video {

	/**
	 * The image size, in pixels.
	 */
	private Dimension size;

	/**
	 * The video length, if known.
	 */
	private int duration;

	/**
	 * @return Returns the size.
	 */
	@Override
	public Dimension getSize() {
		return size;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(final Dimension size) {
		this.size = size;
	}

	/**
	 * Retrieves the duration of the video clip. A value of &lt;= 0 indicates that the duration is unknown.
	 *
	 * @return the duration of the audio clip.
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

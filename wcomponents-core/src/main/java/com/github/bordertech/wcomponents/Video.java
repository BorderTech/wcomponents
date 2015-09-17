package com.github.bordertech.wcomponents;

import java.awt.Dimension;

/**
 * Represents a video. Implementations can choose whether to explicitly store the byte[] or fetch it from some sort of
 * streaming resource as needed to service the {@link #getBytes} and {@link #getStream} methods as needed.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Video extends ContentStreamAccess {

	/**
	 * @return the natural size (in pixels) of the video, or null if no natural size is known.
	 */
	Dimension getSize();

	/**
	 * @return the length of the video clip, in seconds, or &lt;= 0 if unknown (e.g. streaming).
	 */
	int getDuration();
}

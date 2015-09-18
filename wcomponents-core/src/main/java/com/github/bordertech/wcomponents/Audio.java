package com.github.bordertech.wcomponents;

/**
 * Represents an audio clip. Implementations can choose whether to explicitly store the byte[] or fetch it from some
 * sort of streaming resource as needed to service the {@link #getBytes} and {@link #getStream} methods as needed.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Audio extends ContentStreamAccess {

	/**
	 * @return the length of the video clip, in seconds, or &lt;= 0 if unknown (e.g. streaming).
	 */
	int getDuration();
}

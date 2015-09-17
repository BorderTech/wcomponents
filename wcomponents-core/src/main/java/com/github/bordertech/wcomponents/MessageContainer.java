package com.github.bordertech.wcomponents;

/**
 * MessageContainer - Interface for a {@link WComponent} that displays {@link WMessages}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface MessageContainer {

	/**
	 * @return the WMessages contained by this MessageContainer.
	 */
	WMessages getMessages();
}

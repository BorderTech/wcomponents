package com.github.bordertech.wcomponents;

/**
 * This interface is used to mark components which hold a name context.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface NamingContextable extends Container {

	/**
	 * Allow components that implement {@link NamingContextable} to selectively disable prepending their ID to their
	 * descendent's IDs by breaking the prepending logic into a seperately callable method.
	 * <p>
	 * See {@link #getId()} for usage.
	 * </p>
	 * <p>
	 * By default, this method will call through to {@link #getId()} and return the result.
	 * </p>
	 *
	 * @return by default, return getId().
	 */
	String getNamingContextId();

	/**
	 * A naming context is only considered active if an Id name has been set.
	 *
	 * @return true if active naming context.
	 */
	boolean isNamingContext();
}

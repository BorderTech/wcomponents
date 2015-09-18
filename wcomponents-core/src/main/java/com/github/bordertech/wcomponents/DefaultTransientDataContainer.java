package com.github.bordertech.wcomponents;

/**
 * A default implementation of {@link AbstractTransientDataContainer} that doesn't provide any data.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DefaultTransientDataContainer extends AbstractTransientDataContainer {

	/**
	 * Creates an empty DefaultTransientDataContainer.
	 */
	public DefaultTransientDataContainer() {
	}

	/**
	 * Convenience method to creates a DefaultTransientDataContainer with the given content.
	 *
	 * @param child the container's content.
	 */
	public DefaultTransientDataContainer(final WComponent child) {
		add(child);
	}

	/**
	 * A default implementation of setUpData that does nothing.
	 */
	@Override
	public void setupData() {
	}
}

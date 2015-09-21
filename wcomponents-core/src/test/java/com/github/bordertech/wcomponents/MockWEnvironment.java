package com.github.bordertech.wcomponents;

import java.util.HashMap;
import java.util.Map;

/**
 * MockWEnvironment Mock WEnvironment useful for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockWEnvironment extends AbstractEnvironment {

	/**
	 * The hidden parameters map.
	 */
	private Map<String, String> hiddenParameters = new HashMap<>();

	/**
	 * Sets the post path. Overriden in order to make method public, as it's useful for unit testing.
	 *
	 * @param postPath the post path.
	 */
	@Override
	public void setPostPath(final String postPath) {
		super.setPostPath(postPath);
	}

	/**
	 * Overridden so that hidden parameters may be set by test cases.
	 *
	 * @return the hidden parameter map.
	 */
	@Override
	public Map<String, String> getHiddenParameters() {
		return hiddenParameters;
	}

	/**
	 * Sets the hidden parameters.
	 *
	 * @param hiddenParameters the hidden parameters to set.
	 */
	public void setHiddenParameters(final Map<String, String> hiddenParameters) {
		this.hiddenParameters = hiddenParameters;
	}
}

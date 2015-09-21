package com.github.bordertech.wcomponents;

/**
 * Default implementation of {@link FatalErrorPageFactory}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class FatalErrorPageFactoryImpl implements FatalErrorPageFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WComponent createErrorPage(final boolean developerFriendly, final Throwable throwable) {
		return new FatalErrorPage(developerFriendly, throwable);
	}
}

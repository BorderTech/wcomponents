package com.github.bordertech.wcomponents;

/**
 * This interface enables a plug in point for alternative error pages.
 *
 * @author Martin Shevchenko
 */
public interface FatalErrorPageFactory {

	/**
	 * Create a WComponent to display as an error page.
	 *
	 * @param developerFriendly indicates whether developer-friendly information can be provided in the error page (eg a
	 * stack trace). This should never be true in production.
	 * @param error is the error that we are handling.
	 * @return a WComponent to display as an error page.
	 */
	WComponent createErrorPage(boolean developerFriendly, Throwable error);
}

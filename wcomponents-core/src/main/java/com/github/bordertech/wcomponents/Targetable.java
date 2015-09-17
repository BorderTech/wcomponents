package com.github.bordertech.wcomponents;

/**
 * WComponents can be marked as targetable. This means they can be directly targeted via a url to return to the client
 * just their fragment of content, rather than the entire application. They can also be used to return content other
 * than html, such as images and PDFs.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public interface Targetable extends WComponent {

	/**
	 * The target id returned must be unique across all targetable WComponents within the application. In a portal
	 * environment, the target ids must also be unique across portlets. Most components should just return their
	 * component id.
	 *
	 * @return the target id for this targetable.
	 */
	String getTargetId();
}

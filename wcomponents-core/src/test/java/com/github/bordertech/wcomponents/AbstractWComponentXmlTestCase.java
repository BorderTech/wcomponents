package com.github.bordertech.wcomponents;

/**
 * <p>
 * This extension of junit TestCase includes assertions and other features useful for the testing WComponents.</p>
 *
 * <p>
 * This differs from WComponentTestCase in that developers do not need to specify the schema for each component; it is
 * derived from the theme in use.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractWComponentXmlTestCase {

	/**
	 * Obtains a UI Context.
	 *
	 * This is called by many of the other methods where a UIContext is not explicitly passed. Subclasses can therefore
	 * e.g. override this to ensure that the same context is always used.
	 *
	 * @return a new UIContext.
	 */
	protected UIContext createUIContext() {
		return new UIContextImpl();
	}
}

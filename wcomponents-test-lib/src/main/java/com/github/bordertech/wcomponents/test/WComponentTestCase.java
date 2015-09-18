package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.ThemeUtil;

/**
 * This test case base class includes assertions and other features useful for the testing of WComponents.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class WComponentTestCase {

	/**
	 * The top-level UIContext.
	 */
	private UIContext uic;

	/**
	 * The top-level UI.
	 */
	private final WComponent ui;

	/**
	 * Wrapped ui.
	 */
	private final WApplication wrappedUi;

	/**
	 * Creates a WComponentTestCase.
	 *
	 * @param ui the UI to test.
	 */
	public WComponentTestCase(final WComponent ui) {
		// TODO Review how to wrap the tests with a WApplication
		if (ui instanceof WApplication) {
			wrappedUi = (WApplication) ui;
		} else {
			wrappedUi = new WApplication();
			wrappedUi.add(ui);
		}

		this.ui = ui;
		ui.setLocked(true);
	}

	/**
	 * Returns the version of the Theme and Skin to run the tests under. By default, this returns the default theme in
	 * use, but individual tests can override this to use a specific theme.
	 *
	 * @return the version of the Theme and Skin to use.
	 */
	protected String getTheme() {
		return ThemeUtil.getThemeName();
	}

	/**
	 * This returns the top-level context to be used during testing. The default implementation is to create and use a
	 * single context, but subclasses can change this behaviour if they wish.
	 *
	 * @return the top-level UIContext.
	 */
	protected synchronized UIContext getUIContext() {
		if (uic == null) {
			resetUIContext();
		}

		return uic;
	}

	/**
	 * Replaces the UIContext with a new copy, to emulate a fresh session.
	 */
	protected void resetUIContext() {
		uic = new UIContextImpl();
		uic.setUI(getWrappedUi());
	}

	/**
	 * @return the WComponent UI being tested.
	 */
	public WComponent getUi() {
		return ui;
	}

	/**
	 * @return the Wrapped WComponent UI being tested.
	 */
	public WApplication getWrappedUi() {
		return wrappedUi;
	}

}

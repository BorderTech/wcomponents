package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * UIContextDebugWrapper_Test - unit tests for UIContextDebugWrapper.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class UIContextDebugWrapper_Test extends AbstractWComponentTestCase {

	@Test
	public void testGetUIContext() {
		UIContext uic = createUIContext();
		UIContextDebugWrapper debugWrapper = new UIContextDebugWrapper(uic);

		UIContext uicResult = debugWrapper.getUIContext();

		Assert.assertEquals("debugWrapper should have uic as getUIContext", uicResult, uic);
	}

	@Test
	public void testDumpAll() {
		WContainer component = new WContainer();

		// add 2 child components
		WText text1 = new WText("abcd");
		WLabel label1 = new WLabel("xyzw");

		component.setLocked(true);
		UIContext uic = createUIContext();
		setActiveContext(uic);
		component.add(text1);
		component.add(label1);

		UIContextDebugWrapper debugWrapper = new UIContextDebugWrapper(uic);
		String result = debugWrapper.toString();

		// test expected number of components storing data in session
		int expectedNumComponents = component.getChildCount() + 1;
		Assert.assertTrue("should report expected number of components", result
				.indexOf(expectedNumComponents + " WComponent(s) storing data in the session.") != -1);

		// test session usage by class list - finding class names in substring
		Assert.assertTrue("should find the expected label string in report", result
				.indexOf("WComponent session usage by class:") != -1);
		String subResult = result.substring(result.indexOf("WComponent session usage by class:"));

		// top level item
		Assert.assertTrue("should report className for top level component in list", subResult.
				indexOf(component.getClass()
						.getName()) != -1);

		// child items
		for (int i = 0; i < component.getChildCount(); i++) {
			Assert.assertTrue("should report className for each child", subResult.indexOf(component.
					getChildAt(i)
					.getClass().getName()) != -1);
		}
	}
}

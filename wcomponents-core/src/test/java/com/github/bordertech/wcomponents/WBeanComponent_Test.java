package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WBeanComponent}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WBeanComponent_Test extends AbstractWComponentTestCase {

	@Test
	public void testIsChanged() {
		WBeanComponent component = new WBeanComponent();

		String text1 = "WBeanComponent_Test.testIsChanged.text1";
		String text2 = "WBeanComponent_Test.testIsChanged.text2";

		component.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertFalse("Should not be changed by default", component.isChanged());

		component.setData(text1);
		Assert.assertTrue("Should be changed if text differs from default", component.isChanged());

		component.setBean(text1);
		component.setBeanProperty(".");
		Assert.assertFalse("Should not be changed if text equals bean", component.isChanged());

		component.setData(text2);
		Assert.assertTrue("Should be changed if text differs from bean", component.isChanged());
	}
}

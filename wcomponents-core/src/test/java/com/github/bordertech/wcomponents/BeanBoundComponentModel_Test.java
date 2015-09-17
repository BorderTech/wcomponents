package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link BeanBoundComponentModel}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class BeanBoundComponentModel_Test extends AbstractWComponentTestCase {

	@Test
	public void testSetBean() {
		String textIn = "BeanBoundComponentModel_Test.initialText.text1";
		BeanBoundComponentModel boundBean = new BeanBoundComponentModel();
		boundBean.setBean(textIn);

		Assert.
				assertEquals("Bean retrieved should be same as bean set", textIn, boundBean.
						getBean());
	}
}

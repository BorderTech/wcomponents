package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link BeanProviderBoundComponentModel}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class BeanProviderBoundComponentModel_Test extends AbstractWComponentTestCase {

	@Test
	public void testIsSet() {
		String textIn = "BeanProviderBoundComponentModel_Test.initialText.text1";
		BeanProviderBoundComponentModel providerBoundBean = new BeanProviderBoundComponentModel();
		providerBoundBean.setBeanId(textIn);

		Assert.assertEquals("Bean retrieved should be same as bean set", textIn, providerBoundBean.
				getBeanId());
	}
}

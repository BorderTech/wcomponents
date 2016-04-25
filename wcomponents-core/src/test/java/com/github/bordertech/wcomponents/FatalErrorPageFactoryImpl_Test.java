package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link FatalErrorPageFactoryImpl}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class FatalErrorPageFactoryImpl_Test extends AbstractWComponentTestCase {

	@Test
	public void testCreateErrorPage() {
		FatalErrorPageFactoryImpl factory = new FatalErrorPageFactoryImpl();
		TestSampleException exception = new TestSampleException("sample exception");
		WComponent result = factory.createErrorPage(true, exception);

		Assert.assertTrue("result should be instance of FatalErrorPage",
				result instanceof FatalErrorPage);
	}
}

package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WNamingContext}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WNamingContext_Test extends AbstractWComponentTestCase {

	@Test
	public void testIdNameAccessors() {
		assertAccessorsCorrect(new WNamingContext("TEST"), "idName", "TEST", "XX", "YY");
	}

	@Test
	public void testNamingContextId() {
		WNamingContext context = new WNamingContext("TEST");
		Assert.assertEquals("Incorrect naming context", "TEST", context.getNamingContextId());
	}
}

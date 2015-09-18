package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractNamingContextContainer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractNamingContextContainer_Test extends AbstractWComponentTestCase {

	@Test
	public void testNamingContextAccessors() {
		assertAccessorsCorrect(new MyContainer(), "namingContext", false, true, false);
	}

	@Test
	public void testNamingContextIdAccessor() {
		String id = "test";

		AbstractNamingContextContainer container = new MyContainer();
		container.setIdName(id);

		Assert.assertEquals("Incorrect component id", id, container.getId());
		Assert.assertEquals("Naming context should match component id", id, container.
				getNamingContextId());
	}

	/**
	 * Test instance of AbstractNamingContextContainer.
	 */
	private static class MyContainer extends AbstractNamingContextContainer {
	};

}

package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.servlet.ServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LookupTableHelper}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class LookupTableHelper_Test extends AbstractWComponentTestCase {

	@Test
	public void testRegisterList() {
		final String key = "LookupTableHelper_Test.testRegisterList.key";

		UIContext uic = createUIContext();
		UIContextHolder.pushContext(uic);

		MockHttpSession session = new MockHttpSession();
		ServletRequest request = new ServletRequest(new MockHttpServletRequest(session));

		LookupTableHelper.registerList(key, request);

		// Use a new request to ensure that it was stored as a session attribute
		request = new ServletRequest(new MockHttpServletRequest(session));

		Assert.assertSame("Incorrect context returned", uic, LookupTableHelper.getContext(key,
				request));
	}
}

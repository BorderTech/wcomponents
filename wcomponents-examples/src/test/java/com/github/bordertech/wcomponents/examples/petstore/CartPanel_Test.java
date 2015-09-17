package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CartPanel}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class CartPanel_Test {

	/**
	 * testConstructor - case2. check that the Cart is initially empty.
	 */
	@Test
	public void testConstructorCase2() {
		CartPanel cartPanel = new CartPanel();

		UIContextHolder.pushContext(new UIContextImpl());
		MockRequest request = new MockRequest();
		cartPanel.handleRequest(request);

		WComponent comp = cartPanel.getChildAt(0);
		Assert.assertTrue("first child is a WDataTable", comp instanceof WDataTable);
		WDataTable table = (WDataTable) comp;

		Assert.assertEquals("list of cartbeans should be empty - nothing in cart", 0, table.
				getDataModel().getRowCount());
	}

	@After
	public void resetContext() {
		UIContextHolder.reset();
	}
}

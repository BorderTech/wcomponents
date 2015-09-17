package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WComponentGroup}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WComponentGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testGroupAccessors() {
		WComponent item1 = new DefaultWComponent();
		WComponent item2 = new DefaultWComponent();
		WComponent item3 = new DefaultWComponent();

		WComponentGroup<WComponent> group = new WComponentGroup<>();

		// Group should be empty
		Assert.assertTrue("Group should be empty", group.getComponents().isEmpty());

		// Add an item
		group.addToGroup(item1);
		Assert.assertEquals("Group should have 1 item", 1, group.getComponents().size());
		Assert.assertEquals("Wrong item returned in list", item1, group.getComponents().get(0));
		// Remove an Item
		group.removeFromGroup(item1);
		// Group should be empty
		Assert.assertTrue("Group should be empty", group.getComponents().isEmpty());

		// Test User Context
		UIContext uic = createUIContext();

		// Setup Default Items
		group = new WComponentGroup<>();
		group.addToGroup(item1);
		group.addToGroup(item2);
		Assert.assertEquals("Default Group should have 2 items", 2, group.getComponents().size());
		Assert.assertTrue("Default Group should have item1", group.getComponents().contains(item1));
		Assert.assertTrue("Default Group should have item2", group.getComponents().contains(item2));

		// Add Item to User Context
		group.setLocked(true);
		setActiveContext(uic);
		group.addToGroup(item3);
		Assert.assertEquals("User Context Group should have 3 items", 3, group.getComponents().
				size());
		Assert.assertTrue("User Context Group should have item1", group.getComponents().contains(
				item1));
		Assert.assertTrue("User Context Group should have item2", group.getComponents().contains(
				item2));
		Assert.assertTrue("User Context Group should have item3", group.getComponents().contains(
				item3));

		resetContext();
		Assert.assertEquals("Default Group should have 2 items", 2, group.getComponents().size());
		Assert.assertTrue("Default Group should have item1", group.getComponents().contains(item1));
		Assert.assertTrue("Default Group should have item2", group.getComponents().contains(item2));

		// Remove Item from User Context
		setActiveContext(uic);
		group.removeFromGroup(item1);
		Assert.assertEquals("User Context Group should have 2 items", 2, group.getComponents().
				size());
		Assert.assertTrue("User Context Group should have item2", group.getComponents().contains(
				item2));
		Assert.assertTrue("User Context Group should have item3", group.getComponents().contains(
				item3));

		resetContext();
		Assert.assertEquals("Default Group should have 2 items", 2, group.getComponents().size());
		Assert.assertTrue("Default Group should have item1", group.getComponents().contains(item1));
		Assert.assertTrue("Default Group should have item2", group.getComponents().contains(item2));
	}
}

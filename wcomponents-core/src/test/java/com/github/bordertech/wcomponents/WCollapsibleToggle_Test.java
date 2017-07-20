package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WCollapsibleToggle_Test - Unit tests for {@link WCollapsibleToggle}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsibleToggle_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorSetGroup() {
		WCollapsibleToggle toggle = new WCollapsibleToggle();
		toggle.setIdName("myToggle");
		CollapsibleGroup group = new CollapsibleGroup();
		group.setCollapsibleToggle(toggle);
		Assert.assertSame(toggle, group.getCollapsibleToggle());
		Assert.assertEquals("myToggle", group.getGroupName());
	}

	@Test
	public void testConstructorBooleanDoesNothing() {
		WCollapsibleToggle toggle = new WCollapsibleToggle(true);
		CollapsibleGroup group = new CollapsibleGroup();
		group.setCollapsibleToggle(toggle);
		Assert.assertSame(toggle, group.getCollapsibleToggle());
		Assert.assertEquals(toggle.getId(), group.getGroupName());
	}

	@Test
	public void testConstructorWithGroup() {
		CollapsibleGroup group = new CollapsibleGroup();
		WCollapsibleToggle toggle = new WCollapsibleToggle(group);
		Assert.assertSame(toggle, group.getCollapsibleToggle());
	}

	@Test
	public void testGetGroup() {
		CollapsibleGroup group = new CollapsibleGroup();
		WCollapsibleToggle toggle = new WCollapsibleToggle(group);
		Assert.assertSame(group, toggle.getGroup());
	}

	@Test
	public void testGetGroupNoGroup() {
		WCollapsibleToggle toggle = new WCollapsibleToggle();
		Assert.assertNull(toggle.getGroup());
	}

	@Test
	public void testGetGroupName() {
		CollapsibleGroup group = new CollapsibleGroup();
		WCollapsibleToggle toggle = new WCollapsibleToggle(group);
		String expected = "expected";
		toggle.setIdName(expected);
		Assert.assertEquals(expected, toggle.getGroupName());
	}
	@Test
	public void testGetGroupNameNoGroup() {
		WCollapsibleToggle toggle = new WCollapsibleToggle();
		String expected = "expected";
		toggle.setIdName(expected);
		Assert.assertEquals(expected, toggle.getGroupName());
	}

	// pointless test to make sure no-one re-introduces server mode.
	@Test
	public void testClientSideAlwaysTrue() {
		WCollapsibleToggle toggle = new WCollapsibleToggle();
		Assert.assertTrue(toggle.isClientSideToggleable());
		toggle = new WCollapsibleToggle(false);
		Assert.assertTrue(toggle.isClientSideToggleable());
		toggle = new WCollapsibleToggle(true);
		Assert.assertTrue(toggle.isClientSideToggleable());
	}
}

package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * CollapsibleGroup_Test - Unit tests for {@link CollapsibleGroup}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CollapsibleGroup_Test {

	@Test
	public void testGetGroupName() {
		CollapsibleGroup group = new CollapsibleGroup();

		WPanel parent = new WPanel();
		WCollapsible collapsible1 = new WCollapsible(new WTextField(), "heading 1");
		WCollapsible collapsible2 = new WCollapsible(new WTextField(), "heading 2");
		WCollapsible collapsible3 = new WCollapsible(new WTextField(), "heading 3");
		parent.add(collapsible1);
		parent.add(collapsible2);
		parent.add(collapsible3);

		group.addCollapsible(collapsible1);
		group.addCollapsible(collapsible2);
		group.addCollapsible(collapsible3);

		Assert.assertEquals("Incorrect group name",
				collapsible1.getId(), group.getGroupName());
	}

	@Test
	public void testGetAllCollapsibles() {
		CollapsibleGroup group = new CollapsibleGroup();
		Assert.assertEquals("Collapsibles should be empty by default",
				0, group.getAllCollapsibles().size());

		WCollapsible collapsible1 = new WCollapsible(new WTextField(), "heading 1");
		WCollapsible collapsible2 = new WCollapsible(new WTextField(), "heading 2");

		group.addCollapsible(collapsible1);
		Assert.
				assertEquals("Incorrect number of collapsibles", 1, group.getAllCollapsibles().
						size());
		Assert.assertSame("Incorrect collapsible", collapsible1, group.getAllCollapsibles().get(0));

		group.addCollapsible(collapsible2);
		Assert.
				assertEquals("Incorrect number of collapsibles", 2, group.getAllCollapsibles().
						size());
		Assert.assertSame("Incorrect collapsible", collapsible1, group.getAllCollapsibles().get(0));
		Assert.assertSame("Incorrect collapsible", collapsible2, group.getAllCollapsibles().get(1));
	}

	@Test
	public void testGetCollapsibleToggle() {
		CollapsibleGroup group = new CollapsibleGroup();
		Assert.assertNull("Collapsible toggle should be null by default", group.
				getCollapsibleToggle());

		WCollapsibleToggle toggle = new WCollapsibleToggle();
		group.setCollapsibleToggle(toggle);
		Assert.assertSame("Incorrect collapsible toggle", toggle, group.getCollapsibleToggle());
	}
}

package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractContainer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractContainer_Test extends AbstractWComponentTestCase {

	@Test
	public void testChildAccessors() {
		AbstractContainer container = new MyContainer();

		// Check no children
		Assert.assertEquals("Should have no child count", 0, container.getChildCount());

		// Add child (the add is only visible to call because we are in the same package name)
		WComponent child = new DefaultWComponent();
		container.add(child);

		// Check child
		Assert.assertEquals("Incorrect child count", 1, container.getChildCount());
		Assert.assertEquals("Incorrect child index", 0, container.getIndexOfChild(child));
		Assert.assertEquals("Incorrect child returned", child, container.getChildAt(0));

		// Check getChildren
		Assert.assertEquals("Incorrect child count", 1, container.getChildren().size());
		Assert.assertEquals("Incorrect child index", 0, container.getChildren().indexOf(child));
		Assert.assertEquals("Incorrect child returned", child, container.getChildren().get(0));

		try {
			container.getChildren().add(new DefaultWComponent());
			Assert.fail("getChildren() should return unmodifiable List");
		} catch (UnsupportedOperationException e) {
			Assert.assertEquals("Incorrect child count", 1, container.getChildren().size());
		}
	}

	@Test
	public void testAddAll() {
		AbstractContainer container = new MyContainer();

		// Check no children
		Assert.assertEquals("Should have no child count", 0, container.getChildCount());

		WComponent child1 = new DefaultWComponent();
		WComponent child2 = new DefaultWComponent();

		container.addAll(child1, child2);

		// Check getChildren
		Assert.assertEquals("Incorrect child count", 2, container.getChildren().size());
		Assert.assertEquals("Incorrect child index", 0, container.getChildren().indexOf(child1));
		Assert.assertEquals("Incorrect child index", 1, container.getChildren().indexOf(child2));
		Assert.assertEquals("Incorrect child returned", child1, container.getChildren().get(0));
		Assert.assertEquals("Incorrect child returned", child2, container.getChildren().get(1));
	}

	/**
	 * Test instance of AbstractContainer.
	 */
	private static class MyContainer extends AbstractContainer {
	};

}

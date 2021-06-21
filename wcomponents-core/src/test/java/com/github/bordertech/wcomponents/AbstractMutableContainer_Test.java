package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractMutableContainer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractMutableContainer_Test extends AbstractWComponentTestCase {

	@Test
	public void testChildAccessors() {
		AbstractContainer container = new MyContainer();

		// Check no children
		Assert.assertEquals("Should have no child count", 0, container.getChildCount());

		// Add child
		WComponent child = new DefaultWComponent();
		container.add(child);

		// Check child
		Assert.assertEquals("Incorrect child count", 1, container.getChildCount());
		Assert.assertEquals("Incorrect child index", 0, container.getIndexOfChild(child));
		Assert.assertEquals("Incorrect child returned", child, container.getChildAt(0));

		// Remove child
		container.remove(child);

		// Check no children
		Assert.assertEquals("Should have no child count after removing", 0, container.getChildCount());

		// Check getChildren
		container.add(child);
		Assert.assertEquals("Incorrect child count", 1, container.getChildren().size());
		Assert.assertEquals("Incorrect child index", 0, container.getChildren().indexOf(child));
		Assert.assertEquals("Incorrect child returned", child, container.getChildren().get(0));

		try {
			container.getChildren().add(new DefaultWComponent());
			Assert.fail("Expected getChildren() to return unmodifiable List.");
		} catch (UnsupportedOperationException e) {
			Assert.assertEquals("Incorrect child count", 1, container.getChildren().size());
		}
	}

	@Test
	public void testAddAll() {
		AbstractMutableContainer container = new MyContainer();

		// Check no children
		Assert.assertEquals("Should have no child count", 0, container.getChildCount());

		// Add child
		WComponent child1 = new DefaultWComponent();
		WComponent child2 = new DefaultWComponent();

		container.addAll(child1, child2);

		// Check child
		Assert.assertEquals("Incorrect child count", 2, container.getChildCount());
		Assert.assertEquals("Incorrect child index", 0, container.getIndexOfChild(child1));
		Assert.assertEquals("Incorrect child index", 1, container.getIndexOfChild(child2));
		Assert.assertEquals("Incorrect child returned", child1, container.getChildAt(0));
		Assert.assertEquals("Incorrect child returned", child2, container.getChildAt(1));
	}

	/**
	 * Test instance of AbstractContainer.
	 */
	private static class MyContainer extends AbstractMutableContainer {
	};

}

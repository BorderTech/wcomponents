package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.fail;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author javapod@github.com
 * @since release2
 */
public class ComponentModel_Test {

	@Test    
	public void testComponentModelAddAndRemoveChild() {

		final ComponentModel model = new ComponentModel();
		final WContainer child = new WContainer();

		Assert.assertTrue("Expected child to be added",model.addChild(child));
		Assert.assertTrue("Expected model to have children", model.hasChildren());
		Assert.assertTrue("Expected child to be removed", model.removeChild(child));
		Assert.assertFalse("Expected model to have no children", model.hasChildren());
	}

	@Test
	public void testComponentModelGetChildren() {

		final ComponentModel model = new ComponentModel();
		final List<WComponent> children = model.getChildren();

		Assert.assertTrue("Expected children to be empty", children.isEmpty());

		try {
			children.add(new WContainer());
			fail("Expected add to throw UnsupportedOperationException.");
		} catch (UnsupportedOperationException e) {

		}
	}

	@Test
	public void testComponentModelSetChildren() {

		final ComponentModel model = new ComponentModel();
		final List<WComponent> children = new ArrayList<>();

		Assert.assertTrue("Expected children to be empty", children.isEmpty());

                model.setChildren(children);

		Assert.assertFalse("Expected model to have no children", model.hasChildren());

		model.setChildren(null);

		Assert.assertFalse("Expected model to have no children", model.hasChildren());

		final WContainer child = new WContainer();
		children.add(child);
		model.setChildren(children);
		children.remove(child);

		Assert.assertTrue("Expected model to have children", model.hasChildren());
                
		model.removeChild(child);

		Assert.assertFalse("Expected model to have no children", model.hasChildren());
        }
}

package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Disable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Disable_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Disable action = new Disable(target);

		Assert.assertEquals("Value for Disable should be false", Boolean.FALSE, action.getValue());
		Assert.assertEquals("Target for Disable should be the target", target, action.getTarget());
	}

	@Test
	public void testActionType() {
		Disable action = new Disable(new MyTarget());
		Assert.assertEquals("Incorrect Action Type", AbstractAction.ActionType.DISABLE, action.
				getActionType());
	}

	@Test
	public void testToString() {
		MyTarget target = new MyTarget();

		Disable action = new Disable(target);
		Assert.assertEquals("Incorrect toString for action", "disable MyTarget", action.toString());

		WLabel label = new WLabel("test label", target);
		Assert.assertEquals("Incorrect toString for action with a label", "disable " + label.
				getText(), action.toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}

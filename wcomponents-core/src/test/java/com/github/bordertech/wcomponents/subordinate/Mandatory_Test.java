package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Mandatory}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Mandatory_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Mandatory action = new Mandatory(target);

		Assert.assertEquals("Value for Mandatory should be true", Boolean.TRUE, action.getValue());
		Assert.assertEquals("Target for Mandatory should be the target", target, action.getTarget());
	}

	@Test
	public void testActionType() {
		Mandatory action = new Mandatory(new MyTarget());
		Assert.assertEquals("Incorrect Action Type", AbstractAction.ActionType.MANDATORY, action.
				getActionType());
	}

	@Test
	public void testToString() {
		MyTarget target = new MyTarget();

		Mandatory action = new Mandatory(target);
		Assert.assertEquals("Incorrect toString for action", "set MyTarget mandatory", action.
				toString());

		WLabel label = new WLabel("test label", target);
		Assert.assertEquals("Incorrect toString for action with a label",
				"set " + label.getText() + " mandatory", action.toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}

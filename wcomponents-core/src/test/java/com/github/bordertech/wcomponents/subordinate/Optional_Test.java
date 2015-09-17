package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WLabel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Optional}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Optional_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		SubordinateTarget target = new MyTarget();
		Optional action = new Optional(target);

		Assert.assertEquals("Value for Optional should be false", Boolean.FALSE, action.getValue());
		Assert.assertEquals("Target for Optional should be the target", target, action.getTarget());
	}

	@Test
	public void testActionType() {
		Optional action = new Optional(new MyTarget());
		Assert.assertEquals("Incorrect Action Type", AbstractAction.ActionType.OPTIONAL, action.
				getActionType());
	}

	@Test
	public void testToString() {
		MyTarget target = new MyTarget();

		Optional action = new Optional(target);
		Assert.assertEquals("Incorrect toString for action", "set MyTarget optional", action.
				toString());

		WLabel label = new WLabel("test label", target);
		Assert.assertEquals("Incorrect toString for action with a label",
				"set " + label.getText() + " optional", action.toString());
	}

	/**
	 * Test component that implements the SubordinateTarget interface.
	 */
	private static class MyTarget extends AbstractWComponent implements SubordinateTarget {
	}
}

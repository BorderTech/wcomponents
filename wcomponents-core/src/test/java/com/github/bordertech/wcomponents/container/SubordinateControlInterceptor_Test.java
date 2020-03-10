package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Cases for the {@link SubordinateControlInterceptor} class.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlInterceptor_Test extends AbstractWComponentTestCase {

	@Test
	public void testServiceRequestApplyControls() {

		ButtonState state = new ButtonState();

		// Create Target
		WButton target = new WButton();
		target.setAction((final ActionEvent event) -> {
			state.setClicked(true);
		});

		// Create Control - Enable/Disable Button
		WCheckBox box = new WCheckBox();
		Rule rule = new Rule();
		rule.setCondition(new Equal(box, Boolean.TRUE));
		rule.addActionOnTrue(new Enable(target));
		rule.addActionOnFalse(new Disable(target));
		WSubordinateControl control = new WSubordinateControl();
		control.addRule(rule);

		// Create component tree
		WContainer root = new WContainer();
		root.add(control);
		root.add(box);
		root.add(target);

		// Setup Intercepter
		SubordinateControlInterceptor interceptor = new SubordinateControlInterceptor();
		interceptor.setBackingComponent(root);

		UIContext uic = createUIContext();
		uic.setUI(root);

		setActiveContext(uic);
		state.setClicked(false);

		// Test Service Request - Empty Request and no control registered, so the control should not be applied
		MockRequest request = new MockRequest();
		interceptor.serviceRequest(request);
		// Target should still be enabled (control not applied)
		Assert.assertFalse("After service request target should be enabled", target.isDisabled());
		// Button not clicked
		Assert.assertFalse("Button should not have been clicked", state.isClicked());

		// Test Service Request - Try to click button while it is disabled and should not be clicked
		target.setDisabled(true);
		request.setParameter(target.getId(), "x");
		interceptor.serviceRequest(request);
		// Target should still be disabled (control not applied, as still not registered)
		Assert.assertTrue("After service request target should be disabled", target.isDisabled());
		// Button not clicked
		Assert.assertFalse("Button should not have been clicked while disabled", state.isClicked());

		// Test Prepare Paint - Should register and apply the subordinate control
		target.setDisabled(false);
		request = new MockRequest();
		interceptor.preparePaint(request);
		// Target should be disabled (Disabled by control as box is not selected)
		Assert.assertTrue("After service request target should be disabled", target.isDisabled());

		// Test Service Request - Simulate button click as it was enabled on the client by the check box being selected.
		// As the controls have been registered from the Prepare Paint, they will be applied in the Service Request and
		// this will enable the button and allow it to be clicked.
		state.setClicked(false);
		request.setParameter(target.getId(), "x");
		setupCheckBoxRequest(box, request, true);
		interceptor.serviceRequest(request);
		// Target should be enabled (enabled by control as box is selected)
		Assert.assertFalse("After service request target should be enabled", target.isDisabled());
		// Button should have been clicked
		Assert.assertTrue("Button should have been clicked", state.isClicked());
	}

	/**
	 * @param target the subordinate check box target
	 * @param request the request to be processed
	 * @param condition the flag if the check box is selected
	 */
	private void setupCheckBoxRequest(final WCheckBox target, final MockRequest request,
			final boolean condition) {
		if (condition) {
			request.setParameter(target.getId(), "true");
		}
	}

	/**
	 * Hold the state of the button through the tests.
	 */
	private static class ButtonState {

		private boolean clicked = false;

		public boolean isClicked() {
			return clicked;
		}

		public void setClicked(final boolean clicked) {
			this.clicked = clicked;
		}
	}

}

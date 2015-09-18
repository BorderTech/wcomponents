package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.SubordinateControlHelper;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test Cases for the {@link SubordinateControlInterceptor} class.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SubordinateControlInterceptor_Test extends AbstractWComponentTestCase {

	private boolean buttonClicked;

	@Test
	public void testServiceRequestApplyControls() {
		// Create Target
		WButton target = new WButton();
		target.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				buttonClicked = true;
			}
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
		buttonClicked = false;

		// Test Service Request - Empty Request and no control registered, so the control should not be applied
		MockRequest request = new MockRequest();
		interceptor.serviceRequest(request);
		// Target should still be enabled (control not applied)
		Assert.assertFalse("After service request target should be enabled", target.isDisabled());
		// Button not clicked
		Assert.assertFalse("Button should not have been clicked", buttonClicked);

		// Test Service Request - Try to click button while it is disabled and should not be clicked
		target.setDisabled(true);
		request.setParameter(target.getId(), "x");
		interceptor.serviceRequest(request);
		// Target should still be disabled (control not applied, as still not registered)
		Assert.assertTrue("After service request target should be disabled", target.isDisabled());
		// Button not clicked
		Assert.assertFalse("Button should not have been clicked while disabled", buttonClicked);

		// Test Prepare Paint - Should register and apply the subordinate control
		target.setDisabled(false);
		request = new MockRequest();
		interceptor.preparePaint(request);
		// Target should be disabled (Disabled by control as box is not selected)
		Assert.assertTrue("After service request target should be disabled", target.isDisabled());
		// Check Subordinate Controls have been registered
		Assert.assertNotNull("Registered Controls should be registered on the session",
				request.
				getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));

		// Test Service Request - Simulate button click as it was enabled on the client by the check box being selected.
		// As the controls have been registered from the Prepare Paint, they will be applied in the Service Request and
		// this will enable the button and allow it to be clicked.
		buttonClicked = false;
		request.setParameter(target.getId(), "x");
		setupCheckBoxRequest(box, request, true);
		interceptor.serviceRequest(request);
		// Target should be enabled (enabled by control as box is selected)
		Assert.assertFalse("After service request target should be enabled", target.isDisabled());
		// Button should have been clicked
		Assert.assertTrue("Button should have been clicked", buttonClicked);

//        // Check Subordinate Controls have not been cleared from session
//        Assert.assertNotNull("Registered Controls should not have been cleared on the session",
//                          request.getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));
//
//        interceptor.preparePaint(request);
//
//        // Check Subordinate Controls have been cleared from session
//        Assert.assertNull("Registered Controls should have been cleared on the session",
//                          request.getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));
	}

//    @Test
//    public void testServiceRequestApplyOneContext()
//    {
//        final UIContext uic1 = new UIContextImpl();
//        final UIContext uic2 = new UIContextImpl();
//
//        // Create Target
//        WButton target = new WButton("true");
//        target.setAction(new Action()
//        {
//            @Override
//            public void execute(final ActionEvent event)
//            {
//                buttonClicked = true;
//            }
//        });
//
//        // Create Control - Enable/Disable Button
//        WCheckBox box = new WCheckBox();
//        Rule rule = new Rule();
//        rule.setCondition(new Equal(box, Boolean.TRUE)
//        {
//            @Override
//            protected boolean execute(final Request request)
//            {
//                if (UIContextHolder.getCurrent() == uic2)
//                {
//                    Assert.fail("Apply Controls should not have been executed for uic2");
//                }
//
//                return super.execute(request);
//            }
//        });
//        rule.addActionOnTrue(new Enable(target));
//        rule.addActionOnFalse(new Disable(target));
//        WSubordinateControl control = new WSubordinateControl();
//        control.addRule(rule);
//
//        // Create component tree
//        WContainer root = new WContainer();
//        root.add(control);
//        root.add(box);
//        root.add(target);
//        root.setLocked(true);
//
//        // Setup Intercepter
//        SubordinateControlInterceptor interceptor = new SubordinateControlInterceptor();
//        interceptor.setBackingComponent(root);
//
//        // Register the controls
//        MockRequest request = new MockRequest();
//        // UIC1 - Prepare Paint
//        setActiveContext(uic1);
//        target.setDisabled(false);
//        interceptor.preparePaint(request);
//        // UIC2 - Prepare Paint
//        setActiveContext(uic2);
//        target.setDisabled(false);
//        interceptor.preparePaint(request);
//
//        // Check Subordinate Controls have been registered
//        Assert.assertNotNull("Registered Controls should be registered on the session",
//                             request.getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));
//        Assert.assertEquals("Should have 2 registered controls on the session", 2, ((List) request
//            .getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY)).size());
//
//        // Setup request for UIC1
//        buttonClicked = false;
//        setActiveContext(uic1);
//        request.setParameter(target.getId(), "x");
//        setupCheckBoxRequest(box, request, true);
//        interceptor.serviceRequest(request);
//
//        // Check control is only applied for UIC1
//        // UIC1 - Target should be enabled (enabled by control as box is selected)
//        Assert.assertFalse("After service request target uic1 should be enabled", target.isDisabled());
//        // UIC2 - Target should be disabled
//        setActiveContext(uic2);
//        Assert.assertTrue("After service request target uic2 should be disabled", target.isDisabled());
//
//        // Check Subordinate Controls have been cleared from session
//        Assert.assertNull("Registered Controls should have been cleared on the session",
//                          request.getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));
//
//        // Check for AJAX operation only UIC1 should be applied and only UIC1 cleared from registry.
//
//        // Register the controls
//        request = new MockRequest();
//        // UIC1 - Prepare Paint
//        setActiveContext(uic1);
//        target.setDisabled(false);
//        interceptor.preparePaint(request);
//        // UIC2 - Prepare Paint
//        setActiveContext(uic2);
//        target.setDisabled(false);
//        interceptor.preparePaint(request);
//
//        // Setup request for UIC1
//        try
//        {
//            setActiveContext(uic1);
//            // Setup AJAX OPeration
//            AjaxHelper.setCurrentOperation(new AjaxOperation("", new UIContextImpl(), new WPanel(),
//                                                             AjaxOperation.Mode.REPLACE));
//            buttonClicked = false;
//            request.setParameter(target.getId(), "x");
//            setupCheckBoxRequest(box, request, true);
//            interceptor.serviceRequest(request);
//        }
//        finally
//        {
//            // Clear AJAX operation
//            AjaxHelper.setCurrentOperation(null);
//        }
//
////        // Check control is only applied for UIC1
////        // UIC1 - Target should be enabled (enabled by control as box is selected)
////        Assert.assertFalse("After service request target uic1 should be enabled", target.isDisabled());
////        // UIC2 - Target should be disabled
////        setActiveContext(uic2);
////        Assert.assertTrue("After service request target uic2 should be disabled", target.isDisabled());
////
////        // Check UIC1 controls have been removed but UIC2 is still registered
////        Assert.assertNotNull("UIC2 Registered Controls should still be on the session",
////                             request.getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY));
////        List<SubordinateOperation> operations = (List<SubordinateOperation>) request
////            .getSessionAttribute(SubordinateControlHelper.SUBORDINATE_CONTROL_SESSION_KEY);
////        // UIC2 operations should still be on the session
////        Assert.assertEquals("Should have 1 registered control on the session from uic2", 1, operations.size());
////        Assert.assertEquals("Registered Conterol should be for uic2", uic2, (operations.get(0)).getUic());
//
//    }
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

}

package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WAjaxControl}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAjaxControl_Test extends AbstractWComponentTestCase {

	@Test
	public void testTriggerConstructor() {
		AjaxTrigger trigger = new WButton();
		WAjaxControl control = new WAjaxControl(trigger);

		Assert.assertSame("Incorrect trigger", trigger, control.getTrigger());
		Assert.assertFalse("Should not be load once", control.isLoadOnce());
		Assert.assertEquals("Load count should not be set", -1, control.getLoadCount());
		Assert.assertTrue("Targets should be empty", control.getTargets().isEmpty());
		Assert.assertEquals("Delay should not be set", 0, control.getDelay());
	}

	@Test
	public void testTriggerTargetConstructor() {
		AjaxTrigger trigger = new WDropdown();
		AjaxTarget target = new WPanel();
		WAjaxControl control = new WAjaxControl(trigger, target);

		Assert.assertSame("Incorrect trigger", trigger, control.getTrigger());
		Assert.assertFalse("Should not be load once", control.isLoadOnce());
		Assert.assertEquals("Load count should not be set", -1, control.getLoadCount());
		Assert.assertEquals("Delay should not be set", 0, control.getDelay());
		Assert.assertEquals("Incorrect target", Arrays.asList(target), control.getTargets());
	}

	@Test
	public void testTriggerTargetArrayConstructor() {
		AjaxTrigger trigger = new WCheckBox();
		AjaxTarget[] targets = new AjaxTarget[]{new WPanel(), new WTextField()};
		WAjaxControl control = new WAjaxControl(trigger, targets);

		Assert.assertSame("Incorrect trigger", trigger, control.getTrigger());
		Assert.assertFalse("Should not be load once", control.isLoadOnce());
		Assert.assertEquals("Load count should not be set", -1, control.getLoadCount());
		Assert.assertEquals("Delay should not be set", 0, control.getDelay());
		Assert.assertEquals("Incorrect targets list", Arrays.asList(targets), control.getTargets());
		Assert.assertTrue("Incorrect targets array", Arrays.equals(targets, control.
				getTargetsArray()));
	}

	@Test
	public void testTriggerTargetListConstructor() {
		AjaxTrigger trigger = new WCheckBox();
		List<AjaxTarget> targets = Arrays.asList(new AjaxTarget[]{new WPanel(), new WTextField()});
		WAjaxControl control = new WAjaxControl(trigger, targets);

		Assert.assertSame("Incorrect trigger", trigger, control.getTrigger());
		Assert.assertFalse("Should not be load once", control.isLoadOnce());
		Assert.assertEquals("Load count should not be set", -1, control.getLoadCount());
		Assert.assertEquals("Delay should not be set", 0, control.getDelay());
		Assert.assertEquals("Incorrect targets", targets, control.getTargets());
	}

	@Test
	public void testLoadOnceAccessors() {
		WAjaxControl control = new WAjaxControl(new WButton());
		assertAccessorsCorrect(control, "loadOnce", Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);

		control.setLoadCount(0);
		Assert.assertFalse(control.isLoadOnce());

		control.setLoadCount(1);
		Assert.assertTrue(control.isLoadOnce());

		control.setLoadCount(2);
		Assert.assertFalse(control.isLoadOnce());
	}

	@Test
	public void testAddTarget() {
		AjaxTarget target1 = new WPanel();
		AjaxTarget target2 = new WPanel();

		WAjaxControl control = new WAjaxControl(new WButton());
		Assert.assertTrue("Targets should be empty", control.getTargets().isEmpty());

		control.addTarget(target1);
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1), control.getTargets());

		control.addTarget(target2);
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1, target2), control.
				getTargets());
	}

	@Test
	public void testAddTargetsArray() {
		AjaxTarget target1 = new WPanel();
		AjaxTarget target2 = new WPanel();
		AjaxTarget target3 = new WPanel();

		WAjaxControl control = new WAjaxControl(new WButton());
		Assert.assertTrue("Targets should be empty", control.getTargets().isEmpty());

		control.addTargets(new AjaxTarget[]{target1, target2});
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1, target2), control.
				getTargets());

		control.addTargets(new AjaxTarget[]{target3});
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1, target2, target3), control.
				getTargets());
	}

	@Test
	public void testAddTargetsList() {
		AjaxTarget target1 = new WPanel();
		AjaxTarget target2 = new WPanel();
		AjaxTarget target3 = new WPanel();

		WAjaxControl control = new WAjaxControl(new WButton());
		Assert.assertTrue("Targets should be empty", control.getTargets().isEmpty());

		control.addTargets(Arrays.asList(target1, target2));
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1, target2), control.
				getTargets());

		control.addTargets(Arrays.asList(target3));
		Assert.assertEquals("Incorrect targets", Arrays.asList(target1, target2, target3), control.
				getTargets());
	}

	@Test
	public void testPreparePaintComponent() {
		WApplication app = new WApplication();
		AjaxTrigger trigger = new WButton();
		AjaxTarget target = new WPanel();
		WAjaxControl control = new WAjaxControl(trigger, target);
		app.add(control);
		app.add(trigger);
		app.add(target);
		app.setLocked(true);

		UIContext uic = createUIContext();
		uic.setUI(app);
		setActiveContext(uic);

		MockRequest req = new MockRequest();
		control.handleRequest(req);
		control.preparePaint(req);

		AjaxOperation ajaxOperation = AjaxHelper.getAjaxOperation(trigger.getId(), req);
		Assert.assertNotNull("Ajax operation should have been registered", ajaxOperation);
		Assert.assertEquals("Incorrect ajax operation trigger id", trigger.getId(), ajaxOperation.
				getTriggerId());
		Assert.assertEquals("Incorrect ajax operation targets", target.getId(), ajaxOperation.
				getTargets().get(0));
	}

	@Test
	public void testLoadCountAccessors() {
		WAjaxControl control = new WAjaxControl(new WCheckBox());
		assertAccessorsCorrect(control, "loadCount", -1, 1, 2);
	}

	@Test
	public void testDelayAccessors() {
		WAjaxControl control = new WAjaxControl(new WCheckBox());
		assertAccessorsCorrect(control, "delay", 0, 1, 2);
	}
}

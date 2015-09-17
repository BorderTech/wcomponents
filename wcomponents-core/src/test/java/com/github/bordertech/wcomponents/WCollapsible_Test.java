package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WCollapsible_Test - Unit tests for {@link WCollapsible}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsible_Test extends AbstractWComponentTestCase {

	@Test
	public void testSetCollapsed() {
		WCollapsible collapsible = new WCollapsible(new WText(""), "");
		collapsible.setCollapsed(false);

		Assert.assertFalse("Collapsible should not be collapsed by default", collapsible.
				isCollapsed());

		collapsible.setLocked(true);
		setActiveContext(createUIContext());
		collapsible.setCollapsed(true);
		Assert.assertTrue("Collapsible should be collapsed for affected context", collapsible.
				isCollapsed());

		resetContext();
		Assert.assertFalse("Collapsible should not be collapsed for other contexts", collapsible.
				isCollapsed());

		collapsible = new WCollapsible(new WText(""), "");
		collapsible.setCollapsed(true);
		collapsible.setLocked(true);

		Assert.assertTrue("Collapsible should be collapsed by default", collapsible.isCollapsed());

		setActiveContext(createUIContext());
		collapsible.setCollapsed(false);
		Assert.assertFalse("Collapsible should not be collapsed for affected context", collapsible.
				isCollapsed());

		resetContext();
		Assert.assertTrue("Collapsible should be collapsed for other contexts", collapsible.
				isCollapsed());
	}

	@Test
	public void testGetContent() {
		WComponent content = new WText();
		WCollapsible collapsible = new WCollapsible(content, "");
		Assert.assertSame("Incorrect content returned", content, collapsible.getContent());
	}

	@Test
	public void testSetHeading() {
		String heading1 = "WCollapsible_Test.testGetLabel.heading1";
		String heading2 = "WCollapsible_Test.testGetLabel.heading2";

		WCollapsible collapsible = new WCollapsible(new WText(""), heading1);
		Assert.assertEquals("Incorrect heading", heading1, collapsible.getHeading());

		collapsible.setLocked(true);
		setActiveContext(createUIContext());
		collapsible.setHeading(heading2);
		Assert.assertEquals("Incorrect dynamic heading for affected context", heading2, collapsible.
				getHeading());

		resetContext();
		Assert.assertEquals("Incorrect static heading after dynamic setHeading", heading1,
				collapsible.getHeading());
	}

	@Test
	public void testGetGroupName() {
		WCollapsible collapsible = new WCollapsible(new WText(""), "");
		Assert.assertEquals("Incorrect group name", collapsible.getId(), collapsible.getGroupName());

		CollapsibleGroup group = new CollapsibleGroup();
		collapsible.setGroup(group);
		Assert.
				assertEquals("Incorrect group name", group.getGroupName(), collapsible.
						getGroupName());
	}

	@Test
	public void testHandleRequestClientSide() {
		WCollapsible collapsible = new WCollapsible(new WText(""), "",
				WCollapsible.CollapsibleMode.CLIENT);
		collapsible.setCollapsed(false);

		collapsible.setLocked(true);
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(collapsible.getId(), "closed");

		collapsible.handleRequest(request);
		Assert.assertTrue("Collapsible should be collapsed after handleRequest to close",
				collapsible.isCollapsed());

		request.setParameter(collapsible.getId(), "open");
		Assert.assertTrue("Collapsible should not be collapsed after handleRequest to open",
				collapsible.isCollapsed());
	}

	@Test
	public void testMarginAccessors() {
		WCollapsible collapsible = new WCollapsible(new WText(""), "",
				WCollapsible.CollapsibleMode.CLIENT);
		assertAccessorsCorrect(collapsible, "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testHeadingLevelAccessors() {
		WCollapsible collapsible = new WCollapsible(new WText(""), "",
				WCollapsible.CollapsibleMode.CLIENT);
		assertAccessorsCorrect(collapsible, "headingLevel", null, HeadingLevel.H1, HeadingLevel.H2);
	}

}

package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WFieldSet.FrameType;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WFieldSet}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSet_Test extends AbstractWComponentTestCase {

	@Test
	public void testFrameTypeAccessors() {
		assertAccessorsCorrect(new WFieldSet(""), "frameType", FrameType.NORMAL, FrameType.NO_TEXT,
				FrameType.NO_BORDER);
	}

	@Test
	public void testSetTitle() {
		String titleText = "WFieldSet_Test.titleText";

		// Test default title
		WFieldSet fieldSet = new WFieldSet(titleText);
		Assert.assertEquals("Static title incorrect", titleText, fieldSet.getTitle().getText());

		WComponent titleComponent = new WLabel("dummy");
		fieldSet.setTitle(titleComponent);
		Assert.assertSame("Incorrect title", titleComponent, fieldSet.getTitle().getBody());
	}

	@Test
	public void testSetMandatory() {
		assertAccessorsCorrect(new WFieldSet(""), "mandatory", false, true, false);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WFieldSet(""), "margin", null, new Margin(1), new Margin(2));
	}

}

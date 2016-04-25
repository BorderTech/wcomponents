package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WSection.SectionMode;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WSection_Test - Unit tests for {@link WSection}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSection_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WSection section = new WSection("label");
		WText txt = (WText) section.getDecoratedLabel().getBody();
		Assert.assertEquals("Constructor - Incorrect label", "label", txt.getText());
		Assert.assertNotNull("Constructor - Content should not be null by default", section.
				getContent());
	}

	@Test
	public void testConstructor2() {
		WPanel panel = new WPanel();
		WSection section = new WSection(panel, "label");
		WText txt = (WText) section.getDecoratedLabel().getBody();
		Assert.assertEquals("Constructor - Incorrect label", "label", txt.getText());
		Assert.assertSame("Constructor - Incorrect content", panel, section.getContent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor3() {
		new WSection(null, new WDecoratedLabel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor4() {
		new WSection(new WPanel(), (WDecoratedLabel) null);
	}

	@Test
	public void testModeAccessors() {
		assertAccessorsCorrect(new WSection(""), "mode", null, SectionMode.EAGER, SectionMode.LAZY);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WSection(""), "margin", null, new Margin(1), new Margin(2));
	}
}

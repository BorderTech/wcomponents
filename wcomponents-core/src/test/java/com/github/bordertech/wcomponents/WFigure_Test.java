package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WFigure.FigureMode;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WFigure_Test - Unit tests for {@link WFigure}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFigure_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WText content = new WText();
		WFigure figure = new WFigure(content, "label");
		WText txt = (WText) figure.getDecoratedLabel().getBody();
		Assert.assertEquals("Constructor - Incorrect label", "label", txt.getText());
		Assert.assertSame("Constructor - Incorrect content", content, figure.getContent());
	}

	@Test
	public void testConstructor2() {
		WText content = new WText();
		WFigure figure = new WFigure(content, "label");
		WText txt = (WText) figure.getDecoratedLabel().getBody();
		Assert.assertEquals("Constructor2 - Incorrect label", "label", txt.getText());
		Assert.assertSame("Constructor2 - Incorrect content", content, figure.getContent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor3() {
		new WFigure(null, new WDecoratedLabel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor4() {
		new WFigure(new WPanel(), (WDecoratedLabel) null);
	}

	@Test
	public void testModeAccessors() {
		assertAccessorsCorrect(new WFigure(new WText(), ""), "mode", null, FigureMode.EAGER,
				FigureMode.LAZY);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WFigure(new WText(), ""), "margin", null, new Margin(1),
				new Margin(2));
	}
}

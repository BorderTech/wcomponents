package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link WTableColumn}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class WTableColumn_Test extends AbstractWComponentTestCase {

	@Test
	public void testAlignAccessors() {
		assertAccessorsCorrect(new WTableColumn("align", WText.class), "align", null,
				WTableColumn.Alignment.LEFT,
				WTableColumn.Alignment.RIGHT);
	}

	@Test
	public void testWidthAccessors() {
		assertAccessorsCorrect(new WTableColumn("width", WText.class), "width", null, "10%", "200px");
	}

	@Test
	public void testSetWidthRange() {
		WTableColumn col = new WTableColumn("width2", WText.class);

		col.setWidth(-1);
		Assert.assertEquals("Incorrect width percentage from setter -1", null, col.getWidth());

		col.setWidth(0);
		Assert.assertEquals("Incorrect width percentage from setter 0", null, col.getWidth());

		col.setWidth(100);
		Assert.assertEquals("Incorrect width percentage from setter 100", "100%", col.getWidth());
	}

	@Test
	public void testSetWidthStrRange() {
		WTableColumn col = new WTableColumn("width3", WText.class);

		col.setWidth("");
		Assert.assertEquals("Incorrect width from setter ''", null, col.getWidth());

		col.setWidth("auto");
		Assert.assertEquals("Incorrect width percentage from setter 'auto'", null , col.getWidth());

		col.setWidth("5em");
		Assert.assertEquals("Incorrect width percentage from setter '5em'", "5em", col.getWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWidthInvalidGreater100() {
		WTableColumn col = new WTableColumn("width3", WText.class);
		col.setWidth(101);
	}

	@Test
	public void testGetRendererClass() {
		WTableColumn column = new WTableColumn("dummy", WText.class);
		Assert.assertSame("Incorrect renderer class", WText.class, column.getRendererClass());
	}
}

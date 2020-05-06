package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link WTableColumn}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class WTableColumn_Test extends AbstractWComponentTestCase {

	private static final String HEADING_TEXT = "Heading";
	private static final Class RENDER_CLASS = WText.class;

	@Test
	public void testConstructor1() {
		WTableColumn col = new WTableColumn(HEADING_TEXT, RENDER_CLASS);
		Assert.assertEquals("Contructor 1 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 1 - Invalid renderer class", RENDER_CLASS, col.getRendererClass());
		Assert.assertNull("Contructor 1 - Renderer should be null", col.getRenderer());
		Assert.assertNull("Contructor 1 - Footer Renderer should be null", col.getFooterRender());
	}

	@Test
	public void testConstructor2() {
		WText renderer = new WText();
		WTableColumn col = new WTableColumn(HEADING_TEXT, renderer);
		Assert.assertEquals("Contructor 2 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 2 - Invalid renderer class", renderer.getClass(), col.getRendererClass());
		Assert.assertEquals("Contructor 2 - Invalid renderer", renderer, col.getRenderer());
		Assert.assertNull("Contructor 2 - Footer Renderer should be null", col.getFooterRender());
	}

	@Test
	public void testConstructor3() {
		WDecoratedLabel label = new WDecoratedLabel(HEADING_TEXT);
		WTableColumn col = new WTableColumn(label, RENDER_CLASS);
		Assert.assertEquals("Contructor 3 - Invalid label", label, col.getColumnLabel());
		Assert.assertEquals("Contructor 3 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 3 - Invalid renderer class", RENDER_CLASS, col.getRendererClass());
		Assert.assertNull("Contructor 3 - Renderer should be null", col.getRenderer());
		Assert.assertNull("Contructor 3 - Footer Renderer should be null", col.getFooterRender());
	}

	@Test
	public void testConstructor4() {
		WText renderer = new WText();
		WDecoratedLabel label = new WDecoratedLabel(HEADING_TEXT);
		WTableColumn col = new WTableColumn(label, renderer);
		Assert.assertEquals("Contructor 4 - Invalid label", label, col.getColumnLabel());
		Assert.assertEquals("Contructor 4 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 4 - Invalid renderer class", renderer.getClass(), col.getRendererClass());
		Assert.assertEquals("Contructor 4 - Invalid renderer", renderer, col.getRenderer());
		Assert.assertNull("Contructor 4 - Footer Renderer should be null", col.getFooterRender());
	}

	@Test
	public void testConstructor5() {
		WContainer footer = new WContainer();
		WTableColumn col = new WTableColumn(HEADING_TEXT, RENDER_CLASS, footer);
		Assert.assertEquals("Contructor 5 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 5 - Invalid renderer class", RENDER_CLASS, col.getRendererClass());
		Assert.assertNull("Contructor 5 - Renderer should be null", col.getRenderer());
		Assert.assertEquals("Contructor 5 - Invalid footer", footer, col.getFooterRender());
	}

	@Test
	public void testConstructor6() {
		WContainer footer = new WContainer();
		WText renderer = new WText();
		WTableColumn col = new WTableColumn(HEADING_TEXT, renderer, footer);
		Assert.assertEquals("Contructor 6 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 6 - Invalid renderer class", renderer.getClass(), col.getRendererClass());
		Assert.assertEquals("Contructor 6 - Invalid renderer", renderer, col.getRenderer());
		Assert.assertEquals("Contructor 6 - Invalid footer", footer, col.getFooterRender());
	}

	@Test
	public void testConstructor7() {
		WContainer footer = new WContainer();
		WDecoratedLabel label = new WDecoratedLabel(HEADING_TEXT);
		WTableColumn col = new WTableColumn(label, RENDER_CLASS, footer);
		Assert.assertEquals("Contructor 7 - Invalid label", label, col.getColumnLabel());
		Assert.assertEquals("Contructor 7 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 7 - Invalid renderer class", RENDER_CLASS, col.getRendererClass());
		Assert.assertNull("Contructor 7 - Renderer should be null", col.getRenderer());
		Assert.assertEquals("Contructor 7 - Invalid footer", footer, col.getFooterRender());
	}

	@Test
	public void testConstructor8() {
		WContainer footer = new WContainer();
		WText renderer = new WText();
		WDecoratedLabel label = new WDecoratedLabel(HEADING_TEXT);
		WTableColumn col = new WTableColumn(label, renderer, footer);
		Assert.assertEquals("Contructor 8 - Invalid label", label, col.getColumnLabel());
		Assert.assertEquals("Contructor 8 - Invalid heading text", HEADING_TEXT, col.getColumnLabel().getText());
		Assert.assertEquals("Contructor 8 - Invalid renderer class", renderer.getClass(), col.getRendererClass());
		Assert.assertEquals("Contructor 8 - Invalid renderer", renderer, col.getRenderer());
		Assert.assertEquals("Contructor 8 - Invalid footer", footer, col.getFooterRender());
	}

	@Test
	public void testAlignAccessors() {
		assertAccessorsCorrect(new WTableColumn("align", WText.class), WTableColumn::getAlign, WTableColumn::setAlign,
			null, WTableColumn.Alignment.LEFT, WTableColumn.Alignment.RIGHT);
	}

	@Test
	public void testWidthAccessors() {
		assertAccessorsCorrect(new WTableColumn("width", WText.class), WTableColumn::getWidth, WTableColumn::setWidth, 0, 1, 2);
	}

	@Test
	public void testSetWidthRange() {
		WTableColumn col = new WTableColumn("width2", WText.class);

		col.setWidth(-1);
		Assert.assertEquals("Incorrect width percentage from setter -1", 0, col.getWidth());

		col.setWidth(0);
		Assert.assertEquals("Incorrect width percentage from setter 0", 0, col.getWidth());

		col.setWidth(100);
		Assert.assertEquals("Incorrect width percentage from setter 100", 100, col.getWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWidthInvalidGreater100() {
		WTableColumn col = new WTableColumn("width3", WText.class);
		col.setWidth(101);
	}

}

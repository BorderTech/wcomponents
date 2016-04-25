package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WColumn.Alignment;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WColumn_Test - Unit tests for {@link WColumn}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WColumn_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor1() {
		WColumn col = new WColumn();
		Assert.
				assertEquals("Incorrect default width percentage from constructor", 0, col.
						getWidth());
	}

	@Test
	public void testConstructor2() {
		WColumn col = new WColumn(10);
		Assert.assertEquals("Incorrect width percentage from constructor", 10, col.getWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2Invalid() {
		new WColumn(-1);
	}

	@Test
	public void testAlignmentAccessors() {
		assertAccessorsCorrect(new WColumn(10), "alignment", Alignment.LEFT, Alignment.CENTER,
				Alignment.RIGHT);
	}

	@Test
	public void testWidthAccessors() {
		assertAccessorsCorrect(new WColumn(10), "width", 10, 2, 3);
	}

	@Test
	public void testSetWidthRange() {
		WColumn col = new WColumn(10);

		col.setWidth(1);
		Assert.assertEquals("Incorrect width percentage from setter 1", 1, col.getWidth());

		col.setWidth(100);
		Assert.assertEquals("Incorrect width percentage from setter 100", 100, col.getWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWidthInvalidLessOne() {
		WColumn col = new WColumn(10);
		col.setWidth(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWidthInvalidGreater100() {
		WColumn col = new WColumn(10);
		col.setWidth(101);
	}

}

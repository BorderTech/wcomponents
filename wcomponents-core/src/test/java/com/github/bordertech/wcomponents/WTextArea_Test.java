package com.github.bordertech.wcomponents;

import org.junit.Test;

/**
 * Unit tests for {@link WTextArea}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTextArea_Test extends AbstractWComponentTestCase {

	@Test
	public void testRowsAccessors() {
		assertAccessorsCorrect(new WTextArea(), "rows", 0, 1, 2);
	}

	@Test
	public void testRTFAccessors() {
		assertAccessorsCorrect(new WTextArea(), "richTextArea", false, true, false);
	}

}

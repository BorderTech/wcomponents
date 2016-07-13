package com.github.bordertech.wcomponents.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for HtmlClassProperties.
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HtmlClassPropertiesTest {


	@Test
	public void testToString() {
		for (HtmlClassProperties className : HtmlClassProperties.values()) {
			Assert.assertTrue(className.toString().indexOf("wc-") == 0);
		}
	}

}

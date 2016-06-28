/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bordertech.wcomponents.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for HtmlClassUtil.
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HtmlClassUtilTest {


	@Test
	public void testToString() {
		for (HtmlClassUtil.HtmlClassName className : HtmlClassUtil.HtmlClassName.values()) {
			Assert.assertTrue(className.toString().indexOf("wc-") == 0);
		}
	}

}

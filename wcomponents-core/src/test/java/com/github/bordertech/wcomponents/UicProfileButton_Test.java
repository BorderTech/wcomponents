package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.Test;

/**
 * UicProfileButton_Test - unit tests for UicProfileButton.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class UicProfileButton_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		UicProfileButton button = new UicProfileButton();
		Assert.assertNull("caption should be null", button.getText());
	}

	@Test
	public void testConstructorCaption() {
		final String caption = "UicProfile";
		UicProfileButton button = new UicProfileButton(caption);
		Assert.assertEquals("caption should caption set", button.getText(), caption);
	}

	@Test
	public void testAfterPaintButtonNotPressed() {
		UicProfileButton button = new UicProfileButton();
		button.setLocked(true);
		setActiveContext(createUIContext());
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		MockRequest request = new MockRequest();
		button.setPressed(false, request);

		button.afterPaint(new WebXmlRenderContext(writer));

		Assert.assertEquals("for button not pressed afterPaint writer output should be empty",
				strWriter.toString(), "");
	}

	@Test
	public void testAfterPaintButtonPressed() {
		UicProfileButton button = new UicProfileButton();
		button.setLocked(true);
		setActiveContext(createUIContext());
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		MockRequest request = new MockRequest();
		button.setPressed(true, request);

		button.afterPaint(new WebXmlRenderContext(writer));

		Assert.
				assertTrue(
						"for button pressed afterpaint writer output should start with <br/><br/>",
						strWriter.toString()
						.startsWith("<br/><br/>"));
	}

	@Test
	public void testDumpAll() {
		UicProfileButton button = new UicProfileButton();
		button.setLocked(true);
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		button.setPressed(true, request);

		String result = button.dumpAll().toString();

		// result table contains class name
		Assert.assertTrue("dump table should contain component class name", result.indexOf(
				"<td>" + button.getClass().getName() + "</td>") != -1);

		// result table contains component id
		Assert.assertTrue("dump table should contain component id", result.indexOf("<td>" + button.
				getId() + "</td>") != -1);

		// result table contains isSerialisable
		Assert.assertTrue("dump table should contain serialisability", result.indexOf(
				"<td>true</td>") != -1);

		// result table contains size
		Assert.assertTrue("dump table should contain size", result.indexOf("<td>0</td></tr>") != -1);
	}
}

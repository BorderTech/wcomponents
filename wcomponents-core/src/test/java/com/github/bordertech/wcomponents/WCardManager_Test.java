package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WCardManager}.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WCardManager_Test extends AbstractWComponentTestCase {

	@Test
	public void testRequestHandling() {
		WCardManager manager = new WCardManager();
		MockContainer cardOne = new MockContainer();
		MockContainer cardTwo = new MockContainer();
		manager.add(cardOne);
		manager.add(cardTwo);
		manager.setLocked(true);

		Assert.assertEquals("Detector initialisation incorrect", 0, cardOne.getHandleRequestCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardOne.getValidateCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardOne.getPreparePaintCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardOne.getPaintCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardTwo.getHandleRequestCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardTwo.getValidateCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardTwo.getPreparePaintCount());
		Assert.assertEquals("Detector initialisation incorrect", 0, cardTwo.getPaintCount());

		MockRequest request = new MockRequest();
		setActiveContext(createUIContext());
		PrintWriter writer = new XmlStringBuilder(new StringWriter());
		List<Diagnostic> diags = new ArrayList<>();

		manager.serviceRequest(request);
		Assert.assertEquals("Card One should be visible and therefore called", 1, cardOne.
				getHandleRequestCount());
		Assert.assertEquals("Card Two should be invisible and therefore not called", 0, cardTwo.
				getHandleRequestCount());
		cardOne.reset();

		manager.makeVisible(cardTwo);
		manager.serviceRequest(request);
		Assert.assertEquals("Card One should be invisible and therefore not called", 0, cardOne.
				getHandleRequestCount());
		Assert.assertEquals("Card Two should be visible and therefore called", 1, cardTwo.
				getHandleRequestCount());
		cardTwo.reset();

		manager.validateComponent(diags);
		manager.preparePaint(request);
		manager.paint(new WebXmlRenderContext(writer));
		Assert.assertEquals("Card Two should be visible and therefore called", 1, cardTwo.
				getValidateCount());
		Assert.assertEquals("Card Two should be visible and therefore called", 1, cardTwo.
				getPreparePaintCount());
		Assert.assertEquals("Card Two should be visible and therefore called", 1, cardTwo.
				getPaintCount());

		Assert.assertEquals("Card One should be invisible and therefore not called", 0, cardOne.
				getValidateCount());
		Assert.assertEquals("Card One should be invisible and therefore not called", 0, cardOne.
				getPreparePaintCount());
		Assert.assertEquals("Card One should be invisible and therefore not called", 0, cardOne.
				getPaintCount());
	}

	@Test
	public void testMakeVisibleOnNonChild() {
		WCardManager manager = new WCardManager();
		WText cardOne = new WText();
		WText cardTwo = new WText();
		WText cardThree = new WText();

		manager.add(cardOne);

		setActiveContext(createUIContext());
		manager.add(cardTwo);
		manager.remove(cardThree);

		// Ok, has been added in default model
		manager.makeVisible(cardOne);

		// Ok, has been added in UIContext
		manager.makeVisible(cardTwo);

		try {
			// This card has been removed in the UIContext, should throw an exception
			manager.makeVisible(cardThree);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should contain a message", expected.getMessage());
		}
	}
}

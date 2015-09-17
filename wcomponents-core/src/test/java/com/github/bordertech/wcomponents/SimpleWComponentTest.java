package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * A simple example of how you can apply unit tests to a WComponent.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SimpleWComponentTest extends AbstractWComponentTestCase {

	/**
	 * The Logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SimpleWComponentTest.class);

	@Test
	public void testRequest() {
		// Create a test wcomponent with a label and an entry field.
		WTextField name = new WTextField();
		WLabel label = new WLabel("Hero", name);

		WPanel panel = new WPanel();
		panel.add(label);
		panel.add(name);

		// Create a mock context and request.
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Check that the text field successfully stores text.
		name.setText("Batman");
		Assert.assertEquals("text accessors incorrect", "Batman", name.getText());

		// Service a request that simulates a user entering a value
		// into the entry field.
		request.setParameter(name.getId(), "Superman");
		panel.serviceRequest(request);
		Assert.assertEquals("text incorrect after request", "Superman", name.getText());

		// Render the output and log it.
		String output = WebUtilities.render(request, panel);
		LOG.debug(output);
	}
}

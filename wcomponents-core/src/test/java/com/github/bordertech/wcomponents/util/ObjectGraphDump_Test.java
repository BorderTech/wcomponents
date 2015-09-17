package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ObjectGraphDump_Test - unit tests for {@link ObjectGraphDump}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ObjectGraphDump_Test extends AbstractWComponentTestCase {

	/**
	 * label for testing.
	 */
	private static final String TEST_LABEL = "TEST_LABEL";

	@Test
	public void testDump() {
		WPanel component = new WPanel();
		component.setLayout(new BorderLayout());
		component.add(new WLabel(TEST_LABEL), BorderLayout.NORTH);

		ObjectGraphNode graphNode = ObjectGraphDump.dump(component);
		String result = graphNode.toXml();

		// ObjectGraphNode tested independently
		// for the input 'component' above - the dump result must at least contain the following
		// and have run without exceptions
		Assert.assertTrue("",
				result.indexOf("type=\"com.github.bordertech.wcomponents.WPanel\"") != -1);
		Assert.assertTrue("", result.indexOf(
				"field=\"label\" type=\"com.github.bordertech.wcomponents.WLabel\"") != -1);
		Assert.assertTrue("", result.indexOf("field=\"text\" value=\"&quot;" + TEST_LABEL
				+ "&quot;\" type=\"java.io.Serializable\"") != -1);
		Assert.assertTrue("", result.indexOf(
				"field=\"value\" type=\"com.github.bordertech.wcomponents.layout.BorderLayout$BorderLayoutConstraint\"") != -1);
	}
}

package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.MockTreeItemData;
import com.github.bordertech.wcomponents.WTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTreeRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.2.5
 */
public class WTreeRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTree tree = new WTree();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(tree) instanceof WTreeRenderer);
	}

	@Test
	public void testDoPaintWhenEmpty() throws IOException, SAXException, XpathException {
		WTree tree = new WTree();
		setActiveContext(createUIContext());
		assertSchemaMatch(tree);
	}

	@Test
	public void testDoPaintWithData() throws IOException, SAXException, XpathException {
		WTree tree = MockTreeItemData.setupWTree();
		setActiveContext(createUIContext());
		assertSchemaMatch(tree);
	}

	@Test
	public void testDoPaintWithCustomTree() throws IOException, SAXException, XpathException {
		WTree tree = MockTreeItemData.setupWTreeWithCustom();
		setActiveContext(createUIContext());
		assertSchemaMatch(tree);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		List<MockTreeItemData.MyBean> data = new ArrayList<>();
		// Invalid chars
		data.add(new MockTreeItemData.MyBean(getInvalidCharSequence(), "X"));
		// Malicious
		data.add(new MockTreeItemData.MyBean(getMaliciousContent(), "Y"));
		MockTreeItemData.MyTestModel model = new MockTreeItemData.MyTestModel(data);

		WTree tree = new WTree();
		tree.setTreeModel(model);
		assertSafeContent(tree);
	}

}

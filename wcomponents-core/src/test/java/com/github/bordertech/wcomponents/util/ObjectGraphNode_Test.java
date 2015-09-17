package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLabel;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ObjectGraphNode_Test - unit tests for ObjectGraphNode.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ObjectGraphNode_Test extends AbstractWComponentTestCase {

	/**
	 * label for testing.
	 */
	private static final String TEST_LABEL = "TEST_LABEL";

	@Test
	public void testIsPrimitive() {
		ObjectGraphNode node = new ObjectGraphNode(1, "test", String.class.getName(), "test");
		Assert.assertFalse("Node should not be primitive", node.isPrimitive());

		node = new ObjectGraphNode(1, "test", int.class.getName(), 123);
		Assert.assertTrue("Node should be is primitive", node.isPrimitive());
	}

	@Test
	public void testIsSimpleType() {
		ObjectGraphNode node = new ObjectGraphNode(1, "test", String.class.getName(), "test");
		Assert.assertTrue("Node should be a simple type", node.isSimpleType());

		node = new ObjectGraphNode(1, "test", Duplet.class.getName(), new Duplet<String, String>());
		Assert.assertFalse("Node should not be a simple type", node.isSimpleType());
	}

	@Test
	public void testGetSize() {
		ObjectGraphNode node = new ObjectGraphNode(1, "test", int.class.getName(), 1);
		Assert.assertEquals("Incorrect size", 4, node.getSize());

		node = new ObjectGraphNode(1, "test", long.class.getName(), 1);
		Assert.assertEquals("Incorrect size", 8, node.getSize());

		node = new ObjectGraphNode(1, "test", Object.class.getName(), new Object());
		int expectedSize = ObjectGraphNode.OBJECT_SHELL_SIZE + ObjectGraphNode.OBJREF_SIZE;
		Assert.assertEquals("Incorrect size", expectedSize, node.getSize());
	}

	@Test
	public void testGetFieldName() {
		String fieldName = "ObjectGraphNode_Test.testGetFieldName.fieldName";
		ObjectGraphNode node = new ObjectGraphNode(1, fieldName, String.class.getName(), "test");
		Assert.assertEquals("should retrieve correct field name", fieldName, node.getFieldName());
	}

	@Test
	public void testGetId() {
		int nodeId = 12345;
		ObjectGraphNode node = new ObjectGraphNode(nodeId, "test", String.class.getName(), "test");
		Assert.assertEquals("should retrieve correct ID", nodeId, node.getId());
	}

	@Test
	public void testSetRefNode() {
		ObjectGraphNode node = new ObjectGraphNode(1, "test", String.class.getName(), "test");
		ObjectGraphNode refNode = new ObjectGraphNode(2, "test", String.class.getName(), "test");
		node.setRefNode(refNode);

		Assert.assertSame("Should retrieve refNod set", refNode, node.getRefNode());
	}

	@Test
	public void testGetType() {
		ObjectGraphNode node = new ObjectGraphNode(1, "test", int.class.getName(), 1);
		Assert.assertEquals("Should retrieve type set", int.class.getName(), node.getType());
	}

	@Test
	public void testGetValue() {
		String fieldValue = "ObjectGraphNode_Test.testGetValue.fieldValue";
		ObjectGraphNode node = new ObjectGraphNode(1, "test", String.class.getName(), fieldValue);
		Assert.assertSame("Should retrieve value set", fieldValue, node.getValue());
	}

	@Test
	public void testToFlatSummary() {
		WContainer component = new WContainer();
		setActiveContext(createUIContext());
		WLabel label = new WLabel(TEST_LABEL);
		component.add(label);

		final int nodeId = component.getIndexOfChild(label);
		final String fieldName = label.getId();
		ObjectGraphNode node = new ObjectGraphNode(nodeId, fieldName, label.getClass().getName(),
				label);

		String flatSummary = node.toFlatSummary();
		flatSummary = flatSummary.replaceAll("\n", "");

		Assert.assertTrue("summary should start with size", flatSummary.startsWith(
				node.getSize() + " "));
		Assert.assertTrue("summary should end with class name", flatSummary.endsWith(label.
				getClass().getName()));
	}

	@Test
	public void testToXml() {
		WContainer component = new WContainer();
		setActiveContext(createUIContext());
		WLabel label = new WLabel(TEST_LABEL);
		component.add(label);

		final int nodeId = component.getIndexOfChild(label);
		final String fieldName = label.getId();
		ObjectGraphNode node = new ObjectGraphNode(nodeId, fieldName, label.getClass().getName(),
				label);
		String xmlSummary = node.toXml();

		Assert.assertTrue("should report correct ID", xmlSummary.indexOf("object id=\"0\"") != -1);
		Assert.assertTrue("should report correct field name", xmlSummary.indexOf(
				"field=\"" + fieldName + "\"") != -1);
		Assert.assertTrue("should report correct class name",
				xmlSummary.indexOf("type=\"com.github.bordertech.wcomponents.WLabel\"") != -1);
		Assert.assertTrue("should report correct size", xmlSummary.indexOf("size=\"12\"") != -1);
	}
}

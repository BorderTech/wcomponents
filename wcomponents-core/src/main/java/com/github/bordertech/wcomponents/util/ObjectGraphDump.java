package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.ComponentModel;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dumps an object graph, optionally excluding fields that are static or transient.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class ObjectGraphDump {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ObjectGraphDump.class);

	/**
	 * Cached instance fields by class.
	 */
	private final Map<Class<?>, Field[]> instanceFieldsByClass = new HashMap<>();
	/**
	 * Objects that have been visited so far, to stop infinite recursion.
	 */
	private final Map<Object, ObjectGraphNode> visitedNodes = new IdentityHashMap<>();
	/**
	 * The number of nodes generated so far, used to allocated node ids.
	 */
	private int nodeCount = 0;
	/**
	 * Whether transient fields should be excluded from the dump.
	 */
	private final boolean excludeTransient;
	/**
	 * Whether static fields should be excluded from the dump.
	 */
	private final boolean excludeStatic;

	/**
	 * Creates an ObjectGraphDump.
	 *
	 * @param excludeTransient if true, transient fields will be omitted from the dump.
	 * @param excludeStatic if true, static fields will be omitted from the dump.
	 */
	private ObjectGraphDump(final boolean excludeTransient, final boolean excludeStatic) {
		this.excludeTransient = excludeTransient;
		this.excludeStatic = excludeStatic;
	}

	/**
	 * Dumps the contents of the session attributes.
	 *
	 * @param obj the object to dump.
	 * @return the dump of the given object.
	 */
	public static ObjectGraphNode dump(final Object obj) {
		ObjectGraphDump dump = new ObjectGraphDump(false, true);
		ObjectGraphNode root = new ObjectGraphNode(++dump.nodeCount, null, obj.getClass().getName(),
				obj);
		dump.visit(root);

		return root;
	}

	/**
	 * Implementation of the tree walk.
	 *
	 * @param currentNode the node being visited.
	 */
	private void visit(final ObjectGraphNode currentNode) {
		Object currentValue = currentNode.getValue();

		if (currentValue == null
				|| (currentValue instanceof java.lang.ref.SoftReference)
				|| currentNode.isPrimitive() || currentNode.isSimpleType()) {
			return;
		}

		if (isObjectVisited(currentNode)) {
			ObjectGraphNode ref = visitedNodes.get(currentValue);
			currentNode.setRefNode(ref);
			return;
		}

		markObjectVisited(currentNode);

		if (currentValue instanceof List) {
			// ArrayList's elementData is marked transient, and others may be as well, so we have to do this ourselves.
			visitList(currentNode);
		} else if (currentValue instanceof Map) {
			// HashMap's table is marked transient, and others may be as well, so we have to do this ourselves.
			visitMap(currentNode);
		} else if (currentValue instanceof ComponentModel) {
			// Special case for ComponentModel, so we can figure out if any fields are overridden
			visitComponentModel(currentNode);
		} else if (currentValue instanceof Field) {
			visitComplexType(currentNode);
			summariseNode(currentNode);
		} else if (currentValue.getClass().isArray()) {
			visitArray(currentNode);
		} else {
			visitComplexType(currentNode);
		}
	}

	/**
	 * Visits all the fields in the given complex object.
	 *
	 * @param node the ObjectGraphNode containing the object.
	 */
	private void visitComplexType(final ObjectGraphNode node) {
		Field[] fields = getAllInstanceFields(node.getValue());

		for (int i = 0; i < fields.length; i++) {
			Object fieldValue = readField(fields[i], node.getValue());
			String fieldType = fields[i].getType().getName();

			ObjectGraphNode childNode = new ObjectGraphNode(++nodeCount, fields[i].getName(),
					fieldType, fieldValue);
			node.add(childNode);
			visit(childNode);
		}
	}

	/**
	 * Visits all the fields in the given complex object, noting differences.
	 *
	 * @param node the ObjectGraphNode containing the object.
	 * @param otherInstance the other instance to compare to
	 */
	private void visitComplexTypeWithDiff(final ObjectGraphNode node, final Object otherInstance) {
		if (otherInstance == null) {
			// Nothing to compare against, just use the default visit
			visitComplexType(node);
		} else {
			Field[] fields = getAllInstanceFields(node.getValue());

			for (int i = 0; i < fields.length; i++) {
				Object fieldValue = readField(fields[i], node.getValue());
				Object otherValue = readField(fields[i], otherInstance);
				String fieldType = fields[i].getType().getName();
				String nodeFieldName = fields[i].getName() + (Util.equals(fieldValue, otherValue) ? "" : "*");

				ObjectGraphNode childNode = new ObjectGraphNode(++nodeCount, nodeFieldName,
						fieldType, fieldValue);
				node.add(childNode);
				visit(childNode);
			}
		}
	}

	/**
	 * Visits all the fields in the given ComponentModel.
	 *
	 * @param node the ObjectGraphNode containing the ComponentModel.
	 */
	private void visitComponentModel(final ObjectGraphNode node) {
		ComponentModel model = (ComponentModel) node.getValue();
		ComponentModel sharedModel = null;

		List<Field> fieldList = ReflectionUtil.getAllFields(node.getValue(), true, false);
		Field[] fields = fieldList.toArray(new Field[fieldList.size()]);

		for (int i = 0; i < fields.length; i++) {
			if (ComponentModel.class.equals(fields[i].getDeclaringClass())
					&& "sharedModel".equals(fields[i].getName())) {
				sharedModel = (ComponentModel) readField(fields[i], model);
			}
		}

		visitComplexTypeWithDiff(node, sharedModel);
	}

	/**
	 * Reads the contents of a field.
	 *
	 * @param field the field definition.
	 * @param obj the object to read the value from.
	 * @return the value of the field in the given object.
	 */
	private Object readField(final Field field, final Object obj) {
		try {
			return field.get(obj);
		} catch (IllegalAccessException e) {
			// Should not happen as we've called Field.setAccessible(true).
			LOG.error("Failed to read " + field.getName() + " of " + obj.getClass().getName(), e);
		}

		return null;
	}

	/**
	 * Visits all the elements of the given array.
	 *
	 * @param node the ObjectGraphNode containing the array.
	 */
	private void visitArray(final ObjectGraphNode node) {
		if (node.getValue() instanceof Object[]) {
			Object[] array = (Object[]) node.getValue();

			for (int i = 0; i < array.length; i++) {
				String entryType = array[i] == null ? Object.class.getName() : array[i].getClass().
						getName();

				ObjectGraphNode childNode = new ObjectGraphNode(++nodeCount, "[" + i + "]",
						entryType, array[i]);
				node.add(childNode);
				visit(childNode);
			}
		} else {
			ObjectGraphNode childNode = new ObjectGraphNode(++nodeCount, "[primitive array]", node.
					getValue().getClass().getName(), node.getValue());
			node.add(childNode);
		}
	}

	/**
	 * Visits all the elements of the given list.
	 *
	 * @param node the ObjectGraphNode containing the list.
	 */
	private void visitList(final ObjectGraphNode node) {
		int index = 0;

		for (Iterator i = ((List) node.getValue()).iterator(); i.hasNext();) {
			Object entry = i.next();
			String entryType = entry == null ? Object.class.getName() : entry.getClass().getName();

			ObjectGraphNode childNode = new ObjectGraphNode(++nodeCount, "[" + index++ + "]",
					entryType, entry);
			node.add(childNode);
			visit(childNode);
		}

		adjustOverhead(node);
	}

	/**
	 * For some types, we don't care about their internals, so just summarise the size.
	 *
	 * @param node the node to summarise.
	 */
	private void summariseNode(final ObjectGraphNode node) {
		int size = node.getSize();
		node.removeAll();
		node.setSize(size);
	}

	/**
	 * Adjusts the overhead of the given node, for types where not all data is output (e.g. Lists and Maps).
	 *
	 * @param node the node to adjust.
	 */
	private void adjustOverhead(final ObjectGraphNode node) {
		ObjectGraphNode[] originalChildren = new ObjectGraphNode[node.getChildCount()];
		int dataSize = 0;

		for (int i = 0; i < node.getChildCount(); i++) {
			ObjectGraphNode childNode = (ObjectGraphNode) node.getChildAt(i);
			dataSize += childNode.getSize();
			originalChildren[i] = childNode;
		}

		node.removeAll();

		// Figure out the list overhead.
		// All the original children will be included refs, as they have already been visited.
		visitComplexType(node);
		node.setSize(node.getSize() - originalChildren.length * ObjectGraphNode.OBJREF_SIZE);

		// re-attach the old children
		node.removeAll();

		for (int i = 0; i < originalChildren.length; i++) {
			node.add(originalChildren[i]);
		}
	}

	/**
	 * Visits all the keys and entries of the given map.
	 *
	 * @param node the ObjectGraphNode containing the map.
	 */
	private void visitMap(final ObjectGraphNode node) {
		Map map = (Map) node.getValue();

		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			Object key = entry.getKey();

			if (key != null) {
				ObjectGraphNode keyNode = new ObjectGraphNode(++nodeCount, "key", key.getClass().
						getName(), key);
				node.add(keyNode);
				visit(keyNode);
			} else {
				ObjectGraphNode keyNode = new ObjectGraphNode(++nodeCount, "key", Object.class.
						getName(), null);
				node.add(keyNode);
			}

			Object value = entry.getValue();

			if (value != null) {
				ObjectGraphNode valueNode = new ObjectGraphNode(++nodeCount, "value", value.
						getClass().getName(), value);
				node.add(valueNode);
				visit(valueNode);
			} else {
				ObjectGraphNode valueNode = new ObjectGraphNode(++nodeCount, "value", Object.class.
						getName(), null);
				node.add(valueNode);
			}
		}

		adjustOverhead(node);
	}

	/**
	 * Marks the given object as having been visited.
	 *
	 * @param node the Node containing the object to mark as visited.
	 */
	private void markObjectVisited(final ObjectGraphNode node) {
		visitedNodes.put(node.getValue(), node);
	}

	/**
	 * Indicates whether the given object has been visited.
	 *
	 * @param node the Node containing the object to check.
	 * @return true if the object has been visited, false if not.
	 */
	private boolean isObjectVisited(final ObjectGraphNode node) {
		return visitedNodes.containsKey(node.getValue());
	}

	/**
	 * Retrieves all the instance fields for the given object.
	 *
	 * @param obj the object to examine
	 * @return an array of instance fields for the given object
	 */
	private Field[] getAllInstanceFields(final Object obj) {
		Field[] fields = instanceFieldsByClass.get(obj.getClass());

		if (fields == null) {
			List<Field> fieldList = ReflectionUtil.
					getAllFields(obj, excludeStatic, excludeTransient);
			fields = fieldList.toArray(new Field[fieldList.size()]);

			instanceFieldsByClass.put(obj.getClass(), fields);
		}

		return fields;
	}
}

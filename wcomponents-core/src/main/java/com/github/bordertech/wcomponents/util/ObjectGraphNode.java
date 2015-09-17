package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WebUtilities;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores information about a node in a java object graph.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class ObjectGraphNode extends AbstractTreeNode {
	// the following constants are physical sizes (in bytes) and are JVM-dependent:
	// [the current values are Ok for most 32-bit JVMs]
	// Shamelessly copied from com.vladium.utils.ObjectProfiler

	/**
	 * java.lang.Object shell size in bytes.
	 */
	public static final int OBJECT_SHELL_SIZE = 8;
	/**
	 * The size of an object reference (pointer) in bytes.
	 */
	public static final int OBJREF_SIZE = 4;

	/**
	 * The size of the 'long' primitive type in bytes.
	 */
	public static final int LONG_FIELD_SIZE = 8;
	/**
	 * The size of the 'int' primitive type in bytes.
	 */
	public static final int INT_FIELD_SIZE = 4;
	/**
	 * The size of the 'short' primitive type in bytes.
	 */
	public static final int SHORT_FIELD_SIZE = 2;
	/**
	 * The size of the 'char' primitive type in bytes.
	 */
	public static final int CHAR_FIELD_SIZE = 2;
	/**
	 * The size of the 'byte' primitive type in bytes.
	 */
	public static final int BYTE_FIELD_SIZE = 1;
	/**
	 * The size of the 'boolean' primitive type in bytes.
	 */
	public static final int BOOLEAN_FIELD_SIZE = 1;
	/**
	 * The size of the 'double' primitive type in bytes.
	 */
	public static final int DOUBLE_FIELD_SIZE = 8;
	/**
	 * The size of the 'float' primitive type in bytes.
	 */
	public static final int FLOAT_FIELD_SIZE = 4;

	/**
	 * A map of sizes of common primitives and wrappers.
	 */
	private static final Map<String, Integer> SIMPLE_SIZES = new HashMap<>();

	static {
		SIMPLE_SIZES.put(long.class.getName(), LONG_FIELD_SIZE);
		SIMPLE_SIZES.put(int.class.getName(), INT_FIELD_SIZE);
		SIMPLE_SIZES.put(short.class.getName(), SHORT_FIELD_SIZE);
		SIMPLE_SIZES.put(char.class.getName(), CHAR_FIELD_SIZE);
		SIMPLE_SIZES.put(byte.class.getName(), BYTE_FIELD_SIZE);
		SIMPLE_SIZES.put(boolean.class.getName(), BOOLEAN_FIELD_SIZE);
		SIMPLE_SIZES.put(double.class.getName(), DOUBLE_FIELD_SIZE);
		SIMPLE_SIZES.put(float.class.getName(), FLOAT_FIELD_SIZE);

		SIMPLE_SIZES.put(Long.class.getName(), OBJECT_SHELL_SIZE + LONG_FIELD_SIZE);
		SIMPLE_SIZES.put(Integer.class.getName(), OBJECT_SHELL_SIZE + INT_FIELD_SIZE);
		SIMPLE_SIZES.put(Short.class.getName(), OBJECT_SHELL_SIZE + SHORT_FIELD_SIZE);
		SIMPLE_SIZES.put(Character.class.getName(), OBJECT_SHELL_SIZE + CHAR_FIELD_SIZE);
		SIMPLE_SIZES.put(Byte.class.getName(), OBJECT_SHELL_SIZE + BYTE_FIELD_SIZE);
		SIMPLE_SIZES.put(Boolean.class.getName(), OBJECT_SHELL_SIZE + BOOLEAN_FIELD_SIZE);
		SIMPLE_SIZES.put(Double.class.getName(), OBJECT_SHELL_SIZE + DOUBLE_FIELD_SIZE);
		SIMPLE_SIZES.put(Float.class.getName(), OBJECT_SHELL_SIZE + FLOAT_FIELD_SIZE);
	}

	/**
	 * The set of Java primitive types.
	 */
	private static final Set<String> PRIMITIVE_TYPES
			= new HashSet<>(Arrays.asList(new String[]{
		byte.class.getName(),
		short.class.getName(),
		char.class.getName(),
		int.class.getName(),
		long.class.getName(),
		float.class.getName(),
		double.class.getName(),
		boolean.class.getName()
	}));

	/**
	 * The set of Java "simple" types. A type is considered simple if it is a leaf node (ie. contains no further
	 * fields).
	 */
	private static final Set<String> SIMPLE_TYPES
			= new HashSet<>(Arrays.asList(new String[]{
		Byte.class.getName(),
		Short.class.getName(),
		Character.class.getName(),
		Integer.class.getName(),
		Long.class.getName(),
		Float.class.getName(),
		Double.class.getName(),
		Boolean.class.getName(),
		String.class.getName(),
		Class.class.getName()
	}));

	/**
	 * The name of the field that this object is stored under in its parent object.
	 */
	private final String fieldName;

	/**
	 * The fully qualified java type of the field. Not necessarily the same as the {@link #value}'s class.
	 */
	private final String type;

	/**
	 * The value of this node.
	 */
	private final Object value;

	/**
	 * The size of this node, in bytes.
	 */
	private int size;

	/**
	 * This node's id.
	 */
	private final int id;

	/**
	 * If non-zero, this node is a reference to another node, using the other node's id.
	 */
	private ObjectGraphNode refNode;

	/**
	 * Creates an ObjectGraphNode.
	 *
	 * @param id the node id.
	 * @param fieldName the field name that the parent node refers to this node by.
	 * @param type the fully qualified java type name.
	 * @param value the node value.
	 */
	public ObjectGraphNode(final int id, final String fieldName, final String type,
			final Object value) {
		this.id = id;
		this.fieldName = fieldName;
		this.type = type;
		this.value = value;
		this.size = getSize(type, value);
	}

	/**
	 * Calculates the size of a field value obtained using the reflection API.
	 *
	 * @param fieldType the Field's type (class), needed to return the correct values for primitives.
	 * @param fieldValue the field's value (primitives are boxed).
	 *
	 * @return an approximation of amount of memory the field occupies, in bytes.
	 */
	private int getSize(final String fieldType, final Object fieldValue) {
		Integer fieldSize = SIMPLE_SIZES.get(fieldType);

		if (fieldSize != null) {
			if (PRIMITIVE_TYPES.contains(fieldType)) {
				return fieldSize;
			}

			return OBJREF_SIZE + fieldSize;
		} else if (fieldValue instanceof String) {
			return (OBJREF_SIZE + OBJECT_SHELL_SIZE) * 2 // One for the String Object itself, and one for the char[] value
					+ INT_FIELD_SIZE * 3 // offset, count, hash
					+ ((String) fieldValue).length() * CHAR_FIELD_SIZE;
		} else if (fieldValue != null) {
			return OBJECT_SHELL_SIZE + OBJREF_SIZE; // plus the size of any nested nodes.
		} else { // Null
			return OBJREF_SIZE;
		}
	}

	/**
	 * @return true if those node represents a primitive type.
	 */
	public boolean isPrimitive() {
		return PRIMITIVE_TYPES.contains(type);
	}

	/**
	 * @return true if those node represents a "simple" type.
	 */
	public boolean isSimpleType() {
		// Simple types may be stored in java.lang.Object fields, so we have to check both
		return SIMPLE_TYPES.contains(type)
				|| (value != null && Object.class.getName().equals(type) && SIMPLE_TYPES.contains(
				value.getClass().getName()));
	}

	/**
	 * @return a String representation of this node's value if it is simple, otherwise null.
	 */
	private String formatSimpleValue() {
		if (value instanceof Character) {
			char c = ((Character) value);

			if (c < 32 || c > 127) {
				return "(char) " + ((int) c);
			}

			return '\'' + ((Character) value).toString() + '\'';
		} else if (value instanceof String) {
			String stringVal = ((String) value).replace('\n', ' ').replace('\r', ' ');

			if (stringVal.length() > 30) {
				return '"' + stringVal.substring(0, 27) + "...\"";
			} else {
				return '"' + stringVal + '"';
			}
		} else if (isPrimitive() || isSimpleType()) {
			return value == null ? null : value.toString();
		}

		return null;
	}

	/**
	 * Returns the sum of the size of primitive/simple value data contained within this node. This will differ from the
	 * serialized size, as references to shared objects aren't followed/counted.
	 *
	 * @return the actual data size, in bytes.
	 */
	public int getSize() {
		int dataSize = size;

		for (int i = 0; i < getChildCount(); i++) {
			dataSize += ((ObjectGraphNode) getChildAt(i)).getSize();
		}

		return dataSize;
	}

	/**
	 * @return a flat format summary representation of the object tree, starting at this node.
	 */
	public String toFlatSummary() {
		StringBuffer buffer = new StringBuffer();
		toFlatSummary("", buffer);

		return buffer.toString();
	}

	/**
	 * Generates a flat format summary XML representation of this ObjectGraphNode.
	 *
	 * @param indent the indent, for formatting.
	 * @param buffer the StringBuffer to append the summary to.
	 */
	private void toFlatSummary(final String indent, final StringBuffer buffer) {
		buffer.append(indent);

		ObjectGraphNode root = (ObjectGraphNode) getRoot();
		double pct = 100.0 * getSize() / root.getSize();

		buffer.append(getSize()).append(" (");
		buffer.append(new DecimalFormat("0.0").format(pct));
		buffer.append("%) - ").append(getFieldName()).append(" - ");

		if (getRefNode() != null) {
			buffer.append("ref (").append(getRefNode().getValue().getClass().getName()).append(')');
		} else if (getValue() == null) {
			buffer.append("null (").append(getType()).append(')');
		} else {
			buffer.append(getType());
		}

		buffer.append('\n');

		String newIndent = indent + "   ";

		for (int i = 0; i < getChildCount(); i++) {
			((ObjectGraphNode) getChildAt(i)).toFlatSummary(newIndent, buffer);
		}
	}

	/**
	 * @return an XML representation of the object tree, starting at this node.
	 */
	public String toXml() {
		StringBuffer xml = new StringBuffer();
		toXml("", xml);

		return xml.toString();
	}

	/**
	 * Emits an XML representation of this ObjectGraphNode.
	 *
	 * @param indent the indent, for formatting.
	 * @param xml the buffer to write XML output to.
	 */
	private void toXml(final String indent, final StringBuffer xml) {
		String primitiveString = formatSimpleValue();

		xml.append(indent);
		xml.append(isPrimitive() ? "<primitive" : "<object");

		if (refNode == null) {
			xml.append(" id=\"").append(id).append('"');

			if (fieldName != null) {
				xml.append(" field=\"").append(fieldName).append('"');
			}

			if (primitiveString != null) {
				primitiveString = WebUtilities.encode(primitiveString);
				xml.append(" value=\"").append(primitiveString).append('"');
			}

			if (type != null) {
				xml.append(" type=\"").append(type).append('"');
			}

			xml.append(" size=\"").append(getSize()).append('"');

			if (getChildCount() == 0) {
				xml.append("/>\n");
			} else {
				xml.append(">\n");

				String newIndent = indent + "  ";

				for (int i = 0; i < getChildCount(); i++) {
					((ObjectGraphNode) getChildAt(i)).toXml(newIndent, xml);
				}

				xml.append(indent);
				xml.append(isPrimitive() ? "</primitive>\n" : "</object>\n");
			}
		} else {
			if (fieldName != null) {
				xml.append(" field=\"").append(fieldName).append('"');
			}

			xml.append(" refId=\"").append(refNode.id).append('"');

			if (refNode.value == null) {
				xml.append(" type=\"").append(refNode.type).append('"');
			} else {
				xml.append(" type=\"").append(refNode.value.getClass().getName()).append('"');
			}

			xml.append(" size=\"").append(OBJREF_SIZE).append('"');

			xml.append("/>\n");
		}
	}

	/**
	 * @return Returns the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the refNode.
	 */
	public ObjectGraphNode getRefNode() {
		return refNode;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param refNode The refNode to set.
	 */
	public void setRefNode(final ObjectGraphNode refNode) {
		this.refNode = refNode;
		setSize(OBJREF_SIZE);
	}

	/**
	 * Sets the node size. Allows ObjectGraphDump to adjust the overhead size of e.g. collections classes.
	 *
	 * @param size the new node size.
	 */
	protected void setSize(final int size) {
		this.size = size;
	}
}

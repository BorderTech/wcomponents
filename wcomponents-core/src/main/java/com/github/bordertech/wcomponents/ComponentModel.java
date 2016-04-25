package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.ReflectionUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds the extrinsic state information of a WComponent. Subclasses can extend this class and add extra model
 * attributes.
 *
 * <p>
 * Subclasses must adhere to the following rules:
 * </p>
 * <ul>
 * <li>They must define a public no-args constructor (requirement of {@link Externalizable}).</li>
 * <li>They must not contain any final fields (needed for initialising from shared model).</li>
 * <li>They must not contain any non-serializable data.</li>
 * </ul>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ComponentModel implements WebModel, Externalizable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ComponentModel.class);

	/**
	 * Cache fields obtained using ReflectionUtils - HashMap&lt;Class, Field[]&gt;.
	 */
	private static final Map<Class<?>, Field[]> FIELDS_BY_CLASS = new HashMap<>();

	/**
	 * The bit-mask for the flag that indicates whether a component is visible.
	 */
	public static final int VISIBLE_FLAG = 1 << 0;

	/**
	 * The bit-mask for the flag that indicates whether a component is initialised.
	 */
	public static final int INITIALISED_FLAG = 1 << 1;

	/**
	 * The bit-mask for the flag that indicates whether a component is disabled.
	 */
	public static final int DISABLED_FLAG = 1 << 2;

	/**
	 * The bit-mask for the flag that indicates whether a component is read only.
	 */
	public static final int READONLY_FLAG = 1 << 3;

	/**
	 * The bit-mask for the flag that indicates whether a component is mandatory.
	 */
	public static final int MANDATORY_FLAG = 1 << 4;

	/**
	 * The bit-mask for the flag that indicates whether a component needs validation.
	 */
	public static final int VALIDATE_FLAG = 1 << 5;

	/**
	 * The bit-mask for the flag that indicates whether a component is submitted on change/click (client side).
	 */
	public static final int SUBMIT_ON_CHANGE_FLAG = 1 << 6;

	/**
	 * The bit-mask for the flag that indicates whether a component is rendered as "hidden" on the client side.
	 */
	public static final int HIDE_FLAG = 1 << 7;

	/**
	 * <p>
	 * The bit-mask for the flag that indicates whether that user data has been set. This is necessary, as some
	 * components do not contain enough values to determine this automatically. For example, WCheckBox only has two
	 * states (on/off), and could already contain two different "default" values from the shared model and a bean.</p>
	 *
	 * <p>
	 * <b>Note:</b> This bit mask must never be set on a component's shared model.</p>
	 */
	public static final int USER_DATA_SET = 1 << 8;

	/**
	 * The bit-mask for the flag that indicates whether a component's text needs to be encoded before output.
	 */
	public static final int ENCODE_TEXT_FLAG = 1 << 9;

	/**
	 * The bit-mask for the flag that indicates whether a component is trackable via analytics.
	 */
	public static final int TRACKABLE_FLAG = 1 << 10;

	/**
	 * The bit-mask for the flag that indicates whether a component is a naming context.
	 */
	public static final int NAMING_CONTEXT_FLAG = 1 << 11;

	/**
	 * The bit-mask for the default set of flags: visible, validation required and text encoded.
	 */
	protected static final int FLAGS_DEFAULT = VISIBLE_FLAG | VALIDATE_FLAG | ENCODE_TEXT_FLAG;

	/**
	 * When initially constructed or deserialized, some of our fields may be defaulted to the shared model, but we don't
	 * yet have a reference to it. When a reference is supplied using the setSharedModel method, this list controls
	 * which fields should have the values set to the shared model's values.
	 */
	private transient List<Field> unsetFields = Arrays.asList(getFields(this));

	/**
	 * A reference to the sharedModel. This is not serialized, and must therefore be supplied after deserialization.
	 */
	private transient ComponentModel sharedModel;

	/**
	 * Are we visible and/or enabled?
	 */
	private int flags = FLAGS_DEFAULT;

	/**
	 * You can override the children on a per session basis. When children is set to null we know there is no override.
	 * An empty array indicates no children.
	 */
	private List<WComponent> children;

	/**
	 * You can override the parent on a per session basis. When PARENT_OVERRIDDEN_FLAG is not set, we know there is no
	 * override. This is important because setting the parent to java null is a valid override.
	 */
	private Container parent;

	/**
	 * Optional tag used to identify the child by the parent. This is particularly good in templating layout managers.
	 */
	private String tag;

	/**
	 * Components can be given an explicit template to render rather than their default output.
	 */
	private String templateUrl;

	/**
	 * Components can be given an explicit template to render rather than their default output.
	 */
	private String templateMarkUp;

	/**
	 * Adds a toolTip to the component. Browsers will display this value when the element is hovered-over or is in
	 * focus.
	 */
	private Serializable toolTip;

	/**
	 * Adds extra textual information to describe a component. This is intended for screen-readers only.
	 *
	 * @deprecated use toolTip
	 */
	private Serializable accessibleText;

	/**
	 * Adds extra value to the HTML class of the output component.
	 */
	private Serializable htmlClass;

	/**
	 * General placeholder for subclasses of WComponent to place model attributes. This is mostly for convenience so
	 * that subclasses do not need to extend the <code>ComponentModel</code> and override the extrinsic state management
	 * (eg, newComponentModel(), etc).
	 */
	private Map<String, Serializable> attributes;

	/**
	 * The label associated with the component.
	 */
	private WLabel label;

	/**
	 * Hold the id name of component.
	 */
	private String idName;

	/**
	 * Hold the id names used in a name context. Used by NamingContext components.
	 */
	private Map<String, WComponent> contextIds;

	/**
	 * Sets an attribute.
	 *
	 * @param key the attribute key
	 * @param value the attribute value
	 */
	public void setAttribute(final String key, final Serializable value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		attributes.put(key, value);
	}

	/**
	 * Retrieves an attribute.
	 *
	 * @param key the attribute key
	 * @return the value of the attribute, or null if the attribute does not exist.
	 */
	public Serializable getAttribute(final String key) {
		if (attributes == null) {
			return null;
		}

		return attributes.get(key);
	}

	/**
	 * Removes an attribute.
	 *
	 * @param key the attribute key
	 * @return the value of the attribute that was removed, or null if the attribute did not exist.
	 */
	public Serializable removeAttribute(final String key) {
		if (attributes == null) {
			return null;
		}

		return attributes.remove(key);
	}

	/**
	 * Indicates whether this ComponentModel is equal to the given object.
	 *
	 * Subclasses should not need to override this method, as reflection is used to read in all fields.
	 *
	 * @param obj the object to check for equality.
	 * @return true if the other object is a ComponentModel and is equal to this model.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			// Trivial case
			return true;
		} else if (!getClass().equals(obj.getClass())) {
			// Can't be equal if we're not the same model class
			return false;
		} else {
			// Check all fields - this saves subclasses having to override
			// equals and remembering to call super.equals.
			final Field[] fields = getFields(this);

			try {
				for (int i = fields.length - 1; i >= 0; i--) {
					Object value = fields[i].get(this);
					Object otherValue = fields[i].get(obj);

					if (!Util.equals(value, otherValue)) {
						return false;
					}
				}
			} catch (IllegalAccessException e) {
				LOG.error("Failed to read field", e);
				return false;
			}

			return true;
		}
	}

	/**
	 * @return this model's hash code.
	 */
	@Override
	public int hashCode() {
		//TODO: Do we need a better implementation of hashCode using all fields from subclasses?

		return getClass().getName().hashCode()
				+ flags
				+ (children == null ? 0 : children.hashCode())
				+ (attributes == null ? 0 : attributes.hashCode())
				+ (parent == null ? 0 : parent.hashCode())
				+ (tag == null ? 0 : tag.hashCode())
				+ (toolTip == null ? 0 : toolTip.hashCode())
				+ (templateUrl == null ? 0 : templateUrl.hashCode());
	}

	/**
	 * Sets the shared component model. If there are any unset fields, they are set from the values in the shared model.
	 *
	 * @param sharedModel the shared ComponentModel
	 */
	protected final void setSharedModel(final ComponentModel sharedModel) {
		this.sharedModel = sharedModel;

		if (unsetFields != null) {
			// Copy the data from the shared model
			for (Field field : unsetFields) {
				try {
					Object sharedValue = field.get(sharedModel);
					Object sessionValue = copyData(sharedValue);
					field.set(this, sessionValue);
				} catch (IllegalAccessException e) {
					LOG.error("Failed to set field " + field.getName() + " on " + getClass().
							getName(), e);
				} catch (IllegalArgumentException e) {
					LOG.error("Failed to set field " + field.getName() + " on " + getClass().
							getName(), e);
				}
			}

			unsetFields = null;
		}
	}

	/**
	 * Creates a copy of mutable data, to ensure updates to one component model do not update the other.
	 *
	 * TODO: We might need to add other collections/arrays later
	 *
	 * @param data the data to copy
	 * @return a copy of the data, if it is mutable, otherwise the original data.
	 */
	protected Object copyData(final Object data) {
		// For mutable objects, we perform a shallow copy
		// so that we don't change the state of the shared value
		if (data instanceof List) {
			return new ArrayList((List) data);
		} else if (data instanceof Hashtable) {
			return new Hashtable((Hashtable) data);
		} else if (data instanceof Map) {
			return new HashMap((Map) data);
		} else if (data instanceof Set) {
			return new HashSet((Set) data);
		} else if (data instanceof String[]) {
			String[] array = (String[]) data;
			String[] copy = new String[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof int[]) {
			int[] array = (int[]) data;
			int[] copy = new int[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof long[]) {
			long[] array = (long[]) data;
			long[] copy = new long[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof float[]) {
			float[] array = (float[]) data;
			float[] copy = new float[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof double[]) {
			double[] array = (double[]) data;
			double[] copy = new double[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof boolean[]) {
			boolean[] array = (boolean[]) data;
			boolean[] copy = new boolean[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof char[]) {
			char[] array = (char[]) data;
			char[] copy = new char[array.length];
			System.arraycopy(array, 0, copy, 0, copy.length);
			return copy;
		} else if (data instanceof Object[]) {
			Class<?> componentClass = data.getClass().getComponentType();
			Object[] copy = (Object[]) Array.newInstance(componentClass, ((Object[]) data).length);
			System.arraycopy(data, 0, copy, 0, copy.length);
			return copy;
		}

		return data;
	}

	/**
	 * @return Returns the attributes.
	 */
	protected Map<String, Serializable> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes The attributes to set.
	 */
	protected void setAttributes(final Map<String, Serializable> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return Returns the children.
	 */
	protected List<WComponent> getChildren() {
		return children;
	}

	/**
	 * @param children The children to set.
	 */
	protected void setChildren(final List<WComponent> children) {
		this.children = children;
	}

	/**
	 * @return Returns the flags.
	 */
	protected int getFlags() {
		return flags;
	}

	/**
	 * @param flags The flags to set.
	 */
	protected void setFlags(final int flags) {
		this.flags = flags;
	}

	/**
	 * @return Returns the parent.
	 */
	protected Container getParent() {
		return parent;
	}

	/**
	 * @param parent The parent to set.
	 */
	protected void setParent(final Container parent) {
		this.parent = parent;
	}

	/**
	 * @return Returns the tag.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	protected String getTag() {
		return tag;
	}

	/**
	 * @param tag The tag to set.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	protected void setTag(final String tag) {
		this.tag = tag;
	}

	/**
	 * @return Returns the templateUrl.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public String getTemplateUrl() {
		return templateUrl;
	}

	/**
	 * @param templateUrl The templateUrl to set.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public void setTemplateUrl(final String templateUrl) {
		this.templateUrl = templateUrl;
	}

	/**
	 * @return Returns the template mark-up.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public String getTemplateMarkUp() {
		return templateMarkUp;
	}

	/**
	 * @param templateMarkUp The template mark-up to set.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	public void setTemplateMarkUp(final String templateMarkUp) {
		this.templateMarkUp = templateMarkUp;
	}

	/**
	 * @return Returns the tool tip text.
	 */
	protected Serializable getToolTip() {
		return toolTip;
	}

	/**
	 * @param text The tool tip to set.
	 * @param args optional message format arguments.
	 */
	protected void setToolTip(final String text, final Serializable... args) {
		this.toolTip = I18nUtilities.asMessage(text, args);
	}

	/**
	 * @return Returns the accessible text.
	 * @deprecated use getToolTip
	 */
	protected Serializable getAccessibleText() {
		return accessibleText;
	}

	/**
	 * @param text The accessible text to set.
	 * @param args optional message format arguments.
	 * @deprecated use setToolTip
	 */
	protected void setAccessibleText(final String text, final Serializable... args) {
		this.accessibleText = I18nUtilities.asMessage(text, args);
	}

	/**
	 * @return Returns the HTML class.
	 */
	protected Serializable getHtmlClass() {
		return htmlClass;
	}

	/**
	 * @param text The HTML class name text to set.
	 * @param args optional message format arguments.
	 */
	protected void setHtmlClass(final String text, final Serializable... args) {
		this.htmlClass = I18nUtilities.asMessage(text, args);
	}

	/**
	 * @return the label associated with this component.
	 */
	public WLabel getLabel() {
		return label;
	}

	/**
	 * Sets the label associated with this component.
	 *
	 * @param label the label to associate with this component.
	 */
	public void setLabel(final WLabel label) {
		this.label = label;
	}

	/**
	 * @return Returns the sharedModel.
	 */
	protected ComponentModel getSharedModel() {
		return sharedModel;
	}

	/**
	 * @return the id name.
	 */
	protected String getIdName() {
		return idName;
	}

	/**
	 * @param idName the id name to set.
	 */
	protected void setIdName(final String idName) {
		this.idName = idName;
	}

	/**
	 * @return the map of registered ids for the naming context
	 */
	protected Map<String, WComponent> getContextIds() {
		return this.contextIds;
	}

	/**
	 * @param contextIds the map holding the registered ids for the naming context
	 */
	protected void setContextIds(final Map<String, WComponent> contextIds) {
		this.contextIds = contextIds;
	}

	/**
	 * <p>
	 * Implementation of the Externalizable interface to ensure that we don't serialize redundant data (anything
	 * contained by the {@link #sharedModel}.</p>
	 *
	 * <p>
	 * Data is obtained using reflection so that subclasses don't need concern themselves with the ComponentModel
	 * serialization mechanism.</p>
	 *
	 * <p>
	 * After the ComponentModel data is read in, some fields may be in a "unset" state. The static model needs to be
	 * supplied using {@link #setSharedModel(ComponentModel)}
	 *
	 * @param in the ObjectInput to read from.
	 *
	 * @throws IOException if there is an error reading from the ObjectInput
	 * @throws ClassNotFoundException If the class of a serialized object cannot be found.
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		Field[] fields = getFields(this);
		unsetFields = new ArrayList<>();

		for (Field field : fields) {
			try {
				Object value = in.readObject();

				if (value instanceof NoOverride) {
					// No override, so remember for later
					unsetFields.add(field);
				} else {
					field.set(this, value);
				}
			} catch (IllegalAccessException e) {
				LOG.error("Failed to read field " + field.getName(), e);
			}
		}
	}

	/**
	 * <p>
	 * Implementation of the Externalizable interface to ensure that we don't serialize redundant data (anything
	 * contained by the {@link #sharedModel}.</p>
	 *
	 * <p>
	 * Data is obtained using reflection so that subclasses don't need concern themselves with the ComponentModel
	 * serialization mechanism.</p>
	 *
	 * @param out the ObjectOutput to write to.
	 *
	 * @throws IOException if there is an error writing to the ObjectOutput
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		Field[] fields = getFields(this);

		for (Field field : fields) {
			try {
				if (sharedModel == null) {
					// Support serialization of the static model,
					// even though this should not occur.
					Object value = field.get(this);
					out.writeObject(value);
				} else if (unsetFields != null && unsetFields.contains(field)) {
					// Support the unlikely case being deserialized/serialized
					// in short succession without the shared model being set.
					out.writeObject(NoOverride.INSTANCE);
				} else {
					Object sharedValue = field.get(sharedModel);
					Object value = field.get(this);

					if (Util.equals(value, sharedValue)) {
						out.writeObject(NoOverride.INSTANCE);
					} else {
						out.writeObject(value);
					}
				}
			} catch (IllegalAccessException e) {
				LOG.error("Failed to write field " + field.getName(), e);
			}
		}
	}

	/**
	 * Returns the list of fields for the given ComponentModel.
	 *
	 * @param model the ComponentModel to read fields for.
	 * @return an array of fields for the given model.
	 */
	private static Field[] getFields(final ComponentModel model) {
		Class modelClass = model.getClass();
		Field[] fields;

		synchronized (FIELDS_BY_CLASS) {
			fields = FIELDS_BY_CLASS.get(modelClass);

			if (fields == null) {
				List fieldList = ReflectionUtil.getAllFields(model, true, true);
				Collections.sort(fieldList, new FieldComparator());

				fields = (Field[]) fieldList.toArray(new Field[fieldList.size()]);
				FIELDS_BY_CLASS.put(modelClass, fields);
			}
		}

		return fields;
	}

	/**
	 * When serializing the model, we need to be distinguish a null override of the static model's value vs a field that
	 * hasn't been written because it hasn't been overridden. This object is used as a marker.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class NoOverride implements Serializable {

		/**
		 * Singleton instance.
		 */
		public static final NoOverride INSTANCE = new NoOverride();

		/**
		 * Don't allow instantiation from outside of this class.
		 */
		private NoOverride() {
		}
	}

	/**
	 * The reflection API doesn't guarantee that fields will be returned in any particular order. To ensure that fields
	 * are deserialized in the same order that they were serialized, we need to sort the field list.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class FieldComparator implements Comparator<Field>, Serializable {

		/**
		 * Compares two objects for order. Returns a negative integer, zero, or a positive integer as the first object
		 * is less than, equal to, or greater than the second.<p>
		 *
		 * @param field1 the first field to compare.
		 * @param field2 the second field to compare.
		 * @return a negative integer, zero, or a positive integer as the first object is less than, equal to, or
		 * greater than the second.
		 */
		@Override
		public int compare(final Field field1, final Field field2) {
			String field1Str = field1.getDeclaringClass().getName() + '.' + field1.getName();
			String field2Str = field2.getDeclaringClass().getName() + '.' + field2.getName();

			return field1Str.compareTo(field2Str);
		}
	}
}

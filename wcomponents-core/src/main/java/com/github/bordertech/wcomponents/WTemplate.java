package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jonathan
 */
public class WTemplate extends WBeanComponent implements Container, NamingContextable {

	/**
	 * @param templateName the template file name
	 */
	public WTemplate(final String templateName) {
		setTemplateName(templateName);
	}

	/**
	 * @param templateName the template file name
	 */
	public void setTemplateName(final String templateName) {
		if (Util.empty(templateName)) {
			throw new IllegalArgumentException("A template file name must be provded.");
		}
		getOrCreateComponentModel().templateName = templateName;
	}

	/**
	 * @return the template file name
	 */
	public String getTemplateName() {
		return getComponentModel().templateName;
	}

	public void addTagged(final WComponent component, final String tag) {
		if (component == null) {
			throw new IllegalArgumentException("A component must be provided");
		}
		if (Util.empty(tag)) {
			throw new IllegalArgumentException("A tag must be provided");
		}

		TemplateModel model = getOrCreateComponentModel();
		if (model.componentTags == null) {
			model.componentTags = new HashMap<>();
		}
		model.componentTags.put(component, tag);
		add(component);
	}

	public void removeTagged(final WComponent component) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.componentTags != null) {
			model.componentTags.remove(component);
			if (model.componentTags.isEmpty()) {
				model.componentTags = null;
			}
		}
		remove(component);
	}

	public void removeAllTagged() {
		TemplateModel model = getOrCreateComponentModel();
		model.componentTags = null;
		removeAll();
	}

	public Map<WComponent, String> getTaggedComponents() {
		Map<WComponent, String> tagged = getComponentModel().componentTags;
		if (tagged == null) {
			return Collections.EMPTY_MAP;
		} else {
			return Collections.unmodifiableMap(tagged);
		}
	}

	public void addParameter(final String tag, final Object value) {
		if (Util.empty(tag)) {
			throw new IllegalArgumentException("A tag must be provided");
		}

		TemplateModel model = getOrCreateComponentModel();
		if (model.parameters == null) {
			model.parameters = new HashMap<>();
		}
		model.parameters.put(tag, value);
	}

	public void removeParamter(final String tag) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.parameters != null) {
			model.parameters.remove(tag);
			if (model.parameters.isEmpty()) {
				model.parameters = null;
			}
		}
	}

	public void removeAllParamters() {
		TemplateModel model = getOrCreateComponentModel();
		model.parameters = null;
		removeAll();
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> params = getComponentModel().parameters;
		if (params == null) {
			return Collections.EMPTY_MAP;
		} else {
			return Collections.unmodifiableMap(params);
		}
	}

	/**
	 * Can override the default template engine.
	 *
	 * @param templateEngineClassName the template engine class name
	 */
	public void setTemplateEngineClassName(final String templateEngineClassName) {
		getOrCreateComponentModel().templateEngineClassName = templateEngineClassName;
	}

	/**
	 * @return the template engine class name
	 */
	public String getTemplateEngineClassName() {
		return getComponentModel().templateEngineClassName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * A naming context is only considered active if it has been set active via {@link #setNamingContext(boolean)} and
	 * also has an id name set via {@link #setIdName(String)}.
	 *
	 * @param context set true if this is a naming context.
	 */
	public void setNamingContext(final boolean context) {
		setFlag(ComponentModel.NAMING_CONTEXT_FLAG, context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamingContext() {
		return isFlagSet(ComponentModel.NAMING_CONTEXT_FLAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamingContextId() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected TemplateModel getComponentModel() {
		return (TemplateModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected TemplateModel getOrCreateComponentModel() {
		return (TemplateModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new PanelModel.
	 */
	@Override
	protected TemplateModel newComponentModel() {
		return new TemplateModel();
	}

	/**
	 * A class used to hold the list of options for this component.
	 *
	 * @author Jonathan Austin
	 */
	public static class TemplateModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The template name.
		 */
		private String templateName;

		/**
		 * Template engine class name.
		 */
		private String templateEngineClassName;

		/**
		 * Map of component tags.
		 */
		private Map<WComponent, String> componentTags;

		/**
		 * Map of template parameters.
		 */
		private Map<String, Object> parameters;
	}

}

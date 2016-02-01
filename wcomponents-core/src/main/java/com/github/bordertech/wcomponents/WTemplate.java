package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
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
	 * Construct WTemplate.
	 */
	public WTemplate() {
	}

	/**
	 * @param templateName the template file name
	 */
	public WTemplate(final String templateName) {
		this(templateName, (String) null);
	}

	/**
	 * @param templateName the template file name
	 * @param engine the template engine
	 */
	public WTemplate(final String templateName, final TemplateRendererFactory.TemplateEngine engine) {
		this(templateName, engine.getEngineName());
	}

	/**
	 * @param templateName the template file name
	 * @param engineName the template engine
	 */
	public WTemplate(final String templateName, final String engineName) {
		setTemplateName(templateName);
		setEngineName(engineName);
	}

	/**
	 * @param templateName the template file name
	 */
	public void setTemplateName(final String templateName) {
		TemplateModel model = getOrCreateComponentModel();
		model.templateName = templateName;
		model.inlineTemplate = null;
	}

	/**
	 * @return the template file name
	 */
	public String getTemplateName() {
		return getComponentModel().templateName;
	}

	/**
	 * @param inlineTemplate the inline template
	 */
	public void setInlineTemplate(final String inlineTemplate) {
		TemplateModel model = getOrCreateComponentModel();
		model.inlineTemplate = inlineTemplate;
		model.templateName = null;
	}

	/**
	 * @return the inline template
	 */
	public String getInlineTemplate() {
		return getComponentModel().inlineTemplate;
	}

	/**
	 * @param component the component to add
	 * @param tag the tag for this component in the template
	 */
	public void addTagged(final WComponent component, final String tag) {
		if (component == null) {
			throw new IllegalArgumentException("A component must be provided.");
		}
		if (Util.empty(tag)) {
			throw new IllegalArgumentException("A tag must be provided.");
		}

		TemplateModel model = getOrCreateComponentModel();
		if (model.componentTags == null) {
			model.componentTags = new HashMap<>();
		} else {
			if (model.componentTags.containsKey(component)) {
				throw new IllegalArgumentException("Component has already been added.");
			}
			if (model.componentTags.containsValue(tag)) {
				throw new IllegalArgumentException("The tag [" + tag + "] has already been added.");
			}
		}
		model.componentTags.put(component, tag);
		add(component);
	}

	/**
	 *
	 * @param component the tagged component to remove
	 */
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

	/**
	 * Remove all tagged components.
	 */
	public void removeAllTagged() {
		TemplateModel model = getOrCreateComponentModel();
		model.componentTags = null;
		removeAll();
	}

	/**
	 *
	 * @return the list of tagged components
	 */
	public Map<WComponent, String> getTaggedComponents() {
		Map<WComponent, String> tagged = getComponentModel().componentTags;
		if (tagged == null) {
			return Collections.EMPTY_MAP;
		} else {
			return Collections.unmodifiableMap(tagged);
		}
	}

	/**
	 *
	 * @param tag the tag for the parameter
	 * @param value the value for the parameter
	 */
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

	/**
	 *
	 * @param tag the tag of the parameter to remove
	 */
	public void removeParamter(final String tag) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.parameters != null) {
			model.parameters.remove(tag);
			if (model.parameters.isEmpty()) {
				model.parameters = null;
			}
		}
	}

	/**
	 * Remove all parameters.
	 */
	public void removeAllParamters() {
		TemplateModel model = getOrCreateComponentModel();
		model.parameters = null;
		removeAll();
	}

	/**
	 *
	 * @return a list of the parameters
	 */
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
	 * @param templateEngine the template engine
	 */
	public void setEngineName(final TemplateRendererFactory.TemplateEngine templateEngine) {
		setEngineName(templateEngine.getEngineName());
	}

	/**
	 * Can override the default template engine.
	 *
	 * @param engineName the template engine class name
	 */
	public void setEngineName(final String engineName) {
		getOrCreateComponentModel().engineName = engineName;
	}

	/**
	 * @return the template engine class name
	 */
	public String getEngineName() {
		return getComponentModel().engineName;
	}

	public void addEngineOption(final String key, final Object value) {
		TemplateModel model = getComponentModel();
		if (model.engineOptions == null) {
			model.engineOptions = new HashMap<>();
		}
		model.engineOptions.put(key, value);
	}

	public void removeEngineOption(final String key) {
		TemplateModel model = getComponentModel();
		if (model.engineOptions != null) {
			model.engineOptions.remove(key);
		}
	}

	public void removeAllEngineOptions() {
		TemplateModel model = getComponentModel();
		model.engineOptions = null;
	}

	public void setEngineOptions(final Map<String, Object> engineOptions) {
		getOrCreateComponentModel().engineOptions = engineOptions;
	}

	public Map<String, Object> getEngineOptions() {
		TemplateModel model = getComponentModel();
		if (model.engineOptions == null) {
			return Collections.EMPTY_MAP;
		} else {
			return Collections.unmodifiableMap(model.engineOptions);
		}
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
		 * Inline template.
		 */
		private String inlineTemplate;

		/**
		 * Template engine class name.
		 */
		private String engineName;

		private Map<String, Object> engineOptions;

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

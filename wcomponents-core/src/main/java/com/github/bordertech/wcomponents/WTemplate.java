package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.template.TemplateRenderer;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows the use of custom templates to layout components, insert custom css and javascript.
 * <p>
 * Using a template is the preferred method in implementing a responsive design.
 * </p>
 * <p>
 * WComponents comes configured for
 * {@link com.github.bordertech.wcomponents.template.TemplateRendererFactory.TemplateEngine#VELOCITY Velocity}, {@link com.github.bordertech.wcomponents.template.TemplateRendererFactory.TemplateEngine#HANDLEBARS Handlebars}
 * and {@link com.github.bordertech.wcomponents.template.TemplateRendererFactory.TemplateEngine#PLAINTEXT Plain Text}
 * templates.
 * </p>
 * <p>
 * Components can be included in a template by adding the component via
 * {@link #addTaggedComponent(java.lang.String, com.github.bordertech.wcomponents.WComponent) addTaggedComponent(tag, component)}.
 * These components will be rendered in place of the corresponding tag in the template.
 * </p>
 * <p>
 * Parameters can be passed into a template via
 * {@link #addParameter(java.lang.String, java.lang.Object) addParameter(tag, value)}. The instance of WTemplate is
 * passed in as a parameter with a key of "wc". The getter methods of WTemplate can be accessed via the bean notation in
 * the templates. For example "wc.id" to access the WTemplate id or "wc.bean" to access the bean (if set).
 * </p>
 * <p>
 * If no template engine is set on WTemplate then the default templating engine is used. The default template engine is
 * set via the parameter "bordertech.wcomponents.template.renderer". For example:-
 * <code>bordertech.wcomponents.template.renderer=velocity</code>
 * </p>
 * <p>
 * To override the default engine for a particular instance of WTemplate, set the chosen template engine via
 * {@link #setEngineName(com.github.bordertech.wcomponents.template.TemplateRendererFactory.TemplateEngine) setEngineName(TemplateEngine.Type)}.
 * </p>
 *
 * <p>
 * Other templating engines can be implemented by projects by setting a custom engine name via
 * {@link #setEngineName(java.lang.String) setEngineName(name)}. The engine name must be correctly configured as per the
 * {@link TemplateRendererFactory} requirements and have the necessary implementation of {@link TemplateRenderer}.
 * </p>
 * <p>
 * Configuration options can also be passed to the template engines via
 * {@link #addEngineOption(java.lang.String, java.lang.Object) addEngineOption(key, value)}. The options are determined
 * by the {@link TemplateRenderer} implementation.
 * </p>
 *
 * @see TemplateRenderer
 * @see TemplateRendererFactory
 *
 * @author Jonathan Austin
 * @since 1.0.3
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
	 * @param templateName the template file name and path
	 * @param engine the template engine
	 */
	public WTemplate(final String templateName, final TemplateRendererFactory.TemplateEngine engine) {
		this(templateName, engine.getEngineName());
	}

	/**
	 * @param templateName the template file name and path
	 * @param engineName the template engine
	 */
	public WTemplate(final String templateName, final String engineName) {
		setTemplateName(templateName);
		setEngineName(engineName);
	}

	/**
	 * @param templateName the template file name and path
	 */
	public void setTemplateName(final String templateName) {
		TemplateModel model = getOrCreateComponentModel();
		model.templateName = templateName;
		model.inlineTemplate = null;
	}

	/**
	 * @return the template file name and path
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
	 * Add a tagged component to be included in the template. The component will be rendered in place of the
	 * corresponding tag in the template.
	 *
	 * @param tag the tag for this component in the template
	 * @param component the component to add
	 */
	public void addTaggedComponent(final String tag, final WComponent component) {
		if (Util.empty(tag)) {
			throw new IllegalArgumentException("A tag must be provided.");
		}
		if (component == null) {
			throw new IllegalArgumentException("A component must be provided.");
		}

		TemplateModel model = getOrCreateComponentModel();
		if (model.taggedComponents == null) {
			model.taggedComponents = new HashMap<>();
		} else {
			if (model.taggedComponents.containsKey(tag)) {
				throw new IllegalArgumentException("The tag [" + tag + "] has already been added.");
			}
			if (model.taggedComponents.containsValue(component)) {
				throw new IllegalArgumentException("Component has already been added.");
			}
		}
		model.taggedComponents.put(tag, component);
		add(component);
	}

	/**
	 * Remove a tagged component via the component instance.
	 *
	 * @param component the tagged component to remove
	 */
	public void removeTaggedComponent(final WComponent component) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.taggedComponents != null) {
			// Find tag
			String tag = null;
			for (Map.Entry<String, WComponent> entry : model.taggedComponents.entrySet()) {
				if (entry.getValue().equals(component)) {
					tag = entry.getKey();
					break;
				}
			}
			if (tag != null) {
				removeTaggedComponent(tag);
			}
		}
	}

	/**
	 * Remove a tagged component by its tag.
	 *
	 * @param tag the tag of the component to remove
	 */
	public void removeTaggedComponent(final String tag) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.taggedComponents != null) {
			WComponent component = model.taggedComponents.remove(tag);
			if (model.taggedComponents.isEmpty()) {
				model.taggedComponents = null;
			}
			if (component != null) {
				remove(component);
			}
		}
	}

	/**
	 * Remove all tagged components.
	 */
	public void removeAllTaggedComponents() {
		TemplateModel model = getOrCreateComponentModel();
		model.taggedComponents = null;
		removeAll();
	}

	/**
	 *
	 * @return the tagged components
	 */
	public Map<String, WComponent> getTaggedComponents() {
		Map<String, WComponent> tagged = getComponentModel().taggedComponents;
		if (tagged == null) {
			return Collections.EMPTY_MAP;
		} else {
			return Collections.unmodifiableMap(tagged);
		}
	}

	/**
	 * Add a template parameter.
	 *
	 * @param tag the tag for the template parameter
	 * @param value the value for the template parameter
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
	 * Remove a template parameter.
	 *
	 * @param tag the tag of the template parameter to remove
	 */
	public void removeParameter(final String tag) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.parameters != null) {
			model.parameters.remove(tag);
			if (model.parameters.isEmpty()) {
				model.parameters = null;
			}
		}
	}

	/**
	 * Remove all template parameters.
	 */
	public void removeAllParameters() {
		TemplateModel model = getOrCreateComponentModel();
		model.parameters = null;
	}

	/**
	 *
	 * @return the template parameters
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
	 * Set a predefined template engine. If null then the default engine is used.
	 *
	 * @param templateEngine the provided template engine or null to use the default engine
	 */
	public void setEngineName(final TemplateRendererFactory.TemplateEngine templateEngine) {
		setEngineName(templateEngine == null ? null : templateEngine.getEngineName());
	}

	/**
	 * Set a template engine. If null then the default engine is used.
	 * <p>
	 * The engine name must be supported by {@link TemplateRendererFactory} and correctly configured as per the factory
	 * requirements and have the necessary implementation of {@link TemplateRenderer}.
	 * </p>
	 *
	 * @param engineName the template engine name
	 */
	public void setEngineName(final String engineName) {
		getOrCreateComponentModel().engineName = engineName;
	}

	/**
	 * @return the template engine name
	 */
	public String getEngineName() {
		return getComponentModel().engineName;
	}

	/**
	 * Pass configuration options to the template engine.
	 * <p>
	 * The options are determined by the {@link TemplateRenderer} implementation for the template engine.
	 * </p>
	 * <p>
	 * The {@link TemplateRenderer} implemented is determined by the {@link TemplateRendererFactory}.
	 * </p>
	 *
	 * @param key the engine option key
	 * @param value the engine option value
	 */
	public void addEngineOption(final String key, final Object value) {
		if (Util.empty(key)) {
			throw new IllegalArgumentException("A key must be provided");
		}
		TemplateModel model = getOrCreateComponentModel();
		if (model.engineOptions == null) {
			model.engineOptions = new HashMap<>();
		}
		model.engineOptions.put(key, value);
	}

	/**
	 * Remove a template engine option.
	 *
	 * @param key the engine option to remove
	 */
	public void removeEngineOption(final String key) {
		TemplateModel model = getOrCreateComponentModel();
		if (model.engineOptions != null) {
			model.engineOptions.remove(key);
		}
	}

	/**
	 * Remove all template engine options.
	 */
	public void removeAllEngineOptions() {
		TemplateModel model = getOrCreateComponentModel();
		model.engineOptions = null;
	}

	/**
	 *
	 * @return the engine options
	 */
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

	/**
	 * {@inheritDoc}
	 */
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
		 * Template engine name.
		 */
		private String engineName;

		/**
		 * Engine options.
		 */
		private Map<String, Object> engineOptions;

		/**
		 * Map of tagged components.
		 */
		private Map<String, WComponent> taggedComponents;

		/**
		 * Map of template parameters.
		 */
		private Map<String, Object> parameters;
	}

}

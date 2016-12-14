package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WBeanComponent provides a default implementation of a bean-aware component, and is the basis for most bean-aware
 * WComponents. It can be used as a starting point for custom application bean-aware components.
 * <p>
 * A fix has been made to the logic for a bean container, that has a bean property set, to pass the correct bean value
 * to its child bean components. This fix is an "opt in". To enable the correct bean container logic set the parameter
 * "bordertech.wcomponents.bean.logic.correct=true".
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WBeanComponent extends AbstractWComponent implements DataBound, BeanBound,
		BeanProviderBound {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WBeanComponent.class);

	/**
	 * The key used to cache the bean id in the scratch map.
	 */
	private static final String SCRATCHMAP_BEAN_ID_KEY = "WBeanComponent.request.bean.id";

	/**
	 * The key used to cache the bean object in the scratch map.
	 */
	private static final String SCRATCHMAP_BEAN_OBJECT_KEY = "WBeanComponent.request.bean.obj";

	/**
	 * <p>
	 * Retrieves the bean. The following are searched in order:</p>
	 * <ul>
	 * <li>a bean set explicitly using <code>setBean</code>;</li>
	 * <li>a bean cached in the scratch map;</li>
	 * <li>a bean provided by a BeanProvider (and subsequently cached in the scratch map);</li>
	 * <li>a bean provided by a bean-aware parent component (and subsequently cached in the scratch map).</li>
	 * </ul>
	 *
	 * @return this component's bean for the given context
	 */
	@Override
	public Object getBean() {

		// Check if bean is on the user model
		BeanAndProviderBoundComponentModel model = getComponentModel();
		Object bean = model.getBean();
		if (bean != null) {
			return bean;
		}

		// Check if using a bean provider
		BeanProvider beanProvider = getBeanProvider();
		if (beanProvider != null) {
			Object beanId = getBeanId();

			// Check if bean is in the scratch map
			if (isBeanInScratchMap(beanId)) {
				return getBeanFromScratchMap();
			}

			// Get the bean from the provider
			bean = beanProvider.getBean(this);

			// Cache the value as it may have been the result of an expensive operation.
			addBeanToScratchMap(beanId, bean);

			return bean;
		}

		// Check if search ancestors for bean
		if (!isSearchAncestors()) {
			return null;
		}

		// Search for a bean in a bean aware parent.
		// We explicitly do not cache the bean, as we can't tell if the parent's
		// value changes. In any case, it will have been cached by the parent.
		BeanAware parent = WebUtilities.getAncestorOfClass(BeanAware.class, this);
		if (parent != null) {
			String parentBeanProperty = parent.getBeanProperty();
			// Correct
			if (ConfigurationProperties.getCorrectBeanLogic()) {
				if (parentBeanProperty == null || ".".equals(parentBeanProperty)) {
					bean = parent.getBean();
				} else {
					bean = parent.getBeanValue();
				}
			} else { // Legacy
				if (parentBeanProperty != null && !".".equals(parentBeanProperty) && parentBeanProperty.
						contains(".")) {
					LOG.warn("Possible bean property logic error with bean property ["
							+ parentBeanProperty + "]. Check runtime parameter " + ConfigurationProperties.getCorrectBeanLogic() + ".");
				}
				bean = parent.getBean();
			}
		}

		return bean;
	}

	/**
	 * Sets the bean associated with this WBeanComponent. This method of bean association is discouraged, as the bean
	 * will be stored in the user's session. A better alternative is to provide a BeanProvider and a Bean Id.
	 *
	 * @param bean the bean to associate
	 */
	@Override
	public void setBean(final Object bean) {
		BeanAndProviderBoundComponentModel model = getOrCreateComponentModel();
		model.setBean(bean);

		if (getBeanProperty() == null) {
			setBeanProperty(".");
		}
		// Remove values in scratch map
		removeBeanFromScratchMap();
	}

	/**
	 * Sets the bean id associated with this WBeanComponent.
	 *
	 * This bean id will be used to obtain the bean from the associated {@link BeanProvider} whenever the bean data is
	 * required.
	 *
	 * @see BeanProviderBound
	 *
	 * @param beanId the bean id to associate
	 */
	@Override
	public void setBeanId(final Object beanId) {
		BeanAndProviderBoundComponentModel model = getOrCreateComponentModel();
		model.setBeanId(beanId);
		// Remove values in scratch map
		removeBeanFromScratchMap();
	}

	/**
	 * Retrieves the bean id associated with this component. This method will be used by a {@link BeanProvider} to
	 * retrieve the bean.
	 *
	 * @return the bean Id associated with this component
	 */
	@Override
	public Object getBeanId() {
		BeanAndProviderBoundComponentModel model = getComponentModel();
		return model.getBeanId();
	}

	/**
	 * Sets the bean property that this component is interested in. The bean property is expressed in Jakarta
	 * PropertyUtils bean notation, with an extension of "." to indicate that the bean itself should be used.
	 *
	 * @param propertyName the bean property, in Jakarta PropertyUtils bean notation
	 */
	@Override
	public void setBeanProperty(final String propertyName) {
		getOrCreateComponentModel().setBeanProperty(propertyName);
	}

	/**
	 * Retrieves the bean property that this component is interested in.
	 *
	 * @return the bean property, in Jakarta PropertyUtils bean notation
	 */
	@Override
	public String getBeanProperty() {
		return getComponentModel().getBeanProperty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSearchAncestors() {
		return getComponentModel().isSearchAncestors();
	}

	/**
	 * @param search true if search ancestors for the components bean
	 */
	public void setSearchAncestors(final boolean search) {
		getOrCreateComponentModel().setSearchAncestors(search);
	}

	/**
	 * Sets the {@link BeanProvider} associated with this WBeanComponent. The bean provider will be called to supply the
	 * bean whenever necessary.
	 *
	 * @param beanProvider the bean provider to associate
	 */
	@Override
	public void setBeanProvider(final BeanProvider beanProvider) {
		if (isLocked() && beanProvider != null && !(beanProvider instanceof Serializable)) {
			throw new SystemException(
					"Unable to store bean provider in user's session as it is not serializable: " + beanProvider.
					getClass());
		}

		getOrCreateComponentModel().setBeanProvider(beanProvider);
		String beanProperty = getBeanProperty();

		if (beanProperty == null) {
			setBeanProperty(".");
		}

		// Remove values in scratch map
		removeBeanFromScratchMap();
	}

	/**
	 * @return the associated bean provider, or null
	 */
	public BeanProvider getBeanProvider() {
		return getComponentModel().getBeanProvider();
	}

	/**
	 * Returns the data for this component. The following are searched in order:
	 * <ul>
	 * <li>a value set explicitly in the ui context using {@link #setData(Object)};</li>
	 * <li>if a bean is available; the bean's value; or</li>
	 * <li>the value set on the shared model.</li>
	 * </ul>
	 *
	 * @return the current value of this component for the given context
	 */
	@Override
	public Object getData() {
		BeanAndProviderBoundComponentModel model = getComponentModel();
		Object data = model.getData();
		String beanProperty = getBeanProperty();

		if (beanProperty != null) {
			Object sharedData = ((BeanAndProviderBoundComponentModel) getDefaultModel()).getData();

			if (!isFlagSet(ComponentModel.USER_DATA_SET) && Util.equals(data, sharedData)) {
				Object bean = getBean();

				if (bean != null) {
					data = getBeanValue();
				}
			}
		}

		return data;
	}

	/**
	 * Sets the data that this component displays/edits. For bean aware components, this should only be called from
	 * handleRequest to set user-entered data.
	 *
	 * @param data the data to set
	 */
	@Override
	public void setData(final Object data) {
		UIContext uic = UIContextHolder.getCurrent();

		if (uic == null) {
			getComponentModel().setData(data);
		} else {
			Object sharedValue = ((BeanAndProviderBoundComponentModel) getDefaultModel()).getData();

			if (getBeanProperty() != null) {
				sharedValue = getBeanValue();
			}

			getOrCreateComponentModel().setData(data);
			setFlag(ComponentModel.USER_DATA_SET, !Util.equals(data, sharedValue));
		}
	}

	/**
	 * Retrieves the bean value. The value is (temporarily) cached in the scratch map to speed up subsequent accesses.
	 *
	 * @return the bean value, or null if no bean value is available
	 */
	@Override
	public Object getBeanValue() {
		Object bean = getBean();
		Object beanValue = null;

		if (bean != null) {
			String beanProperty = getBeanProperty();

			if (beanProperty == null || ".".equals(beanProperty)) {
				beanValue = bean;
			} else {
				try {
					beanValue = PropertyUtils.getProperty(bean, beanProperty);
				} catch (Exception e) {
					LOG.error("Failed to read bean property " + beanProperty + " from " + bean, e);
				}
			}
		}

		return beanValue;
	}

	/**
	 * Updates the bean value with the value returned by {@link #getData()}.
	 */
	public void updateBeanValue() {
		Object value = getData();
		doUpdateBeanValue(value);
	}

	/**
	 * Updates the bean value with the new value.
	 *
	 * @param value the new value with which to update the bean
	 */
	protected void doUpdateBeanValue(final Object value) {
		String beanProperty = getBeanProperty();
		if (beanProperty != null && beanProperty.length() > 0 && !".".equals(beanProperty)) {
			Object bean = getBean();
			if (bean != null) {
				try {
					Object beanValue = getBeanValue();
					if (!Util.equals(beanValue, value)) {
						PropertyUtils.setProperty(bean, beanProperty, value);
					}
				} catch (Exception e) {
					LOG.error("Failed to set bean property " + beanProperty + " on " + bean);
				}
			}
		}
	}

	/**
	 * @return true if use request scope scratch map to hold bean provider results
	 */
	protected boolean isUseRequestScopeScratchMap() {
		return ConfigurationProperties.getBeanProviderRequestScopeEnabled();

	}

	/**
	 * Remove the bean from the scratch maps.
	 */
	protected void removeBeanFromScratchMap() {
		Map<Object, Object> scratchMap = getBeanScratchMap();
		if (scratchMap == null) {
			return;
		}
		scratchMap.remove(SCRATCHMAP_BEAN_ID_KEY);
		scratchMap.remove(SCRATCHMAP_BEAN_OBJECT_KEY);
	}

	/**
	 * @param beanId check if this bean id is in the scratch map
	 * @return true if current bean is in the scratch map
	 */
	protected boolean isBeanInScratchMap(final Object beanId) {
		Map<Object, Object> scratchMap = getBeanScratchMap();
		if (scratchMap == null) {
			return false;
		}
		if (scratchMap.containsKey(SCRATCHMAP_BEAN_OBJECT_KEY) && scratchMap.containsKey(SCRATCHMAP_BEAN_ID_KEY)) {
			Object scratchId = scratchMap.get(SCRATCHMAP_BEAN_ID_KEY);
			return Objects.equals(scratchId, beanId);
		}
		return false;
	}

	/**
	 * @return the bean from the scratch map
	 */
	protected Object getBeanFromScratchMap() {
		Map<Object, Object> scratchMap = getBeanScratchMap();
		if (scratchMap == null) {
			return null;
		}
		Object bean = scratchMap.get(SCRATCHMAP_BEAN_OBJECT_KEY);
		return bean;
	}

	/**
	 *
	 * @param beanId the bean id to put in the scratch map
	 * @param bean the bean to put in the scratch map
	 */
	protected void addBeanToScratchMap(final Object beanId, final Object bean) {
		Map<Object, Object> scratchMap = getBeanScratchMap();
		if (scratchMap != null) {
			scratchMap.put(SCRATCHMAP_BEAN_ID_KEY, beanId);
			scratchMap.put(SCRATCHMAP_BEAN_OBJECT_KEY, bean);
		}
	}

	/**
	 * @return the scratch map with the correct scope.
	 */
	protected Map<Object, Object> getBeanScratchMap() {
		UIContext uic = UIContextHolder.getCurrent();
		if (uic == null) {
			return null;
		}
		if (isUseRequestScopeScratchMap()) {
			return uic.getRequestScratchMap(this);
		} else {
			return uic.getScratchMap(this);
		}
	}

	/**
	 * Indicates whether this component's data has changed from the default value.
	 *
	 * TODO: This needs to be added to the databound interface after the bulk of the components have been converted.
	 *
	 * @return true if this component's current value differs from the default value for the given context.
	 */
	public boolean isChanged() {
		Object currentValue = getData();
		Object sharedValue = ((BeanAndProviderBoundComponentModel) getDefaultModel()).getData();

		if (getBeanProperty() != null) {
			sharedValue = getBeanValue();
		}

		return !Util.equals(currentValue, sharedValue);
	}

	/**
	 * Resets the data back to the default value, which may either be from a bean or the shared model.
	 */
	public void resetData() {
		getOrCreateComponentModel().resetData();
	}

	/**
	 * Creates a new model appropriate for this component.
	 *
	 * @return a new {@link BeanAndProviderBoundComponentModel}.
	 */
	@Override
	protected BeanAndProviderBoundComponentModel newComponentModel() {
		return new BeanAndProviderBoundComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected BeanAndProviderBoundComponentModel getComponentModel() {
		return (BeanAndProviderBoundComponentModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected BeanAndProviderBoundComponentModel getOrCreateComponentModel() {
		return (BeanAndProviderBoundComponentModel) super.getOrCreateComponentModel();
	}

}

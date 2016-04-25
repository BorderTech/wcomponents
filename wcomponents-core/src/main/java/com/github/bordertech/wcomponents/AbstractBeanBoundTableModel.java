package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTable.BeanBoundTableModel;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A default implementation of the {@link BeanBoundTableModel} interface.
 * <p>
 * This implementation does not support editing, row selection, etc.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractBeanBoundTableModel extends AbstractTableModel implements
		BeanBoundTableModel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractBeanBoundTableModel.class);

	/**
	 * The data model's BeanProvider.
	 */
	private BeanProvider beanProvider;

	/**
	 * The beanId is used to obtain the bean from the provider.
	 */
	private Object beanId;

	/**
	 * This bean property that this component is interested in. The property is specified using Jakarta BeanUtils bean
	 * notation.
	 */
	private String beanProperty;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanProvider(final BeanProvider beanProvider) {
		this.beanProvider = beanProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanId(final Object beanId) {
		this.beanId = beanId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getBeanId() {
		return beanId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanProperty(final String propertyName) {
		this.beanProperty = propertyName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBeanProperty() {
		return beanProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getBean() {
		Object bean = beanProvider.getBean(this);
		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getBeanValue() {
		Object bean = getBean();

		Object beanValue = null;
		if (bean != null) {
			String property = getBeanProperty();
			if (property == null || ".".equals(property)) {
				beanValue = bean;
			} else {
				try {
					beanValue = PropertyUtils.getProperty(bean, property);
				} catch (Exception e) {
					LOG.error("Failed to read bean property " + property + " from " + bean, e);
				}
			}
		}

		return beanValue;
	}
}

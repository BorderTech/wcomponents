package com.github.bordertech.wcomponents;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A default implementation of the {@link BeanTableDataModel} interface. This implementation does not support editing,
 * row selection, filtering etc. Subclasses need only implement the {@link TableDataModel#getRowCount()} and
 * {@link TableDataModel#getValueAt(int, int)} methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated Use {@link WTable} and {@link AbstractBeanBoundTableModel} instead.
 */
@Deprecated
public abstract class AbstractBeanTableDataModel extends AbstractTableDataModel implements
		BeanTableDataModel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractBeanTableDataModel.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getBeanValue() {
		// For backward compatibility, the getBean() method actually returns the "bean value".
		return getBean();
	}

}

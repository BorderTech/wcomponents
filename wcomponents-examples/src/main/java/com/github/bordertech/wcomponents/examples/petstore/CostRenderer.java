package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.WText;
import java.text.DecimalFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CostRenderer renders a dollar amount specified as an int, in cents. Expects an Integer as its bean value.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class CostRenderer extends WText {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(CostRenderer.class);

	/**
	 * @return the label text.
	 */
	@Override
	public String getText() {
		Object beanValue = getBeanValue();

		if (beanValue instanceof Integer) {
			return new DecimalFormat("$0.00").format(((Integer) beanValue).intValue() / 100.0);
		} else if (beanValue != null) {
			LOG.error("Unknown bean value, expected Integer, got " + beanValue.getClass().getName());
		}

		return "$-.--"; // Error
	}
}

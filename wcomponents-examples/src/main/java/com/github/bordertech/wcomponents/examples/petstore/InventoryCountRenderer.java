package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;

/**
 * CostRenderer renders the availability of the inventory item.
 *
 * Expects an InventoryBean as its bean value.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class InventoryCountRenderer extends WText {

	/**
	 * @return the label text.
	 */
	@Override
	public String getText() {
		InventoryBean item = (InventoryBean) getBeanValue();

		if (item != null) {
			if (item.getCount() == 0) {
				switch (item.getStatus()) {
					case InventoryBean.STATUS_NO_LONGER_AVAILABLE:
						return "No longer available";
					case InventoryBean.STATUS_NEW:
						return "Coming soon";
					default:
						return "Sold out";
				}
			} else {
				return String.valueOf(item.getCount());
			}
		}

		return ""; // Error
	}
}

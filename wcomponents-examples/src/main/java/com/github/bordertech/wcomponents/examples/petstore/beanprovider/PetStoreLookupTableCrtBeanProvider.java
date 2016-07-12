package com.github.bordertech.wcomponents.examples.petstore.beanprovider;

import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.examples.petstore.PetStoreLookupTable;
import com.github.bordertech.wcomponents.util.LookupTable;
import java.util.List;

/**
 * CrtBeanProvider is a beanProvider that provides data from a CRT. The provider can be used to look-up a single or
 * multiple entries, depending on which constructor is used.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class PetStoreLookupTableCrtBeanProvider implements BeanProvider {

	/**
	 * The name of the lookup table that will be used to look up the value.
	 */
	private final String lookupTableName;

	/**
	 * The table code that will be used to look up the value. If null, the bound component will provide the code.
	 */
	private final String tableCode;

	/**
	 * Singleton instance of the PetStoreLookupTable. This would normally be handled by
	 * factory.newInstance(LookupTable), but we don't want to pollute tests with the specific property for the
	 * PetSToreLookupTable.
	 */
	private static final LookupTable table = new PetStoreLookupTable();

	/**
	 * Creates a CrtBeanProvider that will use the given Crt, and the code from the bound component. The value returned
	 * by this provided will vary depending on the Crt and bound component.
	 *
	 * @param lookupTableName the crt name.
	 */
	public PetStoreLookupTableCrtBeanProvider(final String lookupTableName) {
		this(lookupTableName, null);
	}

	/**
	 * Creates a CrtBeanProvider that will use the given Crt name and code. This provider will then only return one
	 * value from the Crt.
	 *
	 * @param lookupTableName the Crt name.
	 * @param tableCode the Crt key that will be use to look up the value.
	 */
	public PetStoreLookupTableCrtBeanProvider(final String lookupTableName, final String tableCode) {
		this.lookupTableName = lookupTableName;
		this.tableCode = tableCode;
	}

	/**
	 * Obtains the bean for the given {@link BeanProviderBound}.
	 *
	 * @param beanProviderBound the BeanProviderBound to provide data for.
	 * @return the CrtEntry description if found, otherwise null.
	 */
	@Override
	public Object getBean(final BeanProviderBound beanProviderBound) {
		if (lookupTableName != null) {
			List<?> entries = table.getTable(lookupTableName);
			String code = tableCode;
			String desc = null;

			// If a code hasn't been provided, try to get it from the bean provider bound object
			if (code == null) {
				code = (String) beanProviderBound.getBeanId();
			}

			for (Object entry : entries) {
				if (code.equals(table.getCode(lookupTableName, entry))) {
					desc = table.getDescription(lookupTableName, entry);
					break;
				}
			}

			return desc;
		}

		return null;
	}
}

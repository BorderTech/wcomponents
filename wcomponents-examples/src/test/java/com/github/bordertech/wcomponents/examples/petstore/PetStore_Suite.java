package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.examples.petstore.beanprovider.CrtBeanProvider_Test;
import com.github.bordertech.wcomponents.examples.petstore.beanprovider.InventoryBeanProvider_Test;
import com.github.bordertech.wcomponents.examples.petstore.beanprovider.ProductBeanProvider_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.AddressBean_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.CartBean_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.ConfirmOrderBean_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.PlaceOrderService_Test;
import com.github.bordertech.wcomponents.examples.petstore.model.ProductBean_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech.wcomponents.examples.petstore} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	PetStoreApp_Test.class,
	CartPanel_Test.class,
	CostRenderer_Test.class,
	InventoryCostRenderer_Test.class,
	InventoryCountRenderer_Test.class,
	ProductImage_Test.class,
	CrtBeanProvider_Test.class,
	InventoryBeanProvider_Test.class,
	ProductBeanProvider_Test.class,
	AddressBean_Test.class,
	CartBean_Test.class,
	ConfirmOrderBean_Test.class,
	InventoryBean_Test.class,
	PetStoreDao_Test.class,
	PlaceOrderService_Test.class,
	ProductBean_Test.class
})
public class PetStore_Suite {
}

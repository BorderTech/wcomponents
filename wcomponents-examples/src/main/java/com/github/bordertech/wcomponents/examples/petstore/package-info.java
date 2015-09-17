/**
 * Demonstrates best practices in using the WComponent API to build a "real-world" application.
 *
 * <p>
 * The application ("PetStoreApp") is a mock-up of an online store, using a format most people are familiar with. The
 * store features a product-listing, from which users can view products and add them to their "shopping cart". To
 * complete an order, users select to "check out" their cart and fill in contact and payment details.</p>
 *
 * <h3>Running the PetStore application</h3>
 *
 * <p>
 * PetStoreApp can be run from the Local Development Environment (LDE). Instructions on setting up the LDE can be found
 * in the WComponent Maven site documentation.
 *
 * To configure the LDE to run the PetStoreApp, set the following value in your <code>local_app.properties</code> file:
 * <pre>   ui.web.component.to.launch=com.github.bordertech.wcomponents.examples.petstore.PetStoreApp</pre>
 *
 * <h3>Package structure</h3>
 *
 * The package structure is as follows:
 *
 * <ul>
 * <li><code>com.github.bordertech.wcomponents.examples.petstore</code> -- contains the core application classes.</li>
 * <li><code>com.github.bordertech.wcomponents.examples.petstore.beanprovider</code> -- contains
 * {@link com.github.bordertech.wcomponents.BeanProvider BeanProvider}s to provide data to the UI.</li>
 * <li><code>com.github.bordertech.wcomponents.examples.petstore.model</code> -- contains the PetStore data beans.</li>
 * <li><code>com.github.bordertech.wcomponents.examples.petstore.resources</code> -- contains various binary
 * resources.</li>
 * </ul>
 */
package com.github.bordertech.wcomponents.examples.petstore;

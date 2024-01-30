package com.github.bordertech.wcomponents.container;

/**
 * Helper class for tests using {@link TransformXMLInterceptor}.
 *
 * @author Jonathan Austin
 */
public final class TransformXMLTestHelper {

	/**
	 * The input xml.
	 */
	public static final String TEST_XML = "<kung><fu>is good for you</fu></kung>";

	/**
	 * The expected HTML result.
	 */
	public static final String EXPECTED = "<omg><wtf>is good for you</wtf></omg>";

	/**
	 * Prevent instantiation.
	 */
	private TransformXMLTestHelper() {
	}

	/**
	 * Use reflection the reinitialize the TransformXMLInterceptor class.
	 */
	public static void reloadTransformer() {
		TransformXMLInterceptor.initTemplates();
	}

}

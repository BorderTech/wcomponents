package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.util.SystemException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.xml.transform.Templates;

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
		try {
			Field field = TransformXMLInterceptor.class.getDeclaredField("TEMPLATES");
			// Make the field accessible.
			field.setAccessible(true);

			//Make the field non-final.
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

			//Get the value from the static method
			Method initTemplates = TransformXMLInterceptor.class.getDeclaredMethod("initTemplates");
			initTemplates.setAccessible(true);
			Templates value = (Templates) initTemplates.invoke(null);

			field.set(null, value);

		} catch (SecurityException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException ex) {
			throw new SystemException(ex);
		}
	}

}

package com.github.bordertech.wcomponents.velocity;

import com.github.bordertech.wcomponents.WTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author James Gifford
 * @since 1.0.0
 * @deprecated Use {@link WTemplate} instead.
 */
@Deprecated
public final class VelocityTemplateManager {

	/**
	 * Look for templates under this directory in the classpath.
	 */
	private static final String VELOCITY_PREFIX = "";

	/**
	 * Remove this prefix from full classnames before appending to the above prefix.
	 */
	private static final String PATH_REMOVE = "";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityTemplateManager.class);

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private VelocityTemplateManager() {
	}

	/**
	 * Map the given class to a velocity template resource name.
	 * <p>
	 * For instance, com.github.bordertech.wcomponents.WTextField maps to the template in
	 * com/github/bordertech/wcomponents/WTextField.vm
	 * </p>
	 *
	 * @param clazz is the Class to find the resource name for.
	 * @return the template resource name for the given class.
	 */
	public static String toTemplateResourceName(final Class clazz) {
		String url = getTemplatePath(clazz);
		url = url.replace('.', '/');
		int index = url.lastIndexOf('$');

		if (index != -1) {
			int first = url.lastIndexOf('/');
			String classs = url.substring(index + 1, url.length());
			String dir = url.substring(0, first + 1);
			url = dir + classs;
		}

		url = VELOCITY_PREFIX + url + ".vm";
		// Internalize to avoid having heaps of copies of identical strings around.
		url = url.intern();
		LOG.debug("vm template : " + url);
		return url;
	}

	/**
	 * @param clazz a Class from which to retrieve the Class Name
	 * @return String which is the class name without the path
	 */
	private static String getTemplatePath(final Class clazz) {
		String name = clazz.getName();
		LOG.debug("Full ClassPath : " + name);
		name = name.substring(PATH_REMOVE.length(), name.length());
		LOG.debug("Classname : " + name);
		return name;
	}
}

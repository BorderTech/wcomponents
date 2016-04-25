package com.github.bordertech.wcomponents.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Provides a generic mechanism for obtaining objects which implement a requested interface. A new object will be
 * created each time the newImplementation method is called.</p>
 *
 * <p>
 * The runtime {@link Config} class is used to look up the implementing class, based on the requested interface's
 * classname. This is done by prefixing the full interface name with "bordertech.wcomponents.factory.impl.". For
 * example, to specify that the com.github.myapp.util.FooImpl implements com.github.myapp.util.Foo interface, the
 * following should be added to the configuration:</p>
 * <pre>
 * bordertech.wcomponents.factory.impl.com.github.myapp.util.Foo=com.github.myapp.util.FooImpl
 * </pre>
 *
 * @author James Gifford
 */
public final class Factory {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(Factory.class);

	/**
	 * Prefix used to look up implementing classes in the {@link Config configuration}.
	 */
	public static final String PREFIX = "bordertech.wcomponents.factory.impl.";

	/**
	 * Prevent instantiation of this utility class.
	 */
	private Factory() {
	}

	/**
	 * Given an interface, instantiate a class implementing that interface.
	 *
	 * The classname to instantiate is obtained by looking in the runtime {@link Config configuration}, under the
	 * bordertech.wcomponents.factory.impl.&lt;interface name&gt; key.
	 *
	 * @param <T> the interface type.
	 * @param interfaz the interface to instantiate an implementation for.
	 * @return an Object which implements the given interface.
	 * @throws SystemException if no implementing class is registered in the {@link Config configuration}.
	 */
	public static <T> T newInstance(final Class<T> interfaz) {
		Configuration config = Config.getInstance();
		String classname = config.getString(PREFIX + interfaz.getName());

		if (classname == null) {
			// Hmmm - this is bad. For the time being let's dump the parameters.
			LOG.fatal("No implementing class for " + interfaz.getName());
			LOG.fatal("There needs to be a parameter defined for " + PREFIX + interfaz.getName());

			throw new SystemException("No implementing class for " + interfaz.getName() + "; "
					+ "There needs to be a parameter defined for " + PREFIX + interfaz.getName());
		}

		try {
			Class<T> clas = (Class<T>) Class.forName(classname.trim());
			return clas.newInstance();
		} catch (Exception ex) {
			throw new SystemException("Failed to instantiate object of class " + classname, ex);
		}
	}

	/**
	 * Given an interface, determine if an implementation of that interface is available to this factory, see
	 * newInstance(.) method.
	 *
	 * @param interfaz the interface to check for.
	 * @return true if an implementation of the interface is available to this factory.
	 */
	public static boolean implementationExists(final Class<?> interfaz) {
		Configuration config = Config.getInstance();
		return !Util.empty(config.getString(PREFIX + interfaz.getName()));
	}
}

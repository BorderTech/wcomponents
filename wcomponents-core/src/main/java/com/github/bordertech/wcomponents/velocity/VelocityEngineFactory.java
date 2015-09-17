package com.github.bordertech.wcomponents.velocity;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * A wrapper for the creation of a (singleton) VelocityEngine, using runtime {@link Config configuration} parameters for
 * configuration.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public final class VelocityEngineFactory {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityEngineFactory.class);

	/**
	 * If this setting is non-null, velocity templates will be loaded from the given directory. This is good for
	 * developers, who can point into their source tree directly. Templates will not be cached in this case.
	 */
	private static final String FILE_TEMPLATES_KEY = "bordertech.wcomponents.velocity.fileTemplatesDir";

	/**
	 * If we are not using fileTemplates, the templates are read from the classpath. This setting governs whether we
	 * cache these templates or not. For production, we should cache.
	 */
	private static final String CACHE_TEMPLATES_KEY = "bordertech.wcomponents.velocity.cacheTemplates.enabled";

	/**
	 * The singleton VelocityEngine instance associated with this factory.
	 */
	private static VelocityEngine engine;

	/**
	 * Prevent instantiation of this class.
	 */
	private VelocityEngineFactory() {
	}

	/**
	 * <p>
	 * Returns the VelocityEngine associated with this factory. If this is the first time we are using the engine,
	 * create it and initialise it.</p>
	 *
	 * <p>
	 * Note that velocity engines are hugely resource intensive, so we don't want too many of them. For the time being
	 * we have a single instance stored as a static variable. This would only be a problem if the VelocityLayout class
	 * ever wanted to use different engine configurations (unlikely).</p>
	 *
	 * @return the VelocityEngine associated with this factory.
	 */
	public static synchronized VelocityEngine getVelocityEngine() {
		if (engine == null) {
			Configuration config = Config.getInstance();
			String fileTemplates = config.getString(FILE_TEMPLATES_KEY);
			boolean cacheTemplates = config.getBoolean(CACHE_TEMPLATES_KEY);

			VelocityEngine newEngine = new VelocityEngine();
			Properties props = new Properties();

			// Configure the velocity template differently according to whether we are in
			// "source mode" or not
			if (fileTemplates != null && !"".equals(fileTemplates)) {
				// Source mode
				LOG.info("Velocity engine running in source mode from " + fileTemplates);

				props.setProperty("resource.loader", "file,class");
				props.setProperty("file.resource.loader.path", fileTemplates);
				props.setProperty("file.resource.loader.cache", "false");
				props.setProperty("file.resource.loader.modificationCheckInterval", "2");

				props.setProperty("class.resource.loader.cache", "false");
				props.setProperty("class.resource.loader.modificationCheckInterval", "2");

				props.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			} else {
				String cache = String.valueOf(cacheTemplates);
				props.setProperty("class.resource.loader.cache", cache);
				props.setProperty("resource.loader", "class");
				props.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			}

			// Setup commons logging for velocity
			props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
					"com.github.bordertech.wcomponents.velocity.VelocityLogger");

			// Set up access to the common velocity macros.
			props.setProperty(RuntimeConstants.VM_LIBRARY, config.getString(
					"bordertech.wcomponents.velocity.macroLibrary"));

			try {
				if (LOG.isInfoEnabled()) {
					// Dump properties
					StringWriter writer = new StringWriter();
					props.list(new PrintWriter(writer));
					LOG.info("Configuring velocity with the following properties...\n" + writer);
				}

				newEngine.init(props);
			} catch (Exception ex) {
				throw new SystemException("Failed to configure VelocityEngine", ex);
			}

			engine = newEngine;
		}

		return engine;
	}
}

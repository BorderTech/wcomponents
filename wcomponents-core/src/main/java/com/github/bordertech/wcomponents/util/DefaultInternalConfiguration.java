package com.github.bordertech.wcomponents.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.MapConfiguration;

/**
 * <p>
 * Implementation of the {@link Configuration} interface.
 * </p>
 * <p>
 * Note: We can't use the logging infrastructure in this class, because the logging infrastructure is probably not
 * initialised yet. Indeed, it will be via this mechanism that the logging configuration is loaded! Instead, we have a
 * primitive mechanism for recording significant events which can be accessed later for logging if need be.
 * </p>
 *
 * @author Yiannis Paschalidis, based on ParamImpl
 * @since 1.0.0
 */
final class DefaultInternalConfiguration implements Configuration {

	/**
	 * This array defines the file names which we load the internal configuration from, as well as the order in which
	 * the files are loaded.
	 */
	private static final String[] PARAMETER_LOAD_ORDER
			= {
				// The name of the first resource we look for is for internal properties
				"wcomponents.properties",
				// The name of the next resource we look at is for application properties
				"wcomponents-app.properties",
				// The last properties which are loaded are local/developer properties
				"local_app.properties"
			};

	/**
	 * If this parameter is defined, it is treated as a comma-separated list of additional resources to load. The
	 * include is processed immediately.
	 */
	private static final String INCLUDE = "include";

	/**
	 * If this parameter is defined, it is taken as a (comma-separated) resource to load. The resource is loaded after
	 * the current (set of) resources is loaded.
	 */
	private static final String INCLUDE_AFTER = "includeAfter";

	/**
	 * If this parameter is defined and resolves to true as a boolean, then the system properties will be merged at the
	 * end of the loading process.
	 */
	private static final String USE_SYSTEM_PROPERTIES = "bordertech.wcomponents.parameters.useSystemProperties";

	/**
	 * If this parameter is set to true, then after loading the parameters, they will be dumped to the console.
	 */
	private static final String DUMP = "bordertech.wcomponents.parameters.dump.console";

	/**
	 * Parameters with this prefix will be dumped into the System parameters. This feature is for handling recalcitrant
	 * 3rd party software only - not for general use!!!
	 */
	private static final String SYSTEM_PARAMETERS_PREFIX = "bordertech.wcomponents.parameters.system.";

	// -----------------------------------------------------------------------------------------------------------------
	// State used during loading of parameters
	/**
	 * The messages logged during loading of the configuration. We can't depend on a logging framework to log errors, as
	 * this class is typically used to configure the logging.
	 */
	private final StringBuffer messages = new StringBuffer();

	/**
	 * The resource being loaded. This is used for the relative form of resource loading.
	 */
	private final Stack<String> resources = new Stack<>();

	/**
	 * A generic object that allows us to synchronized refreshes. Required so that gets and refreshes are threadsafe
	 */
	private final Object lockObject = new Object();

	// -----------------------------------------------------------------------------------------------------------------
	// Implementation
	/**
	 * Our backing store is a Map object.
	 */
	private Map<String, Object> backing;

	/**
	 * Explicitly cache booleans for flag look-up speed.
	 */
	private Set<String> booleanBacking;

	/**
	 * Stores "explanations" of where each setting comes from. Each parameter will have a history, explaining all the
	 * locations where that parameter was defined, in reverse order (so the first entry is the defining entry).
	 */
	private Map<String, String> locations;

	/**
	 * Cache of subcontexts, by {true,false}-prefix.
	 */
	private Map<String, Properties> subcontextCache;

	private IncludeProperties runtimeProperties;

	/**
	 * Variables that we are in the process of substituting. This is used to detect recursive substitutions
	 */
	private final Set<String> substituting = new HashSet<>();

	/**
	 * Creates a DefaultInternalConfiguration. The constructor is package protected as it is called from
	 * {@link Config#getInstance()};
	 */
	protected DefaultInternalConfiguration() {
		initialiseInstanceVariables();
		load();
	}

	/**
	 * Create an instance of DefaultInternalConfiguration, loading values from a resource named "resourceName". This
	 * constructor is provided purely for testing purposes. Application code should always use the
	 * {@link Config#getInstance()} method.
	 *
	 * @param resourceName the name of the resource to load (from the classloader, working or home directory)
	 */
	protected DefaultInternalConfiguration(final String resourceName) {
		initialiseInstanceVariables();
		loadTop(resourceName);

		// Now perform variable substitution.
		do {
		} while (substitute());
	}

	/**
	 * {@inheritDoc}
	 */
	public String get(final String key, final String defolt) {
		String result = get(key);

		if (result == null) {
			return defolt;
		} else {
			return result;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String get(final String key) {
		return (String) backing.get(key);
	}

	/**
	 * Splits the given comma-delimited string into an an array. Leading/trailing spaces in list items will be trimmed.
	 *
	 * @param list the String to split.
	 * @return the split version of the list.
	 */
	private String[] parseStringArray(final String list) {
		StringTokenizer tokenizer = new StringTokenizer(list, ",", false);
		int length = tokenizer.countTokens();
		String[] arr = new String[length];

		for (int i = 0; tokenizer.hasMoreElements(); i++) {
			arr[i] = cleanSpaces(tokenizer.nextToken());
		}

		return arr;
	}

	/**
	 * Removes any leading/trailing spaces from the given string. This has the same effect as calling
	 * {@link String#trim}, but is null-safe.
	 *
	 * @param aStr the String to trim.
	 * @return the trimmed String, or null if the <code>aStr</code> was null.
	 */
	private String cleanSpaces(final String aStr) {
		if (aStr == null) {
			return aStr;
		}

		return aStr.trim();
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getProperties() {
		// Don't return the backing directly; make a copy so that the caller can't change us...
		Properties copy = new Properties();
		copy.putAll(backing);
		return copy;
	}

	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * This method initialises most of the instance variables.
	 */
	private void initialiseInstanceVariables() {
		backing = new HashMap<>();
		booleanBacking = new HashSet<>();
		locations = new HashMap<>();

		// subContextCache is updated on the fly so ensure no concurrent modification.
		subcontextCache = Collections.synchronizedMap(new HashMap());
		runtimeProperties = new IncludeProperties("Runtime: added at runtime");
	}

	/**
	 * Load the backing from the properties file visible to our classloader, plus the filesystem.
	 */
	@SuppressWarnings("checkstyle:emptyblock")
	private void load() {
		recordMessage("Loading parameters");
		File cwd = new File(".");
		String workingDir;

		try {
			workingDir = cwd.getCanonicalPath();
		} catch (IOException ex) {
			workingDir = "UNKNOWN";
		}

		recordMessage("Working directory is " + workingDir);

		for (String resourceName : PARAMETER_LOAD_ORDER) {
			loadTop(resourceName);
		}

		if (getBoolean(USE_SYSTEM_PROPERTIES)) {
			recordMessage("Loading from system properties");
			load(System.getProperties(), "System Properties", true);
		}

		// Now perform variable substitution.
		do {
			// Do nothing while loop
		} while (substitute());

		if (getBoolean(DUMP)) {
			// Can't use logging infrastructure here, so dump to console
			log(getDebuggingInfo());
			log(getMessages());
		}

		// We don't want the StringBuffer hanging around after 'DUMP'.
		clearMessages();

		// Now move any parameters with the system parameters prefix into the real system parameters.
		Properties systemProperties = getSubProperties(SYSTEM_PARAMETERS_PREFIX, true);
		System.getProperties().putAll(systemProperties);
	}

	/**
	 * @return debugging information for logging on application start-up.
	 */
	protected String getDebuggingInfo() {
		final String paramsFile = "log4j.appender.PARAMS.File";

		File cwd = new File(".");
		String workingDir;

		try {
			workingDir = cwd.getCanonicalPath();
		} catch (IOException ex) {
			workingDir = "UNKNOWN";
		}

		String codesourceStr = "";

		// Try to be sneaky and print the codesource location (for orientation of user)
		try {
			ProtectionDomain domain = getClass().getProtectionDomain();
			CodeSource codesource = null;

			if (domain != null) {
				codesource = domain.getCodeSource();
			}

			codesourceStr = (codesource != null ? " code location of ParamImpl: " + codesource.
					getLocation() : "");
		} catch (Throwable failed) {
			// Okay
		}

		StringBuffer info = new StringBuffer();

		info.append("----Parameters start----");
		info.append(codesourceStr);
		info.append("\nWorking directory is ");
		info.append(workingDir);
		info.append(
				"\nParameters have loaded, there is a full parameter dump in log4j FILE appender at ");
		info.append(get(paramsFile));
		info.append("\nTo dump all params to stdout set ");
		info.append(DUMP);
		info.append(" to true; currently value is ");
		info.append(get(DUMP));
		info.append("\n----Parameters end------");

		return info.toString();
	}

	/**
	 * Loading of "top level" resources is different to the general recursive case, since it is only at the top level
	 * that we check for the includeAfter parameter.
	 *
	 * @param resourceName the path of the resource to load from.
	 */
	@SuppressWarnings("checkstyle:emptyblock")
	private void loadTop(final String resourceName) {
		try {
			resources.push(resourceName);

			load(resourceName);

			// Now check for INCLUDE_AFTER resources
			String includes = get(INCLUDE_AFTER);

			if (includes != null) {
				// First, do substitution on the INCLUDE_AFTER
				do {
				} while (substitute(INCLUDE_AFTER));

				// Now split and process
				String[] includeAfter = getString(INCLUDE_AFTER).split(",");
				backing.remove(INCLUDE_AFTER);
				for (String after : includeAfter) {
					loadTop(after);
				}
			}
		} finally {
			resources.pop();
		}
	}

	/**
	 * Try loading the given resource name. There may be several resources corresponding to that name...
	 *
	 * @param resourceName the path of the resource to load from.
	 */
	private void load(final String resourceName) {
		boolean found = false;

		try {
			resources.push(resourceName);

			// Try classloader - load the resources in reverse order of the enumeration.  Since later-loaded resources
			// override earlier-loaded ones, this better corresponds to the usual classpath behaviour.
			ClassLoader classloader = getParamsClassLoader();
			List<URL> urls = new ArrayList<>();

			for (Enumeration<URL> res = classloader.getResources(resourceName); res.
					hasMoreElements();) {
				urls.add(res.nextElement());
			}

			recordMessage("Resource " + resourceName + " was found  " + urls.size() + " times");

			// Sometimes the same URL will crop up several times (because of redundant entries in classpaths).  Also,
			// sometimes the same file appears under several URLS (because it's packaged into a jar and also a classes
			// directory, perhaps). In these circumstances we really only want to load the resource once - we load the
			// first one and then ignore later ones.
			Map<String, String> loadedFiles = new HashMap<>();

			// Build up a list of the byte arrays from the files that we then process.
			List<byte[]> contentsList = new ArrayList<>();
			List<URL> urlList = new ArrayList<>();

			// This processes from the front-of-classpath to end-of-classpath since end-of-classpath ones appear last in
			// the enumeration
			for (int i = 0; i < urls.size(); i++) {
				URL url = urls.get(i);
				found = true;

				// Load the contents of the resource, for comparison with existing resources.
				InputStream urlContentStream = url.openStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				StreamUtil.copy(urlContentStream, baos);
				urlContentStream.close();
				byte[] urlContentBytes = baos.toByteArray();
				String urlContent = new String(urlContentBytes, "UTF-8");

				// Check if we have already loaded this file.
				if (loadedFiles.keySet().contains(urlContent)) {
					recordMessage("Skipped url " + url + " - duplicate of " + loadedFiles.get(urlContent));
					continue;
				}

				loadedFiles.put(urlContent, url.toString());
				contentsList.add(urlContentBytes);
				urlList.add(url);
			}

			for (int i = contentsList.size() - 1; i >= 0; i--) {
				byte[] buff = contentsList.get(i);
				URL url = urlList.get(i);
				recordMessage("Loading from url " + url + "...");
				ByteArrayInputStream in = new ByteArrayInputStream(buff);

				// Use the "IncludeProperties" to load properties into us one at a time....
				IncludeProperties properties = new IncludeProperties(url.toString());
				properties.load(in);
			}

			File file = new File(resourceName);

			// Don't reload the file in the working directory if we are in the home directory.
			if (file.exists()) {
				recordMessage("Loading from file " + filename(file) + "...");
				found = true;

				// Use the "IncludeProperties" to load properties into us, one at a time....
				IncludeProperties properties = new IncludeProperties("file:" + filename(file));
				properties.load(new BufferedInputStream(new FileInputStream(file)));
			}

			if (!found) {
				recordMessage("Did not find resource " + resourceName);
			}
		} catch (IOException ex) {
			// This is bad.
			recordException(ex);
		} catch (IllegalArgumentException ex) {
			// Most likely a "Malformed uxxxx encoding." error, which is
			// usually caused by a developer forgetting to escape backslashes
			recordException(ex);
		} finally {
			resources.pop();
		}
	}

	/**
	 * Retrieves the canonical path for a given file.
	 *
	 * @param aFile the file to get the canonical path for.
	 * @return the canonical path to the given file, or <code>"UNKNOWN FILE"</code> on error.
	 */
	private String filename(final File aFile) {
		try {
			return aFile.getCanonicalPath();
		} catch (IOException ex) {
			recordException(ex);
			return "UNKNOWN FILE";
		}
	}

	/**
	 * @return the ClassLoader instance for this class.
	 */
	private ClassLoader getParamsClassLoader() {
		// Ideally we could just use the defining classloader for this class.  But unfortunately we have to deal with
		// "legacy" deployment styles where this class is visible to the container's system class loader (ie in the
		// system class path), instead of being deployed within the application.
		//
		// One idea is to use the context class loader; but iPlanet does not set this usefully, so we have to fool
		// about...

		// First, try the context class loader.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if (loader == null) {
			recordMessage("No context classloader had been set");
			loader = getClass().getClassLoader();
			recordMessage("Using classloader " + loader);
			return loader;
		}

		// Are we visible to this class loader?
		try {
			Class test = loader.loadClass(getClass().getName());

			if (test == getClass()) {
				recordMessage("Visible to ContextClassLoader");
				recordMessage("Using classloader " + loader);

				// Beauty - context class loader looks good
				return loader;
			} else {
				// Rats - this should not happen with a sane application server
				recordMessage(
						"Whoa - is visible to context class loader, but it gives a different class");

				// If this happens we need to investigate further, but for the time being we'll use the context class
				// loader
				return loader;
			}
		} catch (Exception ex) {
			recordMessage("Not visible to context class loader (" + loader + "):" + ex.getMessage());
			loader = getClass().getClassLoader();
			recordMessage("Using classloader " + loader);
			return loader;
		}
	}

	/**
	 * Load the properties from the given Properties object, recording the origin on those properties as being from the
	 * given location.
	 *
	 * @param properties the properties to load from
	 * @param location the location where the parameter was defined.
	 * @param overwriteOnly if true, only properties that are already defined will be loaded
	 */
	private void load(final Properties properties, final String location,
			final boolean overwriteOnly) {
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {

			String key = (String) entry.getKey();
			String already = get(key);

			if (overwriteOnly && already == null && !INCLUDE.equals(key)) {
				continue;
			}

			String value = (String) entry.getValue();
			load(key, value, location);
		}
	}

	/**
	 * Loads a single parameter into the configuration. This handles the special directives such as "include".
	 *
	 * @param key the parameter key.
	 * @param value the parameter value.
	 * @param location the location where the parameter was defined.
	 */
	private void load(final String key, final String value, final String location) {
		// Recursive bit
		if (INCLUDE.equals(key)) {
			load(parseStringArray(value));
		} else {
			backing.put(key, value);

			if ("yes".equals(value) || "true".equals(value)) {
				booleanBacking.add(key);
			} else {
				booleanBacking.remove(key);
			}

			String history = locations.get(key);

			if (history == null) {
				history = location;
			} else {
				history = location + "; " + history;
			}

			locations.put(key, history);
		}
	}

	/**
	 * Loads the configuration from a set of files.
	 *
	 * @param subFiles the files to load from.
	 */
	private void load(final String[] subFiles) {
		for (int i = 0; i < subFiles.length; i++) {
			load(subFiles[i]);
		}
	}

	/**
	 * Logs an exception.
	 *
	 * @param throwable the exception to log.
	 */
	private void recordException(final Throwable throwable) {
		throwable.printStackTrace();
	}

	/**
	 * Records a message in the internal log buffer.
	 *
	 * @param msg the message log.
	 */
	private void recordMessage(final String msg) {
		messages.append(msg).append('\n');
	}

	/**
	 * @return the set of logged messages.
	 */
	private String getMessages() {
		return messages.toString();
	}

	/**
	 * Clears the logged message buffer.
	 */
	private void clearMessages() {
		messages.setLength(0);
	}

	/**
	 * Returns a sub-set of the parameters contained in this configuration.
	 *
	 * @param prefix the prefix of the parameter keys which should be included.
	 * @param truncate if true, the prefix is truncated in the returned properties.
	 * @return the properties sub-set, may be empty.
	 */
	public Properties getSubProperties(final String prefix, final boolean truncate) {
		String cacheKey = truncate + prefix;
		Properties sub = subcontextCache.get(cacheKey);

		if (sub != null) {
			// make a copy so users can't change.
			Properties copy = new Properties();
			copy.putAll(sub);
			return copy;
		}

		sub = new Properties();

		int length = prefix.length();

		for (Map.Entry<String, Object> entry : backing.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith(prefix)) {
				// If we are truncating, remove the prefix
				String newKey = key;

				if (truncate) {
					newKey = key.substring(length);
				}

				sub.setProperty(newKey, (String) entry.getValue());
			}
		}

		subcontextCache.put(cacheKey, sub);

		// Make a copy so users can't change.
		Properties copy = new Properties();
		copy.putAll(sub);

		return copy;
	}

	/**
	 * Iterates through the values, looking for values containing ${...} strings. For those that do, we substitute if
	 * the stuff in the {...} is a defined key.
	 *
	 * @return true if any substitutions were made, false otherwise.
	 */
	private boolean substitute() {
		boolean madeChange = false;

		for (String key : backing.keySet()) {
			madeChange = madeChange || substitute(key);
		}

		return madeChange;
	}

	/**
	 * Performs value substitution for the given key. For values containing ${...} strings, we substitute if the stuff
	 * in the {...} is a defined key.
	 *
	 * @param aKey the key to run the substitution for.
	 * @return true if a substitutions was made, false otherwise.
	 */
	private boolean substitute(final String aKey) {
		boolean madeChange = false;

		if (substituting.contains(aKey)) {
			backing.put(aKey, "");
			booleanBacking.remove(aKey);
			recordMessage("WARNING: Recursive substitution detected on parameter " + aKey);
			String history = locations.get(aKey);
			locations.put(aKey, history + "recursion detected, using null value; " + history);
			return true;
		}

		try {
			substituting.add(aKey);

			String value = (String) backing.get(aKey);

			int start = findStartVariable(value);

			if (start == -1) {
				return madeChange;
			}

			int end = value.indexOf('}', start);

			if (end == -1) {
				return madeChange;
			}

			String variableName = value.substring(start + 2, end);

			madeChange = madeChange || substitute(variableName);

			String variableValue = get(variableName);

			if (variableValue == null) {
				return madeChange;
			}

			String newValue = value.substring(0, start) + variableValue + value.substring(end + 1);

			madeChange = true;

			backing.put(aKey, newValue);

			if ("yes".equals(newValue) || "true".equals(newValue)) {
				booleanBacking.add(aKey);
			} else {
				booleanBacking.remove(aKey);
			}

			// Record this substitution in the history
			String history = locations.get(aKey);
			history = "substitution of ${" + variableName + "}; " + history;
			locations.put(aKey, history);

			return madeChange;
		} finally {
			substituting.remove(aKey);
		}
	}

	/**
	 * Finds the start of a variable name in the given string. Variables use the "${<i>variableName</i>}" notation.
	 *
	 * @param aKey the key to search.
	 * @return the index of the start of a variable name in the given string, or -1 if not found.
	 */
	private int findStartVariable(final String aKey) {
		if (aKey == null) {
			return -1;
		}

		// Look for the first occurence of ${ in the parameter.
		int index = aKey.indexOf('$');

		for (; index >= 0; index = aKey.indexOf('$', index + 1)) {
			if (index == aKey.length() - 1) {
				continue;
			}

			if (aKey.charAt(index + 1) != '{') {
				continue;
			}

			// Ahh - got it
			break; // NOPMD
		}

		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh() {
		synchronized (lockObject) {
			// Now reset this object back to its initial state.
			initialiseInstanceVariables();

			// Load all the parameters from scratch.
			load();

			// Finally, notify all the listeners that have registered with this object that a change in properties has
			// occurred.
			Config.notifyListeners();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOrModifyProperty(final String name, final String value) {
		if (name == null) {
			throw new SystemException("name parameter can not be null.");
		}

		if (name.length() < 1) {
			throw new SystemException("name parameter can not be the empty String.");
		}

		if (value == null) {
			throw new SystemException("value parameter can not be null.");
		}

		recordMessage(
				"modifyProperties() - Adding property '" + name + "' with the value '" + value + "'.");

		runtimeProperties.setProperty(name, value);

		// clear the subContext cache, it's now invalid
		subcontextCache.clear();
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Helper classes
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * A helper class for properties which are being loaded.
	 */
	class IncludeProperties extends Properties {

		/**
		 * The properties file location (if applicable).
		 */
		private final String location;

		/**
		 * Creates an IncludeProperties, which has not being sourced externally.
		 */
		IncludeProperties() {
			this("Modified at Runtime");
		}

		/**
		 * Creates an IncludeProperties, which will be sourced from the given location.
		 *
		 * @param aLocation the location of the external properties.
		 */
		IncludeProperties(final String aLocation) {
			location = aLocation;
		}

		/**
		 * Adds a value to the properties set. This has been overridden to support the Configuration extensions (e.g.
		 * the "include" directive).
		 *
		 * @param aKey the key to add
		 * @param aValue the value to add
		 * @return the old value for the key, or null if there was no previously associated value.
		 */
		@Override
		public Object put(final Object aKey, final Object aValue) {
			String key = (String) aKey;
			String value = (String) aValue;

			// Act on "include" directives immediately
			if (INCLUDE.equals(key)) {
				DefaultInternalConfiguration.this.load(parseStringArray(value));
				return value;
			} else {
				// Check for a trailing "+" sign on the key (or a leading "+= on the value")
				boolean append = false;

				if (key.endsWith("+")) {
					key = key.substring(0, key.length() - 1);
					append = true;
				} else if (value != null && value.startsWith("+=")) {
					// If the line contained "key += value" then the Properties will have parsed this as 'key'
					// and '+= value'
					value = value.substring(2).trim();
					append = true;
				}

				if (append) {
					String already = DefaultInternalConfiguration.this.get(key);

					// If there is no value already, strip off the leading comma, otherwise append.
					value = (already != null ? already + "," + value : value);
				}

				DefaultInternalConfiguration.this.load(key, value, location);

				return super.put(key, value);
			}
		}
	}

	/**
	 * The parameters implementation can not depend on a logging framework to log errors, as it is typically used to
	 * configure logging.
	 *
	 * @param message the message to log.
	 */
	private static void log(final String message) {
		System.out.println(message);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// The rest of this class is the implementation of Configuration interface
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInt(final String key, final int defolt) {
		try {
			String value = get(key);

			if (value == null) {
				return defolt;
			}

			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInt(final String key) {
		return getInt(key, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getShort(final String key) {
		return getShort(key, (short) 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getShort(final String key, final short defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Short.parseShort(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Short getShort(final String key, final Short defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Short.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProperty(final String key, final Object value) {
		if (containsKey(key)) {
			String newValue = get(key) + ',' + (value == null ? "" : value);
			addOrModifyProperty(key, newValue);
		} else {
			addOrModifyProperty(key, value == null ? null : value.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		backing.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearProperty(final String key) {
		backing.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(final String key) {
		return backing.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getBigDecimal(final String key) {
		return getBigDecimal(key, new BigDecimal("0.0"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return new BigDecimal(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigInteger getBigInteger(final String key) {
		return getBigInteger(key, BigInteger.ZERO);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return new BigInteger(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBoolean(final String key) {
		return booleanBacking.contains(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBoolean(final String key, final boolean defaultValue) {
		return containsKey(key) ? getBoolean(key) : defaultValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getBoolean(final String key, final Boolean defaultValue) {
		return containsKey(key) ? Boolean.valueOf(getBoolean(key)) : defaultValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getByte(final String key) {
		return getByte(key, (byte) 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getByte(final String key, final byte defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Byte.parseByte(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte getByte(final String key, final Byte defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Byte.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDouble(final String key) {
		return getDouble(key, 0.0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDouble(final String key, final double defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getDouble(final String key, final Double defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Double.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloat(final String key) {
		return getFloat(key, 0.0f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloat(final String key, final float defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Float.parseFloat(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float getFloat(final String key, final Float defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Float.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getInteger(final String key, final Integer defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<String> getKeys() {
		return backing.keySet().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<String> getKeys(final String prefix) {
		Set<String> keys = new HashSet<>();

		for (String key : backing.keySet()) {
			if (key.startsWith(prefix)) {
				keys.add(key);
			}
		}

		return keys.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List getList(final String key) {
		return getList(key, new ArrayList(1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List getList(final String key, final List defaultValue) {
		if (containsKey(key)) {
			return Arrays.asList(getStringArray(key));
		} else {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLong(final String key) {
		return getLong(key, 0L);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLong(final String key, final long defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getLong(final String key, final Long defaultValue) {
		try {
			String value = get(key);

			if (value == null) {
				return defaultValue;
			}

			return Long.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ConversionException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Properties getProperties(final String key) {
		String[] keyValuePairs = getStringArray(key);

		Properties props = new Properties();

		for (String pair : keyValuePairs) {
			int index = pair.indexOf('=');

			if (index < 1) {
				throw new IllegalArgumentException("Malformed property: " + pair);
			}

			props.put(pair.substring(0, index), pair.substring(index + 1, pair.length()));
		}

		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getProperty(final String key) {
		return get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(final String key) {
		return get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(final String key, final String defaultValue) {
		return get(key, defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getStringArray(final String key) {
		String list = get(key);

		if (list == null) {
			return new String[0];
		}

		return parseStringArray(list);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperty(final String key, final Object value) {
		addOrModifyProperty(key, value == null ? null : value.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Configuration subset(final String prefix) {
		return new MapConfiguration(getSubProperties(prefix, false));
	}
}

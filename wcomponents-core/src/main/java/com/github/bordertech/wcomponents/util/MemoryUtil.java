package com.github.bordertech.wcomponents.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is intended to help detect and trace possible memory leaks.
 *
 * @author Rick Brown
 */
public final class MemoryUtil {

	/**
	 * For convenience this can be used as a "rule of thumb" when testing the size of a List of components in the UI.
	 */
	public static final int WARN_THRESHOLD;

	static {
		WARN_THRESHOLD = ConfigurationProperties.getMemoryUtilWarnThreshold();
	}

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(MemoryUtil.class);

	/**
	 * Utility class needs no constructor.
	 */
	private MemoryUtil() {

	}

	/**
	 * Performs default check on the size of a "registry" of UI widgets (or similar).
	 * In practice "size" is likely to be the size of a java Collection but it could be anything you want.
	 *
	 * This is a convenience which should be adequate for the majority of cases.
	 *
	 * @param size The current size of a component registry (or similar).
	 * @param className The name of the class which holds the registry.
	 */
	public static void checkSize(final int size, final String className) {
		if (MemoryUtil.WARN_THRESHOLD < size) {
			MemoryUtil.log(className + " may be leaking memory, it contains a large number of items: " + size);
		}
	}

	/**
	 * Log a possible memory leak as necessary.
	 * The benefit of using this to log possible memory leaks is that there is one global logger to enable/disable
	 * as debugging dictates.
	 *
	 * This method also creates a stack trace so that possible memory leaks are easier to track.
	 *
	 * @param message A meaningful message that provides information about the possible memory leak.
	 * The message should ideally provide information about the source of the leak.
	 */
	public static void log(final String message) {
		if (LOG.isWarnEnabled()) {
			try {
				throw new PossibleMemLeakException(message);
			} catch (PossibleMemLeakException ex) {
				LOG.warn(message, ex);
			}
		}
	}

	/**
	 * An exception that is used to generate a stack trace to aid tracing the original cause of the possible memory leak.
	 */
	private static final class PossibleMemLeakException extends RuntimeException {
		/**
		 * Creates an exception that indicates there is possibly (probably) a memory leak.
		 * @param message A meaningful message that provides information about the possible leak.
		 */
		private PossibleMemLeakException(final String message) {
			super(message);
		}
	}
}

package com.github.bordertech.wcomponents.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is intended to help detect and trace possible memory leaks.
 * The goals are:
 * - "Make some noise" when lists of UI components continue to grow beyond a reasonable level.
 * - Use a common logger so that these messages can be easily silenced for all classes.
 * - Provide a stack trace so that possible memory leaks are easier to track.
 *
 * @author Rick Brown
 */
public final class MemoryUtil {

	/**
	 * For convenience this can be used as a "rule of thumb" when testing the size of a List of components in the UI.
	 */
	public static final short WARN_THRESHOLD = 1000;

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
	 * Performs default check and default log message.
	 * This is a convenience which should be adequate for the majority of cases.
	 * @param count The current count of a component registry (or similar).
	 * @param className The name of the class which holds the registry.
	 */
	public static void checkAndLog(final int count, final String className) {
		if (MemoryUtil.WARN_THRESHOLD < count) {
			MemoryUtil.log(className + " may be leaking memory, it contains a large number of items: " + count);
		}
	}

	/**
	 * Log a possible memory leak as necessary.
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

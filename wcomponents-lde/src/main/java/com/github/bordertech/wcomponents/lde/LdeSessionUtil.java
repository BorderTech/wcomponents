package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StreamUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LdeSessionUtil provides session utility methods for the LDE.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class LdeSessionUtil {

	/**
	 * The key used to look up the LDE session persistance flag in the {@link Config WComponent Configuration}.
	 */
	private static final String LDE_PERSIST_SESSION_PARAM
			= "bordertech.wcomponents.lde.session.persist";

	/**
	 * The key used to look up the LDE persistant session load flag in the {@link Config WComponent Configuration}.
	 */
	private static final String LDE_LOAD_PERSISTANT_SESSION_PARAM
			= "bordertech.wcomponents.lde.session.loadPersisted";

	/**
	 * The serialized session file name.
	 */
	private static final String SERIALIZE_SESSION_NAME = "lde_session.dat";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(LdeSessionUtil.class);

	/**
	 * Prevent instantiation of this class.
	 */
	private LdeSessionUtil() {
	}

	/**
	 * Attempts to deserialize the persisted session attributes into the given session.
	 *
	 * @param session the session to restore the attributes to.
	 */
	public static void deserializeSessionAttributes(final HttpSession session) {
		File file = new File(SERIALIZE_SESSION_NAME);
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		if (file.canRead()) {
			try {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);

				List data = (List) ois.readObject();

				for (Iterator i = data.iterator(); i.hasNext();) {
					String key = (String) i.next();
					Object value = i.next();
					session.setAttribute(key, value);
				}
			} catch (Exception e) {
				LOG.error("Failed to read serialized session from " + file, e);
			} finally {
				if (ois != null) {
					StreamUtil.safeClose(ois);
				} else {
					StreamUtil.safeClose(fis);
				}
			}
		} else {
			LOG.warn("Unable to read serialized session from " + file);
		}
	}

	/**
	 * Serializes the session attributes of the given session.
	 *
	 * @param session the session to persist.
	 */
	public static synchronized void serializeSessionAttributes(final HttpSession session) {
		if (session != null) {
			File file = new File(SERIALIZE_SESSION_NAME);

			if (!file.exists() || file.canWrite()) {
				// Retrieve the session attributes
				List data = new ArrayList();

				for (Enumeration keyEnum = session.getAttributeNames();
						keyEnum.hasMoreElements();) {
					String attributeName = (String) keyEnum.nextElement();
					Object attributeValue = session.getAttribute(attributeName);

					if (attributeValue instanceof Serializable) {
						data.add(attributeName);
						data.add(attributeValue);
					} else {
						LOG.error(
								"Skipping attribute \"" + attributeName
								+ "\" as it is not Serializable");
					}
				}

				// Write them to the file
				FileOutputStream fos = null;
				ObjectOutputStream oos = null;

				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);

					oos.writeObject(data);
				} catch (Exception e) {
					LOG.error("Failed to write serialized session to " + file, e);
				} finally {
					if (oos != null) {
						StreamUtil.safeClose(oos);
					} else {
						StreamUtil.safeClose(fos);
					}
				}
			} else {
				LOG.warn("Unable to write serialized session to " + file);
			}
		}
	}

	/**
	 * Indicates whether new sessions should be populated from a persisted session.
	 *
	 * @return true if new sessions should be loaded from a persisted session.
	 */
	public static boolean isLoadPersistedSessionEnabled() {
		return Config.getInstance().getBoolean(LDE_LOAD_PERSISTANT_SESSION_PARAM, false);
	}

	/**
	 * Indicates whether sessions should be persisted.
	 *
	 * @return true if sessions should be persisted.
	 */
	public static boolean isPersistSessionEnabled() {
		return Config.getInstance().getBoolean(LDE_PERSIST_SESSION_PARAM, false);
	}
}

package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.Enumerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * <p>
 * This HttpServletRequestWrapper implementation changes the way in which sessions are managed, to allow multiple
 * "sub-sessions" per HTTP session.
 * </p>
 * <p>
 * The standard (cookie) mechanism is used to store the normal HTTP session, and an additional request parameter
 * ("ssid") is used to determine which sub-session to use. Using a wrapper approach limits the impact to the WServlet
 * class only, and doesn't require deployment of additional servlet filters.
 * </p>
 * <p>
 * Invalidated sub-sessions have their data cleared out to minimise session use, but are not removed from the backing
 * HTTP session.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class SubSessionHttpServletRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * This key is used to store subsessions in the backing HTTP session's attribute map.
	 */
	private static final String SESSION_MAP_KEY = SubSessionHttpServletRequestWrapper.class.
			getName() + ".subsessions";

	/**
	 * The requested session id.
	 */
	private final int sessionId;

	/**
	 * Creates a SubSessionHttpServletRequestWrapper.
	 *
	 * @param backing the backing request.
	 */
	public SubSessionHttpServletRequestWrapper(final HttpServletRequest backing) {
		super(backing);

		HttpSession backingSession = backing.getSession();

		synchronized (backingSession) {
			Map<Integer, HttpSubSession> subsessions = (Map<Integer, HttpSubSession>) backingSession
					.getAttribute(SESSION_MAP_KEY);

			if (subsessions == null) {
				subsessions = new HashMap<>();
				backingSession.setAttribute(SESSION_MAP_KEY, subsessions);
			}

			int ssid = 0;

			try {
				String param = getParameter("ssid");

				if (param == null) {
					ssid = subsessions.size();
				} else {
					ssid = Integer.parseInt(param);

					if (ssid < 0 || ssid >= subsessions.size()) {
						ssid = 0;
					}
				}

				if (!subsessions.containsKey(ssid)) {
					HttpSubSession subsession = new HttpSubSession(backingSession, ssid);
					subsessions.put(ssid, subsession);
				}
			} catch (NumberFormatException e) {
				// Someone's been fiddling with HTTP parameters,
				// ignore it and use the default session
			}

			this.sessionId = ssid;
		}
	}

	/**
	 * Retrieves the subsession for this request. If there is no existing subsession, a new one is created.
	 *
	 * @return the subsession for this request.
	 */
	private synchronized HttpSubSession getSubSession() {
		HttpSession backingSession = super.getSession();
		Map<Integer, HttpSubSession> subsessions = (Map<Integer, HttpSubSession>) backingSession
				.getAttribute(SESSION_MAP_KEY);

		HttpSubSession subsession = subsessions.get(sessionId);
		subsession.setLastAccessedTime(System.currentTimeMillis());

		return subsession;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpSession getSession() {
		return getSubSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpSession getSession(final boolean create) {
		return getSubSession();
	}

	/**
	 * @return the session id
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * A "sub-session" implementation of a HTTPSession.
	 */
	public static final class HttpSubSession implements HttpSession, Serializable {

		/**
		 * The map which stores the session attributes.
		 */
		private Map<String, Object> attributes = new HashMap<>();

		/**
		 * The maximum interval before a sub-session can be invalidated due to inactivity, specified in milliseconds.
		 */
		private int maxInactiveInterval;

		/**
		 * The timestamp when the sub-session was created.
		 */
		private final long creationTime;

		/**
		 * The timestamp when the sub-session was last accessed by the user.
		 */
		private long lastAccessedTime;

		/**
		 * The subsession id, unique per user HTTP session.
		 */
		private final int sessionId;

		/**
		 * A flag indicating whether this sub-session has been invalidated.
		 */
		private boolean invalid = false;

		/**
		 * The backing HTTP session.
		 */
		private final HttpSession backing;

		/**
		 * Creates an HttpSubSession.
		 *
		 * @param backing the backing HTTP session.
		 * @param subsessionId the subsession's id.
		 */
		public HttpSubSession(final HttpSession backing, final int subsessionId) {
			maxInactiveInterval = backing.getMaxInactiveInterval();
			creationTime = System.currentTimeMillis();
			lastAccessedTime = creationTime;
			this.sessionId = subsessionId;
			this.backing = backing;
		}

		/**
		 * @return Returns the sessionId.
		 */
		public int getSessionId() {
			return sessionId;
		}

		/**
		 * Returns the value of the attribute with the specified name.
		 *
		 * @param name the attribute name.
		 * @return the attribute value, or null if the attribute does not exist.
		 */
		@Override
		public Object getAttribute(final String name) {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return attributes.get(name);
		}

		/**
		 * @return an enumeration of the attribute names.
		 */
		@Override
		public Enumeration<String> getAttributeNames() {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return getKeys(attributes);
		}

		/**
		 * Returns the session creation timestamp.
		 *
		 * @return 0, as this is not implemented.
		 */
		@Override
		public long getCreationTime() {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return creationTime;
		}

		/**
		 * Returns the session id.
		 *
		 * @return the backing session id.
		 */
		@Override
		public String getId() {
			return backing.getId();
		}

		/**
		 * Returns the session last accessed timestamp.
		 *
		 * @return 0, as this is not implemented.
		 */
		@Override
		public long getLastAccessedTime() {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return lastAccessedTime;
		}

		/**
		 * @return the session maximum inactive interval.
		 */
		@Override
		public int getMaxInactiveInterval() {
			return maxInactiveInterval;
		}

		/**
		 * Returns the session servlet context.
		 *
		 * @return the session servlet context..
		 */
		@Override
		public ServletContext getServletContext() {
			return backing.getServletContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@Deprecated
		public HttpSessionContext getSessionContext() {
			return backing.getSessionContext();
		}

		/**
		 * @param name ignored.
		 * @return null, as this is not implemented.
		 */
		@Override
		public Object getValue(final String name) {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return getAttribute(name);
		}

		/**
		 * @return null, as this is not implemented.
		 */
		@Override
		public String[] getValueNames() {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			List<String> names = new ArrayList<>();

			for (Enumeration<String> attributeNames = getAttributeNames(); attributeNames.
					hasMoreElements();) {
				names.add(attributeNames.nextElement());
			}

			return names.toArray(new String[names.size()]);
		}

		/**
		 * No effect.
		 */
		@Override
		public void invalidate() {
			if (invalid) {
				throw new IllegalStateException("Session has already been invalidated");
			}

			invalid = true;
			attributes = null;
		}

		/**
		 * Returns whether the session is new.
		 *
		 * @return false, as this is not implemented.
		 */
		@Override
		public boolean isNew() {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			return lastAccessedTime == creationTime;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@Deprecated
		public void putValue(final String name, final Object value) {
			setAttribute(name, value);
		}

		/**
		 * Removes the specified attribute.
		 *
		 * @param name the attribute name.
		 */
		@Override
		public void removeAttribute(final String name) {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			attributes.remove(name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@Deprecated
		public void removeValue(final String name) {
			removeAttribute(name);
		}

		/**
		 * Sets the specified attribute.
		 *
		 * @param name the attribute name.
		 * @param value the attribute value.
		 */
		@Override
		public void setAttribute(final String name, final Object value) {
			if (invalid) {
				throw new IllegalStateException("Session has been invalidated");
			}

			attributes.put(name, value);
		}

		/**
		 * Sets the maximum inactive interval.
		 *
		 * @param maxInactiveInterval the maximum inactive interval.
		 */
		@Override
		public void setMaxInactiveInterval(final int maxInactiveInterval) {
			this.maxInactiveInterval = maxInactiveInterval;
		}

		/**
		 * Returns an enumeration of the keys in the specified map.
		 *
		 * @param map the map to enumerate.
		 * @param <K> the key type
		 * @param <V> the value type
		 * @return an enumeration of the map keys.
		 */
		private <K, V> Enumeration<K> getKeys(final Map<K, V> map) {
			return new Enumerator<>(map.keySet().iterator());
		}

		/**
		 * @param lastAccessedTime The lastAccessedTime to set.
		 */
		public void setLastAccessedTime(final long lastAccessedTime) {
			this.lastAccessedTime = lastAccessedTime;
		}
	}
}

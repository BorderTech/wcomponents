package com.github.openborders.util.mock.servlet; 

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.github.openborders.util.Enumerator;

/** 
 * A mock HttpSsession, useful for unit testing.
 * 
 * Extracted from PortalBridge_Test
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockHttpSession implements HttpSession
{
    private final Map attributes = new HashMap();
    private int maxInactiveInterval;
    private boolean invalidated;

    /**
     * Returns the value of the attribute with the specified name.
     * 
     * @param name the attribute name.
     * @return the attribute value, or null if the attribute does not exist. 
     */
    public Object getAttribute(final String name)
    {
        return attributes.get(name);
    }

    /**
     * @return an enumeration of the attribute names.
     */
    public Enumeration getAttributeNames()
    {
        return getKeys(attributes);
    }

    /**
     * Returns the session creation timestamp.
     * @return 0, as this is not implemented.
     */
    public long getCreationTime()
    {
        return 0;
    }

    /**
     * Returns the session id.
     * @return 0, as this is not implemented.
     */
    public String getId()
    {
        return null;
    }

    /**
     * Returns the session last accessed timestamp.
     * @return 0, as this is not implemented.
     */
    public long getLastAccessedTime()
    {
        return 0;
    }

    /**
     * @return the session maximum inactive interval.
     */
    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    /**
     * Returns the session servlet context.
     * @return null, as this is not implemented.
     */
    public ServletContext getServletContext()
    {
        return null;
    }

    /** 
     * @deprecated 
     * @return null, as this is not implemented.
     */
    @Deprecated
    public javax.servlet.http.HttpSessionContext getSessionContext()
    {
        return null;
    }

    /**
     * @param arg0 ignored.
     * @return null, as this is not implemented.
     */
    public Object getValue(final String arg0)
    {
        return null;
    }

    /**
     * @return null, as this is not implemented.
     */
    public String[] getValueNames()
    {
        return null;
    }

    /**
     * Invalidates the session.
     */
    public void invalidate()
    {
        invalidated = true;
    }

    /**
     * @return true if {@link #invalidate()} has been called.
     */
    public boolean isInvalidated()
    {
        return invalidated;
    }

    /**
     * Returns whether the session is new.
     * @return false, as this is not implemented.
     */
    public boolean isNew()
    {
        return false;
    }

    /**
     * No effect.
     * @param arg0 ignored.
     * @param arg1 ignored.
     */
    public void putValue(final String arg0, final Object arg1)
    {
        // No effect
    }

    /**
     * Removes the specified attribute.
     * @param name the attribute name.
     */
    public void removeAttribute(final String name)
    {
        attributes.remove(name);
    }


    /**
     * No effect.
     * @param arg0 ignored.
     */
    public void removeValue(final String arg0)
    {
        // No effect
    }

    /**
     * Sets the specified attribute.
     * 
     * @param name the attribute name.
     * @param value the attribute value.
     */
    public void setAttribute(final String name, final Object value)
    {
        attributes.put(name, value);
    }

    /**
     * Sets the maximum inactive interval.
     * 
     * @param maxInactiveInterval the maximum inactive interval.
     */
    public void setMaxInactiveInterval(final int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /** @return the attributes map. */
    public Map getAttributes()
    {
        return attributes;
    }

    /**
     * Returns an enumeration of the keys in the specified map.
     * 
     * @param map the map to enumerate.
     * @return an enumeration of the map keys.
     */
    private Enumeration getKeys(final Map map)
    {
        return new Enumerator(map.keySet().iterator());
    }
}

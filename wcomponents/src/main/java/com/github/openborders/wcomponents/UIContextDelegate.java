package com.github.openborders.wcomponents;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UIContextDelegate implements UIContext, but delegates all the work to a backing 
 * context. This allows us to easily write UIContext extensions that only need to 
 * change the behaviour of a few methods.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class UIContextDelegate implements UIContext
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(UIContextDelegate.class);

    /** The backing context. */
    private UIContext backing;

    /**
     * Creates a UIContextDelegate. This is marked protected,
     * as it doesn't make sense to create an instance of the
     * delegate without changing behaviour. 
     * 
     * @param backing the backing context.
     */
    protected UIContextDelegate(final UIContext backing)
    {
        this.backing = backing;
    }
    
    /**
     * Sets the backing context.
     * @param backing the backing context.
     */
    protected void setBacking(final UIContext backing)
    {
        this.backing = backing;
    }

    /** {@inheritDoc}  */
    public void clearScratchMap()
    {
        backing.clearScratchMap();
    }

    /** {@inheritDoc}  */
    public void clearScratchMap(final WComponent component)
    {
        backing.clearScratchMap(component);
    }

    /** {@inheritDoc}  */
    public void doInvokeLaters()
    {
        backing.doInvokeLaters();
    }

    /** {@inheritDoc}  */
    public Set getComponents()
    {
        return backing.getComponents();
    }

    /** {@inheritDoc}  */
    public long getCreationTime()
    {
        return backing.getCreationTime();
    }

    /** {@inheritDoc}  */
    public Environment getEnvironment()
    {
        return backing.getEnvironment();
    }

    /** {@inheritDoc}  */
    public WComponent getFocussed()
    {
        return backing.getFocussed();
    }

    /** {@inheritDoc}  */
    public String getFocussedId()
    {
        return backing.getFocussedId();
    }

    /** {@inheritDoc}  */
    public Object getFwkAttribute(final String name)
    {
        return backing.getFwkAttribute(name);
    }

    /** {@inheritDoc}  */
    public Set getFwkAttributeNames()
    {
        return backing.getFwkAttributeNames();
    }

    /** {@inheritDoc}  */
    public Headers getHeaders()
    {
        return backing.getHeaders();
    }

    /** {@inheritDoc}  */
    public WebModel getModel(final WebComponent component)
    {
        return backing.getModel(component);
    }

    /** {@inheritDoc}  */
    public Map getScratchMap(final WComponent component)
    {
        return backing.getScratchMap(component);
    }

    /** {@inheritDoc}  */
    public WComponent getUI()
    {
        return backing.getUI();
    }

    /** {@inheritDoc}  */
    public void invokeLater(final Runnable runnable)
    {
        backing.invokeLater(runnable);
    }

    /** {@inheritDoc}  */
    public void invokeLater(final UIContext uic, final Runnable runnable)
    {
        backing.invokeLater(uic, runnable);
    }

    /** {@inheritDoc}  */
    public boolean isDummyEnvironment()
    {
        return backing.isDummyEnvironment();
    }

    /** {@inheritDoc}  */
    public boolean isFocusRequired()
    {
        return backing.isFocusRequired();
    }

    /** {@inheritDoc}  */
    public void removeFwkAttribute(final String name)
    {
        backing.removeFwkAttribute(name);
    }

    /** {@inheritDoc}  */
    public void removeModel(final WebComponent component)
    {
        backing.removeModel(component);
    }

    /** {@inheritDoc}  */
    public void setEnvironment(final Environment environment)
    {
        backing.setEnvironment(environment);
    }

    /** {@inheritDoc}  */
    public void setFocusRequired(final boolean b)
    {
        backing.setFocusRequired(b);
    }

    /** {@inheritDoc}  */
    public void setFocussed(final WComponent component, final UIContext uic)
    {
        backing.setFocussed(component, uic);
    }

    /** {@inheritDoc}  */
    public void setFocussed(final WComponent component)
    {
        backing.setFocussed(component);
    }

    /** {@inheritDoc}  */
    public void setFwkAttribute(final String name, final Object value)
    {
        backing.setFwkAttribute(name, value);
    }

    /** {@inheritDoc}  */
    public void setModel(final WebComponent component, final WebModel model)
    {
        backing.setModel(component, model);
    }

    /** {@inheritDoc}  */
    public void setUI(final WComponent ui)
    {
        backing.setUI(ui);
    }
    
    /** {@inheritDoc} */
    public Locale getLocale()
    {
        return backing.getLocale();
    }

    /** {@inheritDoc} */
    public void setLocale(final Locale locale)
    {
        backing.setLocale(locale);
    }

    /**
     * @return the backing context.
     */
    public UIContext getBacking()
    {
        return backing;
    }
    
    /**
     * A utility function to iterate to the primary (top most) 
     * context and return it.
     * 
     * @param uic the UIContext to retrieve the primary context for.
     * @return the primary context for the given context.
     */
    public static UIContext getPrimaryUIContext(final UIContext uic)
    {
        if (uic == null)
        {
            return null;
        }
        
        UIContext primary = null;
        UIContext current = uic;
        
        while (primary == null)
        {
            if (current instanceof UIContextDelegate)
            {
                UIContext backing = ((UIContextDelegate) current).getBacking();
                if (backing != null)
                {
                    current = backing;
                }
                else
                {
                    // This case should probably never happen.
                    primary = current;
                    log.warn("UIContextDelegate found without a backing context");
                }
            }
            else
            {
                primary = current;
            }
        }
        
        return primary;
    }
}

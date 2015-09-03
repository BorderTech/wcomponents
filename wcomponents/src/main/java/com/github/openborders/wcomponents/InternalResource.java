package com.github.openborders.wcomponents;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.servlet.WServlet;
import com.github.openborders.wcomponents.util.Config;
import com.github.openborders.wcomponents.util.StreamUtil;

/**
 * Provides a bridge to static resources which are present 
 * in the class path, but not in the web application itself.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class InternalResource implements ContentStreamAccess
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(InternalResource.class);
    
    /** The resource name. */
    private final String resourceName;

    /** The description of the resource. */
    private final String description;
    
    /** An empty array to use when resource retrieval fails. */
    private static final byte[] EMPTY = new byte[0];
    
    /**
     * Creates an InternalResource.
     * @param resourceName the resource name.
     * @param description the description of the resource (e.g. file name).
     */
    public InternalResource(final String resourceName, final String description)
    {
        this.resourceName = resourceName;
        this.description = description;
        InternalResourceMap.registerResource(this);
    }

    /**
     * @return the data from the resource, or an empty byte array.
     */
    public byte[] getBytes()
    {
        InputStream stream = null;
        
        try
        {
            stream = getClass().getResourceAsStream(resourceName);
            return StreamUtil.getBytes(stream);
        }
        catch (Exception e)
        {
            log.error("Failed to read resource: " + resourceName, e);
        }
        finally
        {
            StreamUtil.safeClose(stream);
        }
        
        return EMPTY;
    }
    
    /** {@inheritDoc} */
    public InputStream getStream() throws IOException
    {
        try
        {
            return getClass().getResourceAsStream(resourceName);
        }
        catch (Exception e)
        {
            log.error("Failed to read resource: " + resourceName, e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return description;
    }

    /** {@inheritDoc} */
    public String getMimeType()
    {
        int index = resourceName.indexOf('.'); 
        
        if (index != -1)
        {
            String extension = resourceName.substring(index + 1);
            String fileMimeType =  Config.getInstance().getString("wcomponent.mimeType." + extension.toLowerCase());
            
            if (fileMimeType != null)
            {
                return fileMimeType;
            }
        }
        
        return Config.getInstance().getString("wcomponent.mimeType.defaultMimeType");
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + (resourceName == null ? "(null)" : "(\"" + resourceName + "\")");
    }

    /** @return the name (path) of the resource. */
    public String getResourceName()
    {
        return resourceName;
    }
    
    /**
     * @return the URL which can be used to target this resource.
     */
    public String getTargetUrl()
    {
        UIContext uic = UIContextHolder.getCurrent();
        
        if (uic == null)
        {
            return null;
        }
        
        String url = uic.getEnvironment().getWServletPath();
        Map<String, String> parameters = new HashMap<String, String>();
        String resourceCacheKey = InternalResourceMap.getResourceCacheKey(resourceName);
        parameters.put(WServlet.STATIC_RESOURCE_PARAM_NAME, resourceName);
        
        if (resourceCacheKey != null)
        {
            parameters.put("cacheKey", resourceCacheKey);
        }
            
        return WebUtilities.getPath(url, parameters, true);
    }    
}

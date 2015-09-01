package com.github.dibp.wcomponents; 

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * MockContentStreamAccess - a ContentStreamAccess useful for unit testing.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class MockContentStreamAccess extends MockContentAccess implements ContentStreamAccess
{
    /**
     * @return a stream to the content.
     */
    public InputStream getStream()
    {
        return new ByteArrayInputStream(getBytes());
    }
}

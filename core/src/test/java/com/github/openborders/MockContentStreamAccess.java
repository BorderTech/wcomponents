package com.github.openborders; 

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.github.openborders.ContentStreamAccess;

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

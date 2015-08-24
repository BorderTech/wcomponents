package com.github.openborders.render.webxml; 

import com.github.openborders.Renderer;
import com.github.openborders.util.SystemException;

/** 
 * The layout factory for the WebXml renderer package.
 * This factory uses reflection to cut down on the amount
 * of code necessary to support every conceivable component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class RendererFactory implements com.github.openborders.RendererFactory
{
    /** The package prefix for classes which this factory can create. */
    private final String packagePrefix;
    
    /**
     * Creates the RendererFactory.
     */
    public RendererFactory()
    {
        String qualifiedClassName = getClass().getName();
        String packageName = qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf('.'));
        packagePrefix = packageName + '.';
    }

    /** {@inheritDoc} */
    public Renderer getRenderer(final Class<?> clazz)
    {
        String qualifiedClassName = clazz.getName();
        String unqualifiedClassName = qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);        
        String rendererName = packagePrefix + unqualifiedClassName + "Renderer";
        
        return createRenderer(rendererName);
    }
    
    /**
     * Attempts to create a Renderer with the given name.
     * 
     * @param rendererName the name of the Renderer
     * @return a LayoutManager of the given type, or null if the class was not found.
     */
    private Renderer createRenderer(final String rendererName)
    {
        try
        {
            Class<?> managerClass = Class.forName(rendererName);
            Object manager = managerClass.newInstance();
            
            if (!(manager instanceof Renderer))
            {
                throw new SystemException(rendererName + " is not a Renderer");
            }
            
            return (Renderer) manager;
        }
        catch (ClassNotFoundException e)
        {
            // Legal - there might not a renderer implementation for the component in this format
            return null;
        }
        catch (InstantiationException e)
        {
            throw new SystemException("Failed to instantiate " + rendererName, e);
        }
        catch (IllegalAccessException e)
        {
            throw new SystemException("Failed to access " + rendererName, e);
        }
    }
    
    /** {@inheritDoc} */
    public Renderer getTemplateRenderer()
    {
        return new VelocityRenderer();
    }
}

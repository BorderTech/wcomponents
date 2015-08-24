package com.github.openborders.render.nil;

import com.github.openborders.RenderContext;
import com.github.openborders.Renderer;
import com.github.openborders.WComponent;

/** 
 * The layout factory for the nil renderer package.
 * This factory produces a renderer which doesn't do anything,
 * and is useful for testing.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class RendererFactory implements com.github.openborders.RendererFactory
{
    /** The singleton NullRenderer instance. */
    private static final Renderer NULL_RENDERER = new NullRenderer();

    /** {@inheritDoc} */
    public Renderer getRenderer(final Class<?> clazz)
    {
        return NULL_RENDERER;
    }
    
    /** {@inheritDoc} */
    public Renderer getTemplateRenderer()
    {
        return NULL_RENDERER;
    }

    /** A no-op renderer. */
    private static final class NullRenderer implements Renderer
    {
        /**
         * Doesn't do anything.
         * @param component ignored.
         * @param renderContext ignored.
         */
        public void render(final WComponent component, final RenderContext renderContext)
        {
            // NO-OP
        }
    };
}

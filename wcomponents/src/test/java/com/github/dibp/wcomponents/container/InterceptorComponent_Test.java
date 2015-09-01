package com.github.dibp.wcomponents.container; 

import junit.framework.Assert;

import org.junit.Test;

import com.github.dibp.wcomponents.AbstractWComponentTestCase;
import com.github.dibp.wcomponents.RenderContext;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WebComponent;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.util.mock.MockRequest;

/**
 * InterceptorComponent_Test - unit tests for {@link InterceptorComponent}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class InterceptorComponent_Test extends AbstractWComponentTestCase
{
    @Test
    public void testBackingComponentAccessors()
    {
        WComponent backing = new WLabel();

        InterceptorComponent interceptor = new InterceptorComponent(backing);
        Assert.assertSame("Incorrect backing component returned", 
                   backing, interceptor.getBackingComponent());
        
        interceptor = new InterceptorComponent();
        interceptor.setBackingComponent(backing);
        Assert.assertSame("Incorrect backing component returned", 
                   backing, interceptor.getBackingComponent());
    }
    
    @Test
    public void testGetName()
    {
        WComponent backing = new WLabel();
        setActiveContext(createUIContext());

        InterceptorComponent interceptor = new InterceptorComponent(backing);
        Assert.assertEquals("Incorrect name returned", 
                     backing.getName(), interceptor.getName());
        
        interceptor = new InterceptorComponent(interceptor);
        Assert.assertEquals("Incorrect name returned for nested interceptor", 
                     backing.getName(), interceptor.getName());        
    }
    
    @Test
    public void testGetId()
    {
        WComponent backing = new WLabel();
        setActiveContext(createUIContext());

        InterceptorComponent interceptor = new InterceptorComponent(backing);
        Assert.assertEquals("Incorrect id returned", 
                     backing.getId(), interceptor.getId());
        
        interceptor = new InterceptorComponent(interceptor);
        Assert.assertEquals("Incorrect id returned for nested interceptor", 
                     backing.getId(), interceptor.getId());
    }    
    
    @Test
    public void testGetUI()
    {
        WComponent backing = new WLabel();

        InterceptorComponent interceptor = new InterceptorComponent(backing);
        
        Assert.assertSame("Incorrect backing component returned", 
                   backing, interceptor.getUI());
        
        // Test nested interceptors
        interceptor = new InterceptorComponent(interceptor);
        
        Assert.assertSame("Incorrect backing component returned", 
                   backing, interceptor.getUI());        
    }
    
    @Test(expected = IllegalStateException.class)
    public void testAttachUIBadBacking()
    {
        InterceptorComponent interceptor = new InterceptorComponent(new WebComponent()
        {
            /** {@inheritDoc} */
            public void serviceRequest(final Request request)
            {
                // NO-OP
            }
            
            /** {@inheritDoc} */
            public void preparePaint(final Request request)
            {
                // NO-OP
            }
            
            /** {@inheritDoc} */
            public void paint(final RenderContext renderContext)
            {
                // NO-OP
            }
            
            /** {@inheritDoc} */
            public String getName()
            {
                return null;
            }
            
            /** {@inheritDoc} */
            public String getId()
            {
                return null;
            }
        });
        
        // This should throw an exception, as it doesn't know how to attach the UI
        // to the anonymous WebComponent implementation. 
        interceptor.attachUI(new WLabel());
    }
    
    @Test
    public void testAttachUI()
    {
        WComponent ui = new WLabel();
        InterceptorComponent interceptor = new InterceptorComponent(ui);
        
        Assert.assertSame("Incorrect UI returned", 
                   ui, interceptor.getUI());
        
        // Test nested interceptors
        interceptor = new InterceptorComponent();
        InterceptorComponent parentInterceptor = new InterceptorComponent(interceptor);
        parentInterceptor.attachUI(ui);
        
        Assert.assertSame("Incorrect UI returned by parent interceptor", 
                   ui, parentInterceptor.getUI());        
        
        Assert.assertSame("Incorrect UI returned by child interceptor", 
                   ui, interceptor.getUI());
    }
    
    @Test
    public void testReplaceInterceptor()
    {
        WComponent ui = new WLabel();

        InterceptorComponent interceptor1 = new InterceptorComponent();
        InterceptorComponent interceptor2 = new FormInterceptor();
        InterceptorComponent interceptor3 = new HeadLineInterceptor();
        InterceptorComponent replacement = new PageShellInterceptor();
        
        // Build chain
        interceptor1.attachUI(ui);
        interceptor2.setBackingComponent(interceptor1);
        interceptor3.setBackingComponent(interceptor2);
        
        // Replace interceptor2
        InterceptorComponent newChain = InterceptorComponent.replaceInterceptor(interceptor2.getClass(), replacement, interceptor3);
        Assert.assertSame("Incorrect head of chain", interceptor3, newChain);
        Assert.assertSame("Incorrect replacement", replacement, newChain.getBackingComponent());
        Assert.assertSame("Incorrect tail of chain", interceptor1, replacement.getBackingComponent());
        Assert.assertSame("Incorrect UI", ui, newChain.getUI());
    }

    @Test
    public void testReplaceInterceptorNullChain()
    {
        InterceptorComponent newChain = InterceptorComponent.replaceInterceptor(InterceptorComponent.class, new FormInterceptor(), null);
        Assert.assertNull("New chain should be null", newChain);
    }

    @Test
    public void testRender()
    {
        WLabel ui = new WLabel("InterceptorComponent_Test.testRender");
        setActiveContext(createUIContext());
        
        String expected = WebUtilities.render(new MockRequest(), ui);
        String actual = InterceptorComponent.render(ui);
        Assert.assertEquals("Incorrect render output", expected, actual);
    }
}

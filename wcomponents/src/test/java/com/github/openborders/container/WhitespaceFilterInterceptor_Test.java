package com.github.openborders.container; 

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.openborders.AbstractWComponentTestCase;
import com.github.openborders.WLabel;
import com.github.openborders.container.WhitespaceFilterInterceptor;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.Config;

/**
 * WhitespaceFilterInterceptor_Test - unit tests for {@link WhitespaceFilterInterceptor}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class WhitespaceFilterInterceptor_Test extends AbstractWComponentTestCase
{
    @After
    public void resetConfig()
    {
        Config.reset();
    }
    
    @Test
    public void testPaint()
    {
        final String testString = "    foo    bar    ";
        final String filteredString = " foo bar ";
        
        WLabel label = new WLabel(testString);
        WhitespaceFilterInterceptor interceptor = new WhitespaceFilterInterceptor();
        interceptor.attachUI(label);
        label.setLocked(true);
        setActiveContext(createUIContext());

        // Test when disabled
        Config.getInstance().setProperty("wcomponent.whitespaceFilter.enabled", "false");        
        
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        interceptor.paint(new WebXmlRenderContext(printWriter));
        printWriter.close();
        
        Assert.assertTrue("Should not have filtered text when disabled", writer.toString().contains(testString));
        
        // Test when enabled
        Config.getInstance().setProperty("wcomponent.whitespaceFilter.enabled", "true");
        
        writer = new StringWriter();
        printWriter = new PrintWriter(writer);
        interceptor.paint(new WebXmlRenderContext(printWriter));
        printWriter.close();
        
        Assert.assertTrue("Should have filtered text when enabled", writer.toString().contains(filteredString));
    }
}

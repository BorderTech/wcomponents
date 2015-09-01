package com.github.dibp.wcomponents.render.webxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.dibp.wcomponents.DefaultWComponent;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WTextArea;
import com.github.dibp.wcomponents.validation.Diagnostic;
import com.github.dibp.wcomponents.validation.DiagnosticImpl;
import com.github.dibp.wcomponents.validation.WValidationErrors;

/**
 * Junit test case for {@link WValidationErrorsRenderer}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WValidationErrorsRenderer_Test extends AbstractWebXmlRendererTestCase
{
    /**
     * Test the Layout is correctly configured.
     */
    @Test
    public void testRendererCorrectlyConfigured()
    {
        WValidationErrors errors = new WValidationErrors();
        Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(errors) instanceof WValidationErrorsRenderer);
    }

    @Test
    public void testDoPaintBasic() throws IOException, SAXException, XpathException
    {
        WValidationErrors errors = new WValidationErrors();

        WTextArea text1 = new WTextArea();
        text1.setText("text1");

        WContainer root = new WContainer();
        root.add(errors);
        root.add(text1);
        root.setLocked(true);

        // Validate Schema with no errors
        assertSchemaMatch(root);

        // Simulate Error Message
        setActiveContext(createUIContext());
        List<Diagnostic> diags = new ArrayList<Diagnostic>();
        diags.add(new DiagnosticImpl(Diagnostic.ERROR, text1, "Test Error1"));
        root.showErrorIndicators(diags);
        errors.setErrors(diags);

        assertSchemaMatch(root);
        assertXpathEvaluatesTo(text1.getId(), "//ui:validationErrors/ui:error/@for", root);
        assertXpathEvaluatesTo("Test Error1", "//ui:validationErrors/ui:error", root);
        
        // Check for error message with no associated component
        setActiveContext(createUIContext());
        diags.clear();
        diags.add(new DiagnosticImpl(Diagnostic.ERROR, null, "Test Error1"));
        errors.setErrors(diags);
        
        assertSchemaMatch(root);
        assertXpathNotExists("//ui:validationErrors/ui:error/@for", root);
        assertXpathEvaluatesTo("Test Error1", "//ui:validationErrors/ui:error", root);
    }

    @Test
    public void testXssEscaping() throws IOException, SAXException, XpathException
    {
        WValidationErrors errors = new WValidationErrors();
        String content = getMaliciousContent();
        
        List<Diagnostic> diags = new ArrayList<Diagnostic>();
        diags.add(new DiagnosticImpl(Diagnostic.ERROR, new DefaultWComponent(), content));
        errors.setErrors(diags);

        assertSafeContent(errors);
    }
}

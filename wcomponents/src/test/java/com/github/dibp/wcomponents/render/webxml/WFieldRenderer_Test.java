package com.github.dibp.wcomponents.render.webxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.dibp.wcomponents.ComponentModel;
import com.github.dibp.wcomponents.WField;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WTextArea;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.validation.Diagnostic;
import com.github.dibp.wcomponents.validation.DiagnosticImpl;

/**
 * Junit test case for {@link WFieldRenderer}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldRenderer_Test extends AbstractWebXmlRendererTestCase
{
    /**
     * Test the Layout is correctly configured.
     */
    @Test
    public void testRendererCorrectlyConfigured()
    {
        WField field = new com.github.dibp.wcomponents.WFieldLayout().addField("test1", new WTextArea());
        Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(field) instanceof WFieldRenderer);
    }

    @Test
    public void testDoPaintBasic() throws IOException, SAXException, XpathException
    {
        WTextField text = new WTextField();
        com.github.dibp.wcomponents.WFieldLayout test = new com.github.dibp.wcomponents.WFieldLayout();
        WField field = test.addField("label1", text);
        text.setText("text1");

        // Validate Schema
        assertSchemaMatch(test);
        // Check Attributes
        assertXpathEvaluatesTo(field.getId(), "//ui:field/@id", field);
        assertXpathEvaluatesTo("", "//ui:field/@hidden", field);
        assertXpathEvaluatesTo("", "//ui:field/@inputWidth", field);

        // Check Label
        assertXpathEvaluatesTo("label1", "//ui:field/ui:label", field);
        // Check Input
        assertXpathEvaluatesTo("text1", "//ui:field/ui:input/ui:textField", field);

        // Test Hidden
        setFlag(field, ComponentModel.HIDE_FLAG, true);
        assertSchemaMatch(test);
        assertXpathEvaluatesTo("true", "//ui:field/@hidden", field);
        
        // Test Width - 1
        field.setInputWidth(1);
        assertSchemaMatch(test);
        assertXpathEvaluatesTo("1", "//ui:field/@inputWidth", field);
        
        // Test Width - 100
        field.setInputWidth(100);
        assertSchemaMatch(test);
        assertXpathEvaluatesTo("100", "//ui:field/@inputWidth", field);
    }

    @Test
    public void testNoInputField() throws IOException, SAXException, XpathException
    {
        // No Input field, so label created by WTextWithColon.
        WText text = new WText("text1");
        com.github.dibp.wcomponents.WFieldLayout test = new com.github.dibp.wcomponents.WFieldLayout();
        WField field = test.addField("label1", text);

        // Validate Schema
        assertSchemaMatch(test);
        // Check Attributes
        assertXpathEvaluatesTo(field.getId(), "//ui:field/@id", field);
        // Check Label
        assertXpathEvaluatesTo("label1", "//ui:field/ui:label", field);
        // Check Input
        assertXpathEvaluatesTo("text1", "//ui:field/ui:input", field);
    }

    @Test
    public void testWithValidationMessages() throws IOException, SAXException, XpathException
    {
        WTextField text = new WTextField();
        text.setText("text1");
        com.github.dibp.wcomponents.WFieldLayout test = new com.github.dibp.wcomponents.WFieldLayout();
        WField field = test.addField("label1", text);
        setActiveContext(createUIContext());

        // Simulate Error Message
        List<Diagnostic> diags = new ArrayList<Diagnostic>();
        diags.add(new DiagnosticImpl(Diagnostic.ERROR, text, "Test Error"));
        diags.add(new DiagnosticImpl(Diagnostic.WARNING, text, "Test Warning"));
        field.showErrorIndicators(diags);
        field.showWarningIndicators(diags);

        // Validate Schema
        assertSchemaMatch(test);
        // Check Attributes
        assertXpathEvaluatesTo(field.getId(), "//ui:field/@id", field);
        // Check Label
        assertXpathEvaluatesTo("label1", "//ui:field/ui:label", field);
        // Check Input
        assertXpathEvaluatesTo("text1", "//ui:field/ui:input/ui:textField", field);
        // Check Indicator
        assertXpathEvaluatesTo("Test Error", "//ui:field/ui:input/ui:fieldIndicator[@type='error']/ui:message", field);
        assertXpathEvaluatesTo("Test Warning", "//ui:field/ui:input/ui:fieldIndicator[@type='warn']/ui:message", field);
    }

}

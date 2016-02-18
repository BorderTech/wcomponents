package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.Duplet;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTemplate}.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class WTemplate_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WTemplate template = new WTemplate();
		Assert.assertNull("Default Const - Template name", template.getTemplateName());
		Assert.assertNull("Default Const - Engine name", template.getEngineName());
	}

	@Test
	public void testConstructor1() {
		WTemplate template = new WTemplate("test");
		Assert.assertEquals("Const 1 - Template name", "test", template.getTemplateName());
		Assert.assertNull("Const 1 - Engine name", template.getEngineName());
	}

	@Test
	public void testConstructor2() {
		WTemplate template = new WTemplate("test", TemplateRendererFactory.TemplateEngine.VELOCITY);
		Assert.assertEquals("Const 2 - Template name", "test", template.getTemplateName());
		Assert.assertEquals("Const 2 - Engine name", TemplateRendererFactory.TemplateEngine.VELOCITY.getEngineName(), template.getEngineName());
	}

	@Test
	public void testConstructor3() {
		WTemplate template = new WTemplate("test", "engine");
		Assert.assertEquals("Const 3 - Template name", "test", template.getTemplateName());
		Assert.assertEquals("Const 3 - Engine name", "engine", template.getEngineName());
	}

	@Test
	public void testTemplateNameAccessors() {
		assertAccessorsCorrect(new WTemplate(), "templateName", null, "X", "Y");
	}

	@Test
	public void testInlineTemplateAccessors() {
		assertAccessorsCorrect(new WTemplate(), "inlineTemplate", null, "X", "Y");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddComponentWithNullTag() {
		new WTemplate().addTaggedComponent(null, new WText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddComponentWithEmptyTag() {
		new WTemplate().addTaggedComponent("", new WText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullComponent() {
		new WTemplate().addTaggedComponent("tag", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddComponentAlreadyAdded() {
		WText comp = new WText();
		WTemplate template = new WTemplate();
		template.addTaggedComponent("tag", comp);
		template.addTaggedComponent("tag2", comp);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddComponentTagAlreadyAdded() {
		WTemplate template = new WTemplate();
		template.addTaggedComponent("tag", new WText());
		template.addTaggedComponent("tag", new WText());
	}

	@Test
	public void testTaggedComponentAccessors() {
		assertMapAccessorsCorrect(new WTemplate(), "taggedComponent", String.class, WComponent.class, new Duplet("T1", new WText()), new Duplet("T2", new WText()), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddParameterNullTag() {
		new WTemplate().addParameter(null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddParameterEmptyTag() {
		new WTemplate().addParameter("", "value");
	}

	@Test
	public void testParameterAccessors() {
		assertMapAccessorsCorrect(new WTemplate(), "parameter", String.class, Object.class, new Duplet("T1", "V1"), new Duplet("T2", "V2"), false);
	}

	@Test
	public void testEngineNameAccessors() {
		assertAccessorsCorrect(new WTemplate(), "engineName", null, "X", "Y");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEngineOptionNullKey() {
		new WTemplate().addEngineOption(null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddEngineOptionEmptyKey() {
		new WTemplate().addEngineOption("", "value");
	}

	@Test
	public void testEngineOptionAccessors() {
		assertMapAccessorsCorrect(new WTemplate(), "engineOption", String.class, Object.class, new Duplet("T1", "V1"), new Duplet("T2", "V2"), false);
	}

	@Test
	public void testNamingContextAccessors() {
		assertAccessorsCorrect(new WTemplate(), "namingContext", false, true, false);
	}

}

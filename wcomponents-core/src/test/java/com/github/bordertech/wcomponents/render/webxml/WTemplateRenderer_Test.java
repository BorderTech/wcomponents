package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTemplateRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class WTemplateRenderer_Test extends AbstractWebXmlRendererTestCase {

	private static final String TEST_ID = "MY_ID";
	private static final String CHILD_TEXT = "CHILD_TEXT";
	private static final String PARAMETER_TEXT = "PARAMTER_TEXT";
	private static final String FIRST_NAME = "FIRST_TEST";
	private static final String LAST_NAME = "LAST_TEST";

	@Test
	public void testRendererCorrectlyConfigured() {
		WTemplate template = new WTemplate();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(template) instanceof WTemplateRenderer);
	}

	@Test(expected = SystemException.class)
	public void testHandlebarsInvalidTemplate() {
		WTemplate template = new WTemplate("notexist.hbs", TemplateRendererFactory.TemplateEngine.HANDLEBARS);
		toXHtml(template);
	}

	@Test
	public void testHandlebarsInlineTemplate() throws IOException, SAXException, XpathException {

		StringBuffer inline = new StringBuffer();
		inline.append("id={{wc.id}}");
		inline.append("param={{mytest}}");
		inline.append("firstName={{wc.bean.firstName}}");
		inline.append("lastName={{wc.bean.lastName}}");
		inline.append("child={{child1}}");
		inline.append("not1=[{{notexist}}]");
		inline.append("not2=[{{wc.notexist}}]");

		WTemplate template = new WTemplate();
		setupTemplate(template);
		template.setEngineName(TemplateRendererFactory.TemplateEngine.HANDLEBARS);
		template.setInlineTemplate(inline.toString());

		String output = toXHtml(template);
		assertOutput(output);
	}

	@Test
	public void testHandlebarsTemplate() throws IOException, SAXException, XpathException {
		WTemplate template = new WTemplate("templates/Test_Handlebars.hbs", TemplateRendererFactory.TemplateEngine.HANDLEBARS);
		setupTemplate(template);
		String output = toXHtml(template);
		assertOutput(output);
	}

	@Test(expected = SystemException.class)
	public void testVelocityInvalidTemplate() {
		WTemplate template = new WTemplate("notexist.vm", TemplateRendererFactory.TemplateEngine.VELOCITY);
		toXHtml(template);
	}

	@Test
	public void testVelocityTemplate() throws IOException, SAXException, XpathException {
		WTemplate template = new WTemplate("templates/Test_Velocity.vm", TemplateRendererFactory.TemplateEngine.VELOCITY);
		setupTemplate(template);
		String output = toXHtml(template);
		assertOutput(output);
	}

	@Test
	public void testVelocityInlineTemplate() throws IOException, SAXException, XpathException {

		StringBuffer inline = new StringBuffer();
		inline.append("id=${wc.id}");
		inline.append("param=${mytest}");
		inline.append("firstName=${wc.bean.firstName}");
		inline.append("lastName=${wc.bean.lastName}");
		inline.append("child=${child1}");
		inline.append("not1=[$!{notexist}]");
		inline.append("not2=[$!{wc.notexist}]");

		WTemplate template = new WTemplate();
		setupTemplate(template);
		template.setEngineName(TemplateRendererFactory.TemplateEngine.VELOCITY);
		template.setInlineTemplate(inline.toString());

		String output = toXHtml(template);
		assertOutput(output);
	}

	@Test(expected = SystemException.class)
	public void testPlainTextInvalidTemplate() {
		WTemplate template = new WTemplate("notexist.txt", TemplateRendererFactory.TemplateEngine.PLAINTEXT);
		toXHtml(template);
	}

	@Test
	public void testPlainTextTemplate() throws IOException, SAXException, XpathException {
		WTemplate template = new WTemplate("templates/Test_Plain.txt", TemplateRendererFactory.TemplateEngine.PLAINTEXT);
		setupTemplate(template);
		String output = toXHtml(template);
		String text = "Hello from plain text.";
		Assert.assertTrue("Text not in output", output.contains(text));
	}

	@Test
	public void testPlainTextInlineTemplate() throws IOException, SAXException, XpathException {

		String inline = "Hello from plain text.";

		WTemplate template = new WTemplate();
		setupTemplate(template);
		template.setEngineName(TemplateRendererFactory.TemplateEngine.PLAINTEXT);
		template.setInlineTemplate(inline);

		String output = toXHtml(template);
		Assert.assertTrue("Text not in output", output.contains(inline));
	}

	/**
	 * @param template the template to setup
	 */
	private void setupTemplate(final WTemplate template) {
		template.setIdName(TEST_ID);
		template.addTaggedComponent("child1", new WText(CHILD_TEXT));
		template.addParameter("mytest", PARAMETER_TEXT);
		template.setBean(new TestBean(FIRST_NAME, LAST_NAME));
	}

	/**
	 * @param output the output to assert
	 */
	private void assertOutput(final String output) {
		Assert.assertNotNull("Should have output", output);
		Assert.assertTrue("Id not in output", output.contains("id=" + TEST_ID));
		Assert.assertTrue("Parameter not in output", output.contains("param=" + PARAMETER_TEXT));
		Assert.assertTrue("First name from bean not in output", output.contains("firstName=" + FIRST_NAME));
		Assert.assertTrue("Last name from bean not in output", output.contains("lastName=" + LAST_NAME));
		Assert.assertTrue("Child component not in output", output.contains("child=" + CHILD_TEXT));
		Assert.assertTrue("Not exist param not in output", output.contains("not1=[]"));
		Assert.assertTrue("Not exist from wc not in output", output.contains("not2=[]"));
	}

	/**
	 * Test bean.
	 */
	public static class TestBean {

		/**
		 * First name.
		 */
		private final String firstName;
		/**
		 * Last name.
		 */
		private final String lastName;

		/**
		 * @param firstName the first name
		 * @param lastName the last name
		 */
		public TestBean(final String firstName, final String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		/**
		 * @return the first name
		 */
		public String getFirstName() {
			return firstName;
		}

		/**
		 *
		 * @return the last name
		 */
		public String getLastName() {
			return lastName;
		}
	}

}

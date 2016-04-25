package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.velocity.VelocityEngineFactory;
import com.github.bordertech.wcomponents.velocity.VelocityTemplateManager;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This component is for rendering only, and contains no behaviour. It renders itself using a Velocity template.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 * @deprecated Do not require a template for an interceptor. Write a custom interceptor that extends
 * {@link InterceptorComponent}.
 */
@Deprecated
public class VelocityInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(VelocityInterceptor.class);

	/**
	 * The velocity template's URL.
	 */
	private String templateUrl;

	/**
	 * Sets the template url.
	 *
	 * @param url the template URL.
	 */
	public void setTemplate(final String url) {
		this.templateUrl = url;
	}

	/**
	 * Sets the template url, based on a component's class name. e.g.
	 * <code>com.github.bordertech.wcomponents.foo.Bar</code> will have a template of
	 * <code>/com/github/bordertech/wcomponents/foo/Bar.vm</code>.
	 *
	 * @param clazz the component class.
	 */
	public void setTemplate(final Class<?> clazz) {
		this.templateUrl = VelocityTemplateManager.toTemplateResourceName(clazz);
	}

	/**
	 * Renders the component using the velocity template which has been provided.
	 *
	 * @param renderContext the context for rendering.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		// TODO: I think that some of the velocity errors should bubble up as
		// runtime exceptions else we can get stuck.
		// Eg. com.github.bordertech.wcomponents.examples.ErrorGenerator - paintComponent.

		if (!(renderContext instanceof WebXmlRenderContext)) {
			throw new SystemException("Unable to render to " + renderContext);
		}

		PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();

		Template template = null;

		try {
			template = VelocityEngineFactory.getVelocityEngine().getTemplate(templateUrl);
		} catch (Exception ex) {
			String message = "Could not open velocity template \"" + templateUrl + "\" for \"" + this.
					getClass().getName() + "\"";
			LOG.error(message, ex);
			writer.println(message);
			return;
		}

		try {
			VelocityContext context = new VelocityContext();
			fillContext(context);

			template.merge(context, writer);
		} catch (ResourceNotFoundException rnfe) {
			LOG.error("Could not find template " + templateUrl, rnfe);
		} catch (ParseErrorException pee) {
			// syntax error : problem parsing the template
			LOG.error("Parse problems", pee);
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
			Throwable wrapped = mie.getWrappedThrowable();

			LOG.error("Problems with velocity", mie);

			if (wrapped != null) {
				LOG.error("Wrapped exception...", wrapped);
			}
		} catch (Exception e) {
			LOG.error("Problems with velocity", e);
		}
	}

	/**
	 * Subclasses can override this method in order to add more parameters to the context.
	 *
	 * @param context the velocity context to add parameters to.
	 */
	protected void fillContext(final VelocityContext context) {
		// NOP
	}
}

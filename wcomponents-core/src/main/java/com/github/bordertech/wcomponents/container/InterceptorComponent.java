package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>
 * Interceptor components are used to plug in features to a container that is serving WComponents. Interceptor
 * components can be chained together. The last component in the chain is normally the WComponent (UI) that is being
 * served up.</p>
 *
 * <p>
 * A new interceptor chain is created for each request that is served, so interceptors do not need to be thread safe.
 * Note that in a Portal environment, the action and render phases will have separate interceptor chain instances.</p>
 *
 * @author Martin Shevchenko
 */
public class InterceptorComponent implements WebComponent {

	/**
	 * The top-level WComponent for the UI that is being served.
	 */
	private WebComponent backing;

	/**
	 * The response that can be used by the interceptor, if required.
	 */
	private Response response;

	/**
	 * Creates an InterceptorComponent with no backing component. Set the backing component after construction using
	 * method {@link #setBackingComponent(WebComponent)}.
	 */
	public InterceptorComponent() {
		super();
	}

	/**
	 * Creates an InterceptorComponent with the given backing component.
	 *
	 * @param component the backing component.
	 */
	public InterceptorComponent(final WebComponent component) {
		backing = component;
	}

	/**
	 * Set the next component to process the request after this one.
	 *
	 * @param component the next component
	 */
	public void setBackingComponent(final WebComponent component) {
		this.backing = component;
	}

	/**
	 * @return the next component to process the request after this one.
	 */
	public WebComponent getBackingComponent() {
		return backing;
	}

	/**
	 * @return the response being processed.
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @param resp the response being processed
	 */
	public void attachResponse(final Response resp) {
		this.response = resp;
		if (backing instanceof InterceptorComponent) {
			((InterceptorComponent) backing).attachResponse(resp);
		}
	}

	/**
	 * Utility method for replacing an individual interceptor within an existing chain.
	 *
	 * @param match the type of the interceptor to be replaced.
	 * @param replacement the new interceptor to be used as a replacement.
	 * @param chain the existing interceptor chain in which the replacement will take place.
	 * @return the modified interceptor chain. If no match was found, the existing interceptor chain is returned
	 * unchanged.
	 */
	public static InterceptorComponent replaceInterceptor(final Class match,
			final InterceptorComponent replacement, final InterceptorComponent chain) {
		if (chain == null) {
			return null;
		}

		InterceptorComponent current = chain;
		InterceptorComponent previous = null;
		InterceptorComponent updatedChain = null;

		while (updatedChain == null) {
			if (match.isInstance(current)) {
				// Found the interceptor that needs to be replaced.
				replacement.setBackingComponent(current.getBackingComponent());
				if (previous == null) {
					updatedChain = replacement;
				} else {
					previous.setBackingComponent(replacement);
					updatedChain = chain;
				}
			} else {
				previous = current;
				WebComponent next = current.getBackingComponent();

				if (next instanceof InterceptorComponent) {
					current = (InterceptorComponent) next;
				} else {
					// Reached the end of the chain. No replacement done.
					updatedChain = chain;
				}
			}
		}

		return updatedChain;
	}

	/**
	 * Subclasses can override. By default it delegates to the next interceptor in the chain.
	 *
	 * @see WebComponent#getName()
	 *
	 * @return the name for this component in the given context.
	 * @deprecated no longer used. use {@link #getId()} instead.
	 */
	@Override
	public String getName() {
		return backing.getName();
	}

	/**
	 * Subclasses can override. By default it delegates to the next interceptor in the chain.
	 *
	 * @see WebComponent#getId()
	 *
	 * @return the id for this component.
	 */
	@Override
	public String getId() {
		return backing.getId();
	}

	/**
	 * Subclasses can override. By default it delegates to the next interceptor in the chain.
	 *
	 * @see WebComponent#serviceRequest(com.github.bordertech.wcomponents.Request)
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {
		backing.serviceRequest(request);
	}

	/**
	 * Subclasses can override. By default it delegates to the next interceptor in the chain.
	 *
	 * @see WebComponent#preparePaint(Request)
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void preparePaint(final Request request) {
		backing.preparePaint(request);
	}

	/**
	 * Subclasses can override. By default it delegates to the next interceptor in the chain.
	 *
	 * @see WebComponent#paint(RenderContext)
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		backing.paint(renderContext);
	}

	/**
	 * Renders the given component to a web-XML String and returns it. This occurs outside the context of a Servlet.
	 *
	 * @param component the component to render.
	 * @return the rendered output as a String.
	 */
	protected static String render(final WebComponent component) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		component.paint(new WebXmlRenderContext(printWriter));
		printWriter.flush();
		String content = stringWriter.toString();
		return content;
	}

	/**
	 * @return The top most WComponent which will be at the end of the interceptor chain.
	 */
	public WComponent getUI() {
		if (backing instanceof WComponent) {
			return (WComponent) backing;
		}

		if (backing instanceof InterceptorComponent) {
			return ((InterceptorComponent) backing).getUI();
		}

		return null;
	}

	/**
	 * Add the WComponent to the end of the interceptor chain.
	 *
	 * @param ui the WComponent to add.
	 */
	public void attachUI(final WComponent ui) {
		if (backing == null || backing instanceof WComponent) {
			backing = ui;
		} else if (backing instanceof InterceptorComponent) {
			((InterceptorComponent) backing).attachUI(ui);
		} else {
			throw new IllegalStateException(
					"Unable to attachUI. Unknown type of WebComponent encountered. " + backing.
					getClass().getName());
		}
	}
}

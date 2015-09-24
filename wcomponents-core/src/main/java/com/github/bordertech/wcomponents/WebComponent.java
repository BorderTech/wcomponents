package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * The interface for web components. Internally the web components don't use this - they use WComponent. This is for use
 * outside of the WComponent framework.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public interface WebComponent extends Serializable {

	/**
	 * Called before the component is painted.
	 *
	 * @param request the request being responded to.
	 */
	void preparePaint(Request request);

	/**
	 * Produce xml (or other) output.
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	void paint(RenderContext renderContext);

	/**
	 * Returns the name of this WebComponent. The name for a WebComponent should be unique across the WComponent tree
	 * (within a single http service).
	 *
	 * @return the name of the component.
	 * @deprecated no longer used. use {@link #getId()} instead.
	 */
	String getName();

	/**
	 * Get a unique ID for this WebComponent. The ID is guaranteed to be unique across an entire HTML page. This method
	 * should be used to generate the HTML ID attributes.
	 *
	 * @return the id of the component.
	 */
	String getId();

	/**
	 * Transfer data from the incoming request to the context.
	 *
	 * @param request the request being serviced.
	 */
	void serviceRequest(Request request);
}

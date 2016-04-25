package com.github.bordertech.wcomponents;

/**
 * A container that provides a {@link NamingContextable} for its children's ids.
 * <p>
 * Components with {@link #setIdName(String)} set must be unique within a {@link NamingContextable}.
 * </p>
 * <p>
 * Example of using a WNamingContext:-
 * </p>
 *
 * <pre>
 * // Naming context with the name &quot;client&quot;
 * WNamingContext context = new WNamingContext(&quot;client&quot;);
 *
 * // Save Button with id &quot;saveButton&quot;. Will render with id &quot;client.saveButton&quot;
 * WButton button = new WButton(&quot;save&quot;);
 * button.setIdName(&quot;saveButton&quot;);
 * context.add(button);
 * </pre>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WNamingContext extends WContainer {

	/**
	 * @param context the context name
	 */
	public WNamingContext(final String context) {
		setIdName(context);
		setNamingContext(true);
	}

}

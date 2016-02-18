package com.github.bordertech.wcomponents.template;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.AbstractSearchReplaceWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TemplateWriter extends SearchReplaceWriter to defer rendering of WComponents when using a template.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TemplateWriter extends AbstractSearchReplaceWriter {

	/**
	 * The components, keyed by a marker that will be output by the template.
	 */
	private final SortedMap<String, WComponent> componentsByKey;

	/**
	 * The user's UIContext to use when painting the components.
	 */
	private final UIContext uic;

	/**
	 * Creates a TemplateWriter/.
	 *
	 * @param backing the underlying writer where output will be sent to.
	 * @param componentsByKey a map of WComponents, keyed by a marker that will be output by the template.
	 * @param uic the user's UIContext that will be used when painting the components.
	 */
	public TemplateWriter(final Writer backing, final Map<String, WComponent> componentsByKey,
			final UIContext uic) {
		super(componentsByKey.keySet().toArray(new String[componentsByKey.size()]), backing);

		this.uic = uic;
		this.componentsByKey = new TreeMap<>(componentsByKey);
	}

	/**
	 * Replaces the search string by rendering the corresponding component.
	 *
	 * @param search the search String that was matched.
	 * @param backing the underlying writer to write the replacement to.
	 */
	@Override
	protected void doReplace(final String search, final Writer backing) {
		WComponent component = componentsByKey.get(search);
		UIContextHolder.pushContext(uic);

		try {
			component.paint(new WebXmlRenderContext((PrintWriter) backing));
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * @return false - the backing should not be closed.
	 */
	@Override
	protected boolean closeBackingOnClose() {
		return false;
	}
}

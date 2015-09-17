package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WInvisibleContainer;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.DebugUtil;
import java.util.List;

/**
 * <p>
 * An Interceptor used to output structural debugging information.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DebugStructureInterceptor extends InterceptorComponent {

	/**
	 * Override paint to render additional information for debugging purposes.
	 *
	 * @param renderContext the renderContext to send the output to.
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		super.paint(renderContext);

		if (!DebugUtil.isDebugStructureEnabled() || !(renderContext instanceof WebXmlRenderContext)) {
			return;
		}

		XmlStringBuilder xml = ((WebXmlRenderContext) renderContext).getWriter();

		xml.appendTag("ui:debug");
		writeDebugInfo(getUI(), xml);
		xml.appendEndTag("ui:debug");
	}

	/**
	 * Writes debugging information for the given component.
	 *
	 * @param component the component to write debugging information for.
	 * @param xml the writer to send the debug output to.
	 */
	protected void writeDebugInfo(final WComponent component, final XmlStringBuilder xml) {
		if (component != null && (component.isVisible() || component instanceof WInvisibleContainer)) {
			xml.appendTagOpen("ui:debugInfo");
			xml.appendAttribute("for", component.getId());
			xml.appendAttribute("class", component.getClass().getName());
			xml.appendOptionalAttribute("type", getType(component));
			xml.appendClose();

			xml.appendTagOpen("ui:debugDetail");
			xml.appendAttribute("key", "defaultState");
			xml.appendAttribute("value", component.isDefaultState());
			xml.appendEnd();

			xml.appendEndTag("ui:debugInfo");

			if (component instanceof WRepeater) {
				// special case for WRepeaters - we must paint the info for each row.
				WRepeater repeater = (WRepeater) component;

				List<UIContext> contexts = repeater.getRowContexts();

				for (int i = 0; i < contexts.size(); i++) {
					UIContextHolder.pushContext(contexts.get(i));

					try {
						writeDebugInfo(repeater.getRepeatedComponent(i), xml);
					} finally {
						UIContextHolder.popContext();
					}
				}
			} else if (component instanceof WCardManager) {
				writeDebugInfo(((WCardManager) component).getVisible(), xml);
			} else if (component instanceof Container) {
				final int size = ((Container) component).getChildCount();

				for (int i = 0; i < size; i++) {
					writeDebugInfo(((Container) component).getChildAt(i), xml);
				}
			}
		}
	}

	/**
	 * Tries to return the type of component.
	 *
	 * @param component the component to determine the type of.
	 * @return the component type, or null if the given component is not a core component.
	 */
	private String getType(final WComponent component) {
		for (Class<?> clazz = component.getClass(); clazz != null && WComponent.class.
				isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
			if ("com.github.bordertech.wcomponents".equals(clazz.getPackage().getName())) {
				return clazz.getName();
			}
		}

		return null;
	}
}

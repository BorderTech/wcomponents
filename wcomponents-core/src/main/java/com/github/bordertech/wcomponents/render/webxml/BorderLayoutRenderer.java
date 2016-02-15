package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Renderer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import com.github.bordertech.wcomponents.layout.BorderLayout.BorderLayoutConstraint;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Duplet;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@link Renderer} renders the children of a {@link WPanel} which have been arranged using a {@link BorderLayout}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class BorderLayoutRenderer extends AbstractWebXmlRenderer {

	/**
	 * Paints the given WPanel's children.
	 *
	 * @param component the container to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WPanel panel = (WPanel) component;
		XmlStringBuilder xml = renderContext.getWriter();
		BorderLayout layout = (BorderLayout) panel.getLayout();
		int hgap = layout.getHgap();
		int vgap = layout.getVgap();

		xml.appendTagOpen("ui:borderlayout");

		xml.appendOptionalAttribute("hgap", hgap > 0, hgap);
		xml.appendOptionalAttribute("vgap", vgap > 0, vgap);

		xml.appendClose();

		// Fetch the children and their constraints.
		final int childCount = panel.getChildCount();
		List<Duplet<WComponent, BorderLayoutConstraint>> children = new ArrayList<>(childCount);

		for (int i = 0; i < childCount; i++) {
			WComponent child = panel.getChildAt(i);
			children.add(new Duplet<>(child, getConstraints(panel, child)));
		}

		// Now paint them
		paintChildrenWithConstraint(children, renderContext, BorderLayout.NORTH);
		paintChildrenWithConstraint(children, renderContext, BorderLayout.EAST);
		paintChildrenWithConstraint(children, renderContext, BorderLayout.SOUTH);
		paintChildrenWithConstraint(children, renderContext, BorderLayout.WEST);
		paintChildrenWithConstraint(children, renderContext, BorderLayout.CENTER);

		xml.appendEndTag("ui:borderlayout");
	}

	/**
	 * Retrieves the name of the element that will contain components with the given constraint.
	 *
	 * @param constraint the constraint to retrieve the tag for.
	 * @return the tag for the given constraint.
	 */
	private String getTag(final BorderLayoutConstraint constraint) {
		switch (constraint) {
			case EAST:
				return "ui:east";
			case NORTH:
				return "ui:north";
			case SOUTH:
				return "ui:south";
			case WEST:
				return "ui:west";
			case CENTER:

			default:
				return "ui:center";
		}
	}

	/**
	 * Paints all the child components with the given constraint.
	 *
	 * @param children the list of potential children to paint.
	 * @param renderContext the RenderContext to paint to.
	 * @param constraint the target constraint.
	 */
	private void paintChildrenWithConstraint(
			final List<Duplet<WComponent, BorderLayoutConstraint>> children,
			final WebXmlRenderContext renderContext, final BorderLayoutConstraint constraint) {
		String containingTag = null;
		XmlStringBuilder xml = renderContext.getWriter();

		final int size = children.size();

		for (int i = 0; i < size; i++) {
			Duplet<WComponent, BorderLayoutConstraint> child = children.get(i);

			if (constraint.equals(child.getSecond())) {
				if (containingTag == null) {
					containingTag = getTag(constraint);
					xml.appendTag(containingTag);
				}

				child.getFirst().paint(renderContext);
			}
		}

		if (containingTag != null) {
			xml.appendEndTag(containingTag);
		}
	}

	/**
	 * Retrieves the layout constraint for the given component. If the constraint is incorrectly configured, an
	 * {@link IllegalStateException} will be thrown.
	 *
	 * @param panel the panel which contains the child
	 * @param child the component to retrieve the constraint for.
	 *
	 * @return the layout constraint for the given component.
	 */
	private static BorderLayoutConstraint getConstraints(final WPanel panel, final WComponent child) {
		Object constraints = panel.getLayoutConstraints(child);

		if (constraints instanceof BorderLayoutConstraint) {
			return (BorderLayoutConstraint) constraints;
		} else if (constraints == null) {
			return BorderLayout.CENTER;
		} else {
			throw new IllegalStateException("Constraint must be a BorderLayoutConstraint");
		}
	}
}

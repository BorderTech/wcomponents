package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * <p>
 * WDecorated is a "decorated label" which is typically displayed inside other components, for example
 * {@link com.github.bordertech.wcomponents.WTab}. The decorated label allows deveopers to mix text with other content,
 * e.g. images.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class WDecoratedLabel extends AbstractMutableContainer implements AjaxTarget {

	/**
	 * Creates an initially empty decorated label, with a {@link WText} as the body content.
	 */
	public WDecoratedLabel() {
		this(null, new WText(""), null, null);
	}

	/**
	 * Creates a decorated label with a WText as the body content, containing the given text.
	 *
	 * @param text the label's text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public WDecoratedLabel(final String text, final Serializable... args) {
		this(null, new WText(text, args), null);
	}

	/**
	 * Creates a decorated label with the given body.
	 *
	 * @param body the label's body content.
	 */
	public WDecoratedLabel(final WComponent body) {
		this(null, body, null);
	}

	/**
	 * Creates a decorated label with the given content. The head, body, and tail are usually displayed in that order,
	 * from left-to-right. At minimum, the body content must not be null.
	 *
	 * @param head the label's head content.
	 * @param body the label's body content.
	 * @param tail the label's tail content.
	 */
	public WDecoratedLabel(final WComponent head, final WComponent body, final WComponent tail) {
		setHead(head);
		setBody(body);
		setTail(tail);
	}

	/**
	 * Retrieves the label's head component.
	 *
	 * @return the head content, if specified.
	 */
	public WComponent getHead() {
		return getComponentModel().head;
	}

	/**
	 * Sets the label head content.
	 *
	 * @param head the head content, or null for no content.
	 */
	public void setHead(final WComponent head) {
		DecoratedLabelModel model = getOrCreateComponentModel();

		if (model.head != null) {
			remove(model.head);
		}

		model.head = head;

		if (head != null) {
			add(head);
		}
	}

	/**
	 * Retrieves the label's body component.
	 *
	 * @return the body content, if specified.
	 */
	public WComponent getBody() {
		return getComponentModel().body;
	}

	/**
	 * Sets the label body content.
	 *
	 * @param body the body content, must not be null.
	 */
	public void setBody(final WComponent body) {
		DecoratedLabelModel model = getOrCreateComponentModel();

		if (body == null) {
			throw new IllegalArgumentException("Body content must not be null");
		}

		if (model.body != null) {
			remove(model.body);
		}

		model.body = body;
		add(body);
	}

	/**
	 * Retrieves the label's tail component.
	 *
	 * @return the tail content, if specified.
	 */
	public WComponent getTail() {
		return getComponentModel().tail;
	}

	/**
	 * Sets the label tail content.
	 *
	 * @param tail the tail content, or null for no content.
	 */
	public void setTail(final WComponent tail) {
		DecoratedLabelModel model = getOrCreateComponentModel();

		if (model.tail != null) {
			remove(model.tail);
		}

		model.tail = tail;

		if (tail != null) {
			add(tail);
		}
	}

	/**
	 * Attempts to set the text contained in the body component. This works only for simple components types:
	 * <ul>
	 * <li>{@link WButton}</li>
	 * <li>{@link WLink}</li>
	 * <li>{@link WLabel}</li>
	 * <li>{@link WText}</li>
	 * </ul>
	 *
	 * @param text the new body text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setText(final String text, final Serializable... args) {
		WComponent body = getBody();

		if (body instanceof WText) {
			((WText) body).setText(text, args);
		} else if (body instanceof WLabel) {
			((WLabel) body).setText(text, args);
		} else if (body instanceof WButton) {
			((WButton) body).setText(text, args);
		} else if (body instanceof WLink) {
			((WLink) body).setText(text, args);
		} else if (body == null) {
			setBody(new WText(text, args));
		}
	}

	/**
	 * Attempts to retrieve the text contained in the body component. This works only for simple components types:
	 * <ul>
	 * <li>{@link WButton}</li>
	 * <li>{@link WLink}</li>
	 * <li>{@link WLabel}</li>
	 * <li>{@link WText}</li>
	 * </ul>
	 *
	 * @return the text contained in the body, or null if not found.
	 */
	public String getText() {
		WComponent body = getComponentModel().body;

		if (body instanceof WText) {
			return ((WText) body).getText();
		} else if (body instanceof WLabel) {
			return ((WLabel) body).getText();
		} else if (body instanceof WButton) {
			return ((WButton) body).getText();
		} else if (body instanceof WLink) {
			return ((WLink) body).getText();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecoratedLabelModel getComponentModel() {
		return (DecoratedLabelModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecoratedLabelModel getOrCreateComponentModel() {
		return (DecoratedLabelModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new Component model appropriate for this component.
	 *
	 * @return a new DecoratedLabelModel.
	 */
	@Override
	protected DecoratedLabelModel newComponentModel() {
		return new DecoratedLabelModel();
	}

	/**
	 * A class used to hold the type of label for this component.
	 *
	 * @author Jonathan Austin
	 */
	public static class DecoratedLabelModel extends ComponentModel {

		/**
		 * The component used in the label head.
		 */
		private WComponent head;

		/**
		 * The component used in the label body.
		 */
		private WComponent body;

		/**
		 * The component used in the label tail.
		 */
		private WComponent tail;
	}
}

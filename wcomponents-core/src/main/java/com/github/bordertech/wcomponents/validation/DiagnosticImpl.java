package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Default implementation of the {@link Diagnostic} interface.
 *
 * @see Diagnostic
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class DiagnosticImpl implements Diagnostic {

	/**
	 * The severity of the diagnostic. One of {@link Diagnostic#INFO},
	 * {@link Diagnostic#WARNING} or {@link Diagnostic#ERROR}.
	 */
	private final int severity;

	/**
	 * The UIContext in which the diagnostic occurred.
	 */
	private final UIContext uic;

	/**
	 * The field is the source of the diagnostic, or null if there is no appropriate field. Preferably a {@link Input}.
	 */
	private final WComponent component;

	/**
	 * The message pattern. This must use {@link MessageFormat} syntax if args are present.
	 */
	private final String message;

	/**
	 * The message format arguments.
	 */
	private final Object[] args;

	// --------------------------------------------------------
	// Constructors
	/**
	 * Constructs a DiagnosticImpl.
	 *
	 * @param severity The severity of the diagnostic. One of {@link Diagnostic#INFO}, {@link Diagnostic#WARNING} or
	 * {@link Diagnostic#ERROR}.
	 *
	 * @param source the component which the diagnostic relates to.
	 * @param message the message to display to the user, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public DiagnosticImpl(final int severity, final WComponent source,
			final String message, final Serializable... args) {
		this(severity, UIContextHolder.getCurrent(), source, message, args);
	}

	/**
	 * Constructs a DiagnosticImpl (such as an error, warning, or information item) using generic Java Objects as source
	 * and sourceField.
	 *
	 * @param severity The severity of the diagnostic. One of {@link Diagnostic#INFO}, {@link Diagnostic#WARNING} or
	 * {@link Diagnostic#ERROR}.
	 *
	 * @param uic the current user's UIContext
	 * @param source the component which the diagnostic relates to.
	 * @param message the message to display to the user, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public DiagnosticImpl(final int severity, final UIContext uic, final WComponent source,
			final String message, final Serializable... args) {
		this.severity = severity;
		this.uic = uic;
		this.component = source;
		this.message = message;
		this.args = args;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		// We need to change references to input fields to their label or accessible text.
		Object[] modifiedArgs = new Object[args.length];

		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Input) {
				Input input = (Input) args[i];
				String text = null;

				UIContextHolder.pushContext(uic);

				try {
					if (input.getLabel() != null) {
						text = input.getLabel().getText();

						// Some apps use colons at the end of labels. We trim these off automatically
						if (!Util.empty(text) && text.charAt(text.length() - 1) == ':') {
							text = text.substring(0, text.length() - 1);
						}
					}

					if (text == null) {
						text = input.getAccessibleText();
					}
				} finally {
					UIContextHolder.popContext();
				}

				modifiedArgs[i] = text == null ? "" : text;
			} else {
				modifiedArgs[i] = args[i];
			}
		}

		return I18nUtilities.format(null, message, modifiedArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSeverity() {
		return severity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UIContext getContext() {
		return uic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WComponent getComponent() {
		return component;
	}

	/**
	 * Creates a {@link WComponent} that will render the error message. If users want to do something more sophisticated
	 * than rendering the description as text (e.g. the message might include a link/button) then they should extend
	 * this class and override this method.
	 *
	 * @return The WComponent that represents the error message.
	 */
	public WComponent createDiagnosticErrorComponent() {
		return new WText() {
			@Override
			public String getText() {
				return getDescription();
			}
		};
	}
}

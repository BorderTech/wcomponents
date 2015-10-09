package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.AbstractMutableContainer;
import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.MutableContainer;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDelegate;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WInvisibleContainer;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides some protection against bad code when examples are being written.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class SafetyContainer extends AbstractMutableContainer {

	/**
	 * An invisible shim is used to exclude the contents from normal WComponent processing.
	 */
	private final MutableContainer shim = new WInvisibleContainer();

	/**
	 * An attribute key used to temporarily store any exception which occurs in the example.
	 */
	private static final String ERROR_KEY = "error";

	/**
	 * Creates a SafetyContainer.
	 */
	SafetyContainer() {
		super.add(shim);
	}

	/**
	 * Override handleRequest in order to safely process the example component, which has been excluded from normal
	 * WComponent processing.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (!isInitialised()) {
			getOrCreateComponentModel().delegate = new SafetyContainerDelegate(UIContextHolder.
					getCurrent());
			setInitialised(true);
		}

		try {
			UIContext delegate = getComponentModel().delegate;
			UIContextHolder.pushContext(delegate);

			try {
				for (int i = 0; i < shim.getChildCount(); i++) {
					shim.getChildAt(i).serviceRequest(request);
				}

				delegate.doInvokeLaters();
			} finally {
				UIContextHolder.popContext();
			}
		} catch (final ActionEscape e) {
			// We don't want to catch ActionEscapes (e.g. ForwardExceptions)
			throw e;
		} catch (final Exception e) {
			if (isAjaxOrTargetedRequest(request)) {
				throw new SystemException(e.getMessage(), e);
			} else {
				setAttribute(ERROR_KEY, e);
			}
		}
	}

	/**
	 * <p>
	 * Override preparePaintComponent to provide some protection against bad code when examples are being developed,
	 * resulting in invalid XML.</p>
	 *
	 * <p>
	 * Real applications should not emit HTML directly.</p>
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (getAttribute(ERROR_KEY) == null) {
			try {
				for (int i = 0; i < shim.getChildCount(); i++) {
					shim.getChildAt(i).preparePaint(request);
				}
			} catch (final Exception e) {
				if (isAjaxOrTargetedRequest(request)) {
					throw new SystemException(e.getMessage(), e);
				} else {
					setAttribute(ERROR_KEY, e);
				}
			}
		}
	}

	/**
	 * <p>
	 * Override paintComponent to provide some protection against bad code when examples are being developed, resulting
	 * in invalid XML.</p>
	 *
	 * <p>
	 * Real applications should not emit HTML directly.</p>
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		final Throwable error = (Throwable) getAttribute(ERROR_KEY);

		if (error == null) {
			if (renderContext instanceof WebXmlRenderContext) {
				// For a WebXmlContext, we can output partial XML.
				WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;

				final StringWriter buf = new StringWriter();
				WebXmlRenderContext bufferedContext = new WebXmlRenderContext(new PrintWriter(buf));

				try {
					for (int i = 0; i < shim.getChildCount(); i++) {
						shim.getChildAt(i).paint(bufferedContext);
					}

					webRenderContext.getWriter().write(buf.toString());
				} catch (final Exception e) {
					new ErrorComponent("Error during rendering", e).paint(renderContext);
					new WHorizontalRule().paint(renderContext);

					PrintWriter writer = webRenderContext.getWriter();
					writer.println("\n<br/>Partial XML:<br/>\n<pre>\n");
					writer.println(WebUtilities.encode(buf.toString()));
					writer.println("\n</pre>");
				}
			} else {
				try {
					for (int i = 0; i < shim.getChildCount(); i++) {
						shim.getChildAt(i).paint(renderContext);
					}
				} catch (final Exception e) {
					new ErrorComponent("Error during rendering", e).paint(renderContext);
					new WHorizontalRule().paint(renderContext);
				}
			}
		} else {
			new ErrorComponent("Error during action phase", error).paint(renderContext);
		}
	}

	/**
	 * Resets the contents.
	 */
	public void resetContent() {
		for (int i = 0; i < shim.getChildCount(); i++) {
			WComponent child = shim.getChildAt(i);
			child.reset();
		}

		removeAttribute(SafetyContainer.ERROR_KEY);
	}

	/**
	 * Override add so that components are added to the shim.
	 *
	 * @param component the component to add.
	 */
	@Override
	public void add(final WComponent component) {
		shim.add(component);
	}

	/**
	 * Override remove so that components are removed from the shim.
	 *
	 * @param component the component to remove.
	 */
	@Override
	public void remove(final WComponent component) {
		shim.remove(component);
	}

	/**
	 * Override removeAll so that all components are removed from the shim.
	 */
	@Override
	public void removeAll() {
		shim.removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SafetyContainerModel getComponentModel() {
		return (SafetyContainerModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SafetyContainerModel getOrCreateComponentModel() {
		return (SafetyContainerModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new SafetyContainerModel.
	 */
	@Override
	protected ComponentModel newComponentModel() {
		return new SafetyContainerModel();
	}

	/**
	 * Holds the extrinsic state information of the SafetyContainer.
	 */
	public static final class SafetyContainerModel extends ComponentModel {

		private UIContext delegate;
	}

	/**
	 * We need to keep the Example's invoke later actions separate from the rest, so this delegate keeps its own list of
	 * runnables.
	 */
	private static final class SafetyContainerDelegate extends UIContextDelegate {

		/**
		 * A list of runnables to invoke later.
		 */
		private transient List<Runnable> invokeLaterRunnables;

		/**
		 * Creates a SafetyContainerDelegate.
		 *
		 * @param uic the backing UIContext.
		 */
		private SafetyContainerDelegate(final UIContext uic) {
			super(uic);
		}

		@Override
		public void invokeLater(final Runnable runnable) {
			if (invokeLaterRunnables == null) {
				invokeLaterRunnables = new ArrayList<>();
			}

			invokeLaterRunnables.add(runnable);
		}

		/**
		 * Runs the runnables that were added using {@link #invokeLater(Runnable)}.
		 */
		@Override
		public void doInvokeLaters() {
			if (invokeLaterRunnables == null) {
				return;
			}

			// The Runnables we are about to run may add their own invoke later
			// runnables, so
			// loop to make sure we process them all.
			while (!invokeLaterRunnables.isEmpty()) {
				final List<Runnable> runnables = new ArrayList<>();
				runnables.addAll(invokeLaterRunnables);
				invokeLaterRunnables.clear();

				for (final Runnable run : runnables) {
					run.run();
				}
			}
		}
	}

	/**
	 * @param request the request being processed
	 * @return true if request is AJAX or Target request
	 */
	private boolean isAjaxOrTargetedRequest(final Request request) {
		return request.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME) != null
				|| request.getParameter(WServlet.TARGET_ID_PARAM_NAME) != null;
	}

}

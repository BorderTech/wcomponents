package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A component which enables users to pick an example to display. The UI is provided by either delegate
 * {@link SimplePicker} or {@link TreePicker}, depending on the configuration parameters. By default, the TreePicker is
 * used. To use the (old) simple example picker, set the following parameter in e.g. your local_app.properties.</p>
 *
 * <p>
 * It also demonstrates how to add additional functionality on the client, by performing syntax highlighting of the java
 * source code using javascript/css.</p>
 *
 * <pre>
 * com.github.bordertech.wcomponents.examples.picker.ExamplePicker.ui = com.github.bordertech.wcomponents.examples.picker.SimplePicker
 * </pre>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExamplePicker extends WApplication {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ExamplePicker.class);

	/**
	 * The parameter key controlling which UI is displayed.
	 */
	private static final String PARAM_KEY = "com.github.bordertech.wcomponents.examples.picker.ExamplePicker.ui";

	/**
	 * Used to display step error messages.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * Construct the example picker.
	 */
	public ExamplePicker() {
		add(messages);

		// Syntax highlighting
		// addCssFile("/com/github/bordertech/wcomponents/examples/sunlight.default.css"); // use ONE CSS File
		addCssFile("/com/github/bordertech/wcomponents/examples/sunlight.dark.css");
		addJsFile("/com/github/bordertech/wcomponents/examples/sunlight-min.js");
		addJsFile("/com/github/bordertech/wcomponents/examples/sunlight.java-min.js");

		String className = Config.getInstance().getString(PARAM_KEY, TreePicker.class.getName());

		try {
			WComponent ui = (WComponent) Class.forName(className).newInstance();
			add(ui);

		} catch (Exception e) {
			add(new ErrorComponent("Unable to load picker ui " + className, e));
		}
	}


	/**
	 * If a step error has occurred, then display an error message to the user.
	 */
	@Override
	public void handleStepError() {
		messages.addMessage(new Message(Message.WARNING_MESSAGE,
				"A request was made that is not in the expected sequence and the application has been refreshed to its current state."));
	}

	/**
	 * Override preparePaint in order to set up the resources on first access by a user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			// Check project versions for Wcomponents-examples and WComponents match
			String egVersion = Config.getInstance().getString("wcomponents-examples.version");
			String wcVersion = WebUtilities.getProjectVersion();

			if (egVersion != null && !egVersion.equals(wcVersion)) {
				String msg = "WComponents-Examples version (" + egVersion + ") does not match WComponents version ("
						+ wcVersion + ").";

				LOG.error(msg);

				messages.addMessage(new Message(Message.ERROR_MESSAGE, msg));
			}
			setInitialised(true);
		}

	}
}

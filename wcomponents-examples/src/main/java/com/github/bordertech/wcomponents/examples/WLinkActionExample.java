package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class demonstrates how an action can be called on a {@link WLink}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WLinkActionExample extends WPanel {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WLinkActionExample.class);

	/**
	 * Test url used in the links.
	 */
	private static final String LINK_URL = "http://www.google.com";

	/**
	 * Construct the example.
	 */
	public WLinkActionExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		WPanel messagePanel = new WPanel();
		add(messagePanel);

		final WMessages messages = new WMessages();
		messagePanel.add(messages);

		final WLink link = new WLink("google (no action target)", LINK_URL);
		add(link);
		Action action = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				LOG.info(link.getText() + " was clicked");
			}
		};
		link.setAction(action);

		final WLink link2 = new WLink("google (with action target)", LINK_URL);
		add(link2);
		Action action2 = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE,
						link2.getText() + " clicked"));
			}
		};
		link2.setAction(action2, this);

		final WLink link3 = new WLink("google (same window that wont navigate)", LINK_URL);
		link3.setTargetWindowName(null);
		add(link3);
		Action action3 = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE,
						link3.getText() + " clicked"));
			}
		};
		link3.setAction(action3, this);

		final WLink link4 = new WLink("google (multiple targets)", LINK_URL);
		add(link4);

		// Text used for second target.
		WPanel textPanel = new WPanel();
		add(textPanel);
		final WText text = new WText();
		textPanel.add(text);

		Action action4 = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE,
						link4.getText() + " clicked"));
				text.setText("Link Action: " + new Date().toString());
			}
		};
		link4.setAction(action4, messagePanel, textPanel);

		final WLink link5 = new WLink("mailto", "mailto:wc@example.org?subject="
				+ WebUtilities.escapeForUrl("Test mailto launch"));
		link5.setTargetWindowName(null);
		add(link5);
		Action action5 = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE,
						link5.getText() + " clicked"));
			}
		};
		link5.setAction(action5, this);

	}

}

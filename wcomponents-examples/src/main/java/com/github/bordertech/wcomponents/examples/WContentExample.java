package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.ContentAccess;
import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.MutableContainer;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WContent.DisplayMode;
import com.github.bordertech.wcomponents.WContentLink;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.GridLayout;

/**
 * Demonstrates the use of {@link WContent} and {@link WContentLink} for displaying arbitrary binary files.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WContentExample extends WPanel {

	/**
	 * Creates a WContentExample.
	 */
	public WContentExample() {
		setLayout(new GridLayout(0, 6, 10, 5));

		add(new WText("WButton + WContent, opens in new window after round-trip"));
		add(new WText("WButton + WContent, opens in new window after AJAX request"));
		add(new WText("WContentLink, opens in new window"));
		add(new WText("WContentLink, prompts to save"));
		add(new WText("WContentLink, opens in-line"));
		add(new WText("WMenu with a link"));

		// Some content which all browsers should be able to display internally
		addContentRow("View GIF", new InternalResource(
				"/com/github/bordertech/wcomponents/examples/picker/wclogo_small.gif", "example.gif"),
				this);

		// Some content which browsers may be able to display using a plugin.
		addContentRow("View PDF", new InternalResource(
				"/com/github/bordertech/wcomponents/examples/test.pdf", "example.pdf"), this);

		// Some content which browsers should not be able to display.
		// The user should always be prompted to save or open with an associated application.
		addContentRow("View TR5", new InternalResource(
				"/com/github/bordertech/wcomponents/examples/test.tr5", "example.tr5"), this);
		addContentRow("MP3", new InternalResource("/audio/mp3.mp3", "example.mp3"), this);
	}

	/**
	 * Adds components to the given container which demonstrate various ways of acessing the given content.
	 *
	 * @param contentDesc the description of the content, used to label the controls.
	 * @param contentAccess the content which will be displayed.
	 * @param target the container to add the UI controls to.
	 */
	private void addContentRow(final String contentDesc, final ContentAccess contentAccess,
			final MutableContainer target) {
		// Demonstrate WButton + WContent, round trip
		WButton button = new WButton(contentDesc);
		final WContent buttonContent = new WContent();

		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				buttonContent.setContentAccess(contentAccess);
				buttonContent.display();
			}
		});

		WContainer buttonCell = new WContainer();
		buttonCell.add(buttonContent);
		buttonCell.add(button);
		target.add(buttonCell);

		// Demonstrate WButton + WContent, using AJAX
		WButton ajaxButton = new WButton(contentDesc);
		final WContent ajaxContent = new WContent();

		ajaxButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				ajaxContent.setContentAccess(contentAccess);
				ajaxContent.display();
			}
		});

		WContainer ajaxCell = new WContainer();
		// The WContent must be wrapped in an AJAX targetable container
		WPanel ajaxContentPanel = new WPanel();
		ajaxContentPanel.add(ajaxContent);
		ajaxCell.add(ajaxButton);
		ajaxCell.add(ajaxContentPanel);
		ajaxButton.setAjaxTarget(ajaxContentPanel);
		target.add(ajaxCell);

		// Demonstrate WContentLink - new window
		WContentLink contentLinkNewWindow = new WContentLink(contentDesc) {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				setContentAccess(contentAccess);
			}
		};

		target.add(contentLinkNewWindow);

		// Demonstrate WContentLink - prompt to save
		WContentLink contentLinkPromptToSave = new WContentLink(contentDesc) {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				setContentAccess(contentAccess);
			}
		};

		contentLinkPromptToSave.setDisplayMode(DisplayMode.PROMPT_TO_SAVE);
		target.add(contentLinkPromptToSave);

		// Demonstrate WContentLink - inline
		WContentLink contentLinkInline = new WContentLink(contentDesc) {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				setContentAccess(contentAccess);
			}
		};

		contentLinkInline.setDisplayMode(DisplayMode.DISPLAY_INLINE);
		target.add(contentLinkInline);

		// Demonstrate targeting of content via a URL
		WMenu menu = new WMenu(WMenu.MenuType.FLYOUT);
		final WContent menuContent = new WContent();
		menuContent.setDisplayMode(DisplayMode.PROMPT_TO_SAVE);
		WMenuItem menuItem = new WMenuItem(contentDesc) {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				menuContent.setContentAccess(contentAccess);
				setUrl(menuContent.getUrl());
			}
		};

		menu.add(menuItem);

		WContainer menuCell = new WContainer();
		menuCell.add(menuContent);
		menuCell.add(menu);
		target.add(menuCell);
	}
}

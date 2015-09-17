package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;

/**
 * A WComponent that displays information about the current environment.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class InfoDump extends WContainer {

	/**
	 * The text area used to display the environment information.
	 */
	private final WTextArea console;

	/**
	 * Creates an InfoDump.
	 */
	public InfoDump() {
		console = new WTextArea();
		console.setColumns(80);
		console.setRows(24);

		WButton envBtn = new WButton("Print WEnvironment");

		envBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				dumpWEnvironment();
			}
		});

		WButton clearBtn = new WButton("Clear");

		clearBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				clearConsole();
			}
		});

		add(clearBtn);
		add(envBtn);

		// TODO: This is bad - use a layout instead
		WText lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);
		add(lineBreak);

		add(console);
	}

	/**
	 * Clears the console text.
	 */
	private void clearConsole() {
		console.setText("");
	}

	/**
	 * Appends the given text to the console.
	 *
	 * @param text the text to append
	 */
	private void appendToConsole(final String text) {
		console.setText(console.getText() + text);
	}

	/**
	 * Appends the current environment to the console.
	 */
	private void dumpWEnvironment() {
		StringBuffer text = new StringBuffer();

		Environment env = getEnvironment();

		text.append("\n\nWEnvironment"
				+ "\n------------");

		text.append("\nAppId: ").append(env.getAppId());
		text.append("\nBaseUrl: ").append(env.getBaseUrl());
		text.append("\nHostFreeBaseUrl: ").append(env.getHostFreeBaseUrl());
		text.append("\nPostPath: ").append(env.getPostPath());
		text.append("\nTargetablePath: ").append(env.getWServletPath());
		text.append("\nAppHostPath: ").append(env.getAppHostPath());
		text.append("\nThemePath: ").append(env.getThemePath());
		text.append("\nStep: ").append(env.getStep());
		text.append("\nSession Token: ").append(env.getSessionToken());
		text.append("\nFormEncType: ").append(env.getFormEncType());
		text.append('\n');

		appendToConsole(text.toString());
	}
}

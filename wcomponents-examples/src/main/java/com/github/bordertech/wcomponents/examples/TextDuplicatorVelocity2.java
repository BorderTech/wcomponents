package com.github.bordertech.wcomponents.examples;

/**
 * An example extension of {@link TextDuplicatorVelocityImpl} to show how different a template can be specified.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TextDuplicatorVelocity2 extends TextDuplicatorVelocityImpl {

	/**
	 * Creates a TextDuplicator_Velocity2.
	 */
	public TextDuplicatorVelocity2() {
		super("Pretty Duplicator 2");

		// Associate a different template.
		// This time we've decided to choose the template by supplying a
		// resource name rather than a class name.
		setTemplate("com/github/bordertech/wcomponents/examples/TextDuplicator_Velocity2.vm");
	}
}

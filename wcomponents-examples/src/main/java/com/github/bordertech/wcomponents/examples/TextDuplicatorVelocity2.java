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
		super("com/github/bordertech/wcomponents/examples/TextDuplicator_Velocity2.vm", "Pretty Duplicator 2");
	}
}

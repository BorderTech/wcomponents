package com.github.openborders.wcomponents.examples;

/**
 * An example extension of {@link TextDuplicator_VelocityImpl}
 * to show how different a template can be specified.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TextDuplicator_Velocity2 extends TextDuplicator_VelocityImpl
{
    /** Creates a TextDuplicator_Velocity2. */
    public TextDuplicator_Velocity2()
    {
        super("Pretty Duplicator 2");

        // Associate a different template.
        // This time we've decided to choose the template by supplying a
        // resource name rather than a class name.
        setTemplate("com/github/openborders/wcomponents/examples/TextDuplicator_Velocity2.vm");
    }
}

package com.github.bordertech.wcomponents.examples.transientcontainer;

/**
 * An example data bean.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleDataBean {

	/**
	 * The 'colour' attribute.
	 */
	private String colour;
	/**
	 * The 'shape' attribute.
	 */
	private String shape;
	/**
	 * The 'animal' attribute.
	 */
	private String animal;

	/**
	 * Creates an ExampleDataBean.
	 *
	 * @param colour the colour.
	 * @param shape the shape.
	 * @param animal the animal.
	 */
	public ExampleDataBean(final String colour, final String shape, final String animal) {
		this.colour = colour;
		this.shape = shape;
		this.animal = animal;
	}

	/**
	 * @return Returns the animal.
	 */
	public String getAnimal() {
		return animal;
	}

	/**
	 * @param animal The animal to set.
	 */
	public void setAnimal(final String animal) {
		this.animal = animal;
	}

	/**
	 * @return Returns the colour.
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * @param colour The colour to set.
	 */
	public void setColour(final String colour) {
		this.colour = colour;
	}

	/**
	 * @return Returns the shape.
	 */
	public String getShape() {
		return shape;
	}

	/**
	 * @param shape The shape to set.
	 */
	public void setShape(final String shape) {
		this.shape = shape;
	}
}

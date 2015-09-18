package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.TestLookupTable;
import java.io.Serializable;
import java.util.List;

/**
 * An example search result bean.
 *
 * @author Martin Shevchenko
 */
public class SearchResultRowBO implements Serializable {

	/**
	 * The set of animal types.
	 */
	public static final String[] ANIMAL_OPTIONS = new String[]{"Ant", "Cat", "Dog", "Fish", "Goat", "Horse", "Rabbit"};

	private String name;
	private TestLookupTable.TableEntry dayOfWeek;
	private Boolean ticked;
	private Boolean happy;
	private List<String> animals;
	private String desc;

	/**
	 * @return Returns the animals.
	 */
	public List<String> getAnimals() {
		return animals;
	}

	/**
	 * @param animals The animals to set.
	 */
	public void setAnimals(final List<String> animals) {
		this.animals = animals;
	}

	/**
	 * @return Returns the country.
	 */
	public TestLookupTable.TableEntry getCountry() {
		return dayOfWeek;
	}

	/**
	 * @param country The country to set.
	 */
	public void setCountry(final TestLookupTable.TableEntry country) {
		this.dayOfWeek = country;
	}

	/**
	 * @return Returns the desc.
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc The desc to set.
	 */
	public void setDesc(final String desc) {
		this.desc = desc;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return Returns the ticked.
	 */
	public Boolean getTicked() {
		return ticked;
	}

	/**
	 * @param ticked The ticked to set.
	 */
	public void setTicked(final Boolean ticked) {
		this.ticked = ticked;
	}

	/**
	 * @return Returns the happy flag.
	 */
	public Boolean getHappy() {
		return happy;
	}

	/**
	 * @param happy The happy flag to set.
	 */
	public void setHappy(final Boolean happy) {
		this.happy = happy;
	}

}

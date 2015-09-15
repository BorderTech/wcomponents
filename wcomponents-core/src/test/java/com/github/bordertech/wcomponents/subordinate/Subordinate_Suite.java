package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.subordinate.builder.Builder_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test cases for Subordinate Control classes.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Abstract
	AbstractAction_Test.class,
	AbstractCompare_Test.class,
	AbstractCondition_Test.class,
	AbstractSetEnable_Test.class,
	AbstractSetMandatory_Test.class,
	AbstractSetVisible_Test.class,
	// Conditions
	And_Test.class,
	Or_Test.class,
	Not_Test.class,
	Equal_Test.class,
	GreaterThan_Test.class,
	GreaterThanOrEqual_Test.class,
	LessThan_Test.class,
	LessThanOrEqual_Test.class,
	Match_Test.class,
	NotEqual_Test.class,
	// Actions
	Disable_Test.class,
	Enable_Test.class,
	Hide_Test.class,
	Show_Test.class,
	Mandatory_Test.class,
	Optional_Test.class,
	// Group Actions
	DisableInGroup_Test.class,
	EnableInGroup_Test.class,
	HideInGroup_Test.class,
	ShowInGroup_Test.class,
	// Control
	Rule_Test.class,
	// Subordinate Control
	WSubordinateControl_Test.class,
	// Builder
	Builder_Suite.class
})
public class Subordinate_Suite {
}

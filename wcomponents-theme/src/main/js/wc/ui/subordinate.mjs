/**
 * This module provides functionality to allow some components to control some states of other components. It is a
 * pretty rubbish way of doing this foisted upon us by having Java write web UIs. This module <strong>will</strong>
 * cause you pain and suffering. It may result in mental health issues. It is, however, not nearly as bad as it was in
 * the previous iteration.
 *
 * For example, allows us to create a rule so that if the selected radio button in radio group "fubar" has a value of
 * "foo" then show the div with the id of "bar".
 */

import interchange from "wc/date/interchange.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import initialise from "wc/dom/initialise.mjs";
import Action from "wc/ui/SubordinateAction.mjs";
import unique from "wc/array/unique.mjs";
import shed from "wc/dom/shed.mjs";
import timers from "wc/timers.mjs";

let event,
	dateField,
	multiSelectPair,
	waitingForRules = false,  // flag if we don't add event listeners when dom is loaded
	changeInited = false;

const ruleStore = {},  // stores the rule objects against their rule id
	elementToRuleMap = {},  // maps dom element ids to rule ids
	regexCache = {};  // cache any dynamically created RegExp instances we may need repeatedly

const instance = {
	/**
	 * Register one or more component groups as being groups of interest for subordinate actions.
	 * @see {@link module:wc/ui/SubordinateAction.registerGroups}
	 * @function module:wc/ui/subordinate.registerGroup
	 * @param {module:wc/ui/SubordinateAction~groupDTO[]} groups An array where each item represents a
	 *    subordinate component group.
	 */
	registerGroups: function(groups) {
		if (groups) {
			Action.registerGroups(groups);
		}
	},

	/**
	 * Register an array of subordinate rules.
	 * @function module:wc/ui/subordinate.register
	 * @param {module:wc/ui/subordinate~registerDTO[]} rules An array of Objects containing configuration
	 *    options.
	 */
	register: function(rules) {
		/**
		 * @param {string} id
		 * @param {string} ruleId
		 */
		const setRule = (id, ruleId) => {
			const ruleArr = elementToRuleMap[id] || (elementToRuleMap[id] = []);
			ruleArr.push(ruleId);
		};
		if (rules) {
			for (const rule of rules) {
				let ruleId = rule.id;
				ruleStore[ruleId] = rule;
				// initialising the actions could be deferred but then you would have to check if inited forever after
				rule.onTrue = initializeActions(rule.onTrue);
				rule.onFalse = initializeActions(rule.onFalse);
				rule.controllers = unique(rule.controllers);  // the provided list of controllers is not guaranteed to be unique
				rule.controllers.forEach(id => setRule(id, ruleId));
			}
		}
		if (waitingForRules) {
			waitingForRules = false;
			console.log("Rules registered: adding event listeners");
			initialiser.initialise(document.body);
		}
	},

	/**
	 * Get a string representation of the instance.
	 * @function module:wc/ui/subordinate.toString
	 * @returns {String} String representation of this object for diagnostic purposes.
	 */
	toString: function() {
		return JSON.stringify(elementToRuleMap);
	},

	/**
	 * Public only for unit testing purposes. Ugly I know, but the ends justify the means.
	 * @ignore
	 */
	_isConditionTrue: isConditionTrue
};

/**
 * Takes an array of uninitialized actions and returns a new array containing corresponding initialized
 * actions. This is used to convert actions from their DTO representation to the actual data structure we
 * want to use for the rest of the subordinate lifecycle.
 * @function
 * @private
 * @param {module:wc/ui/SubordinateAction~ActionDTO[]} actions The actions for this subordinate control.
 * @returns {Action[]} The initialized actions as an array. Null if no actions. An
 *    empty array if actions is an empty array or consists only on unparseable action objects.
 */
function initializeActions(actions) {
	let result = null;
	if (actions) {
		result = [];
		for (const element of actions) {
			try {
				let action = new Action(element);
				if (action) {
					result[result.length] = action;
				} else {
					console.warn("Could not parse", element);
				}
			} catch (ex) {
				console.warn(ex, element);
			}
		}
	}
	return result;
}

/**
 * Get the rules in which this element is a controller.
 * @function
 * @private
 * @param {Element} element The element whose rules we will retrieve (if they exist).
 * @param {boolean} [checkAncestors] If true will search ancestors for rules if there
 * are no rules registered directly against this element
 * @returns {Array} The subordinate rules for which the element is a controller.
 */
function getControlledRules(element, checkAncestors) {
	let ruleIds = elementToRuleMap[element.id];
	if (!ruleIds && element.hasAttribute("name")) {
		ruleIds = elementToRuleMap[element.getAttribute("name")];
	}
	if (ruleIds) {
		return  ruleIds.map(/** @param {string} ruleId */ ruleId => ruleStore[ruleId]);
	} else if (checkAncestors && (element = element.parentElement)) {
		return  getControlledRules(element, checkAncestors);
	}
	return [];
}

/**
 * The primary entry point into the subordinate logic. Can be called with any element whether it is a
 * subordinate trigger or not. This function will determine if the element is a subordinate trigger and if
 * so will activate the associated rules by testing their conditions and invoking the appropriate
 * subordinate actions based on the outcome of the tests.
 * @function
 * @private
 * @param {Element} element A potential subordinate trigger.
 */
function activateSubordinateRules(element) {
	const controlledRules = getControlledRules(element, true);
	if (controlledRules) {
		for (const rule of controlledRules) {
			let rulePassed = rule.test(isConditionTrue);
			let actions = (rulePassed ? rule.onTrue : rule.onFalse) || [];
			actions.forEach(action => {
				try {
					action.execute();
				} catch (ex) {
					console.error(`${ex} executing subordinate action for rule ${rule.id}`);
				}
			});
		}
	}
}

/**
 * Get the element for this identifier. When the subordinate rule is created the app developer is only aware
 * that an input field has an identifier - they do not know if this identifier translates to an 'id' or a
 * 'name' attribute so we need to check both.
 *
 * @function
 * @private
 * @param {String} identifier The element id or name. Note that only input fields can legally have a name.
 * @returns {HTMLElement} The first element with the name identifier or the element with the id identifier.
 *   Tests name first since a grouped component (such as a WRadioButtonSelect) will have the name on each
 *   option (radio button) AND the id on the wrapper (fieldset) and in this class we are interested in the
 *   controls not the wrappers.
 */
function getElement(identifier) {
	const namedElements = ['input', 'select', 'textarea', 'button'];
	const selector = namedElements.map(tn => `${tn}[name='${identifier}']`).join();
	return document.querySelector(selector) || document.getElementById(identifier);
}

/**
 * Indicates if the test function condition is true.
 *
 * <p>A few notes on intended behaviour:</p>
 * <ul><li>If the value of the element we are testing (call this the "triggerVal") is "empty" we only
 * perform equality/inequality tests (including the equality test part of "ge" and "le").
 * In other words we never perform greater than or less than tests on an empty value. The test
 * will always return false in these cases.</li>
 *
 * <li>If the value defined in the rule (call this the "compareVal") is empty it be equal to an empty
 * triggerVal OR a selection group (such as a dropdown list, radio button group) which has zero
 * selected items. This will be tested if the operator contains an equality test including "ge"
 * and "le".</li>
 *
 * <li>Note that a "disabled control" will always return false, this trumps any other rules. Note also
 * that in a composite control like a radiobutton group we consider the control disabled if every
 * option (radio button) is disabled.</li>
 *
 * <li>In the case of "typed fields" (i.e. date field, number field) any tests against these fields
 * will always return false if their value (the triggerVal) is not valid "whole" OR the rule
 * value (the compareVal) is not "whole".
 * For this purpose:
 * <ul><li>Whole, for a number, means it is numeric and can be parsed to a number.</li>
 *     <li>Whole, for a date, means it contains day month and year values.</li></ul></li>
 *
 * <li>In the case of regular expression matching the only supported flag is "case-insensitive". This
 * flag is off by default but can be turned on by prefixing the pattern with (?i).</li></ul>
 *
 * @function
 * @private
 * @param {String} id The identifier of the test subject - could be: an element id, or an element name
 * @param {string} testValue If the test subject matches this value we return true. What "matches this value" means
 * @param {string} [operator] The type of comparison to perform to determine if the condition is true
 *    really depends on what the test subject is. For example if it is a text input then we return true if the
 *    value of the input matches the testValue.
 * @returns {boolean} true if the condition is true otherwise false.
 */
function isConditionTrue(id, testValue, operator) {
	let result = false;
	const element = getElement(id);
	if (element && !shed.isDisabled(element)) {
		let selectedItems;
		if (shed.isSelectable(element) || element.matches("fieldset")) {
			selectedItems = getSelectedOptions(element);
		}
		if (selectedItems) {
			if (selectedItems.length > 0) {
				result = testElementValue(selectedItems, testValue, operator);
			}
			if (!result) {
				/*
				 * In theory we would need to check that not every option in the selectable list
				 * is disabled (which would amount to the control being disabled and always return false).
				 *
				 * In practice we should not need to test for this because:
				 * - If the element itself if one of the options in the list (e.g. a radio button) then we
				 * know it is enabled because we have already rejected disabled elements at the start of
				 * this function.
				 *
				 * - If the element itself DEFINES the group (e.g. a fieldset around a radiobutton group)
				 * then it would never end up in here because of the way the event listeners / shed
				 * subscribers are wired up.
				 */
				let testType = getTestType(operator);
				if (testType.equalityTest) {
					doEqualityTest(element, testValue, testType.negate, selectedItems);
				}
			}
		} else {
			// we are not dealing with a selectable group , just test element
			result = testElementValue(element, testValue, operator);
		}
	} else if (!element) {
		console.warn("Could not find element ", id);
	}
	return result;
}

/**
 * Helper for isConditionTrue.
 * Performs an equality test when there are selectable options at play.
 *
 * @param {Element} element The test subject.
 * @param {string} testValue The test value as passed to `isConditionTrue`.
 * @param {boolean} negate true if the equality test is a "not equal" type.
 * @param {Element[]} selectedItems A collection of selected items related to this element.
 * @returns {boolean} true if the test passes.
 */
function doEqualityTest(element, testValue, negate, selectedItems) {
	/*
	 * A note on the "special case" values.
	 * It is possible for legitimate checkbox values to accidentally match one
	 * of these special cases. This could cause undesired behavior. For example
	 * if a checkbox has the value "false" and a rule is testing for "false" it will
	 * ALWAYS be true (true when checked because values match, true when unchecked
	 * because of the special case).
	 *
	 * The way WComponents works this conflict is not going to happen.
	 */
	if (selectedItems.length === 0) {
		if (isEmpty(testValue) || testValue === "false") {  // we know element is not selected
			return !negate;
		}
	} else if (testValue === "true" || testValue === "false") {
		if ((shed.isSelected(element) + "") === testValue) {
			return  !negate;
		}
	}
	return false;
}

/**
 * Helper for isConditionTrue.
 * Determines if this operator is an equality test and if the result should be negated.
 * The result object will have a `negate` property and an `equalityTest` property.
 * @param {string} operator
 * @return {{negate: boolean, equalityTest: boolean}}
 */
function getTestType(operator) {
	let result = {
		negate: false,
		equalityTest: !operator
	};
	switch (operator) {  // work out if we are doing an equality test
		case "ne":
			result.negate = true;
			/* falls through */
		case "eq":
		case "le":
		case "ge":
			result.equalityTest = true;
			break;
	}
	return result;
}

/**
 * Gets the selected options from a selectable element or group.
 * @function
 * @private
 * @param {Element} element An element which might belong to or define a selectable group.
 * @returns {HTMLElement[]} An array (not a NodeList) of selected options in this group OR null if this element
 * is not any part of any kind of selection mechanism (for example a text input would return null, while an
 * unchecked checkbox would return an empty array).
 */
function getSelectedOptions(element) {
	let gfg = getFilteredGroup,
		gfgConf = { filter: gfg.FILTERS.selected + gfg.FILTERS.enabled, asObject: true };

	/* date field contains one selectable option: the calendar launch
	 * button. But this selectable option cannot be the subordinate
	 * condition determinant; therefore dateField needs to be
	 * explicitly excluded from a test of having selected options.*/
	if (!(dateField?.isOneOfMe(element))) {
		if (multiSelectPair?.isOneOfMe(element)) {
			return Array.from(multiSelectPair.getValue(element));
		}
		const group = gfg(element, gfgConf);
		if (group["filtered"].length || group["unfiltered"].length) {
			return group["filtered"];
		}
		if (shed.isSelectable(element)) {
			// we are dealing with a something that is not an intrinsic part of a selectable group
			// but it might still be a standalone selection mechanism and for subordinate that is
			// good enough
			return shed.isSelected(element) ? [element] : group["filtered"];
		}
	}
	return null;  // flag that this element simply has nothing to do with selection of any type
}

/**
 * Tests the "current value" of the element (however that is to be determined) to see if it compares to
 * "testVal" using the comparison type specified by "operator".
 * @function
 * @private
 * @param {Element|HTMLElement[]} elements Either a single element or an array of them.
 * @param {String} testVal The value we are testing against.
 * @param {String} operator The type of comparison to apply.
 * @returns {Boolean} true if one or more of the elements matches the comparison test.
 */
function testElementValue(elements, testVal, operator) {
	let result = false,
		i = 0;
	const asGroup = Array.isArray(elements);
	do {
		let next = asGroup ? elements[i++] : elements;
		let type = getCompareType(next);
		let triggerVal = getTriggerValue(next, type);
		let compareVal = getCompareValue(testVal, (operator === "rx") ? operator : type);
		if (compareVal !== null && triggerVal !== null) {
			result = doTest(triggerVal, operator, compareVal);
		}
	}
	while (asGroup && !result && i < elements.length);
	return result;
}

/**
 * Helper for testElementValue, implements the actual test itself.
 * @private
 * @function
 * @param {string} triggerVal The value of the subordinate trigger.
 * @param {string} operator The type of test to perform.
 * @param {string} compareVal The value to compare against.
 * @returns {Boolean} the result of the test.
 */
function doTest(triggerVal, operator, compareVal) {
	let result, typedCompedVal;
	switch (operator) {
		case "le":
			result = (triggerVal === compareVal);  // strict equality test
			/* falls through */
		case "lt":
			if (!(isEmpty(triggerVal) || isEmpty(compareVal))) {
				result ||= (triggerVal < compareVal);
			}
			break;
		case "ge":
			result = (triggerVal === compareVal);  // strict equality test
			/* falls through */
		case "gt":
			if (!(isEmpty(triggerVal) || isEmpty(compareVal))) {
				result ||= (triggerVal > compareVal);
			}
			break;
		case "rx":
			typedCompedVal = parseRegex(compareVal);
			result = typedCompedVal.test(triggerVal);
			break;
		case "ne":
			result = (triggerVal !== compareVal);
			break;
		default:
			result = (triggerVal === compareVal);
			break;
	}
	return result;
}

/**
 * Is an element's value "empty"?
 * @function
 * @private
 * @param {String} val The value to check.
 * @returns {Boolean} true if val is an empty string (zero length or just whitespace).
 */
function isEmpty(val) {
	let result = !!val?.length;
	if (result && typeof val === "string") {
		result = !val.trim();
	}
	return result;
}

/**
 * Gets a value of the 'correct' type based on a component value.
 * @function
 * @private
 * @param {string} val The value to convert.
 * @param {string} type The required type.
 * @returns {string} The correct value of the correct type (based on the "type" arg). If the value is SOMETHING
 * (not an empty-ish string) but that something is not correctly formatted for the "type" then we return
 * null which essentially means "invalid" and no comparisons can be done.
 */
function getCompareValue(val, type) {
	let result;
	if (type === "rx") {
		result = val;
	} else if (type === "date") {
		result = getDateCompareValue(val);
	} else if (type === "number") {
		result = getNumberCompareValue(val);
	} else {
		result = val;
	}
	return result;
}

/**
 * Helper for getCompareValue.
 * @param {string} val The value to convert.
 * @returns {string|null} The correct value when comparing dates.
 */
function getDateCompareValue(val) {
	if (val === "" || interchange.isComplete(val)) {
		return val;
	}
	console.warn("Date is not complete", val);
	return null;
}

/**
 * Helper for getCompareValue.
 * @param {string} val The value to convert.
 * @returns {string} The correct value when comparing numbers.
 */
function getNumberCompareValue(val) {
	let result = val;
	if (val !== "") {
		if (isNaN(Number(val))) {  // if the result is NaN we can't use it
			console.warn("Can not parse to a number", val);
			result = null;
		}
	}
	return result;
}

/**
 * Get the value of the element that triggered the subordinate rule.
 * @function
 * @private
 * @param {Element} element An element which has some logical value which we want to get.
 * @param {string} [type] The type for the element we are dealing with, i.e. "number" or "date". If not
 *    provided we will try a few things then give up.
 * @returns {string|null} The correct value of the correct type (based on the "type" arg). If the value is SOMETHING
 *   (not an empty-ish string) but that something is not correctly formatted for the "type" then we return
 *   null which essentially means "invalid" and no comparisons can be done.
 */
function getTriggerValue(element, type) {
	let result;
	if (type === "date") {
		if (dateField?.isOneOfMe(element)) {
			result = dateField.getValue(element);
		}
		if (result !== "" && !interchange.isComplete(result)) {
			return null;
		}
		return result;
	}

	if (type === "number") {
		result = element["value"];
		if (isNaN(Number(result))) {  // if the result is NaN we can't use it ( btw Number("") is 0 )
			result = null;
		}
		return result;
	}
	// don't check element.text, it is not necessary on option elements from IE8 up and is actually harmful because it will bypass a legit value attribute that equates to false (empty string)
	return (element["value"] || element.getAttribute("data-wc-value") || "");
}

/**
 * Get the type for value comparison.
 * @function
 * @private
 * @param {Element} element The element which will be used to determine the comparison type.
 * @returns {string} The type of comparison to perform based on the "type" of the element.
 */
function getCompareType(element) {
	let result;
	if (dateField?.isOneOfMe(element)) {
		result = "date";
	} else if (element.matches("input[type='number']")) {
		result = "number";
	} else {
		result = "string";
	}
	return result;
}

/**
 * Parse the Java API "Regular Expression" string to a regular expression.
 * @function
 * @private
 * @param {string} re The regular expression sent from the server
 * @returns {RegExp} An instance of RegExp ready for your pattern matching pleasure. Will return null if the
 *    regex was empty (valid but stupid) or could not be parsed (invalid or really stupid).
 */
function parseRegex(re) {
	let result = null;
	if (re) {
		result = regexCache[re];
		if (!(result?.hasOwnProperty(re))) {
			try {
				if (re.startsWith("(?i)")) {
					result = new RegExp(re.substring(4), "i");
				} else {
					result = new RegExp(re);
				}
				regexCache[re] = result;
			} catch (ex) {
				console.error("Error parsing regexp", re, ex);
			}
		}
	}
	return result;
}

/**
 * Listen for shed "events" that could be subordinate triggers.
 * @function
 * @private
 * @param {Element} element The element to check.
 */
function shedObserver(element) {
	timers.setTimeout(activateSubordinateRules, 0, element);
}

/**
 * Check if the element SHOULD ACTIVATE a suboridnate action.
 * @function
 * @private
 * @param {Element} element The element to check.
 */
function triggersOnChange(element) {
	return getControlledRules(element, false) && !shed.isSelectable(element);
}

/**
 * Handle the change event - determine if a subordinate trigger has changed.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event The event that has fired.
 */
function changeEvent($event) {
	let element = $event.target;
	if (!$event.defaultPrevented && element) {  // strictly speaking change event can not be cancelled
		if (element.matches("option")) {  // FF with <select multiple="multiple">
			element = element.closest("select");
		}
		if (triggersOnChange(element)) {
			timers.setTimeout(activateSubordinateRules, 0, element);
		}
	}
}


const initialiser = {
	/**
	 * Wire up the necessary event listeners once the dom has loaded.
	 * @function module:wc/ui/subordinate.initialise
	 * @public
	 * @param {Element} element The body element.
	 */
	initialise: element => {
		waitingForRules = true;
		// always require these deps, even if there are no rules, because there are other public methods that need them
		const deps = ["wc/dom/event.mjs", "wc/ui/dateField.mjs", "wc/ui/multiSelectPair.mjs"];
		return Promise.all(deps.map(dep => import(dep))).then(([$event, $dateField, $multiSelectPair]) => {
			event = $event.default;
			dateField = $dateField.default;
			multiSelectPair = $multiSelectPair.default;
			if ((Object.keys(elementToRuleMap)).length) {
				waitingForRules = false;
				if (!changeInited) {
					changeInited = true;
					event.add(element, { type: "change", listener: changeEvent, capture: true });
				}
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.DESELECT, shedObserver);
				shed.subscribe(shed.actions.ENABLE, shedObserver);
				shed.subscribe(shed.actions.DISABLE, shedObserver);
				console.log("Subordinate rules exist: added listeners");
			}
		});
	}
};

initialise.register(initialiser);
export default instance;

/**
 * @typedef {Object} module:wc/ui/subordinate~registerDTO
 * @property {string} id A unique identifier for the control.
 * @property {Function} test The test function to determine if a rule is true or false. Should return a
 *    boolean (true if true kinda makes sense). See {@link module:wc/ui/subordinate~isConditionTrue}.
 * @property {Object[]} [onTrue] The rule[s] to run if the condition determined by test is true. Must be present and valid if onFalse is not.
 * @property {Object[]} [onFalse] The rule[s] to run if the condition determined by test is true. Must be present and valid if onTrue is not.
 * @property {string[]} controllers The ids of the elements which are the control triggers.
 */


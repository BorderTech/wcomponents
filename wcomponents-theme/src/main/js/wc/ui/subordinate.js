/**
 * This module provides functionality to allow some components to control some states of other components. It is a
 * pretty rubbish way of doing this foisted upon us by having Java write web UIs. This module <strong>will</strong>
 * cause you pain and suffering. It may result in mental health issues. It is, however, not nearly as bad as it was in
 * the previous iteration.
 *
 * For example, allows us to create a rule so that if the selected radio button in radio group "fubar" has a value of
 * "foo" then show the div with the id of "bar".
 *
 * @module
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/attribute
 * @requires module:wc/date/interchange
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/SubordinateAction
 * @requires module:wc/array/unique
 * @requires module:wc/dom/getAncestorOrSelf
 * @requires module:wc/dom/shed
 * @requires module:wc/timers
 * @requires module:wc/array/toArray
 * @todo Check source order.
 */
define(["wc/dom/tag",
		"wc/dom/attribute",
		"wc/date/interchange",
		"wc/dom/getFilteredGroup",
		"wc/dom/initialise",
		"wc/ui/SubordinateAction",
		"wc/array/unique",
		"wc/dom/getAncestorOrSelf",
		"wc/dom/shed",
		"wc/timers",
		"wc/array/toArray"],
	/** @param tag wc/dom/tag @param attribute wc/dom/attribute @param interchange wc/date/interchange @param getFilteredGroup wc/dom/getFilteredGroup @param initialise wc/dom/initialise @param Action wc/ui/SubordinateAction @param unique wc/array/unique @param getAncestorOrSelf wc/dom/getAncestorOrSelf @param shed wc/dom/shed @param timers wc/timers @param toArray wc/array/toArray @ignore */
	function(tag, attribute, interchange, getFilteredGroup, initialise, Action, unique, getAncestorOrSelf, shed, timers, toArray) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/subordinate~Subordinate
		 * @private
		 */
		function Subordinate() {
			var event,
				dateField,
				numberField,
				multiSelectPair,
				ruleStore = {},  // stores the rule objects against their rule id
				elementToRuleMap = {},  // maps dom element ids to rule ids
				regexCache = {},  // cache any dynamically created RegExp instances we may need repeatedly
				waitingForRules = false,  // flag if we don't add event listeners when dom is loaded
				changeInited = false,
				BOOTSTRAPPED = "wc/ui/subordinate.bootstrapped";  // ie8 change event bootstrapped flag

			/**
			 * Takes an array of uninitialized actions and returns a new array containing corresponding initialized
			 * actions. This is used to convert actions from their DTO representation to the actual data structure we
			 * want to use for the rest of the subordinate lifecycle.
			 * @function
			 * @private
			 * @param {module:wc/ui/SubordinateAction~ActionDTO[]} actions The actions for this subordinate control.
			 * @returns {?"wc/ui/SubordinateAction"[]} The initialised actions as an array. Null if no actions. An
			 *    empty array if actions is an empty array or consists only on unparseable action objects.
			 */
			function initializeActions(actions) {
				var result = null,
					i,
					len,
					action;
				if (actions) {
					result = [];
					for (i = 0, len = actions.length; i < len; i++) {
						try {
							if ((action = new Action(actions[i]))) {
								result[result.length] = action;
							}
							else {
								console.warn("Could not parse", actions[i]);
							}
						}
						catch (ex) {
							console.warn(ex, actions[i]);
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
				var result,
					ruleIds = elementToRuleMap[element.id] || elementToRuleMap[element.name];
				if (ruleIds) {
					result = ruleIds.map(function(ruleId) {
						return ruleStore[ruleId];
					});
				}
				else if (checkAncestors && (element = element.parentNode)) {
					result = getControlledRules(element, checkAncestors);
				}
				return result;
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
				var i, j, jLen, actions, len, rule, rulePassed,
					controlledRules = getControlledRules(element, true);
				if (controlledRules) {
					for (i = 0, len = controlledRules.length; i < len; i++) {
						rule = controlledRules[i];
						rulePassed = rule.test(isConditionTrue);
						actions = rulePassed ? rule.onTrue : rule.onFalse;
						if (actions) {
							for (j = 0, jLen = actions.length; j < jLen; j++) {
								try {
									actions[j].execute();
								}
								catch (ex) {
									console.error(ex + " executing subordinate action for rule " + rule.id);
								}
							}
						}
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
			 * @returns {?Element} The first element with the name identifier or the element with the id identifier.
			 *   Tests name first since a grouped component (such as a WRadioButtonSelect) will have the name on each
			 *   option (radio button) AND the id on the wrapper (fieldset) and in this class we are interested in the
			 *   controls not the wrappers.
			 */
			function getElement(identifier) {
				var result = document.getElementsByName(identifier);
				if (result.length && result[0].tagName === tag.INPUT) {// gebn always returns a nodelist even if nothing found
					result = result[0];
				}
				else {
					result = document.getElementById(identifier);
				}
				return result;
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
			 * <ul><li>Whole for a number means it is numeric and can be parsed to a number.</li>
			 *     <li>Whole for a date means it contains day month and year values.</li></ul></li>
			 *
			 * <li>In the case of regular expression matching the only supported flag is "case insensitive". This
			 * flag is off by default but can be turned on by prefixing the pattern with (?i).</li></ul>
			 *
			 * @function
			 * @private
			 * @param {String} id The identifier of the test subject - could be: an element id, or an element name
			 * @param {*} testValue If the test subject matches this value we return true. What "matches this value" means
			 * @param {String} [operator] The type of comparison to perform to determine if the condition is true
			 *    really depends on what the test subject is. For example if it is a text input then we return true if the
			 *    value of the input matches the testValue.
			 * @returns {Boolean} true if the condition is true otherwise false.
			 */
			function isConditionTrue(id, testValue, operator) {
				var result = false,
					testType = getTestType(operator),
					element = getElement(id),
					selectedItems;
				if (element && !shed.isDisabled(element)) {
					if (shed.isSelectable(element) || element.tagName === tag.FIELDSET) {
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
							if (testType.equalityTest) {
								doEqualityTest(element, testValue, testType.negate, selectedItems);
							}
						}
					}
					else {
						// we are not dealing with a selectable group , just test element
						result = testElementValue(element, testValue, operator);
					}
				}
				else if (!element) {
					console.warn("Could not find element ", id);
				}
				return result;
			}

			/**
			 * Helper for isConditionTrue.
			 * Performs an equality test when there are selectable options at play.
			 *
			 * @param element The test subject.
			 * @param testValue The test value as passed to `isConditionTrue`.
			 * @param negate true if the equality test is a "not equal" type.
			 * @param {NodeList} selectedItems A collection of selected items related to this element.
			 * @returns {boolean} true if the test passes.
			 */
			function doEqualityTest(element, testValue, negate, selectedItems) {
				var result = false;
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
						result = !negate;
					}
				}
				else if (testValue === "true" || testValue === "false") {
					if ((shed.isSelected(element) + "") === testValue) {
						result = !negate;
					}
				}
				return result;
			}

			/**
			 * Helper for isConditionTrue.
			 * Determines if this operator is an equality test and if the result should be negated.
			 * The result object will have a `negate` property and an `equalityTest` peroperty.
			 * @param {String} operator
			 * @function
			 * @private
			 */
			function getTestType(operator) {
				var result = {
					negate: false
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
					default:
						result.equalityTest = !operator;
						break;
				}
				return result;
			}

			/**
			 * Gets the selected options from a selectable element or group.
			 * @function
			 * @private
			 * @param {Element} element An element which might belong to or define a selectable group.
			 * @returns {?Element[]} An array (not a NodeList) of selected options in this group OR null if this element
			 * is not any part of any kind of selection mechanism (for example a text input would return null, while an
			 * unchecked checkbox would return an empty array).
			 */
			function getSelectedOptions(element) {
				var result,
					gfg = getFilteredGroup,
					gfgConf = {filter: gfg.FILTERS.selected + gfg.FILTERS.enabled, asObject: true},
					group;

				/* date field contains one selectable option: the calendar launch
				 * button. But this selectable option cannot be the subordinate
				 * condition determinant; therefore dateField needs to be
				 * explicitly excluded from a test of having selected options.*/
				if (!(dateField && dateField.isOneOfMe(element))) {
					if (multiSelectPair && multiSelectPair.isOneOfMe(element)) {
						result = multiSelectPair.getValue(element);
						result = toArray(result);
					}
					else if ((group = gfg(element, gfgConf)) && (group.filtered.length || group.unfiltered.length)) {
						result = group.filtered;
					}
					else if (shed.isSelectable(element)) {
						// we are dealing with a something that is not an intrinsic part of a selectable group
						// but it might still be a standalone selection mechanism and for subordinate that is
						// good enough
						result = shed.isSelected(element) ? [element] : group.filtered;
					}
					else {
						result = null;  // flag that this element simply has nothing to do with selection of any type
					}
				}
				return result;
			}

			/**
			 * Tests the "current value" of the element (however that is to be determined) to see if it compares to
			 * "testVal" using the comparison type specified by "operator".
			 * @function
			 * @private
			 * @param {(Element|Element[])} elements Either a single element or an array of them.
			 * @param {String} testVal The value we are testing against.
			 * @param {String} operator The type of comparison to apply.
			 * @returns {Boolean} true if one or more of the elements matches the comparison test.
			 */
			function testElementValue(elements, testVal, operator) {
				var type, triggerVal, next, compareVal,
					result = false,
					i = 0,
					asGroup = Array.isArray(elements);
				do {
					next = asGroup ? elements[i++] : elements;
					type = getCompareType(next);
					triggerVal = getTriggerValue(next, type);
					compareVal = getCompareValue(testVal, (operator === "rx") ? operator : type);
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
			 * @param triggerVal The value of the subordinate trigger.
			 * @param operator The type of test to perform.
			 * @param compareVal The value to compare against.
			 * @returns {Boolean} the result of the test.
			 */
			function doTest(triggerVal, operator, compareVal) {
				var result;
				switch (operator) {
					case "le":
						result = (triggerVal === compareVal);  // strict equality test
						/* falls through */
					case "lt":
						if (!(isEmpty(triggerVal) || isEmpty(compareVal))) {
							result |= (triggerVal < compareVal);
						}
						break;
					case "ge":
						result = (triggerVal === compareVal);  // strict equality test
						/* falls through */
					case "gt":
						if (!(isEmpty(triggerVal) || isEmpty(compareVal))) {
							result |= (triggerVal > compareVal);
						}
						break;
					case "rx":
						result = compareVal.test(triggerVal);
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
				var trimmed,
					result = false;
				if (val.constructor === String) {
					trimmed = val.trim();
					result = !trimmed;
				}
				return result;
			}

			/**
			 * Gets a value of the 'correct' type based on a component value.
			 * @function
			 * @private
			 * @param {String} val The value to convert.
			 * @param {String} type The required type.
			 * @returns {?*} The correct value of the correct type (based on the "type" arg). If the value is SOMETHING
			 * (not an empty-ish string) but that something is not correctly formatted for the "type" then we return
			 * null which essentially means "invalid" and no comparisons can be done.
			 */
			function getCompareValue(val, type) {
				var result;
				if (type === "rx") {
					result = parseRegex(val);
				}
				else if (type === "date") {
					result = getDateCompareValue(val);
				}
				else if (type === "number") {
					result = getNumberCompareValue(val);
				}
				else {
					result = val;
				}
				return result;
			}

			/**
			 * Helper for getCompareValue.
			 * @param val The value to convert.
			 * @returns The correct value when comparing dates.
			 */
			function getDateCompareValue(val) {
				var result;
				if (val === "" || interchange.isComplete(val)) {
					result = val;
				}
				else {
					console.warn("Date is not complete", val);
					result = null;
				}
				return result;
			}

			/**
			 * Helper for getCompareValue.
			 * @param val The value to convert.
			 * @returns The correct value when comparing numbers.
			 */
			function getNumberCompareValue(val) {
				var result;
				if (val !== "") {
					result = parseFloat(val);
					if (isNaN(result)) {// if the result is NaN we can't use it
						console.warn("Can not parse to a number", val);
						result = null;
					}
				}
				else {
					result = val;
				}
				return result;
			}

			/**
			 * Get the value of the element that triggered the subordinate rule.
			 * @function
			 * @private
			 * @param {Element} element An element which has some logical value which we want to get.
			 * @param {String} [type] The type for the element we are dealing with, i.e. "number" or "date". If not
			 *    provided we will try a few things then give up.
			 * @returns {?*} The correct value of the correct type (based on the "type" arg). If the value is SOMETHING
			 *   (not an empty-ish string) but that something is not correctly formatted for the "type" then we return
			 *   null which essentially means "invalid" and no comparisons can be done.
			 */
			function getTriggerValue(element, type) {
				var result;
				if (type === "date") {
					if (dateField && dateField.isOneOfMe(element)) {
						result = dateField.getValue(element);
					}
					if (result !== "" && !interchange.isComplete(result)) {
						return null;
					}
					return result;
				}

				if (type === "number") {
					if (numberField) {
						result = numberField.getValueAsNumber(element);
					}
					if (result !== "" && isNaN(result)) { // if the result is NaN we can't use it
						result = null;
					}
					return result;
				}
				// don't check element.text, it is not necessary on option elements from IE8 up and is actually harmful because it will bypass a legit value attribute that equates to false (empty string)
				return (element.value || element.getAttribute("data-wc-value") || "");
			}

			/**
			 * Get the type for value comparison.
			 * @function
			 * @private
			 * @param {Element} element The element which will be used to determine the comparison type.
			 * @returns {String} The type of comparison to perform based on the "type" of the element.
			 */
			function getCompareType(element) {
				var result;
				if ((dateField && dateField.isOneOfMe(element))) {
					result = "date";
				}
				else if (numberField && numberField.isOneOfMe(element)) {
					result = "number";
				}
				else {
					result = "string";
				}
				return result;
			}

			/**
			 * Parse the Java API "Regular Expression" string to a regular expression.
			 * @function
			 * @private
			 * @param {String} re The regular expression sent from the server
			 * @returns {?RegExp} An instance of RegExp ready for your pattern matching pleasure. Will return null if the
			 *    regex was empty (valid but stupid) or could not be parsed (invalid or really stupid).
			 */
			function parseRegex(re) {
				var result = null;
				if (re) {
					result = regexCache[re];
					if (!(result && result.hasOwnProperty(re))) {
						try {
							if (re.indexOf("(?i)") === 0) {
								result = new RegExp(re.substring(4), "i");
							}
							else {
								result = new RegExp(re);
							}
							regexCache[re] = result;
						}
						catch (ex) {
							console.error("Error parsing regexp", re, ex);
						}
					}
				}
				return result;
			}

			/**
			 * Wire up the necessary event listeners once the dom has loaded.
			 * @function module:wc/ui/subordinate.initialise
			 * @public
			 * @param {Element} element The body element.
			 */
			this.initialise = function(element) {
				waitingForRules = true;
				// always require these deps, even if there are no rules, because there are other public methods that need them
				require(["wc/dom/event", "wc/ui/dateField", "wc/ui/numberField", "wc/ui/multiSelectPair"], function($event, $dateField, $numberField, $multiSelectPair) {
					event = $event;
					dateField = $dateField;
					numberField = $numberField;
					multiSelectPair = $multiSelectPair;
					if ((Object.keys(elementToRuleMap)).length) {
						waitingForRules = false;
						if (!changeInited) {
							changeInited = true;
							if (event.canCapture) {
								event.add(element, event.TYPE.change, changeEvent, null, null, true);
							}
							else {
								event.add(element, event.TYPE.focusin, focusEvent);
							}
						}
						shed.subscribe(shed.actions.SELECT, shedObserver);
						shed.subscribe(shed.actions.DESELECT, shedObserver);
						shed.subscribe(shed.actions.ENABLE, shedObserver);
						shed.subscribe(shed.actions.DISABLE, shedObserver);
						console.log("Subordinate rules exist: added listeners");
					}
				});
			};

			/**
			 * Bootstrapping function to wire up change event for browsers which do not support capture.
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 */
			function focusEvent($event) {
				var target, bootstrapped;
				if (!$event.defaultPrevented) {
					target = $event.target;
					if (target && triggersOnChange(target)) {
						bootstrapped = attribute.get(target, BOOTSTRAPPED);
						if (!bootstrapped) {
							attribute.set(target, BOOTSTRAPPED, true);
							event.add(target, event.TYPE.change, changeEvent);
						}
					}
				}
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
			 * @param {Event} $event The event that has fired.
			 */
			function changeEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && element) {  // strictly speaking change event can not be cancelled
					if (element.tagName === tag.OPTION) {// FF with <select multiple="multiple">
						element = getAncestorOrSelf(element, tag.SELECT);
					}
					if (triggersOnChange(element)) {
						timers.setTimeout(activateSubordinateRules, 0, element);
					}
				}
			}

			/**
			 * Register one or more component groups as being groups of interest for subordinate actions.
			 * @see {@link module:wc/ui/SubordinateAction.registerGroups}
			 * @function module:wc/ui/subordinate.registerGroup
			 * @param {module:wc/ui/SubordinateAction~groupDTO[]} groups An array where each item represents a
			 *    subordinate component group.
			 */
			this.registerGroups = function(groups) {
				if (groups) {
					Action.registerGroups(groups);
				}
			};

			/**
			 * Register an array of subordinate rules.
			 * @function module:wc/ui/subordinate.register
			 * @param {module:wc/ui/subordinate~registerDTO[]} rules An array of Objects containing configuration
			 *    options.
			 */
			this.register = function(rules) {
				var i,
					len,
					rule,
					ruleId,
					getRule = function(id) {
						if (elementToRuleMap[id]) {
							elementToRuleMap[id][elementToRuleMap[id].length] = ruleId;
						}
						else {
							elementToRuleMap[id] = [ruleId];
						}
					};
				if (rules) {
					for (i = 0, len = rules.length; i < len; i++) {
						rule = rules[i];
						ruleId = rule.id;
						ruleStore[ruleId] = rule;
						// initing the actions could be deferred but then you gots to check if inited forever after
						rule.onTrue = initializeActions(rule.onTrue);
						rule.onFalse = initializeActions(rule.onFalse);
						rule.controllers = unique(rule.controllers);  // the provided list of controllers is not guaranteed to be unique
						rule.controllers.forEach(getRule);
					}
				}
				if (waitingForRules) {
					waitingForRules = false;
					console.log("Rules registered: adding event listeners");
					this.initialise(document.body);
				}
			};

			/**
			 * Get a string representation of the instance.
			 * @function module:wc/ui/subordinate.toString
			 * @returns {String} String representation of this object for diagnostic purposes.
			 */
			this.toString = function() {
				return window.JSON.stringify(elementToRuleMap);
			};

			/**
			 * Public only for unit testing purposes. Ugly I know, but the ends justify the means.
			 * @ignore
			 */
			this._isConditionTrue = isConditionTrue;
		}


		var /** @alias module:wc/ui/subordinate */ instance = new Subordinate();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/subordinate~registerDTO
		 * @property {String} id A unique identifier for the control.
		 * @property {Function} test The test function to determine if a rule is true or false. Should return a
		 *    boolean (true if true kinda makes sense). See {@link module:wc/ui/subordinate~isCOnditionTrue}.
		 * @property {Object[]} [onTrue] The rule[s] to run if the condition determined by test is true. Must be present and valid if onFalse is not.
		 * @property {Object[]} [onFalse] The rule[s] to run if the condition determined by test is true. Must be present and valid if onTrue is not.
		 * @property {String[]} controllers The ids of the elements which are the control triggers.
		 */
	});

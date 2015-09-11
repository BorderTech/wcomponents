/**
 * Provides actions used by {@link module:wc/ui/subordinate}.
 *
 * This module knows how to perform a subordinate action. Has no knowledge of if and when to perform the action.
 * Instances of this class are used to to populate the "onTrue" and "onFalse" properties of a subordinate rule.
 *
 * Once a subordinate condition has been evaluated to either true of false to corresponding action is executed.
 *
 * @module
 * @requires module:wc/ui/fx
 * @requires module:wc/dom/shed
 * @requires module:wc/has
 *
 * @todo Document private members. Work out why Rick has used this labrynthine constructor initialisation mechanism...
 */
define(["wc/ui/fx", "wc/dom/shed", "wc/has"],
	/** @param fx wc/ui/fx @param shed wc/dom/shed @param has wc/has @ignore */
	function(fx, shed, has) {
		"use strict";
		var actionRegister = {},  // Map of subordinate action keywords to functions which implement the action.
			groupRegister = {},
			repainter;

		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		/**
		 * @constructor
		 * @alias module:wc/ui/SubordinateAction
		 * @param {module:wc/ui/SubordinateAction~ActionDTO} dto The object defining the action.
		 * @throws TypeError if the dto is the wrong "duck type"
		 */
		function Action(dto) {
			if (actionRegister.hasOwnProperty(dto.type)) {
				// making the function a property of the action gives the correct scope "for free" (no "call" or "bind")
				this.doToElement = actionRegister[dto.type];
			}
			else {
				throw new TypeError("Not a known action type", dto.type);
			}
			this.targets = [];  // one or more identifiers (ids or group names)

			dto.targets.forEach(function(target) {
				try {
					this.targets.push(new Target(target.id, target.groupId));
				}
				catch (ex) {
					console.warn(ex);
				}
			}, this);
		}

		/**
		 * Register groups of components as a single item which may be acted on by a subordinate action.
		 * @function
		 * @param {module:wc/ui/SubordinateAction~groupDTO[]} groups An array where each item represents a
		 *    subordinate group.
		 */
		Action.registerGroups = function(groups) {
			var group, groupName;
			if (groups) {
				while (groups.length) {
					group = groups.pop();
					groupName = group.name;
					if (groupName) {
						groupRegister[groupName] = group;
					}
					else {
						console.warn("Can not register a group without a name", group);
					}
				}
			}
		};


		/**
		 * Get a registered component group.
		 * @function
		 * @param {String} id The group identifier.
		 * @returns {?String[]} An array of ids belonging to this group if the group exists otherwise returns null.
		 */
		Action.getGroup = function (id) {
			var result = groupRegister[id] || null;
			if (result) {
				result = result.identifiers;  // hmmm, should we return a copy of this instead of the real deal?
			}
			return result;
		};

		/**
		 * Register a subordinate action - this establishes a mapping of an action name to a callback function.
		 * @function
		 * @param {string} name The name of the subordinate action. This is used in rules to invoke this callback.
		 * @param {Function} callback The function that implements this action. Will be passed the Element to perform the action on.
		 */
		Action.register = function(name, callback) {
			actionRegister[name] = callback;  // TODO should we check if the name is already used?
		};

		/**
		 * Represents the target of an action.
		 * Each action can have multiple targets.
		 * @constructor module:wc/ui/SubordinateAction~Target
		 * @private
		 * @param {String} [id] The id of the target. Must be truthy if groupId is not truthy.
		 * @param {String} [groupId] The id of the target group.  Must be truthy if id is not truthy.
		 * @throws {TypeError} if id and groupId are both falsey.
		 */
		function Target(id, groupId) {
			// filtering out empty string. the id will always be a string and therefore never null, undefined or zero
			this.id = id || null;
			this.groupId = groupId || null;
			if (!id && !groupId) {
				throw new TypeError("Action target must have an id or a groupId");
			}
		}

		/**
		 * Initialize the Target "class" - set up its prototype etc.
		 * @function initTargetConstructor
		 * @private
		 */
		function initTargetConstructor() {
			var idsOnly = false;  // if true getGroup returns an array of ids (just a performance optimization for isTargeted)

			/**
			 * Is a particular id a target?
			 * @function module:wc/ui/SubordinateAction~Target#isTargeted
			 * @public
			 * @param {String} id The id we want to test.
			 * @returns {Boolean} true if the id is targeted by this target instance.
			 * Note: will search inside groups - will not test the group name itself.
			 */
			Target.prototype.isTargeted = function(id) {
				var group, result = false;
				try {
					idsOnly = true;
					if (this.id !== null && this.id === id) {
						result = true;
					}
					else if ((group = this.getGroup())) {
						result = group.indexOf(id) >= 0;
					}
				}
				finally {
					idsOnly = false;
				}
				return result;
			};

			/**
			 * Gets a target element.
			 * @function module:wc/ui/SubordinateAction~Target#getElement
			 * @public
			 * @throws {ReferenceError} if the referenced element does not exist
			 * @returns {?Element} The element referenced by this target (if applicable)
			 */
			Target.prototype.getElement = function() {
				var result = null, element;
				if (this.id) {
					if ((element = document.getElementById(this.id))) {
						result = element;
					}
					else {
						throw new ReferenceError("Could not find element " + this.id);
					}
				}
				return result;
			};

			/**
			 * Get a targetted component group.
			 * @function module:wc/ui/SubordinateAction~Target#getGroup
			 * @public
			 * @throws {ReferenceError} if the referenced group does not exist.
			 * @returns {?Element[]} An array of elements in the group targeted by this instance (if applicable).
			 */
			Target.prototype.getGroup = function() {
				var result = null, next, i, len, group;
				if (this.groupId !== null) {
					if ((group = Action.getGroup(this.groupId))) {
						if (idsOnly) {
							result = group;
						}
						else {
							result = [];
							for (i = 0, len = group.length; i < len; i++) {
								if ((next = document.getElementById(group[i]))) {
									result[result.length] = next;
								}
								else {
									console.warn("Could not find element", group[i]);
								}
							}
						}
					}
					else {
						throw new ReferenceError("Could not find group " + this.groupId);
					}
				}
				return result;
			};
		}

		/**
		 * Initialize the Action "class" - set up its prototype etc.
		 * @function initActionConstructor
		 * @private
		 */
		function initActionConstructor() {
			/**
			 * Is a particular id representative of something that is targetted by this action?
			 * Note: will search inside groups - will not test group names themselves.
			 * @function module:wc/ui/SubordinateTarget#isTargeted
			 * @public
			 * @param {String} id The id we want to test.
			 * @returns {Boolean} true if the id is targeted by this action instance.
			 */
			Action.prototype.isTargeted = function(id) {
				return this.targets.some(function(target) {
					return target.isTargeted(id);
				}, this);
			};

			/**
			 * Exectutes the action defined by this instance.
			 * @function
			 * @public
			 */
			Action.prototype.execute = function() {
				var targets = this.targets, group, i, next, element;
				for (i = 0; i < targets.length; i++) {
					next = targets[i];
					try {
						if ((element = next.getElement())) {
							this.doToElement(element);
						}
						else if ((group = next.getGroup())) {
							group.forEach(this.doToElement, this);
						}
					}
					catch (ex) {
						console.warn(ex);
					}
				}
			};
		}

		/**
		 * Sets up the lookup object which maps subordinate action keywords to functions which implement the action.
		 *
		 * **THIS IS WHERE YOU PLAY IF YOU ARE ADDING NEW ACTIONS OR MODIFYING EXISTING ONES! YOU SHOULD NOT
		 * NEED TO CHANGE ANYTHING IN OTHER OTHER SCOPE.**
		 *
		 * Your action function will be passed one argument: "element" which is the DOM element on which the action
		 * is to be performed. The scope (the "this" keyword) will be the instance of action currently executing.
		 */
		function initActionImplementations() {

			Action.register("hide", hideItem);
			Action.register("show", showItem);
			Action.register("mandatory", makeMandatory);
			Action.register("optional", makeOptional);
			Action.register("disable", disableItem);
			Action.register("enable", enableItem);
			Action.register("select", selectItem);
			Action.register("unselect", unselectItem);
			Action.register("toggleselect", toggleSelect);
			Action.register("showIn", showInGroup);
			Action.register("hideIn", hideInGroup);
			Action.register("enableIn", enableInGroup);
			Action.register("disableIn", disableInGroup);

			function isCheckable(element) {
				var type = element.type;
				return (type === "checkbox" || type === "radio");
			}

//			function isVisible(element)
//			{
//				return !shed.isHidden(element);
//			}
//
//			function isEnabled(element)
//			{
//				return !shed.isDisabled(element);
//			}

			/**
			* doInGroup is a helper for {@link module:wc/ui/SubordinateAction~hideInGroup} and
			* {@link module:wc/ui/SubordinateAction~showInGroup}.
			* @function doInGroup
			* @private
			* @param {Element} element The element "in" the group - i.e. the one we are singling out.
			* @param {Function} func The function to apply to "element".
			* @param {Function} funcToggle The function to apply to all elements in the group that are not "element".
			* @this module:wc/ui/SubordinateAction
			*/
			function doInGroup(element, func, funcToggle) {
				var action = this,
					targets = action.targets,
					group, i, j, next;

				func(element);  // apply the "special treatment" to the "in" element
				for (i = 0; i < targets.length; i++) {  // toggle the rest
					if ((group = targets[i].getGroup())) {
						for (j = 0; j < group.length; j++) {
							if (element !== (next = group[j])) {  // don't toggle the "in" element
								funcToggle.call(this, next);
							}
						}
					}
				}

				if (repainter) {
					repainter.checkRepaint(element);
				}
			}

			/**
			 * Hide a component.
			 * @function hideItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function hideItem(element) {
				shed.hide(element);
				if (repainter) {
					repainter.checkRepaint(element);
				}
			}

			/**
			 * Show a component.
			 * @function showItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function showItem(element) {
				shed.show(element);
				applyEffects(element);
			}

			/**
			 * Make a field optional.
			 * @function makeOptional
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function makeOptional(element) {
				shed.optional(element);
			}

			/**
			 * Make a field mandatory.
			 * @function makeMandatory
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function makeMandatory(element) {
				shed.mandatory(element);
			}

			/**
			 * Disable a component.
			 * @function disableItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function disableItem(element) {
				if (disable(element)) {
					applyEffects(element);
				}
			}

			/**
			 * Enable a component.
			 * @function enableItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function enableItem(element) {
				if (disable(element, true)) {
					applyEffects(element);
				}
			}

			/**
			 * Select a component.
			 * @function selectItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function selectItem(element) {
				if (isCheckable(element)) {
					shed.select(element);
				}
			}

			/**
			 * Deselect a component.
			 * @function unselectItem
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function unselectItem(element) {
				if (isCheckable(element)) {
					shed.deselect(element);
				}
			}

			/**
			 * Toggle the selected state of a component.
			 * @function toggleSelect
			 * @private
			 * @param {Element} element The element to act on.
			 * @todo Why not use {@link module:wc/dom/shed#toggle}?
			 */
			function toggleSelect(element) {
				if (isCheckable(element)) {
					shed[shed.isSelected(element) ? "deselect" : "select"](element);
				}
			}

			/**
			 * Hide a component in a group (and show all others in that group).
			 * @function hideInGroup
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function hideInGroup(element) {
				doInGroup.call(this, element, hideItem, showItem);
			}

			/**
			 * Show a component in a group (and hide all others in that group).
			 * @function showInGroup
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function showInGroup(element) {
				doInGroup.call(this, element, showItem, hideItem);
			}

			/**
			 * Enable a component in a group (and disable all others in that group).
			 * @function enableInGroup
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function enableInGroup(element) {
				doInGroup.call(this, element, enableItem, disableItem);
			}

			/**
			 * Disable a component in a group (and enable all others in that group).
			 * @function showInGroup
			 * @private
			 * @param {Element} element The element to act on.
			 */
			function disableInGroup(element) {
				doInGroup.call(this, element, disableItem, enableItem);
			}

			/**
			 * Disable OR enable the element. Will recursively enable or disable the element's descendants.
			 * @function disable
			 * @private
			 * @param {Element} element The element to enable or disable
			 * @param {Boolean} enable if true the element will be enabled, otherwise it will be disabled
			 * @returns {Boolean} true if the element's disabled state was changed one way or the other
			 */
			function disable(element, enable) {
				var originalState = shed.isDisabled(element);
				if (enable) {
					shed.enable(element);
				}
				else {
					shed.disable(element);
				}
				return (shed.isDisabled(element) !== originalState);
			}

			/**
			 * Apply some kind of visual effect to an element that would otherwise change state "quietly".
			 * @function applyEffects
			 * @private
			 * @param {Element} element The element to whch the effect is applied.
			 */
			function applyEffects(element) {
				if (repainter) {
					repainter.checkRepaint(element);
				}
				fx.yellowFade(element);
			}
		}

		initActionConstructor();  // Set up the prototype chain for Action
		initTargetConstructor();  // Set up the prototype chain for Target
		initActionImplementations();  // Map the named subordinate actions to functions
		return Action;


		/**
		 * @typedef {Object} module:wc/ui/SubordinateAction~ActionDTO
		 * @property {String} type The action type. Must be one of the property names in
		 *    {@link module:wc/ui/SubordinateAction~actionRegister}
		 * @property {Object[]} targets An array of Target definitions
		 * @property {String} [targets.id] The id of an individual target element. Must be truthy if targets.groupId is
		 *    not truthy.
		 * @property {String} [targets.groupId] The id of a target component group. Must be truthy if targets.id is
		 *    not truthy.
		 */

		/**
		 * @typedef {Object} module:wc/ui/SubordinateAction~groupDTO
		 * @property {String} name The group unique identifier.
		 * @property {String[]} identifiers An array of component IDs.
		 */
	});

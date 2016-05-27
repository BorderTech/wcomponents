/**
 * @module
 * @requires module:wc/dom/getAncestorOrSelf
 * @requires module:wc/dom/uid
 *
 * @todo needs to have its functions sorted properly.
 */
define(["wc/dom/getAncestorOrSelf", "wc/dom/uid"], /** @param getAncestorOrSelf wc/dom/getAncestorOrSelf @param uid wc/dom/uid @ignore */ function(getAncestorOrSelf, uid) {
	"use strict";

	/**
	 * Provides a mechanism to describe DOM elements as Widgets. A Widget can answer these common DOM problems:
	 *
	 * * Is this element "one of me"?
	 * * Is this element contained in "one of me"? If so return the containing "me".
	 * * Does this element contain "one (or more) of me"? If so return all descendant instances of "me".
	 *
	 * @constructor
	 * @alias module:wc/dom/Widget
	 *
	 * @param {String} [tagName] element tagName that describes this widget
	 * @param {String|Array} [className] className/s that describe this widget - if it's an array then EVERY className
	 *    must match (i.e. they are ANDed).
	 * @param  {Object} [attributes] A map of attributes that describe this widget, each key is an attribute name, each
	 *    value is an attribute value. E.g.: {type:'button',checked: null}. Note use null to test for the presence of an
	 *    attribute without regard to its value. The value, if supplied, is used as part of a contains selector ([~]].
	 *
	 * @throws {TypeError} Thrown if none of the optional args are defined: we need at least one!
	 *
	 * @example  var myWidget = new Widget("input");
	 * // describes any input element
	 *
	 * @example  var myWidget = new Widget("input","myClass");
	 * // describes any input element with className which includes 'myClass'
	 *
	 * @example var myWidget = new Widget("input","",{"type":"radio"});
	 * // describes any radio button.
	 *
	 * @example var myWidget = new Widget("","",{"role":null});
	 * // describes any element with a role attribute.
	 *
	 * @example var myWidget = new Widget("input","",{type:"checkbox","aria-controls":"id1"});
	 * // describes a checkbox which controls elements including an element with id='id1'
	 */
	function Widget(tagName, className, attributes) {
		// changing any of these properties manually is not supported
		if (!tagName && !className && !attributes) {
			throw new TypeError("Widget needs a tagName/className/attributes");
		}
		/**
		 * The Widget instance's tag name if set.
		 * @var
		 * @type {?String}
		 * @public
		 */
		this.tagName = tagName;
		/**
		 * The Widget instance's class name if set.
		 * @var
		 * @type {?String}
		 * @public
		 */
		this.className = className;
		/**
		 * The Widget instance's attribute selector object if set.
		 * @var
		 * @type {?Object}
		 * @public
		 */
		this.attributes = attributes;
		/**
		 * The Widget instance's start container element. Set per dearch for descendant searches.
		 * @var
		 * @type {?Element}
		 * @public
		 */
		this.container = null;
		/**
		 * Is this widget instance only interested in immediate child elements of container? Set per search.
		 * @var
		 * @type {Boolean}
		 * @public
		 * @default false
		 */
		this.immediate = false;
	}

	/**
	 * Escape a querySelector string portion if it contains any CSS reserved characters.
	 *
	 * A HTML5 id is basically /\S/+. This can do nasty things to querySelector[All] so we escape them here. This is
	 * another good reason to use Widget rather than calling querySelect[All] directly.
	 *
	 * NOTE: at the moment this is only needed for ids which we use when the immediate flag is set. However, we are
	 * in the process of improving the Java render phase to make it much easier to output custom controls including
	 * any HTML element, so we apply this to all qs before we call querySelector[All].
	 *
	 * @function escapeQs
	 * @private
	 * @param {String} qs A CSS string for query selector
	 * @returns {String} qs with potentially hazardous characters escaped.
	 */
	function escapeQs(qs) {
		var RX = /(.)?([?(\$\.\*\#\{\}\[\]\(\):;+~|=>"'])/g,
			result = qs;
		if (RX.test(result)) {
			result = result.replace(RX, function (match, p1, p2) {
				var res = p2, ESC = "\\";
				if (p1 !== ESC) {
					res = ESC + res;
				}
				return p1 + res;
			});
		}
		return result;
	}

	/**
	 * Format this instance as a querySelector / css selector.
	 *
	 * @function toQs
	 * @private
	 * @param {module:wc/dom/Widget} wd An instance of Widget.
	 * @returns {String} A css selector for this widget instance
	 */
	function toQs(wd) {
		var result,
			cn,
			att,
			DOT = ".";
		if (!((result = wd.qs) && wd.hasOwnProperty("qs"))) {
			result = wd.tagName || "";
			cn = wd.className;
			att = wd.attributes;
			if (cn) {
				if (Array.isArray(cn)) {
					cn.forEach(function(nxt) {
						result += (DOT + escapeQs(nxt));
					});
				}
				else {
					result += DOT + cn;
				}
			}
			if (att) {
				result += attributesToQs(att);
			}
			wd.qs = result;  // cache own qs
		}
		if (wd.container) {
			// get container qs
			result = toQs(wd.container) + (wd.immediate ? ">" : " ") + result;
		}
		return result;
	}

	/**
	 * Format a widget's attribute object to a string for querySelector. If the attribute property key is "id" then we
	 * create an id selector (#value) otherwise we create an attribute selector. If the key has value null then the
	 * attribute selector is just the attribute ([key]); if it has a value and the value does not include any spaces we
	 * use an 'any value' attribute selector ([key~'value']), otherwise we use an exact match selector [key='value'].
	 *
	 * @function attributesToQs
	 * @private
	 * @param {Object} attributes Any POJSO but this was designed for the attributes property of a Widget instance.
	 * @returns {String} The attributes converted to a CSS selector.
	 */
	function attributesToQs(attributes) {
		var names = Object.keys(attributes),
			result = "";
		names.forEach(function(name) {
			var val = attributes[name],
				hasVal = (val !== null),
				operator = "=";
			if (hasVal && name === "id") {
				result += "#" + escapeQs(val);
			}
			else {
				if (hasVal && val.indexOf(" ") === -1) {
					operator = "~" + operator;
				}
				result += "[" + escapeQs(name) + (hasVal ? ( operator + "\"" + escapeQs(val) + "\"]") : "]");
			}
		});
		return result;
	}

	/**
	 * Get the widget as a meaningful string.
	 * @function
	 * @public
	 * @returns {String} The query selector string of the Widget.
	 */
	Widget.prototype.toString = function() {
		return toQs(this);
	};

	/**
	 * Determine if an element is a match for the widget instance.
	 * @function
	 * @public
	 * @param {Element} element The element to test
	 * @returns {Boolean} true if element has the requisite form to match the widget.
	 * @example var myWidget = new Widget("input","",{type:"checkbox","aria-controls":"id1"});
	 * // given an element var el representing <input type='checkbox' aria-controls='id0 id1 id3'> then
	 * myWidget.isOneOfMe(el);  // will return true
	 */
	Widget.prototype.isOneOfMe = function(element) {
		return this.findAncestor(element, element.tagName) === element;
	};

	/**
	 * Find an ancestor element (or self) which matches the descriptor.
	 * @function
	 * @public
	 * @param {Element} element The element whose ancestors we are searching.
	 * @param {String} [limitTagName] Optionally stop searching if you reach an ancestor with this tagName.
	 * @param {Boolean} [getTree] if true will return an array where item 0 will be the result for this widget. item 1
	 *    will be the result for this widget's container and so on...
	 * @param {Boolean} [outermost] If true then get the matching ancestor which is furthest from element rather than
	 *    the first matching ancestor.
	 * @returns {?Element} The ancestor element if found or null (null even if getTree true).
	 */
	Widget.prototype.findAncestor = function(element, limitTagName, getTree, outermost) {
		var result = null,
			tree,
			next,
			limit;
		try {
			this.element = element;
			this.limitTagName = limitTagName;
			this.outermost = outermost;
			next = getAncestorOrSelf(this);
			if (next) {
				if (this.container) {
					if (next.parentNode) {
						tree = [next];
						limit = this.immediate ? next.parentNode.tagName : "";
						next = this.container.findAncestor(next.parentNode, limit, getTree);
						if (next) {
							if (getTree) {
								if (next.length) {
									result = tree.concat(next);
								}
							}
							else {
								result = tree[0];
							}
						}
					}
				}
				else {
					result = getTree ? [next] : next;
				}
			}
		}
		finally {
			this.element = null;
			this.limitTagName = null;
			this.outermost = null;
		}
		return result;
	};

	/**
	 * Find all descendant elements which match the descriptor.
	 * @function
	 * @public
	 * @param {Element} element The element whose descendants we are searching.
	 * @param {Boolean} [immediate] If true only searches immediate child nodes.
	 * @returns {NodeList} Array-like collection of descendants
	 */
	Widget.prototype.findDescendants = function(element, immediate) {
		return findDescendantsHelper(this, element, immediate, true);
	};

	/**
	 * Find the first descendant element which matches the descriptor.
	 * @function
	 * @public
	 * @param {Element} element The element whose descendant we are searching.
	 * @param {Boolean} [immediate] Only searches immediate child nodes.
	 * @returns {?Element} The descendant element if found otherwise null.
	 */
	Widget.prototype.findDescendant = function(element, immediate) {
		return findDescendantsHelper(this, element, immediate, false);
	};


	/**
	 * Find descendant element/s which match the descriptor.
	 * @function findDescendantsHelper
	 * @private
	 * @param {(module:wc/dom/Widget|module:wc/dom/Widget[])} widget A single Widget OR an array of Widgets.
	 * @param {Element} element The element whose descendants we are searching.
	 * @param {Booelan} [immediate] If true only searches immediate child nodes.
	 * @param {Boolean} [findAll] If false returns the first match only, toerwise finds all.
	 * @returns {?(NodeList|Element)} Array-like collection of descendants OR, if findAll false, an element.
	 */
	function findDescendantsHelper(widget, element, immediate, findAll) {
		var result,
			next,
			elementId,
			immediatePrefix,
			qs = "",
			idx = 0,
			widgetArray = Array.isArray(widget),
			method = findAll ? "querySelectorAll" : "querySelector";
		if (immediate) {
			elementId = element.id || (element.id = uid());
		}
		do {
			next = widgetArray ? widget[idx++] : widget;
			if (immediate) {
				immediatePrefix = "#" + escapeQs(elementId) + ">";
				if (next.immediate && next.container) {
					if (next.container.isOneOfMe(element)) {
						immediatePrefix = "";
					}
				}
				qs += immediatePrefix + toQs(next);
			}
			else {
				qs += toQs(next);
			}
		}
		while (widgetArray && idx < widget.length && (qs += ","));
		result = element[method](qs);
		return result;
	}


	/**
	 * Reflects the DOM hierarchy in the this Widget. If you have a "container" widget and a "subComponent" widget you
	 * can use this function to declare that relationship.
	 * @function
	 * @public
	 * @param {module:wc/dom/Widget} containerWidget An instance of Widget that "contains" this instance.
	 * @param {Boolean} [immediate] If true then this widget descends immediately from the containerWidget (ie
	 *    containerWidget is immediate parent NOT distant ancestor).
	 */
	Widget.prototype.descendFrom = function (containerWidget, immediate) {
		this.container = containerWidget;
		this.immediate = !!immediate;
	};

	/**
	 * Create "subclass instance" of this widget that adds to the className / attributes.
	 * This allows you to target the widget in special states, for example when it has a
	 * class of "invite" on top of the base definition.
	 * @function
	 * @public
	 * @param {String} [additionalClassName] class name/s to add to the parent className/s.
	 * @param {Object} [additionalAttributes] attribute/s to add to the parent attribute/s.
	 * @returns {module:wc/dom/Widget} A "child" instance of this Widget (treat it like any other instance).
	 * @throws {TypeError} Thrown if neither optiona attributeis present: we need one to do anything!
	 */
	Widget.prototype.extend = function(additionalClassName, additionalAttributes) {
		var result,
			F = function() {},
			key;

		if (additionalClassName || additionalAttributes) {
			F.prototype = this;
			result = new F();
			if (additionalClassName) {
				if (this.className) {
					result.className = [].concat(this.className, additionalClassName);
				}
				else {
					result.className = additionalClassName;
				}
			}
			if (additionalAttributes || this.attributes) {
				result.attributes = {};
				for (key in additionalAttributes) {
					result.attributes[key] = additionalAttributes[key];
				}
				for (key in this.attributes) {
					result.attributes[key] = this.attributes[key];
				}
			}
		}
		else {
			throw new TypeError("You do not need to extend this widget");
		}
		return result;
	};

	/**
	 * Creates a clone of this widget.
	 * @returns {module:wc/dom/Widget} A clone of this widget.
	 */
	Widget.prototype.clone = function() {
		return this.extend("", {});
	};

	/**
	 * Test an element against a Widget (or Widget array) to determine if the element is described by a Widget. This
	 * Allows us to test an element against many Widgets at once rather than having to test individually against many
	 * instances of Widget.
	 * @function
	 * @public
	 * @static
	 * @param {Element} element the element to test.
	 * @param {(module:wc/dom/Widget[]|module:wc/dom/Widget)} widgets An array of Widgets or a single Widget.
	 * @returns {Boolean} True if the element matches one of the Widget instances in the array.
	 */
	Widget.isOneOfMe = function(element, widgets) {
		var result;
		if (Array.isArray(widgets)) {
			result = widgets.some(function(widget) {
				return widget.isOneOfMe(element);
			});
		}
		else {
			result = widgets.isOneOfMe(element);
		}
		return result;
	};

	/**
	 * Find the first descendant element which matches any of an array of widget descriptors.
	 * @function
	 * @public
	 * @static
	 * @param {Element} element the start element
	 * @param {(module:wc/dom/Widget[]|module:wc/dom/Widget)} widgets an array of Widgets.
	 * @param {boolean} [immediate] If true look only in element's child nodes otherwise look in descendant nodes.
	 * @returns {?Element} The first matching descendant element.
	 */
	Widget.findDescendant = function(element, widgets, immediate) {
		return findDescendantsHelper(widgets, element, immediate, false);
	};

	/**
	 * Find the all descendant elements which matches any of an array of widget descriptors.
	 * @function
	 * @public
	 * @static
	 * @param {Element} element The start element.
	 * @param {(module:wc/dom/Widget[]|module:wc/dom/Widget)} widgets an array of Widgets.
	 * @param {Boolean} [immediate] if true look only in element's child nodes otherwise look in descendant nodes.
	 */
	Widget.findDescendants = function(element, widgets, immediate) {
		return findDescendantsHelper(widgets, element, immediate, true);
	};

	/**
	 * Find the nearest or outermost ancestor which matches any of an array of descriptors.
	 * @function
	 * @public
	 * @static
	 * @param {Element} element the reference element.
	 * @param {(module:wc/dom/Widget[]|module:wc/dom/Widget)} widgets an array of Widgets.
	 * @param {Object} [config] A configuration object.
	 * @param {String} [config.limit] tagName for limit tag.
	 * @param {Boolean} [config.tree] return tree from individual widgets.
	 * @param {Boolean} [config.outermost] If true returns the outermost matching ancestor from all of the widgets.
	 * @returns {?(Element|Array)} The ancestor element, if any or the ancestor tree array if config.tree is true.
	 */
	Widget.findAncestor = function(element, widgets, config) {
		var matches = [],
			result,
			i,
			len,
			next,
			ancestor,
			limitTag = null,
			tree = false,
			outermost = false;

		if (!Array.isArray(widgets)) {
			widgets = [widgets];
		}
		if (config) {
			limitTag = config.limit || null;
			tree = config.tree || false;
			outermost = config.outermost || false;
		}
		for (i = 0, len = widgets.length; i < len; ++i) {
			next = widgets[i];
			if ((ancestor = next.findAncestor(element, limitTag, tree, outermost))) {
				matches.push(ancestor);
			}
		}
		if (matches.length) {
			/*
			 * This beautiful little sort function is from http://snipplr.com/view/8763/
			 * and was written by a person with an eye for minimalism by the name of
			 * kouphax: http://snipplr.com/users/kouphax but I want them in reverse order
			 */
			matches.sort(function(a, b) {
				return (a.compareDocumentPosition(b) & 6) - 3;
			});
			if (outermost) {
				matches.reverse();
			}
			result = matches[0];
		}
		return result;
	};

	return Widget;
});

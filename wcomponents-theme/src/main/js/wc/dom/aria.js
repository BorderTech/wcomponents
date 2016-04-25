/**
 * This module wraps the underlying ARIA class (from {@link http://code.google.com/p/aria-toolkit/})
 * which allows us to cut and paste updated ARIA code without losing any of our own  customizations.
 *
 *
 * @module
 * @requires module:wc/loader/resource
 * @requires module:wc/xml/xpath
 *
 *  @license The core functionality of this file is a cut and paste from this project: {@link http://code.google.com/p/aria-toolkit/}
 */
define(["wc/loader/resource", "wc/xml/xpath"], /** @param loader wc/loader/resource @param xpath wc/xml/xpath @ignore */ function(loader, xpath) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/dom/aria~AriaWrapper
	 * @private
	 */
	function AriaWrapper() {
		var $this = this,
			FILE_NAME = "aria-1.rdf",
			i,
			aria,
			next,
			methods = ["getScope", "getMustContain", "getSupported", "getScopedTo", "getScopedBy"];

		loader.preload(FILE_NAME);

		for (i = 0; i < methods.length; i++) {
			next = methods[i];
			$this[next] = wrappedMethodFactory(next);
		}

		/**
		 * Generate a wrapped public method for querying ARIA info.
		 * @function
		 * @private
		 * @param {String} method the name of a method to wrap.
		 * @returns {Function} a wrapped function.
		 */
		function wrappedMethodFactory(method) {
			return function() {
				if (!aria) {
					aria = new ARIA({
						loadXml: loader.load.bind(loader, FILE_NAME),
						query: xpath.query
					});
					$this.SUPPORTED = aria.SUPPORTED;
					$this.REQUIRED = aria.REQUIRED;
				}
				return aria[method].apply(aria, arguments);
			};
		}
	}

	/**
	 * Creating more that one instance of this class is pointless and is considered an error. We are going to
	 * ignore this class as it is just an include.
	 *
	 * @see {@link http://code.google.com/p/aria-toolkit/} for documentation.
	 * @constructor
	 * @private
	 * @alias module:wc/dom/aria~Aria
	 * @param {Object} config An object that provides helpers / data for this class
	 * @ignore
	 */
	function ARIA(config) {
		var $this = this,
			ANCHOR_ONLY_RE = /^.*?#/,
			xmlDoc,
			baseRole,// at time of writing (and probably forever) this will be roleType
			instances = {},
			constructors = {};

		$this.SUPPORTED = 1;
		$this.REQUIRED = 2;

		$this.getScope = getScopeFactory("role:scope");
		$this.getMustContain = getScopeFactory("role:mustContain");
		$this.getScopedTo = getScopedFactory("role:scope");
		$this.getScopedBy = getScopedFactory("role:mustContain");

		/**
		 * Call to perform one-time initialisation routine
		 * @ignore
		 */
		function initialise() {
			if (!baseRole) {
				xmlDoc = config.loadXml();
				buildConstructors();
			}
		}

		/**
		 * Find all the aria attributes supported/required by this element/role.
		 * Note that if role is anything other than a known ARIA role then the supported
		 * attributes will be the global ARIA attributes.
		 * @see http://www.w3.org/TR/wai-aria/states_and_properties#global_states
		 *
		 * @function module:wc/dom/aria~Aria.getSupported
		 * @public
		 * @param {(String|Element)} role An ARIA role or a DOM element.
		 * @returns {Object} an object whose properties are the supported attributes. The values of these properties
		 * will be either SUPPORTED or REQUIRED
		 * @example getSupported("checkbox");
		 * @ignore
		 */
		$this.getSupported = function(role) {
			var result, in$tance, F = function() {};
			initialise();
			if (role) {
				if (role.getAttribute) {
					role = role.getAttribute("role") || baseRole;
				}
			}
			else {
				role = baseRole;
			}
			in$tance = getInstance(role);
			if (in$tance) {
				/*
				 * we could return the actual instance (dangerous)
				 * or a clone (would have to clone it)
				 * or a new object that inherits all the properties
				 */
				F.prototype = in$tance;
				result = new F();
			}
			return result;
		};
		/** @param nodeName Node Name @ignore */
		function getScopedFactory(nodeName) {
			var cache = {};
			/**
			 * Given an ARIA role will find the container role/s (if any) which "contain" this role.
			 *
			 * This is to allow for asymetrical scoping in ARIA. For example, the role
			 * "menubar" is not required to contain anything, therefore:
			 * getMustContain("menubar") returns empty array
			 * However: getScopedTo("menubar") returns ["menuitem", "menuitemcheckbox", "menuitemradio"]
			 * This is useful when trying to determine what a particlar role SHOULD contain, not must
			 * contain (and not CAN contain because anything can contain anything).
			 * @param {string} [role] An ARIA role
			 * @returns {Array} An array of strings representing ARIA roles
			 */
			return function (role) {
				var result, expression;
				if (role) {
					// owl:Class[child::role:scope[@rdf:resource='#role']]/@rdf:ID
					result = cache[role];
					if (!result) {
						initialise();
						expression = "//owl:Class[child::" + nodeName + "[@rdf:resource='#" + role + "']]/@*[local-name()='ID']";
						result = cache[role] = cleanRoles(config.query(expression, false, xmlDoc));
					}
				}
				else {
					throw new TypeError("role can not be null");
				}
				return result;
			};
		}

		/**
		 * Creates methods for getScope and getMustContain.
		 * @param {string} nodeName Either role:mustContain or role:scope
		 * @ignore
		 */
		function getScopeFactory(nodeName) {
			var cache = [];
			/**
			 * getScope: Find the "Required Context Role" for this role
			 * getMustContain: Find the "Required Owned Elements" for this role
			 * @param {string} [role] An ARIA role OR if not provided will return ALL
			 * 	roles that have a "Required Context Role" (for getScope) or ALL roles
			 *  that have "Required Owned Elements" (for getMustContain)
			 * @returns {Array} An array of strings representing ARIA roles
			 * @example getScope("menuitem");
			 * @example getMustContain("menu");
			 */
			return function(role) {
				var result;
				initialise();
				if (role) {
					result = cache[role] || (cache[role] = getRoleNodes(role, false, nodeName));
				}
				else {
					role = "*";
					result = cache[role] || (cache[role] = getScopedRoles(nodeName));
				}
				return result;
			};
		}

		/**
		 * @param {string} role An ARIA role
		 * @returns {object} An instance the internal ARIA class for this role
		 * 	which stores aria property support information
		 * @ignore
		 */
		function getInstance(role) {
			var Con$tructor, in$tance = instances[role];
			if (in$tance === undefined) {
				Con$tructor = constructors[role];
				in$tance = Con$tructor ? (instances[role] = new Con$tructor()) : null;
			}
			return in$tance;
		}

		/**
		 * @param {string} [role] An ARIA role
		 * @param {boolean} [firstMatch] Set to true to return a single node only
		 * @param {string} child The name of a child element which refers to roles in an rdf:resource attribute
		 * @returns {Array|Element} An array of matching nodes OR if firstMatch is true a single node. OR if
		 * child is provided then an array of strings representing ARIA roles.
		 * @ignore
		 */
		function getRoleNodes(role, firstMatch, child) {
			var result, xpathQuery = "//owl:Class";
			if (role) {
				xpathQuery += "[@rdf:ID='" + role + "']";
				if (child) {
					// The query with /@rdf:resource didn't work in MS Edge version 1 hence the local-name check
					xpathQuery += "/" + child + "/@*[local-name()='resource']";
				}
			}
			result = config.query(xpathQuery, firstMatch, xmlDoc);
			if (child) {
				result = cleanRoles(result);
			}
			return result;
		}

		/**
		 * @param {string} type either "role:scope" or "role:mustContain"
		 * @ignore
		 */
		function getScopedRoles(type) {
			var expression = "//owl:Class[count(" + type + ")>0]/@*[local-name()='ID']",
				result = config.query(expression, false, xmlDoc);
			return cleanRoles(result);
		}
		/** @param roles the roles @ignore */
		function cleanRoles(roles) {
			return roles.map(function(next) {
				return next.nodeValue.replace(ANCHOR_ONLY_RE, "");
			});
		}

		/**
		 * Initialize the constructors
		 * Should only be called once.
		 * @ignore
		 */
		function buildConstructors() {
			var i, classes = getRoleNodes();
			for (i = 0; i < classes.length; i++) {
				buildConstructor(classes[i]);
			}

			/**
			 * Build a JS "class" that represents an ARIA role.
			 * @param {Element} classElement An owl:Class element from the ARIA taxonomy.
			 * @ignore
			 */
			function buildConstructor(classElement) {
				var i, superclasses, required, supported, name;
				if (typeof classElement.getAttributeNS !== "undefined") {
					name = classElement.getAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "ID");
				}
				else {
					name = classElement.getAttribute("rdf:ID");
				}
				if (!constructors[name]) {
					superclasses = getRoleNodes(name, false, "rdfs:subClassOf");
					for (i = superclasses.length - 1; i >= 0; i--) {
						buildConstructor(getRoleNodes(superclasses[i], true));
					}
					required = getRoleNodes(name, false, "role:requiredState");
					supported = getRoleNodes(name, false, "role:supportedState");
					constructors[name] = constructorFactory(required, supported, superclasses);
					// window.console.log("Building constructor:", name);
					if (!baseRole) {
						// window.console.log("Setting baseRole to:", name);
						baseRole = name;
					}
				}
			}

			/**
			 * Add the ARIA states/properties to this object
			 * @param {Object} in$tance An instance of an ARIA class
			 * @param {Array} states An array of strings representing ARIA properties/states
			 * @param {Number} lvl One of the supportLvl enum
			 * @ignore
			 */
			function applyStates(in$tance, states, lvl) {
				var i;
				if (states) {
					for (i = states.length - 1; i >= 0; i--) {
						in$tance[states[i]] = lvl;
					}
				}
			}

			/**
			 * Creates a new "class" representing an ARIA role.
			 * @param {Array} required an array of strings representing ARIA properties/states required by this role
			 * @param {Array} supported an array of strings representing ARIA properties/states supported by this role
			 * @param {Array} superclassRoles an array of strings representing ARIA roles this role inherits from
			 * @ignore
			 */
			function constructorFactory(required, supported, superclassRoles) {
				var i,
					prop,
					len,
					superClass,
					result = function() {
						applyStates(this, required, $this.REQUIRED);
						applyStates(this, supported, $this.SUPPORTED);
					};
				try {
					if (superclassRoles) {
						len = superclassRoles.length;
						for (i = 0; i < len; i++) {
							superClass = new constructors[superclassRoles[i]]();
							if (i === 0) {
								result.prototype = superClass;
							}
							else {
								for (prop in superClass) {
									if (!(prop in result.prototype)) {
										result.prototype[prop] = superClass[prop];
									}
								}
							}
						}
					}
					return result;
				}
				finally {
					superclassRoles = null;
				}
			}
		}
	}
	return /** @alias module:wc/dom/aria */ new AriaWrapper();
});

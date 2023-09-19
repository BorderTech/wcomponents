/**
 * This module wraps the underlying ARIA class (from http://code.google.com/p/aria-toolkit/)
 * which allows us to cut and paste updated ARIA code without losing any of our own  customizations.
 *
 *
 * @module
 *
 * @license The core functionality of this file is a cut and paste from [this project](http://code.google.com/p/aria-toolkit/)
 */
const cache = { scopedTo: {}, scopedBy: {} };
const aria = {
	"alert": {
		"subClassOf": ["region"]
	},
	"alertdialog": {
		"subClassOf": [
			"alert",
			"dialog"
		]
	},
	"application": {
		"subClassOf": ["landmark"]
	},
	"article": {
		"subClassOf": [
			"document",
			"region"
		]
	},
	"banner": {
		"subClassOf": ["landmark"]
	},
	"button": {
		"subClassOf": ["command"],
		"supportedState": [
			"aria-expanded",
			"aria-pressed"
		]
	},
	"checkbox": {
		"subClassOf": ["input"],
		"requiredState": ["aria-checked"]
	},
	"columnheader": {
		"subClassOf": [
			"gridcell",
			"sectionhead",
			"widget"
		],
		"scope": ["row"],
		"supportedState": ["aria-sort"]
	},
	"combobox": {
		"subClassOf": ["select"],
		"mustContain": [
			"listbox",
			"textbox"
		],
		"supportedState": [
			"aria-autocomplete",
			"aria-required"
		],
		"requiredState": ["aria-expanded"]
	},
	"command": {
		"subClassOf": ["widget"]
	},
	"complementary": {
		"subClassOf": ["landmark"]
	},
	"composite": {
		"subClassOf": ["widget"],
		"supportedState": ["aria-activedescendant"]
	},
	"contentinfo": {
		"subClassOf": ["landmark"]
	},
	"definition": {
		"subClassOf": ["section"]
	},
	"dialog": {
		"subClassOf": ["window"]
	},
	"directory": {
		"subClassOf": ["list"]
	},
	"document": {
		"subClassOf": ["structure"],
		"supportedState": ["aria-expanded"]
	},
	"form": {
		"subClassOf": ["landmark"]
	},
	"grid": {
		"subClassOf": [
			"composite",
			"region"
		],
		"mustContain": [
			"row",
			"rowgroup",
			"row"
		],
		"supportedState": [
			"aria-level",
			"aria-multiselectable",
			"aria-readonly"
		]
	},
	"gridcell": {
		"subClassOf": [
			"section",
			"widget"
		],
		"scope": ["row"],
		"supportedState": [
			"aria-readonly",
			"aria-required",
			"aria-selected"
		]
	},
	"group": {
		"subClassOf": ["section"],
		"supportedState": ["aria-activedescendant"]
	},
	"heading": {
		"subClassOf": ["sectionhead"],
		"supportedState": ["aria-level"]
	},
	"img": {
		"subClassOf": ["section"]
	},
	"input": {
		"subClassOf": ["widget"]
	},
	"landmark": {
		"subClassOf": ["region"]
	},
	"link": {
		"subClassOf": ["command"],
		"supportedState": ["aria-expanded"]
	},
	"list": {
		"subClassOf": ["region"],
		"mustContain": [
			"group",
			"listitem",
			"listitem"
		]
	},
	"listbox": {
		"subClassOf": [
			"list",
			"select"
		],
		"mustContain": ["option"],
		"supportedState": [
			"aria-multiselectable",
			"aria-required"
		]
	},
	"listitem": {
		"subClassOf": ["section"],
		"scope": ["list"],
		"supportedState": [
			"aria-level",
			"aria-posinset",
			"aria-setsize"
		]
	},
	"log": {
		"subClassOf": ["region"]
	},
	"main": {
		"subClassOf": ["landmark"]
	},
	"marquee": {
		"subClassOf": ["section"]
	},
	"math": {
		"subClassOf": ["section"]
	},
	"menu": {
		"subClassOf": [
			"list",
			"select"
		],
		"mustContain": [
			"group",
			"menuitemradio",
			"menuitem",
			"menuitemcheckbox",
			"menuitemradio"
		]
	},
	"menubar": {
		"subClassOf": ["menu"]
	},
	"menuitem": {
		"subClassOf": ["command"],
		"scope": [
			"menu",
			"menubar"
		]
	},
	"menuitemcheckbox": {
		"subClassOf": [
			"checkbox",
			"menuitem"
		],
		"scope": [
			"menu",
			"menubar"
		]
	},
	"menuitemradio": {
		"subClassOf": [
			"menuitemcheckbox",
			"radio"
		],
		"scope": [
			"menu",
			"menubar"
		]
	},
	"navigation": {
		"subClassOf": ["landmark"]
	},
	"note": {
		"subClassOf": ["section"]
	},
	"option": {
		"subClassOf": ["input"],
		"supportedState": [
			"aria-checked",
			"aria-posinset",
			"aria-selected",
			"aria-setsize"
		]
	},
	"presentation": {
		"subClassOf": ["structure"]
	},
	"progressbar": {
		"subClassOf": ["range"]
	},
	"radio": {
		"subClassOf": [
			"checkbox",
			"option"
		]
	},
	"radiogroup": {
		"subClassOf": ["select"],
		"mustContain": ["radio"],
		"supportedState": ["aria-required"]
	},
	"range": {
		"subClassOf": ["widget"],
		"supportedState": [
			"aria-valuemax",
			"aria-valuemin",
			"aria-valuenow",
			"aria-valuetext"
		]
	},
	"region": {
		"subClassOf": ["section"]
	},
	"roletype": {
		"supportedState": [
			"aria-atomic",
			"aria-busy",
			"aria-controls",
			"aria-describedby",
			"aria-disabled",
			"aria-dropeffect",
			"aria-flowto",
			"aria-grabbed",
			"aria-haspopup",
			"aria-hidden",
			"aria-invalid",
			"aria-label",
			"aria-labelledby",
			"aria-live",
			"aria-owns",
			"aria-relevant"
		]
	},
	"row": {
		"subClassOf": [
			"group",
			"widget"
		],
		"scope": [
			"grid",
			"rowgroup",
			"treegrid"
		],
		"mustContain": [
			"columnheader",
			"gridcell",
			"rowheader"
		],
		"supportedState": [
			"aria-level",
			"aria-selected"
		]
	},
	"rowgroup": {
		"subClassOf": ["group"],
		"scope": ["grid"],
		"mustContain": ["row"]
	},
	"rowheader": {
		"subClassOf": [
			"gridcell",
			"sectionhead",
			"widget"
		],
		"scope": ["row"],
		"supportedState": ["aria-sort"]
	},
	"search": {
		"subClassOf": ["landmark"]
	},
	"section": {
		"subClassOf": ["structure"],
		"supportedState": ["aria-expanded"]
	},
	"sectionhead": {
		"subClassOf": ["structure"],
		"supportedState": ["aria-expanded"]
	},
	"select": {
		"subClassOf": [
			"composite",
			"group",
			"input"
		]
	},
	"separator": {
		"subClassOf": ["structure"],
		"supportedState": [
			"aria-expanded",
			"aria-orientation"
		]
	},
	"scrollbar": {
		"subClassOf": [
			"input",
			"range"
		],
		"requiredState": [
			"aria-controls",
			"aria-orientation",
			"aria-valuemax",
			"aria-valuemin",
			"aria-valuenow"
		]
	},
	"slider": {
		"subClassOf": [
			"input",
			"range"
		],
		"supportedState": ["aria-orientation"],
		"requiredState": [
			"aria-valuemax",
			"aria-valuemin",
			"aria-valuenow"
		]
	},
	"spinbutton": {
		"subClassOf": [
			"input",
			"range"
		],
		"supportedState": ["aria-required"],
		"requiredState": [
			"aria-valuemax",
			"aria-valuemin",
			"aria-valuenow"
		]
	},
	"status": {
		"subClassOf": ["region"]
	},
	"structure": {
		"subClassOf": ["roletype"]
	},
	"tab": {
		"subClassOf": [
			"sectionhead",
			"widget"
		],
		"scope": ["tablist"],
		"supportedState": ["aria-selected"]
	},
	"tablist": {
		"subClassOf": [
			"composite",
			"directory"
		],
		"mustContain": ["tab"],
		"supportedState": ["aria-level"]
	},
	"tabpanel": {
		"subClassOf": ["region"]
	},
	"textbox": {
		"subClassOf": ["input"],
		"supportedState": [
			"aria-activedescendant",
			"aria-autocomplete",
			"aria-multiline",
			"aria-readonly",
			"aria-required"
		]
	},
	"timer": {
		"subClassOf": ["status"]
	},
	"toolbar": {
		"subClassOf": ["group"]
	},
	"tooltip": {
		"subClassOf": ["section"]
	},
	"tree": {
		"subClassOf": ["select"],
		"mustContain": [
			"group",
			"treeitem",
			"treeitem"
		],
		"supportedState": [
			"aria-multiselectable",
			"aria-required"
		]
	},
	"treegrid": {
		"subClassOf": [
			"grid",
			"tree"
		],
		"mustContain": ["row"]
	},
	"treeitem": {
		"subClassOf": [
			"listitem",
			"option"
		],
		"scope": [
			"group",
			"tree"
		]
	},
	"widget": {
		"subClassOf": ["roletype"]
	},
	"window": {
		"subClassOf": ["roletype"],
		"supportedState": ["aria-expanded"]
	}};

function getAriaRole(role) {
	const _role = role || "widget";
	let ariaRole = cache[_role];
	if (ariaRole) {
		return ariaRole;
	}
	ariaRole = aria[_role] || {
		subClassOf: ["widget"]
	};
	if (ariaRole?.subClassOf) {
		ariaRole.subClassOf.forEach(superClassRole => {
			const superClass = getAriaRole(superClassRole);
			for (const [key, value] of Object.entries(superClass)) {
				if (key === "supportedState" || key === "requiredState") {
					const existing = ariaRole[key] || [];
					ariaRole[key] = [...existing, ...value];
				}
			}
		});
	}
	return cache[role] = ariaRole;
}


/**
 * Creating more than one instance of this class is pointless and is considered an error. We are going to
 * ignore this class as it is just an include.
 *
 * @see http://code.google.com/p/aria-toolkit/ for documentation.
 * @param {Object} config An object that provides helpers / data for this class
 * @ignore
 */
let instance = {
	SUPPORTED: 1,
	REQUIRED: 2,

	/**
	 * Find the "Required Context Role" for this role.
	 * @param {string} [role] An ARIA role OR if not provided will return ALL
	 * roles that have a "Required Context Role"
	 * @returns {string[]} An array of strings representing ARIA roles
	 * @example getScope("menuitem");
	 */
	getScope: function(role) {
		const ariaRole = getAriaRole(role);
		return ariaRole.scope?.concat() || [];
	},
	/**
	 * Find the "Required Owned Elements" for this role
	 * @param {string} [role] An ARIA role OR if not provided will return ALL
	 * roles that have "Required Owned Elements"
	 * @returns {string[]} An array of strings representing ARIA roles
	 * @example getMustContain("menu");
	 */
	getMustContain: function(role) {
		const ariaRole = getAriaRole(role);
		return ariaRole.mustContain?.concat() || [];
	},
	/**
	 * Given an ARIA role will find the container role/s (if any) which "contain" this role.
	 *
	 * This is to allow for asymmetrical scoping in ARIA. For example, the role
	 * "menubar" is not required to contain anything, therefore:
	 * getMustContain("menubar") returns empty array
	 * However: getScopedTo("menubar") returns ["menuitem", "menuitemcheckbox", "menuitemradio"]
	 * This is useful when trying to determine what a particular role SHOULD contain, not must
	 * contain (and not CAN contain because anything can contain anything).
	 * @param {string} [role] An ARIA role
	 * @returns {string[]} An array of strings representing ARIA roles
	 */
	getScopedTo: function (role) {
		if (cache.scopedTo[role]) {
			return cache.scopedTo[role];
		}
		const result = [];
		for (const [nextRole, roleObj] of Object.entries(aria)) {
			if (roleObj?.scope?.includes(role)) {
				result.push(nextRole);
			}
		}
		return (cache.scopedTo[role] = result);
	},
	getScopedBy: function (role) {
		if (cache.scopedBy[role]) {
			return cache.scopedBy[role];
		}
		const result = [];
		for (const [nextRole, roleObj] of Object.entries(aria)) {
			if (roleObj?.mustContain?.includes(role)) {
				result.push(nextRole);
			}
		}
		return (cache.scopedBy[role] = result);
	},

	/**
	 * Find all the aria attributes supported/required by this element/role.
	 * Note that if role is anything other than a known ARIA role then the supported
	 * attributes will be the global ARIA attributes.
	 * @see http://www.w3.org/TR/wai-aria/states_and_properties#global_states
	 *
	 * @function module:wc/dom/aria~Aria.getSupported
	 * @public
	 * @param {String} role An ARIA role or a DOM element.
	 * @returns {Object} an object whose properties are the supported attributes. The values of these properties
	 * will be either SUPPORTED or REQUIRED
	 * @example getSupported("checkbox");
	 * @ignore
	 */
	getSupported: function (role) {
		const ariaRole = getAriaRole(role);
		let result = ariaRole.allSupported;
		if (!result) {
			result = ariaRole.allSupported = {};
			ariaRole.supportedState.forEach(next => result[next] = this.SUPPORTED);
			ariaRole.requiredState?.forEach(next => result[next] = this.REQUIRED);
		}
		return {...result};
	}
};

export default instance;

import dayName from "wc/date/dayName.mjs";
import $monthName from "wc/date/monthName.mjs";
import search from "wc/array/search.mjs";
import $today from "wc/date/today.mjs";
import expandYear from "wc/date/expandYear.mjs";
import escapeRe from "wc/string/escapeRe.mjs";
import addDays from "wc/date/addDays.mjs";
import asciify from "wc/i18n/asciify.mjs";

const YEAR = "year",
	MONTH = "month",
	DAY = "day",
	SEPARATOR = "separator",
	MNTHNAME = "monthName";

/**
 * Module to provide date parsing patterns.
 * Class to generate a pattern used in date parsing.
 * @private
 * @alias module:wc/date/pattern~Pattern
 * @param {String} name An identifier for the particular pattern.
 * @param {String|RegExp} input The date segment pattern as a RegExp or a string which can be converted to a
 *    regular expression
 * @param {Function} [normalise] A function to normalise the result of applying this pattern.
 * @param {function} [output] Set if a particular pattern output is required.
 * @constructor
 */
class Pattern {
	constructor(name, input, normalise, output) {
		/**
		 * The pattern name.
		 * @var
		 * @public
		 * @type String
		 */
		this.name = name;
		/**
		 * The pattern input regular expression.
		 * @var
		 * @type {(RegExp|String)}
		 */
		this.input = input;
		this.output = output || function() {};
		this.normalise = normalise || function() {};
	}
}

/**
 * Create the required patterns.
 *
 * Date patterns, used on parsing and formatting:
    G    Era                                         BC or AD
   y    Year - matches yyyy then tries yy           1800-2999 or 00-99
   y?   Year - non-greedy: match yy first then yyyy 00-99 or 1800-2999
   yy   Year                                        00-99
   yyyy Fullyear                                    1800-2999
   M    Month                                       [0]1-12
   M?   Month - non-greedy, match d then dd         [0]1-12
   MM   Month with a leading zero                   01-12
   MON  Month name                                  e.g. Jan[u[a[r[y]]]]
   w    Week of the year                            W01
   W    Week of the month
   d    Day in Month                                [0]1-31
   d?   Day in Month - non-greedy, match d then dd  [0]1-31
   dd   Day of the month with a leading zero        01-31
   D    Day of year - ordinal date                  001-366
   F    Day of the week                             01 (where 1 = Monday, 07 = Sunday)
   E    Day of the week name                        Monday, Tuesday, etc ( Mon[d[a[y]]] )
        (space) Common separators                   \ / . - (space)
   /    Separator                                   /
   -    Separator                                   -
   ytm  today, yesterday, tomorrow short forms
   +-   +- days from today's date<

 * Reserved for future compatability with Java patterns
a      Am/pm marker     Text      PM
H     Hour in day (0-23)     Number     0
k     Hour in day (1-24)     Number     24
K     Hour in am/pm (0-11)     Number     0
h     Hour in am/pm (1-12)     Number     12
m     Minute in hour     Number     30
s     Second in minute     Number     55
S     Millisecond     Number     978
z     Time zone     General time zone     Pacific Standard Time; PST; GMT-08:00
Z     Time zone     RFC 822 time zone     -0800

/**
 * Normalise a year by expanding two digit years to four using {@link module:wc/date/expandYear} then making
 * sure it is numeric. Modifies the arg by resetting its year property.
 * @function nYear
 * @private
 * @param {Object} result The result of applying a pattern to a date string.
 */
function nYear(result) {
	let value = result.year;

	// normalise the year
	if (value.length === 2) {
		value = expandYear(value);
	}
	value *= 1;
	result.year = value;
}

/**
 * Normalise a month. If the month is defined convert it to a number otherwise initialise it as an empty string.
 * If the month name is defined normalise it by getting its i18n value as the full version or ascii-fied version.
 * Modifies the arg by resetting its month property.
 * arg.
 * @function nMonth
 * @private
 * @param {Object} result The result of applying a pattern to a date string.
 */
function nMonth(result) {
	let value;
	if (MONTH in result) {
		value = result.month;
		value = value * 1 ? value * 1 : "";
		result.month = value;
	}
	if (MNTHNAME in result) {
		let monthName = result.monthName;
		let searchRe = new RegExp(escapeRe(monthName), "i");
		value = search($monthName.get(), searchRe) + 1;
		if (value < 1 && $monthName.hasAsciiVersion()) {
			// if not found check to see if there is a match on the asciified version
			value = search($monthName.get(false, true), searchRe) + 1;
		}
		result.month = value;
	}
	//    value = parseInt(value, 10);
}

/**
 * Normalise a day. If the arg has a day property then make it numeric otherwise make it an empty string.
 * Modifies the arg by resetting its day property.
 * @function nDay
 * @private
 * @param {Object} result The result of applying a pattern to a date string.
 */
function nDay(result) {
	// normalise the day
	let value = result.day;
	value = value * 1 ? value * 1 : "";
	result.day = value;
}

/**
 * Normalise the short form of a date by converting abbreviations (case-insensitive any of "Y", "M", or "T") to
 * a date based on "today". Modifies the arg by setting is day, month and year properties.
 * @function nShortForm
 * @private
 * @param {Object} result The result of applying a pattern to a date string.
 */
function nShortForm(result) {
	const value = result.shortForm,
		today = $today.get();
	// T M Y short forms
	switch (value.toLocaleUpperCase()) {
		case "Y": case "M":
			addDays((value.toLocaleUpperCase() === "Y" ? -1 : 1), today);
		/* falls through */
		case "T":
			result.day = today.getDate();
			result.month = today.getMonth() + 1;
			result.year = today.getFullYear();
			break;
	}
}

/**
 * Normalise a relative date (+/- n) by converting it to date segments relative to "today". Modifies the arg by
 * setting is day, month and year properties.
 * @function nRelative
 * @private
 * @param {Object} result The result of applying a pattern to a date string.
 */
function nRelative(result) {
	// +-days short form
	const today = $today.get();
	addDays(parseInt(result.relative, 10), today);
	result.day = today.getDate();
	result.month = today.getMonth() + 1;
	result.year = today.getFullYear();
}

/**
 * Builds the month "MON" pattern based on the current language.
 * @function monthNameRe
 * @private
 * @returns {module:wc/date/pattern~Pattern} The pattern for the abbreviated month as text for the current
 *    locale.
 */
function monthNameRe() {
	return getPatternFor($monthName.get());
}

/**
 * Builds the day names pattern based on the current language.
 * @function weekdayNameRe
 * @private
 * @returns {module:wc/date/pattern~Pattern} The pattern for the names of the days for the current
 *    locale.
 */
function weekdayNameRe() {
	return getPatternFor(dayName.get());
}

/**
 * Dynamically builds the pattern for shortcut characters yesterday/today/tomorrow for the current locale.
 * @function shortFormRe
 * @private
 * @returns {string} The pattern for the shortcuts for the current locale.
 */
function shortFormRe() {
	// y t m yesterday today tomorrow
	return objectToPattern(["T", "Y", "M"], 1);
	// return '([tT][oO][dD][aA][yY]|[yY][eE][sS][tT][eE][rR][dD][aA][yY]|[tT][oO][mM][oO][rR][rR][oO][wW]|[tT]|[yY]|[mM])';
}

/**
 * Top level function for producing our patterns, call this puppy and it will do the rest.
 * Note, the pattern is only generated once for each object. Subsequent calls will return  a saved pattern.
 * @function getPatternFor
 * @private
 * @param {Object} obj The thing for which we need to generate a pattern.
 * @returns {module:wc/date/pattern~Pattern} The pattern.
 */
function getPatternFor(obj) {
	let objPattern = obj.storedPattern;
	if (!objPattern) {
		objPattern = objectToPattern(obj, 3);
		obj.storedPattern = objPattern;  // store it for future calls
	}
	return objPattern;
}

/**
 * Returns a regex pattern representation of an object whose values are strings. This function guarantees that
 * no duplicate patterns will be produced.
 * @function objectToPattern
 * @private
 * @inner
 * @param {(Object|Array)} obj An object with key value pairs where the values are strings.
 * @param {number} minLength The minimum length of the smallest pattern.
 * @returns {String} corresponding regex pattern.
 *
 * @example objectToPattern({key1:'banana',key2:'bandaid'},3)
 * would produce this:
 * [bB][aA][nN]|[bB][aA][nN][aA]|[bB][aA][nN][aA][nN]|[bB][aA][nN][aA][nN][aA]|[bB][aA][nN][dD]|[bB][aA][nN][dD][aA]|[bB][aA][nN][dD][aA][iI]|[bB][aA][nN][dD][aA][iI][dD]
 */
function objectToPattern(obj, minLength) {
	const map = new PatternMap();
	let result;
	for (let key in obj) {
		if (obj.hasOwnProperty(key)) {  // allows us to pass an array as obj
			let s = obj[key];
			for (let i = minLength; i <= s.length; i++) {
				let fragment = s.substring(0, i);
				map.add(toPattern(fragment));
			}
		}
	}
	result = `(${map.join()})`;
	return result;
}

/**
 * This object provides a simple mechanism to avoid producing duplicate patterns.
 * @constructor
 * @private
 * @alias module:wc/date/pattern~PatternMap
 */
function PatternMap() {
	/**
	 * @var
	 * @type {Object}
	 * @public
	 */
	this.store = {};
}

/**
 * Add the pattern (string) to the map (duplicates will not change the state of the map)
 * @function
 * @public
 * @param {Object} s A pattern to be added to the map
 */
PatternMap.prototype.add = function (s) {
	this.store[s] = s;
};

/**
 * Join all the patterns stored in the map joined with the | character.
 * @function
 * @public
 * @returns {String} The joined patterns.
 */
PatternMap.prototype.join = function () {
	let result = "";
	const store = this.store;
	for (let key in store) {
		if (store.hasOwnProperty(key)) {
			let val = store[key];
			result += val;
			result += "|";
		}
	}
	result = result.replace(/\|$/, "");
	return result;
};

/**
 * Helper for {@link module:wc/date/pattern~objectToPattern} which gets a regex pattern that will match a word
 * or any of its incomplete versions.
 * @function toPattern
 * @private
 * @example input/output: <code>toPattern("banana")</code> will return [bB][aA][nN][aA][nN][aA].
 *
 * @param {string} s A string containing only alphabet characters.
 */
function toPattern(s) {
	let result = "";
	for (let i = 0; i < s.length; i++) {
		let character = s.substring(i, i + 1);
		let next;
		try {
			next = character.toLocaleLowerCase();
			next += character.toLocaleUpperCase();
			let asciified = asciify(next);
			if (asciified && asciified !== next) {
				next += asciified;
			}
		} catch (ex) {
			result += character;
		}
		result += (`[${next}]`);
	}
	return result;
}

const patternCache = {};

/**
 * Getter here to defer initialisation to first use without changing the API.
 */
export default {
	get G() {
		return patternCache["G"] || (patternCache["G"] = new Pattern("era", "(BC|AD)"));
	},
	get y() {
		return patternCache["y"] || (patternCache["y"] = new Pattern(YEAR, "(18[0-9]{2}|19[0-9]{2}|2[0-9]{3}|[0-9]{2}|[0-9]{2})", nYear));
	},
	get "y?"() {
		return patternCache["y?"] || (patternCache["y?"] = new Pattern(YEAR, "([0-9]{2}|18[0-9]{2}|19[0-9]{2}|2[0-9]{3})", nYear));
	},
	get yy() {
		return patternCache["yy"] || (patternCache["yy"] = new Pattern(YEAR, "([0-9]{2})", nYear));
	},
	get yyyy() {
		return patternCache["yyyy"] || (patternCache["yyyy"] = new Pattern(YEAR, "(18[0-9]{2}|19[0-9]{2}|2[0-9]{3}|[0-9]{4})", nYear));
	},
	get M() {
		return patternCache["M"] || (patternCache["M"] = new Pattern(MONTH, "(12|11|10|0[1-9]|[1-9])", nMonth));
	},
	get "M?"() {
		return patternCache["M?"] || (patternCache["M?"] = new Pattern(MONTH, "([1-9]|0[1-9]|10|11|12)", nMonth));
	},
	get MM() {
		return patternCache["MM"] || (patternCache["MM"] = new Pattern(MONTH, "(12|11|10|0[1-9])", nMonth));
	},
	get MON() {
		return patternCache["MON"] || (patternCache["MON"] = new Pattern(MNTHNAME, monthNameRe, nMonth));
	},
	get w() {
		return patternCache["w"] || (patternCache["w"] = new Pattern("weekInYear", "[wW]([0-9]{2})"));
	},
	get W() {
		return patternCache["W"] || (patternCache["W"] = new Pattern("weekInMonth", "_not_defined_"));
	},
	get D() {
		return patternCache["D"] || (patternCache["D"] = new Pattern("dayInYear", "(00[1-9]|0[1-9][0-9]|[1-2][0-9][0-9]|3[0-5][0-9]|3[6][0-6])"));
	},
	get d() {
		return patternCache["d"] || (patternCache["d"] = new Pattern(DAY, "(31|30|[1-2][0-9]|0[1-9]|[1-9])", nDay));
	},
	get "d?"() {
		return patternCache["d?"] || (patternCache["d?"] = new Pattern(DAY, "([1-9]|0[1-9]|[1-2][0-9]|30|31)", nDay));
	},
	get dd() {
		return patternCache["dd"] || (patternCache["dd"] = new Pattern(DAY, "(31|30|[1-2][0-9]|0[1-9])", nDay));
	},
	get F() {
		return patternCache["F"] || (patternCache["F"] = new Pattern("dayInWeek", "(0[1-7])"));
	},
	get E() {
		return patternCache["E"] || (patternCache["E"] = new Pattern("dayInWeekName", weekdayNameRe));
	},
	get " "() {
		return patternCache[" "] || (patternCache[" "] = new Pattern(SEPARATOR, "([ \\\\\\/\\.-])"));
	},
	get "/"() {
		return patternCache["/"] || (patternCache["/"] = new Pattern(SEPARATOR, "(\\/)"));
	},
	get "-"() {
		return patternCache["-"] || (patternCache["-"] = new Pattern(SEPARATOR, "(\\-)"));
	},
	get "+-"() {
		return patternCache["+-"] || (patternCache["+-"] = new Pattern("relative", "(\\+[0-9]+|\\-[0-9]+)", nRelative, null));
	},
	get ytm() {
		return patternCache["ytm"] || (patternCache["ytm"] = new Pattern("shortForm", shortFormRe, nShortForm, null));
	}
};

/**
 * Date patterns, used on parsing and formatting
 * @typedef {Object} module:wc/date/pattern~patterns
 * @property {module:wc/date/pattern~Pattern} G Era BC or AD
 * @property {module:wc/date/pattern~Pattern} y Year - matches yyyy then tries yy 1800-2999 or 00-99
 * @property {module:wc/date/pattern~Pattern} y? Year - non-greedy: match yy first then yyyy 00-99 or 1800-2999
 * @property {module:wc/date/pattern~Pattern} yy Year 00-99
 * @property {module:wc/date/pattern~Pattern} yyyy Fullyear 1800-2999
 * @property {module:wc/date/pattern~Pattern} M Month [0]1-12
 * @property {module:wc/date/pattern~Pattern} M? Month - non-greedy, match d then dd [0]1-12
 * @property {module:wc/date/pattern~Pattern} MM Month with a leading zero 01-12
 * @property {module:wc/date/pattern~Pattern} MON Month name e.g. Jan[u[a[r[y]]]]
 * @property {module:wc/date/pattern~Pattern} w Week of the year W01
 * @property {module:wc/date/pattern~Pattern} W Week of the month
 * @property {module:wc/date/pattern~Pattern} d Day in Month [0]1-31
 * @property {module:wc/date/pattern~Pattern} d? Day in Month - non-greedy, match d then dd  [0]1-31
 * @property {module:wc/date/pattern~Pattern} dd Day of the month with a leading zero 01-31
 * @property {module:wc/date/pattern~Pattern} D Day of year - ordinal date 001-366
 * @property {module:wc/date/pattern~Pattern} F Day of the week 01 (where 1 = Monday, 07 = Sunday)
 * @property {module:wc/date/pattern~Pattern} E Day of the week name Monday, Tuesday, etc ( Mon[d[a[y]]] )
 * @property {module:wc/date/pattern~Pattern} " " (space) Common separators \ / . - (space)
 * @property {module:wc/date/pattern~Pattern} "/" Separator /
 * @property {module:wc/date/pattern~Pattern} "-" Separator -
 * @property {module:wc/date/pattern~Pattern} ytm  today, yesterday, tomorrow short forms
 * @property {module:wc/date/pattern~Pattern} "+-"   +- days from today's date
 */

define(["wc/dom/initialise", "wc/timers", "axe", "wc/has", "wc/ui/ajax/processResponse"], function(initialise, timers, axe, has, processResponse) {
	"use strict";

	var DEFAULT_DELAY = 3000,
		showOnScreen = false,
		defaultAxeConfig = {
			reporter: "v2",
			resultTypes: ["violations"],
			rules: axe.getRules(["wcag2a", "wcag2aa"])
		},
		ignoreBestPracticeIssues = true,
		ignoreExperimentalIssues = true,
		warnIE = true,
		defaultRunConfig = {
			runOnly: {
				type: "tags",
				value: {
					include: ["wcag2a", "wcag2aa"]
				}
			}
		};

	// exclude does not appear to be working properly
	function filterIssues(inArr) {
		if (!(inArr.impact && inArr.tags && inArr.tags.length)) {
			// nothing to report: probably an error
			return false;
		}

		if (ignoreBestPracticeIssues && inArr.tags.indexOf("best-practice") > -1) {
			// ignore this issue
			return false;
		}

		if (ignoreExperimentalIssues && inArr.tags.indexOf("experimental") > -1) {
			// ignore this issue
			return false;
		}
		return true;
	}

	function addData(value, isUrl) {
		var result;

		if (value === null) {
			return;
		}
		if (typeof value === "undefined") {
			return;
		}
		if (isUrl) {
			return  "<dd><a target='_blank' href='" + value + "'>" + value + "</a></dd>";
		}
		if (typeof value === "object") {
			result = "<dd><dl>";
			Object.keys(value).forEach(function (innerKey) {
				result += addTerm(innerKey, value[innerKey]);
			});
			result += "</dl></dd>";
			return result;
		}
		return "<dd>" + value + "</dd>";
	}

	function addTerm(key, value) {
		var result;
		if (!key) {
			return "";
		}

		result = "<dt>" + key + "</dt>";
		if (value === null || typeof value === "undefined") {
			return result;
		}

		if (key === "helpUrl") {
			result += addData(value, true);
		} else if (Array.isArray(value)) {
			value.forEach(function (next) {
				result += addData(next);
			});
		} else if (typeof value === "object") {
			Object.keys(value).forEach(function (innerKey) {
				result += "<dd><dl>";
				result += addTerm(innerKey, value[innerKey]);
				result += "</dl></dd>";
			});
		} else {
			result += addData(value);
		}
		return result;
	}

	/**
	 * Simple reporter to show issues on screen. Used if module configuration object has `visible` == `true` and does not includ a custom callback
	 * function.
	 *
	 * @param {Error} err null unless the attempt to run axe failed.
	 * @param {Array} issues the issues found (if any)
	 */
	function visibleReporter(err, issues) {
		var container,
			html,
			filteredIssues;
		if (err) {
			throw err;
		}

		if (!(issues && issues.violations && issues.violations.length)) {
			return;
		}

		filteredIssues = issues.violations.filter(filterIssues);
		if (!filteredIssues.length) {
			return;
		}

		container = document.createElement("div");
		container.className = "wc_a11y";
		document.body.appendChild(container);

		html = "<ul>";
		filteredIssues.forEach(function (issue) {
			html += "<li><dl class='wc-definitionlist-type-column wc-a11y-" + issue.impact + "'>";

			Object.keys(issue).forEach(function (key) {
				html += addTerm(key, issue[key]);
			});
			html += "</dl></li>";
		});

		html += "</ul>";
		container.innerHTML = html;
	}

	/**
	 * Simple axe callback. Used if module configuration object does not include a custom callback function.
	 *
	 * @param {Error} err null unless the attempt to run axe failed.
	 * @param {Array} issues the issues found (if any)
	 */
	function defaultReporter(err, issues) {
		if (err) {
			throw err;
		}
		var c = window.console,
			filteredIssues;
		if (showOnScreen || (showOnScreen = !c.table)) {
			return visibleReporter(err, issues);
		}

		if (!(issues && issues.violations && issues.violations.length)) {
			c.log("No violations found");
			return;
		}

		filteredIssues = issues.violations.filter(filterIssues);
		if (!filteredIssues.length) {
			c.log("Violations found but excluded");
			return;
		}

		filteredIssues.forEach(function (issue) {
			c.table(issue);
		});
	}

	/**
	 * Run the accessbility test on the current container (or page).
	 * @param {Node} [container=document] the container element we are testing.
	 */
	function a11yTest(container) {
		var c = window.console,
			what = container || document;

		c.log("Starting a11y check...");
		c.time("a11y_deque");
		axe.run(what, defaultRunConfig, defaultReporter);
		c.timeEnd("a11y_deque");
		c.log("Finished a11y check.");
	}

	function run(container) {
		timers.setTimeout(a11yTest, DEFAULT_DELAY, container);
	}

	function doA11yCheck() {
		var excludeArray,
			bail = false;
		if (warnIE && has("ie")) {
			bail = !window.confirm("Running Accessibility tools in IE is very slow, are you sure you want to do this?");
			showOnScreen = true;
		}
		if (bail) {
			return;
		}
		axe.configure(defaultAxeConfig);
		processResponse.subscribe(run, true);

		if (ignoreExperimentalIssues || ignoreBestPracticeIssues) {
			excludeArray = [];

			if (ignoreBestPracticeIssues) {
				excludeArray.push("best-practice");
			}

			if (ignoreExperimentalIssues) {
				excludeArray.push("experimental");
			}
			defaultRunConfig.runOnly.value.exclude = excludeArray;
		}
		run();
	}

	initialise.register({postInit: doA11yCheck});
});

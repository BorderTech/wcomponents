import initialise from "wc/dom/initialise";
import timers from "wc/timers";
import axe from "axe";
import processResponse from "wc/ui/ajax/processResponse";


const DEFAULT_DELAY = 3000,
	defaultAxeConfig = {
		reporter: "v2",
		resultTypes: ["violations"],
		rules: axe.getRules(["wcag2a", "wcag2aa"])
	},
	ignoreBestPracticeIssues = true,
	ignoreExperimentalIssues = true,
	defaultRunConfig = {
		runOnly: {
			type: "tags",
			value: {
				include: ["wcag2a", "wcag2aa"]
			}
		}
	};
let showOnScreen = false;

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

	return !(ignoreExperimentalIssues && inArr.tags.indexOf("experimental") > -1);

}

function addData(value, isUrl) {
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
		let result = "<dd><dl>";
		Object.keys(value).forEach(innerKey => result += addTerm(innerKey, value[innerKey]));
		result += "</dl></dd>";
		return result;
	}
	return "<dd>" + value + "</dd>";
}

function addTerm(key, value) {
	if (!key) {
		return "";
	}

	let result = "<dt>" + key + "</dt>";
	if (value === null || typeof value === "undefined") {
		return result;
	}

	if (key === "helpUrl") {
		result += addData(value, true);
	} else if (Array.isArray(value)) {
		value.forEach(next => result += addData(next));
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
 * @param {{ violations: Array<{ impact: string, tags: string[] }> }} issues the issues found (if any)
 */
function visibleReporter(err, issues) {
	var container,
		html,
		filteredIssues;
	if (err) {
		throw err;
	}

	if (!(issues?.violations && issues.violations.length)) {
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
 * @param {{ violations: Array<{ impact: string, tags: string[] }> }} issues the issues found (if any)
 */
function defaultReporter(err, issues) {
	var c = window.console,
		filteredIssues;
	if (err) {
		throw err;
	}
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
 * Run the accessibility test on the current container (or page).
 * @param {Node} [container=document] the container element we are testing.
 */
function a11yTest(container) {
	console.log("Starting a11y check...");
	console.time("a11y_deque");
	axe.run(container || document, defaultRunConfig, defaultReporter);
	console.timeEnd("a11y_deque");
	console.log("Finished a11y check.");
}

function run(container) {
	timers.setTimeout(a11yTest, DEFAULT_DELAY, container);
}

function doA11yCheck() {
	axe.configure(defaultAxeConfig);
	processResponse.subscribe(run, true);

	if (ignoreExperimentalIssues || ignoreBestPracticeIssues) {
		const excludeArray = [];

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

initialise.register({ postInit: doA11yCheck });

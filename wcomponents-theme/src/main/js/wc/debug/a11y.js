define(["wc/ui/loading"], function(loading) {
	"use strict";
	var GOOGLE = true;  // TODO externalize this

	loading.done.then(a11yTest);

	/**
	 * Run the accessbility test on the current page.
	 */
	function a11yTest() {
		console.log("Starting a11y check...");
		if (GOOGLE) {
			require(["axs"], function(axs) {
				console.time("a11y_goog");
				googleA11yDevTools(axs);
				console.timeEnd("a11y_goog");
				console.log("Finished a11y check.");
			});
		}
		else {
			require(["axe"], function(axe) {
				console.time("a11y_deque");
				axeCore(axe);
				console.timeEnd("a11y_deque");
				console.log("Finished a11y check.");
			});
		}
	}

	function axeCore(axe) {
		axe.a11yCheck(document, function (issues) {
			issues.violations.forEach(function(issue) {
				var obj = {
					url: issue.helpUrl
				};
				issue.nodes.forEach(function(node) {
					node.none.forEach(function(none) {
						obj.isWarning = none.impact !== "serious";
						obj.description = none.message;
						obj.nodes = none.relatedNodes;
					});
					formatIssue(obj);
				});
			});
		});
	}

	function googleA11yDevTools(axs) {
		var auditConfig, issues;
		try {
			auditConfig = new axs.AuditConfiguration();
			auditConfig.showUnsupportedRulesWarning = false;
			auditConfig.scope = document.body;
			/*
			 * Skip "focusableElementNotVisibleAndNotAriaHidden" because it sets focus and that could be annoying.
			 */
			auditConfig.auditRulesToIgnore = ["focusableElementNotVisibleAndNotAriaHidden"];
			issues = axs.Audit.run(auditConfig);
			issues.forEach(function(issue) {
				var obj, isFail = issue.result === axs.constants.AuditResult.FAIL;
				if (isFail) {
					obj = {
						isWarning: issue.rule.severity === axs.constants.Severity.WARNING,
						url: issue.rule.url,
						description: issue.rule.heading,
						nodes: issue.elements
					};
					formatIssue(obj);
				}
			});
		}
		catch (ex) {
			console.error(ex);
		}
	}

	/*
	 * TODO hook this into a generic "debug messages" mechanism.
	 * This could be a good place for an experiment such as mustache templates or webcomponents since it only runs when
	 *    debug is enabled.
	 */
	function formatIssue(issue) {
		var container = document.createElement("div"),
			list = document.createElement("ul"),
			link = document.createElement("a");
		link.href = issue.url;
		link.target = "_blank";
		link.innerHTML = issue.description;
		issue.nodes.forEach(function(element) {
			var html, listItem = document.createElement("li");
			if (element.html) {
				html = element.html;
			}
			else {
				html = element.outerHTML;
			}
			html = html.replace(/</g, "&lt;");
			html = html.replace(/>/g, "&gt;");
			listItem.innerHTML = html;
			list.appendChild(listItem);
		});
		container.className = "wc_a11y";
		if (!issue.isWarning) {
			container.classList.add("severe");
		}
		else {
			container.classList.add("warning");
		}
		container.appendChild(link);
		container.appendChild(list);
		document.body.appendChild(container);
	}
});

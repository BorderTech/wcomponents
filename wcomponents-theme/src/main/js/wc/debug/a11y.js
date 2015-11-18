define(["axs", "wc/ui/loading"], function(axs, loading) {
	"use strict";

	loading.done.then(a11yTest);

	/**
	 * Run the accessbility test on the current page.
	 */
	function a11yTest() {
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
			issues.forEach(formatIssue);
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
		if (issue.result === axs.constants.AuditResult.FAIL) {
			link.href = issue.rule.url;
			link.target = "_blank";
			link.innerHTML = issue.rule.heading;
			issue.elements.forEach(function(element) {
				var html, listItem = document.createElement("li");
				html = element.outerHTML;
				html = html.replace(/</g, "&lt;");
				html = html.replace(/>/g, "&gt;");
				listItem.innerHTML = html;
				list.appendChild(listItem);
			});
			container.className = "wc_a11y";
			if (issue.rule.severity === axs.constants.Severity.SEVERE) {
				container.classList.add("severe");
			}
			else if (issue.rule.severity === axs.constants.Severity.WARNING) {
				container.classList.add("warning");
			}
			container.appendChild(link);
			container.appendChild(list);
			document.body.appendChild(container);
		}
	}
});

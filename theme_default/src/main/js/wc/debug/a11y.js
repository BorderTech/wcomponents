define(["axs", "wc/dom/initialise"], function(axs, initialise) {
	"use strict";

	initialise.addCallback(a11yTest);

	function a11yTest() {
		var auditConfig, issues;
		try {
			auditConfig = new axs.AuditConfiguration();
			auditConfig.showUnsupportedRulesWarning = false;
			issues = axs.Audit.run(auditConfig);
			issues.forEach(formatIssue);
		}
		catch(ex) {
			console.error(ex);
		}
	}

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

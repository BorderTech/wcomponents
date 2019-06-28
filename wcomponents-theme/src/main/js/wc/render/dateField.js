define(["wc/render/utils",
	"wc/has",
	"wc/dom/dateFieldUtils",
	"wc/mixin"],
	function(renderUtils, has, dfUtils, mixin) {

		var hasNative = has("native-dateinput"),
			dataAttributeMap = {
				"data-wc-tooltip": "title",
				"data-wc-required": "required",
				"data-wc-disabled": "disabled",
				"data-wc-accessibletext": "aria-label",
				"data-wc-buttonid": "data-wc-submit",
				"data-wc-placeholder": "placeholder",
				"data-wc-min": "min",
				"data-wc-max": "max"
			},
			widgets = dfUtils.getWidgets();



		function renderDateField(element) {
			var container = findContainer(element),
				allowPartial = element.getAttribute("data-wc-allowpartial"),
				launcher = widgets.LAUNCHER.findDescendant(container),
				suggestionList = widgets.SUGGESTION_LIST.findDescendant(container),
				elements;
			if (!hasNative || allowPartial === "true" || dfUtils.hasPartialDate(element)) {
				elements = [createFakeDateInput(element)];
				if (!launcher) {
					elements.push(renderDatePickerLauncher(element));
				}
				if (!suggestionList) {
					elements.push(createListBox());
				}
			} else {
				if (launcher) {
					launcher.parentNode.removeChild(launcher);
				}
				if (suggestionList) {
					suggestionList.parentNode.removeChild(suggestionList);
				}
				elements = [createDateInput(element)];
			}
			if (allowPartial !== null) {
				container.classList.add("wc_datefield_partial");
				if (!widgets.SWITCHER.findDescendant(container)) {
					elements.push(createPartialSwitcher(element));
				}
			}
			elements = createWrapper(elements);
			container.replaceChild(elements, element);
		}

		function createWrapper(children) {
			var wrapper = document.createDocumentFragment();
			children.forEach(function(element) {
				wrapper.appendChild(element);
			});
			return wrapper;
		}

		function createPartialSwitcher(element) {
			var switcher,
				dateFieldId = element.getAttribute("data-wc-id"),
				switcherId = dateFieldId + "_partial",
				allowPartial = element.getAttribute("data-wc-allowpartial"),
				config = {
					attrs: {
						name: switcherId,
						type: "checkbox",
						value: "true",
						"aria-controls": dateFieldId
					},
					onChange: changeEvent
				};
			if (allowPartial === "true") {
				config.attrs.checked = "checked";
			}
			/*
			 * The mere existence of @allowPartial indicates that we are dealing with a partial date field.
			 * The value is irrelevant, it really has three meaningful states:
			 * "true" - allow partial dates and user requested partial
			 * "false" - allow partial dates but user has not requested partialx
			 * null - does not allow partial dates
			 */
			switcher = renderUtils.createElement("input", config);
			switcher = renderUtils.createElement("label", {}, [switcher, "I'm not sure"]);
			switcher = renderUtils.createElement("div", {}, [switcher]);
			return switcher;
		}

		function renderDatePickerLauncher(element) {
			var container = findContainer(element),
				icon, result, config = {
					attrs: {
						"aria-hidden": "true",
						tabindex: "-1",
						type: "button",
						value: container.id + "_input"
					}
				};
			icon = renderUtils.createElement("i", { attrs: {"aria-hidden": "true"} });
			icon.className = "fa fa-calendar";

			result = renderUtils.createElement("button", config, [icon]);
			result.className = "wc_wdf_cal wc-invite";

			if (element.hasAttribute("data-wc-disabled")) {
				result.disabled = true;
			}

			return result;
		}

		function createListBox() {
			var config = {
				attrs: {
					"aria-busy": "true",
					role: "listbox"
				}
			};
			return renderUtils.createElement("span", config);
		}

		function createDateInput(element) {
			var input, fieldId = element.getAttribute("data-wc-id"),
				dateVal = dfUtils.reverseFormat(element),
				config = { attrs: {} };

			renderUtils.extractAttributes(element, dataAttributeMap, config.attrs);
			mixin({
				value: dateVal,
				id: fieldId + "_input",
				name: fieldId,
				type: "date",
				autocomplete: "off" },
			config.attrs);

			if (!dateVal) {
				config.attrs["aria-invalid"] = "true";
			}

			input = renderUtils.createElement("input", config);

			if (element.hasAttribute("wc-data-submitonchange")) {
				input.className = "wc_soc";
			}
			return input;
		}

		function createFakeDateInput(element) {
			var input,
				fieldId = element.getAttribute("data-wc-id"),
				config = { attrs: {} };
			renderUtils.extractAttributes(element, dataAttributeMap, config.attrs);
			mixin({
				value: dfUtils.getDateValue(element),
				id: fieldId + "_input",
				name: fieldId,
				type: "text",
				autocomplete: "off" },
			config.attrs);

			input = renderUtils.createElement("input", config);

			if (element.hasAttribute("wc-data-submitonchange")) {
				input.className = "wc_soc";
			}
			return input;
		}

		function changeEvent($event) {
			var switcher = $event.target,
				containerId = switcher.getAttribute("aria-controls"),
				dateField = document.getElementById(containerId + "_input");
			if (dateField) {
				dateField.setAttribute("data-wc-allowpartial", switcher.checked);
				renderDateField(dateField);
			}
		}

		function findContainer(element) {
			var containerId, result = widgets.DATE_FIELD.findAncestor(element);
			if (!result) {
				containerId = element.getAttribute("data-wc-id");
				result = document.getElementById(containerId);
			}
			return result;
		}

		return {
			widgets: widgets,
			render: renderDateField
		};
	});

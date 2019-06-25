define(["wc/render/utils", "wc/has"], function(renderUtils, has) {

	var hasNative = has("native-dateinput"),
		dataAttributeMap = {
			"data-wc-id": "name",
			"data-wc-tooltip": "title",
			"data-wc-required": "required",
			"data-wc-disabled": "disabled",
			"data-wc-accessibletext": "aria-label",
			"data-wc-buttonid": "data-wc-submit",
			"data-wc-placeholder": "placeholder",
			"data-wc-min": "min",
			"data-wc-max": "max"
		};


	function renderDateField(element) {
		var container = element.parentNode,
			allowPartial = element.getAttribute("data-wc-allowpartial");
		if (!hasNative || allowPartial === "true") {
			renderFakeDateField(element, container);
		} else {
			renderRealDateField(element, container);
		}
		if (allowPartial !== null) {
			container.classList.add("wc_datefield_partial");
			container.appendChild(createPartialSwitcher(element));
		}
	}

	function renderRealDateField(element, container) {
		var newElement = createDateInput(element);
		container.replaceChild(newElement, element);
	}

	function renderFakeDateField(element, container) {
		var newElement = createListBox();
		container.replaceChild(newElement, element);
		newElement = container.insertBefore(renderDatePickerLauncher(element), newElement);
		newElement = container.insertBefore(createFakeDateInput(element), newElement);
	}

	function getDateValue(element) {
		var result = "",
			valueWrapper = element.querySelector(".wc_value");
		if (valueWrapper) {
			result = valueWrapper.textContent;
		}
		return result;
	}

	function createPartialSwitcher(element) {
		var switcher,
			switcherId = element.getAttribute("data-wc-id") + "_partial",
			allowPartial = element.getAttribute("data-wc-allowpartial"),
			config = {
				attrs: {
					name: switcherId,
					type: "checkbox",
					value: "true"
				}
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
		return switcher;
	}

	function renderDatePickerLauncher(element) {
		var icon, result, config = {
				attrs: {
					"aria-hidden": "true",
					tabindex: "-1",
					type: "button",
					value: element.getAttribute("data-wc-id") + "_input"
				}
			};
		icon = renderUtils.createElement("i", { attrs: {"aria-hidden": "true"} });
		icon.className = "fa fa-calendar";

		result = renderUtils.createElement("button", config, [icon]);
		result.className = "wc_wdf_cal wc-invite";

		if (element.disabled) {
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
		var input, dateVal = getDateValue(element), config = {
				attrs: {
					value: dateVal,
					id: element.getAttribute("data-wc-id") + "_input",
					type: "date",
					autocomplete: "off"
				}
			};

		renderUtils.extractAttributes(element, dataAttributeMap, config.attrs);

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
		var input, config = {
				attrs: {
					value: getDateValue(element),
					id: element.getAttribute("data-wc-id") + "_input",
					type: "text",
					autocomplete: "off"
				}
			};
		renderUtils.extractAttributes(element, dataAttributeMap, config.attrs);

		input = renderUtils.createElement("input", config);

		if (element.hasAttribute("wc-data-submitonchange")) {
			input.className = "wc_soc";
		}
		return input;
	}

	return {
		render: renderDateField
	};
});

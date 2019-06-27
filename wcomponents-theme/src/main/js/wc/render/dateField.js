define(["wc/render/utils", "wc/has", "wc/date/interchange", "wc/dom/Widget", "wc/mixin"], function(renderUtils, has, dateInterchange, Widget, mixin) {

	var FIELD_CLASS = "wc-datefield",
		hasNative = has("native-dateinput"),
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
		widgets = createWidgets();


	function createWidgets() {
		var widgetMap = {
			DATE_FIELD: new Widget("div", FIELD_CLASS),
			DATE_WRAPPER_INCL_RO: new Widget("", FIELD_CLASS),
			DATE_RO: new Widget("", "", {"data-wc-component": "datefield"}),
			INPUT: new Widget("input"),
			SUGGESTION_LIST: new Widget("", "", { role: "listbox"}),
			OPTION_WD: new Widget("", "", { role: "option"}),
			LAUNCHER: new Widget("button", "wc_wdf_cal"),
			SWITCHER: new Widget("input", "", { type: "checkbox" })
		};
		widgetMap.DATE = widgetMap.INPUT.extend("", { type: "date"});
		widgetMap.DATE_PARTIAL = widgetMap.INPUT.extend("", { type : "text"});
		widgetMap.INPUT.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.DATE.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.DATE_PARTIAL.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.SUGGESTION_LIST.descendFrom(widgetMap.DATE_FIELD, true);
		return widgetMap;
	}

	function renderDateField(element) {
		var container = findContainer(element),
			allowPartial = element.getAttribute("data-wc-allowpartial"),
			dateVal = getDateValue(element),
			elements,
			isPartial = (dateVal && !dateInterchange.isComplete(dateVal));
		if (!hasNative || allowPartial === "true" || isPartial) {
			elements = [createFakeDateInput(element)];
			if (!widgets.LAUNCHER.findDescendant(container)) {
				elements.push(renderDatePickerLauncher(element));
			}
			if (!widgets.SUGGESTION_LIST.findDescendant(container)) {
				elements.push(createListBox());
			}
		} else {
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

	function getDateValue(element) {
		var result;
		if ("value" in element) {
			result = element.value;
		} else if (element.children.length < 1) {
			result = element.textContent;
		} else {
			result = "";
		}
		return result;
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
			dateVal = getDateValue(element),
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
			value: getDateValue(element),
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
		render: renderDateField,
		/**
		 * Indicates that the requested element is a dateField OR the textbox sub-component
		 * @function module:wc/ui/dateField.isOneOfMe
		 * @public
		 * @param {Element} element The DOM element to test
		 * @param {Boolean} [onlyContainer] Set `true` to test if the element is exactly the dateField, explicitly
		 *    `false` to test if only the input element.
		 * @returns {Boolean} true if the passed in element is a dateField or date input textbox sub-component of a
		 *    dateField
		 */
		isOneOfMe: function (element, onlyContainer) {
			var result;
			if (onlyContainer) {
				result = widgets.DATE_WRAPPER_INCL_RO.isOneOfMe(element);
			} else if (onlyContainer === false) {
				result = widgets.INPUT.isOneOfMe(element);
			} else {
				result = Widget.isOneOfMe(element, [widgets.INPUT, widgets.DATE_WRAPPER_INCL_RO]);
			}
			return result;
		}
	};
});

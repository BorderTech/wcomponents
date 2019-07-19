define(["wc/render/utils",
	"wc/has",
	"wc/i18n/i18n",
	"wc/dom/shed",
	"wc/dom/dateFieldUtils",
	"wc/dom/fieldIndicatorUtils",
	"wc/date/Format",
	"wc/mixin",
	"wc/debounce"],
	function(renderUtils, has, i18n, shed, dfUtils, fieldIndicatorUtils, Format, mixin, debounce) {

		var inputAttributeMap = {
				"data-wc-tooltip": "title",
				"data-wc-required": "required",
				"data-wc-disabled": "disabled",
				"data-wc-accessibletext": "aria-label",
				"data-wc-buttonid": "data-wc-submit",
				"data-wc-placeholder": "placeholder",
				"data-wc-min": "min",
				"data-wc-max": "max",
				"aria-busy": null,
				"aria-describedby": null,
				"aria-invalid": null
			},
			checkEnableSwitcherEvent = debounce(function($event) {
				checkEnableSwitcher($event.target);
			}, 330),
			widgets = dfUtils.getWidgets();

		function renderAsync(element) {
			var messageKeys = ["datefield_title_default", "datefield_partial_switcher_label"],
				bundle = {};
			return i18n.translate(messageKeys).then(function(messages) {
				var i;
				for (i = 0; i < messageKeys.length; i++) {
					bundle[messageKeys[i]] = messages[i];
				}
				renderDateField(element, bundle);
			});
		}

		function getId(element) {
			return element.id || element.getAttribute("data-wc-id");
		}

		/**
		 * Imports all field indicators into a target array.
		 * @param {Element} element  An element potentially containing field inidcators.
		 * @param {Element[]} target An array to which the field indicators will be imported.
		 * @return {Element[]} An array of field indicator elements.
		 */
		function gatherFieldIndicators(element, target) {
			// TODO how will this work with client side validation messages?
			var result= target || [],
				fiWidgets = fieldIndicatorUtils.getWidgets(),
				container = fiWidgets.FIELDINDICATOR.findDescendant(element);
			if (container) {
				renderUtils.importKids(container, result);
				container.parentNode.removeChild(container);
			}
			return result;
		}

		function renderDateField(element, i18nBundle) {
			var createContainerFunc = createContainer,
				allowPartial = element.getAttribute("data-wc-allowpartial"),
				elements, hasPartialDate = dfUtils.hasPartialDate(element);
			if (!has("native-dateinput") || allowPartial === "true" || hasPartialDate) {
				elements = [createFakeDateInput(element, i18nBundle)];
				elements.push(renderDatePickerLauncher(element));
				elements.push(createListBox());
				createContainerFunc = createCustomContainer;
			} else {
				elements = [createDateInput(element)];
			}
			if (allowPartial !== null && has("native-dateinput")) {
				elements.push(createPartialSwitcher(element, i18nBundle, hasPartialDate));
			}
			gatherFieldIndicators(element, elements);
			elements = createContainerFunc(element, elements, allowPartial);
			element.parentNode.replaceChild(elements, element);
		}


		function createPartialSwitcher(element, i18nBundle, disabled) {
			var switcher,
				dateFieldId = getId(element),
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
			/*
			 * The mere existence of @allowPartial indicates that we are dealing with a partial date field.
			 * The value is irrelevant, it really has three meaningful states:
			 * "true" - allow partial dates and user requested partial
			 * "false" - allow partial dates but user has not requested partialx
			 * null - does not allow partial dates
			 */
			switcher = renderUtils.createElement("input", config);
			if (allowPartial === "true") {
				shed.select(switcher, true);
			}
			if (disabled || isDisabled(element)) {
				shed.disable(switcher, true);
			}
			switcher = renderUtils.createElement("label", {}, [switcher, i18nBundle["datefield_partial_switcher_label"]]);
			switcher = renderUtils.createElement("div", {}, [switcher]);
			return switcher;
		}

		function renderDatePickerLauncher(element) {
			var launcherWidget = widgets.LAUNCHER.extend("", {
					"aria-hidden": "true",
					tabindex: "-1",
					value: getId(element) + "_input"
				}),
				result = launcherWidget.render();
			renderUtils.appendKids(result, [widgets.LAUNCHER_ICON.render()]);

			if (isDisabled(element)) {
				shed.disable(result, true);
			}

			return result;
		}

		function createContainer(element, children, allowPartial) {
			var container,
				attrs = {
					id: getId(element)
				};

			if (allowPartial !== null) {
				attrs["data-wc-allowpartial"] = allowPartial;
			}
			container = widgets.DATE_FIELD.render({ state: attrs });
			renderUtils.appendKids(container, children);
			return container;
		}

		function createCustomContainer(element, children, allowPartial) {
			var widget,
				container,
				attrs = {
					id: getId(element),
					"aria-expanded": "false"
				};
			if (allowPartial !== null) {
				widget = widgets.DATE_WRAPPER_PARTIAL;
				attrs["data-wc-allowpartial"] = allowPartial;
			} else {
				widget = widgets.DATE_WRAPPER_FAKE;
			}
			container = widget.render({ state: attrs });
			renderUtils.appendKids(container, children);
			return container;
		}

		function createListBox() {
			return widgets.SUGGESTION_LIST.render();
		}

		function createDateInput(element) {
			var input, fieldId = getId(element),
				dateVal = dfUtils.getValue(element),
				config = { attrs: {} };

			renderUtils.extractAttributes(element, inputAttributeMap, config.attrs);
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

		function createFakeDateInput(element, i18nBundle) {
			var input,
				formatter = Format.getDefaultFormatter(),
				value = dfUtils.getRawValue(element),
				fieldId = getId(element),
				config = {
					onChange: checkEnableSwitcherEvent,
					onKeydown: checkEnableSwitcherEvent,
					attrs: {
						title: i18nBundle["datefield_title_default"]
					} };
			renderUtils.extractAttributes(element, inputAttributeMap, config.attrs);
			value = formatter.format(value) || value;
			mixin({
				value: value,
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
				dateField = document.getElementById(containerId);
			if (dateField) {
				dateField.setAttribute("data-wc-allowpartial", shed.isSelected(switcher));
				renderAsync(dateField);
			}
		}

		/*
		 * don't call this directly, call checkEnableSwitcherEvent instead
		 */
		function checkEnableSwitcher(element) {
			var dateField = dfUtils.get(element),
				switcher = widgets.SWITCHER.findDescendant(dateField),
				hasPartial;
			if (switcher) {
				if (shed.isSelected(switcher)) {
					hasPartial = dfUtils.hasPartialDate(element);
					// Currently partial dates are accepted, disable switcher if date contains partial date
					if (hasPartial) {
						shed.disable(switcher, true);
					} else {
						shed.enable(switcher, true);
					}
				} else {
					shed.enable(switcher, true);
				}
			}
		}

		function isDisabled(element) {
			return element.hasAttribute("data-wc-disabled") || shed.isDisabled(element);
		}

		return {
			render: renderAsync
		};
	});

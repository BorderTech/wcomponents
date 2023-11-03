import diagnostic from "wc/dom/diagnostic.mjs";
/*
	Porting from XSLT, notes:
	'additional' attribute should just be 'class' attribute now
	'hidden' attribute can be on the custom element, rather than the fieldset
	'toolTip' attribute should just be the 'title' attribute now

 */
const template = (props) => `
	<fieldset id="${props.id}" class="${props.className}" ${props.constraints}>

		<ul class="wc_list_nb wc-vgap-sm">
			<xsl:choose>
				<xsl:when test="count(.//ui:option[@selected]) eq 0">
					<xsl:apply-templates mode="multiDropDown" select="(ui:option | ui:optgroup/ui:option)[1]"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates mode="multiDropDown" select=".//ui:option[@selected]"/>
				</xsl:otherwise>
			</xsl:choose>
		</ul>
		<xsl:apply-templates select="ui:fieldindicator"/>

	</fieldset>`;
class MultiDropdown extends HTMLElement {
	constructor() {
		super();

	}

	getHtml() {
		const id = this.getAttribute("id");

		return template({
			id,
			className: getClassName(this),
			aria: getAriaProps(this),
			constraints: getConstraints(this)
		});
	}
}

function getConstraints(element) {
	const constraints = [];
	if (element.hasAttribute("min")) {
		constraints.push(`data-wc-min="${element.getAttribute("min")}"`)
	}

	if (element.hasAttribute("max")) {
		constraints.push(`data-wc-max="${element.getAttribute("max")}"`)
	}
	return constraints.join(" ");
}

function getAriaProps(element) {
	const props = [
		`aria-atomic="false"`,
		`aria-relevant="additions removals"`];
	if (element.hasAttribute("accessibleText")) {
		props.push(`aria-label="${element.getAttribute("accessibleText")}"`);
	}
	const fieldIndicators = diagnostic.getWithin(element);
	if (fieldIndicators) {
		const ids = [];
		let isError = false;
		fieldIndicators.forEach(fieldIndicator => {
			if (fieldIndicator.id) {
				ids.push(fieldIndicator.id);
			}
			if (diagnostic.getLevel(fieldIndicator) === diagnostic.LEVEL.ERROR) {
				isError = true;
			}
		});
		if (isError) {
			props.push(`aria-invalid="true"`)
		}
		if (ids.length) {
			props.push(`aria-describedby="${ids.join(" ")}"`)
		}
	}
	return props.join(" ");
}

function getClassName(element) {
	const classNames = ["wc-multidropdown", "wc_mfc wc_noborder"];
	if (element.hasAttribute("required")) {
		classNames.push("wc_req");
	}

	if (element.hasAttribute("class")) {
		classNames.push(element.getAttribute("class"));
	}
	return classNames.join(" ");
}

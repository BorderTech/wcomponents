import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";
import { useContext, useEffect } from "react";
import { RequestContext } from "../contexts.ts";

// This component arbitrarily attaches and removes event listeners in response to <wc-ajaxtrigger> being encountered.
// TODO: Currently only "click" interactions are handled.
export default function WAjaxTrigger(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const processAjax = useContext(RequestContext);

	useEffect(() => {
		const ajaxTarget = getWComponentNodeFromElement(wcNode.children[0]);

		if (ajaxTarget && ajaxTarget.tagName === "wc-ajaxtargetid") {
			const triggerId = wcNode.attributes["triggerId"];
			const trigger = document.getElementById(triggerId);

			if (trigger) {
				const buttonClick = (e: PointerEvent) => {
					e.preventDefault();
					processAjax(triggerId, "x", true);
				};
				trigger.addEventListener("click", buttonClick);
				return () => {
					trigger.removeEventListener("click", buttonClick);
				};
			}
		}
	}, [wcNode.attributes, wcNode.children, processAjax]);

	return <></>;
}

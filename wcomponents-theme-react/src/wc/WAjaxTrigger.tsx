import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";
import { useContext, useEffect } from "react";
import { AjaxContext } from "../contexts.ts";

export default function WAjaxTrigger(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const processAjax = useContext(AjaxContext);

	console.log("ajax render");
	useEffect(() => {
		console.log("useeffect");
		const ajaxTarget = getWComponentNodeFromElement(wcNode.children[0]);

		if (ajaxTarget && ajaxTarget.tagName === "wc-ajaxtargetid") {
			const triggerId = wcNode.attributes["triggerId"];
			const targetId = ajaxTarget.attributes["targetId"];

			const trigger = document.getElementById(triggerId);
			if (trigger) {
				console.log("ADDED EVENT LISTENER");
				const buttonClick = (e: PointerEvent) => {
					e.preventDefault();
					console.log("button clicked!!!");
					processAjax(triggerId, targetId);
				};
				trigger.addEventListener("click", buttonClick);
				return () => {
					console.log("REMOVED EVENT LISTENER");
					trigger.removeEventListener("click", buttonClick);
				};
			}
		}
	}, [wcNode.attributes, wcNode.children, processAjax]);

	return <></>;
}

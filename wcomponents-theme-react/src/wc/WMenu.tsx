import type { WComponentNode } from "../data.ts";
import { MenuItem, MenuList } from "@mui/material";
import { WComponentSet } from "../WComponent.tsx";
import { useContext } from "react";
import { RequestContext } from "../contexts.ts";

// Implements ui:menu using MUI MenuList (https://mui.com/material-ui/api/menu-list/).
export default function WMenu(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const processRequest = useContext(RequestContext);

	let selectedItemId = "";

	return (
		<>
			<MenuList id={wcNode.id}>
				{wcNode.children.map((node, i) => {
					const menuItem = node as Element;
					if (menuItem.getAttribute("selected") === "true") {
						selectedItemId = menuItem.id;
					}
					return (
						menuItem.tagName === "ui:menuitem" && (
							// The MUI menus don't use <button type="submit"> so we need to manually handle
							// submitting on click.
							<MenuItem key={i} id={menuItem.id} onClick={() => processRequest(menuItem.id, "x")}>
								<WComponentSet xmlNodes={Array.from(menuItem.children)} />
							</MenuItem>
						)
					);
				})}
			</MenuList>
			<input type="hidden" name={`${selectedItemId}.selected`} value="x" />
			<input type="hidden" name={`${wcNode.id}-h`} value="x" />
		</>
	);
}

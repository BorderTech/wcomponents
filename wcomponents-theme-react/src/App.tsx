import { useEffect, useState } from "react";
import { WComponentSet } from "./WComponent.tsx";
import { getClientLayout, processAjaxRequest, processSearchRequest, type WComponentNode } from "./data.ts";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { AjaxContext } from "./contexts.ts";

function extractApplicationParams(wcNode: WComponentNode): { [key: string]: string } {
	const params: { [key: string]: string } = {};
	for (const xmlNode of wcNode.children) {
		const element = xmlNode as Element;
		if (element.tagName === "ui:param") {
			const name = element.getAttribute("name");
			const value = element.getAttribute("value");
			if (name && value) {
				params[name] = value;
			}
		}
	}
	return params;
}

export default function App() {
	const [application, setApplication] = useState<WComponentNode | null>(null);
	const [appParams, setAppParams] = useState<{ [key: string]: string }>({});

	const processAjax = (triggerId: string, targetId: string) => {
		if (application) {
			processAjaxRequest(application, appParams, triggerId, targetId)
				.then((c) => {
					setApplication(c);
					setAppParams(c ? extractApplicationParams(c) : {});
					console.log("completed ajax and updated UI");
				})
				.catch((e) => console.error(e));
		}
	};

	const processSearch = (formData: FormData) => {
		processSearchRequest(formData, appParams)
			.then((c) => {
				setApplication(c);
				setAppParams(c ? extractApplicationParams(c) : {});
			})
			.catch((e) => console.error(e));
	};

	useEffect(() => {
		getClientLayout()
			.then((c) => {
				setApplication(c);
				setAppParams(c ? extractApplicationParams(c) : {});
			})
			.catch((e) => console.error(e));
	}, []);

	return (
		<>
			{!application && <p>No valid XML application structure</p>}
			{application && (
				<AjaxContext value={processAjax}>
					<LocalizationProvider dateAdapter={AdapterDayjs}>
						<form id={application.id} action={processSearch}>
							<WComponentSet xmlNodes={application.children} />
						</form>
					</LocalizationProvider>
				</AjaxContext>
			)}
		</>
	);
}

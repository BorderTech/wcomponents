import { useEffect, useState } from "react";
import { WComponentSet } from "./WComponent.tsx";
import {
	getClientLayout,
	processCustomRequest,
	processSearchRequest,
	processTabSetRequest,
	type WComponentNode,
} from "./data.ts";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { RequestContext, TabSetContext } from "./contexts.ts";

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

	const processCustom = (triggerId: string, triggerValue: string, ajax?: boolean) => {
		if (application) {
			processCustomRequest(application, appParams, triggerId, triggerValue, ajax)
				.then((c) => {
					setApplication(c);
					setAppParams(c ? extractApplicationParams(c) : {});
					console.log("completed request and updated UI");
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

	const processTabSet = (tabSetId: string, tabId: string, tabIndex: number) => {
		if (application) {
			processTabSetRequest(application, appParams, tabSetId, tabId, tabIndex)
				.then((c) => {
					setApplication(c);
					setAppParams(c ? extractApplicationParams(c) : {});
					console.log("completed request and updated UI");
				})
				.catch((e) => console.error(e));
		}
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
				<RequestContext value={processCustom}>
					<TabSetContext value={processTabSet}>
						<LocalizationProvider dateAdapter={AdapterDayjs}>
							<form id={application.id} action={processSearch}>
								<WComponentSet xmlNodes={application.children} />
							</form>
						</LocalizationProvider>
					</TabSetContext>
				</RequestContext>
			)}
		</>
	);
}

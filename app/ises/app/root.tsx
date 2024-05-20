import {
	Links,
	Meta,
	Outlet,
	Scripts,
	ScrollRestoration,
} from "@remix-run/react";

import type { LinksFunction } from "@remix-run/node";
import stylesheet from "~/tailwind.css?url";
import Navbar from "./components/navbar";
import toast, { Toaster } from "react-hot-toast";
import { useEffect } from "react";
import { getJwtShouldNagPassword } from "./util/localstorage";

export const links: LinksFunction = () => [
	{ rel: "stylesheet", href: stylesheet },
];

export default function App() {
	useEffect(() => {
		if (getJwtShouldNagPassword()) {
			toast.error(
				"Your password is too weak! Please change your password."
			);
		}
	});

	return (
		<html className="bg-slate-50 text-black dark:text-white dark:bg-gray-900">
			<head>
				<link rel="icon" href="data:image/x-icon;base64,AA" />
				<Meta />
				<Links />
			</head>
			<body>
				<Navbar />

				<ScrollRestoration />
				<Scripts />
				<Outlet />
				<Toaster />
			</body>
		</html>
	);
}

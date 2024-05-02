import {
    Links,
    Meta,
    Scripts,
} from "@remix-run/react";
import { useEffect, useState } from "react";
import axiosInstance from "./util/axiosInterceptor";

export default function App() {
    const [text, setText] = useState<string | null>(null);

    useEffect(() => {
        axiosInstance.get("http://localhost:8080/api/test").then((res) => {
            setText(res.data);
        });
    }, []);

    return (
        <html>
            <head>
                <link
                    rel="icon"
                    href="data:image/x-icon;base64,AA"
                />
                <Meta />
                <Links />
            </head>
            <body>
                <h1>ises</h1>

                {
                    (text == null)
                        ? <p>Submitting a request to the server...</p>
                        : <p>{text}</p>
                }

                <Scripts />
            </body>
        </html>
    );
}
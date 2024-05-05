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
        let jwt: string = "";

        axiosInstance.post("http://localhost:8080/api/auth/login", {
            email: "admin@sbnz.com",
            password: "admin123"
        }).then((res) => {
            jwt = res.data;

            const config = {
                headers: {
                    Authorization: "Bearer " + jwt
                }
            };
            axiosInstance.get("http://localhost:8080/api/test/any", config)
                .then((res) => setText(res.data))
                .catch((err) => console.error(err));
        }).catch((err) => {
            console.error(err);
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
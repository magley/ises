import type { MetaFunction } from "@remix-run/node";
import { useEffect, useState } from "react";
import axiosInstance from "~/util/axiosInterceptor";

export const meta: MetaFunction = () => {
    return [
        { title: "New Remix App" },
        { name: "description", content: "Welcome to Remix!" },
    ];
};


export default function Home() {
    const [text, setText] = useState<string | null>(null);

    useEffect(() => {
        axiosInstance.get("http://localhost:8080/api/test/any")
            .then((res) => setText(res.data))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div>
            Welcome to <b>ises</b>.

            <div>Message from the backend: <b>{text}</b></div>
            <br />
            This page should contain articles and other stuff that all users have
            access to.
        </div>
    );
}
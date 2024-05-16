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

    return (
        <div>
            Welcome to <b>ises</b>.
            <br />
            This page should contain articles and other stuff that all users have
            access to.
        </div>
    );
}
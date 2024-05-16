import { Outlet } from "@remix-run/react";

export default function Article() {
    return (
        <>
            <h1>Layout</h1>
            <article>
                <Outlet />
            </article>
        </>
    );
}
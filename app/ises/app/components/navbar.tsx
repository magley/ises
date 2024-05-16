import { Link, useNavigate } from "@remix-run/react";
import { useEffect, useState } from "react";
import { clearJWT, getJwtRole } from "~/util/localstorage";

export default () => {
    const navigate = useNavigate();

    const logOut = () => {
        clearJWT();
        //navigate("/login");
        window.location.replace("/login");
    }

    interface NavigationItem {
        name: string,
        route: string | null,
        onClick: (() => void) | null
    }

    const navigationGuest: NavigationItem[] = [
        { name: 'Home', route: '/', onClick: null },
        { name: 'Login', route: '/login', onClick: null },
        { name: 'Register', route: '/register', onClick: null },
    ];

    const navigationClient: NavigationItem[] = [
        { name: 'Home', route: '/', onClick: null },
        { name: 'Log Out', route: null, onClick: logOut },
    ];

    const navigationAdmin: NavigationItem[] = [
        { name: 'Home', route: '/', onClick: null },
        { name: 'Log Out', route: null, onClick: logOut },
    ];

    // TODO: This should update whenever the user logs in/logs out.
    const [navigation, setNavigation] = useState(navigationGuest);

    useEffect(() => {
        const role = getJwtRole();

        switch (role) {
            case "SuperAdmin": case "Admin":
                setNavigation(navigationAdmin);
                break;
            case "User": case "Full user":
                setNavigation(navigationClient);
                break;
            default:
                setNavigation(navigationGuest);
                break;
        }
    }, []);

    return (
        <nav className="bg-slate-900 py-2">
            <h1 className="text-indigo-400 rounded-md px-3 py-2 text-2xl font-medium inline-block">ises</h1>
            <span className="space-y-1 px-2 pb-3 pt-2">
                {navigation.map((item) => (
                    item.route ?
                        <Link
                            key={item.name}
                            to={item.route}
                            className="text-gray-300 hover:bg-gray-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">
                            {item.name}
                        </Link> :
                        <Link
                            key={item.name}
                            to=""
                            onClick={(e) => { e.preventDefault(); if (item.onClick != null) item.onClick(); }}
                            className="text-gray-300 hover:bg-gray-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">
                            {item.name}
                        </Link>
                ))}
            </span>
        </nav >
    );
}
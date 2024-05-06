import { Link, useNavigate } from "@remix-run/react";
import { clearJWT } from "~/util/localstorage";

export default () => {
    const navigate = useNavigate();

    const logOut = () => {
        clearJWT();
        navigate("/login");
    }

    const navigationGuest = [
        { name: 'Home', route: '/' },
        { name: 'Login', route: '/login' },
        { name: 'Sign Up', route: '/register' },
        { name: 'Log Out', route: null, onClick: logOut },
    ];

    const navigationClient = [
        { name: 'Home', route: '/' },
    ];

    const navigationAdmin = [
        { name: 'Home', route: '/' },
    ];

    // TODO: This should update whenever the user logs in/logs out.
    const navigation = navigationGuest;


    return (
        <nav className="bg-slate-900 py-2">
            <h1 className="text-indigo-400 rounded-md px-3 py-2 text-2xl font-medium inline-block">ises</h1>
            <span className="space-y-1 px-2 pb-3 pt-2">
                {navigation.map((item) => (
                    item.route &&
                    <Link
                        key={item.name}
                        to={item.route}
                        className="text-gray-300 hover:bg-gray-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">
                        {item.name}
                    </Link>
                ))}
            </span>
            <Link
                to=""
                onClick={(e) => { e.preventDefault(); logOut(); }}
                className="text-gray-300 hover:bg-gray-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">Log out
            </Link>
        </nav >
    );
}
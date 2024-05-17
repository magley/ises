import { Link, NavLink, useNavigate } from "@remix-run/react";
import { Fragment, useEffect, useState } from "react";
import { clearJWT, getJwtEmail, getJwtRole } from "~/util/localstorage";
import { Disclosure, Menu, Transition } from '@headlessui/react'

function classNames(...classes: any[]) {
    return classes.filter(Boolean).join(' ')
}

export default () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState<string>("");

    const logOut = () => {
        clearJWT();
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
    ];

    const navigationFullClient: NavigationItem[] = [
        { name: 'Home', route: '/', onClick: null },
        { name: 'New Article', route: '/article/new', onClick: null },
    ];

    const navigationAdmin: NavigationItem[] = [
        { name: 'Home', route: '/', onClick: null },
    ];

    // TODO: This should update whenever the user logs in/logs out.
    const [navigation, setNavigation] = useState(navigationGuest);

    const [loggedIn, setLoggedIn] = useState(false);

    useEffect(() => {
        const role = getJwtRole();

        setLoggedIn(role != "");

        setEmail(getJwtEmail());

        switch (role) {
            case "SuperAdmin": case "Admin":
                setNavigation(navigationAdmin);
                break;
            case "User":
                setNavigation(navigationClient);
                break;
            case "Full user":
                setNavigation(navigationFullClient);
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
                {
                    navigation.map((item) => (
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
                    ))
                }
                <span>
                    {loggedIn &&
                        <Menu as="div" className="relative ml-3 inline-block float-right mr-5 mt-2">
                            <div>
                                <Menu.Button className="relative flex rounded-full bg-gray-800 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800">
                                    <span className="absolute inset-1.5" />
                                    <span className="sr-only">Open user menu</span>
                                    <img
                                        className="h-8 w-8 rounded-full"
                                        src="nopfp.jpg"
                                        alt=""
                                    />
                                </Menu.Button>
                            </div>

                            <Transition
                                as={Fragment}
                                enter="transition ease-out duration-100"
                                enterFrom="transform opacity-0 scale-95"
                                enterTo="transform opacity-100 scale-100"
                                leave="transition ease-in duration-75"
                                leaveFrom="transform opacity-100 scale-100"
                                leaveTo="transform opacity-0 scale-95"
                            >
                                <Menu.Items className="absolute right-0 z-10 mt-2 w-56 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                                    <div className="py-1">
                                        <Menu.Item>
                                            {({ active }) => (
                                                <>
                                                    <span className='block px-4 py-2 text-sm text-slate-400'>
                                                        {email}
                                                    </span>
                                                    <hr />
                                                </>
                                            )}
                                        </Menu.Item>
                                        <Menu.Item>
                                            {({ active }) => (
                                                <NavLink
                                                    to="/account"
                                                    className={classNames(
                                                        active ? 'bg-indigo-100 text-gray-900' : 'text-gray-700',
                                                        'block px-4 py-2 text-sm'
                                                    )}
                                                >
                                                    <svg className="h-6 w-6 inline-block mr-2 text-gray-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                                    </svg>
                                                    My Account
                                                </NavLink>
                                            )}
                                        </Menu.Item>
                                        <Menu.Item>
                                            {({ active }) => (
                                                <NavLink
                                                    to="#"
                                                    className={classNames(
                                                        active ? 'bg-indigo-100 text-gray-900' : 'text-gray-700',
                                                        'block px-4 py-2 text-sm'
                                                    )}
                                                >
                                                    <svg className="h-6 w-6 inline-block mr-2 text-gray-700" width="24" height="24" viewBox="0 0 24 24" strokeWidth="2" stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">  <path stroke="none" d="M0 0h24v24H0z" />  <circle cx="9" cy="19" r="2" />  <circle cx="17" cy="19" r="2" />  <path d="M3 3h2l2 12a3 3 0 0 0 3 2h7a3 3 0 0 0 3 -2l1 -7h-15.2" /></svg>
                                                    Purchase History
                                                </NavLink>
                                            )}
                                        </Menu.Item>
                                        <Menu.Item>
                                            {({ active }) => (
                                                <button
                                                    type="submit"
                                                    onClick={logOut}
                                                    className={classNames(
                                                        active ? 'bg-indigo-100 text-gray-900' : 'text-gray-700',
                                                        'block w-full px-4 py-2 text-left text-sm'
                                                    )}
                                                >
                                                    <svg className="h-6 w-6 inline-block mr-2 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />  <polyline points="16 17 21 12 16 7" />  <line x1="21" y1="12" x2="9" y2="12" /></svg>
                                                    Sign out
                                                </button>
                                            )}
                                        </Menu.Item>
                                    </div>
                                </Menu.Items>
                            </Transition>
                        </Menu>
                    }
                </span>
            </span>
        </nav >
    );
}
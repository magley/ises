import { Link, NavLink, useNavigate } from "@remix-run/react";
import { Fragment, useEffect, useState } from "react";
import { clearJWT, getIpFromLocalStorage, getJwtEmail, getJwtRole, setIpInLocalStorage } from "~/util/localstorage";
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

    interface DropdownItem {
        name: string,
        route: string | null,
        onClick: (() => void) | null,
        iconFname: string,
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
        { name: 'Users', route: '/admin/users', onClick: null },
        { name: 'Block list', route: '/admin/blocks', onClick: null },
    ];

    //////////////////////////////////////////////////////////////

    const userDropdownClient: DropdownItem[] = [
        { name: 'My Account', route: '/account', onClick: null, iconFname: 'cog.png' },
        { name: 'Purchase History', route: '/history', onClick: null, iconFname: 'cart.png' },
        { name: 'Sign Out', route: null, onClick: logOut, iconFname: 'log_out.png' },
    ];

    const userDropdownAdmin: DropdownItem[] = [
        { name: 'My Account', route: '/account', onClick: null, iconFname: 'cog.png' },
        { name: 'Sign Out', route: null, onClick: logOut, iconFname: 'log_out.png' },
    ];

    // TODO: This should update whenever the user logs in/logs out.
    const [navigation, setNavigation] = useState(navigationGuest);
    const [dropdown, setDropdown] = useState<DropdownItem[] | null>(null);

    const [loggedIn, setLoggedIn] = useState(false);

    //////////////////////////////////////////////////////////////////

    const ips: string[] = ['210.171.28.38', '21.489.26.96', '111.111.111.111', '123.0.0.1'];
    const [ip, setIp] = useState<string>("Fake IP...");

    const onClickSetIp = (ip: string) => {
        setIp(ip);
        setIpInLocalStorage(ip);
    }

    useEffect(() => {
        const role = getJwtRole();

        setIp(getIpFromLocalStorage());

        setLoggedIn(role != "");

        setEmail(getJwtEmail());

        switch (role) {
            case "SuperAdmin": case "Admin":
                setNavigation(navigationAdmin);
                setDropdown(userDropdownAdmin);
                break;
            case "User":
                setNavigation(navigationClient);
                setDropdown(userDropdownClient);
                break;
            case "Full user":
                setNavigation(navigationFullClient);
                setDropdown(userDropdownClient);
                break;
            default:
                setNavigation(navigationGuest);
                setDropdown(null);
                break;
        }
    }, []);

    return (
        <nav className="bg-indigo-900 py-2 shadow-md">
            <h1 className="text-indigo-50 rounded-md px-3 py-2 text-2xl font-bold inline-block">ises</h1>
            <span className="space-y-1 px-2 pb-3 pt-2">
                {
                    navigation.map((item) => (
                        item.route ?
                            <Link
                                key={item.name}
                                to={item.route}
                                className="text-gray-300 hover:bg-indigo-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">
                                {item.name}
                            </Link> :
                            <Link
                                key={item.name}
                                to=""
                                onClick={(e) => { e.preventDefault(); if (item.onClick != null) item.onClick(); }}
                                className="text-gray-300 hover:bg-indigo-700 hover:text-white inline-block rounded-md px-3 py-2 text-base font-extrabold">
                                {item.name}
                            </Link>
                    ))
                }
                <span>
                    {/* Fake IP selector */}
                    <Menu as="div" className="relative inline-block text-left">
                        <div>
                            <Menu.Button className="inline-flex w-full justify-center gap-x-1.5 rounded-md bg-indigo-100 px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50">
                                {ip}
                                <svg className="w-2.5 h-2.5 ms-3 mt-1" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 6">
                                    <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 4 4 4-4" />
                                </svg>
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
                                    {ips.map(ip_ =>
                                        <Menu.Item key={ip_}>
                                            {({ active }) => (
                                                <Link
                                                    to="#"
                                                    onClick={(e) => { e.preventDefault(); onClickSetIp(ip_); }}
                                                    className={classNames(
                                                        active ? 'bg-indigo-100 text-indigo-900' : (ip == ip_ ? 'bg-indigo-200' : 'text-indigo-700'),
                                                        'block px-4 py-2 text-sm'
                                                    )}>
                                                    {ip_}
                                                </Link>

                                            )}
                                        </Menu.Item>)
                                    }
                                </div>
                            </Menu.Items>
                        </Transition>
                    </Menu>

                    {/* User dropdown (if logged in) */}
                    {(loggedIn && dropdown != null) &&
                        <Menu as="div" className="relative ml-3 inline-block float-right mr-5 mt-2">
                            <div>
                                <Menu.Button className="relative flex rounded-full bg-indigo-800 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-indigo-800">
                                    <span className="absolute inset-1.5" />
                                    <span className="sr-only">Open user menu</span>
                                    <img
                                        className="h-8 w-8 rounded-full"
                                        src="/nopfp.jpg"
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
                                <Menu.Items className="absolute right-0 z-10 mt-2 w-56 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-indigo ring-opacity-5 focus:outline-none">
                                    <div key="1331331" className="py-1">
                                        <Menu.Item key="12353263223">
                                            {({ active }) => (
                                                <>
                                                    <span className='block px-4 py-2 text-sm text-gray-400'>
                                                        {email}
                                                    </span>
                                                    <hr />
                                                </>
                                            )}
                                        </Menu.Item>
                                        {dropdown.map((item) => (
                                            <Menu.Item key={item.name}>
                                                {({ active }) => (
                                                    item.route ?
                                                        <Link
                                                            to={item.route}
                                                            className={classNames(
                                                                active ? 'bg-indigo-100 text-indigo-900' : 'text-indigo-700',
                                                                'block px-4 py-2 text-sm'
                                                            )}>
                                                            <img src={`/${item.iconFname}`} className="h-6 w-6 inline-block mr-2 text-indigo-700" />
                                                            {item.name}
                                                        </Link>
                                                        :
                                                        <Link
                                                            to=""
                                                            onClick={(e) => { e.preventDefault(); if (item.onClick != null) item.onClick(); }}
                                                            className={classNames(
                                                                active ? 'bg-indigo-100 text-indigo-900' : 'text-indigo-700',
                                                                'block px-4 py-2 text-sm'
                                                            )}>
                                                            <img src={`/${item.iconFname}`} className="h-6 w-6 inline-block mr-2 text-indigo-700" />
                                                            {item.name}
                                                        </Link>
                                                )}
                                            </Menu.Item>
                                        ))}
                                    </div>
                                </Menu.Items>
                            </Transition>
                        </Menu>
                    }
                </span>
            </span >
        </nav >
    );
}
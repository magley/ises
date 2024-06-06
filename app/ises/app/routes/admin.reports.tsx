import { Link } from "@remix-run/react";
import { Menu, Transition } from "@headlessui/react";
import { Fragment, useState } from "react";
import {
	AdminService,
} from "~/service/admin";

export default function AdminReportsPage() {

    const [reportName, setReportName] = useState("Select report name");
    const [result, setResult] = useState<string[]>([]);

    const reportNames = [
        "attackEventSpecific",
        "attackEventAll",
        "failedLogin",
        "blockEvent",
    ];

    const requestReport = () => {
        if (reportName === "Select report name") {
            return;
        }
        AdminService.report(reportName)
            .then((res) => {
                setResult(res.data);
            })
            .catch((err) => {
                console.log(err);
            })
    }

	return (
        <div className="bg-white dark:bg-slate-800 shadow-md mx-16 mt-16">
            <div className="mx-auto max-w-2xl px-4 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8 flex flex-row gap-1">
                <Menu
                    as="div"
                    className="relative inline-block text-left mt-2"
                >
                    <div>
                        <Menu.Button className="inline-flex w-full justify-center gap-x-1.5 rounded-md dark:bg-slate-800 bg-white px-3 py-2 text-sm font-semibold text-gray-900 dark:text-gray-200 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:hover:bg-slate-600">
                            {reportName}
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
                        <Menu.Items className="absolute right-0 z-10 mt-2 w-56 origin-top-right rounded-md dark:bg-slate-800 bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                            <div className="py-1">
                                {reportNames.map((name) => (
                                    <Menu.Item key={name}>
                                        <Link to="#" onClick={(e) => {
                                            e.preventDefault();
                                            setReportName(name);
                                        }}>
                                            <p>{name}</p>
                                        </Link>
                                    </Menu.Item>
                                ))}
                            </div>
                        </Menu.Items>
                    </Transition>
                </Menu>
                <button
                    onClick={(e) => {
                        e.preventDefault();
                        requestReport();
                    }}
                    disabled={reportName === "Select report name"}
                    className="
                        dark:text-slate-900 dark:disabled:bg-slate-500 dark:disabled:text-slate-700
                        flex justify-center rounded-md bg-indigo-600 px-5 py-1.5 text-sm font-semibold leading-6 
                        text-white shadow-md hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 
                        focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:bg-slate-300 
                        disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none"
                >
                    Request
                </button>
            </div>
            <div className="mx-auto sm:py-6 lg:max-w-7xl lg:px-8">
                {result.length == 0 ? (
                    <p>No results to show</p>
                ) : (
                    result.map((val) => (
                        <p key={val}>{val}</p>
                    ))
                )}
            </div>
        </div>
    );
}

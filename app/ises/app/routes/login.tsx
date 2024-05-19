import { Form, Link, redirect, useNavigate } from "@remix-run/react";
import { useState } from "react";
import toast from "react-hot-toast";
import axiosInstance from "~/util/axiosInterceptor";
import { getJwtShouldNagPassword, setJWT } from "~/util/localstorage";

export default function Login() {
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [errMsg, setErrMsg] = useState<string | null>("");
    const navigate = useNavigate();

    const submitLogin = () => {
        axiosInstance
            .post("http://localhost:8080/api/auth/login", { email, password })
            .then((res) => {
                setErrMsg(null);
                const jwt = res.data;
                setJWT(jwt);
                window.location.replace("/");
                //navigate("/");
            })
            .catch((err) => {
                setErrMsg("Wrong email or password.");
            });
    };

    return (
        <>
            {/* 
                Form design taken from:
                https://tailwindui.com/components/application-ui/forms/sign-in-forms 
                Tailwind installed following this guide:
                https://tailwindcss.com/docs/guides/remix
            */}
            <div className="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8 bg-white shadow-md mx-64 mt-16">
                <div className="sm:mx-auto sm:w-full sm:max-w-sm">
                    <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                        Sign in to your account
                    </h2>
                </div>

                <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
                    <Form className="space-y-6" onSubmit={e => { e.preventDefault(); submitLogin(); }}>
                        <div>
                            <label
                                htmlFor="email"
                                className="block text-sm font-medium leading-6 text-gray-900"
                            >
                                Email address
                            </label>
                            <div className="mt-2">
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    autoComplete="email"
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                />
                            </div>
                        </div>

                        <div>
                            <div className="flex items-center justify-between">
                                <label
                                    htmlFor="password"
                                    className="block text-sm font-medium leading-6 text-gray-900"
                                >
                                    Password
                                </label>
                            </div>
                            <div className="mt-2">
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    autoComplete="current-password"
                                    required
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                    className="block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                />
                            </div>
                        </div>

                        <div>
                            <button
                                type="submit"
                                className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                            >
                                Sign in
                            </button>
                            <div className="mt-5 font-semibold text-red-600">
                                {" "}
                                {errMsg}{" "}
                            </div>
                        </div>
                    </Form>

                    <p className="mt-10 text-center text-sm text-gray-500">
                        Not a member?{" "}
                        <Link
                            to={"/register"}
                            className="font-semibold leading-6 text-indigo-600 hover:text-indigo-500"
                        >
                            Create an account
                        </Link>
                    </p>
                </div>
            </div>
        </>
    );

    // return (
    //     <>
    //         <h1 className="text-3xl font-bold underline">Please sign in</h1>
    //         <Form onSubmit={submitLogin}>
    //             <input type='email' value={email} onChange={(e) => setEmail(e.target.value)} />  <br />
    //             <input type='password' value={password} onChange={(e) => setPassword(e.target.value)} /> <br />
    //             <button type='submit'>Sign in</button> <br />
    //         </Form >
    //         <div> {errMsg} </div>
    //     </>
    // );
}

import { Form } from "@remix-run/react";
import { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { PasswordChangeDTO, UserDTO, UserService } from "~/service/user";
import { clearJWT, getJwtId } from "~/util/localstorage";

export default function MyAccount() {
	const [userId, setUserId] = useState<number>(-1);
	const [user, setUser] = useState<UserDTO | null>(null);

	useEffect(() => {
		const id = getJwtId();
		if (id == -1) {
			// Redirect to home or login...?
		}

		UserService.findById(id)
			.then((res) => {
				setUser(res.data);
			})
			.catch((err) => {
				console.error(err);
			});
	}, []);

	const [currentPass, setCurrentPass] = useState("");
	const [newPass, setNewPass] = useState("");
	const [confirmNewPass, setConfirmNewPass] = useState("");
	const [errMsg, setErrMsg] = useState<string | null>("");

	const validateForm = (): FormValidation => {
		let res: FormValidation = {
			invalid: false,
			invalid_field: "",
			invalid_message: "",
		};

		let required = [currentPass, newPass, confirmNewPass];
		for (let field of required) {
			if (field.trim().length == 0) {
				res.invalid = true;
				res.invalid_message = "Field must not be empty.";
				return res;
			}
		}

		if (newPass != confirmNewPass) {
			res.invalid = true;
			res.invalid_message = "Passwords do not match.";
			return res;
		}

		if (currentPass == newPass) {
			res.invalid = true;
			res.invalid_message = "New password must be different.";
			return res;
		}

		return res;
	};

	const submitChangePassword = () => {
		const formValidation = validateForm();
		if (formValidation.invalid) {
			setErrMsg(formValidation.invalid_message);
			return;
		} else {
			setErrMsg(null);
		}

		const dto: PasswordChangeDTO = {
			currentPassword: currentPass,
			newPassword: newPass,
		};

		UserService.changePassword(dto)
			.then((res) => {
				console.log("hoorah");
				clearJWT();
				window.location.replace("/login");
			})
			.catch((err: AxiosError) => {
				if (err.response) {
					if (err.response.status == 400) {
						setErrMsg(err.response.data as string);
					}
				}
				console.error(err);
			});
	};

	return (
		<>
			{user && (
				<>
					<div className="grid grid-cols-2 gap-4 p-10 text-xl">
						<div className="p-7 bg-white dark:bg-slate-800 rounded-lg shadow-md">
							<h2 className="font-bold">Account info</h2>
							<div className="grid grid-rows-3 gap-5 p-10">
								<span>
									<span className="font-medium leading-6 text-gray-900 dark:text-slate-500">
										E-mail address:{" "}
									</span>
									{user.email}
								</span>
								<span>
									<span className="font-medium leading-6 text-gray-900 dark:text-slate-500">
										Name:{" "}
									</span>
									{user.name}
								</span>
								<span>
									<span className="font-medium leading-6 text-gray-900 dark:text-slate-500">
										Last name:{" "}
									</span>
									{user.lastName}
								</span>
							</div>
						</div>
						<div className="p-7 bg-white dark:bg-slate-800 rounded-lg shadow-md">
							<h2 className="font-bold">Change your password</h2>

							<Form
								className="space-y-6 mt-3 p-8"
								onSubmit={(e) => {
									e.preventDefault();
									submitChangePassword();
								}}
							>
								<div>
									<div className="flex items-center justify-between">
										<label
											htmlFor="password"
											className="block text-sm font-medium leading-6 text-gray-900 dark:text-slate-500"
										>
											Your current password
										</label>
									</div>
									<div className="mt-2">
										<input
											id="password"
											name="password"
											type="password"
											autoComplete="password"
											required
											value={currentPass}
											onChange={(e) =>
												setCurrentPass(e.target.value)
											}
											className="dark:text-gray-100 dark:placeholder:text-gray-400 dark:bg-slate-800 block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-md ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
										/>
									</div>
								</div>

								<div>
									<div className="flex items-center justify-between">
										<label
											htmlFor="password"
											className="block text-sm font-medium leading-6 text-gray-900 dark:text-slate-500"
										>
											New password
										</label>
									</div>
									<div className="mt-2">
										<input
											id="password"
											name="password"
											type="password"
											autoComplete="password"
											required
											value={newPass}
											onChange={(e) =>
												setNewPass(e.target.value)
											}
											className="dark:text-gray-100 dark:placeholder:text-gray-400 dark:bg-slate-800 block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-md ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
										/>
									</div>
								</div>

								<div>
									<div className="flex items-center justify-between">
										<label
											htmlFor="passwordConfirm"
											className="block text-sm font-medium leading-6 text-gray-900 dark:text-slate-500"
										>
											Confirm new password
										</label>
									</div>
									<div className="mt-2">
										<input
											id="passwordConfirm"
											name="passwordConfirm"
											type="password"
											autoComplete="password-confirm"
											required
											value={confirmNewPass}
											onChange={(e) =>
												setConfirmNewPass(
													e.target.value
												)
											}
											className="dark:text-gray-100 dark:placeholder:text-gray-400 dark:bg-slate-800 block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-md ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
										/>
									</div>
								</div>

								<div>
									<button
										type="submit"
										className="flex px-10 justify-center rounded-md bg-indigo-600 py-1.5 text-sm font-semibold leading-6 text-white shadow-md hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
									>
										Change password
									</button>
									<div className="mt-2 font-thin text-sm">
										You will be signed out after this.
									</div>
									<div className="mt-5 font-semibold text-base text-red-600">
										{" "}
										{errMsg}{" "}
									</div>
								</div>
							</Form>
						</div>
					</div>
				</>
			)}
		</>
	);
}

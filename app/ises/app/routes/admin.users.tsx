import { Transition, Dialog, Menu } from "@headlessui/react";
import { NavLink } from "@remix-run/react";
import { AxiosError } from "axios";
import { Fragment, useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";
import { RoleDTO, RoleService } from "~/service/role";
import { SetUserRoleDTO, UserDTO, UserService } from "~/service/user";
import { getJwtId } from "~/util/localstorage";

function classNames(...classes: any[]) {
	return classes.filter(Boolean).join(" ");
}

export default function AdminBlocksPage() {
	const [users, setUsers] = useState<UserDTO[]>([]);
	const [availableRoles, setAvailableRoles] = useState<RoleDTO[]>([]);

	// "Modify user role" state.
	const [modifyRoleDialogOpen, setModifyRoleDialogOpen] = useState(false);
	const cancelButtonRef = useRef(null);
	const [modifyUserId, setModifyUserId] = useState(-1);
	const [modifyRoleEmail, setModifyRoleEmail] = useState("");
	const [modifyRoleRole, setModifyRoleRole] = useState("");

	const openModifyRoleDialog = (id: number, email: string, role: string) => {
		setModifyUserId(id);
		setModifyRoleDialogOpen(true);
		setModifyRoleEmail(email);
		setModifyRoleRole(role);
	};

	const onSubmitChangesToUserRole = () => {
		const dto: SetUserRoleDTO = {
			userId: modifyUserId,
			roleId: availableRoles.filter((r) => r.name == modifyRoleRole)[0]
				.id,
		};

		UserService.changeRole(dto)
			.then((res) => {
				toast.success("Role changed!");
				setModifyRoleDialogOpen(false);
				loadData();
			})
			.catch((err: AxiosError) => {
				if (err.response) {
					if (err.response.status == 400) {
						toast.error(err.response.data as string);
					}
				}
				console.error(err);
			});
	};

	const loadData = () => {
		loadUsers();
		loadAvailableRoles();
	};

	const loadAvailableRoles = () => {
		RoleService.findAll()
			.then((res) => {
				setAvailableRoles(res.data);
			})
			.catch((err) => {
				console.error(err);
			});
	};

	const loadUsers = () => {
		UserService.findAll()
			.then((res) => {
				setUsers(res.data);
			})
			.catch((err) => {
				console.error(err);
			});
	};

	useEffect(() => {
		loadData();
	}, []);

	return (
		<div>
			{/* Modal  */}
			<Transition.Root show={modifyRoleDialogOpen} as={Fragment}>
				<Dialog
					className="relative z-10"
					initialFocus={cancelButtonRef}
					onClose={setModifyRoleDialogOpen}
				>
					<Transition.Child
						as={Fragment}
						enter="ease-out duration-300"
						enterFrom="opacity-0"
						enterTo="opacity-100"
						leave="ease-in duration-200"
						leaveFrom="opacity-100"
						leaveTo="opacity-0"
					>
						<div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
					</Transition.Child>

					<div className="fixed inset-0 z-10 w-screen">
						<div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
							<Transition.Child
								as={Fragment}
								enter="ease-out duration-300"
								enterFrom="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
								enterTo="opacity-100 translate-y-0 sm:scale-100"
								leave="ease-in duration-200"
								leaveFrom="opacity-100 translate-y-0 sm:scale-100"
								leaveTo="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
							>
								<Dialog.Panel className="relative transform rounded-lg dark:bg-slate-900 bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
									<div className="dark:bg-slate-900 bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
										<div className="sm:flex sm:items-start">
											<div className="mx-auto flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"></div>
											<div className="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-left">
												<Dialog.Title
													as="h3"
													className="text-base font-semibold leading-6 text-gray-900 dark:text-gray-100"
												>
													Change role
												</Dialog.Title>
												<div className="mt-2">
													<p className="text-sm text-gray-500">
														You can change the role
														of {modifyRoleEmail}{" "}
														here.
													</p>
													<div>
														<Menu
															as="div"
															className="relative inline-block text-left mt-2"
														>
															<div>
																<Menu.Button className="inline-flex w-full justify-center gap-x-1.5 rounded-md dark:bg-slate-800 bg-white px-3 py-2 text-sm font-semibold text-gray-900 dark:text-gray-200 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 dark:hover:bg-slate-600">
																	{
																		modifyRoleRole
																	}
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
																		{availableRoles.map(
																			(
																				role
																			) => (
																				<Menu.Item
																					key={
																						role.id
																					}
																				>
																					{({
																						active,
																					}) => (
																						<NavLink
																							to=""
																							onClick={(
																								e
																							) => {
																								e.preventDefault();
																								setModifyRoleRole(
																									role.name
																								);
																							}}
																							className={classNames(
																								modifyRoleRole ==
																									role.name
																									? "bg-indigo-100 text-gray-900 dark:bg-slate-600 dark:text-gray-100"
																									: active
																									? "bg-gray-100 text-gray-900 dark:bg-slate-700 dark:text-gray-100"
																									: "text-gray-700 dark:text-gray-400",
																								"block px-4 py-2 text-sm"
																							)}
																						>
																							{
																								role.name
																							}
																						</NavLink>
																					)}
																				</Menu.Item>
																			)
																		)}
																	</div>
																</Menu.Items>
															</Transition>
														</Menu>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div className="dark:bg-slate-900 bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
										<button
											type="button"
											className="inline-flex w-full justify-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 sm:ml-3 sm:w-auto"
											onClick={() =>
												onSubmitChangesToUserRole()
											}
										>
											Confirm
										</button>
										<button
											type="button"
											className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
											onClick={() =>
												setModifyRoleDialogOpen(false)
											}
											ref={cancelButtonRef}
										>
											Cancel
										</button>
									</div>
								</Dialog.Panel>
							</Transition.Child>
						</div>
					</div>
				</Dialog>
			</Transition.Root>

			{/* Main */}
			{users.length == 0 ? (
				<div>There are no users in the system.</div>
			) : (
				<div className="my-10 mx-48 border-slate-300 border-2 shadow-md bg-white">
					<table className="table-auto w-full text-sm text-left rtl:text-right">
						<caption className="p-5 text-lg font-semibold text-left rtl:text-right text-slate-900 bg-white dark:text-white dark:bg-slate-900">
							Users
							<p className="mt-1 text-sm font-normal text-slate-500 dark:text-slate-400">
								Users of the system.
							</p>
						</caption>
						<thead className="text-xs text-slate-700 dark:text-slate-100 uppercase bg-slate-50 dark:bg-slate-800">
							<tr>
								<th className="px-6 py-3">ID</th>
								<th className="px-6 py-3">Email</th>
								<th className="px-6 py-3">Name</th>
								<th className="px-6 py-3">Last name</th>
								<th className="px-6 py-3">Role</th>
							</tr>
						</thead>
						<tbody>
							{users.map((u) => (
								<tr
									key={u.id}
									className="bg-white dark:bg-slate-700 border-b hover:bg-slate-50 dark:hover:bg-slate-600"
								>
									<td className="px-6 py-4">{u.id}</td>
									<td className="px-6 py-4">
										{u.email}{" "}
										{u.id == getJwtId() ? "(You)" : ""}{" "}
									</td>
									<td className="px-6 py-4">{u.name}</td>
									<td className="px-6 py-4">{u.lastName}</td>
									<td className="px-6 py-4">
										<NavLink
											className="font-medium text-indigo-400"
											to=""
											onClick={(e) => {
												e.preventDefault();
												openModifyRoleDialog(
													u.id,
													u.email,
													u.role
												);
											}}
										>
											{u.role}
										</NavLink>
									</td>
								</tr>
							))}
						</tbody>
					</table>
				</div>
			)}
		</div>
	);
}

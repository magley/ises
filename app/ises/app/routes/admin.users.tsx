import { useEffect, useState } from "react";
import { UserDTO, UserService } from "~/service/user";
import { getJwtId } from "~/util/localstorage";

export default function AdminBlocksPage() {
    const [users, setUsers] = useState<UserDTO[]>([]);

    const loadUsers = () => {
        UserService.findAll().then((res) => {
            setUsers(res.data);
        }).catch((err) => {
            console.error(err);
        });
    }

    useEffect(() => {
        loadUsers();
    }, []);

    return (<div>
        {
            users.length == 0 ?
                <div>
                    There are no users in the system.
                </div>
                :
                <div className="my-10 mx-48 border-gray-300 border-2 shadow-md bg-white">
                    <table className="table-auto w-full text-sm text-left rtl:text-right">
                        <caption className="p-5 text-lg font-semibold text-left rtl:text-right text-gray-900 bg-white dark:text-white dark:bg-gray-800">
                            Users
                            <p className="mt-1 text-sm font-normal text-gray-500 dark:text-gray-400">Users of the system.</p>
                        </caption>
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50 ">
                            <tr>
                                <th className="px-6 py-3">ID</th>
                                <th className="px-6 py-3">Email</th>
                                <th className="px-6 py-3">Name</th>
                                <th className="px-6 py-3">Last name</th>
                                <th className="px-6 py-3">Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id} className="bg-white border-b hover:bg-gray-50">
                                    <td className="px-6 py-4">{u.id}</td>
                                    <td className="px-6 py-4">{u.email} {u.id == getJwtId() ? '(You)' : ''} </td>
                                    <td className="px-6 py-4">{u.name}</td>
                                    <td className="px-6 py-4">{u.lastName}</td>
                                    <td className="px-6 py-4">{u.role}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
        }
    </div>);
}
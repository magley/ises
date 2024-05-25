import { NavLink } from "@remix-run/react";
import { useEffect, useState } from "react";
import { AdminService, AlarmDTO } from "~/service/admin";

export default function Alarms() {
    const [alarms, setAlarms] = useState<AlarmDTO[]>([]);

    useEffect(() => {
        const interval = setInterval(() => {
            getAlarms();
        }, 1000 * 2);

        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        getAlarms();
    }, []);

    const handleAlarm = (alarmUuid: string) => {
        AdminService.markAlarmAsRead(alarmUuid).then(() => {
            getAlarms();
        }).catch((err) => {
            console.error(err);
        });
    }

    const getAlarms = () => {
        AdminService.getActiveAlarms().then((res) => {
            setAlarms(res.data);
        }).catch((err) => {
            console.error(err);
        });
    }

    return (
        <div>
            {alarms.length == 0 ? (
                <div>There are no alarms at the moment.</div>
            ) : (
                <div className="my-10 mx-48 border-gray-300 border-2 shadow-md  bg-white">
                    <table className="table-auto w-full text-sm text-left rtl:text-right">
                        <caption className="p-5 text-lg font-semibold text-left rtl:text-right text-gray-900 bg-white dark:text-white dark:bg-slate-900">
                            Alarms
                            <p className="mt-1 text-sm font-normal text-gray-500 dark:text-gray-400">
                                Alarms in the system.
                            </p>
                        </caption>
                        <thead className="text-xs dark:bg-slate-800 dark:text-gray-400 text-gray-700 uppercase bg-gray-50 ">
                            <tr>
                                <th className="px-6 py-3">UUID</th>
                                <th className="px-6 py-3">Severity</th>
                                <th className="px-6 py-3">Type</th>
                                <th className="px-6 py-3">Description</th>
                                <th className="px-6 py-3"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {alarms.map((alarm) => (
                                <tr
                                    key={alarm.uuid}
                                    className="bg-white border-b hover:bg-gray-50 dark:bg-slate-700 dark:text-gray-300 dark:hover:bg-slate-600"
                                >
                                    <td className="px-6 py-4">{alarm.uuid}</td>
                                    <td className="px-6 py-4">{alarm.severity}</td>
                                    <td className="px-6 py-4">{alarm.type}</td>
                                    <td className="px-6 py-4">{alarm.description}</td>
                                    <td className="px-6 py-4">
                                        <NavLink
                                            className="font-medium text-indigo-400 hover:bg-indigo-400 hover:text-white rounded-md px-3 py-2"
                                            to=""
                                            onClick={(e) => {
                                                e.preventDefault();
                                                handleAlarm(alarm.uuid);
                                            }}
                                        >
                                            Mark as read
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
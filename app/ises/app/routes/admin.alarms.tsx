import { NavLink } from "@remix-run/react";
import classNames from "classnames";
import { useEffect, useState } from "react";
import { AdminService, AlarmDTO } from "~/service/admin";
import pkg from 'lodash';
const { orderBy } = pkg;


class Sorter {
    column: string = "";
    asc: boolean = true;

    constructor(column: string, asc: boolean) {
        this.column = column;
        this.asc = asc;
    }
}

export default function Alarms() {
    const [alarms, setAlarms] = useState<AlarmDTO[]>([]);
    const [sorter, setSorter] = useState<Sorter>(new Sorter('uuid', true));

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
            const arr = sortAlarms(res.data);
            setAlarms(arr);
        }).catch((err) => {
            console.error(err);
        });
    }

    const onClickSort = (column: string) => {
        if (sorter.column == column) {
            sorter.asc = !sorter.asc;
        } else {
            sorter.column = column;
            sorter.asc = true;
        }

        const arr = sortAlarms(alarms);
        setAlarms(arr);
    }

    const sortAlarms = (data: AlarmDTO[]): AlarmDTO[] => {
        const res = orderBy(data, [sorter.column], [sorter.asc ? 'asc' : 'desc'])
        return res;
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
                                <th className="px-6 py-3" onClick={(e) => onClickSort('uuid')}>UUID</th>
                                <th className="px-6 py-3" onClick={(e) => onClickSort('severity')}>Severity</th>
                                <th className="px-6 py-3" onClick={(e) => onClickSort('type')}>Type</th>
                                <th className="px-6 py-3" onClick={(e) => onClickSort('description')}>Description</th>
                                <th className="px-6 py-3"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {alarms.map((alarm) => (
                                <tr
                                    key={alarm.uuid}
                                    className="bg-white border-b hover:bg-gray-50 dark:bg-slate-700 dark:text-gray-300 dark:hover:bg-slate-600"
                                >
                                    <td className="px-6 py-4"><div className="text-xs">{alarm.uuid}</div></td>
                                    <td className='px-6 py-4'>
                                        <div className={classNames(`m-1 p-1.5 rounded-xl text-center shadow-md font-bold`, {
                                            'bg-red-400 text-black': alarm.severity == 'CRITICAL',
                                            'bg-red-500 text-white': alarm.severity == 'HIGH',
                                            'bg-yellow-300 text-black': alarm.severity == 'NORMAL',
                                        })}>{alarm.severity}</div>
                                    </td>
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
            )
            }
        </div >
    );
}
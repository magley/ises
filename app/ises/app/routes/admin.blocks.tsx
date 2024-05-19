import { NavLink } from "@remix-run/react";
import { useEffect, useState } from "react";
import { AdminService, BlockEventDTO } from "~/service/admin";

export default function AdminBlocksPage() {
    const [blocked, setBlocked] = useState<BlockEventDTO[]>([]);

    const loadBlocked = () => {
        AdminService.getBlocks().then((res) => {
            setBlocked(res.data);
        }).catch((err) => {
            console.error(err);
        });
    }

    const unblock = (ip: string) => {
        AdminService.unblockIP(ip).then((res) => {
            loadBlocked();
        }).catch((err) => {
            console.error(err);
        })
    }

    const durationText = (ms: number) => {
        const msInS = 1000;
        const msInM = msInS * 60;
        const msInH = msInM * 60;
        const msInD = msInH * 24;
        if (ms >= msInD) {
            return `${Math.round(ms / msInD)} days`;
        }
        if (ms >= msInH) {
            return `${Math.round(ms / msInH)} hours`;
        }
        if (ms >= msInM) {
            return `${Math.round(ms / msInM)} minutes`;
        }
        if (ms >= msInS) {
            return `${Math.round(ms / msInS)} seconds`;
        }
    }

    useEffect(() => {
        loadBlocked();
    }, []);

    return (<div>
        {
            blocked.length == 0 ?
                <div>
                    Nobody is blocked at the moment.
                </div>
                :
                <div className="my-10 mx-48 border-gray-300 border-2 shadow-md bg-white">
                    <table className="table-auto w-full text-sm text-left rtl:text-right">
                        <caption className="p-5 text-lg font-semibold text-left rtl:text-right text-gray-900 bg-white dark:text-white dark:bg-gray-800">
                            Blocked IP addresses
                            <p className="mt-1 text-sm font-normal text-gray-500 dark:text-gray-400">Below is a list of IP addresses that are currently blocked from using the system.</p>
                        </caption>
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50 ">
                            <tr>
                                <th className="px-6 py-3">IP address</th>
                                <th className="px-6 py-3">Duration</th>
                                <th className="px-6 py-3">Reason</th>
                                <th className="px-6 py-3"></th>
                            </tr>
                        </thead>
                        <tbody>
                            {blocked.map(block => (
                                <tr key={block.ip} className="bg-white border-b hover:bg-gray-50">
                                    <td className="px-6 py-4">{block.ip}</td>
                                    <td className="px-6 py-4">{durationText(block.duration)}</td>
                                    <td className="px-6 py-4">{block.reason}</td>
                                    <td className="px-6 py-4">
                                        <NavLink
                                            className="font-medium text-indigo-400 hover:bg-indigo-400 hover:text-white rounded-md px-3 py-2"
                                            to=""
                                            onClick={(e) => { e.preventDefault(); unblock(block.ip); }}>Unblock</NavLink>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
        }
    </div>);
}
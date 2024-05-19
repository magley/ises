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
                blocked.map(block => (
                    <div>
                        {block.ip}<br />
                        {block.duration}<br />
                        {block.reason}<br />
                        <NavLink
                            to=""
                            onClick={(e) => { e.preventDefault(); unblock(block.ip); }}>Unblock</NavLink>
                    </div>
                ))
        }
    </div>);
}
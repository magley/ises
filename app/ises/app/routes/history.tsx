import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import { ArticlePurchaseDTO, ArticleService } from "~/service/article";

export default function PurchaseHistory() {
    const [purchases, setPurchases] = useState<ArticlePurchaseDTO[]>([]);

    useEffect(() => {
        loadHistory();
    }, []);

    const loadHistory = () => {
        ArticleService.getPurchases().then((res) => {
            res.data.sort((c1, c2) => {
                const d1 = new Date(c1.timestamp as unknown as string);
                const d2 = new Date(c2.timestamp as unknown as string);
                return d2.getTime() - d1.getTime();
            });
            setPurchases(res.data);
        }).catch((err) => {
            console.error(err);
        })
    }

    return (
        <>
            <div className="p-10">

                {purchases.length == 0 ?
                    <div className="text-center font-light mb-5 ml-5 mt-40">
                        <div className="text-6xl">You have made no purchases yet. </div>
                        <div className="text-5xl mt-5">Why not <NavLink to="/" className="text-indigo-300">take a look around</NavLink>?</div>
                    </div>
                    :
                    <div>
                        <h1 className="text-4xl font-semibold mb-12 text-center">My purchases</h1>
                        <ul role="list" className="divide-y divide-gray-100 mx-56">
                            {purchases.map((p) => (
                                <li key={p.id} className="flex justify-between gap-x-6 py-5 bg-white p-10 my-10 rounded-2xl shadow-md">
                                    <div className="shadow-md flex aspect-h-1 aspect-w-1 w-36 h-36 overflow-hidden rounded-md bg-gray-50 lg:aspect-none group-hover:opacity-75">
                                        <img
                                            src={`data:image/png;base64,${p.article.imgBase64}`}
                                            className="p-5 h-full w-full object-cover object-center lg:h-full lg:w-full"
                                        />
                                    </div>
                                    <div className="flex min-w-0 gap-x-4">
                                        {/* <img className="h-12 w-12 flex-none rounded-full bg-gray-50" src={person.imageUrl} alt="" /> */}
                                        <div className="min-w-0 flex-auto">
                                            <p className="text-2xl font-semibold leading-6 mb-3 text-gray-900">{p.article.name}</p>
                                            <p className="mt-1 truncate text-lg leading-5 text-gray-500">${p.article.price}</p>
                                        </div>
                                    </div>
                                    <div className="shrink-0 sm:flex sm:flex-col sm:items-end">
                                        <p className="mt-1 text-lg leading-5 text-gray-500 font-bold">
                                            Time of purchase <p className="font-normal mt-3">{p.timestamp.toLocaleString()}</p>
                                        </p>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                }
            </div>
        </>
    );
}
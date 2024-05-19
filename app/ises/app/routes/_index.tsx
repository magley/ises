import type { MetaFunction } from "@remix-run/node";
import { Link } from "@remix-run/react";
import { useEffect, useState } from "react";
import { ArticleDTO, ArticleService } from "~/service/article";
import axiosInstance from "~/util/axiosInterceptor";

export const meta: MetaFunction = () => {
    return [
        { title: "ises" },
        { name: "description", content: "ises - [Buy] something from [here]" },
    ];
};

export default function Home() {
    const [articles, setArticles] = useState<ArticleDTO[]>([]);

    function loadArticles() {
        ArticleService.getAllArticles().then((res) => {
            setArticles(res.data);
        }).catch((err) => {
            console.error(err);
        })
    }

    useEffect(() => {
        loadArticles();
    }, []);

    return (
        <div key="what" className="bg-white shadow-md mx-16 mt-16">
            <div className="mx-auto max-w-2xl px-4 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8">
                <h2 className="text-2xl font-bold tracking-tight text-gray-900">Browse our articles</h2>

                <div className="mt-6 grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 xl:gap-x-8">
                    {articles.map((product) => (
                        <div key={product.id} className="group relative shadow-md p-5">
                            <div className="aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-md bg-gray-200 lg:aspect-none group-hover:opacity-75 lg:h-80">
                                <img
                                    src={`data:image/png;base64,${product.imgBase64}`}
                                    className="h-full w-full object-cover object-center lg:h-full lg:w-full"
                                />
                            </div>
                            <div className="mt-4 flex justify-between">
                                <div>
                                    <h3 className="text-sm text-gray-700">
                                        <Link to={`article/${product.id}`}>
                                            <span aria-hidden="true" className="absolute inset-0" />
                                            {product.name}
                                        </Link>
                                    </h3>
                                </div>
                                <p className="text-sm font-medium text-gray-900">${product.price}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}

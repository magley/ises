import type { MetaFunction } from "@remix-run/node";
import { Link } from "@remix-run/react";
import { useEffect, useState } from "react";
import { ArticleDTO } from "~/service/article";
import axiosInstance from "~/util/axiosInterceptor";

export const meta: MetaFunction = () => {
    return [
        { title: "ises" },
        { name: "description", content: "ises - [Buy] something from [here]" },
    ];
};

const products: ArticleDTO[] = [
    {
        id: 1,
        name: 'T-Shirt',
        imageB64: 'iVBORw0KGgoAAAANSUhEUgAAAFgAAABCCAIAAABcuoaeAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAANSSURBVHhe7ZqxixNBFMZzlcED9cBAjrOInIVgZWltK5gydimDVdBCLCwllZ0QsUkv2Njkz0jpf2AjanO6qS5+5H15xOzu7GR2JsnJ+7GFuzP77Xu/zO1t9mwsjCUmgpgIYiKIiSAmgpgI8l+JmE6nnU6n8S84MplMOKMcXxHIyl/DB886arKh4HHz+pfW2bPjG7LbbDY5rxwvEePxWBKDQZUIYVw8CpcA+Hp698ede9/OzrnfqG7TS0S73UbWhuZK8vMjrg7HCh0MBrAgGw/FEiFZovnn+QMereLXw0da0Oy0o0aCdZR9/goUyEy9LgfiitB0Hq0CyvQUbFio/eObEgXQUuUPi7tzrDj4xfbxyVOesEIvyqn7FfHn/Qc9RTfUva4jDCwu5GhmfpHqEE/YrwilvhH58DcSdPv9+g2vtEKHeH5cEVjbko7GOODN5Xx+8eKV1re+ocPCG7C7ed3yFoCOMiuuiLe3bku6//1yA4eObbeL50OkMTeHTpPKAQfK2ULEtaMjvQAHQgkzUvjhF6KnSOWAA+VsIQLoBThQm8Lbx/rm3/w6ejrrPnwRidA6WbeJYN3pRMw/febYQaJ1su7oIr537ssFgn9x7IZUIvA1VuJenrTkAmH3sJ2RSsRoNGLeCp9vCnsklQgwHA4Z6ST4m2VcEorIsqzf7zO1iujrJf811H2JhCLW8ZQScXXIm6E8ZTp2JKKM/K3E52WhD4wrIa9DX4XIBH1h4yCmCGFjvcAOB2rArNUHW7YkdQ3Kk/u7k5Ycl7PcxBch6M01yqKQKMD9JQ4dskC4v18RqJJV+NXhhkFFUT43LE51kkoE0Mew2WzGQ6FIDuB+Efk7lMIZThKK6PV6Uke32+WhUCQHcL+c/ALxuVOChCKwEFiL32figCm1cxwkjAYs30SwfBPB8k2E/uKo+VglIYD7CUgrItZjlYQA7icgrYhYj1WMuLoiADswEezARLADE8EOTAQ7MBHswESwAxPBDkwEOzAR7MBEsAMTwQ5MBDswEezARLADE8EOTAQ7MBHsoNGo818EGHGlRej7W/xjWxeY31kiCYADCUguwvEnyQAYmoDkIkAsF55/xQxjFyJAsIter5dlGVNSsiMRh4+JICZiyWLxF4pLkguoYtLQAAAAAElFTkSuQmCC',
        price: 35,
    },
    {
        id: 2,
        name: 'Thing',
        imageB64: 'iVBORw0KGgoAAAANSUhEUgAAAFgAAABCCAIAAABcuoaeAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAANSSURBVHhe7ZqxixNBFMZzlcED9cBAjrOInIVgZWltK5gydimDVdBCLCwllZ0QsUkv2Njkz0jpf2AjanO6qS5+5H15xOzu7GR2JsnJ+7GFuzP77Xu/zO1t9mwsjCUmgpgIYiKIiSAmgpgI8l+JmE6nnU6n8S84MplMOKMcXxHIyl/DB886arKh4HHz+pfW2bPjG7LbbDY5rxwvEePxWBKDQZUIYVw8CpcA+Hp698ede9/OzrnfqG7TS0S73UbWhuZK8vMjrg7HCh0MBrAgGw/FEiFZovnn+QMereLXw0da0Oy0o0aCdZR9/goUyEy9LgfiitB0Hq0CyvQUbFio/eObEgXQUuUPi7tzrDj4xfbxyVOesEIvyqn7FfHn/Qc9RTfUva4jDCwu5GhmfpHqEE/YrwilvhH58DcSdPv9+g2vtEKHeH5cEVjbko7GOODN5Xx+8eKV1re+ocPCG7C7ed3yFoCOMiuuiLe3bku6//1yA4eObbeL50OkMTeHTpPKAQfK2ULEtaMjvQAHQgkzUvjhF6KnSOWAA+VsIQLoBThQm8Lbx/rm3/w6ejrrPnwRidA6WbeJYN3pRMw/febYQaJ1su7oIr537ssFgn9x7IZUIvA1VuJenrTkAmH3sJ2RSsRoNGLeCp9vCnsklQgwHA4Z6ST4m2VcEorIsqzf7zO1iujrJf811H2JhCLW8ZQScXXIm6E8ZTp2JKKM/K3E52WhD4wrIa9DX4XIBH1h4yCmCGFjvcAOB2rArNUHW7YkdQ3Kk/u7k5Ycl7PcxBch6M01yqKQKMD9JQ4dskC4v18RqJJV+NXhhkFFUT43LE51kkoE0Mew2WzGQ6FIDuB+Efk7lMIZThKK6PV6Uke32+WhUCQHcL+c/ALxuVOChCKwEFiL32figCm1cxwkjAYs30SwfBPB8k2E/uKo+VglIYD7CUgrItZjlYQA7icgrYhYj1WMuLoiADswEezARLADE8EOTAQ7MBHswESwAxPBDkwEOzAR7MBEsAMTwQ5MBDswEezARLADE8EOTAQ7MBHsoNGo818EGHGlRej7W/xjWxeY31kiCYADCUguwvEnyQAYmoDkIkAsF55/xQxjFyJAsIter5dlGVNSsiMRh4+JICZiyWLxF4pLkguoYtLQAAAAAElFTkSuQmCC',
        price: 17,
    },
    {
        id: 3,
        name: 'Bottle of air',
        imageB64: 'iVBORw0KGgoAAAANSUhEUgAAAFgAAABCCAIAAABcuoaeAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAANSSURBVHhe7ZqxixNBFMZzlcED9cBAjrOInIVgZWltK5gydimDVdBCLCwllZ0QsUkv2Njkz0jpf2AjanO6qS5+5H15xOzu7GR2JsnJ+7GFuzP77Xu/zO1t9mwsjCUmgpgIYiKIiSAmgpgI8l+JmE6nnU6n8S84MplMOKMcXxHIyl/DB886arKh4HHz+pfW2bPjG7LbbDY5rxwvEePxWBKDQZUIYVw8CpcA+Hp698ede9/OzrnfqG7TS0S73UbWhuZK8vMjrg7HCh0MBrAgGw/FEiFZovnn+QMereLXw0da0Oy0o0aCdZR9/goUyEy9LgfiitB0Hq0CyvQUbFio/eObEgXQUuUPi7tzrDj4xfbxyVOesEIvyqn7FfHn/Qc9RTfUva4jDCwu5GhmfpHqEE/YrwilvhH58DcSdPv9+g2vtEKHeH5cEVjbko7GOODN5Xx+8eKV1re+ocPCG7C7ed3yFoCOMiuuiLe3bku6//1yA4eObbeL50OkMTeHTpPKAQfK2ULEtaMjvQAHQgkzUvjhF6KnSOWAA+VsIQLoBThQm8Lbx/rm3/w6ejrrPnwRidA6WbeJYN3pRMw/febYQaJ1su7oIr537ssFgn9x7IZUIvA1VuJenrTkAmH3sJ2RSsRoNGLeCp9vCnsklQgwHA4Z6ST4m2VcEorIsqzf7zO1iujrJf811H2JhCLW8ZQScXXIm6E8ZTp2JKKM/K3E52WhD4wrIa9DX4XIBH1h4yCmCGFjvcAOB2rArNUHW7YkdQ3Kk/u7k5Ycl7PcxBch6M01yqKQKMD9JQ4dskC4v18RqJJV+NXhhkFFUT43LE51kkoE0Mew2WzGQ6FIDuB+Efk7lMIZThKK6PV6Uke32+WhUCQHcL+c/ALxuVOChCKwEFiL32figCm1cxwkjAYs30SwfBPB8k2E/uKo+VglIYD7CUgrItZjlYQA7icgrYhYj1WMuLoiADswEezARLADE8EOTAQ7MBHswESwAxPBDkwEOzAR7MBEsAMTwQ5MBDswEezARLADE8EOTAQ7MBHsoNGo818EGHGlRej7W/xjWxeY31kiCYADCUguwvEnyQAYmoDkIkAsF55/xQxjFyJAsIter5dlGVNSsiMRh4+JICZiyWLxF4pLkguoYtLQAAAAAElFTkSuQmCC',
        price: 999.91,
    },
]

function ImageB64(b64: string, props: any) {
    return (
        <img {...props} src={`data:image/png;base64,${b64}`} />
    );
}


export default function Home() {
    return (
        <div className="bg-white">
            <div className="mx-auto max-w-2xl px-4 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8">
                <h2 className="text-2xl font-bold tracking-tight text-gray-900">Browse our articles</h2>

                <div className="mt-6 grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 xl:gap-x-8">
                    {products.map((product) => (
                        <div key={product.id} className="group relative">
                            <div className="aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-md bg-gray-200 lg:aspect-none group-hover:opacity-75 lg:h-80">
                                <img
                                    src={`data:image/png;base64,${product.imageB64}`}
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

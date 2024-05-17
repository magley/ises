import { Form } from "@remix-run/react";
import { useState } from "react";
import toast from "react-hot-toast";
import { ArticleService, NewArticleDTO } from "~/service/article";

export default function NewArticle() {
    const [name, setName] = useState<string>("");
    const [price, setPrice] = useState<string>("");
    const [errMsg, setErrMsg] = useState<string | null>("");

    const validateForm = (): FormValidation => {
        let res: FormValidation = {
            invalid: false,
            invalid_field: "",
            invalid_message: "",
        };

        let required = [name, price];
        for (let field of required) {
            if (field.trim().length == 0) {
                res.invalid = true;
                res.invalid_message = "Field must not be empty.";
                return res;
            }
        }

        const priceNumber = Number(price);
        if (isNaN(+priceNumber)) {
            res.invalid = true;
            res.invalid_message = "Price must be a number.";
        }

        if (priceNumber <= 0) {
            res.invalid = true;
            res.invalid_message = "Price must be a number greater than 0.";
        }

        return res;
    };

    const doSubmit = () => {
        const formValidation = validateForm();
        if (formValidation.invalid) {
            setErrMsg(formValidation.invalid_message);
            return;
        } else {
            setErrMsg(null);
        }
        const priceNumber = Number(price);

        const dto: NewArticleDTO = {
            name: name,
            price: priceNumber,
        }

        ArticleService.create(dto).then((res) => {
            setErrMsg(null);
            toast.success("Article is set up for sale!");
        }).catch((err) => {
            console.error(err);
            setErrMsg("Could not create article.");
        });
    };

    return (
        <>
            <div className="bg-white border-4 rounded-lg shadow relative m-10">

                <div className="flex items-start justify-between p-5 border-b rounded-t">
                    <h3 className="text-xl font-semibold">
                        Create a new article
                    </h3>
                </div>

                <div className="p-6 space-y-6">
                    <Form onSubmit={e => { e.preventDefault(); doSubmit(); }}>
                        <div className="grid grid-cols-6 gap-6">
                            <div className="col-span-6 sm:col-span-3">
                                <label className="text-sm font-medium text-gray-900 block mb-2">Name</label>
                                <input
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    type="text"
                                    name="ProductName"
                                    id="brand"
                                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
                                    placeholder="T-Shirt"
                                />
                            </div>
                            <div className="col-span-6 sm:col-span-3">
                                <label className="text-sm font-medium text-gray-900 block mb-2">Price</label>
                                <input
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    type="number"
                                    name="price"
                                    id="price"
                                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
                                    placeholder="$10"
                                />
                            </div>
                            <div className="col-span-full">
                                <label className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Upload photo (Not implemented)</label>
                                <div className="flex items-center justify-center w-full">
                                    <label className="flex flex-col items-center justify-center w-full h-64 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600">
                                        <div className="flex flex-col items-center justify-center pt-5 pb-6">
                                            <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2" />
                                            </svg>
                                            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400"><span className="font-semibold">Click to upload</span> or drag and drop</p>
                                            <p className="text-xs text-gray-500 dark:text-gray-400">SVG, PNG, JPG or GIF (MAX. 800x400px)</p>
                                        </div>
                                        <input id="dropzone-file" type="file" className="hidden" />
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div className="p-6 border-t border-gray-200 rounded-b">
                            <div>
                                <button
                                    type="submit"
                                    className="flex justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                                >
                                    Finish
                                </button>
                                <div className="mt-5 font-semibold text-red-600">
                                    {" "}
                                    {errMsg}{" "}
                                </div>
                            </div>
                        </div>
                    </Form>
                </div>
            </div>
        </>
    );
}
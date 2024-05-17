import { Form, useParams } from "@remix-run/react";
import { useEffect } from "react";
import { useState } from "react";
import { ArticleCommentDTO, ArticleDetailsDTO, ArticleService } from "~/service/article";
import toast from "react-hot-toast";

export default function ArticleDetails() {
    const [product, setProduct] = useState<ArticleDetailsDTO>();
    const [comments, setComments] = useState<ArticleCommentDTO[]>([]);
    const params = useParams();

    // Comment section state
    const [comment, setComment] = useState<string>("");
    const [errMsg, setErrMsg] = useState<string | null>("");

    const loadProduct = (id: number) => {
        ArticleService.findById(id).then((res) => {
            setProduct(res.data);
            setComments(res.data.comments);
        });
    }

    const buyArticle = () => {
        if (product == undefined) {
            return;
        }

        console.log(`Buy ${product.id}`);
        toast.success("Item bought.");
    }

    const submitComment = () => {
        if (comment.trim().length == 0) {
            setErrMsg("Comment cannot be empty.");
            return;
        }
        setErrMsg("");

        console.log('Submit comment', comment);
        toast.success("Comment submitted.");
    }

    useEffect(() => {
        loadProduct(params['id'] as unknown as number);
    }, []);

    return (
        <div className="grid gap-4 grid-cols-2">
            {/* Left column */}
            <div className="w-full p-10 bg-gradient-to-r from-slate-50 to-slate-100">
                {/* Product details */}
                <>
                    {
                        product != undefined &&
                        <div className="">
                            ID: {product.id} <br />
                            Name: {product.name} <br />
                            Price: ${product.price} <br />
                            <img
                                src={`data:image/png;base64,${''}`}
                                className="h-full w-full object-cover object-center lg:h-full lg:w-full"
                            />
                        </div>
                    }
                </>
            </div>
            {/* Right column */}
            <div className="w-full p-10 bg-gradient-to-r from-slate-100 to-slate-200">
                {/* Purchase button */}
                <>
                    <button
                        className="flex justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                        onClick={buyArticle}
                    >
                        Purchase
                    </button>
                </>
                {/* Comment section */}
                <div className="flex min-h-full flex-1 flex-col justify-center px-6 py-12">
                    <h2>Comments</h2> <hr />

                    <ul role="list" className="divide-y divide-gray-100">
                        {comments.map((comment) => (
                            <li key={comment.userEmail} className="flex justify-between gap-x-6 py-5">
                                <div className="flex min-w-0 gap-x-4">
                                    <div className="min-w-0 flex-auto">
                                        <p className="text-sm font-semibold leading-6 text-gray-900">{comment.userEmail}</p>
                                        <p className="mt-1 truncate text-xs leading-5 text-gray-500">{comment.comment}</p>
                                    </div>
                                </div>
                                <div className="hidden shrink-0 sm:flex sm:flex-col sm:items-end">
                                    <p className="mt-1 text-xs leading-5 text-gray-500">
                                        {comment.timestamp.toLocaleString()}
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ul>

                    <div>
                        <Form className="space-y-6 lg:px-40">
                            <div>
                                <label
                                    htmlFor="comment"
                                    className="block text-sm font-medium leading-6 text-gray-900"
                                >
                                    Add your comment
                                </label>
                                <div className="mt-2">
                                    <textarea
                                        id="comment"
                                        name="comment"
                                        rows={5}
                                        autoComplete="comment"
                                        value={comment}
                                        onChange={(e) => setComment(e.target.value)}
                                        className="block w-full rounded-md border-0 p-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                    />
                                </div>
                            </div>
                            <div>
                                <button
                                    onClick={submitComment}
                                    className="flex justify-center rounded-md bg-indigo-600 px-5 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                                >
                                    Post
                                </button>
                                <div className="mt-5 font-semibold text-red-600">
                                    {" "}
                                    {errMsg}{" "}
                                </div>
                            </div>
                        </Form>

                    </div>
                </div>
            </div>
        </div>
    );
}
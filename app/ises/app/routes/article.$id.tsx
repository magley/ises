import { Form, useParams } from "@remix-run/react";
import { useEffect } from "react";
import { useState } from "react";
import {
	ArticleCommentDTO,
	ArticleDetailsDTO,
	ArticleService,
	NewArticleCommentDTO,
	NewArticlePurchaseDTO,
} from "~/service/article";
import toast from "react-hot-toast";
import { UserService } from "~/service/user";

export default function ArticleDetails() {
	const [product, setProduct] = useState<ArticleDetailsDTO>();
	const [comments, setComments] = useState<ArticleCommentDTO[]>([]);
	const params = useParams();

	// Comment section state
	const [comment, setComment] = useState<string>("");
	const [errMsg, setErrMsg] = useState<string | null>("");
	const [canLeaveComment, setCanLeaveComment] = useState<boolean>(false);

	// Purchase state
	const [canPurchase, setCanPurchase] = useState<boolean>(false);

	const loadProduct = (id: number) => {
		ArticleService.findById(id).then((res) => {
			setProduct(res.data);
			res.data.comments.sort((c1, c2) => {
				const d1 = new Date(c1.timestamp as unknown as string);
				const d2 = new Date(c2.timestamp as unknown as string);
				return d2.getTime() - d1.getTime();
			});
			setComments(res.data.comments);
		});
	};

	const checkPermissions = () => {
		// TODO: This is buggy? When signed in as admin, sometimes one of the
		// buttons becomes enabled even though both of these `canDoX` booleans
		// are false. Investigate.

		UserService.hasPermission("comment_on_articles")
			.then((res) => {
				setCanLeaveComment(res.data);
			})
			.catch((err) => {
				console.error(err);
			});

		UserService.hasPermission("buy_articles")
			.then((res) => {
				setCanPurchase(res.data);
			})
			.catch((err) => {
				console.error(err);
			});

		console.log(canLeaveComment, canPurchase);
	};

	const buyArticle = () => {
		if (!product) {
			return;
		}

		const dto: NewArticlePurchaseDTO = {
			articleId: product.id,
		};

		ArticleService.purchase(dto)
			.then((res) => {
				toast.success("Article purchased.");
			})
			.catch((err) => {
				console.error(err);
			});
	};

	const submitComment = () => {
		if (comment.trim().length == 0) {
			setErrMsg("Comment cannot be empty.");
			return;
		}
		if (!product) {
			return;
		}
		setErrMsg("");

		const dto: NewArticleCommentDTO = {
			comment: comment,
			articleId: product.id,
		};

		ArticleService.leaveComment(dto)
			.then((res) => {
				toast.success("Comment submitted.");
				loadProduct(product.id);
				setComment("");
			})
			.catch((err) => {
				console.error(err);
			});
	};

	useEffect(() => {
		loadProduct(params["id"] as unknown as number);
		checkPermissions();
	}, []);

	return (
		<div className="grid gap-4 grid-cols-2 m-10 bg-white dark:bg-slate-800 shadow-md p-10">
			{/* Left column */}
			<div className="w-full p-16 dark:bg-gradient-to-t dark:from-slate-800 dark:to-slate-700 bg-gradient-to-r from-slate-50 to-slate-100 shadow-lg rounded-lg">
				{/* Product details */}
				<>
					{product != undefined && (
						<div className="">
							ID: {product.id} <br />
							Name: {product.name} <br />
							Price: ${product.price} <br />
							<img
								src={`data:image/png;base64,${product.imgBase64}`}
								className="h-full w-full object-cover object-center lg:h-full lg:w-full shadow-md"
							/>
						</div>
					)}
				</>
			</div>
			{/* Right column */}
			<div className="w-full p-16 dark:bg-gradient-to-t dark:from-slate-800 dark:to-gray-700 bg-gradient-to-r  from-slate-100 to-slate-200 shadow-lg rounded-lg">
				{/* Purchase button */}
				<>
					<button
						onClick={buyArticle}
						disabled={!canPurchase}
						className="
                            dark:text-slate-900 dark:disabled:bg-slate-500 dark:disabled:text-slate-700
                            flex justify-center rounded-md bg-indigo-600 px-5 py-1.5 text-sm font-semibold leading-6 
                            text-white shadow-md hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 
                            focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:bg-slate-300 
                            disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none"
					>
						Purchase
					</button>
				</>
				{/* Comment section */}
				<div className="flex min-h-full flex-1 flex-col justify-center px-6 py-12 shadow-md dark:bg-gradient-to-t dark:from-slate-700 dark:to-slate-600 bg-gradient-to-r from-slate-100 to-slate-200">
					<h2>Comments</h2> <hr />
					<ul role="list" className="divide-y divide-gray-100">
						{comments.map((comment) => (
							<li
								key={comment.id}
								className="flex justify-between gap-x-6 py-5 m-2 shadow-md p-2 bg-gradient-to-r dark:bg-gradient-to-r dark:from-slate-700 dark:to-slate-700 from-slate-50 to-slate-100"
							>
								<div className="flex min-w-0 gap-x-4">
									<div className="min-w-0 flex-auto">
										<p className="text-sm font-semibold leading-6 text-gray-900 dark:text-gray-400">
											{comment.userEmail}
										</p>
										<p className="mt-1 truncate text-xs leading-5 text-gray-500 dark:text-gray-300">
											{comment.comment}
										</p>
									</div>
								</div>
								<div className="hidden shrink-0 sm:flex sm:flex-col sm:items-end">
									<p className="mt-1 text-xs leading-5 text-gray-500 dark:text-gray-400">
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
									className="block text-sm font-medium leading-6 text-gray-900 dark:text-gray-400"
								>
									Add your comment
								</label>
								<div className="mt-2">
									<textarea
										disabled={!canLeaveComment}
										id="comment"
										name="comment"
										rows={5}
										autoComplete="comment"
										value={comment}
										onChange={(e) =>
											setComment(e.target.value)
										}
										className="block w-full rounded-md border-0 p-1.5 dark:disabled:bg-slate-700 text-gray-900 dark:text-gray-100 shadow-md ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
									/>
								</div>
							</div>
							<div>
								<button
									onClick={(e) => {
										e.preventDefault();
										submitComment();
									}}
									disabled={!canLeaveComment}
									className="
                                        dark:text-slate-900 dark:disabled:bg-slate-500 dark:disabled:text-slate-700
                                        flex justify-center rounded-md bg-indigo-600 px-5 py-1.5 text-sm font-semibold leading-6 
                                        text-white shadow-md hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 
                                        focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:bg-slate-300 
                                        disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none"
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

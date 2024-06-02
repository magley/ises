import { Form } from "@remix-run/react";
import { useState } from "react";
import toast from "react-hot-toast";
import { ArticleService, NewArticleDTO } from "~/service/article";

export default function NewArticle() {
	const [name, setName] = useState<string>("");
	const [price, setPrice] = useState<string>("");
	const [errMsg, setErrMsg] = useState<string | null>("");

	const [selectedImage, setSelectedImage] = useState<File | null>(null);

	const convertBase64 = (file: File): Promise<string> => {
		return new Promise((resolve, reject) => {
			const fileReader = new FileReader();
			fileReader.readAsDataURL(file);
			fileReader.onload = () => {
				resolve(fileReader.result as string);
			};
			fileReader.onerror = (error) => {
				reject(error);
			};
		});
	};

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

		if (selectedImage == null) {
			res.invalid = true;
			res.invalid_message = "Please select an image of your product.";
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

		let dto: NewArticleDTO = {
			name: name,
			price: priceNumber,
			imgBase64: "",
		};

		if (selectedImage != null) {
			// ^^^ neccessary to shut typescript up

			convertBase64(selectedImage)
				.then((res) => {
					dto.imgBase64 = res;

					ArticleService.create(dto)
						.then((res) => {
							setErrMsg(null);
							toast.success("Article is set up for sale!");
						})
						.catch((err) => {
							console.error(err);
							setErrMsg("Could not create article.");
						});
				})
				.catch((err) => {
					console.error(err);
				});
		}
	};

	return (
		<>
			<div className="bg-white dark:bg-slate-800 rounded-lg shadow relative m-10">
				<div className="flex items-start justify-between p-5 border-b rounded-t">
					<h3 className="text-xl font-semibold">
						Create a new article
					</h3>
				</div>

				<div className="p-6 space-y-6">
					<Form
						onSubmit={(e) => {
							e.preventDefault();
							doSubmit();
						}}
					>
						<div className="grid grid-cols-6 gap-6">
							<div className="col-span-6 sm:col-span-3">
								<label className="text-sm font-medium dark:text-gray-200 text-gray-900 block mb-2">
									Name
								</label>
								<input
									value={name}
									onChange={(e) => setName(e.target.value)}
									type="text"
									name="ProductName"
									id="brand"
									className="shadow-sm bg-gray-50 border dark:text-gray-200 dark:bg-slate-900 border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
									placeholder="T-Shirt"
								/>
							</div>
							<div className="col-span-6 sm:col-span-3">
								<label className="text-sm font-medium dark:text-gray-200 text-gray-900 block mb-2">
									Price
								</label>
								<input
									value={price}
									onChange={(e) => setPrice(e.target.value)}
									type="number"
									name="price"
									id="price"
									className="shadow-sm bg-gray-50 border border-gray-300 dark:bg-slate-900 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
									placeholder="$10"
								/>
							</div>
							<div className="col-span-full">
								<label className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">
									Upload photo
								</label>
								{selectedImage && (
									<div>
										<img
											alt="not found"
											width={"250px"}
											src={URL.createObjectURL(
												selectedImage
											)}
										/>
									</div>
								)}
								<br />
								<input
									type="file"
									name="myImage"
									onChange={(event) => {
										if (
											event.target &&
											event.target.files
										) {
											setSelectedImage(
												event.target.files[0]
											);
										}
									}}
								/>
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

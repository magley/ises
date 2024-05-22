import { AxiosResponse } from "axios";
import axiosInstance from "~/util/axiosInterceptor";
import { UserDTO } from "./user";

export interface NewArticleDTO {
    name: string,
    price: number,
    imgBase64: string,
}

export interface ArticleDTO {
    id: number,
    name: string,
    imgBase64: string,
    price: number,
}

export interface ArticleCommentDTO {
    id: number,
    userId: number,
    userEmail: string,
    timestamp: Date,
    comment: string
}

export interface NewArticleCommentDTO {
    comment: string,
    articleId: number,
}

export interface ArticleDetailsDTO {
    id: number,
    timestamp: Date,
    owner: UserDTO,
    imgBase64: string,
    comments: ArticleCommentDTO[],
    name: string,
    price: number
}

export interface NewArticlePurchaseDTO {
    articleId: number
}

export interface ArticlePurchaseDTO {
    id: number,
    user: UserDTO,
    article: ArticleDTO,
    timestamp: Date
}

export abstract class ArticleService {
    static getAllArticles(): Promise<AxiosResponse<ArticleDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/article/all`);
    }

    static findById(id: number): Promise<AxiosResponse<ArticleDetailsDTO>> {
        return axiosInstance.get(`http://localhost:8080/api/article/${id}`);
    }

    static leaveComment(dto: NewArticleCommentDTO): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/article/${dto.articleId}/comment`, dto);
    }

    static purchase(dto: NewArticlePurchaseDTO): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/article/${dto.articleId}/purchase`, dto);
    }

    static create(dto: NewArticleDTO): Promise<AxiosResponse<void>> {
        return axiosInstance.post(`http://localhost:8080/api/article`, dto);
    }

    static getPurchases(): Promise<AxiosResponse<ArticlePurchaseDTO[]>> {
        return axiosInstance.get(`http://localhost:8080/api/article/purchases`);
    }
};
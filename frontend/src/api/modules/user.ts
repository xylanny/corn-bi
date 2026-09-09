import client from "@/api/client";
import type {
  ResultUserVO,
  UserUpdateDTO,
  UserRegisterDTO,
  UserLoginDTO,
} from "../types";

export const userAPI = {
  update: (data: UserUpdateDTO): Promise<ResultUserVO> => {
    return client.post(`/user/update`, data);
  },
  register: (data: UserRegisterDTO): Promise<ResultUserVO> => {
    return client.post(`/user/register`, data);
  },
  sendEmailCode: (query: { userEmail?: string } = {}): Promise<any> => {
    return client.post(`/user/mail?userEmail=${query.userEmail}`);
  },
  login: (data: UserLoginDTO): Promise<ResultUserVO> => {
    return client.post(`/user/login`, data);
  },
  getUserByAuthorization: (): Promise<ResultUserVO> => {
    return client.get(`/user/info`);
  },
};

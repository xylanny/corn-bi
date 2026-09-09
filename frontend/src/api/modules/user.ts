import client from '@/api/client';
import type { ResultUserVO, UserRegisterDTO, UserLoginDTO } from '../types';

export const userAPI = {
  register: (data: UserRegisterDTO): Promise<ResultUserVO> => {
    return client.post(`/user/register`, data);
  },
  sendEmailCode: (query: { userEmail?: string } = {}): Promise<void> => {
    return client.post(`/user/mail?userEmail=${query.userEmail}`);
  },
  login: (data: UserLoginDTO): Promise<ResultUserVO> => {
    return client.post(`/user/login`, data);
  },
  getUserByAuthorization: (): Promise<ResultUserVO> => {
    return client.post(`/user/info`);
  },
};
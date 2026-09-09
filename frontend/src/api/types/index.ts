
export interface Result<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface UserRegisterDTO {
  userName: string;
  userEmail: string;
  emailCode: string;
  userPassword: string;
  checkedPassword: string;
}

export interface ResultUserVO {
  code: number;
  message: string;
  data: UserVO;
}

export interface UserVO {
  id: number;
  userName: string;
  userAvatar: string;
  userEmail: string;
  userRole: string;
  createTime: string;
  updateTime: string;
  token: string;
}

export interface UserLoginDTO {
  userEmail: string;
  userPassword: string;
}

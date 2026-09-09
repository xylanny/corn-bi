import { userAPI } from "@/api/modules/user";
import type { UserLoginDTO, UserRegisterDTO, UserVO } from "@/api/types";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const user = ref<UserVO | null>(null);

  /**
   * 登录用户
   *
   * @param {UserLoginDTO} params - 包括用户邮箱、用户密码
   * @returns {Promise<void>}
   */
  async function login(params: UserLoginDTO): Promise<void> {
    const responseBody = await userAPI.login(params);

    // 登录验证成功，存储去敏用户信息
    user.value = responseBody.data;
    // 持久化token
    localStorage.setItem("token", user.value?.token as string);
  }

  /**
   * 注册用户
   *
   * @param {UserRegisterDTO} params - 包括用户名、用户邮箱、用户秘密
   * @returns {Promise<void>}
   */
  async function register(params: UserRegisterDTO): Promise<void> {
    const responseBody = await userAPI.register(params);

    // 注册验证成功，存储去敏用户信息
    user.value = responseBody.data;
    // 持久化token
    localStorage.setItem("token", user.value?.token as string);
  }

  function logout() {
    user.value = null;

    localStorage.removeItem("token");
  }

  // /**
  //  * 更新用户信息
  //  * @param {UserUpdateReq} params - 可以是用户信息的任何部分
  //  * @returns {Promise<void>}
  //  */
  // async function update(params: UserUpdateReq) {
  //   await userAPI.updateUser(params);
  // }

  /**
   * 获取用户信息，前提是已经认证成功了
   *
   * @returns {Promise<void>}
   */
  async function get(): Promise<void> {
    const responseBody = await userAPI.getUserByAuthorization();

    if (responseBody.code === 20000) {
      user.value = responseBody.data;
    }

    throw new Error(responseBody.message ?? "restore session failure");
  }

  /**
   * 实现下次登录用户信息恢复，前提是token存在、有效
   *
   * @returns {Promise<boolean>} -
   */
  async function resume(): Promise<boolean> {
    // 若认证成功而存在用户信息，不需要恢复
    if (user.value) return true;

    if (!localStorage.getItem("token")) return false;

    try {
      await get();
      return user.value !== null;
    } catch (error) {
      localStorage.removeItem("token");
      user.value = null;
      return false;
    }
  }

  /**
   * 获取邮箱验证码
   *
   * @param {string} param - 邮箱地址字符串
   */
  function getEmailCode(param: string): void {
    userAPI.sendEmailCode({ userEmail: param });
  }

  return {
    user,
    login,
    logout,
    register,
    resume,
    getEmailCode,
  };
});

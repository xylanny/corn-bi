package com.xylanny.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xylanny.backend.exception.BusinessException;
import com.xylanny.backend.model.dto.UserVO;
import com.xylanny.backend.model.entity.User;
import com.xylanny.backend.model.enums.BusinessCode;
import com.xylanny.backend.service.UserService;
import com.xylanny.backend.service.EmailService;
import com.xylanny.backend.mapper.UserMapper;
import com.xylanny.backend.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Jun
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-09-08 10:56:19
*/
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TokenUtils tokenUtils;

    @Resource
    private EmailService emailService;

    @Override
    public UserVO register(String userName, String userEmail, String userPassword, String checkPassword) {
        return registerInternal(userName, userEmail, userPassword, checkPassword);
    }

    @Override
    public UserVO register(String userName, String userEmail, String emailCode,
                           String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userName, userEmail, emailCode, userPassword, checkPassword)){
            throw new BusinessException(BusinessCode.PARAMS_MISSING);
        }
        emailService.verifyRegisterCode(userEmail, emailCode);
        return registerInternal(userName, userEmail, userPassword, checkPassword);
    }

    @Override
    public void sendRegisterEmailCode(String userEmail) {
        emailService.sendRegisterCode(userEmail);
    }

    private UserVO registerInternal(String userName, String userEmail,
                                    String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userName, userEmail, userPassword, checkPassword)){
            throw new BusinessException(BusinessCode.PARAMS_MISSING);
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(BusinessCode.PASSWORD_DISPARITY);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserEmail, userEmail);
        User existedUser = userMapper.selectOne(queryWrapper);

        if(existedUser != null){
            throw new BusinessException(BusinessCode.USER_EXISTS);
        }

        User user = new User();
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        user.setUserPassword(userPassword);
        user.setUserRole("user");
        user.setIsDelete(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 存储到数据库中
        int effectiveRow = userMapper.insert(user);
        if(effectiveRow != 1){
            throw new BusinessException(BusinessCode.DATABASE_ERROR, "用户注册失败");
        }

        // 去敏
        UserVO userVO = this.getUserVO(user);
        // 加入token
        userVO.setToken(tokenUtils.createToken(user.getId()));

        return userVO;
    }

    @Override
    public UserVO login(String userEmail, String userPassword) {

        if(StringUtils.isAnyBlank(userEmail, userPassword)){
            throw  new BusinessException(BusinessCode.PARAMS_MISSING);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserEmail, userEmail)
                .eq(User::getIsDelete, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(BusinessCode.USER_NOT_EXISTS);
        }

        if (!user.getUserPassword().equals(userPassword)) {
            throw new BusinessException(BusinessCode.PASSWORD_ERROR);
        }

        // 去敏
        UserVO userVO = this.getUserVO(user);
        // 加入token
        userVO.setToken(tokenUtils.createToken(user.getId()));

        return userVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param authorization 请求头中的Authorization字符串
     * @return
     */
    @Override
    public UserVO getUserByAuthorization(String authorization) {
        long userId = tokenUtils.getUserId(authorization);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        User currentUser = userMapper.selectOne(queryWrapper);

        if (currentUser == null) {
            throw new BusinessException(BusinessCode.NOT_LOGIN);
        }

        UserVO userVO = this.getUserVO(currentUser);

        return userVO;
    }

    @Override
    public UserVO update(User user){
        if(user == null || user.getId() == null){
            throw new BusinessException(BusinessCode.PARAMS_MISSING, "用户ID不能为空");
        }

        User existedUser = userMapper.selectById(user.getId());
        if(existedUser == null){
            throw new BusinessException(BusinessCode.USER_NOT_EXISTS);
        }

        if(StringUtils.isNotBlank(user.getUserName())){
            existedUser.setUserName(user.getUserName());
        }
        if(StringUtils.isNotBlank(user.getUserEmail())){
            existedUser.setUserEmail(user.getUserEmail());
        }
        if(StringUtils.isNotBlank(user.getUserPassword())){
            existedUser.setUserPassword(user.getUserPassword());
        }
        if(StringUtils.isNotBlank(user.getUserAvatar())){
            existedUser.setUserAvatar(user.getUserAvatar());
        }
        existedUser.setUpdateTime(new Date());

        // 更新到数据库中
        int effectiveRow = userMapper.updateById(existedUser);
        if(effectiveRow != 1){
            throw new BusinessException(BusinessCode.DATABASE_ERROR, "用户更新信息失败");
        }

        return this.getUserVO(existedUser);
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

}

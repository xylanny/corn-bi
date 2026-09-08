package com.xylanny.backend.controller;

import com.xylanny.backend.model.dto.Result;
import com.xylanny.backend.model.dto.UserLoginDTO;
import com.xylanny.backend.model.dto.UserRegisterDTO;
import com.xylanny.backend.model.dto.UserVO;
import com.xylanny.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(UserRegisterDTO userRegisterDTO){

        UserVO userVO = userService.register(
                userRegisterDTO.getUserName(),
                userRegisterDTO.getUserEmail(),
                userRegisterDTO.getUserPassword(),
                userRegisterDTO.getCheckedPassword()
        );

        return Result.success(userVO);
    }

    @PostMapping("/login")
    public Result<UserVO> login(UserLoginDTO userLoginDTO){
        UserVO userVO = userService.login(
                userLoginDTO.getUserEmail(),
                userLoginDTO.getUserPassword()
        );

        return Result.success(userVO);
    }
    
}

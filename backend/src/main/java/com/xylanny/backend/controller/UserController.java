package com.xylanny.backend.controller;

import com.xylanny.backend.model.dto.*;
import com.xylanny.backend.model.entity.User;
import com.xylanny.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO userRegisterDTO){

        UserVO userVO = userService.register(
                userRegisterDTO.getUserName(),
                userRegisterDTO.getUserEmail(),
                userRegisterDTO.getEmailCode(),
                userRegisterDTO.getUserPassword(),
                userRegisterDTO.getCheckedPassword()
        );

        return Result.success(userVO);
    }

    @PostMapping("/mail")
    public Result<Void> sendEmailCode(@RequestParam String userEmail) {
        userService.sendRegisterEmailCode(userEmail);
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO){
        UserVO userVO = userService.login(
                userLoginDTO.getUserEmail(),
                userLoginDTO.getUserPassword()
        );

        return Result.success(userVO);
    }

    @PostMapping("/info")
    @SecurityRequirement(name = "bearerAuth")
    public Result<UserVO> getUserByAuthorization(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");

        UserVO userVO = userService.getUserByAuthorization(authorization);

        return Result.success(userVO);
    }

//    @PostMapping("/update")
//    public Result<UserVO> update(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request){
//         String authorization = request.getHeader("Authorization");
//
//         UserVO userVO = userService.getUserByAuthorization(authorization);
//    }
}

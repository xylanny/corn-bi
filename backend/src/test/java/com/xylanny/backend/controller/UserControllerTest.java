package com.xylanny.backend.controller;

import com.xylanny.backend.model.dto.Result;
import com.xylanny.backend.model.dto.UserRegisterDTO;
import com.xylanny.backend.model.dto.UserVO;
import com.xylanny.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void registerShouldReturnSuccessResult() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setUserName("Jun");
        request.setUserEmail("jun@example.com");
        request.setUserPassword("password");
        request.setCheckedPassword("password");

        UserVO userVO = new UserVO();
        userVO.setUserName("Jun");
        userVO.setUserEmail("jun@example.com");
        when(userService.register("Jun", "jun@example.com", "password", "password"))
                .thenReturn(userVO);

        Result<UserVO> result = userController.register(request);

        assertEquals(20000, result.getCode());
        assertEquals(userVO, result.getData());
        verify(userService).register("Jun", "jun@example.com", "password", "password");
    }
}

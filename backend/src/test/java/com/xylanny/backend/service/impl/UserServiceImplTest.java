package com.xylanny.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xylanny.backend.mapper.UserMapper;
import com.xylanny.backend.model.dto.UserVO;
import com.xylanny.backend.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 启用 Mockito框架，让@Mock和@InjectMocks注解生效
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // 创建一个虚拟的UserMapper对象（不连接真实数据库）
    @Mock
    private UserMapper userMapper;

    // 创建一个UserServiceImpl实例，并把@Mock标记的对象注入进去
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // UserServiceImpl实例中直接注入UserMapper实例，不连接真实数据库
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
    }

    @Test
    void registerShouldSaveNewUser() {
        // 当userMapper执行selectOne查询时，返回null（表示用户不存在）
        when(userMapper.selectOne(any(Wrapper.class))).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 调用register方法
        UserVO user = userService.register("Jun", "jun@example.com", "password", "password");

        // 断言
        assertEquals("Jun", user.getUserName());
        assertEquals("jun@example.com", user.getUserEmail());

        // 验证userMapper的insert方法被调用了1次
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void loginShouldReturnUserWithoutPassword() {
        // 创建一个模拟的“已存储用户”
        User storedUser = new User();
        storedUser.setId(1L);
        storedUser.setUserEmail("jun@example.com");
        storedUser.setUserPassword("password");

        // 当userMapper查询时，返回这个模拟用户
        when(userMapper.selectOne(any(Wrapper.class))).thenReturn(storedUser);

        // 调用login方法
        UserVO user = userService.login("jun@example.com", "password");

        assertEquals("jun@example.com", user.getUserEmail());

        // 验证selectOne方法被调用了1次
        verify(userMapper).selectOne(any(Wrapper.class));
    }
}
